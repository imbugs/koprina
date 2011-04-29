/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_autocorrelation_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_autocorrelation_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_autocorrelation_FLP 
{
	/* compute autocorrelation */
	static void SKP_Silk_autocorrelation_FLP( 
	    float[]       results,           /* O    result (length correlationCount)            */
	    int results_offset,
	    float[]       inputData,         /* I    input data to correlate                     */
	    int inputData_offset,
	    int         inputDataSize,      /* I    length of input                             */
	    int         correlationCount    /* I    number of correlation taps to compute       */
	)
	{
	    int i;

	    if ( correlationCount > inputDataSize )
	    {
	        correlationCount = inputDataSize;
	    }

	    for( i = 0; i < correlationCount; i++ ) 
	    {
	        results[ results_offset+i ] =  (float)Silk_inner_product_FLP.SKP_Silk_inner_product_FLP( inputData,inputData_offset, inputData,inputData_offset + i, inputDataSize - i );
	    }
	}
}


