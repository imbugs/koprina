/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_NLSF2A" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_NLSF2A.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author
 *
 */
public class Silk_NLSF2A 
{
	/* conversion between prediction filter coefficients and LSFs   */
	/* order should be even                                         */
	/* a piecewise linear approximation maps LSF <-> cos(LSF)       */
	/* therefore the result is not accurate LSFs, but the two       */
	/* function are accurate inverses of each other                 */

	
	/* helper function for NLSF2A(..) */
//	SKP_INLINE void SKP_Silk_NLSF2A_find_poly(
//	    SKP_int32        *out,        /* o    intermediate polynomial, Q20            */
//	    const SKP_int32    *cLSF,     /* i    vector of interleaved 2*cos(LSFs), Q20  */
//	    SKP_int            dd         /* i    polynomial order (= 1/2 * filter order) */
//	)
	static void SKP_Silk_NLSF2A_find_poly(
		    int    []out,       /* o    intermediate polynomial, Q20            */
		    int    []cLSF,     /* i    vector of interleaved 2*cos(LSFs), Q20  */
		    int    cLSF_offset,
		    int    dd         /* i    polynomial order (= 1/2 * filter order) */
		)
	{
	    int    k, n;
	    int    ftmp;

//	    out[0] = SKP_LSHIFT( 1, 20 );
	    out[0] = ( 1 << 20 );
	    out[1] = -cLSF[ cLSF_offset + 0];
	    for( k = 1; k < dd; k++ ) {
	        ftmp = cLSF[cLSF_offset + 2*k];            // Q20
//	        out[k+1] = SKP_LSHIFT( out[k-1], 1 ) - (int)SKP_RSHIFT_ROUND64( SKP_SMULL( ftmp, out[k] ), 20 );
	        int test = ftmp * out[k];
	        long test2 =  Silk_SigProc_FIX.SKP_SMULL(ftmp, out[k]);
	        
	        out[k+1] = ( out[k-1] << 1 ) - (int)Silk_SigProc_FIX.SKP_RSHIFT_ROUND64( Silk_SigProc_FIX.SKP_SMULL( ftmp , out[k] ), 20 );

	        for( n = k; n > 1; n-- ) {
//	            out[n] += out[n-2] - (int)SKP_RSHIFT_ROUND64( SKP_SMULL( ftmp, out[n-1] ), 20 );
//	            out[n] += out[n-2] - (int)Silk_SigProc_FIX.SKP_RSHIFT_ROUND64( ( ftmp * out[n-1] ), 20 );
	            out[n] += out[n-2] - (int)Silk_SigProc_FIX.SKP_RSHIFT_ROUND64( Silk_SigProc_FIX.SKP_SMULL( ftmp , out[n-1] ), 20 );

	        }
	        out[1] -= ftmp;
	    }
	}

