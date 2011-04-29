/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_pitch_est_defines" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_pitch_est_defines.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * Definitions For Fix pitch estimator
 *
 * @author Jing Dai
 */
public class Silk_pitch_est_defines 
{
	/************************************************************/
	/* Definitions For Fix pitch estimator                      */
	/************************************************************/

	static final int PITCH_EST_SHORTLAG_BIAS_Q15 =        6554;    /* 0.2f. for logarithmic weighting    */
	static final int PITCH_EST_PREVLAG_BIAS_Q15 =         6554;    /* Prev lag bias    */
	static final int PITCH_EST_FLATCONTOUR_BIAS_Q20 =     52429;   /* 0.05f */
}
