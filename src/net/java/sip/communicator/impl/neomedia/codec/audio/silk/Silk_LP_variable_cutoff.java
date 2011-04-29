/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_HP_variable_cutoff" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_HP_variable_cutoff.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_LP_variable_cutoff 
{
	/* Helper function, that interpolates the filter taps */
	static void SKP_Silk_LP_interpolate_filter_taps( 
//	    SKP_int32           B_Q28[ TRANSITION_NB ],
		int[] B_Q28,
//	    SKP_int32           A_Q28[ TRANSITION_NA ],
		int[] A_Q28,
	    final int       ind,
	    final int     fac_Q16
	)
	{
	    int nb, na;

	    if( ind < Silk_define.TRANSITION_INT_NUM - 1 ) 
	    {
	        if( fac_Q16 > 0 ) 
	        {
	            if( fac_Q16 == Silk_SigProc_FIX.SKP_SAT16( fac_Q16 ) ) 
	            { /* fac_Q16 is in range of a 16-bit int */
	                /* Piece-wise linear interpolation of B and A */
	                for( nb = 0; nb < Silk_define.TRANSITION_NB; nb++ ) 
	                {
	                    B_Q28[ nb ] = Silk_macros.SKP_SMLAWB(
	                        Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind     ][ nb ],
	                        Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind + 1 ][ nb ] -
	                        Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind     ][ nb ],
	                        fac_Q16 );
	                }
	                for( na = 0; na < Silk_define.TRANSITION_NA; na++ ) 
	                {
	                    A_Q28[ na ] = Silk_macros.SKP_SMLAWB(
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind     ][ na ],
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind + 1 ][ na ] -
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind     ][ na ],
	                        fac_Q16 );
	                }
	            } 
	            else if( fac_Q16 == ( 1 << 15 ) )
	            { /* Neither fac_Q16 nor ( ( 1 << 16 ) - fac_Q16 ) is in range of a 16-bit int */

	                /* Piece-wise linear interpolation of B and A */
	                for( nb = 0; nb < Silk_define.TRANSITION_NB; nb++ ) 
	                {
	                    B_Q28[ nb ] = Silk_SigProc_FIX.SKP_RSHIFT( 
	                    	Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind     ][ nb ] +
	                    	Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind + 1 ][ nb ],
	                        1 );
	                }
	                for( na = 0; na < Silk_define.TRANSITION_NA; na++ ) 
	                {
	                    A_Q28[ na ] = Silk_SigProc_FIX.SKP_RSHIFT( 
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind     ][ na ] + 
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind + 1 ][ na ], 
	                        1 );
	                }
	            }
	            else 
	            { /* ( ( 1 << 16 ) - fac_Q16 ) is in range of a 16-bit int */
	                
	                assert( ( ( 1 << 16 ) - fac_Q16 ) == Silk_SigProc_FIX.SKP_SAT16( ( ( 1 << 16 ) - fac_Q16) ) );
	                /* Piece-wise linear interpolation of B and A */
	                for( nb = 0; nb < Silk_define.TRANSITION_NB; nb++ ) 
	                {
	                    B_Q28[ nb ] = Silk_macros.SKP_SMLAWB(
	                    	Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind + 1 ][ nb ],
	                    	Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind     ][ nb ] -
	                    	Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind + 1 ][ nb ],
	                        ( 1 << 16 ) - fac_Q16 );
	                }
	                for( na = 0; na < Silk_define.TRANSITION_NA; na++ ) 
	                {
	                    A_Q28[ na ] = Silk_macros.SKP_SMLAWB(
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind + 1 ][ na ],
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind     ][ na ] -
	                    	Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind + 1 ][ na ],
	                        ( 1 << 16 ) - fac_Q16 );
	                }
	            }
	        } 
	        else 
	        {
//	            SKP_memcpy( B_Q28, SKP_Silk_Transition_LP_B_Q28[ ind ], TRANSITION_NB * sizeof( SKP_int32 ) );
	        	for(int i_djinn=0; i_djinn<Silk_define.TRANSITION_NB; i_djinn++)
	        		B_Q28[i_djinn] = Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ ind ][i_djinn];
