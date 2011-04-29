/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_structs" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_structs.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_structs
{	
	
}
/************************************/
/**
 * Noise shaping quantization state
 *
 * @author Jing Dai
 */
/************************************/
class SKP_Silk_nsq_state
{
    short[] xq = new short[2 * Silk_define.MAX_FRAME_LENGTH]; /* Buffer for quantized output signal */
    int[]   sLTP_shp_Q10 = new int[ 2 * Silk_define.MAX_FRAME_LENGTH ];
    int[]   sLPC_Q14 = new int[ Silk_define.MAX_FRAME_LENGTH / Silk_define.NB_SUBFR + Silk_define.MAX_LPC_ORDER ];
    int[]   sAR2_Q14 = new int[ Silk_define.SHAPE_LPC_ORDER_MAX ];
    int     sLF_AR_shp_Q12;
    int     lagPrev;
    int     sLTP_buf_idx;
    int     sLTP_shp_buf_idx;
    int     rand_seed;
    int     prev_inv_gain_Q16;
    int     rewhite_flag;
}/* FIX*/
//typedef struct {
//    SKP_int16   xq[           2 * MAX_FRAME_LENGTH ]; /* Buffer for quantized output signal */
//    SKP_int32   sLTP_shp_Q10[ 2 * MAX_FRAME_LENGTH ];
//    SKP_int32   sLPC_Q14[ MAX_FRAME_LENGTH / NB_SUBFR + MAX_LPC_ORDER ];
//    SKP_int32   sAR2_Q14[ SHAPE_LPC_ORDER_MAX ];
//    SKP_int32   sLF_AR_shp_Q12;
//    SKP_int     lagPrev;
//    SKP_int     sLTP_buf_idx;
//    SKP_int     sLTP_shp_buf_idx;
//    SKP_int32   rand_seed;
//    SKP_int32   prev_inv_gain_Q16;
//    SKP_int     rewhite_flag;
//} SKP_Silk_nsq_state; /* FIX*/

/* Struct for Low BitRate Redundant (LBRR) information */
/**
 * Struct for Low BitRate Redundant (LBRR) information
 * @author
 */
class SKP_SILK_LBRR_struct
{
//djinn	?    SKP_uint8   payload[ MAX_ARITHM_BYTES ];  
	byte[]		payload = new byte[Silk_define.MAX_ARITHM_BYTES];
    int         nBytes;                         /* Number of bytes in payload                               */
    int         usage;                          /* Tells how the payload should be used as FEC              */
}
//typedef struct {
//    SKP_uint8   payload[ MAX_ARITHM_BYTES ];    
//    SKP_int     nBytes;                         /* Number of bytes in payload                               */
//    SKP_int     usage;                          /* Tells how the payload should be used as FEC              */
//} SKP_SILK_LBRR_struct;

/********************************/
/* VAD state                    */
/********************************/
class SKP_Silk_VAD_state
{
    int[]     AnaState = new int[ 2 ];                  /* Analysis filterbank state: 0-8 kHz                       */
    int[]     AnaState1 = new int[ 2 ];                 /* Analysis filterbank state: 0-4 kHz                       */
    int[]     AnaState2 = new int[ 2 ];                 /* Analysis filterbank state: 0-2 kHz                       */
    int[]     XnrgSubfr = new int[ Silk_define.VAD_N_BANDS ];       /* Subframe energies                                        */
    int[]     NrgRatioSmth_Q8 = new int[ Silk_define.VAD_N_BANDS ]; /* Smoothed energy level in each band                       */
    short     HPstate;                        /* State of differentiator in the lowest band               */
    int[]     NL = new int[ Silk_define.VAD_N_BANDS ];              /* Noise energy level in each band                          */
    int[]     inv_NL = new int[ Silk_define.VAD_N_BANDS ];          /* Inverse noise energy level in each band                  */
    int[]     NoiseLevelBias = new int[ Silk_define.VAD_N_BANDS ];  /* Noise level estimator bias/offset                        */
    int   counter;                        /* Frame counter used in the initial phase                  */
} 
//typedef struct {
//    SKP_int32   AnaState[ 2 ];                  /* Analysis filterbank state: 0-8 kHz                       */
//    SKP_int32   AnaState1[ 2 ];                 /* Analysis filterbank state: 0-4 kHz                       */
//    SKP_int32   AnaState2[ 2 ];                 /* Analysis filterbank state: 0-2 kHz                       */
//    SKP_int32   XnrgSubfr[ VAD_N_BANDS ];       /* Subframe energies                                        */
//    SKP_int32   NrgRatioSmth_Q8[ VAD_N_BANDS ]; /* Smoothed energy level in each band                       */
//    SKP_int16   HPstate;                        /* State of differentiator in the lowest band               */
//    SKP_int32   NL[ VAD_N_BANDS ];              /* Noise energy level in each band                          */
//    SKP_int32   inv_NL[ VAD_N_BANDS ];          /* Inverse noise energy level in each band                  */
//    SKP_int32   NoiseLevelBias[ VAD_N_BANDS ];  /* Noise level estimator bias/offset                        */
//    SKP_int32   counter;                        /* Frame counter used in the initial phase                  */
//} SKP_Silk_VAD_state;

