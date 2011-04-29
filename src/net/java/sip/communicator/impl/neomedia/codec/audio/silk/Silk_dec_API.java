/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_dec_API" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_dec_API.h
 */

package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.util.Arrays;

public class Silk_dec_API 
{
	/*********************/
	/* Decoder functions */
	/*********************/
//djinn ?
	static int SKP_Silk_SDK_Get_Decoder_Size( int[] decSizeBytes ) 
	{
	    int ret = 0;

//djinn TODO:???	    decSizeBytes = sizeof( SKP_Silk_decoder_state );
//	    decSizeBytes[0] = ??
	    System.err.println("Silk_dec_API.SKP_Silk_SDK_Get_Decoder_Size is usless??");
	    return ret;
	}

	/* Reset decoder state */
//	int SKP_Silk_SDK_InitDecoder(
//	    void* decState                                      /* I/O: State                                          */
//	)
	static int SKP_Silk_SDK_InitDecoder(
		    Object decState                                      /* I/O: State                                          */
	)
	{
	    int ret = 0;
//	    SKP_Silk_decoder_state *struc;
	    SKP_Silk_decoder_state struc;
//	    struc = (SKP_Silk_decoder_state *)decState;
	    struc = (SKP_Silk_decoder_state )decState;
	    ret  = Silk_create_init_destroy.SKP_Silk_init_decoder( struc );

	    return ret;
	}

