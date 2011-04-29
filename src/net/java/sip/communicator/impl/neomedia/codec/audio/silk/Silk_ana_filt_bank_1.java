/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_ana_filt_bank_1" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_ana_filt_bank_1.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author 
 *
 */
public class Silk_ana_filt_bank_1
{
	/* Coefficients for 2-band filter bank based on first-order allpass filters */
	// old
	static short[] A_fb1_20 = {  5394 << 1 };
	static short[] A_fb1_21 = { (short)(20623 << 1) };        /* wrap-around to negative number is intentional */

	/* Split signal into two decimated bands using first-order allpass filters */
	static void SKP_Silk_ana_filt_bank_1
	(
	    short[]      in,        /* I:   Input signal [N]        */
	    int in_offset,
	    int[]            S,         /* I/O: State vector [2]        */
	    int S_offset,
	    short[]            outL,      /* O:   Low band [N/2]          */
	    int outL_offset,
	    short[]            outH,      /* O:   High band [N/2]         */
	    int outH_offset,
	    int[]            scratch,   /* I:   Scratch memory [3*N/2]  */   // todo: remove - no longer used
	    final int      N           /* I:   Number of input samples */
	)
	{
	    int      k, N2 = N >> 1;
	    int    in32, X, Y, out_1, out_2;

	    /* Internal variables and state are in Q10 format */
	    for( k = 0; k < N2; k++ ) 
	    {
	        /* Convert to Q10 */
	        in32 = (int)in[ 2 * k ] << 10;

	        /* All-pass section for even input sample */
	        Y      = in32 - S[ 0 ];
	        X      = Silk_macros.SKP_SMLAWB( Y, Y, A_fb1_21[ 0 ] );
	        out_1  = S[ 0 ] + X;
	        S[ 0 ] = in32 + X;

	        /* Convert to Q10 */
	        in32 = (int)in[ 2 * k + 1 ] << 10;

	        /* All-pass section for odd input sample, and add to output of previous section */
	        Y      = in32 - S[ 1 ];
	        X      = Silk_macros.SKP_SMULWB( Y, A_fb1_20[ 0 ] );
	        out_2  = S[ 1 ] + X;
	        S[ 1 ] = in32 + X;

	        /* Add/subtract, convert back to int16 and store to output */
	        outL[ k ] = (short)Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out_2 + out_1, 11 ) );
	        outH[ k ] = (short)Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( out_2 - out_1, 11 ) );
	    }
	}
}
