/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_resampler_private_copy" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_resampler_private_copy.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_resampler_private_copy 
{
	/* Copy */
	static void SKP_Silk_resampler_private_copy(
//		void	                        *SS,		    /* I/O: Resampler state (unused)				*/
		short[]						out,		/* O:	Output signal 							*/
		int out_offset,
		short[]					    in,		    /* I:	Input signal							*/
		int in_offset,
		int	    				    inLen       /* I:	Number of input samples					*/
	)
	{
//	    SKP_memcpy( out, in, inLen * sizeof( SKP_int16 ) );
		for(int k=0; k<inLen; k++)
			out[out_offset+k] = in[in_offset+k];
	}
}
