package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;
import java.util.*;

import javax.media.*;
import javax.media.format.*;

public class DecoderTest
{
    static final int MAX_BYTES_PER_FRAME = 1024;

    static final int MAX_INPUT_FRAMES = 5;

    static final int MAX_FRAME_LENGTH = 480;

    static final int FRAME_LENGTH_MS = 20;

    static final int MAX_API_FS_KHZ = 48;

    static final int MAX_LBRR_DELAY = 2;

    /**
     * Function to convert a little endian int16 to a big endian int16 or vica
     * verca.
     * 
     * @param vec
     * @param offset
     * @param len
     */
    static void swap_endian(short vec[], int offset, int len)
    {
        int i;
        short tmp;
        byte[] p1;
        byte[] p2;

        for (i = offset; i < offset + len; i++)
        {
            tmp = vec[i];
            tmp = (short) (((tmp >>> 8) & 0xFF) + ((tmp << 8) & 0xFF00));
            vec[i] = tmp;
        }
    }

    private static void printUsage(String[] argv)
    {
        System.out.printf("\nusage:  in.bit out.pcm [settings]\n");
        System.out.printf("\nin.bit       : Bitstream input to decoder");
        System.out.printf("\nout.pcm      : Speech output from decoder");
        System.out.printf("\n   settings:");
        System.out
            .printf("\n-Fs_API <Hz> : Sampling rate of output signal in Hz; default: 24000");
        System.out
            .printf("\n-loss <perc> : Simulated packet loss percentage (0-100); default: 0");
        System.out.printf("\n");
    }

