/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_k2a_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_k2a_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_k2a_FLP 
{
	/* step up function, converts reflection coefficients to prediction coefficients */
	static void SKP_Silk_k2a_FLP(
	    float[]       A,                 /* O:   prediction coefficients [order]             */
	    float[] rc,                /* I:   reflection coefficients [order]             */
	    int       order               /* I:   prediction order                            */
	)
	{
	    int   k, n;
	    float[] Atmp = new float[Silk_SigProc_FIX.SKP_Silk_MAX_ORDER_LPC];

	    for( k = 0; k < order; k++ )
	    {
	        for( n = 0; n < k; n++ )
	        {
	            Atmp[ n ] = A[ n ];
	        }
	        for( n = 0; n < k; n++ ) 
	        {
	            A[ n ] += Atmp[ k - n - 1 ] * rc[ k ];
	        }
	        A[ k ] = -rc[ k ];
	    }
	}


}
