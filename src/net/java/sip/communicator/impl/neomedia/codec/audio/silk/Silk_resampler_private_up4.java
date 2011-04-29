/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_resampler_private_up4" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_resampler_private_up4.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_resampler_private_up4 
{
	/* Upsample by a factor 4, Note: very low quality, only use with output sampling rates above 96 kHz. */
	static void SKP_Silk_resampler_private_up4(
	    int[]                       S,             /* I/O: State vector [ 2 ]                      */
	    int S_offset,
	    short[]                     out,           /* O:   Output signal [ 4 * len ]               */
	    int out_offset,
	    short[]                     in,            /* I:   Input signal [ len ]                    */
	    int in_offset,
	    int                         len            /* I:   Number of INPUT samples                 */
	)
	{
	    int k;
	    int in32, out32, Y, X;
	    int out16;

	    assert( Silk_resampler_rom.SKP_Silk_resampler_up2_lq_0 > 0 );
	    assert( Silk_resampler_rom.SKP_Silk_resampler_up2_lq_1 < 0 );

	    /* Internal variables and state are in Q10 format */
	    for( k = 0; k < len; k++ ) 
	    {
	        /* Convert to Q10 */
	        in32 = (int)in[ in_offset+k ] << 10;

	        /* All-pass section for even output sample */
	        Y      = in32 - S[ 0 ];
	        X      = Silk_macros.SKP_SMULWB( Y, Silk_resampler_rom.SKP_Silk_resampler_up2_lq_0 );
	        out32  = S[ 0 ] + X;
	        S[ 0 ] = in32 + X;

	        /* Convert back to int16 and store to output */
	        out16 = (short)Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out32, 10 ) );
	        out[ out_offset + 4 * k ]     = (short)out16;
	        out[ out_offset + 4 * k + 1 ] = (short)out16;

	        /* All-pass section for odd output sample */
	        Y      = in32 - S[ 1 ];
	        X      = Silk_macros.SKP_SMLAWB( Y, Y, Silk_resampler_rom.SKP_Silk_resampler_up2_lq_1 );
	        out32  = S[ 1 ] + X;
	        S[ 1 ] = in32 + X;

	        /* Convert back to int16 and store to output */
	        out16 = (short)Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out32, 10 ) );
	        out[ out_offset + 4 * k + 2 ] = (short)out16;
	        out[ out_offset + 4 * k + 3 ] = (short)out16;
	    }
	}
}


