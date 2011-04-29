/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Decoder" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/test/SKP_Decoder.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;
import java.util.*;

/**
 * Silk decoder test program
 * @author 
 *
 */
public class Decoder 
{

	/* Define codec specific settings should be moved to h file */
	static final int MAX_BYTES_PER_FRAME =    1024;
	static final int MAX_INPUT_FRAMES =       5;
	static final int MAX_FRAME_LENGTH =       480;
	static final int FRAME_LENGTH_MS =        20;
	static final int MAX_API_FS_KHZ =         48;
	static final int MAX_LBRR_DELAY =         2;

//djinn ??	#ifdef _SYSTEM_IS_BIG_ENDIAN
	/* Function to convert a little endian int16 to a */
	/* big endian int16 or vica verca                 */
	static void swap_endian(
	    short       vec[],
	    int         len
	)
	{
	    int i;
	    short tmp;
//	    SKP_uint8 *p1, *p2;
	    byte[] p1;
	    byte[] p2;

	    for( i = 0; i < len; i++ ){
	        tmp = vec[ i ];
//	        p1 = (SKP_uint8 *)&vec[ i ]; p2 = (SKP_uint8 *)&tmp;
//	        p1[ 0 ] = p2[ 1 ]; p1[ 1 ] = p2[ 0 ];
//djinn ???	
	        tmp = (short) (((tmp>>>8)&0xFF) + ((tmp<<8)&0xFF00)) ;
	        vec[i] = tmp;
	    }
	}
//	#endif

//	static void print_usage(char* argv[]) {
//	    printf( "\nusage: %s in.bit out.pcm [settings]\n", argv[ 0 ] );
//	    printf( "\nin.bit       : Bitstream input to decoder" );
//	    printf( "\nout.pcm      : Speech output from decoder" );
//	    printf( "\n   settings:" );
//	    printf( "\n-Fs_API <Hz> : Sampling rate of output signal in Hz; default: 24000" );
//	    printf( "\n-loss <perc> : Simulated packet loss percentage (0-100); default: 0" );
//	    printf( "\n" );
//	}
	static void print_usage(String[] argv) {
//	    System.out.printf( "\nusage: %s in.bit out.pcm [settings]\n", argv[ 0 ] );
		 System.out.printf( "\nusage:  in.bit out.pcm [settings]\n" );
	    System.out.printf( "\nin.bit       : Bitstream input to decoder" );
	    System.out.printf( "\nout.pcm      : Speech output from decoder" );
	    System.out.printf( "\n   settings:" );
	    System.out.printf( "\n-Fs_API <Hz> : Sampling rate of output signal in Hz; default: 24000" );
	    System.out.printf( "\n-loss <perc> : Simulated packet loss percentage (0-100); default: 0" );
	    System.out.printf( "\n" );
	}
	
//	int main( int argc, char* argv[] )
	public static void main(String[] argv)
	{
		int writeNum = 0;
//	    size_t    counter;
		long 	  counter = 0;
	    int args, totPackets, i, k;
	    short ret, len, tot_len;
	    short[] len_ptr = new short[1];
	    short nBytes = 0;
//	    SKP_uint8 payload[    MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
	    byte[]	payload = new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 )];
//	    SKP_uint8 *payloadEnd = NULL, *payloadToDec = NULL;
	    byte[]  payloadEnd = null;
	    int     payloadEnd_offset;
	    
	    byte[]  payloadToDec = null;
	    
//	    SKP_uint8 FECpayload[ MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES ], *payloadPtr;
	    byte[]  FECpayload = new byte[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES];
	    byte[]  payloadPtr;
	    int     payloadPtr_offset;
	    
	    short nBytesFEC;
	    short[]   nBytesFEC_ptr = new short[1]; 
//	    short nBytesPerPacket[ MAX_LBRR_DELAY + 1 ], totBytes;
	    short[] nBytesPerPacket = new short[MAX_LBRR_DELAY + 1];
	    short   totBytes;
	    
//	    short out[ ( ( FRAME_LENGTH_MS * MAX_API_FS_KHZ ) << 1 ) * MAX_INPUT_FRAMES ], *outPtr;
	    short[] out = new short[( ( FRAME_LENGTH_MS * MAX_API_FS_KHZ ) << 1 ) * MAX_INPUT_FRAMES ];
	    short[] outPtr;
	    int     outPtr_offset;
	    
