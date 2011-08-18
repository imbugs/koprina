/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;
import java.util.*;

import net.java.sip.communicator.util.*;

/**
 * Silk decoder test program
 * 
 * @author Jing Dai
 * @author Dingxin Xu
 */
public class Decoder 
{
    /**
     * The <tt>Logger</tt> used by the <tt>Decoder</tt> class and its
     * instances for logging output.
     */
    private static final Logger logger = Logger.getLogger(Decoder.class);
    
    /**
     * the maximum bytes per frame.
     */
    static final int MAX_BYTES_PER_FRAME =    1024;
    
    /**
     * the maximum input frames in each packet.
     */
    static final int MAX_INPUT_FRAMES =       5;
    
    /**
     * the maximum frame length in samples.
     */
    static final int MAX_FRAME_LENGTH =       480;
    
    /**
     * the internal frame length is 20ms.
     */
    static final int FRAME_LENGTH_MS =        20;
    
    /**
     * the maximum external sampling frequency supported.
     */
    static final int MAX_API_FS_KHZ =         48;
    
    /**
     * the maximum LBRR delay.
     */
    static final int MAX_LBRR_DELAY =         2;

    /**
     * convert a little endian int16 to a big endian int16
     * or vice versa.
     * 
     * @param vec the input short vector which to be converted.
     * @param len the length of the vector.
     */
    static void swap_endian(
        short       vec[],
        int         len
    )
    {
        int i;
        short tmp;

        for( i = 0; i < len; i++ ){
            tmp = vec[ i ];
            tmp = (short) (((tmp>>>8)&0xFF) + ((tmp<<8)&0xFF00));
            vec[i] = tmp;
        }
    }
    
    /**
     * print the usage information
     * 
     * @param argv the command line arguments.
     */
    static void print_usage(String[] argv) {
        System.out.printf( "\nusage:  in.bit out.pcm [settings]\n" );
        System.out.printf( "\nin.bit       : Bitstream input to decoder" );
        System.out.printf( "\nout.pcm      : Speech output from decoder" );
        System.out.printf( "\n   settings:" );
        System.out.printf( "\n-Fs_API <Hz> : Sampling rate of output signal in Hz; default: 24000" );
        System.out.printf( "\n-loss <perc> : Simulated packet loss percentage (0-100); default: 0" );
        System.out.printf( "\n" );
    }
    
    /**
     * starts the Decoder test program.
     * 
     * @param argv command line arguments if any.
     */
    public static void main(String[] argv)
    {
    	File errLog = new File("decoder_errLog_"  + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) +"__" +
				new java.text.SimpleDateFormat("HH-mm-ss").format(new java.util.Date()) +".log");
    	if(!errLog.exists()) {
			try {
				errLog.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	 try {
 			System.setErr(new PrintStream(new FileOutputStream(errLog)));
 		} catch (FileNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
        long      counter = 0;
        int args, totPackets, i, k;
        short ret, len, tot_len;
        short[] len_ptr = new short[1];
        short nBytes = 0;
        byte[]  payload = new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 )];
        byte[]  payloadEnd = null;
        int     payloadEnd_offset;
        
        byte[]  payloadToDec = null;
        
        byte[]  FECpayload = new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES];
        byte[]  payloadPtr;
        int     payloadPtr_offset;
        
        short nBytesFEC;
        short[]   nBytesFEC_ptr = new short[1]; 

        short[] nBytesPerPacket = new short[MAX_LBRR_DELAY + 1];
        short   totBytes;
        
        short[] out = new short[( ( FRAME_LENGTH_MS * MAX_API_FS_KHZ ) << 1 ) * MAX_INPUT_FRAMES ];
        short[] outPtr;
        int     outPtr_offset;
        
        String speechOutFileName;
        String bitInFileName;
        
        DataInputStream bitInFile = null;
        DataOutputStream speechOutFile = null;
        
        int API_Fs_Hz = 0;
        int decSizeBytes;
        
        SKP_Silk_decoder_state psDec;
        float     loss_prob;
        int frames, lost, quiet;
        SKP_SILK_SDK_DecControlStruct DecControl;
 