/*******************************/
/* Range encoder/decoder state */
/*******************************/
//djinn ?	
class SKP_Silk_range_coder_state
{
    int   bufferLength;
    int   bufferIx;
//    SKP_uint32  base_Q32;
//or ??	    int   base_Q32;
    long  base_Q32;	    
//    SKP_uint32  range_Q16;
//or ??	    int   range_Q16;
    long  range_Q16;	    
    
    int   error;
//    SKP_uint8   buffer[ MAX_ARITHM_BYTES ];     /* Buffer containing payload                                */
    byte[] buffer = new byte[Silk_define.MAX_ARITHM_BYTES];
} 
//typedef struct {
//    SKP_int32   bufferLength;
//    SKP_int32   bufferIx;
//    SKP_uint32  base_Q32;
//    SKP_uint32  range_Q16;
//    SKP_int32   error;
//    SKP_uint8   buffer[ MAX_ARITHM_BYTES ];     /* Buffer containing payload                                */
//} SKP_Silk_range_coder_state;

/* Input frequency range detection struct */
class SKP_Silk_detect_SWB_state
{
//djinn ?	    SKP_int32 S_HP_8_kHz[ NB_SOS ][ 2 ];  /* HP filter State */		
    int[][] S_HP_8_kHz = new int[ Silk_define.NB_SOS ][ 2 ];  /* HP filter State */
    int     ConsecSmplsAboveThres;
    int     ActiveSpeech_ms;            /* Accumulated time with active speech */
    int     SWB_detected;               /* Flag to indicate SWB input */
    int     WB_detected;                /* Flag to indicate WB input */
} 
//typedef struct {
//    SKP_int32                   S_HP_8_kHz[ NB_SOS ][ 2 ];  /* HP filter State */
//    SKP_int32                   ConsecSmplsAboveThres;
//    SKP_int32                   ActiveSpeech_ms;            /* Accumulated time with active speech */
//    SKP_int                     SWB_detected;               /* Flag to indicate SWB input */
//    SKP_int                     WB_detected;                /* Flag to indicate WB input */
//} SKP_Silk_detect_SWB_state;

//djinn ?	
//#if SWITCH_TRANSITION_FILTERING
/* Variable cut-off low-pass filter state */
class SKP_Silk_LP_state 
{
    int[] In_LP_State = new int[ 2 ];           /* Low pass filter state */
    int   transition_frame_no;        /* Counter which is mapped to a cut-off frequency */
    int   mode;                       /* Operating mode, 0: switch down, 1: switch up */
}	
//typedef struct {
//    SKP_int32                   In_LP_State[ 2 ];           /* Low pass filter state */
//    SKP_int32                   transition_frame_no;        /* Counter which is mapped to a cut-off frequency */
//    SKP_int                     mode;                       /* Operating mode, 0: switch down, 1: switch up */
//} SKP_Silk_LP_state;
//#endif

/* Structure for one stage of MSVQ */
class SKP_Silk_NLSF_CBS
{
	public SKP_Silk_NLSF_CBS(int nVectors, short[] CB_NLSF_Q15, short[] Rates_Q5)
	{
		this.CB_NLSF_Q15 = CB_NLSF_Q15;
		this.nVectors    = nVectors;
		this.Rates_Q5    = Rates_Q5;
	}
	public SKP_Silk_NLSF_CBS(int nVectors, short[] SKP_Silk_NLSF_MSVQ_CB0_10_Q15, int Q15_offset,
							short[] SKP_Silk_NLSF_MSVQ_CB0_10_rates_Q5, int Q5_offset)
	{
		this.nVectors = nVectors;
		this.CB_NLSF_Q15 = new short[SKP_Silk_NLSF_MSVQ_CB0_10_Q15.length-Q15_offset];
//		System.arraycopy(this.CB_NLSF_Q15, 0, SKP_Silk_NLSF_MSVQ_CB0_10_Q15, Q15_offset, this.CB_NLSF_Q15.length);
		System.arraycopy( SKP_Silk_NLSF_MSVQ_CB0_10_Q15, Q15_offset, this.CB_NLSF_Q15, 0, this.CB_NLSF_Q15.length);
		this.Rates_Q5 = new short[SKP_Silk_NLSF_MSVQ_CB0_10_rates_Q5.length - Q5_offset];
//		System.arraycopy(this.Rates_Q5, 0, SKP_Silk_NLSF_MSVQ_CB0_10_rates_Q5, Q5_offset, this.Rates_Q5.length);
		System.arraycopy(SKP_Silk_NLSF_MSVQ_CB0_10_rates_Q5, Q5_offset, this.Rates_Q5, 0, this.Rates_Q5.length);
	}
//
	public SKP_Silk_NLSF_CBS()
	{
		super();
	}
//    const SKP_int32             nVectors;
//djinn ??? ignore const ???		
	 int      nVectors;
//    const SKP_int16             *CB_NLSF_Q15;
	short[]   CB_NLSF_Q15;
//    const SKP_int16             *Rates_Q5;
	short[]   Rates_Q5;
}	
//typedef struct {
//    const SKP_int32             nVectors;
//    const SKP_int16             *CB_NLSF_Q15;
//    const SKP_int16             *Rates_Q5;
//} SKP_Silk_NLSF_CBS;

