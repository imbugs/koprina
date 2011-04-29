/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_resampler_private_AR2" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_resampler_private_AR2.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author djinn
 *
 */
public class Silk_resampler_private_AR2 
{
	/* Second order AR filter with single delay elements */
	static void SKP_Silk_resampler_private_AR2(
		int[]					    S,		    /* I/O: State vector [ 2 ]			    	    */
		int S_offset,
		int[]					    out_Q8,		/* O:	Output signal				    	    */
		int out_Q8_offset,
		short[]			    	    in,			/* I:	Input signal				    	    */
		int in_offset,
		short[]				        A_Q14,		/* I:	AR coefficients, Q14 	                */
		int A_Q14_offset,
		int				            len			/* I:	Signal length				        	*/
	)
	{
		int	k;
		int	out32;

		for( k = 0; k < len; k++ ) 
		{
			out32       = S[ S_offset ] + ( (int)in[ in_offset+k ] << 8 );
			out_Q8[ out_Q8_offset+k ] = out32;
			out32       = out32 << 2;
			S[ S_offset   ]      = Silk_macros.SKP_SMLAWB( S[ S_offset+1 ], out32, A_Q14[ A_Q14_offset ] );
			S[ S_offset+1 ]      = Silk_macros.SKP_SMULWB( out32, A_Q14[ A_Q14_offset+1 ] );
		}
	}
}
