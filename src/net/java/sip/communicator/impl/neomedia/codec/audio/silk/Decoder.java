/***********************************************************************
Copyright (c) 2006-2010, Skype Limited. All rights reserved. 
Redistribution and use in source and binary forms, with or without 
modification, (subject to the limitations in the disclaimer below) 
are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright 
notice, this list of conditions and the following disclaimer in the 
documentation and/or other materials provided with the distribution.
- Neither the name of Skype Limited, nor the names of specific 
contributors, may be used to endorse or promote products derived from 
this software without specific prior written permission.
NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED 
BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF 
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
***********************************************************************/
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;
import java.util.*;

/**
 * Silk decoder test program
 * @author Xu Dingxin
 *
 */
public class Decoder 
{
    static final int MAX_BYTES_PER_FRAME =    1024;
    static final int MAX_INPUT_FRAMES =       5;
    static final int MAX_FRAME_LENGTH =       480;
    static final int FRAME_LENGTH_MS =        20;
    static final int MAX_API_FS_KHZ =         48;
    static final int MAX_LBRR_DELAY =         2;

    /**
     * convert a little endian int16 to a big endian int16
     * or vica verca
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
    
    static void print_usage(String[] argv) {
        System.out.printf( "\nusage:  in.bit out.pcm [settings]\n" );
        System.out.printf( "\nin.bit       : Bitstream input to decoder" );
        System.out.printf( "\nout.pcm      : Speech output from decoder" );
        System.out.printf( "\n   settings:" );
        System.out.printf( "\n-Fs_API <Hz> : Sampling rate of output signal in Hz; default: 24000" );
        System.out.printf( "\n-loss <perc> : Simulated packet loss percentage (0-100); default: 0" );
        System.out.printf( "\n" );
    }
    
    public static void main(String[] argv)
    {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                System.err.printf("\rPackets decoded:             %d", totPackets);
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
                System.err.printf("\rPackets decoded:              %d", totPackets);
            }
        }
        if( quiet == 0 ) {
            System.out.printf( "\nDecoding Finished \n" );
        }

        try {
            speechOutFile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ;
    }
}
