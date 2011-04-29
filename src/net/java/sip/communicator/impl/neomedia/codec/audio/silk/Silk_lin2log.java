/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_lin2log" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_PLC.c
 *and
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_lin2log.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * Convert input to a log scale  
 * @author
 *
 */
public class Silk_lin2log
{
	/*                                                                      *
	 * SKP_Silk_lin2log.c                                                 *
	 *                                                                      *
	 * Convert input to a log scale                                         *
	 * Approximation of 128 * log2()                                        *
	 *                                                                      *
	 * Copyright 2006 (c), Skype Limited                                    *
	 * Date: 060221                                                         *
	 *                                                                      */

	/* Approximation of 128 * log2() (very close inverse of approx 2^() below) */
	/* Convert input to a log scale    */ 
//	SKP_int32 SKP_Silk_lin2log( const SKP_int32 inLin )    /* I:    Input in linear scale */
	static int SKP_Silk_lin2log( final int inLin )    /* I:    Input in linear scale */
	{
	    int lz, frac_Q7;

//	    SKP_Silk_CLZ_FRAC( inLin, &lz, &frac_Q7 );
	    int[] lz_ptr = new int[1];
//	    lz_ptr[0] = lz;
	    int[] frac_Q7_ptr = new int[1];
//	    frac_Q7_ptr[0] = frac_Q7;
	    
	    Silk_Inlines.SKP_Silk_CLZ_FRAC( inLin, lz_ptr, frac_Q7_ptr );
	    lz = lz_ptr[0];
	    frac_Q7 = frac_Q7_ptr[0];
	    
	    /* Piece-wise parabolic approximation */
//	    return( SKP_LSHIFT( 31 - lz, 7 ) + SKP_SMLAWB( frac_Q7, SKP_MUL( frac_Q7, 128 - frac_Q7 ), 179 ) );
	    return( Silk_SigProc_FIX.SKP_LSHIFT( 31 - lz, 7 ) + Silk_macros.SKP_SMLAWB( frac_Q7, Silk_SigProc_FIX.SKP_MUL( frac_Q7, 128 - frac_Q7 ), 179 ) );

	}


}
