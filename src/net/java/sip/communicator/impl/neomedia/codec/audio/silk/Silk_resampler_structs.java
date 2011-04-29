/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_resampler_structs" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_resampler_structs.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * 
 * @author 
 *
 */
public class Silk_resampler_structs 
{
	/* Flag to enable support for input/output sampling rates above 48 kHz. Turn off for embedded devices */
	static final int RESAMPLER_SUPPORT_ABOVE_48KHZ =                   1;

	static final int SKP_Silk_RESAMPLER_MAX_FIR_ORDER =                16;
	static final int SKP_Silk_RESAMPLER_MAX_IIR_ORDER =                6;


	
//	typedef struct {
//		SKP_int32       sIIR[ SKP_Silk_RESAMPLER_MAX_IIR_ORDER ];        /* this must be the first element of this struct */
//		SKP_int32       sFIR[ SKP_Silk_RESAMPLER_MAX_FIR_ORDER ];
//		SKP_int32       sDown2[ 2 ];
//		void            (*resampler_function)( void *, SKP_int16 *, const SKP_int16 *, SKP_int32 );
//		void            (*up2_function)(  SKP_int32 *, SKP_int16 *, const SKP_int16 *, SKP_int32 );
//	    SKP_int32       batchSize;
//		SKP_int32       invRatio_Q16;
//		SKP_int32       FIR_Fracs;
//	    SKP_int32       input2x;
//		const SKP_int16	*Coefs;
//	#if RESAMPLER_SUPPORT_ABOVE_48KHZ
//		SKP_int32       sDownPre[ 2 ];
//		SKP_int32       sUpPost[ 2 ];
//		void            (*down_pre_function)( SKP_int32 *, SKP_int16 *, const SKP_int16 *, SKP_int32 );
//		void            (*up_post_function)(  SKP_int32 *, SKP_int16 *, const SKP_int16 *, SKP_int32 );
//		SKP_int32       batchSizePrePost;
//		SKP_int32       ratio_Q16;
//		SKP_int32       nPreDownsamplers;
//		SKP_int32       nPostUpsamplers;
//	#endif
//		SKP_int32 magic_number;
//	} SKP_Silk_resampler_state_struct;
}


 class SKP_Silk_resampler_state_struct
 {
	int[]       sIIR = new int[ Silk_resampler_structs.SKP_Silk_RESAMPLER_MAX_IIR_ORDER ];        /* this must be the first element of this struct */
	int[]       sFIR = new int[ Silk_resampler_structs.SKP_Silk_RESAMPLER_MAX_FIR_ORDER ];
	int[]       sDown2 = new int[ 2 ];
//djinn ???? how to translate c reference function????		
//	void            (*resampler_function)( void *, SKP_int16 *, const SKP_int16 *, int );
	String resampler_function;
	ResamplerFP resamplerCB;
	void resampler_function( Object state, short[] out, int out_offset, short[] in, int in_offset, int len )
	{
		resamplerCB.resampler_function(state, out, out_offset, in, in_offset, len);
	}
	
//	void            (*up2_function)(  int *, SKP_int16 *, const SKP_int16 *, int );
	String up2_function;
	Up2FP up2CB;
	void up2_function(  int[] state, short[] out, int out_offset, short[] in, int in_offset, int len )
	{
		up2CB.up2_function(state, out, out_offset, in, in_offset, len);
		
	}
    int       batchSize;
	int       invRatio_Q16;
	int       FIR_Fracs;
    int       input2x;
//	const SKP_int16	*Coefs;
    short[]   Coefs;
//#if RESAMPLER_SUPPORT_ABOVE_48KHZ
//djinn ??	    
	int[]       sDownPre = new int[ 2 ];
	int[]       sUpPost = new int[ 2 ];
//djinn ???? how to translate c reference function????		
//	void            (*down_pre_function)( int *, SKP_int16 *, const SKP_int16 *, int );
	String down_pre_function;
	DownPreFP  downPreCB;
	void down_pre_function ( int[] state, short[] out, int out_offset, short[] in, int in_offset, int len )
	{
		downPreCB.down_pre_function(state, out, out_offset, in, in_offset, len);
	}
//	void            (*up_post_function)(  int *, SKP_int16 *, const SKP_int16 *, int );
	String up_post_function;
	UpPostFP  upPostCB;
	void up_post_function ( int[] state, short[] out, int out_offset, short[] in, int in_offset, int len )
	{
		upPostCB.up_post_function(state, out, out_offset, in, in_offset, len);
	}
	int       batchSizePrePost;
	int       ratio_Q16;
	int       nPreDownsamplers;
	int       nPostUpsamplers;
//#endif
	int magic_number;
}
 /*************************************************************************************/
 interface ResamplerFP
 {
//	 void            (*resampler_function)( void *, SKP_int16 *, const SKP_int16 *, int );
	 void resampler_function( Object state, short[] out, int out_offset, short[] in, int in_offset, int len );
 }
 interface Up2FP
 {
//	 void            (*up2_function)(  int *, SKP_int16 *, const SKP_int16 *, int );
	 void up2_function(  int[] state, short[] out, int out_offset, short[] in, int in_offset, int len );
 }
 interface DownPreFP
 {
//	 void            (*down_pre_function)( int *, SKP_int16 *, const SKP_int16 *, int );
	 void down_pre_function ( int[] state, short[] out, int out_offset, short[] in, int in_offset, int len );
 }
 interface UpPostFP
 {
//	 void            (*up_post_function)(  int *, SKP_int16 *, const SKP_int16 *, int );	 
	 void up_post_function ( int[] state, short[] out, int out_offset, short[] in, int in_offset, int len );
 }
 /*************************************************************************************/