//	            SKP_memcpy( A_Q28, SKP_Silk_Transition_LP_A_Q28[ ind ], TRANSITION_NA * sizeof( SKP_int32 ) );
	        	for(int i_djinn=0; i_djinn<Silk_define.TRANSITION_NA; i_djinn++)
	        		A_Q28[i_djinn] = Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ ind ][i_djinn];
	        }
	    } 
	    else 
	    {
//	        SKP_memcpy( B_Q28, SKP_Silk_Transition_LP_B_Q28[ TRANSITION_INT_NUM - 1 ], TRANSITION_NB * sizeof( SKP_int32 ) );
	    	for(int i_djinn=0; i_djinn<Silk_define.TRANSITION_NB; i_djinn++)
        		B_Q28[i_djinn] = Silk_tables_other.SKP_Silk_Transition_LP_B_Q28[ Silk_define.TRANSITION_INT_NUM - 1 ][i_djinn];
//	        SKP_memcpy( A_Q28, SKP_Silk_Transition_LP_A_Q28[ TRANSITION_INT_NUM - 1 ], TRANSITION_NA * sizeof( SKP_int32 ) );
	    	for(int i_djinn=0; i_djinn<Silk_define.TRANSITION_NB; i_djinn++)
	    		A_Q28[i_djinn] = Silk_tables_other.SKP_Silk_Transition_LP_A_Q28[ Silk_define.TRANSITION_INT_NUM - 1 ][i_djinn];
	    }
	}

	/* Low-pass filter with variable cutoff frequency based on  */
	/* piece-wise linear interpolation between elliptic filters */
	/* Start by setting psEncC->transition_frame_no = 1;            */
	/* Deactivate by setting psEncC->transition_frame_no = 0;   */
	static void SKP_Silk_LP_variable_cutoff(
	    SKP_Silk_LP_state               psLP,          /* I/O  LP filter state                     */
	    short[]                         out,           /* O    Low-pass filtered output signal     */
	    int out_offset,
	    short[]                         in,            /* I    Input signal                        */
	    int in_offset,
	    final int                       frame_length    /* I    Frame length                        */
	)
	{
	    int[]   B_Q28 = new int[ Silk_define.TRANSITION_NB ], A_Q28 = new int[ Silk_define.TRANSITION_NA ]; 
	    int fac_Q16 = 0;
	    int     ind = 0;

	    assert( psLP.transition_frame_no >= 0 );
	    assert( ( ( ( psLP.transition_frame_no <= Silk_define.TRANSITION_FRAMES_DOWN ) && ( psLP.mode == 0 ) ) || 
	                  ( ( psLP.transition_frame_no <= Silk_define.TRANSITION_FRAMES_UP   ) && ( psLP.mode == 1 ) ) ) );

	    /* Interpolate filter coefficients if needed */
	    if( psLP.transition_frame_no > 0 ) 
	    {
	        if( psLP.mode == 0 ) 
	        {
	            if( psLP.transition_frame_no < Silk_define.TRANSITION_FRAMES_DOWN ) 
	            {
	                /* Calculate index and interpolation factor for interpolation */
//	#if( TRANSITION_INT_STEPS_DOWN == 32 )
	            	if( Silk_define.TRANSITION_INT_STEPS_DOWN == 32 )
	                fac_Q16 = psLP.transition_frame_no << ( 16 - 5 );
//	#else
	            	else
	                fac_Q16 = ( psLP.transition_frame_no << 16 ) / Silk_define.TRANSITION_INT_STEPS_DOWN ;
//	#endif
	                ind      = fac_Q16 >> 16;
	                fac_Q16 -= ind << 16;

	                assert( ind >= 0 );
	                assert( ind < Silk_define.TRANSITION_INT_NUM );

	                /* Interpolate filter coefficients */
	                SKP_Silk_LP_interpolate_filter_taps( B_Q28, A_Q28, ind, fac_Q16 );

	                /* Increment transition frame number for next frame */
	                psLP.transition_frame_no++;

	            } 
	            else if( psLP.transition_frame_no == Silk_define.TRANSITION_FRAMES_DOWN ) 
	            {
	                /* End of transition phase */
	                SKP_Silk_LP_interpolate_filter_taps( B_Q28, A_Q28, Silk_define.TRANSITION_INT_NUM - 1, 0 );
	            }
	        } 
	        else if( psLP.mode == 1 ) 
	        {
	            if( psLP.transition_frame_no < Silk_define.TRANSITION_FRAMES_UP ) 
	            {
	                /* Calculate index and interpolation factor for interpolation */
//	#if( TRANSITION_INT_STEPS_UP == 64 )
	            	if( Silk_define.TRANSITION_INT_STEPS_UP == 64 )
	                fac_Q16 = ( Silk_define.TRANSITION_FRAMES_UP - psLP.transition_frame_no ) << ( 16 - 6 );
//	#else
	                else
	                fac_Q16 = ( ( Silk_define.TRANSITION_FRAMES_UP - psLP.transition_frame_no ) << 16 ) / Silk_define.TRANSITION_INT_STEPS_UP;
//	#endif
	                ind      = fac_Q16 >> 16;
	                fac_Q16 -= ind << 16;

	                assert( ind >= 0 );
	                assert( ind < Silk_define.TRANSITION_INT_NUM );

	                /* Interpolate filter coefficients */
	                SKP_Silk_LP_interpolate_filter_taps( B_Q28, A_Q28, ind, fac_Q16 );

	                /* Increment transition frame number for next frame */
	                psLP.transition_frame_no++;
	            
	            } 
	            else if( psLP.transition_frame_no == Silk_define.TRANSITION_FRAMES_UP ) 
	            {
	                /* End of transition phase */
	                SKP_Silk_LP_interpolate_filter_taps( B_Q28, A_Q28, 0, 0 );
	            }
	        }
	    } 
	    
	    if( psLP.transition_frame_no > 0 ) 
	    {
	        /* ARMA low-pass filtering */
	        assert( Silk_define.TRANSITION_NB == 3 && Silk_define.TRANSITION_NA == 2 );
	        Silk_biquad_alt.SKP_Silk_biquad_alt( in,in_offset, B_Q28, A_Q28, psLP.In_LP_State, out,out_offset, frame_length );
	    }
	    else 
	    {
	        /* Instead of using the filter, copy input directly to output */
//	        SKP_memcpy( out, in, frame_length * sizeof( SKP_int16 ) );
	    	for(int i_djinn=0; i_djinn<frame_length; i_djinn++)
        		out[out_offset+i_djinn] = in[in_offset+i_djinn];
	    }
	}
}



