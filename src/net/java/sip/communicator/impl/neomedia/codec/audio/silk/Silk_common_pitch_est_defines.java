/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_common_pitch_est_defines" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_common_pitch_est_defines.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * Definitions For Fix pitch estimator
 *
 * @author Jing Dai
 */
public class Silk_common_pitch_est_defines 
{

	/************************************************************/
	/* Definitions For Fix pitch estimator                      */
	/************************************************************/

	static final int PITCH_EST_MAX_FS_KHZ =               24; /* Maximum sampling frequency used */

	static final int PITCH_EST_FRAME_LENGTH_MS =          40; /* 40 ms */

	static final int PITCH_EST_MAX_FRAME_LENGTH =         (PITCH_EST_FRAME_LENGTH_MS * PITCH_EST_MAX_FS_KHZ);
	static final int PITCH_EST_MAX_FRAME_LENGTH_ST_1 =    (PITCH_EST_MAX_FRAME_LENGTH >> 2);
	static final int PITCH_EST_MAX_FRAME_LENGTH_ST_2 =    (PITCH_EST_MAX_FRAME_LENGTH >> 1);
//djinn ??? TODO: PITCH_EST_SUB_FRAME is neither defined nor used, temporally ignore it;
//	static final int PITCH_EST_MAX_SF_FRAME_LENGTH =      (PITCH_EST_SUB_FRAME * PITCH_EST_MAX_FS_KHZ);

	static final int PITCH_EST_MAX_LAG_MS =               18;           /* 18 ms -> 56 Hz */
	static final int PITCH_EST_MIN_LAG_MS =               2;            /* 2 ms -> 500 Hz */
	static final int PITCH_EST_MAX_LAG =                  (PITCH_EST_MAX_LAG_MS * PITCH_EST_MAX_FS_KHZ);
	static final int PITCH_EST_MIN_LAG =                  (PITCH_EST_MIN_LAG_MS * PITCH_EST_MAX_FS_KHZ);

	static final int PITCH_EST_NB_SUBFR =                 4;

	static final int PITCH_EST_D_SRCH_LENGTH =            24;

	static final int PITCH_EST_MAX_DECIMATE_STATE_LENGTH = 7;

	static final int PITCH_EST_NB_STAGE3_LAGS =           5;

	static final int PITCH_EST_NB_CBKS_STAGE2 =           3;
	static final int PITCH_EST_NB_CBKS_STAGE2_EXT =       11;

	static final int PITCH_EST_CB_mn2 =                   1;
	static final int PITCH_EST_CB_mx2 =                   2;

	static final int PITCH_EST_NB_CBKS_STAGE3_MAX =       34;
	static final int PITCH_EST_NB_CBKS_STAGE3_MID =       24;
	static final int PITCH_EST_NB_CBKS_STAGE3_MIN =       16;

//djinn ?? these declaration are defined in "SKP_Silk_pitch_est_tables.c"; ignore them here.	
//	extern const SKP_int16 SKP_Silk_CB_lags_stage2[PITCH_EST_NB_SUBFR][PITCH_EST_NB_CBKS_STAGE2_EXT];
//	extern const SKP_int16 SKP_Silk_CB_lags_stage3[PITCH_EST_NB_SUBFR][PITCH_EST_NB_CBKS_STAGE3_MAX];
//	extern const SKP_int16 SKP_Silk_Lag_range_stage3[ SKP_Silk_PITCH_EST_MAX_COMPLEX + 1 ] [ PITCH_EST_NB_SUBFR ][ 2 ];
//	extern const SKP_int16 SKP_Silk_cbk_sizes_stage3[ SKP_Silk_PITCH_EST_MAX_COMPLEX + 1 ];
//	extern const SKP_int16 SKP_Silk_cbk_offsets_stage3[ SKP_Silk_PITCH_EST_MAX_COMPLEX + 1 ];	
}
