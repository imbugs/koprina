/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_decode_pitch" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_decode_pitch.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * Pitch analyser function
 * @author 
 *
 */
public class Silk_decode_pitch 
{
	/***********************************************************
	* Pitch analyser function
	********************************************************** */
//	void SKP_Silk_decode_pitch(
//	    SKP_int          lagIndex,                        /* I                             */
//	    SKP_int          contourIndex,                    /* O                             */
//	    SKP_int          pitch_lags[],                    /* O 4 pitch values              */
//	    SKP_int          Fs_kHz                           /* I sampling frequency (kHz)    */
//	)
	static void SKP_Silk_decode_pitch(
		    int          lagIndex,                        /* I                             */
		    int          contourIndex,                    /* O                             */
		    int          pitch_lags[],                    /* O 4 pitch values              */
		    int          Fs_kHz                           /* I sampling frequency (kHz)    */
	)
	{
	    int lag, i, min_lag;

//djinn ??	    min_lag = SKP_SMULBB( PITCH_EST_MIN_LAG_MS, Fs_kHz );
	    min_lag = Silk_macros.SKP_SMULBB(Silk_common_pitch_est_defines.PITCH_EST_MIN_LAG_MS, Fs_kHz);

	    /* Only for 24 / 16 kHz version for now */
	    lag = min_lag + lagIndex;
	    if( Fs_kHz == 8 ) {
	        /* Only a small codebook for 8 khz */
	        for( i = 0; i < Silk_common_pitch_est_defines.PITCH_EST_NB_SUBFR; i++ ) {
	            pitch_lags[ i ] = lag + Silk_pitch_est_tables.SKP_Silk_CB_lags_stage2[ i ][ contourIndex ];
	        }
	    } else {
	        for( i = 0; i < Silk_common_pitch_est_defines.PITCH_EST_NB_SUBFR; i++ ) {
	            pitch_lags[ i ] = lag + Silk_pitch_est_tables.SKP_Silk_CB_lags_stage3[ i ][ contourIndex ];
	        }
	    }
	}

}
