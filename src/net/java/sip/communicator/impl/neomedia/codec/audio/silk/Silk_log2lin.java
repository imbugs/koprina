/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_log2lin" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_PLC.c
 *and
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_log2lin.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * Convert input to a linear scale 
 * @author 
 *
 */
public class Silk_log2lin
{
	/*                                                                      *
	 * SKP_Silk_log2lin.c                                                 *
	 *                                                                      *
	 * Convert input to a linear scale                                      *
	 *                                                                      *
	 * Copyright 2006 (c), Skype Limited                                    *
	 * Date: 060221                                                         *
	 *                                                                      */


	/* Approximation of 2^() (very close inverse of SKP_Silk_lin2log()) */
	/* Convert input to a linear scale    */ 
//	SKP_int32 SKP_Silk_log2lin( const SKP_int32 inLog_Q7 )    /* I:    Input on log scale */ 
	static int SKP_Silk_log2lin( final int inLog_Q7 )    /* I:    Input on log scale */ 
	{
	    int out, frac_Q7;

	    if( inLog_Q7 < 0 ) {
	        return 0;
	    }

//	    out = SKP_LSHIFT( 1, SKP_RSHIFT( inLog_Q7, 7 ) );
	    out = ( 1 << ( inLog_Q7 >> 7 ) );
	    
	    frac_Q7 = inLog_Q7 & 0x7F;
	    if( inLog_Q7 < 2048 ) {
	        /* Piece-wise parabolic approximation */
//	        out = SKP_ADD_RSHIFT( out, SKP_MUL( out, SKP_SMLAWB( frac_Q7, SKP_MUL( frac_Q7, 128 - frac_Q7 ), -174 ) ), 7 );
	        out = Silk_SigProc_FIX.SKP_ADD_RSHIFT( out, Silk_SigProc_FIX.SKP_MUL( out, Silk_macros.SKP_SMLAWB( frac_Q7, Silk_SigProc_FIX.SKP_MUL( frac_Q7, 128 - frac_Q7 ), -174 ) ), 7 );

	    } else {
	        /* Piece-wise parabolic approximation */
//	        out = SKP_MLA( out, SKP_RSHIFT( out, 7 ), SKP_SMLAWB( frac_Q7, SKP_MUL( frac_Q7, 128 - frac_Q7 ), -174 ) );
	        out = Silk_SigProc_FIX.SKP_MLA( out, Silk_SigProc_FIX.SKP_RSHIFT( out, 7 ), Silk_macros.SKP_SMLAWB( frac_Q7, Silk_SigProc_FIX.SKP_MUL( frac_Q7, 128 - frac_Q7 ), -174 ) );

	    }
	    return out;
	}

}