//djinn ??	    char      speechOutFileName[ 150 ], bitInFileName[ 150 ];
	    String speechOutFileName;
	    String bitInFileName;
	    
//	    FILE      *bitInFile, *speechOutFile;
//	    File       bitInFile,  speechOutFile;
	    DataInputStream bitInFile = null;
	    DataOutputStream speechOutFile = null;
	    
	    int API_Fs_Hz = 0;
	    int decSizeBytes;
//	    void      *psDec;
//	    Object    psDec;
	    SKP_Silk_decoder_state psDec;
	    float     loss_prob;
	    int frames, lost, quiet;
	    SKP_SILK_SDK_DecControlStruct DecControl;
//djinn ?? How to initialize 	DecControl    
	    DecControl = new SKP_SILK_SDK_DecControlStruct();

//	    if( argc < 3 ) {
//	        print_usage( argv );
//	        exit( 0 );
//	    } 
	    if(argv.length<2)
	    {
	    	print_usage(argv);
	    	System.exit(0);
	    }
	    /* default settings */
	    quiet     = 0;
	    loss_prob = 0.0f;

	    /* get arguments */
//	    args = 1;
	    args = 0;
//	    strcpy( bitInFileName, argv[ args ] );
	    bitInFileName = argv[args];
	    args++;
//	    strcpy( speechOutFileName, argv[ args ] );
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
//	        System.out.printf("******************* Compiled for %d bit cpu ********* \n", (int)sizeof(void*) * 8 );
//how to detect the CPU bitmode in Java?
	        System.out.println ( "Input:                       " +
	        		            bitInFileName );
	        System.out.println( "Output:                      " +
	        		           speechOutFileName );
	    }
	    /* Open files */
//	    bitInFile = fopen( bitInFileName, "rb" );
//	    bitInFile = new File(bitInFileName);
	    
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
//	    speechOutFile = fopen( speechOutFileName, "wb" );
//	    speechOutFile = new File(speechOutFileName);
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
//
	    short[] test_short = Silk_tables_NLSF_CB0_16.SKP_Silk_NLSF_MSVQ_CB0_16_Q15;
//	    System.err.println(test_short[0]);
//	    System.err.println(test_short[1]);
//	    System.err.println(test_short[2]);
//	    
//	    psDec = malloc( decSizeBytes );
	    psDec = new  SKP_Silk_decoder_state();
	    
	    /* Reset decoder */
	    ret = (short) Silk_dec_API.SKP_Silk_SDK_InitDecoder( psDec );
	    if( ret != 0 ) {
	        System.out.printf( "\nSKP_Silk_InitDecoder returned %d", ret );
	    }

	    totPackets = 0;
	    payloadEnd = payload;
	    payloadEnd_offset = 0;
//
	    Config._SYSTEM_IS_BIG_ENDIAN = true;
	    
	    /* Simulate the jitter buffer holding MAX_FEC_DELAY packets */
	    for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
	        /* Read payload size */