/* Structure containing NLSF MSVQ codebook */
class SKP_Silk_NLSF_CB_struct 
{
	public SKP_Silk_NLSF_CB_struct(int nStates, SKP_Silk_NLSF_CBS[] CBStages, int[] NDeltaMin_Q15,
									int[] CDF, int[][] StartPtr, int[] MiddleIx)
	{
		this.CBStages 	   = CBStages;
		this.CDF      	   = CDF;
		this.MiddleIx 	   = MiddleIx;
		this.NDeltaMin_Q15 = NDeltaMin_Q15;
		this.nStages       = nStates;
		this.StartPtr      = StartPtr;
		
	}
// djinn TODO: ???
	public SKP_Silk_NLSF_CB_struct()
	{
		super();
	}
//	const SKP_int32             nStages;
//djinn ??? ignore const ???		
    int                 nStages;

    /* Fields for (de)quantizing */
//    const SKP_Silk_NLSF_CBS     *CBStages;
//djinn TODO: ??? here CBStates should be defined as a array but a object reference?    
    SKP_Silk_NLSF_CBS[] 	CBStages;
//    const SKP_int               *NDeltaMin_Q15;
    int[] 				NDeltaMin_Q15;

    /* Fields for arithmetic (de)coding */
//    const SKP_uint16            *CDF;
    int[] 				CDF;
//    const SKP_uint16 * const    *StartPtr;
//djinn ??? ignore ???	    
    int[][]		        StartPtr;
//    const SKP_int               *MiddleIx;
    int[]			    MiddleIx;
}
//typedef struct {
//    const SKP_int32             nStages;
//
//    /* Fields for (de)quantizing */
//    const SKP_Silk_NLSF_CBS     *CBStages;
//    const SKP_int               *NDeltaMin_Q15;
//
//    /* Fields for arithmetic (de)coding */
//    const SKP_uint16            *CDF;
//    const SKP_uint16 * const    *StartPtr;
//    const SKP_int               *MiddleIx;
//} SKP_Silk_NLSF_CB_struct;

/********************************/
/* Encoder state                */
/********************************/
class SKP_Silk_encoder_state
{
//	
    SKP_Silk_range_coder_state      sRC = new SKP_Silk_range_coder_state();                            /* Range coder state                                                    */
    SKP_Silk_range_coder_state      sRC_LBRR = new SKP_Silk_range_coder_state();                       /* Range coder state (for low bitrate redundancy)                       */
//#if HIGH_PASS_INPUT
    int[]                           In_HP_State = new int[ 2 ];     /* High pass filter state                                               */
//#endif
//#if SWITCH_TRANSITION_FILTERING
    SKP_Silk_LP_state               sLP = new SKP_Silk_LP_state();                            /* Low pass filter state */
//#endif
    SKP_Silk_VAD_state              sVAD = new SKP_Silk_VAD_state();                           /* Voice activity detector state                                        */

