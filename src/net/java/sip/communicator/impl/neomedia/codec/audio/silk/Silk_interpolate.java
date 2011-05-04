/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_interpolate" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_interpolate.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_interpolate 
{
	/* Interpolate two vectors */
	static void SKP_Silk_interpolate(
//	    SKP_int                         xi[ MAX_LPC_ORDER ],    
		int[] xi, 											/* O    interpolated vector                     */
//	    const SKP_int                   x0[ MAX_LPC_ORDER ],    
		int[] x0, 											/* I    first vector                            */
//	    const SKP_int                   x1[ MAX_LPC_ORDER ],   
		int[] x1, 											/* I    second vector                           */
	    final int                   ifact_Q2,               /* I    interp. factor, weight on 2nd vector    */
	    final int                   d                       /* I    number of parameters                    */
	)
	{
	    int i;

	    assert( ifact_Q2 >= 0 );
	    assert( ifact_Q2 <= ( 1 << 2 ) );

	    for( i = 0; i < d; i++ ) 
	    {
	        xi[ i ] = ( int )( ( int )x0[ i ] + ( ( ( int )x1[ i ] - ( int )x0[ i ] ) * ifact_Q2 >> 2 ) );
	    }
	}
}


