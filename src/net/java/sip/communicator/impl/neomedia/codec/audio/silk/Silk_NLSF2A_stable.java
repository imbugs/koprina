/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_NLSF2A_stable" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_NLSF2A_stable.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_NLSF2A_stable 
{
	/* Convert NLSF parameters to stable AR prediction filter coefficients */
//	void SKP_Silk_NLSF2A_stable(
//	    SKP_int16                       pAR_Q12[ MAX_LPC_ORDER ],   /* O    Stabilized AR coefs [LPC_order]     */ 
//	    const SKP_int                   pNLSF[ MAX_LPC_ORDER ],     /* I    NLSF vector         [LPC_order]     */
//	    const SKP_int                   LPC_order                   /* I    LPC/LSF order                       */
//	)
	static void SKP_Silk_NLSF2A_stable(
		    short                       pAR_Q12[],   /* O    Stabilized AR coefs [LPC_order]     */ 
		    int                         pNLSF[],     /* I    NLSF vector         [LPC_order]     */
		    final int                   LPC_order                   /* I    LPC/LSF order                       */
	)
	{
	    int   i;
	    int invGain_Q30;
	    int invGain_Q30_ptr[] = new int[1];
//	    SKP_Silk_NLSF2A( pAR_Q12, pNLSF, LPC_order );
	    Silk_NLSF2A.SKP_Silk_NLSF2A( pAR_Q12, pNLSF, LPC_order );

	    
	    /* Ensure stable LPCs */
	    for( i = 0; i < Silk_define.MAX_LPC_STABILIZE_ITERATIONS; i++ ) {
//	        if( SKP_Silk_LPC_inverse_pred_gain( &invGain_Q30, pAR_Q12, LPC_order ) == 1 ) {
	    	if( Silk_LPC_inv_pred_gain.SKP_Silk_LPC_inverse_pred_gain( invGain_Q30_ptr, pAR_Q12, LPC_order ) == 1 ) {
	    		invGain_Q30 = invGain_Q30_ptr[0];
//	            SKP_Silk_bwexpander( pAR_Q12, LPC_order, 65536 - SKP_SMULBB( 66, i ) ); /* 66_Q16 = 0.001 */
	    		Silk_bwexpander.SKP_Silk_bwexpander( pAR_Q12, LPC_order, 65536 - Silk_macros.SKP_SMULBB( 66, i ) ); /* 66_Q16 = 0.001 */
	        } else {
	        	invGain_Q30 = invGain_Q30_ptr[0];
	            break;
	        }
	    }

	    /* Reached the last iteration */
	    if( i == Silk_define.MAX_LPC_STABILIZE_ITERATIONS ) {
	        for( i = 0; i < LPC_order; i++ ) {
	            pAR_Q12[ i ] = 0;
	        }
	    }
	}

}
