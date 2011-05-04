/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_allpass_int_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_allpass_int_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_allpass_int_FLP 
{
	/* first-order allpass filter */
	static void SKP_Silk_allpass_int_FLP
	(
	    float[]           in,        /* I:   input signal [len]          */
	    int in_offset,
	    float[]           S,         /* I/O: state [1]                   */
	    int S_offset,
	    float             A,         /* I:   coefficient (0 <= A < 1)    */
	    float[]           out,       /* O:   output signal [len]         */
	    int out_offset,
	    final int         len        /* I:   number of samples           */
	)
	{
	    float Y2, X2, S0;
	    int k;

	    S0 = S[ S_offset ];
	    for ( k = len-1; k >= 0; k-- ) 
	    {
	        Y2        = in[in_offset] - S0;
	        X2        = Y2 * A;
	        out[out_offset++]  = S0 + X2;
	        S0        = in[in_offset++] + X2;
	    }
	    S[ S_offset ] = S0;
	}
}


