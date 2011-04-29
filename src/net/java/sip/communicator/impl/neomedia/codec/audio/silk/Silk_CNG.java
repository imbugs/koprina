/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_CNG" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_CNG.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import java.util.Arrays;

/**
 * 
 * @author 
 *
 */
public class Silk_CNG 
{	/* Generates excitation for CNG LPC synthesis */
//	SKP_INLINE void SKP_Silk_CNG_exc(
//	    SKP_int16                       residual[],         /* O    CNG residual signal Q0                      */
//	    SKP_int32                       exc_buf_Q10[],      /* I    Random samples buffer Q10                   */
//	    SKP_int32                       Gain_Q16,           /* I    Gain to apply                               */
//	    SKP_int                         length,             /* I    Length                                      */
//	    SKP_int32                       *rand_seed          /* I/O  Seed to random index generator              */
//	)
	static void SKP_Silk_CNG_exc(
		    short                     residual[],         /* O    CNG residual signal Q0                      */
		    int 					  residual_offset,
		    int                       exc_buf_Q10[],      /* I    Random samples buffer Q10                   */
		    int                       exc_buf_Q10_offset,
		    int                       Gain_Q16,           /* I    Gain to apply                               */
		    int                       length,             /* I    Length                                      */
		    int[]                     rand_seed          /* I/O  Seed to random index generator              */
	)
	{
	    int seed;
	    int   i, idx, exc_mask;

	    exc_mask = Silk_define.CNG_BUF_MASK_MAX;
	    while( exc_mask > length ) {
//	        exc_mask = SKP_RSHIFT( exc_mask, 1 );
	    	 exc_mask = ( exc_mask >> 1 );
	    }

//	    seed = *rand_seed;
	    seed = rand_seed[0];
	    
	    for( i = 0; i < length; i++ ) {
	        seed = Silk_SigProc_FIX.SKP_RAND( seed );
//	        idx = ( int )( SKP_RSHIFT( seed, 24 ) & exc_mask );
	        idx = ( int )( ( seed >> 24 ) & exc_mask );
	        Silk_typedef.SKP_assert( idx >= 0 );
	        Silk_typedef.SKP_assert( idx <= Silk_define.CNG_BUF_MASK_MAX );
//	        residual[ i ] = ( short )SKP_SAT16( SKP_RSHIFT_ROUND( SKP_SMULWW( exc_buf_Q10[ idx ], Gain_Q16 ), 10 ) );
	        residual[ residual_offset+i ] = ( short )Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( Silk_macros.SKP_SMULWW( exc_buf_Q10[ idx ], Gain_Q16 ), 10 ) );
	    }
//	    *rand_seed = seed;
	    rand_seed[0] = seed;
	}

//	void SKP_Silk_CNG_Reset(
//	    SKP_Silk_decoder_state      *psDec              /* I/O  Decoder state                               */
//	)
	static void SKP_Silk_CNG_Reset(
		    SKP_Silk_decoder_state     psDec              /* I/O  Decoder state                               */
	)
	{
	    int i, NLSF_step_Q15, NLSF_acc_Q15;

//	    NLSF_step_Q15 = SKP_DIV32_16( SKP_int16_MAX, psDec.LPC_order + 1 );
	    NLSF_step_Q15 = ( Silk_typedef.SKP_int16_MAX / (psDec.LPC_order + 1) );
	    NLSF_acc_Q15 = 0;
	    for( i = 0; i < psDec.LPC_order; i++ ) {
	        NLSF_acc_Q15 += NLSF_step_Q15;
	        psDec.sCNG.CNG_smth_NLSF_Q15[ i ] = NLSF_acc_Q15;
	    }
	    psDec.sCNG.CNG_smth_Gain_Q16 = 0;
	    psDec.sCNG.rand_seed = 3176576;
	}

	/* Updates CNG estimate, and applies the CNG when packet was lost   */
