/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_bwexpander_32" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_bwexpander_32.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author 
 *
 */
public class Silk_bwexpander_32
{
	/* Chirp (bandwidth expand) LP AR filter */
//	void SKP_Silk_bwexpander_32( 
//	    SKP_int32        *ar,      /* I/O    AR filter to be expanded (without leading 1)    */
//	    const SKP_int    d,        /* I    Length of ar                                      */
//	    SKP_int32        chirp_Q16 /* I    Chirp factor in Q16                               */
//	)
	static void SKP_Silk_bwexpander_32( 
		    int        []ar,      /* I/O    AR filter to be expanded (without leading 1)    */
		    final int  d,        /* I    Length of ar                                      */
		    int        chirp_Q16 /* I    Chirp factor in Q16                               */
		)
	{
	    int   i;
	    int tmp_chirp_Q16;

	    tmp_chirp_Q16 = chirp_Q16;
	    for( i = 0; i < d - 1; i++ ) {
	        ar[ i ]       = Silk_macros.SKP_SMULWW( ar[ i ],   tmp_chirp_Q16 );
	        tmp_chirp_Q16 = Silk_macros.SKP_SMULWW( chirp_Q16, tmp_chirp_Q16 );
	    }
	    ar[ d - 1 ] = Silk_macros.SKP_SMULWW( ar[ d - 1 ], tmp_chirp_Q16 );
	}

}
