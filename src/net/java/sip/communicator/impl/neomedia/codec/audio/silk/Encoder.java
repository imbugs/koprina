
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;

import net.java.sip.communicator.util.*;

/**
 * Define codec specific settings.
 * 
 * @author Jing Dai
 * @author Dingxin Xu
 */
class Encoder_constants
{
    static final int MAX_BYTES_PER_FRAME = 250; // Equals peak bitrate of 100
                                                // kbps

    static final int MAX_INPUT_FRAMES = 5;

    static final int MAX_LBRR_DELAY = 2;

    static final int MAX_FRAME_LENGTH = 480;

    static final int FRAME_LENGTH_MS = 20;

    static final int MAX_API_FS_KHZ = 48;
}

public class Encoder
    extends Encoder_constants
{
    /**
     * The <tt>Logger</tt> used by the <tt>Encoder</tt> class and its instances
     * for logging output.
     */
    private static final Logger logger = Logger.getLogger(Encoder.class);

    /**
     * Function to convert a little endian int16 to a big endian int16 or vica
     * verca
     * 
     * @param vec
     * @param len
     */
    static void swap_endian(short[] vec, /* I/O array of */
    int len /* I length */
    )
    {
        int i;
        short tmp;

        for (i = 0; i < len; i++)
        {
            tmp = vec[i];
            vec[i] = (short) (((tmp << 8) & 0xFF00) | ((tmp >>> 8) & 0x00FF));
        }
    }

    static void byteToShortArray(byte[] byteArray, int byteArray_offset,
        short[] shortArray, int shortArray_offset, int out_len)
    {
        int i;
        int tmp;
        for (i = 0; i < out_len; i++)
        {
            tmp = (byteArray[byteArray_offset + 2 * i] << 8) & 0x0000FF00;
            tmp |= byteArray[byteArray_offset + 2 * i + 1] & 0x000000FF;
            shortArray[shortArray_offset + i] = (short) tmp;
        }
    }

    static void print_usage()
    {
        System.out.printf("\nusage: Encoder in.pcm out.bit [settings]\n");
        System.out.printf("\nin.pcm               : Speech input to encoder");
        System.out
            .printf("\nout.bit              : Bitstream output from encoder");
        System.out.printf("\n   settings:");
        System.out
            .printf("\n-Fs_API <Hz>         : API sampling rate in Hz, default: 24000");
        System.out
            .printf("\n-Fs_maxInternal <Hz> : Maximum internal sampling rate in Hz, default: 24000");
        System.out
            .printf("\n-packetlength <ms>   : Packet interval in ms, default: 20");
        System.out
            .printf("\n-rate <bps>          : Target bitrate; default: 25000");
        System.out
            .printf("\n-loss <perc>         : Uplink loss estimate, in percent (0-100); default: 0");
        System.out
            .printf("\n-inbandFEC <flag>    : Enable inband FEC usage (0/1); default: 0");
        System.out
            .printf("\n-complexity <comp>   : Set complexity, 0: low, 1: medium, 2: high; default: 2");
        System.out
            .printf("\n-DTX <flag>          : Enable DTX (0/1); default: 0");
        System.out
            .printf("\n-quiet               : Print only some basic values");
        System.out.printf("\n");
    }

    public static void main(String[] argv) throws IOException
    {
        int counter;
        int k, args, totPackets, totActPackets, ret;
        short[] nBytes = new short[1];
        double sumBytes, sumActBytes, avg_rate, act_rate, nrg;
        byte[] payload = new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES];
        short[] in =
            new short[FRAME_LENGTH_MS * MAX_API_FS_KHZ * MAX_INPUT_FRAMES];
        byte[] in_tmp =
            new byte[2 * FRAME_LENGTH_MS * MAX_API_FS_KHZ * MAX_INPUT_FRAMES];

        String speechInFileName, bitOutFileName;
        FileInputStream speechInFile = null;
        DataInputStream speechInData = null;
        FileOutputStream bitOutFile = null;
        DataOutputStream bitOutData = null;

        int[] encSizeBytes = new int[1];
        SKP_Silk_encoder_state psEnc;

        short[] nBytes_LE = new short[1];

        /* default settings */
        int API_fs_Hz = 24000;
        int max_internal_fs_Hz = 0;
        int targetRate_bps = 25000;
        int packetSize_ms = 20;
        int frameSizeReadFromFile_ms = 20;
        int packetLoss_perc = 0, complexity_mode = 2, smplsSinceLastPacket;
        int INBandFEC_enabled = 0, DTX_enabled = 0, quiet = 0;
        SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder
        encControl = new SKP_SILK_SDK_EncControlStruct();

        if (argv.length < 2)
        {
            print_usage();
            System.exit(0);
        }

        /* get arguments */
        args = 0;
        speechInFileName = argv[args];
        args++;
        bitOutFileName = argv[args];
        args++;
        while (args < argv.length)
        {
            if (argv[args].compareToIgnoreCase("-Fs_API") == 0)
            {
                API_fs_Hz = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-Fs_maxInternal") == 0)
            {
                max_internal_fs_Hz = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-packetlength") == 0)
            {
                packetSize_ms = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-rate") == 0)
            {
                targetRate_bps = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-loss") == 0)
            {
                packetLoss_perc = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-complexity") == 0)
            {
                complexity_mode = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-inbandFEC") == 0)
            {
                INBandFEC_enabled = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-DTX") == 0)
            {
                DTX_enabled = Integer.parseInt(argv[args + 1]);
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-quiet") == 0)
            {
                quiet = 1;
                args++;
            }
            else
            {
                System.out.printf("Error: unrecognized setting: %s\n\n",
                    argv[args]);
                print_usage();
                System.exit(0);
            }
        }

        /* If no max internal set set to API fs */
        if (max_internal_fs_Hz == 0)
        {
            max_internal_fs_Hz = API_fs_Hz;
        }

        /* Print options */
        if (quiet == 0)
        {
            System.out.printf("******************* Silk Encoder v "
                + Silk_dec_API.SKP_Silk_SDK_get_version()
                + " ****************\n");
            System.out.printf("Input:                          %s\n",
                speechInFileName);
            System.out.printf("Output:                         %s\n",
                bitOutFileName);
            System.out.printf("API sampling rate:              %d Hz\n",
                API_fs_Hz);
            System.out.printf("Maximum internal sampling rate: %d Hz\n",
                max_internal_fs_Hz);
            System.out.printf("Packet interval:                %d ms\n",
                packetSize_ms);
            System.out.printf("Inband FEC used:                %d\n",
                INBandFEC_enabled);
            System.out.printf("DTX used:                       %d\n",
                DTX_enabled);
            System.out.printf("Complexity:                     %d\n",
                complexity_mode);
            System.out.printf("Target bitrate:                 %d bps\n",
                targetRate_bps);
        }

        /* Open files */
        try
        {
            speechInFile = new FileInputStream(speechInFileName);
            speechInData = new DataInputStream(speechInFile);
        }
        catch (IOException e)
        {
            logger.error("Error: could not open input file %s\n", e);
            System.exit(0);
        }
        if (speechInData == null)
        {
            System.out.println("Error: could not open output file "
                + speechInFileName);
            System.exit(0);
        }
        try
        {
            bitOutFile = new FileOutputStream(bitOutFileName);
            bitOutData = new DataOutputStream(bitOutFile);
        }
        catch (IOException e)
        {
            logger.error("Error: could not open output file %s\n", e);
            System.exit(0);
        }
        if (bitOutData == null)
        {
            System.out.println("Error: could not open output file "
                + bitOutFileName);
            System.exit(0);
        }

        /* Create Encoder */
        ret = Silk_enc_API.SKP_Silk_SDK_Get_Encoder_Size(encSizeBytes);
        if (ret != 0)
        {
            System.out.printf("\nSKP_Silk_create_encoder returned %d", ret);
        }

        psEnc = new SKP_Silk_encoder_state();

        /* Reset Encoder */
        ret = Silk_enc_API.SKP_Silk_SDK_InitEncoder(psEnc, encControl);
        if (ret != 0)
        {
            System.out.printf("\nSKP_Silk_reset_encoder returned %d", ret);
        }

        /* Set Encoder parameters */
        encControl.API_sampleRate = API_fs_Hz;
        encControl.maxInternalSampleRate = max_internal_fs_Hz;
        encControl.packetSize = (packetSize_ms * API_fs_Hz) / 1000;
        encControl.packetLossPercentage = packetLoss_perc;
        encControl.useInBandFEC = INBandFEC_enabled;
        encControl.useDTX = DTX_enabled;
        encControl.complexity = complexity_mode;
        encControl.bitRate = (targetRate_bps > 0 ? targetRate_bps : 0);

        if (API_fs_Hz > MAX_API_FS_KHZ * 1000 || API_fs_Hz < 0)
        {
            System.out
                .printf(
                    "\nError: API sampling rate = %d out of range, valid range 8000 - 48000 \n \n",
                    API_fs_Hz);
            System.exit(0);
        }

        totPackets = 0;
        totActPackets = 0;
        smplsSinceLastPacket = 0;
        sumBytes = 0.0;
        sumActBytes = 0.0;

        while (true)
        {
            /* Read input from file */
            counter =
                speechInData.read(in_tmp, 0,
                    2 * (frameSizeReadFromFile_ms * API_fs_Hz) / 1000) >> 1;
            byteToShortArray(in_tmp, 0, in, 0, counter);

            if (Config._SYSTEM_IS_BIG_ENDIAN)
                swap_endian(in, counter);

            if ((int) counter < ((frameSizeReadFromFile_ms * API_fs_Hz) / 1000))
            {
                break;
            }

            /* max payload size */
            nBytes[0] = MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES;

            /* Silk Encoder */
            ret =
                Silk_enc_API.SKP_Silk_SDK_Encode(psEnc, encControl, in, 0,
                    counter, payload, 0, nBytes);
            if (ret != 0)
            {
                System.out.printf("\nSKP_Silk_Encode returned %d", ret);
                break;
            }

            /* Get packet size */
            packetSize_ms =
                (int) ((1000 * (int) encControl.packetSize) / encControl.API_sampleRate);

            smplsSinceLastPacket += (int) counter;

            if (((1000 * smplsSinceLastPacket) / API_fs_Hz) == packetSize_ms)
            {
                /* Sends a dummy zero size packet in case of DTX period */
                /* to make it work with the decoder test program. */
                /* In practice should be handled by RTP sequence numbers */
                totPackets++;
                sumBytes += nBytes[0];
                nrg = 0.0;
                for (k = 0; k < (int) counter; k++)
                {
                    nrg += in[k] * (double) in[k];
                }
                if ((nrg / (int) counter) > 1e3)
                {
                    sumActBytes += nBytes[0];
                    totActPackets++;
                }

                /* Write payload size */
                if (Config._SYSTEM_IS_BIG_ENDIAN)
                {
                    nBytes_LE[0] = nBytes[0];
                    swap_endian(nBytes_LE, 1);
                    bitOutData.writeShort(nBytes_LE[0]);
                }
                else
                {
                    bitOutData.writeShort(nBytes[0]);
                }          

                /* Write payload */
                bitOutData.write(payload, 0, nBytes[0]);

                if (quiet == 0)
                {
                    System.err.printf("\nPackets encoded:              "
                        + totPackets);
                }
                smplsSinceLastPacket = 0;
            }
        }

        /* Write dummy because it can not end with 0 bytes */
        nBytes[0] = -1;

        /* Write payload size */
        bitOutData.writeShort(nBytes[0]);

        speechInFile.close();
        speechInData.close();
        bitOutFile.close();
        bitOutData.close();

        avg_rate = 8.0 / packetSize_ms * sumBytes / totPackets;
        act_rate = 8.0 / packetSize_ms * sumActBytes / totActPackets;
        if (quiet == 0)
        {
            System.out.printf("\nAverage bitrate:             %.3f kbps",
                avg_rate);
            System.out.printf("\nActive bitrate:              %.3f kbps",
                act_rate);
            System.out.printf("\n\n");
        }
        else
        {
            /* print average and active bitrates */
            System.out.printf("%.3f %.3f \n", avg_rate, act_rate);
        }
    }
}