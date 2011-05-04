/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_apply_sine_window_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_apply_sine_window_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author 
 *
 */
public class Silk_apply_sine_window_FLP 
{
	/* Apply sine window to signal vector.                                                                  */
	/* Window types:                                                                                        */
	/*  0 -> sine window from 0 to pi                                                                       */
	/*  1 -> sine window from 0 to pi/2                                                                     */
	/*  2 -> sine window from pi/2 to pi                                                                    */
	static void SKP_Silk_apply_sine_window_FLP(
	          float                 px_win[],           /* O    Pointer to windowed signal              */
	          int px_win_offset,
	          float                 px[],               /* I    Pointer to input signal                 */
	          int px_offset,
	    final int                   win_type,           /* I    Selects a window type                   */
	    final int                   length              /* I    Window length, multiple of 4            */
	)
	{
	    int   k;
	    float freq, c, S0, S1;

	    /* Length must be multiple of 4 */
	    assert( ( length & 3 ) == 0 );

	    freq = Silk_SigProc_FLP.PI / ( length + 1 );
	    if( win_type == 0 ) 
	    {
	        freq = 2.0f * freq;
	    }

	    /* Approximation of 2 * cos(f) */
	    c = 2.0f - freq * freq;

	    /* Initialize state */
	    if( win_type < 2 ) 
	    {
	        /* Start from 0 */
	        S0 = 0.0f;
	        /* Approximation of sin(f) */
	        S1 = freq;
	    } 
	    else 
	    {
	        /* Start from 1 */
	        S0 = 1.0f;
	        /* Approximation of cos(f) */
	        S1 = 0.5f * c;
	    }

	    /* Uses the recursive equation:   sin(n*f) = 2 * cos(f) * sin((n-1)*f) - sin((n-2)*f)   */
	    /* 4 samples at a time */
	    for( k = 0; k < length; k += 4 ) 
	    {
	        px_win[ px_win_offset + k + 0 ] = px[ px_offset + k + 0 ] * 0.5f * ( S0 + S1 );
	        px_win[ px_win_offset + k + 1 ] = px[ px_offset + k + 1 ] * S1;
	        S0 = c * S1 - S0;
	        px_win[ px_win_offset + k + 2 ] = px[ px_offset + k + 2 ] * 0.5f * ( S1 + S0 );
	        px_win[ px_win_offset + k + 3 ] = px[ px_offset + k + 3 ] * S0;
	        S1 = c * S0 - S1;
	    }
	}
}


