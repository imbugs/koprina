/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_gain_quant" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_gain_quant.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author 
 *
 */
public class Silk_gain_quant
{

	static final int OFFSET =         ( ( Silk_define.MIN_QGAIN_DB * 128 ) / 6 + 16 * 128 );
	static final int SCALE_Q16 =      ( ( 65536 * ( Silk_define.N_LEVELS_QGAIN - 1 ) ) / ( ( ( Silk_define.MAX_QGAIN_DB - Silk_define.MIN_QGAIN_DB ) * 128 ) / 6 ) );
	static final int INV_SCALE_Q16 =   ( ( 65536 * ( ( ( Silk_define.MAX_QGAIN_DB - Silk_define.MIN_QGAIN_DB ) * 128 ) / 6 ) ) / ( Silk_define.N_LEVELS_QGAIN - 1 ) );

	/* Gain scalar quantization with hysteresis, uniform on log scale */
//	void SKP_Silk_gains_quant(
//	    SKP_int                         ind[ NB_SUBFR ],        /* O    gain indices                            */
//	    SKP_int32                       gain_Q16[ NB_SUBFR ],   /* I/O  gains (quantized out)                   */
//	    SKP_int                         *prev_ind,              /* I/O  last index in previous frame            */
//	    const SKP_int                   conditional             /* I    first gain is delta coded if 1          */
//	)
	static void SKP_Silk_gains_quant(
		    int                       ind[],        /* O    gain indices                            */
		    int                       gain_Q16[],   /* I/O  gains (quantized out)                   */
		    int                       []prev_ind,              /* I/O  last index in previous frame            */
		    final int                 conditional             /* I    first gain is delta coded if 1          */
	)
	{
	    int k;

	    for( k = 0; k < Silk_define.NB_SUBFR; k++ ) {
	        /* Add half of previous quantization error, convert to log scale, scale, floor() */
//	        ind[ k ] = Silk_macros.SKP_SMULWB( SCALE_Q16, SKP_Silk_lin2log( gain_Q16[ k ] ) - OFFSET );
	        ind[ k ] = Silk_macros.SKP_SMULWB( SCALE_Q16, Silk_lin2log.SKP_Silk_lin2log( gain_Q16[ k ] ) - OFFSET );

	        /* Round towards previous quantized gain (hysteresis) */
	        if( ind[ k ] < prev_ind[0] ) {
	            ind[ k ]++;
	        }

	        /* Compute delta indices and limit */
	        if( k == 0 && conditional == 0 ) {
	            /* Full index */
	            ind[ k ] = Silk_SigProc_FIX.SKP_LIMIT_int( ind[ k ], 0, Silk_define.N_LEVELS_QGAIN - 1 );
	            ind[ k ] = Math.max( ind[ k ], prev_ind[0] + Silk_define.MIN_DELTA_GAIN_QUANT );
	            prev_ind[0] = ind[ k ];
	        } else {
	            /* Delta index */
	            ind[ k ] = Silk_SigProc_FIX.SKP_LIMIT_int( ind[ k ] - prev_ind[0], Silk_define.MIN_DELTA_GAIN_QUANT, Silk_define.MAX_DELTA_GAIN_QUANT );
	            /* Accumulate deltas */
	            prev_ind[0] += ind[ k ];
	            /* Shift to make non-negative */
	            ind[ k ] -= Silk_define.MIN_DELTA_GAIN_QUANT;
	        }

	        /* Convert to linear scale and scale */
//	        gain_Q16[ k ] = SKP_Silk_log2lin( Math.min( Silk_macros.SKP_SMULWB( INV_SCALE_Q16, prev_ind[0] ) + OFFSET, 3967 ) ); /* 3967 = 31 in Q7 */
	        gain_Q16[ k ] = Silk_log2lin.SKP_Silk_log2lin( Math.min( Silk_macros.SKP_SMULWB( INV_SCALE_Q16, prev_ind[0] ) + OFFSET, 3967 ) ); /* 3967 = 31 in Q7 */

	    }
	}

	/* Gains scalar dequantization, uniform on log scale */
//	void SKP_Silk_gains_dequant(
//	    SKP_int32                       gain_Q16[ NB_SUBFR ],   /* O    quantized gains                         */
//	    const SKP_int                   ind[ NB_SUBFR ],        /* I    gain indices                            */
//	    SKP_int                         *prev_ind,              /* I/O  last index in previous frame            */
//	    const SKP_int                   conditional             /* I    first gain is delta coded if 1          */
//	)
	static void SKP_Silk_gains_dequant(
		    int                         gain_Q16[ ],   /* O    quantized gains                         */
		    int                         ind[  ],        /* I    gain indices                            */
		    int                         []prev_ind,              /* I/O  last index in previous frame            */
		    final int                   conditional             /* I    first gain is delta coded if 1          */
		)
	{
	    int   k;

	    for( k = 0; k < Silk_define.NB_SUBFR; k++ ) {
	        if( k == 0 && conditional == 0 ) {
	            prev_ind[0] = ind[ k ];
	        } else {
	            /* Delta index */
	            prev_ind[0] += ind[ k ] + Silk_define.MIN_DELTA_GAIN_QUANT;
	        }

	        /* Convert to linear scale and scale */
//	        gain_Q16[ k ] = SKP_Silk_log2lin( Math.min( Silk_macros.SKP_SMULWB( INV_SCALE_Q16, prev_ind[0] ) + OFFSET, 3967 ) ); /* 3967 = 31 in Q7 */
	        gain_Q16[ k ] = Silk_log2lin.SKP_Silk_log2lin( Math.min( Silk_macros.SKP_SMULWB( INV_SCALE_Q16, prev_ind[0] ) + OFFSET, 3967 ) ); /* 3967 = 31 in Q7 */

	    }
	}

}