    int                         LBRRprevLastGainIndex;
    int                         prev_sigtype;
    int                         typeOffsetPrev;                 /* Previous signal type and quantization offset                         */
    int                         prevLag;
    int                         prev_lagIndex;
    int                         API_fs_Hz;                      /* API sampling frequency (Hz)                                          */
    int                         prev_API_fs_Hz;                 /* Previous API sampling frequency (Hz)                                 */
    int                         maxInternal_fs_kHz;             /* Maximum internal sampling frequency (kHz)                            */
    int                         fs_kHz;                         /* Internal sampling frequency (kHz)                                    */
    int                         fs_kHz_changed;                 /* Did we switch yet?                                                   */
    int                         frame_length;                   /* Frame length (samples)                                               */
    int                         subfr_length;                   /* Subframe length (samples)                                            */
    int                         la_pitch;                       /* Look-ahead for pitch analysis (samples)                              */
    int                         la_shape;                       /* Look-ahead for noise shape analysis (samples)                        */
    int                         TargetRate_bps;                 /* Target bitrate (bps)                                                 */
    int                         PacketSize_ms;                  /* Number of milliseconds to put in each packet                         */
    int                         PacketLoss_perc;                /* Packet loss rate measured by farend                                  */
    int                         frameCounter;
    int                         Complexity;                     /* Complexity setting: 0-> low; 1-> medium; 2->high                     */
    int                         nStatesDelayedDecision;         /* Number of states in delayed decision quantization                    */
    int                         useInterpolatedNLSFs;           /* Flag for using NLSF interpolation                                    */
    int                         shapingLPCOrder;                /* Filter order for noise shaping filters                               */
    int                         predictLPCOrder;                /* Filter order for prediction filters                                  */
    int                         pitchEstimationComplexity;      /* Complexity level for pitch estimator                                 */
    int                         pitchEstimationLPCOrder;        /* Whitening filter order for pitch estimator                           */
    int                         LTPQuantLowComplexity;          /* Flag for low complexity LTP quantization                             */
    int                         NLSF_MSVQ_Survivors;            /* Number of survivors in NLSF MSVQ                                     */
    int                         first_frame_after_reset;        /* Flag for deactivating NLSF interp. and fluc. reduction after resets  */

    /* Input/output buffering */
    short[]                     inputBuf = new short[ Silk_define.MAX_FRAME_LENGTH ];   /* buffer containin input signal                                        */
    int                         inputBufIx;
    int                         nFramesInPayloadBuf;            /* number of frames sitting in outputBuf                                */
    int                         nBytesInPayloadBuf;             /* number of bytes sitting in outputBuf                                 */

    /* Parameters For LTP scaling Control */
    int                         frames_since_onset;

//    const SKP_Silk_NLSF_CB_struct   *psNLSF_CB[ 2 ];                /* Pointers to voiced/unvoiced NLSF codebooks */
    SKP_Silk_NLSF_CB_struct[]   psNLSF_CB = new SKP_Silk_NLSF_CB_struct[ 2 ];                /* Pointers to voiced/unvoiced NLSF codebooks */

    /* Struct for Inband LBRR */ 
    SKP_SILK_LBRR_struct[]      LBRR_buffer = new SKP_SILK_LBRR_struct[ Silk_define.MAX_LBRR_DELAY ];
//    SKP_SILK_LBRR_struct            LBRR_buffer[ MAX_LBRR_DELAY ];
    int                         oldest_LBRR_idx;
    int                         useInBandFEC;                   /* Saves the API setting for query                                      */
    int                         LBRR_enabled;                   
    int                         LBRR_GainIncreases;             /* Number of shifts to Gains to get LBRR rate Voiced frames             */

    /* Bitrate control */
    int                       bitrateDiff;                    /* Accumulated diff. between the target bitrate and the switch bitrates */
    int                       bitrate_threshold_up;           /* Threshold for switching to a higher internal sample frequency        */
    int                       bitrate_threshold_down;         /* Threshold for switching to a lower internal sample frequency         */
//djinn in "SKP_Silk_resampler_structs.h"
    SKP_Silk_resampler_state_struct  resampler_state = new SKP_Silk_resampler_state_struct();

    /* DTX */
    int                         noSpeechCounter;                /* Counts concecutive nonactive frames, used by DTX                     */
    int                         useDTX;                         /* Flag to enable DTX                                                   */
    int                         inDTX;                          /* Flag to signal DTX period                                            */
    int                         vadFlag;                        /* Flag to indicate Voice Activity                                      */

    /* Struct for detecting SWB input */
    SKP_Silk_detect_SWB_state       sSWBdetect = new SKP_Silk_detect_SWB_state();


    /* Buffers */
    byte[]                      q = new byte[ Silk_define.MAX_FRAME_LENGTH ];      /* pulse signal buffer */
    byte[]                      q_LBRR = new byte[ Silk_define.MAX_FRAME_LENGTH ]; /* pulse signal buffer */
}