	/* Decode a frame */
//	int SKP_Silk_SDK_Decode(
//	    void*                               decState,       /* I/O: State                                           */
//	    SKP_SILK_SDK_DecControlStruct*      decControl,     /* I/O: Control structure                               */
//	    int                             lostFlag,       /* I:   0: no loss, 1 loss                              */
//	    const SKP_uint8                     *inData,        /* I:   Encoded input vector                            */
//	    const int                       nBytesIn,       /* I:   Number of input Bytes                           */
//	    SKP_int16                           *samplesOut,    /* O:   Decoded output speech vector                    */
//	    SKP_int16                           *nSamplesOut    /* I/O: Number of samples (vector/decoded)              */
//	)
	static int SKP_Silk_SDK_Decode(
		    Object                               decState,       /* I/O: State                                           */
		    SKP_SILK_SDK_DecControlStruct        decControl,     /* I/O: Control structure                               */
		    int                                  lostFlag,       /* I:   0: no loss, 1 loss                              */
		    byte[]                               inData,        /* I:   Encoded input vector                            */
		    int 								 inData_offset,
		    final int                            nBytesIn,       /* I:   Number of input Bytes                           */
		    short[]                              samplesOut,    /* O:   Decoded output speech vector                    */
		    int									 samplesOut_offset,
		    short[]                              nSamplesOut    /* I/O: Number of samples (vector/decoded)              */
	)
	{
	    int ret = 0, used_bytes, prev_fs_kHz;
//	    SKP_Silk_decoder_state *psDec;
	    SKP_Silk_decoder_state psDec;
	    
//	    psDec = (SKP_Silk_decoder_state *)decState;
	    psDec = (SKP_Silk_decoder_state )decState;

	    /**********************************/
	    /* Test if first frame in payload */
	    /**********************************/
	    if( psDec.moreInternalDecoderFrames == 0 ) {
	        /* First Frame in Payload */
	        psDec.nFramesDecoded = 0;  /* Used to count frames in packet */
	    }

	    if( psDec.moreInternalDecoderFrames == 0 &&    /* First frame in packet    */
	        lostFlag == 0 &&                            /* Not packet loss          */
	        nBytesIn > Silk_define.MAX_ARITHM_BYTES ) {             /* Too long payload         */
	            /* Avoid trying to decode a too large packet */
	            lostFlag = 1;
	            ret = Silk_errors.SKP_SILK_DEC_PAYLOAD_TOO_LARGE;
	    }
	            
	    /* Save previous sample frequency */
	    prev_fs_kHz = psDec.fs_kHz;
	    
	    /* Call decoder for one frame */
//	    ret += SKP_Silk_decode_frame( psDec, samplesOut, nSamplesOut, inData, nBytesIn, 
//	            lostFlag, &used_bytes );
	    int[] used_bytes_ptr =new int[1];
	    ret += Silk_decode_frame.SKP_Silk_decode_frame( psDec, samplesOut, samplesOut_offset, nSamplesOut, inData, inData_offset,
	    		nBytesIn, lostFlag, used_bytes_ptr );
	    used_bytes = used_bytes_ptr[0];
	    
	    if( used_bytes !=0) /* Only Call if not a packet loss */
	    { 
	        if( psDec.nBytesLeft > 0 && psDec.FrameTermination == Silk_define.SKP_SILK_MORE_FRAMES && psDec.nFramesDecoded < 5 ) {
	            /* We have more frames in the Payload */
	            psDec.moreInternalDecoderFrames = 1;
	        } else {
	            /* Last frame in Payload */
	            psDec.moreInternalDecoderFrames = 0;
	            psDec.nFramesInPacket = psDec.nFramesDecoded;
	        
	            /* Track inband FEC usage */
	            if( psDec.vadFlag == Silk_define.VOICE_ACTIVITY ) {
	                if( psDec.FrameTermination == Silk_define.SKP_SILK_LAST_FRAME ) {
	                    psDec.no_FEC_counter++;
	                    if( psDec.no_FEC_counter > Silk_define.NO_LBRR_THRES ) {
	                        psDec.inband_FEC_offset = 0;
	                    }
	                } else if( psDec.FrameTermination == Silk_define.SKP_SILK_LBRR_VER1 ) {
	                    psDec.inband_FEC_offset = 1; /* FEC info with 1 packet delay */
	                    psDec.no_FEC_counter    = 0;
	                } else if( psDec.FrameTermination == Silk_define.SKP_SILK_LBRR_VER2 ) {
	                    psDec.inband_FEC_offset = 2; /* FEC info with 2 packets delay */
	                    psDec.no_FEC_counter    = 0;
	                }
	            }
	        }
	    }

	    if( Silk_define.MAX_API_FS_KHZ * 1000 < decControl.API_sampleRate ||
	        8000       > decControl.API_sampleRate ) {
	        ret = Silk_errors.SKP_SILK_DEC_INVALID_SAMPLING_FREQUENCY;
	        return( ret );
	    }

	    /* Resample if needed */
	    if( psDec.fs_kHz * 1000 != decControl.API_sampleRate ) { 
//	        SKP_int16 samplesOut_tmp[ MAX_API_FS_KHZ * FRAME_LENGTH_MS ];
	    	short[] samplesOut_tmp = new short[Silk_define.MAX_API_FS_KHZ * Silk_define.FRAME_LENGTH_MS];
	        Silk_typedef.SKP_assert( psDec.fs_kHz <= Silk_define.MAX_API_FS_KHZ );

	        /* Copy to a tmp buffer as the resampling writes to samplesOut */
//djinn ??	        SKP_memcpy( samplesOut_tmp, samplesOut, *nSamplesOut * sizeof( SKP_int16 ) );
	        System.arraycopy(samplesOut, samplesOut_offset+0, samplesOut_tmp, 0, nSamplesOut[0]);
	        /* (Re-)initialize resampler state when switching internal sampling frequency */
	        if( prev_fs_kHz != psDec.fs_kHz || psDec.prev_API_sampleRate != decControl.API_sampleRate ) {
//	            ret = SKP_Silk_resampler_init( &psDec.resampler_state, SKP_SMULBB( psDec.fs_kHz, 1000 ), decControl.API_sampleRate );
	        	ret = Silk_resampler.SKP_Silk_resampler_init( psDec.resampler_state, psDec.fs_kHz*1000, decControl.API_sampleRate );
	        }

	        /* Resample the output to API_sampleRate */
//	        ret += SKP_Silk_resampler( &psDec.resampler_state, samplesOut, samplesOut_tmp, *nSamplesOut );
	        ret += Silk_resampler.SKP_Silk_resampler( psDec.resampler_state, samplesOut, samplesOut_offset, samplesOut_tmp, 0, nSamplesOut[0] );

	        /* Update the number of output samples */
//	        *nSamplesOut = SKP_DIV32( ( int )*nSamplesOut * decControl.API_sampleRate, psDec.fs_kHz * 1000 );
	        nSamplesOut[0] = (short)((int)(nSamplesOut[0] * decControl.API_sampleRate) / (psDec.fs_kHz * 1000));
	    }

	    psDec.prev_API_sampleRate = decControl.API_sampleRate;

	    /* Copy all parameters that are needed out of internal structure to the control stucture */
	    decControl.frameSize                 = ( int )psDec.frame_length;
	    decControl.framesPerPacket           = ( int )psDec.nFramesInPacket;
	    decControl.inBandFECOffset           = ( int )psDec.inband_FEC_offset;
	    decControl.moreInternalDecoderFrames = ( int )psDec.moreInternalDecoderFrames;

	    return ret;
	}

