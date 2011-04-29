/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_find_pred_coefs_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_find_pred_coefs_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_find_pred_coefs_FLP 
{
	static void SKP_Silk_find_pred_coefs_FLP(
		    SKP_Silk_encoder_state_FLP      psEnc,         /* I/O  Encoder state FLP               */
		    SKP_Silk_encoder_control_FLP    psEncCtrl,     /* I/O  Encoder control FLP             */
		    float                 res_pitch[]     /* I    Residual from pitch analysis    */
		)
		{
		    int         i;
		    float[]       WLTP = new float[ Silk_define.NB_SUBFR * Silk_define.LTP_ORDER * Silk_define.LTP_ORDER ];
		    float[]       invGains = new float[ Silk_define.NB_SUBFR ], Wght = new float[ Silk_define.NB_SUBFR ];
		    float[]       NLSF = new float[ Silk_define.MAX_LPC_ORDER ];
		    float[] x_ptr;
		    float[]       x_pre_ptr, LPC_in_pre = new float[ Silk_define.NB_SUBFR * Silk_define.MAX_LPC_ORDER + Silk_define.MAX_FRAME_LENGTH ];


		    /* Weighting for weighted least squares */
		    for( i = 0; i < Silk_define.NB_SUBFR; i++ ) 
		    {
		        assert( psEncCtrl.Gains[ i ] > 0.0f );
		        invGains[ i ] = 1.0f / psEncCtrl.Gains[ i ];
		        Wght[ i ]     = invGains[ i ] * invGains[ i ];
		    }

		    if( psEncCtrl.sCmn.sigtype == SIG_TYPE_VOICED ) 
		    {
		        /**********/
		        /* VOICED */
		        /**********/
		        assert( psEnc.sCmn.frame_length - psEnc->sCmn.predictLPCOrder >= psEncCtrl->sCmn.pitchL[ 0 ] + LTP_ORDER / 2 );

		        /* LTP analysis */
		        SKP_Silk_find_LTP_FLP( psEncCtrl->LTPCoef, WLTP, &psEncCtrl->LTPredCodGain, res_pitch, 
		            res_pitch + ( psEnc->sCmn.frame_length >> 1 ), psEncCtrl->sCmn.pitchL, Wght, 
		            psEnc->sCmn.subfr_length, psEnc->sCmn.frame_length );


		        /* Quantize LTP gain parameters */
		        SKP_Silk_quant_LTP_gains_FLP( psEncCtrl->LTPCoef, psEncCtrl->sCmn.LTPIndex, &psEncCtrl->sCmn.PERIndex, 
		            WLTP, psEnc->mu_LTP, psEnc->sCmn.LTPQuantLowComplexity );

		        /* Control LTP scaling */
		        SKP_Silk_LTP_scale_ctrl_FLP( psEnc, psEncCtrl );

		        /* Create LTP residual */
		        SKP_Silk_LTP_analysis_filter_FLP( LPC_in_pre, psEnc->x_buf + psEnc->sCmn.frame_length - psEnc->sCmn.predictLPCOrder, 
		            psEncCtrl->LTPCoef, psEncCtrl->sCmn.pitchL, invGains, psEnc->sCmn.subfr_length, psEnc->sCmn.predictLPCOrder );

		    } else {
		        /************/
		        /* UNVOICED */
		        /************/
		        /* Create signal with prepended subframes, scaled by inverse gains */
		        x_ptr     = psEnc->x_buf + psEnc->sCmn.frame_length - psEnc->sCmn.predictLPCOrder;
		        x_pre_ptr = LPC_in_pre;
		        for( i = 0; i < NB_SUBFR; i++ ) {
		            SKP_Silk_scale_copy_vector_FLP( x_pre_ptr, x_ptr, invGains[ i ], 
		                psEnc->sCmn.subfr_length + psEnc->sCmn.predictLPCOrder );
		            x_pre_ptr += psEnc->sCmn.subfr_length + psEnc->sCmn.predictLPCOrder;
		            x_ptr     += psEnc->sCmn.subfr_length;
		        }

		        SKP_memset( psEncCtrl->LTPCoef, 0, NB_SUBFR * LTP_ORDER * sizeof( SKP_float ) );
		        psEncCtrl->LTPredCodGain = 0.0f;
		    }

		    /* LPC_in_pre contains the LTP-filtered input for voiced, and the unfiltered input for unvoiced */
		    SKP_Silk_find_LPC_FLP( NLSF, &psEncCtrl->sCmn.NLSFInterpCoef_Q2, psEnc->sPred.prev_NLSFq, 
		        psEnc->sCmn.useInterpolatedNLSFs * ( 1 - psEnc->sCmn.first_frame_after_reset ), psEnc->sCmn.predictLPCOrder, 
		        LPC_in_pre, psEnc->sCmn.subfr_length + psEnc->sCmn.predictLPCOrder );


		    /* Quantize LSFs */
		    SKP_Silk_process_NLSFs_FLP( psEnc, psEncCtrl, NLSF );

		    /* Calculate residual energy using quantized LPC coefficients */
		    SKP_Silk_residual_energy_FLP( psEncCtrl->ResNrg, LPC_in_pre, psEncCtrl->PredCoef, psEncCtrl->Gains,
		        psEnc->sCmn.subfr_length, psEnc->sCmn.predictLPCOrder );

		    /* Copy to prediction struct for use in next frame for fluctuation reduction */
		    SKP_memcpy( psEnc->sPred.prev_NLSFq, NLSF, psEnc->sCmn.predictLPCOrder * sizeof( SKP_float ) );


		}

}
