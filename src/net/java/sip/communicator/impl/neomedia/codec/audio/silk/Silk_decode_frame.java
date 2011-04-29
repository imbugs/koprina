/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_decode_frame" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_decode_frame.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * Decode frame
 *
 * @author Jing Dai
 */
public class Silk_decode_frame 
{
	/****************/
	/* Decode frame */
	/****************/
//	SKP_int SKP_Silk_decode_frame(
//		    SKP_Silk_decoder_state          *psDec,             /* I/O  Pointer to Silk decoder state               */
//		    SKP_int16                       pOut[],             /* O    Pointer to output speech frame              */
//		    SKP_int16                       *pN,                /* O    Pointer to size of output frame             */
//		    const SKP_uint8                 pCode[],            /* I    Pointer to payload                          */
//		    const SKP_int                   nBytes,             /* I    Payload length                              */
//		    SKP_int                         action,             /* I    Action from Jitter Buffer                   */
//		    SKP_int                         *decBytes           /* O    Used bytes to decode this frame             */
//		)
	static int SKP_Silk_decode_frame(
	    SKP_Silk_decoder_state        psDec,             /* I/O  Pointer to Silk decoder state               */
	    short[]                       pOut,             /* O    Pointer to output speech frame              */
	    int							  pOut_offset,
	    short[]                       pN,                /* O    Pointer to size of output frame             */
	    byte[]                        pCode,            /* I    Pointer to payload                          */
	    int 						  pCode_offset,
	    final int                     nBytes,             /* I    Payload length                              */
	    int                           action,             /* I    Action from Jitter Buffer                   */
	    int[]                         decBytes           /* O    Used bytes to decode this frame             */
	)
	{
//djinn ???	    SKP_Silk_decoder_control sDecCtrl;
		SKP_Silk_decoder_control sDecCtrl = new SKP_Silk_decoder_control();
	    int         L, fs_Khz_old, LPC_order_old, ret = 0;
	    int[]         Pulses = new int[ Silk_define.MAX_FRAME_LENGTH ];


	    L = psDec.frame_length;
	    sDecCtrl.LTP_scale_Q14 = 0;
	    
	    /* Safety checks */
	    Silk_typedef.SKP_assert( L > 0 && L <= Silk_define.MAX_FRAME_LENGTH );

	    /********************************************/
	    /* Decode Frame if packet is not lost  */
	    /********************************************/
//	    *decBytes = 0;
	    decBytes[0] = 0;
	    if( action == 0 ) {
	        /********************************************/
	        /* Initialize arithmetic coder              */
	        /********************************************/
	        fs_Khz_old    = psDec.fs_kHz;
	        LPC_order_old = psDec.LPC_order;
	        if( psDec.nFramesDecoded == 0 ) {
	            /* Initialize range decoder state */
//	            SKP_Silk_range_dec_init( &psDec.sRC, pCode, nBytes );
	        	Silk_range_coder.SKP_Silk_range_dec_init( psDec.sRC, pCode, pCode_offset, nBytes );
	        }

	        /********************************************/
	        /* Decode parameters and pulse signal       */
	        /********************************************/
//	        SKP_Silk_decode_parameters( psDec, &sDecCtrl, Pulses, 1 );
	        Silk_decode_parameters.SKP_Silk_decode_parameters( psDec, sDecCtrl, Pulses, 1 );

	        if( psDec.sRC.error !=0 ) {
	            psDec.nBytesLeft = 0;

	            action              = 1; /* PLC operation */
	            /* revert fs if changed in decode_parameters */
	            Silk_decoder_set_fs.SKP_Silk_decoder_set_fs( psDec, fs_Khz_old );

	            /* Avoid crashing */
//	            *decBytes = psDec.sRC.bufferLength;
	            decBytes[0] = psDec.sRC.bufferLength;
	            
	            if( psDec.sRC.error == Silk_define.RANGE_CODER_DEC_PAYLOAD_TOO_LONG ) {
	                ret = Silk_errors.SKP_SILK_DEC_PAYLOAD_TOO_LARGE;
	            } else {
	                ret = Silk_errors.SKP_SILK_DEC_PAYLOAD_ERROR;
	            }
	        } else {
//	            *decBytes = psDec.sRC.bufferLength - psDec.nBytesLeft;
	        	decBytes[0] = psDec.sRC.bufferLength - psDec.nBytesLeft;
	            psDec.nFramesDecoded++;
	        
	            /* Update lengths. Sampling frequency could have changed */
	            L = psDec.frame_length;

	            /********************************************************/
	            /* Run inverse NSQ                                      */
	            /********************************************************/
//	            SKP_Silk_decode_core( psDec, &sDecCtrl, pOut, Pulses );
	            Silk_decode_core.SKP_Silk_decode_core( psDec, sDecCtrl, pOut, pOut_offset, Pulses );

	            /********************************************************/
	            /* Update PLC state                                     */
	            /********************************************************/
//	            SKP_Silk_PLC( psDec, &sDecCtrl, pOut, L, action );
	            Silk_PLC.SKP_Silk_PLC( psDec, sDecCtrl, pOut, pOut_offset, L, action );

	            psDec.lossCnt = 0;
	            psDec.prev_sigtype = sDecCtrl.sigtype;

	            /* A frame has been decoded without errors */
	            psDec.first_frame_after_reset = 0;
	        }
	    }
	    /*************************************************************/
	    /* Generate Concealment Frame if packet is lost, or corrupt  */
	    /*************************************************************/
	    if( action == 1 ) {
	        /* Handle packet loss by extrapolation */
//	        SKP_Silk_PLC( psDec, &sDecCtrl, pOut, L, action );
	    	Silk_PLC.SKP_Silk_PLC( psDec, sDecCtrl, pOut, pOut_offset, L, action );
	        psDec.lossCnt++;
	    
	    }

	    /*************************/
	    /* Update output buffer. */
	    /*************************/
//djinn ??	    SKP_memcpy( psDec.outBuf, pOut, L * sizeof( short ) );
	    System.arraycopy(pOut, pOut_offset+0, psDec.outBuf, 0, L );

	    /****************************************************************/
	    /* Ensure smooth connection of extrapolated and good frames     */
	    /****************************************************************/
//	    SKP_Silk_PLC_glue_frames( psDec, &sDecCtrl, pOut, L );
	    Silk_PLC.SKP_Silk_PLC_glue_frames( psDec, sDecCtrl, pOut, pOut_offset, L );

	    /************************************************/
	    /* Comfort noise generation / estimation        */
	    /************************************************/
//	    SKP_Silk_CNG( psDec, &sDecCtrl, pOut , L );
	    Silk_CNG.SKP_Silk_CNG( psDec, sDecCtrl, pOut , pOut_offset, L );

	    /********************************************/
	    /* HP filter output                            */
	    /********************************************/
	    Silk_typedef.SKP_assert( ( ( psDec.fs_kHz == 12 ) && ( L % 3 ) == 0 ) || 
	                ( ( psDec.fs_kHz != 12 ) && ( L % 2 ) == 0 ) );
//	    SKP_Silk_biquad( pOut, psDec.HP_B, psDec.HP_A, psDec.HPState, pOut, L );
	    Silk_biquad.SKP_Silk_biquad( pOut, pOut_offset, psDec.HP_B, psDec.HP_A, psDec.HPState, pOut, pOut_offset, L );

	    /********************************************/
	    /* set output frame length                    */
	    /********************************************/
//	    *pN = ( short )L;
	    pN[0] = (short)L;

	    /* Update some decoder state variables */
	    psDec.lagPrev = sDecCtrl.pitchL[ Silk_define.NB_SUBFR - 1 ];

	    return ret;
	}

}
