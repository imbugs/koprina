/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_main_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/Silk_main_FLP.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_main_FLP 
{
	/* using log2() helps the fixed-point conversion */
	static float SKP_Silk_log2( double x ) 
	{ 
		return ( float )( 3.32192809488736 * Math.log10( x ) ); 
	}
}