//typedef struct {
//    SKP_Silk_range_coder_state      sRC;                            /* Range coder state                                                    */
//    SKP_Silk_range_coder_state      sRC_LBRR;                       /* Range coder state (for low bitrate redundancy)                       */
//#if HIGH_PASS_INPUT
//    SKP_int32                       In_HP_State[ 2 ];               /* High pass filter state                                               */
//#endif
//#if SWITCH_TRANSITION_FILTERING
//    SKP_Silk_LP_state               sLP;                            /* Low pass filter state */
//#endif
//    SKP_Silk_VAD_state              sVAD;                           /* Voice activity detector state                                        */
//
//    SKP_int                         LBRRprevLastGainIndex;
//    SKP_int                         prev_sigtype;
//    SKP_int                         typeOffsetPrev;                 /* Previous signal type and quantization offset                         */
//    SKP_int                         prevLag;
//    SKP_int                         prev_lagIndex;
//    SKP_int32                       API_fs_Hz;                      /* API sampling frequency (Hz)                                          */
//    SKP_int32                       prev_API_fs_Hz;                 /* Previous API sampling frequency (Hz)                                 */
//    SKP_int                         maxInternal_fs_kHz;             /* Maximum internal sampling frequency (kHz)                            */
//    SKP_int                         fs_kHz;                         /* Internal sampling frequency (kHz)                                    */
//    SKP_int                         fs_kHz_changed;                 /* Did we switch yet?                                                   */
//    SKP_int                         frame_length;                   /* Frame length (samples)                                               */
//    SKP_int                         subfr_length;                   /* Subframe length (samples)                                            */
//    SKP_int                         la_pitch;                       /* Look-ahead for pitch analysis (samples)                              */
//    SKP_int                         la_shape;                       /* Look-ahead for noise shape analysis (samples)                        */
//    SKP_int32                       TargetRate_bps;                 /* Target bitrate (bps)                                                 */
//    SKP_int                         PacketSize_ms;                  /* Number of milliseconds to put in each packet                         */
//    SKP_int                         PacketLoss_perc;                /* Packet loss rate measured by farend                                  */
//    SKP_int32                       frameCounter;
//    SKP_int                         Complexity;                     /* Complexity setting: 0-> low; 1-> medium; 2->high                     */
//    SKP_int                         nStatesDelayedDecision;         /* Number of states in delayed decision quantization                    */
//    SKP_int                         useInterpolatedNLSFs;           /* Flag for using NLSF interpolation                                    */
//    SKP_int                         shapingLPCOrder;                /* Filter order for noise shaping filters                               */
//    SKP_int                         predictLPCOrder;                /* Filter order for prediction filters                                  */
//    SKP_int                         pitchEstimationComplexity;      /* Complexity level for pitch estimator                                 */
//    SKP_int                         pitchEstimationLPCOrder;        /* Whitening filter order for pitch estimator                           */
//    SKP_int                         LTPQuantLowComplexity;          /* Flag for low complexity LTP quantization                             */
//    SKP_int                         NLSF_MSVQ_Survivors;            /* Number of survivors in NLSF MSVQ                                     */
//    SKP_int                         first_frame_after_reset;        /* Flag for deactivating NLSF interp. and fluc. reduction after resets  */
//
//    /* Input/output buffering */
//    SKP_int16                       inputBuf[ MAX_FRAME_LENGTH ];   /* buffer containin input signal                                        */
//    SKP_int                         inputBufIx;
//    SKP_int                         nFramesInPayloadBuf;            /* number of frames sitting in outputBuf                                */
//    SKP_int                         nBytesInPayloadBuf;             /* number of bytes sitting in outputBuf                                 */
//
//    /* Parameters For LTP scaling Control */
//    SKP_int                         frames_since_onset;
//
//    const SKP_Silk_NLSF_CB_struct   *psNLSF_CB[ 2 ];                /* Pointers to voiced/unvoiced NLSF codebooks */
//
//    /* Struct for Inband LBRR */ 
//    SKP_SILK_LBRR_struct            LBRR_buffer[ MAX_LBRR_DELAY ];
//    SKP_int                         oldest_LBRR_idx;
//    SKP_int                         useInBandFEC;                   /* Saves the API setting for query                                      */
//    SKP_int                         LBRR_enabled;                   
//    SKP_int                         LBRR_GainIncreases;             /* Number of shifts to Gains to get LBRR rate Voiced frames             */
//
//    /* Bitrate control */
//    SKP_int32                       bitrateDiff;                    /* Accumulated diff. between the target bitrate and the switch bitrates */
//    SKP_int32                       bitrate_threshold_up;           /* Threshold for switching to a higher internal sample frequency        */
//    SKP_int32                       bitrate_threshold_down;         /* Threshold for switching to a lower internal sample frequency         */
//
//    SKP_Silk_resampler_state_struct  resampler_state;
//
//    /* DTX */
//    SKP_int                         noSpeechCounter;                /* Counts concecutive nonactive frames, used by DTX                     */
//    SKP_int                         useDTX;                         /* Flag to enable DTX                                                   */
//    SKP_int                         inDTX;                          /* Flag to signal DTX period                                            */
//    SKP_int                         vadFlag;                        /* Flag to indicate Voice Activity                                      */
//
//    /* Struct for detecting SWB input */
//    SKP_Silk_detect_SWB_state       sSWBdetect;
//
//
//    /* Buffers */
//    SKP_int8                        q[ MAX_FRAME_LENGTH ];      /* pulse signal buffer */
//    SKP_int8                        q_LBRR[ MAX_FRAME_LENGTH ]; /* pulse signal buffer */
//} SKP_Silk_encoder_state;