//	void SKP_Silk_CNG(
//	    SKP_Silk_decoder_state      *psDec,             /* I/O  Decoder state                               */
//	    SKP_Silk_decoder_control    *psDecCtrl,         /* I/O  Decoder control                             */
//	    short                   signal[],           /* I/O  Signal                                      */
//	    int                     length              /* I    Length of residual                          */
//	)
	static void SKP_Silk_CNG(
		    SKP_Silk_decoder_state      psDec,             /* I/O  Decoder state                               */
		    SKP_Silk_decoder_control    psDecCtrl,         /* I/O  Decoder control                             */
		    short                       signal[],           /* I/O  Signal                                      */
		    int							signal_offset,
		    int                         length              /* I    Length of residual                          */
	)
	{
	    int   i, subfr;
	    int tmp_32, Gain_Q26, max_Gain_Q16;
//	    short LPC_buf[ MAX_LPC_ORDER ];
	    short[] LPC_buf = new short[Silk_define.MAX_LPC_ORDER];
//	    short CNG_sig[ MAX_FRAME_LENGTH ];
	    short[] CNG_sig = new short[Silk_define.MAX_FRAME_LENGTH];
	    
//	    SKP_Silk_CNG_struct *psCNG;
	    SKP_Silk_CNG_struct  psCNG;
	    
	    psCNG = psDec.sCNG;

	    if( psDec.fs_kHz != psCNG.fs_kHz ) {
	        /* Reset state */
	        SKP_Silk_CNG_Reset( psDec );

	        psCNG.fs_kHz = psDec.fs_kHz;
	    }
	    if( psDec.lossCnt == 0 && psDec.vadFlag == Silk_define.NO_VOICE_ACTIVITY ) {
	        /* Update CNG parameters */

	        /* Smoothing of LSF's  */
	        for( i = 0; i < psDec.LPC_order; i++ ) {
//	            psCNG.CNG_smth_NLSF_Q15[ i ] += SKP_SMULWB( psDec.prevNLSF_Q15[ i ] - psCNG.CNG_smth_NLSF_Q15[ i ], CNG_NLSF_SMTH_Q16 );
	        	psCNG.CNG_smth_NLSF_Q15[ i ] += Silk_macros.SKP_SMULWB( psDec.prevNLSF_Q15[ i ] - psCNG.CNG_smth_NLSF_Q15[ i ], Silk_define.CNG_NLSF_SMTH_Q16 );
	        }
	        /* Find the subframe with the highest gain */
	        max_Gain_Q16 = 0;
	        subfr        = 0;
	        for( i = 0; i < Silk_define.NB_SUBFR; i++ ) {
	            if( psDecCtrl.Gains_Q16[ i ] > max_Gain_Q16 ) {
	                max_Gain_Q16 = psDecCtrl.Gains_Q16[ i ];
	                subfr        = i;
	            }
	        }
	        /* Update CNG excitation buffer with excitation from this subframe */
	   //     SKP_memmove( &psCNG.CNG_exc_buf_Q10[ psDec.subfr_length ], psCNG.CNG_exc_buf_Q10, ( NB_SUBFR - 1 ) * psDec.subfr_length * sizeof( int ) );
	        System.arraycopy(psCNG.CNG_exc_buf_Q10, 0, psCNG.CNG_exc_buf_Q10, psDec.subfr_length, ( Silk_define.NB_SUBFR - 1 ) * psDec.subfr_length);
	   //     SKP_memcpy(   psCNG.CNG_exc_buf_Q10, &psDec.exc_Q10[ subfr * psDec.subfr_length ], psDec.subfr_length * sizeof( int ) );
	        System.arraycopy(psDec.exc_Q10, subfr * psDec.subfr_length , psCNG.CNG_exc_buf_Q10, 0, psDec.subfr_length);
	        /* Smooth gains */
	        for( i = 0; i < Silk_define.NB_SUBFR; i++ ) {
//	            psCNG.CNG_smth_Gain_Q16 += SKP_SMULWB( psDecCtrl.Gains_Q16[ i ] - psCNG.CNG_smth_Gain_Q16, CNG_GAIN_SMTH_Q16 );
	        	psCNG.CNG_smth_Gain_Q16 += Silk_macros.SKP_SMULWB( psDecCtrl.Gains_Q16[ i ] - psCNG.CNG_smth_Gain_Q16, Silk_define.CNG_GAIN_SMTH_Q16 );
	        }
	    }

	    /* Add CNG when packet is lost and / or when low speech activity */
	    if( psDec.lossCnt != 0 ) {//|| psDec.vadFlag == NO_VOICE_ACTIVITY ) {

	        /* Generate CNG excitation */
//	        SKP_Silk_CNG_exc( CNG_sig, psCNG.CNG_exc_buf_Q10, 
//	                psCNG.CNG_smth_Gain_Q16, length, &psCNG.rand_seed );
	    	int[] psCNG_rand_seed_ptr = new int[1];
	    	psCNG_rand_seed_ptr[0] = psCNG.rand_seed;
	    	
	    	 SKP_Silk_CNG_exc( CNG_sig, 0,  psCNG.CNG_exc_buf_Q10, 0,
		                psCNG.CNG_smth_Gain_Q16, length, psCNG_rand_seed_ptr );
	    	 psCNG.rand_seed = psCNG_rand_seed_ptr[0];
	    	 
	        /* Convert CNG NLSF to filter representation */
//	        SKP_Silk_NLSF2A_stable( LPC_buf, psCNG.CNG_smth_NLSF_Q15, psDec.LPC_order );
	    	Silk_NLSF2A_stable.SKP_Silk_NLSF2A_stable( LPC_buf, psCNG.CNG_smth_NLSF_Q15, psDec.LPC_order );

	        Gain_Q26 = ( int )1 << 26; /* 1.0 */
	        
	        /* Generate CNG signal, by synthesis filtering */
	        if( psDec.LPC_order == 16 ) {
//	            SKP_Silk_LPC_synthesis_order16( CNG_sig, LPC_buf, 
//	                Gain_Q26, psCNG.CNG_synth_state, CNG_sig, length );
	            Silk_LPC_synthesis_order16.SKP_Silk_LPC_synthesis_order16( CNG_sig, LPC_buf, 
		                Gain_Q26, psCNG.CNG_synth_state, CNG_sig, length );
	        } else {
//	            SKP_Silk_LPC_synthesis_filter( CNG_sig, LPC_buf, 
//	                Gain_Q26, psCNG.CNG_synth_state, CNG_sig, length, psDec.LPC_order );
	            Silk_LPC_synthesis_filter.SKP_Silk_LPC_synthesis_filter( CNG_sig, LPC_buf, 
		                Gain_Q26, psCNG.CNG_synth_state, CNG_sig, length, psDec.LPC_order );
	        }
	        /* Mix with signal */
	        for( i = 0; i < length; i++ ) {
	            tmp_32 = signal[ signal_offset + i ] + CNG_sig[ i ];
	            signal[ signal_offset+i ] = (short) Silk_SigProc_FIX.SKP_SAT16( tmp_32 );
	        }
	    } else {
//	        SKP_memset( psCNG.CNG_synth_state, 0, psDec.LPC_order *  sizeof( int ) );
	    	Arrays.fill(psCNG.CNG_synth_state,0, psDec.LPC_order,0);
	    }
	}

}
