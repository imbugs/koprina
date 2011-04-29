/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_code_signs" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_code_signs.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author 
 *
 */
public class Silk_code_signs 
{

	//#define SKP_enc_map(a)                ((a) > 0 ? 1 : 0)
	//#define SKP_dec_map(a)                ((a) > 0 ? 1 : -1)
	/* shifting avoids if-statement */
//	#define SKP_enc_map(a)                  ( SKP_RSHIFT( (a), 15 ) + 1 )
	static int SKP_enc_map(int a)
	{
		return (a>>15)+1;
	}

//	#define SKP_dec_map(a)                  ( SKP_LSHIFT( (a),  1 ) - 1 )
	static int SKP_dec_map(int a)
	{
		return (a<<1)-1;
	}

	/* Encodes signs of excitation */
	void SKP_Silk_encode_signs(
	    SKP_Silk_range_coder_state      sRC,               /* I/O  Range coder state                       */
	    byte[]                      q,                  /* I    Pulse signal                            */
	    final int                   length,             /* I    Length of input                         */
	    final int                   sigtype,            /* I    Signal type                             */
	    final int                   QuantOffsetType,    /* I    Quantization offset type                */
	    final int                   RateLevelIndex      /* I    Rate level index                        */
	)
	{
	    int i;
	    int inData;
//	    SKP_uint16 cdf[ 3 ];
	    int[] cdf = new int[3];

	    i = Silk_macros.SKP_SMULBB( Silk_define.N_RATE_LEVELS - 1, ( sigtype << 1 ) + QuantOffsetType ) + RateLevelIndex;
	    cdf[ 0 ] = 0;
	    cdf[ 1 ] = Silk_tables_sign.SKP_Silk_sign_CDF[ i ];
	    cdf[ 2 ] = 65535;
	    
	    for( i = 0; i < length; i++ ) 
	    {
	        if( q[ i ] != 0 )
	        {
	            inData = SKP_enc_map( q[ i ] ); /* - = 0, + = 1 */
//djinn v817	            
	            Silk_range_coder.SKP_Silk_range_encoder( sRC, inData, cdf, 0 );
	        }
	    }
	}

	/* Decodes signs of excitation */
//	void SKP_Silk_decode_signs(
//	    SKP_Silk_range_coder_state      *sRC,               /* I/O  Range coder state                           */
//	    SKP_int                         q[],                /* I/O  pulse signal                                */
//	    const SKP_int                   length,             /* I    length of output                            */
//	    const SKP_int                   sigtype,            /* I    Signal type                                 */
//	    const SKP_int                   QuantOffsetType,    /* I    Quantization offset type                    */
//	    const SKP_int                   RateLevelIndex      /* I    Rate Level Index                            */
//	)
	static void SKP_Silk_decode_signs(
		    SKP_Silk_range_coder_state      sRC,               /* I/O  Range coder state                           */
		    int                         q[],                /* I/O  pulse signal                                */
		    final int                   length,             /* I    length of output                            */
		    final int                   sigtype,            /* I    Signal type                                 */
		    final int                   QuantOffsetType,    /* I    Quantization offset type                    */
		    final int                   RateLevelIndex      /* I    Rate Level Index                            */
		)
	{
	    int i;
	    int data;
	    int data_ptr[] = new int[1];
//	    SKP_uint16 cdf[ 3 ];
	    int[] cdf = new int[3];

//	    i = SKP_SMULBB( N_RATE_LEVELS - 1, SKP_LSHIFT( sigtype, 1 ) + QuantOffsetType ) + RateLevelIndex;
	    i = Silk_macros.SKP_SMULBB( Silk_define.N_RATE_LEVELS - 1, ( sigtype << 1 ) + QuantOffsetType ) + RateLevelIndex;
	    cdf[ 0 ] = 0;
//	    cdf[ 1 ] = SKP_Silk_sign_CDF[ i ];
	    cdf[ 1 ] = Silk_tables_sign.SKP_Silk_sign_CDF[ i ];
	    cdf[ 2 ] = 65535;
	    
	    for( i = 0; i < length; i++ ) {
	        if( q[ i ] > 0 ) {
//	            SKP_Silk_range_decoder( &data, sRC, cdf, 1 );
	        	
	        	Silk_range_coder.SKP_Silk_range_decoder( data_ptr, 0, sRC, cdf, 0, 1 );
	        	data = data_ptr[0];
	            /* attach sign */
	            /* implementation with shift, subtraction, multiplication */
	            q[ i ] *= SKP_dec_map( data );
	        }
	    }
	}


}
