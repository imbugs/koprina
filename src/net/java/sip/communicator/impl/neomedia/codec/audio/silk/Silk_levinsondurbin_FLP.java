/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_levinsondurbin_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_levinsondurbin_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author 
 *
 */
public class Silk_levinsondurbin_FLP 
{
	/* Solve the normal equations using the Levinson-Durbin recursion */
	static float SKP_Silk_levinsondurbin_FLP(    /* O    prediction error energy                     */
	    float       A[],                /* O    prediction coefficients [order]             */
	    int A_offset,
	    float corr[],             /* I    input auto-correlations [order + 1]         */
	    final int   order               /* I    prediction order                            */
	)
	{
	    int   i, mHalf, m;
	    float min_nrg, nrg, t, km, Atmp1, Atmp2;
	    
	    min_nrg = 1e-12f * corr[ 0 ] + 1e-9f;
	    nrg = corr[ 0 ];
	    nrg = Math.max(min_nrg, nrg);
	    A[ A_offset ] = corr[ 1 ] / nrg;
	    nrg -= A[ A_offset ] * corr[ 1 ];
	    nrg = Math.max(min_nrg, nrg);

	    for( m = 1; m < order; m++ )
	    {
	        t = corr[ m + 1 ];
	        for( i = 0; i < m; i++ ) 
	        {
	            t -= A[ A_offset+i ] * corr[ m - i ];
	        }

	        /* reflection coefficient */
	        km = t / nrg;

	        /* residual energy */
	        nrg -= km * t;
	        nrg = Math.max(min_nrg, nrg);

	        mHalf = m >> 1;
	        for( i = 0; i < mHalf; i++ ) 
	        {
	            Atmp1 = A[ A_offset+i ];
	            Atmp2 = A[ A_offset + m - i - 1 ];
	            A[ A_offset + m - i - 1 ] -= km * Atmp1;
	            A[ A_offset+i ]         -= km * Atmp2;
	        }
	        if( (m & 1) != 0) 
	        {
	            A[ A_offset+mHalf ]     -= km * A[ mHalf ];
	        }
	        A[ A_offset+m ] = km;
	    }

	    /* return the residual energy */
	    return nrg;
	}
}