	/* Function to find LBRR information in a packet */
//	void SKP_Silk_SDK_search_for_LBRR(
//	    const SKP_uint8                     *inData,        /* I:   Encoded input vector                            */
//	    const SKP_int16                     nBytesIn,       /* I:   Number of input Bytes                           */
//	    int                             lost_offset,    /* I:   Offset from lost packet                         */
//	    SKP_uint8                           *LBRRData,      /* O:   LBRR payload                                    */
//	    SKP_int16                           *nLBRRBytes     /* O:   Number of LBRR Bytes                            */
//	)
	static void SKP_Silk_SDK_search_for_LBRR(
		    byte[]                          inData,        /* I:   Encoded input vector                            */
		    int 							inData_offset,
		    final short                     nBytesIn,       /* I:   Number of input Bytes                           */
		    int                             lost_offset,    /* I:   Offset from lost packet                         */
		    byte[]                          LBRRData,      /* O:   LBRR payload                                    */
		    int								LBRRData_offset,
		    short[]                         nLBRRBytes     /* O:   Number of LBRR Bytes                            */
	)
	{
//	    SKP_Silk_decoder_state   sDec; // Local decoder state to avoid interfering with running decoder */
//djinn ??? the local sDec must be initialized in Java???
		SKP_Silk_decoder_state   sDec = new SKP_Silk_decoder_state(); // Local decoder state to avoid interfering with running decoder */
//	    SKP_Silk_decoder_control sDecCtrl;
//djinn ???
		SKP_Silk_decoder_control sDecCtrl = new SKP_Silk_decoder_control();
	    int[] TempQ = new int[ Silk_define.MAX_FRAME_LENGTH ];

	    if( lost_offset < 1 || lost_offset > Silk_define.MAX_LBRR_DELAY ) {
	        /* No useful FEC in this packet */
//	        *nLBRRBytes = 0;
	    	nLBRRBytes[0] = 0;
	        return;
	    }

	    sDec.nFramesDecoded = 0;
	    sDec.fs_kHz         = 0; /* Force update parameters LPC_order etc */
//djinn ?	    SKP_memset( sDec.prevNLSF_Q15, 0, MAX_LPC_ORDER * sizeof( int ) );
	    Arrays.fill(sDec.prevNLSF_Q15, 0, Silk_define.MAX_LPC_ORDER, 0);
	    
	    for(int i=0; i<Silk_define.MAX_LPC_ORDER; i++)
	    	sDec.prevNLSF_Q15[i] = 0;
	    
//	    SKP_Silk_range_dec_init( &sDec.sRC, inData, ( int )nBytesIn );
	    Silk_range_coder.SKP_Silk_range_dec_init( sDec.sRC, inData, inData_offset, ( int )nBytesIn );
	    
	    while(true) {
//	        SKP_Silk_decode_parameters( &sDec, &sDecCtrl, TempQ, 0 );
	    	Silk_decode_parameters.SKP_Silk_decode_parameters(sDec, sDecCtrl, TempQ, 0);
	        if( sDec.sRC.error!=0 ) {
	            /* Corrupt stream */
//	            *nLBRRBytes = 0;
	        	nLBRRBytes[0] = 0;
	            return;
	        }
//djinn ???	        };//djinn TODO: ; ??? 
	        if( (( sDec.FrameTermination - 1 ) & lost_offset)!=0 && sDec.FrameTermination > 0 && sDec.nBytesLeft >= 0 ) {
	            /* The wanted FEC is present in the packet */
//	            *nLBRRBytes = sDec.nBytesLeft;
	            nLBRRBytes[0] = (short)sDec.nBytesLeft;
//	            SKP_memcpy( LBRRData, &inData[ nBytesIn - sDec.nBytesLeft ], sDec.nBytesLeft * sizeof( SKP_uint8 ) );
	            System.arraycopy(inData, inData_offset+nBytesIn - sDec.nBytesLeft, LBRRData, LBRRData_offset+0, sDec.nBytesLeft);
	            break;
	        }
	        if( sDec.nBytesLeft > 0 && sDec.FrameTermination == Silk_define.SKP_SILK_MORE_FRAMES ) {
	            sDec.nFramesDecoded++;
	        } else {
	            LBRRData = null;
//	            *nLBRRBytes = 0;
	            nLBRRBytes[0] = 0;
	            break;
	        }
	    }
	}