/************************/
/* Encoder control      */
/************************/
class SKP_Silk_encoder_control
{
    /* Quantization indices */
    int     lagIndex;
    int     contourIndex;
    int     PERIndex;
    int[]   LTPIndex = new int[ Silk_define.NB_SUBFR ];
    int[]   NLSFIndices = new int[ Silk_define.NLSF_MSVQ_MAX_CB_STAGES ];  /* NLSF path of quantized LSF vector   */
    int     NLSFInterpCoef_Q2;
    int[]   GainsIndices = new int[ Silk_define.NB_SUBFR ];
    int     Seed;
    int     LTP_scaleIndex;
    int     RateLevelIndex;
    int     QuantOffsetType;
    int     sigtype;

    /* Prediction and coding parameters */
    int[]   pitchL = new int[ Silk_define.NB_SUBFR ];

    int     LBRR_usage;                     /* Low bitrate redundancy usage                             */
}

//typedef struct {
//    /* Quantization indices */
//    SKP_int     lagIndex;
//    SKP_int     contourIndex;
//    SKP_int     PERIndex;
//    SKP_int     LTPIndex[ NB_SUBFR ];
//    SKP_int     NLSFIndices[ NLSF_MSVQ_MAX_CB_STAGES ];  /* NLSF path of quantized LSF vector   */
//    SKP_int     NLSFInterpCoef_Q2;
//    SKP_int     GainsIndices[ NB_SUBFR ];
//    SKP_int32   Seed;
//    SKP_int     LTP_scaleIndex;
//    SKP_int     RateLevelIndex;
//    SKP_int     QuantOffsetType;
//    SKP_int     sigtype;
//
//    /* Prediction and coding parameters */
//    SKP_int     pitchL[ NB_SUBFR ];
//
//    SKP_int     LBRR_usage;                     /* Low bitrate redundancy usage                             */
//} SKP_Silk_encoder_control;

/* Struct for Packet Loss Concealment */

 class SKP_Silk_PLC_struct
 {
    int       pitchL_Q8;                      /* Pitch lag to use for voiced concealment                  */
    short[]   LTPCoef_Q14 = new short[ Silk_define.LTP_ORDER ];       /* LTP coeficients to use for voiced concealment            */
    short[]   prevLPC_Q12 = new short[ Silk_define.MAX_LPC_ORDER ];
    int   	  last_frame_lost;                /* Was previous frame lost                                  */
    int       rand_seed;                      /* Seed for unvoiced signal generation                      */
    short     randScale_Q14;                  /* Scaling of unvoiced random signal                        */
    int       conc_energy;
    int 	  conc_energy_shift;
    short     prevLTP_scale_Q14;
    int[]     prevGain_Q16 = new int[ Silk_define.NB_SUBFR ];
    int       fs_kHz;
}	
//typedef struct {
//    SKP_int32   pitchL_Q8;                      /* Pitch lag to use for voiced concealment                  */
//    SKP_int16   LTPCoef_Q14[ LTP_ORDER ];       /* LTP coeficients to use for voiced concealment            */
//    SKP_int16   prevLPC_Q12[ MAX_LPC_ORDER ];
//    SKP_int     last_frame_lost;                /* Was previous frame lost                                  */
//    SKP_int32   rand_seed;                      /* Seed for unvoiced signal generation                      */
//    SKP_int16   randScale_Q14;                  /* Scaling of unvoiced random signal                        */
//    SKP_int32   conc_energy;
//    SKP_int     conc_energy_shift;
//    SKP_int16   prevLTP_scale_Q14;
//    SKP_int32   prevGain_Q16[ NB_SUBFR ];
//    SKP_int     fs_kHz;
//} SKP_Silk_PLC_struct;

/* Struct for CNG */
 class SKP_Silk_CNG_struct
 {
	 int[]   CNG_exc_buf_Q10 = new int[ Silk_define.MAX_FRAME_LENGTH ];
	 int[]   CNG_smth_NLSF_Q15 = new int[ Silk_define.MAX_LPC_ORDER ];
	 int[]   CNG_synth_state = new int[ Silk_define.MAX_LPC_ORDER ];
	 int     CNG_smth_Gain_Q16;
	 int     rand_seed;
	 int     fs_kHz;
}	 

 
//typedef struct {
//    SKP_int32   CNG_exc_buf_Q10[ MAX_FRAME_LENGTH ];
//    SKP_int     CNG_smth_NLSF_Q15[ MAX_LPC_ORDER ];
//    SKP_int32   CNG_synth_state[ MAX_LPC_ORDER ];
//    SKP_int32   CNG_smth_Gain_Q16;
//    SKP_int32   rand_seed;
//    SKP_int     fs_kHz;
//} SKP_Silk_CNG_struct;

