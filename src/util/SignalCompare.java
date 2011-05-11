/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package util;

import java.io.*;
import java.util.*;

import net.java.sip.communicator.util.*;
import net.java.sip.communicator.impl.neomedia.codec.audio.*;


/**
 * Compare two audio signals and compute weighted SNR difference.
 * 
 * @author Dingxin Xu
 */
public class SignalCompare
{
    static final int FRAME_LENGTH_MS = 10;
    static final int WIN_LENGTH_MS = 20;
    static final float BW_EXPANSION = 0.7f;

    static final int MAX_FS_KHZ = 24;
    static final int LPC_ORDER = 10;
    
    /**
     * The <tt>Logger</tt> used by the <tt>Decoder</tt> class and its
     * instances for logging output.
     */
    private static final Logger logger = Logger.getLogger(SignalCompare.class);
    
    /**
     * print the usage information.
     * @param argv the command line arguments.
     */
    static void print_usage(String[] argv) {
        System.out.printf("\nusage: ref.pcm test.pcm [settings]\n");
        System.out.printf("\nref.pcm       : Reference file");
        System.out.printf("\ntest.pcm      : File to be tested, should be of same length as ref.pcm");
        System.out.printf("\n   settings:");
        System.out.printf("\n-diff         : Only determine bit-exactness");
        System.out.printf("\n-fs <Hz>      : Sampling rate in Hz, max: %d; default: 24000", MAX_FS_KHZ * 1000 );
        System.out.printf("\n");
    }

