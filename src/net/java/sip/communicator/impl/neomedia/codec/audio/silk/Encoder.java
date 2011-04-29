/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Encoder" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/test/Encoder.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.io.*;

/**
 * @author
 *
 */
class Encoder_constants
{
	/* Define codec specific settings */
	static final int MAX_BYTES_PER_FRAME =    250; // Equals peak bitrate of 100 kbps 
	static final int MAX_INPUT_FRAMES =       5;
	static final int MAX_LBRR_DELAY =         2;
	static final int MAX_FRAME_LENGTH =       480;
	static final int FRAME_LENGTH_MS =        20;
	static final int MAX_API_FS_KHZ =         48;	
}

public class Encoder 
	extends Encoder_constants
{
	/* Function to convert a little endian int16 to a */
	/* big endian int16 or vica verca                 */
	static void swap_endian
	(
	    short[]     vec,                /*  I/O array of */
	    int         len                 /*  I   length   */
	)
	{
	    int i;
	    short tmp;

	    for( i = 0; i < len; i++ )
	    {
	        tmp = vec[ i ];
	        vec[ i ] = (short)( ((tmp<<8)&0xFF00) | ((tmp>>>8)&0x00FF) );
	    }
	}
	
	static void byteToShortArray(byte[] byteArray, int byteArray_offset, short[] shortArray, int shortArray_offset, int out_len)
	{
		int i;
		int tmp;
		for(i=0; i<out_len; i++)
		{
			tmp = (byteArray[byteArray_offset+2*i]<<8) & 0x0000FF00;
			tmp |= byteArray[byteArray_offset+2*i+1] & 0x000000FF;
			shortArray[shortArray_offset+i] = (short)tmp;
		}
	}

	static void print_usage() 
	{
	    System.out.printf( "\nusage: Encoder in.pcm out.bit [settings]\n" );
	    System.out.printf( "\nin.pcm               : Speech input to encoder" );
	    System.out.printf( "\nout.bit              : Bitstream output from encoder" );
	    System.out.printf( "\n   settings:" );
	    System.out.printf( "\n-Fs_API <Hz>         : API sampling rate in Hz, default: 24000" );
	    System.out.printf( "\n-Fs_maxInternal <Hz> : Maximum internal sampling rate in Hz, default: 24000" ); 
	    System.out.printf( "\n-packetlength <ms>   : Packet interval in ms, default: 20" );
	    System.out.printf( "\n-rate <bps>          : Target bitrate; default: 25000" );
	    System.out.printf( "\n-loss <perc>         : Uplink loss estimate, in percent (0-100); default: 0" );
	    System.out.printf( "\n-inbandFEC <flag>    : Enable inband FEC usage (0/1); default: 0" );
	    System.out.printf( "\n-complexity <comp>   : Set complexity, 0: low, 1: medium, 2: high; default: 2" );
	    System.out.printf( "\n-DTX <flag>          : Enable DTX (0/1); default: 0" );
	    System.out.printf( "\n-quiet               : Print only some basic values" );
	    System.out.printf( "\n");
	}
	
	public static void main( String[] argv )
	{
	    int    counter;
	    int k, args, totPackets, totActPackets, ret;
	    short[] nBytes = new short[1];
	    double    sumBytes, sumActBytes, avg_rate, act_rate, nrg;
//	    SKP_uint8 payload[ MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES ];
	    byte[] payload = new byte[ MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES ];
	    short[] in = new short[ FRAME_LENGTH_MS * MAX_API_FS_KHZ * MAX_INPUT_FRAMES ];
	    byte[] in_tmp = new byte[ 2*FRAME_LENGTH_MS * MAX_API_FS_KHZ * MAX_INPUT_FRAMES ];
//	    char      speechInFileName[ 150 ], bitOutFileName[ 150 ];
	    String speechInFileName, bitOutFileName;
//	    FILE      *bitOutFile, *speechInFile;
	    FileInputStream speechInFile;
	    DataInputStream speechInData;
	    FileOutputStream bitOutFile;
	    DataOutputStream bitOutData;
	    int[] encSizeBytes = new int[1];
//	    void      *psEnc;
	    SKP_Silk_encoder_state psEnc;
//	#ifdef _SYSTEM_IS_BIG_ENDIAN
	    short[] nBytes_LE = new short[1];
//	#endif

	    /* default settings */
	    int API_fs_Hz = 24000;
		int max_internal_fs_Hz = 0;
		int targetRate_bps = 25000;
		int packetSize_ms = 20;
		int frameSizeReadFromFile_ms = 20;
		int packetLoss_perc = 0, complexity_mode = 2, smplsSinceLastPacket;
		int INBandFEC_enabled = 0, DTX_enabled = 0, quiet = 0;
	    SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder
	        
	    if( argv.length < 2 ) 
	    {
	        print_usage();
	        System.exit( 0 );
	    } 
	    
	    /* get arguments */
	    args = 0;
//	    strcpy( speechInFileName, argv[ args ] );
	    speechInFileName = argv[ args ];
	    args++;
//	    strcpy( bitOutFileName,   argv[ args ] );
	    bitOutFileName = argv[ args ];
	    args++;
	    while( args < argv.length )
	    {
	        if( argv[ args ].compareTo("-Fs_API") == 0 ) 
	        {
//	            sscanf( argv[ args + 1 ], "%d", &API_fs_Hz );
	        	API_fs_Hz = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-Fs_maxInternal") == 0 )
	        {
//	            sscanf( argv[ args + 1 ], "%d", &max_internal_fs_Hz );
	        	max_internal_fs_Hz = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-packetlength") == 0 ) 
	        {
//	            sscanf( argv[ args + 1 ], "%d", &packetSize_ms );
	        	packetSize_ms = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        }
	        else if( argv[ args ].compareTo("-rate") == 0 ) 
	        {
//	            sscanf( argv[ args + 1 ], "%d", &targetRate_bps );
	            targetRate_bps = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-loss") == 0 ) 
	        {
//	            sscanf( argv[ args + 1 ], "%d", &packetLoss_perc );
	            packetLoss_perc = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-complexity") == 0 )
	        {
//	            sscanf( argv[ args + 1 ], "%d", &complexity_mode );
	        	complexity_mode = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        }
	        else if( argv[ args ].compareTo("-inbandFEC") == 0 ) 
	        {
//	            sscanf( argv[ args + 1 ], "%d", &INBandFEC_enabled );
	            INBandFEC_enabled = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-DTX") == 0 )
	        {
//	            sscanf( argv[ args + 1 ], "%d", &DTX_enabled );
	        	DTX_enabled = Integer.parseInt(argv[ args + 1 ]);
	            args += 2;
	        } 
	        else if( argv[ args ].compareTo("-quiet") == 0 ) 
	        {
	            quiet = 1;
	            args++;
	        } 
	        else 
	        {
	        	System.out.printf( "Error: unrecognized setting: %s\n\n", argv[ args ] );
	            print_usage();
	            System.exit( 0 );
	        }
	    }

	    /* If no max internal set set to API fs */
	    if( max_internal_fs_Hz == 0 ) 
	    {
	        max_internal_fs_Hz = API_fs_Hz;
	    }

	    /* Print options */
	    if( quiet==0 ) 
	    {
	    	System.out.printf("******************* Silk Encoder v "+ Silk_dec_API.SKP_Silk_SDK_get_version() +" ****************\n");
//	    	System.out.printf("******************* Compiled for %d bit cpu ********* \n", (int)sizeof(void*) * 8 );
	    	System.out.printf( "Input:                          %s\n",     speechInFileName );
	    	System.out.printf( "Output:                         %s\n",     bitOutFileName );
	    	System.out.printf( "API sampling rate:              %d Hz\n",  API_fs_Hz );
	    	System.out.printf( "Maximum internal sampling rate: %d Hz\n",  max_internal_fs_Hz );
	    	System.out.printf( "Packet interval:                %d ms\n",  packetSize_ms );
	    	System.out.printf( "Inband FEC used:                %d\n",     INBandFEC_enabled );
	    	System.out.printf( "DTX used:                       %d\n",     DTX_enabled );
	    	System.out.printf( "Complexity:                     %d\n",     complexity_mode );
	    	System.out.printf( "Target bitrate:                 %d bps\n", targetRate_bps );
	    }

	    /* Open files */
//	    speechInFile = fopen( speechInFileName, "rb" );
	    try
	    {
	    	speechInFile = new FileInputStream(speechInFileName);
	    	speechInData = new DataInputStream(speechInFile);
	    }
//	    if( speechInFile == null )
	    catch(IOException e)
	    {
	    	System.out.printf( "Error: could not open input file %s\n", speechInFileName );
	    	System.exit( 0 );
	    }
//	    bitOutFile = fopen( bitOutFileName, "wb" );
	    try
	    {
	    	bitOutFile = new FileOutputStream(bitOutFileName);
	    	bitOutData = new DataOutputStream(bitOutFile);
	    }
//	    if( bitOutFile == null )
	    catch(IOException e)
	    {
	    	System.out.printf( "Error: could not open output file %s\n", bitOutFileName );
	    	System.exit( 0 );
	    }

	    /* Create Encoder */
??	    ret = Silk_enc_API.SKP_Silk_SDK_Get_Encoder_Size( encSizeBytes );
	    if( ret!=0 ) 
	    {
	    	System.out.printf( "\nSKP_Silk_create_encoder returned %d", ret );
	    }

//	    psEnc = malloc( encSizeBytes );
	    psEnc = new SKP_Silk_encoder_state();

	    /* Reset Encoder */
	    ret = Silk_enc_API.SKP_Silk_SDK_InitEncoder( psEnc, &encControl );
	    if( ret!=0 ) 
	    {
	    	System.out.printf( "\nSKP_Silk_reset_encoder returned %d", ret );
	    }
	    
	    /* Set Encoder parameters */
	    encControl.API_sampleRate        = API_fs_Hz;
	    encControl.maxInternalSampleRate = max_internal_fs_Hz;
	    encControl.packetSize            = ( packetSize_ms * API_fs_Hz ) / 1000;
	    encControl.packetLossPercentage  = packetLoss_perc;
	    encControl.useInBandFEC          = INBandFEC_enabled;
	    encControl.useDTX                = DTX_enabled;
	    encControl.complexity            = complexity_mode;
	    encControl.bitRate               = ( targetRate_bps > 0 ? targetRate_bps : 0 );

	    if( API_fs_Hz > MAX_API_FS_KHZ * 1000 || API_fs_Hz < 0 ) 
	    {
	    	System.out.printf( "\nError: API sampling rate = %d out of range, valid range 8000 - 48000 \n \n", API_fs_Hz );
	    	System.exit( 0 );
	    }

	    totPackets           = 0;
	    totActPackets        = 0;
	    smplsSinceLastPacket = 0;
	    sumBytes             = 0.0;
	    sumActBytes          = 0.0;
	    
	    while( true )
	    {
	        /* Read input from file */
//	        counter = fread( in, sizeof( SKP_int16 ), ( frameSizeReadFromFile_ms * API_fs_Hz ) / 1000, speechInFile );
	    	counter = speechInData.read(in_tmp, 0, 2*( frameSizeReadFromFile_ms * API_fs_Hz ) / 1000) >> 1;
	    	byteToShortArray(in_tmp, 0, in, 0, counter);
	    	
//	#ifdef _SYSTEM_IS_BIG_ENDIAN
	        if(Config._SYSTEM_IS_BIG_ENDIAN)
	        swap_endian( in, counter );
//	#endif
	        if( (int)counter < ( ( frameSizeReadFromFile_ms * API_fs_Hz ) / 1000 ) ) 
	        {
	            break;
	        }

	        /* max payload size */
	        nBytes[0] = MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES;

	        /* Silk Encoder */
	        ret = Silk_enc_API.SKP_Silk_SDK_Encode( psEnc, &encControl, in, (SKP_int16)counter, payload, &nBytes );
	        if( ret!=0 )
	        {
	        	System.out.printf( "\nSKP_Silk_Encode returned %d", ret );
	            break;
	        }

	        /* Get packet size */
	        packetSize_ms = ( int )( ( 1000 * ( int )encControl.packetSize ) / encControl.API_sampleRate );

	        smplsSinceLastPacket += ( int )counter;
	        
	        if( ( ( 1000 * smplsSinceLastPacket ) / API_fs_Hz ) == packetSize_ms )
	        {
	            /* Sends a dummy zero size packet in case of DTX period  */
	            /* to make it work with the decoder test program.        */
	            /* In practice should be handled by RTP sequence numbers */
	            totPackets++;
	            sumBytes  += nBytes[0];
	            nrg = 0.0;
	            for( k = 0; k < ( int )counter; k++ ) {
	                nrg += in[ k ] * (double)in[ k ];
	            }
	            if( ( nrg / ( int )counter ) > 1e3 ) {
	                sumActBytes += nBytes[0];
	                totActPackets++;
	            }

	            /* Write payload size */
//	#ifdef _SYSTEM_IS_BIG_ENDIAN
	            if(Config._SYSTEM_IS_BIG_ENDIAN)
	            {
	            nBytes_LE[0] = nBytes[0];
	            swap_endian( nBytes_LE, 1 );
//	            fwrite( &nBytes_LE, sizeof( SKP_int16 ), 1, bitOutFile );
	            bitOutData.writeShort(nBytes_LE[0]);
	            }
//	#else
	            else
//	            fwrite( &nBytes, sizeof( SKP_int16 ), 1, bitOutFile );
	            bitOutData.writeShort(nBytes[0]);
//	#endif

	            /* Write payload */
//	            fwrite( payload, sizeof( SKP_uint8 ), nBytes, bitOutFile );
	            bitOutData.write(payload, 0, nBytes[0]);
	        
	            if( quiet==0 )
	            {
	            	System.err.printf( "\nPackets encoded:              "+ totPackets );
	            }
	            smplsSinceLastPacket = 0;
	        }
	    }

	    /* Write dummy because it can not end with 0 bytes */
	    nBytes[0] = -1;

	    /* Write payload size */
//	    fwrite( &nBytes, sizeof( SKP_int16 ), 1, bitOutFile );
	    bitOutData.writeShort(nBytes[0]);

	    /* Free Encoder */
//	    free( psEnc );

//	    fclose( speechInFile );
	    speechInFile.close();
	    speechInData.close();
//	    fclose( bitOutFile   );
	    bitOutFile.close();
	    bitOutData.close();

	    avg_rate  = 8.0 / packetSize_ms * sumBytes       / totPackets;
	    act_rate  = 8.0 / packetSize_ms * sumActBytes    / totActPackets;
	    if( quiet==0 ) 
	    {
	    	System.out.printf( "\nAverage bitrate:             %.3f kbps", avg_rate  );
	    	System.out.printf( "\nActive bitrate:              %.3f kbps", act_rate  );
	    	System.out.printf( "\n\n" );
	    }
	    else 
	    {
	        /* print average and active bitrates */
	    	System.out.printf( "%.3f %.3f \n", avg_rate, act_rate );
	    }
//	    return 0;
	}
}


