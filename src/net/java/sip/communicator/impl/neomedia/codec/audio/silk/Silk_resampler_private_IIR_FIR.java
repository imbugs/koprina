/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_resampler_private_IIR_FIR" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_resampler_private_IIR_FIR.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_resampler_private_IIR_FIR
{
	/* Upsample using a combination of allpass-based 2x upsampling and FIR interpolation */
	static void SKP_Silk_resampler_private_IIR_FIR(
//		void	                        *SS,		    
//djinn V817		SKP_Silk_resampler_state_struct	S,              /* I/O: Resampler state 						*/
		Object							SS,              /* I/O: Resampler state 						*/
		short[]						    out,		    /* O:	Output signal 							*/
		int out_offset,
		short[]     					in, 		    /* I:	Input signal							*/
		int in_offset,
		int					            inLen		    /* I:	Number of input samples					*/
	)
	{
//	    SKP_Silk_resampler_state_struct *S = (SKP_Silk_resampler_state_struct *)SS;
//djinn v817
		SKP_Silk_resampler_state_struct S = (SKP_Silk_resampler_state_struct )SS;
		
		int nSamplesIn, table_index;
		int max_index_Q16, index_Q16, index_increment_Q16, res_Q15;
		short[] buf = new short[ 2 * Silk_resampler_private.RESAMPLER_MAX_BATCH_SIZE_IN + Silk_resampler_rom.RESAMPLER_ORDER_FIR_144 ];
	    int buf_ptr;

		/* Copy buffered samples to start of buffer */	
//		SKP_memcpy( buf, S->sFIR, RESAMPLER_ORDER_FIR_144 * sizeof( SKP_int32 ) );
	    for(int i_djinn=0; i_djinn<Silk_resampler_rom.RESAMPLER_ORDER_FIR_144; i_djinn++)
	    {
	    	buf[2*i_djinn] = (short)(S.sFIR[i_djinn]>>>16);
	    	buf[2*i_djinn+1] = (short)(S.sFIR[i_djinn]&0x0000FFFF);
	    }

		/* Iterate over blocks of frameSizeIn input samples */
	    index_increment_Q16 = S.invRatio_Q16;
		while( true ) 
		{
			nSamplesIn = Math.min( inLen, S.batchSize );

	        if( S.input2x == 1 ) 
	        {
			    /* Upsample 2x */
//djinn v817	            S->up2_function( S->sIIR, &buf[ RESAMPLER_ORDER_FIR_144 ], in, nSamplesIn );
	        	S.up2_function(S.sIIR, buf, Silk_resampler_rom.RESAMPLER_ORDER_FIR_144, in, in_offset, nSamplesIn);
	        } 
	        else
	        {
			    /* Fourth-order ARMA filter */
	        	Silk_resampler_private_ARMA4.SKP_Silk_resampler_private_ARMA4( S.sIIR,0, buf,Silk_resampler_rom.RESAMPLER_ORDER_FIR_144, in,in_offset, S.Coefs,0, nSamplesIn );
	        }

	        max_index_Q16 = nSamplesIn << ( 16 + S.input2x );         /* +1 if 2x upsampling */

			/* Interpolate upsampled signal and store in output array */
		    for( index_Q16 = 0; index_Q16 < max_index_Q16; index_Q16 += index_increment_Q16 ) 
		    {
	            table_index = Silk_macros.SKP_SMULWB( index_Q16 & 0xFFFF, 144 );
//	            buf_ptr = &buf[ index_Q16 >> 16 ];
	            buf_ptr = index_Q16 >> 16;
	            res_Q15 = Silk_macros.SKP_SMULBB(          buf[ buf_ptr   ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[       table_index ][ 0 ] );
	            res_Q15 = Silk_macros.SKP_SMLABB( res_Q15, buf[ buf_ptr+1 ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[       table_index ][ 1 ] );
	            res_Q15 = Silk_macros.SKP_SMLABB( res_Q15, buf[ buf_ptr+2 ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[       table_index ][ 2 ] );
	            res_Q15 = Silk_macros.SKP_SMLABB( res_Q15, buf[ buf_ptr+3 ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[ 143 - table_index ][ 2 ] );
	            res_Q15 = Silk_macros.SKP_SMLABB( res_Q15, buf[ buf_ptr+4 ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[ 143 - table_index ][ 1 ] );
	            res_Q15 = Silk_macros.SKP_SMLABB( res_Q15, buf[ buf_ptr+5 ], Silk_resampler_rom.SKP_Silk_resampler_frac_FIR_144[ 143 - table_index ][ 0 ] );
				out[out_offset++] = (short)Silk_SigProc_FIX.SKP_SAT16( Silk_SigProc_FIX.SKP_RSHIFT_ROUND( res_Q15, 15 ) );
		    }
			in_offset += nSamplesIn;
			inLen -= nSamplesIn;

			if( inLen > 0 ) 
			{
				/* More iterations to do; copy last part of filtered signal to beginning of buffer */
//				SKP_memcpy( buf, &buf[ nSamplesIn << S->input2x ], RESAMPLER_ORDER_FIR_144 * sizeof( SKP_int32 ) );
				for(int i_djinn=0; i_djinn<Silk_resampler_rom.RESAMPLER_ORDER_FIR_144; i_djinn++)
			    	buf[i_djinn] = buf[(nSamplesIn << S.input2x) + i_djinn];
			} 
			else 
			{
				break;
			}
		}

		/* Copy last part of filtered signal to the state for the next call */
//		SKP_memcpy( S->sFIR, &buf[nSamplesIn << S->input2x ], RESAMPLER_ORDER_FIR_144 * sizeof( SKP_int32 ) );
		for(int i_djinn=0; i_djinn<Silk_resampler_rom.RESAMPLER_ORDER_FIR_144; i_djinn++)
		{
	    	S.sFIR[i_djinn] = (int)buf[(nSamplesIn << S.input2x) + 2*i_djinn] << 16;
	    	S.sFIR[i_djinn] |= (int)buf[(nSamplesIn << S.input2x) + 2*i_djinn+1] & 0x0000FFFF;
		}
	}
}