	/* compute whitening filter coefficients from normalized line spectral frequencies */
//	void SKP_Silk_NLSF2A(
//	    short       *a,               /* o    monic whitening filter coefficients in Q12,  [d]    */
//	    const int    *NLSF,           /* i    normalized line spectral frequencies in Q15, [d]    */
//	    const int    d                /* i    filter order (should be even)                       */
//	)
	static void SKP_Silk_NLSF2A(
		    short       []a,               /* o    monic whitening filter coefficients in Q12,  [d]    */
		    int         []NLSF,           /* i    normalized line spectral frequencies in Q15, [d]    */
		    final int   d                /* i    filter order (should be even)                       */
		)
	{
	    int k, i, dd;
//	    int cos_LSF_Q20[SKP_Silk_MAX_ORDER_LPC];
	    int[] cos_LSF_Q20=new int[Silk_SigProc_FIX.SKP_Silk_MAX_ORDER_LPC];
	    
//	    int P[SKP_Silk_MAX_ORDER_LPC/2+1], Q[SKP_Silk_MAX_ORDER_LPC/2+1];
	    int[] P = new int[Silk_SigProc_FIX.SKP_Silk_MAX_ORDER_LPC/2+1];
	    int[] Q = new int[Silk_SigProc_FIX.SKP_Silk_MAX_ORDER_LPC/2+1];
	    
	    int Ptmp, Qtmp;
	    int f_int;
	    int f_frac;
	    int cos_val, delta;
//	    int a_int32[SKP_Silk_MAX_ORDER_LPC];
	    int[] a_int32 = new int[Silk_SigProc_FIX.SKP_Silk_MAX_ORDER_LPC];
	    
	    int maxabs, absval, idx=0, sc_Q16; 

	    Silk_typedef.SKP_assert(Silk_SigProc_FIX.LSF_COS_TAB_SZ_FIX == 128);

	    /* convert LSFs to 2*cos(LSF(i)), using piecewise linear curve from table */
	    for( k = 0; k < d; k++ ) {
	    	Silk_typedef.SKP_assert(NLSF[k] >= 0 );
	    	Silk_typedef.SKP_assert(NLSF[k] <= 32767 );

	        /* f_int on a scale 0-127 (rounded down) */
//	        f_int = SKP_RSHIFT( NLSF[k], 15 - 7 ); 
	    	f_int = ( NLSF[k] >> (15 - 7) ); 
	        
	        /* f_frac, range: 0..255 */
//	        f_frac = NLSF[k] - SKP_LSHIFT( f_int, 15 - 7 ); 
	        f_frac = NLSF[k] - ( f_int << (15 - 7) ); 

	    	
	        Silk_typedef.SKP_assert(f_int >= 0);
	        Silk_typedef.SKP_assert(f_int < Silk_SigProc_FIX.LSF_COS_TAB_SZ_FIX );

	        /* Read start and end value from table */
//	        cos_val = SKP_Silk_LSFCosTab_FIX_Q12[ f_int ];                /* Q12 */
//	        delta   = SKP_Silk_LSFCosTab_FIX_Q12[ f_int + 1 ] - cos_val;  /* Q12, with a range of 0..200 */
	        cos_val = Silk_LSF_cos_table.SKP_Silk_LSFCosTab_FIX_Q12[ f_int ];                /* Q12 */
	        delta   = Silk_LSF_cos_table.SKP_Silk_LSFCosTab_FIX_Q12[ f_int + 1 ] - cos_val;  /* Q12, with a range of 0..200 */

	        /* Linear interpolation */
//	        cos_LSF_Q20[k] = SKP_LSHIFT( cos_val, 8 ) + SKP_MUL( delta, f_frac ); /* Q20 */
	        cos_LSF_Q20[k] = ( cos_val << 8 ) + ( delta * f_frac ); /* Q20 */

	    }
	    
//	    dd = SKP_RSHIFT( d, 1 );
	    dd = ( d >> 1 );
	    
	    /* generate even and odd polynomials using convolution */
//	    SKP_Silk_NLSF2A_find_poly( P, &cos_LSF_Q20[0], dd );
//	    SKP_Silk_NLSF2A_find_poly( Q, &cos_LSF_Q20[1], dd );
	    SKP_Silk_NLSF2A_find_poly( P, cos_LSF_Q20, 0, dd );
	    SKP_Silk_NLSF2A_find_poly( Q, cos_LSF_Q20, 1, dd );

	    
	    /* convert even and odd polynomials to int Q12 filter coefs */
	    for( k = 0; k < dd; k++ ) {
	        Ptmp = P[k+1] + P[k];
	        Qtmp = Q[k+1] - Q[k];

	        /* the Ptmp and Qtmp values at this stage need to fit in int32 */

	        a_int32[k]     = -Silk_SigProc_FIX.SKP_RSHIFT_ROUND( Ptmp + Qtmp, 9 ); /* Q20 -> Q12 */
	        a_int32[d-k-1] =  Silk_SigProc_FIX.SKP_RSHIFT_ROUND( Qtmp - Ptmp, 9 ); /* Q20 -> Q12 */
	    }

	    /* Limit the maximum absolute value of the prediction coefficients */
	    for( i = 0; i < 10; i++ ) {
	        /* Find maximum absolute value and its index */
	        maxabs = 0;
	        for( k = 0; k < d; k++ ) {
	            absval = Silk_SigProc_FIX.SKP_abs( a_int32[k] );
	            if( absval > maxabs ) {
	                maxabs = absval;
	                idx       = k;
	            }    
	        }
	    
	        if( maxabs > Silk_typedef.SKP_int16_MAX ) {    
	            /* Reduce magnitude of prediction coefficients */
	            maxabs = Silk_SigProc_FIX.SKP_min( maxabs, 98369 ); // ( SKP_int32_MAX / ( 65470 >> 2 ) ) + SKP_int16_MAX = 98369 
//	            sc_Q16 = 65470 - SKP_DIV32( SKP_MUL( 65470 >> 2, maxabs - SKP_int16_MAX ), 
//	                                        SKP_RSHIFT32( SKP_MUL( maxabs, idx + 1), 2 ) );
	            sc_Q16 = 65470 - ( ( (65470 >> 2) * (maxabs - Silk_typedef.SKP_int16_MAX) ) / 
                        ( ( maxabs * (idx + 1)) >> 2 ) );
//	            SKP_Silk_bwexpander_32( a_int32, d, sc_Q16 );
	            Silk_bwexpander_32.SKP_Silk_bwexpander_32( a_int32, d, sc_Q16 );

	        } else {
	            break;
	        }
	    }    

	    /* Reached the last iteration */
	    if( i == 10 ) {
	    	Silk_typedef.SKP_assert(false);
	        for( k = 0; k < d; k++ ) {
	            a_int32[k] = Silk_SigProc_FIX.SKP_SAT16( a_int32[k] ); 
	        }
	    }

	    /* Return as SKP_int16 Q12 coefficients */
	    for( k = 0; k < d; k++ ) {
	        a[k] = (short)a_int32[k];
	    }
	}

}