/********************************/
/* Decoder state                */
/********************************/
 class SKP_Silk_decoder_state
 {
////djinn TODO ???   	 
//    SKP_Silk_range_coder_state  sRC ;                            /* Range coder state 
	SKP_Silk_range_coder_state  sRC = new  SKP_Silk_range_coder_state();                            /* Range coder state */
    int       prev_inv_gain_Q16;
    int[]     sLTP_Q16 = new int[ 2 * Silk_define.MAX_FRAME_LENGTH ];
    int[]     sLPC_Q14 = new int[ Silk_define.MAX_FRAME_LENGTH / Silk_define.NB_SUBFR + Silk_define.MAX_LPC_ORDER ];
    int[]     exc_Q10 = new int [ Silk_define.MAX_FRAME_LENGTH ];
    int[]     res_Q10 = new int [ Silk_define.MAX_FRAME_LENGTH ];
    short[]   outBuf = new short[ 2 * Silk_define.MAX_FRAME_LENGTH ];             /* Buffer for output signal                                             */
    int       sLTP_buf_idx;                               /* LTP_buf_index                                                        */
    int       lagPrev;                                    /* Previous Lag                                                         */
    int       LastGainIndex;                              /* Previous gain index                                                  */
    int       LastGainIndex_EnhLayer;                     /* Previous gain index                                                  */
    int       typeOffsetPrev;                             /* Previous signal type and quantization offset                         */
    int[]     HPState = new int[ Silk_define.DEC_HP_ORDER ];                    /* HP filter state                                                      */
//    const SKP_int16 *HP_A;                                      /* HP filter AR coefficients                                            */
    short[]   HP_A;                                        /* HP filter AR coefficients                                            */
//    const SKP_int16 *HP_B;                                      /* HP filter MA coefficients                                            */
    short[]   HP_B;                                        /* HP filter MA coefficients                                            */
    int       fs_kHz;                                     /* Sampling frequency in kHz                                            */
    int       prev_API_sampleRate;                        /* Previous API sample frequency (Hz)                                   */
    int         frame_length;                               /* Frame length (samples)                                               */
    int         subfr_length;                               /* Subframe length (samples)                                            */
    int         LPC_order;                                  /* LPC order                                                            */
    int[]       prevNLSF_Q15 = new int[ Silk_define.MAX_LPC_ORDER ];              /* Used to interpolate LSFs                                             */
    int         first_frame_after_reset;                    /* Flag for deactivating NLSF interp. and fluc. reduction after resets  */

    /* For buffering payload in case of more frames per packet */
    int         nBytesLeft;
    int         nFramesDecoded;
    int         nFramesInPacket;
    int         moreInternalDecoderFrames;
    int         FrameTermination;
//
    SKP_Silk_resampler_state_struct  resampler_state = new SKP_Silk_resampler_state_struct();

//    const SKP_Silk_NLSF_CB_struct *psNLSF_CB[ 2 ];      /* Pointers to voiced/unvoiced NLSF codebooks */
    SKP_Silk_NLSF_CB_struct[]  psNLSF_CB= new SKP_Silk_NLSF_CB_struct[ 2 ];      /* Pointers to voiced/unvoiced NLSF codebooks */

    /* Parameters used to investigate if inband FEC is used */
    int         vadFlag;
    int         no_FEC_counter;                             /* Counts number of frames wo inband FEC                                */
    int         inband_FEC_offset;                          /* 0: no FEC, 1: FEC with 1 packet offset, 2: FEC w 2 packets offset    */ 

    /* CNG state */
//djinn TODO ???    
//    SKP_Silk_CNG_struct sCNG;
    SKP_Silk_CNG_struct sCNG = new SKP_Silk_CNG_struct();

    /* Stuff used for PLC */
//  //djinn TODO ???     
//    SKP_Silk_PLC_struct sPLC;
    SKP_Silk_PLC_struct sPLC = new SKP_Silk_PLC_struct();
    int         lossCnt;
    int         prev_sigtype;                               /* Previous sigtype                                                     */




}