//	        counter = fread( &nBytes, sizeof( short ), 1, bitInFile );
	    	try {
				nBytes = bitInFile.readShort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
//	#ifdef _SYSTEM_IS_BIG_ENDIAN
	    	if(Config._SYSTEM_IS_BIG_ENDIAN)
	    	{
	    		short[] nBytes_ptr = new short[1];
	    		nBytes_ptr[0] = nBytes;
//	    		swap_endian( &nBytes, 1 );
	    		swap_endian(nBytes_ptr, 1);
	    		nBytes = nBytes_ptr[0];
	    	}
	        
//	#endif
	        /* Read payload */
//	        counter = fread( payloadEnd, sizeof( SKP_uint8 ), nBytes, bitInFile );
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
//	        payloadEnd          += nBytes;
	        payloadEnd_offset   += nBytes;
	    }

	    while( true ) {
	        /* Read payload size */
//	        counter = fread( &nBytes, sizeof( short ), 1, bitInFile );
	    	try {
				nBytes = bitInFile.readShort();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    	
//	#ifdef _SYSTEM_IS_BIG_ENDIAN
//	        swap_endian( &nBytes, 1 );
	        if(Config._SYSTEM_IS_BIG_ENDIAN)
		    {
	        	short[] nBytes_ptr = new short[1];
		    	nBytes_ptr[0] = nBytes;
//		    	swap_endian( &nBytes, 1 );
		    	swap_endian(nBytes_ptr, 1);
		    	nBytes = nBytes_ptr[0];
		    }
//	#endif
	    	if(nBytes>0)
	    		counter = 1;
	    	else 
	    		counter = 0;
	    		
	        if( nBytes < 0 || counter < 1 ) {
	            break;
	        }
	        
	        /* Read payload */
//	        counter = fread( payloadEnd, sizeof( SKP_uint8 ), nBytes, bitInFile );
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
//	        if( ( (float)rand() / (float)RAND_MAX >= loss_prob / 100 ) && counter > 0 ) {
	        if( ( (float)rand.nextInt(0x7fff) / (float)0x7fff >= loss_prob / 100 ) && counter > 0 ) {
	            nBytesPerPacket[ MAX_LBRR_DELAY ] = nBytes;
	  //          payloadEnd                       += nBytes;
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
//	                    SKP_Silk_SDK_search_for_LBRR( payloadPtr, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, &nBytesFEC );
	                    Silk_dec_API.SKP_Silk_SDK_search_for_LBRR(payloadPtr, payloadPtr_offset, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, 0, nBytesFEC_ptr);
	                    nBytesFEC = nBytesFEC_ptr[0];
	                    
	                	if( nBytesFEC > 0 ) {
	                        payloadToDec = FECpayload;
	                        nBytes = nBytesFEC;
	                        lost = 0;
	                        break;
	                    }
	                }
//	                payloadPtr += nBytesPerPacket[ i + 1 ];
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
//	                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0, payloadToDec, nBytes, outPtr, &len );
	            	
	                ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode(psDec, DecControl, 0, payloadToDec, 0,  nBytes, outPtr, outPtr_offset, len_ptr);
	                len = len_ptr[0];
//????		                len = 480;
	            	if( ret !=0 ) {
	                    System.out.printf( "\nSKP_Silk_SDK_Decode returned %d", ret );
	                }

	                frames++;
//	                outPtr  += len;
	                outPtr_offset += len;
	                
	                tot_len += len;
	                if( frames > MAX_INPUT_FRAMES ) {
	                    /* Hack for corrupt stream that could generate too many frames */
//	                    outPtr  = out;
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
//	                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 1, payloadToDec, nBytes, outPtr, &len );
	            	ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode(psDec, DecControl, 1, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr);
	            	len = len_ptr[0];
	            	
	            	if( ret != 0) {
	                    System.out.printf( "\nSKP_Silk_Decode returned %d", ret );
	                }
//	                outPtr  += len;
	            	outPtr_offset += len;
	                tot_len += len;
	            }
	        }
	        totPackets++;

	        /* Write output to file */
//	#ifdef _SYSTEM_IS_BIG_ENDIAN   
//djinn TODO:	        
//	        swap_endian( out, tot_len );
	        if(Config._SYSTEM_IS_BIG_ENDIAN)
	        {
	        	swap_endian(out, tot_len);
	        }

//	#endif
//	        fwrite( out, sizeof( short ), tot_len, speechOutFile );
			for( int write=0; write<tot_len; write++)
			{
				try {
					speechOutFile.writeShort(out[write]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			writeNum++;
			System.err.println("writeNum = "+writeNum);
			if(writeNum == 98)
			{
				System.err.println("*************************");
			}

	        /* Update buffer */
	        totBytes = 0;
	        for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
	            totBytes += nBytesPerPacket[ i + 1 ];
	        }
//	        SKP_memmove( payload, &payload[ nBytesPerPacket[ 0 ] ], totBytes * sizeof( SKP_uint8 ) );
	        System.arraycopy(payload, nBytesPerPacket[ 0 ], payload, 0, totBytes);
	        
//	        payloadEnd -= nBytesPerPacket[ 0 ];
	        payloadEnd_offset -= nBytesPerPacket[ 0 ];
	        
//	        SKP_memmove( nBytesPerPacket, &nBytesPerPacket[ 1 ], MAX_LBRR_DELAY * sizeof( short ) );
	        System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0, MAX_LBRR_DELAY);
	        
//	        if( !quiet ) {
	        if(quiet == 0)
	        {
//	            fprintf( stderr, "\rPackets decoded:             %d", totPackets );
	        	System.err.printf("\rPackets decoded:             %d", totPackets);
	        }
	    }
	    System.err.println("writeNum = " + writeNum);
//****************************************************************************************// 
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
//	                    SKP_Silk_SDK_search_for_LBRR( payloadPtr, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, &nBytesFEC );
	                	Silk_dec_API.SKP_Silk_SDK_search_for_LBRR( payloadPtr, payloadPtr_offset, nBytesPerPacket[ i + 1 ], i + 1, FECpayload, 0, nBytesFEC_ptr );
	                	nBytesFEC = nBytesFEC_ptr[0];
	                	
	                    if( nBytesFEC > 0 ) {
	                        payloadToDec = FECpayload;
	                        nBytes = nBytesFEC;
	                        lost = 0;
	                        break;
	                    }
	                }
