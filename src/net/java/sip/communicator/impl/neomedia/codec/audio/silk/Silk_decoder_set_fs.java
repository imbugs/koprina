/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_decoder_set_fs" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_decoder_set_fs.c
 */

package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.util.Arrays;

/**
 * 
 * @author 
 *
 */
public class Silk_decoder_set_fs
{

	/* Set decoder sampling rate */
	static void SKP_Silk_decoder_set_fs(
	    SKP_Silk_decoder_state          psDec,             /* I/O  Decoder state pointer                       */
	    int                             fs_kHz              /* I    Sampling frequency (kHz)                    */
	)
	{
	    if( psDec.fs_kHz != fs_kHz ) {
	        psDec.fs_kHz  = fs_kHz;
//	        psDec.frame_length = SKP_SMULBB( FRAME_LENGTH_MS, fs_kHz );
	        psDec.frame_length = Silk_define.FRAME_LENGTH_MS*fs_kHz;
	        
//	        psDec.subfr_length = SKP_SMULBB( FRAME_LENGTH_MS / NB_SUBFR, fs_kHz );
	        psDec.subfr_length = (Silk_define.FRAME_LENGTH_MS/Silk_define.NB_SUBFR)*fs_kHz;
	        
	        if( psDec.fs_kHz == 8 ) {
	            psDec.LPC_order = Silk_define.MIN_LPC_ORDER;
//	            psDec.psNLSF_CB[ 0 ] = &SKP_Silk_NLSF_CB0_10;
	            psDec.psNLSF_CB[0]   = Silk_tables_NLSF_CB0_10.SKP_Silk_NLSF_CB0_10;
//	            psDec.psNLSF_CB[ 1 ] = &SKP_Silk_NLSF_CB1_10;
	            psDec.psNLSF_CB[1]   = Silk_tables_NLSF_CB1_10.SKP_Silk_NLSF_CB1_10;
	        } else {
	            psDec.LPC_order = Silk_define.MAX_LPC_ORDER;
//	            psDec.psNLSF_CB[ 0 ] = &SKP_Silk_NLSF_CB0_16;
	            psDec.psNLSF_CB[0]   = Silk_tables_NLSF_CB0_16.SKP_Silk_NLSF_CB0_16;
//	            psDec.psNLSF_CB[ 1 ] = &SKP_Silk_NLSF_CB1_16;
	            psDec.psNLSF_CB[1]   = Silk_tables_NLSF_CB1_16.SKP_Silk_NLSF_CB1_16;
	        }
	        /* Reset part of the decoder state */
//	        SKP_memset( psDec.sLPC_Q14,     0, MAX_LPC_ORDER      * sizeof( SKP_int32 ) );
	        Arrays.fill(psDec.sLPC_Q14, 0, Silk_define.MAX_LPC_ORDER, 0);
	        
//	        SKP_memset( psDec.outBuf,       0, MAX_FRAME_LENGTH   * sizeof( SKP_int16 ) );
	        Arrays.fill(psDec.outBuf, 0, Silk_define.MAX_FRAME_LENGTH, (short)0);
	        
//	        SKP_memset( psDec.prevNLSF_Q15, 0, MAX_LPC_ORDER      * sizeof( SKP_int )   );
	        Arrays.fill(psDec.prevNLSF_Q15, 0, Silk_define.MAX_LPC_ORDER, 0);
	        

	        psDec.sLTP_buf_idx            = 0;
	        psDec.lagPrev                 = 100;
	        psDec.LastGainIndex           = 1;
	        psDec.prev_sigtype            = 0;
	        psDec.first_frame_after_reset = 1;

	        if( fs_kHz == 24 ) {
	            psDec.HP_A = Silk_tables_other.SKP_Silk_Dec_A_HP_24;
	            psDec.HP_B = Silk_tables_other.SKP_Silk_Dec_B_HP_24;
	        } else if( fs_kHz == 16 ) {
	            psDec.HP_A = Silk_tables_other.SKP_Silk_Dec_A_HP_16;
	            psDec.HP_B = Silk_tables_other.SKP_Silk_Dec_B_HP_16;
	        } else if( fs_kHz == 12 ) {
	            psDec.HP_A = Silk_tables_other.SKP_Silk_Dec_A_HP_12;
	            psDec.HP_B = Silk_tables_other.SKP_Silk_Dec_B_HP_12;
	        } else if( fs_kHz == 8 ) {
	            psDec.HP_A = Silk_tables_other.SKP_Silk_Dec_A_HP_8;
	            psDec.HP_B = Silk_tables_other.SKP_Silk_Dec_B_HP_8;
	        } else {
	            /* unsupported sampling rate */
//	        	SKP_assert(0);
	        	Silk_typedef.SKP_assert( false );
	        }
	    } 

	    /* Check that settings are valid */
	    Silk_typedef.SKP_assert( psDec.frame_length > 0 && psDec.frame_length <= Silk_define.MAX_FRAME_LENGTH );
	}


}