        DecControl = new SKP_SILK_SDK_DecControlStruct();

        if(argv.length<2)
        {
            print_usage(argv);
            System.exit(0);
        }
        /* default settings */
        quiet     = 0;
        loss_prob = 0.0f;

        /* get arguments */
        args = 0;
        bitInFileName = argv[args];
        args++;
        speechOutFileName = argv[args];
        args++;
        
        while(args<argv.length)
        {
            if(argv[args].compareToIgnoreCase("-loss") == 0)
            {
                loss_prob = Float.valueOf(argv[args+1]).floatValue();
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-Fs_API") == 0)
            {
                API_Fs_Hz = Integer.valueOf(argv[args+1]).intValue();
                args += 2;
            }
            else if(argv[args].compareToIgnoreCase("-quiet") == 0)
            {
                quiet = 1;
                args++;
            }
            else
            {
                System.out.printf( "Error: unrecognized setting: %s\n\n", argv[ args ] );
                print_usage( argv );
                System.exit(0);
            }
        }

        if( quiet ==0 ) {
            System.out.print("******************* Silk Decoder v " + Silk_dec_API.SKP_Silk_SDK_get_version()
                             +" ****************\n"  );
            System.out.println ( "Input:                       " +
                                bitInFileName );
            System.out.println( "Output:                      " +
                               speechOutFileName );
        }
        
        /* Open files */
        try {
            bitInFile = new DataInputStream(
                            new BufferedInputStream(
                                    new FileInputStream(bitInFileName)));
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        }   
        if( bitInFile == null ) {
            System.out.println( "Error: could not open input file " + bitInFileName );
            System.exit( 0 );
        } 
        try {
            speechOutFile = new DataOutputStream(
                                new BufferedOutputStream(
                                        new FileOutputStream(speechOutFileName)));
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        }
        if( speechOutFile == null ) {
            System.out.println( "Error: could not open output file " + speechOutFileName );
            System.exit( 0 );
        }

        /* Set the samplingrate that is requested for the output */
        if( API_Fs_Hz == 0 ) {
            DecControl.API_sampleRate = 24000;
        } else {
            DecControl.API_sampleRate = API_Fs_Hz;
        }

        /* Create decoder */
        int[] decSizeBytes_ptr = new int[1];
        ret = (short) Silk_dec_API.SKP_Silk_SDK_Get_Decoder_Size( decSizeBytes_ptr );
        decSizeBytes = decSizeBytes_ptr[0];
        if( ret != 0 ) {
            System.out.printf( "\nSKP_Silk_SDK_Get_Decoder_Size returned %d", ret );
        }
        psDec = new  SKP_Silk_decoder_state();
        
        /* Reset decoder */
        ret = (short) Silk_dec_API.SKP_Silk_SDK_InitDecoder( psDec );
        if( ret != 0 ) {
            System.out.printf( "\nSKP_Silk_InitDecoder returned %d", ret );
        }

        totPackets = 0;
        payloadEnd = payload;
        payloadEnd_offset = 0;

        Config._SYSTEM_IS_BIG_ENDIAN = true;
        
        /* Simulate the jitter buffer holding MAX_FEC_DELAY packets */
        for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
            /* Read payload size */
            try {
                nBytes = bitInFile.readShort();
            } catch (IOException e) {
                logger.error("error when read from input file", e);
            }           
            if(Config._SYSTEM_IS_BIG_ENDIAN)
            {
                short[] nBytes_ptr = new short[1];
                nBytes_ptr[0] = nBytes;
                swap_endian(nBytes_ptr, 1);
                nBytes = nBytes_ptr[0];
            }           
            /* Read payload */
            try {
                counter = bitInFile.read(payloadEnd, payloadEnd_offset+0, nBytes);
            } catch (IOException e) {
                logger.error("error when read from input file", e);
            }
    
            if( ( short )counter < nBytes ) {
                break;
            }
            nBytesPerPacket[ i ] = nBytes;
            payloadEnd_offset   += nBytes;
        }

