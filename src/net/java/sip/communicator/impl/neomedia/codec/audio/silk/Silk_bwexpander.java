/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_bwexpander" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_bwexpander.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * Chirp (bandwidth expand) LP AR filter
 *
 * @author Jing Dai
 */
public class Silk_bwexpander
{
	/* Chirp (bandwidth expand) LP AR filter */
//	void SKP_Silk_bwexpander( 
//	    SKP_int16            *ar,        /* I/O  AR filter to be expanded (without leading 1)    */
//	    const SKP_int        d,          /* I    Length of ar                                    */
//	    SKP_int32            chirp_Q16   /* I    Chirp factor (typically in the range 0 to 1)    */
//	)
	static void SKP_Silk_bwexpander( 
		    short            []ar,        /* I/O  AR filter to be expanded (without leading 1)    */
		    final int        d,          /* I    Length of ar                                    */
		    int              chirp_Q16   /* I    Chirp factor (typically in the range 0 to 1)    */
	)
	{
	    int   i;
	    int chirp_minus_one_Q16;

	    chirp_minus_one_Q16 = chirp_Q16 - 65536;

	    /* NB: Dont use SKP_SMULWB, instead of SKP_RSHIFT_ROUND( SKP_MUL() , 16 ), below. */
	    /* Bias in SKP_SMULWB can lead to unstable filters                                */
	    for( i = 0; i < d - 1; i++ ) {
//	        ar[ i ]    = (SKP_int16)SKP_RSHIFT_ROUND( SKP_MUL( chirp_Q16, ar[ i ]             ), 16 );
//	        chirp_Q16 +=            SKP_RSHIFT_ROUND( SKP_MUL( chirp_Q16, chirp_minus_one_Q16 ), 16 );
	        ar[ i ]    = (short)Silk_SigProc_FIX.SKP_RSHIFT_ROUND( ( chirp_Q16 * ar[ i ]             ), 16 );
	        chirp_Q16 +=            Silk_SigProc_FIX.SKP_RSHIFT_ROUND( ( chirp_Q16 * chirp_minus_one_Q16 ), 16 );
	    }
//	    ar[ d - 1 ] = (SKP_int16)SKP_RSHIFT_ROUND( SKP_MUL( chirp_Q16, ar[ d - 1 ] ), 16 );
	    ar[ d - 1 ] = (short)Silk_SigProc_FIX.SKP_RSHIFT_ROUND( ( chirp_Q16 * ar[ d - 1 ] ), 16 );
	}

}
