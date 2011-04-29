/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_LPC_synthesis_filter" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_PLC.c
 *and
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_LPC_synthesis_filter.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author
 *
 */
public class Silk_LPC_synthesis_filter 
{
	/*                                                                      *
	 * SKP_Silk_LPC_synthesis_filter.c                                    *
	 * Coefficients are in Q12                                              *
	 *                                                                      *
	 * even order AR filter                                                 *
	 *                                                                      */


	/* even order AR filter */
//	void SKP_Silk_LPC_synthesis_filter(
//	    const SKP_int16 *in,        /* I:   excitation signal */
//	    const SKP_int16 *A_Q12,     /* I:   AR coefficients [Order], between -8_Q0 and 8_Q0 */
//	    const SKP_int32 Gain_Q26,   /* I:   gain */
//	    SKP_int32 *S,               /* I/O: state vector [Order] */
//	    SKP_int16 *out,             /* O:   output signal */
//	    const SKP_int32 len,        /* I:   signal length */
//	    const SKP_int Order         /* I:   filter order, must be even */
//	)
	static void SKP_Silk_LPC_synthesis_filter
	(
		    short     []in,        /* I:   excitation signal */
		    short     []A_Q12,     /* I:   AR coefficients [Order], between -8_Q0 and 8_Q0 */
		    final int Gain_Q26,   /* I:   gain */
		    int       []S,               /* I/O: state vector [Order] */
		    short     []out,             /* O:   output signal */
		    final int len,        /* I:   signal length */
		    final int Order         /* I:   filter order, must be even */
	)
	{
//	    int   k, j, idx, Order_half = SKP_RSHIFT( Order, 1 );
		int   k, j, idx;
		int   Order_half = ( Order >> 1 );
	    int SA, SB, out32_Q10, out32;

	    /* Order must be even */
	    Silk_typedef.SKP_assert( 2 * Order_half == Order );

	    /* S[] values are in Q14 */
	    for( k = 0; k < len; k++ ) {
	        SA = S[ Order - 1 ];
	        out32_Q10 = 0;
	        for( j = 0; j < ( Order_half - 1 ); j++ ) {
//	            idx = SKP_SMULBB( 2, j ) + 1;
	            idx = Silk_macros.SKP_SMULBB( 2, j ) + 1;
	            SB = S[ Order - 1 - idx ];
	            S[ Order - 1 - idx ] = SA;
//	            out32_Q10 = SKP_SMLAWB( out32_Q10, SA, A_Q12[ ( j << 1 ) ] );
//	            out32_Q10 = SKP_SMLAWB( out32_Q10, SB, A_Q12[ ( j << 1 ) + 1 ] );
	            out32_Q10 = Silk_macros.SKP_SMLAWB( out32_Q10, SA, A_Q12[ ( j << 1 ) ] );
	            out32_Q10 = Silk_macros.SKP_SMLAWB( out32_Q10, SB, A_Q12[ ( j << 1 ) + 1 ] );
	            SA = S[ Order - 2 - idx ];
	            S[ Order - 2 - idx ] = SB;
	        }

	        /* unrolled loop: epilog */
	        SB = S[ 0 ];
	        S[ 0 ] = SA;
//	        out32_Q10 = SKP_SMLAWB( out32_Q10, SA, A_Q12[ Order - 2 ] );
//	        out32_Q10 = SKP_SMLAWB( out32_Q10, SB, A_Q12[ Order - 1 ] );
	        out32_Q10 = Silk_macros.SKP_SMLAWB( out32_Q10, SA, A_Q12[ Order - 2 ] );
	        out32_Q10 = Silk_macros.SKP_SMLAWB( out32_Q10, SB, A_Q12[ Order - 1 ] );
	        /* apply gain to excitation signal and add to prediction */
//	        out32_Q10 = SKP_ADD_SAT32( out32_Q10, SKP_SMULWB( Gain_Q26, in[ k ] ) );
	        out32_Q10 = Silk_macros.SKP_ADD_SAT32( out32_Q10, Silk_macros.SKP_SMULWB( Gain_Q26, in[ k ] ) );

	        /* scale to Q0 */
//	        out32 = SKP_RSHIFT_ROUND( out32_Q10, 10 );
	        out32 = Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out32_Q10, 10 );

	        /* saturate output */
//	        out[ k ] = ( short )SKP_SAT16( out32 );
	        out[ k ] = ( short )Silk_SigProc_FIX.SKP_SAT16( out32 );

	        /* move result into delay line */
//	        S[ Order - 1 ] = SKP_LSHIFT_SAT32( out32_Q10, 4 );
	        S[ Order - 1 ] = Silk_SigProc_FIX.SKP_LSHIFT_SAT32( out32_Q10, 4 );
	    }
	}

}