        while( true ) {
            /* Read payload size */
            try {
                nBytes = bitInFile.readShort();
            } catch (IOException e) {
                logger.error("error when read from input file", e);
            }       
            if(Config._SYSTEM_IS_BIG_ENDIAN)
            {
                short[] nBytes_ptr = new short[1];
                nBytes_ptr[0] = nBytes;
                swap_endian(nBytes_ptr, 1);
                nBytes = nBytes_ptr[0];
            }
            if(nBytes>0)
                counter = 1;
            else 
                counter = 0;
                
            if( nBytes < 0 || counter < 1 ) {
                break;
            }
            
            /* Read payload */
            try {
                counter = bitInFile.read(payloadEnd, payloadEnd_offset, nBytes);
            } catch (IOException e) {
                logger.error("error when read from input file", e);
            }
            
            if( ( short )counter < nBytes ) {
                break;
            }

            /* Simulate losses */
            Random rand = new Random();
            if( ( (float)rand.nextInt(0x7fff) / (float)0x7fff >= loss_prob / 100 ) && counter > 0 ) {
                nBytesPerPacket[ MAX_LBRR_DELAY ] = nBytes;
                payloadEnd_offset                 += nBytes;
            } else {
                nBytesPerPacket[ MAX_LBRR_DELAY ] = 0;
            }

            if( nBytesPerPacket[ 0 ] == 0 ) {
                /* Indicate lost packet */
                lost = 1;

                /* Packet loss. Search after FEC in next packets. Should be done in the jitter buffer */
                payloadPtr = payload;
                payloadPtr_offset = 0;
                
                for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                    if( nBytesPerPacket[ i + 1 ] > 0 ) {
                        Silk_dec_API.SKP_Silk_SDK_search_for_LBRR(payloadPtr, payloadPtr_offset, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, 0, nBytesFEC_ptr);
                        nBytesFEC = nBytesFEC_ptr[0];
                        
                        if( nBytesFEC > 0 ) {
                            payloadToDec = FECpayload;
                            nBytes = nBytesFEC;
                            lost = 0;
                            break;
                        }
                    }
                    payloadPtr_offset += nBytesPerPacket[ i + 1 ];
                }
            } else {
                lost = 0;
                nBytes = nBytesPerPacket[ 0 ];
                payloadToDec = payload;
            }

            /* Silk decoder */
            outPtr = out;
            outPtr_offset = 0;
            tot_len = 0;
            
            
            if( lost == 0 ) {
                /* No Loss: Decode all frames in the packet */
                frames = 0;
                do {
                    /* Decode 20 ms */                  
                    ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode(psDec, DecControl, 0, payloadToDec, 0,  nBytes, outPtr, outPtr_offset, len_ptr);
                    len = len_ptr[0];
                    if( ret !=0 ) {
                        System.out.printf( "\nSKP_Silk_SDK_Decode returned %d", ret );
                    }
                    frames++;
                    outPtr_offset += len;
                    
                    tot_len += len;
                    if( frames > MAX_INPUT_FRAMES ) {
                        /* Hack for corrupt stream that could generate too many frames */
                        outPtr  = out;
                        outPtr_offset = 0;
                        
                        tot_len = 0;
                        frames  = 0;
                    }
                    /* Until last 20 ms frame of packet has been decoded */
                } while( DecControl.moreInternalDecoderFrames != 0 ); 
            } else {    
                /* Loss: Decode enough frames to cover one packet duration */
                for( i = 0; i < DecControl.framesPerPacket; i++ ) {
                    /* Generate 20 ms */
                    ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode(psDec, DecControl, 1, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr);
                    len = len_ptr[0];
                    
                    if( ret != 0) {
                        System.out.printf( "\nSKP_Silk_Decode returned %d", ret );
                    }
                    outPtr_offset += len;
                    tot_len += len;
                }
            }
            totPackets++;

            /* Write output to file */
            if(Config._SYSTEM_IS_BIG_ENDIAN)
            {
                swap_endian(out, tot_len);
            }

            for( int write=0; write<tot_len; write++)
            {
                try {
                    speechOutFile.writeShort(out[write]);
                } catch (IOException e) {
                    logger.error("error when write to the output file", e);
                }
            }

