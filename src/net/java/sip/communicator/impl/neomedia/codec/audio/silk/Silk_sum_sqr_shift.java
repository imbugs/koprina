/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_sum_sqr_shift" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_sum_sqr_shift.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * compute number of bits to right shift the sum of squares of a vector 
 * of int16s to make it fit in an int32 
 * @author 
 *
 */
public class Silk_sum_sqr_shift 
{
	/*                                                                      *
	 * SKP_Silk_sum_sqr_shift.c                                           *
	 *                                                                      *
	 * compute number of bits to right shift the sum of squares of a vector *
	 * of int16s to make it fit in an int32                                 *
	 *                                                                      *
	 * Copyright 2006-2008 (c), Skype Limited                               *
	 *                                                                      */

	/* Compute number of bits to right shift the sum of squares of a vector */
	/* of int16s to make it fit in an int32                                 */
//	void SKP_Silk_sum_sqr_shift(
//	    SKP_int32            *energy,            /* O    Energy of x, after shifting to the right            */
//	    SKP_int              *shift,             /* O    Number of bits right shift applied to energy        */
//	    const SKP_int16      *x,                 /* I    Input vector                                        */
//	    SKP_int              len                 /* I    Length of input vector                              */
//	)
	static void SKP_Silk_sum_sqr_shift(
		    int       []energy,            /* O    Energy of x, after shifting to the right            */
		    int       []shift,             /* O    Number of bits right shift applied to energy        */
		    short     []x,                 /* I    Input vector                                        */
		    int       x_offset,
		    int       len                 /* I    Length of input vector                              */
		)
	{
	    int   i, shft;
	    int in32, nrg_tmp, nrg;

//	    if( (int)( (SKP_int_ptr_size)x & 2 ) != 0 ) {
// djinn TODO ?????	    
	    if( false ) {
	        /* Input is not 4-byte aligned */
//	        nrg = SKP_SMULBB( x[ x_offset + 0 ], x[x_offset + 0 ] );
	        nrg = Silk_macros.SKP_SMULBB( x[ x_offset + 0 ], x[x_offset + 0 ] );
	        i = 1;
	    } else {
	        nrg = 0;
	        i   = 0;
	    }
	    shft = 0;
	    len--;
	    while( i < len ) {
	        /* Load two values at once */
//	        in32 = *( (int *)&x[ i ] );
	    	in32 = x[ x_offset + i];
	    	
//	        nrg = SKP_SMLABB_ovflw( nrg, in32, in32 );
//	        nrg = SKP_SMLATT_ovflw( nrg, in32, in32 );
	        nrg = Silk_SigProc_FIX.SKP_SMLABB_ovflw( nrg, in32, in32 );
	        nrg = Silk_SigProc_FIX.SKP_SMLATT_ovflw( nrg, in32, in32 );
	        i += 2;
	        if( nrg < 0 ) {
	            /* Scale down */
//	            nrg = (int)SKP_RSHIFT_uint( (SKP_uint32)nrg, 2 );
	            nrg = (int)( ((long)nrg)&0xFFFFFFFFL >>> 2 );
	            shft = 2;
	            break;
	        }
	    }
	    for( ; i < len; i += 2 ) {
	        /* Load two values at once */
//	        in32 = *( (int *)&x[ i ] );
	        in32 =  x[x_offset + i];
//	        
//	        nrg_tmp = SKP_SMULBB( in32, in32 );
//	        nrg_tmp = SKP_SMLATT_ovflw( nrg_tmp, in32, in32 );
//	        nrg = (int)SKP_ADD_RSHIFT_uint( nrg, (SKP_uint32)nrg_tmp, shft );
	        nrg_tmp = Silk_macros.SKP_SMULBB( in32, in32 );
	        nrg_tmp = Silk_SigProc_FIX.SKP_SMLATT_ovflw( nrg_tmp, in32, in32 );
//djinn TODO ???	        
//or	        nrg = (int)( nrg + (((long)nrg_tmp)&0xFFFFFFFFL) >>>  shft );
	        nrg = nrg + (nrg_tmp>>>shft);
	        
	        if( nrg < 0 ) {
	            /* Scale down */
//	            nrg = (int)SKP_RSHIFT_uint( (SKP_uint32)nrg, 2 );
	            nrg = (int)( nrg >>> 2 );
	            shft += 2;
	        }
	    }
	    if( i == len ) {
	        /* One sample left to process */
//	        nrg_tmp = SKP_SMULBB( x[ i ], x[ i ] );
	        nrg_tmp = Silk_macros.SKP_SMULBB( x[ x_offset + i ], x[ x_offset + i ] );
//	        nrg = (int)SKP_ADD_RSHIFT_uint( nrg, nrg_tmp, shft );
	        nrg = ( nrg + (nrg_tmp >>> shft) );
	    }

	    /* Make sure to have at least one extra leading zero (two leading zeros in total) */
	    if( (nrg & 0xC0000000) != 0 ) {
//	        nrg = SKP_RSHIFT_uint( (SKP_uint32)nrg, 2 );
	        nrg = (  nrg >>> 2 );
	        shft += 2;
	    }

	    /* Output arguments */
//	    *shift  = shft;
//	    *energy = nrg;
	    shift[0]  = shft;
	    energy[0] = nrg;
	}


}