//typedef struct {
//    SKP_Silk_range_coder_state  sRC;                            /* Range coder state                                                    */
//    SKP_int32       prev_inv_gain_Q16;
//    SKP_int32       sLTP_Q16[ 2 * MAX_FRAME_LENGTH ];
//    SKP_int32       sLPC_Q14[ MAX_FRAME_LENGTH / NB_SUBFR + MAX_LPC_ORDER ];
//    SKP_int32       exc_Q10[ MAX_FRAME_LENGTH ];
//    SKP_int32       res_Q10[ MAX_FRAME_LENGTH ];
//    SKP_int16       outBuf[ 2 * MAX_FRAME_LENGTH ];             /* Buffer for output signal                                             */
//    SKP_int         sLTP_buf_idx;                               /* LTP_buf_index                                                        */
//    SKP_int         lagPrev;                                    /* Previous Lag                                                         */
//    SKP_int         LastGainIndex;                              /* Previous gain index                                                  */
//    SKP_int         LastGainIndex_EnhLayer;                     /* Previous gain index                                                  */
//    SKP_int         typeOffsetPrev;                             /* Previous signal type and quantization offset                         */
//    SKP_int32       HPState[ DEC_HP_ORDER ];                    /* HP filter state                                                      */
//    const SKP_int16 *HP_A;                                      /* HP filter AR coefficients                                            */
//    const SKP_int16 *HP_B;                                      /* HP filter MA coefficients                                            */
//    SKP_int         fs_kHz;                                     /* Sampling frequency in kHz                                            */
//    SKP_int32       prev_API_sampleRate;                        /* Previous API sample frequency (Hz)                                   */
//    SKP_int         frame_length;                               /* Frame length (samples)                                               */
//    SKP_int         subfr_length;                               /* Subframe length (samples)                                            */
//    SKP_int         LPC_order;                                  /* LPC order                                                            */
//    SKP_int         prevNLSF_Q15[ MAX_LPC_ORDER ];              /* Used to interpolate LSFs                                             */
//    SKP_int         first_frame_after_reset;                    /* Flag for deactivating NLSF interp. and fluc. reduction after resets  */
//
//    /* For buffering payload in case of more frames per packet */
//    SKP_int         nBytesLeft;
//    SKP_int         nFramesDecoded;
//    SKP_int         nFramesInPacket;
//    SKP_int         moreInternalDecoderFrames;
//    SKP_int         FrameTermination;
//
//    SKP_Silk_resampler_state_struct  resampler_state;
//
//    const SKP_Silk_NLSF_CB_struct *psNLSF_CB[ 2 ];      /* Pointers to voiced/unvoiced NLSF codebooks */
//
//    /* Parameters used to investigate if inband FEC is used */
//    SKP_int         vadFlag;
//    SKP_int         no_FEC_counter;                             /* Counts number of frames wo inband FEC                                */
//    SKP_int         inband_FEC_offset;                          /* 0: no FEC, 1: FEC with 1 packet offset, 2: FEC w 2 packets offset    */ 
//
//    /* CNG state */
//    SKP_Silk_CNG_struct sCNG;
//
//    /* Stuff used for PLC */
//    SKP_Silk_PLC_struct sPLC;
//    SKP_int         lossCnt;
//    SKP_int         prev_sigtype;                               /* Previous sigtype                                                     */
//
//
//
//
//} SKP_Silk_decoder_state;

/************************/
/* Decoder control      */
/************************/
class SKP_Silk_decoder_control
{
    /* prediction and coding parameters */
    int[]             pitchL = new int[ Silk_define.NB_SUBFR ];
    int[]             Gains_Q16 = new int[ Silk_define.NB_SUBFR ];
    int               Seed;
    /* holds interpolated and final coefficients, 4-byte aligned */
//djinn ??	    SKP_array_of_int16_4_byte_aligned( PredCoef_Q12[ 2 ], MAX_LPC_ORDER );
//djinn TODO: ???    
    int[]            dummy_int32PredCoef_Q12 = new int[2];
    short[][]        PredCoef_Q12 = new short[2][Silk_define.MAX_LPC_ORDER];
    
    
    short[]           LTPCoef_Q14 = new short[ Silk_define.LTP_ORDER * Silk_define.NB_SUBFR ];
    int               LTP_scale_Q14;

    /* quantization indices */
    int             PERIndex;
    int             RateLevelIndex;
    int             QuantOffsetType;
    int             sigtype;
    int             NLSFInterpCoef_Q2;
} 

//typedef struct {
//    /* prediction and coding parameters */
//    SKP_int             pitchL[ NB_SUBFR ];
//    SKP_int32           Gains_Q16[ NB_SUBFR ];
//    SKP_int32           Seed;
//    /* holds interpolated and final coefficients, 4-byte aligned */
//    SKP_array_of_int16_4_byte_aligned( PredCoef_Q12[ 2 ], MAX_LPC_ORDER );
//    SKP_int16           LTPCoef_Q14[ LTP_ORDER * NB_SUBFR ];
//    SKP_int             LTP_scale_Q14;
//
//    /* quantization indices */
//    SKP_int             PERIndex;
//    SKP_int             RateLevelIndex;
//    SKP_int             QuantOffsetType;
//    SKP_int             sigtype;
//    SKP_int             NLSFInterpCoef_Q2;
//} SKP_Silk_decoder_control;