/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_biquad" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_biquad.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * Second order ARMA filter 
 * Can handle slowly varying filter coefficients 
 * @author 
 *
 */
public class Silk_biquad 
{
	/*                                                                      *
	 * SKP_Silk_biquad.c                                                    *
	 *                                                                      *
	 * Second order ARMA filter                                             *
	 * Can handle slowly varying filter coefficients                        *
	 *                                                                      *
	 * Copyright 2006 (c), Skype Limited                                    *
	 * Date: 060221                                                         *
	 *                                                                      */
//	#include "SKP_Silk_SigProc_FIX.h"

	/* Second order ARMA filter */
	/* Can handle slowly varying filter coefficients */
//	void SKP_Silk_biquad(
//	    const SKP_int16      *in,        /* I:    input signal               */
//	    const SKP_int16      *B,         /* I:    MA coefficients, Q13 [3]   */
//	    const SKP_int16      *A,         /* I:    AR coefficients, Q13 [2]   */
//	    SKP_int32            *S,         /* I/O:  state vector [2]           */
//	    SKP_int16            *out,       /* O:    output signal              */
//	    const SKP_int32      len         /* I:    signal length              */
//	)
	static void SKP_Silk_biquad(
		    short      []in,        /* I:    input signal               */
		    int          in_offset,
		    short      []B,         /* I:    MA coefficients, Q13 [3]   */
		    short      []A,         /* I:    AR coefficients, Q13 [2]   */
		    int        []S,         /* I/O:  state vector [2]           */
		    short      []out,       /* O:    output signal              */
		    int          out_offset,
		    final int    len         /* I:    signal length              */
		)
	{
	    int   k, in16;
	    int A0_neg, A1_neg, S0, S1, out32, tmp32;

	    S0 = S[ 0 ];
	    S1 = S[ 1 ];
	    A0_neg = -A[ 0 ];
	    A1_neg = -A[ 1 ];
	    for( k = 0; k < len; k++ ) {
	        /* S[ 0 ], S[ 1 ]: Q13 */
	        in16  = in[ in_offset + k ];
	        out32 = Silk_macros.SKP_SMLABB( S0, in16, B[ 0 ] );

	        S0 = Silk_macros.SKP_SMLABB( S1, in16, B[ 1 ] );
//	        S0 += SKP_LSHIFT( SKP_SMULWB( out32, A0_neg ), 3 );
	        S0 += ( Silk_macros.SKP_SMULWB( out32, A0_neg ) << 3 );
	        

//	        S1 = SKP_LSHIFT( SKP_SMULWB( out32, A1_neg ), 3 );
	        S1 = ( Silk_macros.SKP_SMULWB( out32, A1_neg ) << 3 );
//	        S1 = SKP_SMLABB( S1, in16, B[ 2 ] );
	        S1 = Silk_macros.SKP_SMLABB( S1, in16, B[ 2 ] );
	        tmp32    = Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out32, 13 ) + 1;
	        out[ out_offset + k ] = (short)Silk_SigProc_FIX.SKP_SAT16( tmp32 );
	    }
	    S[ 0 ] = S0;
	    S[ 1 ] = S1;
	}

}