//	                payloadPtr += nBytesPerPacket[ i + 1 ];
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
//	                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0, payloadToDec, nBytes, outPtr, &len );
	            	ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode( psDec, DecControl, 0, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr );
	            	len = len_ptr[0];
	                if( ret != 0) {
	                    System.out.printf( "\nSKP_Silk_SDK_Decode returned %d", ret );
	                }

	                frames++;
//	                outPtr  += len;
	                outPtr_offset += len;
	                tot_len += len;
	                if( frames > MAX_INPUT_FRAMES ) {
	                    /* Hack for corrupt stream that could generate too many frames */
//	                    outPtr  = out;
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
//	                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 1, payloadToDec, nBytes, outPtr, &len );
	                ret = (short) Silk_dec_API.SKP_Silk_SDK_Decode( psDec, DecControl, 1, payloadToDec, 0, nBytes, outPtr, outPtr_offset, len_ptr );
	                len = len_ptr[0];
	                
	                if( ret != 0) {
	                    System.out.printf( "\nSKP_Silk_Decode returned %d", ret );
	                }
//	                outPtr  += len;
	                outPtr_offset += len;
	                tot_len += len;
	            }
	        }
	        totPackets++;

	        /* Write output to file */
//	#ifdef _SYSTEM_IS_BIG_ENDIAN   
//	        swap_endian( out, tot_len );
//	        if(Config._SYSTEM_IS_BIG_ENDIAN)
//		    {
//	        	short[] nBytes_ptr = new short[1];
//		    	nBytes_ptr[0] = nBytes;
////		    	swap_endian( &nBytes, 1 );
//		    	swap_endian(nBytes_ptr, 1);
//		    	nBytes = nBytes_ptr[0];
//		    }
//	#endif
	        if(Config._SYSTEM_IS_BIG_ENDIAN)
	        {
	        	swap_endian(out, tot_len);
	        }
	        
//	        fwrite( out, sizeof( short ), tot_len, speechOutFile );
			for( int write =0; write<tot_len; write++)
			{
				try {
					speechOutFile.writeShort(out[write]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			writeNum++;
			System.err.println("writeNum = "+writeNum);
			
	        /* Update Buffer */
	        totBytes = 0;
	        for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
	            totBytes += nBytesPerPacket[ i + 1 ];
	        }
//	        SKP_memmove( payload, &payload[ nBytesPerPacket[ 0 ] ], totBytes * sizeof( SKP_uint8 ) );
	        System.arraycopy(payload, nBytesPerPacket[ 0 ], payload, 0, totBytes);
	        
//	        payloadEnd -= nBytesPerPacket[ 0 ];
	        payloadEnd_offset -= nBytesPerPacket[ 0 ];
	        
//	        SKP_memmove( nBytesPerPacket, &nBytesPerPacket[ 1 ], MAX_LBRR_DELAY * sizeof( short ) );
	        System.arraycopy(nBytesPerPacket, 1, nBytesPerPacket, 0, MAX_LBRR_DELAY);
	        
//	        if( !quiet ) {
	        if( quiet == 0 ) {
//	            fprintf( stderr, "\rPackets decoded:              %d", totPackets );
	        	System.err.printf("\rPackets decoded:              %d", totPackets);
	        }
	    }
	    System.err.println("writeNum = "+ writeNum);
//	    if( !quiet ) {
	    if( quiet == 0 ) {
	        System.out.printf( "\nDecoding Finished \n" );
	    }

	    /* Free decoder */
//	    free( psDec );

	    /* Close files */
//	    fclose( speechOutFile );
//	    fclose( bitInFile );

	    try {
			speechOutFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ;
	}

}
