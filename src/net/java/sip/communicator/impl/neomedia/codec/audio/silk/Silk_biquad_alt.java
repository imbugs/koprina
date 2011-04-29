/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_biquad_alt" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_biquad_alt.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author 
 *
 */
public class Silk_biquad_alt 
{
	/* Second order ARMA filter, alternative implementation */
	static void SKP_Silk_biquad_alt(
	    short[]      in,            /* I:    Input signal                   */
	    int in_offset,
	    int[]        B_Q28,         /* I:    MA coefficients [3]            */
	    int[]        A_Q28,         /* I:    AR coefficients [2]            */
	    int[]        S,             /* I/O: State vector [2]                */
	    short[]      out,           /* O:    Output signal                  */
	    int out_offset,
	    final int    len             /* I:    Signal length (must be even)   */
	)
	{
	    /* DIRECT FORM II TRANSPOSED (uses 2 element state vector) */
	    int   k;
	    int inval, A0_U_Q28, A0_L_Q28, A1_U_Q28, A1_L_Q28, out32_Q14;

	    /* Negate A_Q28 values and split in two parts */
	    A0_L_Q28 = ( -A_Q28[ 0 ] ) & 0x00003FFF;        /* lower part */
	    A0_U_Q28 = ( -A_Q28[ 0 ] ) >> 14;       /* upper part */
	    A1_L_Q28 = ( -A_Q28[ 1 ] ) & 0x00003FFF;        /* lower part */
	    A1_U_Q28 = ( -A_Q28[ 1 ] ) >> 14;       /* upper part */
	    
	    for( k = 0; k < len; k++ ) 
	    {
	        /* S[ 0 ], S[ 1 ]: Q12 */
	        inval = in[ in_offset+k ];
	        out32_Q14 = Silk_macros.SKP_SMLAWB( S[ 0 ], B_Q28[ 0 ], inval ) << 2;

	        S[ 0 ] = S[1] + ( Silk_macros.SKP_SMULWB( out32_Q14, A0_L_Q28 ) >> 14 );
	        S[ 0 ] = Silk_macros.SKP_SMLAWB( S[ 0 ], out32_Q14, A0_U_Q28 );
	        S[ 0 ] = Silk_macros.SKP_SMLAWB( S[ 0 ], B_Q28[ 1 ], inval);

	        S[ 1 ] = Silk_macros.SKP_SMULWB( out32_Q14, A1_L_Q28 ) >> 14;
	        S[ 1 ] = Silk_macros.SKP_SMLAWB( S[ 1 ], out32_Q14, A1_U_Q28 );
	        S[ 1 ] = Silk_macros.SKP_SMLAWB( S[ 1 ], B_Q28[ 2 ], inval );

	        /* Scale back to Q0 and saturate */
	        out[ out_offset+k ] = (short)Silk_SigProc_FIX.SKP_SAT16( ( out32_Q14 >> 14 ) + 2 );
	    }
	}
}