            /* Update buffer */
            totBytes = 0;
            for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                totBytes += nBytesPerPacket[ i + 1 ];
            }
            System.arraycopy(payload, nBytesPerPacket[ 0 ], payload, 0, totBytes);
            
            payloadEnd_offset -= nBytesPerPacket[ 0 ];
            
            System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0, MAX_LBRR_DELAY);
            
            if(quiet == 0)
            {
                System.err.printf("\r\nPackets decoded:             %d", totPackets);
                System.out.printf("\rPackets decoded:             %d", totPackets);

            }
        }
        /* Empty the recieve buffer */
        for( k = 0; k < MAX_LBRR_DELAY; k++ ) {
            if( nBytesPerPacket[ 0 ] == 0 ) {
                /* Indicate lost packet */
                lost = 1;

                /* Packet loss. Search after FEC in next packets. Should be done in the jitter buffer */
                payloadPtr = payload;
                payloadPtr_offset = 0;
                for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                    if( nBytesPerPacket[ i + 1 ] > 0 ) {
                        Silk_dec_API.SKP_Silk_SDK_search_for_LBRR( payloadPtr, payloadPtr_offset, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, 0, nBytesFEC_ptr );
                        nBytesFEC = nBytesFEC_ptr[0];
                        
                        if( nBytesFEC > 0 ) {
                            payloadToDec = FECpayload;
                            nBytes = nBytesFEC;
                            lost = 0;
                            break;
                        }
                    }
                    payloadPtr_offset += nBytesPerPacket[ i + 1 ];
                }
            } else {
                lost = 0;
                nBytes = nBytesPerPacket[ 0 ];
                payloadToDec = payload;
            }

            /* Silk decoder */
            outPtr  = out;
            outPtr_offset = 0;
            tot_len = 0;

            if( lost == 0 ) {
                /* No loss: Decode all frames in the packet */
                frames = 0;
                do {
                    /* Decode 20 ms */
                    ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode( psDec, DecControl, 0, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr );
                    len = len_ptr[0];
                    if( ret != 0) {
                        System.out.printf( "\nSKP_Silk_SDK_Decode returned %d", ret );
                    }

                    frames++;
                    outPtr_offset += len;
                    tot_len += len;
                    if( frames > MAX_INPUT_FRAMES ) {
                        /* Hack for corrupt stream that could generate too many frames */
                        outPtr  = out;
                        outPtr_offset = 0;
                        
                        tot_len = 0;
                        frames  = 0;
                    }
                /* Until last 20 ms frame of packet has been decoded */
                } while( DecControl.moreInternalDecoderFrames != 0);
            } else {    
                /* Loss: Decode enough frames to cover one packet duration */

                /* Generate 20 ms */
                for( i = 0; i < DecControl.framesPerPacket; i++ ) {
                    ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode( psDec, DecControl, 1, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr );
                    len = len_ptr[0];
                    
                    if( ret != 0) {
                        System.out.printf( "\nSKP_Silk_Decode returned %d", ret );
                    }
                    outPtr_offset += len;
                    tot_len += len;
                }
            }
            totPackets++;

            if(Config._SYSTEM_IS_BIG_ENDIAN)
            {
                swap_endian(out, tot_len);
            }        
            for( int write =0; write<tot_len; write++)
            {
                try {
                    speechOutFile.writeShort(out[write]);
                } catch (IOException e) {
                    logger.error("error when write to the output file", e);
                }
            }
            
            /* Update Buffer */
            totBytes = 0;
            for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                totBytes += nBytesPerPacket[ i + 1 ];
            }
            System.arraycopy(payload, nBytesPerPacket[ 0 ], payload, 0, totBytes);
            
            payloadEnd_offset -= nBytesPerPacket[ 0 ];
            
            System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0, MAX_LBRR_DELAY);
            
            if( quiet == 0 ) {
                System.err.printf("\r\nPackets decoded:              %d", totPackets);
                System.out.printf("\rPackets decoded:              %d", totPackets);
            }
        }
        if( quiet == 0 ) {
            System.out.printf( "\nDecoding Finished \n" );
        }

        try {
            speechOutFile.close();
        } catch (IOException e) {
            logger.error("error when close the output file", e);
        }
        return ;
    }
}
