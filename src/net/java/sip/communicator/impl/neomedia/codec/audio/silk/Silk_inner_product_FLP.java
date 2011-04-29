/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_inner_product_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_inner_product_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author 
 *
 */
public class Silk_inner_product_FLP 
{
	/* inner product of two SKP_float arrays, with result as double     */
	static double SKP_Silk_inner_product_FLP(    /* O    result              */
	    float[]     data1,         /* I    vector 1            */
	    int data1_offset,
	    float[]     data2,         /* I    vector 2            */
	    int data2_offset,
	    int         dataSize       /* I    length of vectors   */
	)
	{
	    int  i, dataSize4;
	    double   result;

	    /* 4x unrolled loop */
	    result = 0.0f;
	    dataSize4 = dataSize & 0xFFFC;
	    for( i = 0; i < dataSize4; i += 4 ) 
	    {
	        result += data1[ data1_offset + i + 0 ] * data2[ data2_offset + i + 0 ] + 
	                  data1[ data1_offset + i + 1 ] * data2[ data2_offset + i + 1 ] +
	                  data1[ data1_offset + i + 2 ] * data2[ data2_offset + i + 2 ] +
	                  data1[ data1_offset + i + 3 ] * data2[ data2_offset + i + 3 ];
	    }

	    /* add any remaining products */
	    for( ; i < dataSize; i++ ) 
	    {
	        result += data1[ data1_offset+i ] * data2[ data2_offset+i ];
	    }

	    return result;
	}
}