    /**
     * starts the singal compare program.
     * @param argv command line arguments if any.
     */
   public static void main(String[] argv)
    {
        int   args, n, i, counterRef = 0, counterTest = 0;
        String testInFileName, refInFileName;
        
        DataInputStream refInFile=null, testInFile=null;

        int   nFrames = 0, isUnequal = 0;
        int   diff = 0, Fs_kHz;
        int Fs_Hz = 24000;
        float c, refWhtnd, testWhtnd, refNrg, diffNrg;
        double    SNR = 0.0;

        short[] refIn = new short[WIN_LENGTH_MS * MAX_FS_KHZ];
        short[] testIn = new short[WIN_LENGTH_MS * MAX_FS_KHZ];
        byte[] refInTmp = new byte[2*refIn.length];
        byte[] testInTmp = new byte[2*testIn.length];
        
        float[] refWin = new float[WIN_LENGTH_MS * MAX_FS_KHZ];
        float[] testWin = new float[WIN_LENGTH_MS * MAX_FS_KHZ];
        float[] autoCorr = new float[LPC_ORDER + 1], LPC_Coef = new float[LPC_ORDER];

        if (argv.length < 2) {
            print_usage(argv);
            System.exit(0);
        } 

        /* get arguments */
        args = 0;
        refInFileName = argv[args];
        args++;
        testInFileName = argv[args];
        args++;
        
        while(args<argv.length) {
            if(argv[args].compareToIgnoreCase("-diff") == 0) {
                diff = 1;
                args++;
            } else if (argv[args].compareToIgnoreCase("-fs") == 0) {
                Fs_Hz = Integer.valueOf(argv[args+1]).intValue();
                args += 2;
            } else {
                System.out.printf("Error: unrecognized setting: %s\n\n", argv[args]);
                print_usage(argv);
                System.exit(0);
            }
        }

        Fs_kHz = ( Fs_Hz / 1000 );

        if( Fs_kHz > MAX_FS_KHZ ) {
            System.out.printf("Error: sampling rate too high: %d\n\n", Fs_kHz);
            print_usage(argv);
            System.exit(0);
        }

        System.out.printf("Reference:  %s\n", refInFileName);
        
        /* open files */
        try {
            refInFile = new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(refInFileName)));
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        } 
               
        if (refInFile==null) {
            System.out.printf("Error: could not open input file %s\n", refInFileName);
            System.exit(0);
        } 
        try {
            testInFile = new DataInputStream(
                                new BufferedInputStream(
                                        new FileInputStream(testInFileName)));
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        }
        if (testInFile==null) {
            System.out.printf("Error: could not open input file %s\n", testInFileName);
            System.exit(0);
        }

        Arrays.fill(refIn, (short)0);
        Arrays.fill(testIn, (short)0);

        while(true) {
            /* Read inputs */
            try
            {
                counterRef = refInFile.read(refInTmp, (WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz, FRAME_LENGTH_MS * Fs_kHz *2);
            }
            catch (IOException e)
            {
                logger.error("error when read from input file", e);
            }
            try
            {
                counterTest = testInFile.read(testInTmp, (WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz, FRAME_LENGTH_MS * Fs_kHz *2);
            }
            catch (IOException e)
            {
                logger.error("error when read from input file", e);
            }
            
            if(counterRef != FRAME_LENGTH_MS * Fs_kHz *2 || counterTest != FRAME_LENGTH_MS * Fs_kHz *2){
                break;
            }
            refIn = Utils.byteToShortArray(refInTmp, 0, refInTmp.length, true);
            testIn = Utils.byteToShortArray(testInTmp, 0, testInTmp.length, true);

            /* test for bit-exactness */
            for( n = 0; n < FRAME_LENGTH_MS * Fs_kHz; n++ ) {
                if( refIn[(WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz + n] != 
                    testIn[(WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz + n] ) {
                        isUnequal = 1;
                        break;
                }
            }

            /* apply sine window */
            for( n = 0; n < WIN_LENGTH_MS * Fs_kHz; n++ ) {
                c = (float) Math.sin( 3.14159265 * (n + 1) / (WIN_LENGTH_MS * Fs_kHz + 1) );
                refWin[n]  = refIn[n]  * c;
                testWin[n] = testIn[n] * c;
            }

            /* LPC analysis on reference signal */

            /* Calculate auto correlation */
            Autocorrelation(autoCorr, 0, refWin, 0, WIN_LENGTH_MS * Fs_kHz, LPC_ORDER + 1);

            /* Add white noise */
            autoCorr[ 0 ] += autoCorr[ 0 ] * 1e-6f + 1.0f; 

            /* Convert correlations to prediction coefficients */
            Levinsondurbin(LPC_Coef, 0, autoCorr, 0, LPC_ORDER);

            /* Bandwdith expansion */
            Bwexpander(LPC_Coef, 0, LPC_ORDER, BW_EXPANSION);


            /* Filter both signals */
            refNrg = 1.0f;
            diffNrg = 1e-10f;
            for( n = (WIN_LENGTH_MS - FRAME_LENGTH_MS) / 2 * Fs_kHz; 
                 n < (WIN_LENGTH_MS + FRAME_LENGTH_MS) / 2 * Fs_kHz; n++ ) {
                    refWhtnd = refIn[n];
                    testWhtnd = testIn[n];
                    for( i = 0; i < LPC_ORDER; i++ ) {
                        refWhtnd  -= LPC_Coef[ i ] * refIn[n - i - 1];
                        testWhtnd -= LPC_Coef[ i ] * testIn[n - i - 1];
                    }
                    refNrg += refWhtnd * refWhtnd;
                    diffNrg += (refWhtnd - testWhtnd) * (refWhtnd - testWhtnd);
            }

            /* weighted SNR */
            if( refNrg > FRAME_LENGTH_MS * Fs_kHz ) {
                SNR += 10.0 * Math.log10( refNrg / diffNrg );
                nFrames++;
            }

            /* Update Buffer */
            System.arraycopy(refIn, FRAME_LENGTH_MS * Fs_kHz, refIn, 0, (WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz);
            System.arraycopy(testIn, FRAME_LENGTH_MS * Fs_kHz, testIn, 0, (WIN_LENGTH_MS - FRAME_LENGTH_MS) * Fs_kHz);
        }

        if( diff!=0 ) {
            if( isUnequal!=0 ) {
                System.out.printf("Signals     DIFFER\n");
            } else {
                if(counterRef != counterTest){
                    System.out.printf("Warning: signals differ in length\n");
                }
                System.out.printf("Signals     BIT-EXACT\n");
            }
        } else {
            if( nFrames == 0 ) {
                System.out.printf("At least one signal too short or not loud enough\n");
                System.exit(0);
            }
            if(counterRef != counterTest){
                System.out.printf("Warning: signals differ in length\n");
            }
            if( isUnequal == 0 ) {
                System.out.printf("Signals     BIT-EXACT\n");
            } else {
                System.out.printf("AVERAGE WEIGHTED SNR: %4.1f dB\n", SNR / nFrames);
            }
        }
        System.out.printf("\n");

        /* Close Files */
        try
        {
            refInFile.close();
        }
        catch (IOException e)
        {
            logger.error("error when close the output file", e);
        }
        try
        {
            testInFile.close();
        }
        catch (IOException e)
        {
            logger.error("error when close the output file", e);
        }
        
        return;
    }
   
   /**
    * compute autocorrelation.
    * @param results result (length correlationCount).
    * @param results_offset offset of valid data.
    * @param inputData input data to correlate.
    * @param inputData_offset offset of valid data.
    * @param inputDataSize  length of input.
    * @param correlationCount number of correlation taps to compute.
    */
    static void Autocorrelation( 
        float[] results,                 /* o    result (length correlationCount)            */
        int results_offset,
        float[] inputData,         /* i    input data to correlate                     */
        int inputData_offset,
        int inputDataSize,              /* i    length of input                             */
        int correlationCount            /* i    number of correlation taps to compute       */
    )
    {
        int i;

        if (correlationCount > inputDataSize) {
            correlationCount = inputDataSize;
        }

        for( i = 0; i < correlationCount; i++ ) {
            results[ results_offset + i ] =  (float)Inner_product( inputData, inputData_offset, 
                inputData, inputData_offset + i, inputDataSize - i );
        }
    }

    /**
     * inner product of two SKP_float arrays, with result as double.
     * @param data1
     * @param data1_offset
     * @param data2
     * @param data2_offset
     * @param dataSize
     */
    static double Inner_product( 
        float[] data1, 
        int data1_offset,
        float[] data2, 
        int data2_offset,
        int dataSize
    )
    {
        int  i, dataSize4;
        double   result;

        /* 4x unrolled loop */
        result = 0.0f;
        dataSize4 = dataSize & 0xFFFC;
        for( i = 0; i < dataSize4; i += 4 ) {
            result += data1[ data1_offset + i + 0 ] * data2[ data2_offset + i + 0 ] + 
                      data1[ data1_offset + i + 1 ] * data2[ data2_offset + i + 1 ] +
                      data1[ data1_offset + i + 2 ] * data2[ data2_offset + i + 2 ] +
                      data1[ data1_offset + i + 3 ] * data2[ data2_offset + i + 3 ];
        }

        /* add any remaining products */
        for( ; i < dataSize; i++ ) {
            result += data1[ data1_offset + i ] * data2[ data2_offset + i ];
        }

        return result;
    }
    
    /**
     * Solve the normal equations using the Levinson-Durbin recursion.
     * @param A prediction coefficients [order].
     * @param A_offset offset of valid data.
     * @param corr input auto-correlations [order + 1].
     * @param corr_offset offset of valid data.
     * @param order prediction order.
     */
    static float Levinsondurbin(               /* O    prediction error energy                     */
        float       A[],                /* O    prediction coefficients [order]             */
        int A_offset,
        final float corr[],             /* I    input auto-correlations [order + 1]         */
        int corr_offset,
        final int   order               /* I    prediction order                            */
    )
    {
        int   i, mHalf, m;
        float min_nrg, nrg, t, km, Atmp1, Atmp2;
        
        min_nrg = 1e-12f * corr[ corr_offset+0 ] + 1e-9f;
        nrg = corr[ corr_offset+0 ];
        nrg = Math.max(min_nrg, nrg);
        A[ A_offset+0 ] = corr[ corr_offset+1 ] / nrg;
        nrg -= A[ A_offset+0 ] * corr[ corr_offset+1 ];
        nrg = Math.max(min_nrg, nrg);

        for( m = 1; m < order; m++ )
        {
            t = corr[ corr_offset+m + 1 ];
            for( i = 0; i < m; i++ ) {
                t -= A[ A_offset+i ] * corr[ corr_offset+m - i ];
            }

            /* reflection coefficient */
            km = t / nrg;

            /* residual energy */
            nrg -= km * t;
            nrg = Math.max(min_nrg, nrg);

            mHalf = m >> 1;
            for( i = 0; i < mHalf; i++ ) {
                Atmp1 = A[ A_offset+i ];
                Atmp2 = A[ A_offset+m - i - 1 ];
                A[ A_offset+m - i - 1 ] -= km * Atmp1;
                A[ A_offset+i ]         -= km * Atmp2;
            }
            if( (m & 1)!=0 ) {
                A[ A_offset+mHalf ]     -= km * A[ A_offset+mHalf ];
            }
            A[ A_offset+m ] = km;
        }

        /* return the residual energy */
        return nrg;
    }
    
    /**
     * Chirp (bw expand) LP AR filter.
     * @param ar AR filter to be expanded (without leading 1).
     * @param ar_offset offset of the valid data.
     * @param d length of ar.
     * @param chirp chirp factor (typically in range (0..1) ).
     */
    static void Bwexpander( 
        float[] ar,                      /* io   AR filter to be expanded (without leading 1)    */
        int ar_offset,
        final int d,                    /* i    length of ar                                    */
        final float chirp               /* i    chirp factor (typically in range (0..1) )       */
    )
    {
        int   i;
        float cfac = chirp;

        for( i = 0; i < d - 1; i++ ) {
            ar[ ar_offset + i ] *=  cfac;
            cfac    *=  chirp;
        }
        ar[ ar_offset + d - 1 ] *=  cfac;
    }
}