	/* Getting type of content for a packet */
//	void SKP_Silk_SDK_get_TOC(
//	    const SKP_uint8                     *inData,        /* I:   Encoded input vector                            */
//	    const SKP_int16                     nBytesIn,       /* I:   Number of input bytes                           */
//	    SKP_Silk_TOC_struct                 *Silk_TOC       /* O:   Type of content                                 */
//	)
	static void SKP_Silk_SDK_get_TOC(
		     byte[]                        inData,        /* I:   Encoded input vector                            */
		     final short                   nBytesIn,       /* I:   Number of input bytes                           */
		    SKP_Silk_TOC_struct            Silk_TOC       /* O:   Type of content                                 */
		)
	{
		
//	    SKP_Silk_decoder_state      sDec; // Local Decoder state to avoid interfering with running decoder */
//djinn ??? the local sDec must be initialized in Java???
		SKP_Silk_decoder_state      sDec = new SKP_Silk_decoder_state();
//		SKP_Silk_decoder_control    sDecCtrl;
//djinn ???
		SKP_Silk_decoder_control    sDecCtrl = new SKP_Silk_decoder_control();
		int[] TempQ = new int[ Silk_define.MAX_FRAME_LENGTH ];

	    sDec.nFramesDecoded = 0;
	    sDec.fs_kHz         = 0; /* Force update parameters LPC_order etc */
//	    SKP_Silk_range_dec_init( &sDec.sRC, inData, ( int )nBytesIn );
	    Silk_range_coder.SKP_Silk_range_dec_init( sDec.sRC, inData, 0,  ( int )nBytesIn );

	    Silk_TOC.corrupt = 0;
	    while( true ) {
//	        SKP_Silk_decode_parameters( &sDec, &sDecCtrl, TempQ, 0 );
	    	Silk_decode_parameters.SKP_Silk_decode_parameters( sDec, sDecCtrl, TempQ, 0 );
	        
	        Silk_TOC.vadFlags[     sDec.nFramesDecoded ] = sDec.vadFlag;
	        Silk_TOC.sigtypeFlags[ sDec.nFramesDecoded ] = sDecCtrl.sigtype;
	    
	        if( sDec.sRC.error != 0) {
	            /* Corrupt stream */
	            Silk_TOC.corrupt = 1;
	            break;
	        }
//djinn ???	        };//djinn TODO: ; ???
	    
	        if( sDec.nBytesLeft > 0 && sDec.FrameTermination == Silk_define.SKP_SILK_MORE_FRAMES ) {
	            sDec.nFramesDecoded++;
	        } else {
	            break;
	        }
	    }
	    if( Silk_TOC.corrupt !=0 || sDec.FrameTermination == Silk_define.SKP_SILK_MORE_FRAMES || 
	        sDec.nFramesInPacket > Silk_SDK_API.SILK_MAX_FRAMES_PER_PACKET ) {
	        /* Corrupt packet */
//	        SKP_memset( Silk_TOC, 0, sizeof( SKP_Silk_TOC_struct ) );
//djinn TODO: ???	    	
	    	{
	    		Silk_TOC.corrupt = 0;
	    		Silk_TOC.framesInPacket = 0;
	    		Silk_TOC.fs_kHz = 0;
	    		Silk_TOC.inbandLBRR = 0;
	    		
	    		for(int i=0; i<Silk_TOC.vadFlags.length; i++)
	    			Silk_TOC.vadFlags[i] = 0;
	    		for(int i=0; i<Silk_TOC.sigtypeFlags.length; i++)
	    			Silk_TOC.sigtypeFlags[i] = 0;
	    	}
	    	
	        Silk_TOC.corrupt = 1;
	    } else {
	        Silk_TOC.framesInPacket = sDec.nFramesDecoded + 1;
	        Silk_TOC.fs_kHz         = sDec.fs_kHz;
	        if( sDec.FrameTermination == Silk_define.SKP_SILK_LAST_FRAME ) {
	            Silk_TOC.inbandLBRR = sDec.FrameTermination;
	        } else {
	            Silk_TOC.inbandLBRR = sDec.FrameTermination - 1;
	        }
	    }
	}

	/**************************/
	/* Get the version number */
	/**************************/
	/* Return a pointer to string specifying the version */ 
//	const char *SKP_Silk_SDK_get_version()
//djinn ?	
	static String SKP_Silk_SDK_get_version()
	{
//	    static const char version[] = "1.0.6";
		String version = "1.0.6";
	    return version;
	}

}
