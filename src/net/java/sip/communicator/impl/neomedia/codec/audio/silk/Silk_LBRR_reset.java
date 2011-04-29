/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_LBRR_reset" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_LBRR_reset.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_LBRR_reset 
{
	/* Resets LBRR buffer, used if packet size changes */
	static void SKP_Silk_LBRR_reset( 
	    SKP_Silk_encoder_state      psEncC             /* I/O  state                                       */
	)
	{
	    int i;

	    for( i = 0; i < Silk_define.MAX_LBRR_DELAY; i++ ) {
	        psEncC.LBRR_buffer[ i ].usage = Silk_define.SKP_SILK_NO_LBRR;
	    }
	}
}