    public static void main(String[] argv)
    {
        if (argv.length < 2)
        {
            printUsage(argv);
            System.exit(0);
        }

        int args = 0;
        String speechOutFileName;
        String bitInFileName;
        DataInputStream bitInFile = null;
        DataOutputStream speechOutFile = null;
        bitInFileName = argv[args++];
        speechOutFileName = argv[args++];
        int quiet = 0;
        float loss_prob = 0.0f;
        int API_Fs_Hz = 0;

        while (args < argv.length)
        {
            if (argv[args].compareToIgnoreCase("-loss") == 0)
            {
                loss_prob = Float.valueOf(argv[args + 1]).floatValue();
                args += 2;
            }
            else if (argv[args].compareToIgnoreCase("-Fs_API") == 0)
            {
                API_Fs_Hz = Integer.valueOf(argv[args + 1]).intValue();
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
                printUsage(argv);
                System.exit(0);
            }
        }

        if (quiet == 0)
        {
            System.out.print("******************* Silk Decoder v "
                + Silk_dec_API.SKP_Silk_SDK_get_version()
                + " ****************\n");
            System.out.println("Input:                       " + bitInFileName);
            System.out.println("Output:                      "
                + speechOutFileName);
        }
        /* Open files */
        try
        {
            bitInFile =
                new DataInputStream(new BufferedInputStream(
                    new FileInputStream(bitInFileName)));
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (bitInFile == null)
        {
            System.out.println("Error: could not open input file "
                + bitInFileName);
            System.exit(0);
        }
        try
        {
            speechOutFile =
                new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(speechOutFileName)));
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (speechOutFile == null)
        {
            System.out.println("Error: could not open output file "
                + speechOutFileName);
            System.exit(0);
        }

        JavaDecoder decoder = new JavaDecoder();

        /* Set the samplingrate that is requested for the output */
        if (API_Fs_Hz == 0)
        {
            decoder.setFs(24000);
        }
        else
        {
            decoder.setFs(API_Fs_Hz);
        }

        // TODO:how to set the input/output audio format???
        AudioFormat inputFormat =
            new AudioFormat("SILK encoding", AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                new byte[0].getClass());
        AudioFormat outputFormat =
            new AudioFormat("SILK encoding", AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                AudioFormat.NOT_SPECIFIED, AudioFormat.NOT_SPECIFIED,
                new short[0].getClass());

        decoder.setInputFormat(inputFormat);
        decoder.setOutputFormat(outputFormat);

        try
        {
            decoder.open();
        }
        catch (ResourceUnavailableException e)
        {
            // TODO Auto-generated catch block
            System.err.println("Error when open() " + e);
            e.printStackTrace();
            System.exit(1);
        }

        boolean isLittleEndian = true;
        short nBytes = 0;
        int counter = 0;
        byte[] payload =
            new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES
                * (MAX_LBRR_DELAY + 1)];
        byte[] payloadEnd;
        int payloadEnd_offset;
        payloadEnd = payload;
        payloadEnd_offset = 0;
        byte[] payloadToDec = null;
        short[] nBytesPerPacket = new short[MAX_LBRR_DELAY + 1];
        short[] out =
            new short[((FRAME_LENGTH_MS * MAX_API_FS_KHZ) << 1)
                * MAX_INPUT_FRAMES];
        int totBytes = 0;
        int lost = 0;
        Buffer inBuffer = new Buffer();
        Buffer outBuffer = new Buffer();
        inBuffer.setFormat(inputFormat);
        outBuffer.setFormat(outputFormat);
        int totPackets = 0;
        /* Simulate the jitter buffer holding MAX_FEC_DELAY packets */
        for (int i = 0; i < MAX_LBRR_DELAY; i++)
        {
            /* Read payload size */
            try
            {
                nBytes = bitInFile.readShort();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isLittleEndian)
            {
                short[] nBytes_ptr = new short[1];
                nBytes_ptr[0] = nBytes;
                swap_endian(nBytes_ptr, 0, 1);
                nBytes = nBytes_ptr[0];
            }
            /* Read payload */
            try
            {
                counter =
                    bitInFile.read(payloadEnd, payloadEnd_offset + 0, nBytes);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if ((short) counter < nBytes)
            {
                break;
            }
            nBytesPerPacket[i] = nBytes;
            payloadEnd_offset += nBytes;
        }

        while (true)
        {
            /* Read payload size */
            try
            {
                nBytes = bitInFile.readShort();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isLittleEndian)
            {
                short[] nBytes_ptr = new short[1];
                nBytes_ptr[0] = nBytes;
                swap_endian(nBytes_ptr, 0, 1);
                nBytes = nBytes_ptr[0];
            }
            if (nBytes <= 0)
            {
                break;
            }
            /* Read payload */
            try
            {
                counter =
                    bitInFile.read(payloadEnd, payloadEnd_offset + 0, nBytes);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if ((short) counter < nBytes)
            {
                break;
            }

            /* Simulate losses */
            Random rand = new Random();
            if (((float) rand.nextInt(0x7fff) / (float) 0x7fff >= loss_prob / 100)
                && counter > 0)
            {
                nBytesPerPacket[MAX_LBRR_DELAY] = nBytes;
                payloadEnd_offset += nBytes;
            }
            else
            {
                nBytesPerPacket[MAX_LBRR_DELAY] = 0;// this packet has been
                                                    // lost.
            }

            if (nBytesPerPacket[0] == 0)
            {
                /* Indicate lost packet */
                lost = 1;
            }
            else
            {
                lost = 0;
                nBytes = nBytesPerPacket[0];
                payloadToDec = payload;
            }

            if (lost == 0)
            {
                inBuffer.setData(payloadToDec);
                inBuffer.setOffset(0);
                inBuffer.setLength(nBytes);
                outBuffer.setData(out);
                outBuffer.setOffset(0);
                outBuffer.setLength(0);
                int processRes = -1;
                do
                {
                    processRes = decoder.process(inBuffer, outBuffer);
                }
                while ((processRes & PlugIn.INPUT_BUFFER_NOT_CONSUMED) > 0);
                if (processRes == PlugIn.BUFFER_PROCESSED_OK)
                {
                    totPackets++;
                    short[] outData = (short[]) outBuffer.getData();
                    int tot_len = outBuffer.getLength();
                    if (isLittleEndian)
                    {
                        swap_endian(outData, outBuffer.getOffset(), tot_len);
                    }
                    for (int write = 0; write < tot_len; write++)
                    {
                        try
                        {
                            speechOutFile.writeShort(out[write]);
                        }
                        catch (IOException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    /* Update buffer */
                    totBytes = 0;
                    for (int i = 0; i < MAX_LBRR_DELAY; i++)
                    {
                        totBytes += nBytesPerPacket[i + 1];
                    }
                    System.arraycopy(payload, nBytesPerPacket[0], payload, 0,
                        totBytes);
                    payloadEnd_offset -= nBytesPerPacket[0];
                    System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0,
                        MAX_LBRR_DELAY);
                    if (quiet == 0)
                    {
                        System.err.printf("\rPackets decoded:             %d",
                            totPackets);
                    }
                }
            }
            else
            {
                // lost = 1;
            }
        }// end while
        //
        /* Empty the recieve buffer */
        for (int k = 0; k < MAX_LBRR_DELAY; k++)
        {
            if (nBytesPerPacket[0] == 0)
            {
                /* Indicate lost packet */
                lost = 1;
            }
            else
            {
                lost = 0;
                nBytes = nBytesPerPacket[0];
                payloadToDec = payload;
            }

            if (lost == 0)
            {
                inBuffer.setData(payloadToDec);
                inBuffer.setOffset(0);
                inBuffer.setLength(nBytes);
                outBuffer.setData(out);
                outBuffer.setOffset(0);
                outBuffer.setLength(0);
                int processRes = -1;
                do
                {
                    processRes = decoder.process(inBuffer, outBuffer);
                }
                while (processRes == PlugIn.INPUT_BUFFER_NOT_CONSUMED);
                if (processRes == PlugIn.BUFFER_PROCESSED_OK)
                {
                    totPackets++;
                    short[] outData = (short[]) outBuffer.getData();
                    int tot_len = outBuffer.getLength();
                    if (isLittleEndian)
                    {
                        swap_endian(outData, outBuffer.getOffset(), tot_len);
                    }
                    for (int write = 0; write < tot_len; write++)
                    {
                        try
                        {
                            speechOutFile.writeShort(out[write]);
                        }
                        catch (IOException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    /* Update buffer */
                    totBytes = 0;
                    for (int i = 0; i < MAX_LBRR_DELAY; i++)
                    {
                        totBytes += nBytesPerPacket[i + 1];
                    }
                    System.arraycopy(payload, nBytesPerPacket[0], payload, 0,
                        totBytes);
                    payloadEnd_offset -= nBytesPerPacket[0];
                    System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0,
                        MAX_LBRR_DELAY);
                    if (quiet == 0)
                    {
                        System.err.printf("\rPackets decoded:             %d",
                            totPackets);
                    }
                }
            }
            else
            {
                // lost = 1;
            }
        }// end for

        if (quiet == 0)
        {
            System.out.printf("\nDecoding Finished \n");
        }
        decoder.close();

        try
        {
            speechOutFile.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return;
    }// end main

}
