/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_LPC_analysis_filter_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_LPC_analysis_filter_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_LPC_analysis_filter_FLP 
{
	/*******************************************/
	/* LPC analysis filter                     */
	/* NB! State is kept internally and the    */
	/* filter always starts with zero state    */
	/* first Order output samples are not set  */
	/*******************************************/

	static void SKP_Silk_LPC_analysis_filter_FLP(
	          float                 r_LPC[],            /* O    LPC residual signal                     */
	          float                 PredCoef[],         /* I    LPC coefficients                        */
	          float                 s[],                /* I    Input signal                            */
	    final int                   length,             /* I    Length of input signal                  */
	    final int                   Order               /* I    LPC order                               */
	)
	{
	    assert( Order <= length );

	    switch( Order ) 
	    {
	        case 8:
	            SKP_Silk_LPC_analysis_filter8_FLP(  r_LPC, PredCoef, s, length );
	        break;

	        case 10:
	            SKP_Silk_LPC_analysis_filter10_FLP( r_LPC, PredCoef, s, length );
	        break;

	        case 12:
	            SKP_Silk_LPC_analysis_filter12_FLP( r_LPC, PredCoef, s, length );
	        break;

	        case 16:
	            SKP_Silk_LPC_analysis_filter16_FLP( r_LPC, PredCoef, s, length );
	        break;

	        default:
	            assert( false );
	        break;
	    }

	    /* Set first LPC Order samples to zero instead of undefined */
//	    SKP_memset( r_LPC, 0, Order * sizeof( SKP_float ) );
	    for(int i_djinn=0; i_djinn<Order; i_djinn++)
	    	r_LPC[i_djinn] = 0;
	}

	/* 16th order LPC analysis filter, does not write first 16 samples */
	static void SKP_Silk_LPC_analysis_filter16_FLP(
	          float                 r_LPC[],            /* O    LPC residual signal                     */
	          float                 PredCoef[],         /* I    LPC coefficients                        */
	          float                 s[],                /* I    Input signal                            */
	    final int                   length              /* I    Length of input signal                  */
	)
	{
	    int   ix = 16;
	    float LPC_pred;
	    float[] s_ptr;
	    int s_ptr_offset;

	    for ( ; ix < length; ix++) 
	    {
//	        s_ptr = &s[ix - 1];
	    	s_ptr = s;
	    	s_ptr_offset = ix - 1;

	        /* short-term prediction */
	        LPC_pred = s_ptr[ s_ptr_offset ]   * PredCoef[ 0 ]  + 
	                   s_ptr[s_ptr_offset-1]  * PredCoef[ 1 ]  +
	                   s_ptr[s_ptr_offset-2]  * PredCoef[ 2 ]  +
	                   s_ptr[s_ptr_offset-3]  * PredCoef[ 3 ]  +
	                   s_ptr[s_ptr_offset-4]  * PredCoef[ 4 ]  +
	                   s_ptr[s_ptr_offset-5]  * PredCoef[ 5 ]  +
	                   s_ptr[s_ptr_offset-6]  * PredCoef[ 6 ]  +
	                   s_ptr[s_ptr_offset-7]  * PredCoef[ 7 ]  +
	                   s_ptr[s_ptr_offset-8]  * PredCoef[ 8 ]  +
	                   s_ptr[s_ptr_offset-9]  * PredCoef[ 9 ]  +
	                   s_ptr[s_ptr_offset-10] * PredCoef[ 10 ] +
	                   s_ptr[s_ptr_offset-11] * PredCoef[ 11 ] +
	                   s_ptr[s_ptr_offset-12] * PredCoef[ 12 ] +
	                   s_ptr[s_ptr_offset-13] * PredCoef[ 13 ] +
	                   s_ptr[s_ptr_offset-14] * PredCoef[ 14 ] +
	                   s_ptr[s_ptr_offset-15] * PredCoef[ 15 ];

	        /* prediction error */
	        r_LPC[ix] = s_ptr[ s_ptr_offset+1 ] - LPC_pred;
	    }
	}

	/* 12th order LPC analysis filter, does not write first 12 samples */
	static void SKP_Silk_LPC_analysis_filter12_FLP(
			  float                 r_LPC[],            /* O    LPC residual signal                     */
	          float                 PredCoef[],         /* I    LPC coefficients                        */
	          float                 s[],                /* I    Input signal                            */
	    final int                   length              /* I    Length of input signal                  */
	)
	{
	    int   ix = 12;
	    float LPC_pred;
	    float[] s_ptr;
	    int s_ptr_offset;

	    for ( ; ix < length; ix++) 
	    {
//	        s_ptr = &s[ix - 1];
	    	s_ptr = s;
	    	s_ptr_offset = ix - 1;

	        /* short-term prediction */
	        LPC_pred = s_ptr[ s_ptr_offset ]   * PredCoef[ 0 ]  + 
	                   s_ptr[s_ptr_offset-1]  * PredCoef[ 1 ]  +
	                   s_ptr[s_ptr_offset-2]  * PredCoef[ 2 ]  +
	                   s_ptr[s_ptr_offset-3]  * PredCoef[ 3 ]  +
	                   s_ptr[s_ptr_offset-4]  * PredCoef[ 4 ]  +
	                   s_ptr[s_ptr_offset-5]  * PredCoef[ 5 ]  +
	                   s_ptr[s_ptr_offset-6]  * PredCoef[ 6 ]  +
	                   s_ptr[s_ptr_offset-7]  * PredCoef[ 7 ]  +
	                   s_ptr[s_ptr_offset-8]  * PredCoef[ 8 ]  +
	                   s_ptr[s_ptr_offset-9]  * PredCoef[ 9 ]  +
	                   s_ptr[s_ptr_offset-10] * PredCoef[ 10 ] +
	                   s_ptr[s_ptr_offset-11] * PredCoef[ 11 ];

	        /* prediction error */
	        r_LPC[ix] = s_ptr[ s_ptr_offset+1 ] - LPC_pred;
	    }
	}

	/* 10th order LPC analysis filter, does not write first 10 samples */
	static void SKP_Silk_LPC_analysis_filter10_FLP(
			float                 r_LPC[],            /* O    LPC residual signal                     */
			float                 PredCoef[],         /* I    LPC coefficients                        */
			float                 s[],                /* I    Input signal                            */
		final int                 length              /* I    Length of input signal                  */
	)
	{
	    int   ix = 10;
	    float LPC_pred;
	    float[] s_ptr;
	    int s_ptr_offset;

	    for ( ; ix < length; ix++) {
//	        s_ptr = &s[ix - 1];
	    	s_ptr = s;
	    	s_ptr_offset = ix - 1;

	        /* short-term prediction */
	        LPC_pred = s_ptr[ s_ptr_offset ]   * PredCoef[ 0 ]  + 
	                   s_ptr[s_ptr_offset-1]  * PredCoef[ 1 ]  +
	                   s_ptr[s_ptr_offset-2]  * PredCoef[ 2 ]  +
	                   s_ptr[s_ptr_offset-3]  * PredCoef[ 3 ]  +
	                   s_ptr[s_ptr_offset-4]  * PredCoef[ 4 ]  +
	                   s_ptr[s_ptr_offset-5]  * PredCoef[ 5 ]  +
	                   s_ptr[s_ptr_offset-6]  * PredCoef[ 6 ]  +
	                   s_ptr[s_ptr_offset-7]  * PredCoef[ 7 ]  +
	                   s_ptr[s_ptr_offset-8]  * PredCoef[ 8 ]  +
	                   s_ptr[s_ptr_offset-9]  * PredCoef[ 9 ];

	        /* prediction error */
	        r_LPC[ix] = s_ptr[ s_ptr_offset+1 ] - LPC_pred;
	    }
	}

	/* 8th order LPC analysis filter, does not write first 8 samples */
	static void SKP_Silk_LPC_analysis_filter8_FLP(
			  float                 r_LPC[],            /* O    LPC residual signal                     */
	          float                 PredCoef[],         /* I    LPC coefficients                        */
	          float                 s[],                /* I    Input signal                            */
	    final int                   length              /* I    Length of input signal                  */
	)
	{
	    int   ix = 8;
	    float LPC_pred;
	    float[] s_ptr;
	    int s_ptr_offset;

	    for ( ; ix < length; ix++) {
//	        s_ptr = &s[ix - 1];
	    	s_ptr = s;
	    	s_ptr_offset = ix - 1;

	        /* short-term prediction */
	        LPC_pred = s_ptr[  s_ptr_offset ] * PredCoef[ 0 ]  + 
	                   s_ptr[ s_ptr_offset-1 ] * PredCoef[ 1 ]  +
	                   s_ptr[ s_ptr_offset-2 ] * PredCoef[ 2 ]  +
	                   s_ptr[ s_ptr_offset-3 ] * PredCoef[ 3 ]  +
	                   s_ptr[ s_ptr_offset-4 ] * PredCoef[ 4 ]  +
	                   s_ptr[ s_ptr_offset-5 ] * PredCoef[ 5 ]  +
	                   s_ptr[ s_ptr_offset-6 ] * PredCoef[ 6 ]  +
	                   s_ptr[ s_ptr_offset-7 ] * PredCoef[ 7 ];

	        /* prediction error */
	        r_LPC[ix] = s_ptr[ s_ptr_offset+1 ] - LPC_pred;
	    }
	}
}


