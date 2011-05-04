/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_pitch_est_defines_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_pitch_est_defines_FLP.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_pitch_est_defines_FLP
{
	/************************************************************/
	/* Definitions For FLP pitch estimator                      */
	/************************************************************/

	static final float PITCH_EST_FLP_SHORTLAG_BIAS =            0.2f;    /* for logarithmic weighting    */
	static final float PITCH_EST_FLP_PREVLAG_BIAS =             0.2f;    /* for logarithmic weighting    */
	static final float PITCH_EST_FLP_FLATCONTOUR_BIAS =         0.05f;
}
