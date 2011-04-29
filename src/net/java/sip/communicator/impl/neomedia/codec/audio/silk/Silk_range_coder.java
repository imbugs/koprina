/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_range_coder" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_range_coder.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author 
 *
 */
public class Silk_range_coder 
{
	/* Range encoder for one symbol */
//	void SKP_Silk_range_encoder(
//	    SKP_Silk_range_coder_state      *psRC,              /* I/O  compressor data structure                   */
//	    const SKP_int                   data,               /* I    uncompressed data                           */
//	    const SKP_uint16                prob[]              /* I    cumulative density functions                */
//	)
	static void SKP_Silk_range_encoder(
		    SKP_Silk_range_coder_state      psRC,               /* I/O  compressor data structure                   */
		    final int                       data,               /* I    uncompressed data                           */
		    int                             prob[],              /* I    cumulative density functions                */
		    int				        		prob_offset                                     	
	)
	{
//	    SKP_uint32 low_Q16, high_Q16;
//	    SKP_uint32 base_tmp, range_Q32;
	    long low_Q16, high_Q16;
	    long base_tmp, range_Q32;

	    /* Copy structure data */
//	    SKP_uint32 base_Q32  = psRC.base_Q32;
	    long base_Q32  = psRC.base_Q32;
//	    SKP_uint32 range_Q16 = psRC.range_Q16;
	    long range_Q16 = psRC.range_Q16;
	    int  bufferIx  = psRC.bufferIx;
//	    SKP_uint8  *buffer   = psRC.buffer;
	    byte[] buffer = psRC.buffer;

	    if( psRC.error!=0 )
	    {
	        return;
	    }

	    /* Update interval */
	    low_Q16  = prob[ prob_offset + data ];
	    high_Q16 = prob[ prob_offset + data + 1 ];
	    base_tmp = base_Q32; /* save current base, to test for carry */
//djinn ??	    
//	    base_Q32 += SKP_MUL_uint( range_Q16, low_Q16 );
//	    range_Q32 = SKP_MUL_uint( range_Q16, high_Q16 - low_Q16 );
	    base_Q32 += ( range_Q16 * low_Q16 ) & 0xFFFFFFFFL;
	    range_Q32 = ( range_Q16 * (high_Q16 - low_Q16) )& 0xFFFFFFFFL;

	    /* Check for carry */
	    if( base_Q32 < base_tmp ) 
	    {
	        /* Propagate carry in buffer */
	        int bufferIx_tmp = bufferIx;
	        while( ( ++buffer[ --bufferIx_tmp ] ) == 0 );
	    }

	    /* Check normalization */
//	    if( range_Q32 & 0xFF000000 ) {
	    if( (range_Q32 & 0xFF000000)!=0 ) 
	    {
	        /* No normalization */
//	        range_Q16 = SKP_RSHIFT_uint( range_Q32, 16 );
	        range_Q16 = ( range_Q32 >> 16 );
	    }
	    else
	    {
//	        if( range_Q32 & 0xFFFF0000 ) 
	        if( (range_Q32 & 0xFFFF0000)!=0 ) 
	        {
	            /* Normalization of 8 bits shift */
//	            range_Q16 = SKP_RSHIFT_uint( range_Q32, 8 );
	            range_Q16 = ( range_Q32 >> 8 );
	        }
	        else 
	        {
	            /* Normalization of 16 bits shift */
	            range_Q16 = range_Q32;
	            /* Make sure not to write beyond buffer */
	            if( bufferIx >= psRC.bufferLength ) 
	            {
	                psRC.error = Silk_define.RANGE_CODER_WRITE_BEYOND_BUFFER;
	                return;
	            }
	            /* Write one byte to buffer */
	            buffer[ bufferIx++ ] = (byte)( base_Q32 >>> 24 );
	            base_Q32 = base_Q32 << 8;
	        }
	        /* Make sure not to write beyond buffer */
	        if( bufferIx >= psRC.bufferLength )
	        {
	            psRC.error = Silk_define.RANGE_CODER_WRITE_BEYOND_BUFFER;
	            return;
	        }
	        /* Write one byte to buffer */
	        buffer[ bufferIx++ ] = (byte)( base_Q32 >>> 24 );
	        base_Q32 = base_Q32 << 8;
	    }

	    /* Copy structure data back */
	    psRC.base_Q32  = base_Q32;
	    psRC.range_Q16 = range_Q16;
	    psRC.bufferIx  = bufferIx;
	}

	/* Range encoder for multiple symbols */
//	static void SKP_Silk_range_encoder_multi(
//	    SKP_Silk_range_coder_state      psRC,              /* I/O  compressor data structure                   */
//	    int[]                   data,             /* I    uncompressed data    [nSymbols]             */
//	    const SKP_uint16 * const        prob[],             /* I    cumulative density functions                */
//	    final int                   nSymbols            /* I    number of data symbols                      */
//	)
	static void SKP_Silk_range_encoder_multi(
		    SKP_Silk_range_coder_state  psRC,              /* I/O  compressor data structure                   */
		    int[]                   	data,             /* I    uncompressed data    [nSymbols]             */
		    int[][]       		    prob,             /* I    cumulative density functions                */
		    final int                   nSymbols            /* I    number of data symbols                      */
		)
	{
	    int k;
	    for( k = 0; k < nSymbols; k++ ) 
	    {
//	        SKP_Silk_range_encoder( psRC, data[ k ], prob[ k ] );
	        SKP_Silk_range_encoder( psRC, data[ k ], prob[ k ], 0 );
	    }
	}

	/* Range decoder for one symbol */
//	void SKP_Silk_range_decoder(
//	    SKP_int                         data[],             /* O    uncompressed data                           */
//	    SKP_Silk_range_coder_state      *psRC,              /* I/O  compressor data structure                   */
//	    const SKP_uint16                prob[],             /* I    cumulative density function                 */
//	    SKP_int                         probIx              /* I    initial (middle) entry of cdf               */
//	)
	static void SKP_Silk_range_decoder(
		    int                             data[],             /* O    uncompressed data                           */
		    int								data_offset,
		    SKP_Silk_range_coder_state      psRC,              /* I/O  compressor data structure                   */
		    final int                       prob[],             /* I    cumulative density function                 */
		    int 							prob_offset,
		    int                             probIx              /* I    initial (middle) entry of cdf               */
	)
	{
	    long low_Q16, high_Q16;
	    long base_tmp, range_Q32;

	    /* Copy structure data */
	    long base_Q32  = psRC.base_Q32;
	    long range_Q16 = psRC.range_Q16;
	    int  bufferIx  = psRC.bufferIx;
//djinn ??	    SKP_uint8  *buffer   = &psRC.buffer[ 4 ];
	    byte[] buffer  = psRC.buffer;
	    int    buffer_offset = 4;

	    if( psRC.error != 0) {
	        /* Set output to zero */
//	        *data = 0;
	    	data[data_offset + 0] = 0;
	        return;
	    }
	    
//    high_Q16 = prob[ probIx ];
	    high_Q16 = prob[prob_offset + probIx];
	    
//djinn ??	    base_tmp = SKP_MUL_uint( range_Q16, high_Q16 );
	    base_tmp = Silk_SigProc_FIX.SKP_MUL_uint( range_Q16, high_Q16 );
	    
	    if( base_tmp > base_Q32 ) {
	        while( true ) {
//djinn ???	            low_Q16 = prob[ --probIx  ];
	            low_Q16 = prob[ --probIx + prob_offset  ];
	            base_tmp = Silk_SigProc_FIX.SKP_MUL_uint( range_Q16, low_Q16 );
	            if( base_tmp <= base_Q32 ) {
	                break;
	            }
	            high_Q16 = low_Q16;
	            /* Test for out of range */
	            if( high_Q16 == 0 ) {
	                psRC.error = Silk_define.RANGE_CODER_CDF_OUT_OF_RANGE;
	                /* Set output to zero */
//	                *data = 0;
	                data[data_offset + 0] = 0;
	                return;
	            }
	        }
	    } else {
	        while( true ) {
	            low_Q16  = high_Q16;
	            high_Q16 = prob[ ++probIx + prob_offset ];
	            base_tmp = Silk_SigProc_FIX.SKP_MUL_uint( range_Q16, high_Q16 );
	            if( base_tmp > base_Q32 ) {
	                probIx--;
	                break;
	            }
	            /* Test for out of range */
	            if( high_Q16 == 0xFFFF ) {
	                psRC.error = Silk_define.RANGE_CODER_CDF_OUT_OF_RANGE;
	                /* Set output to zero */
//	                *data = 0;
	                data[data_offset + 0] = 0;
	                return;
	            }
	        }
	    }
//	    *data = probIx;
	    data[data_offset + 0] = probIx;
	    
	    base_Q32 -= Silk_SigProc_FIX.SKP_MUL_uint( range_Q16, low_Q16 );
	    range_Q32 = Silk_SigProc_FIX.SKP_MUL_uint( range_Q16, high_Q16 - low_Q16 );

	    /* Check normalization */
	    if( (range_Q32 & 0xFF000000) != 0 ) {
//or 	    if( (range_Q32 & 0xFF000000L) != 0 ) {
	        /* No normalization */
//	        range_Q16 = SKP_RSHIFT_uint( range_Q32, 16 );
	    	range_Q16 = range_Q32>>>16;
	    } else {
	        if( (range_Q32 & 0xFFFF0000) != 0 ) {
//or	        if( (range_Q32 & 0xFFFF0000L) != 0 ) {
	            /* Normalization of 8 bits shift */
//	            range_Q16 = SKP_RSHIFT_uint( range_Q32, 8 );\
	        	range_Q16 = ( range_Q32 >>> 8 );
	            /* Check for errors */
//	            if( SKP_RSHIFT_uint( base_Q32, 24 ) ) {
	        	if( ( base_Q32 >>> 24 ) != 0) {
	                psRC.error = Silk_define.RANGE_CODER_NORMALIZATION_FAILED;
	                /* Set output to zero */
//	                *data = 0;
	                data[data_offset + 0] = 0;
	                return;
	            }
	        } else {
	            /* Normalization of 16 bits shift */
	            range_Q16 = range_Q32;
	            /* Check for errors */
//	            if( SKP_RSHIFT( base_Q32, 16 ) ) {
	            if( ( base_Q32 >> 16 ) != 0 ) {
	                psRC.error = Silk_define.RANGE_CODER_NORMALIZATION_FAILED;
	                /* Set output to zero */
//	                *data = 0;
	                data[data_offset + 0] = 0;
	                return;
	            }
	            /* Update base */
//	            base_Q32 = SKP_LSHIFT_uint( base_Q32, 8 );
	            base_Q32 = ( base_Q32 << 8 );
	            /* Make sure not to read beyond buffer */
	            if( bufferIx < psRC.bufferLength ) {
	                /* Read one byte from buffer */
//djinn ??
//	            	base_Q32 |= (long)buffer[buffer_offset + bufferIx++ ];
	            	base_Q32 |= ((int)buffer[buffer_offset + bufferIx++ ])&0xFF;
	            }
	        }
	        /* Update base */
//	        base_Q32 = SKP_LSHIFT_uint( base_Q32, 8 );
	        base_Q32 = ( base_Q32 << 8 );
	        /* Make sure not to read beyond buffer */
	        if( bufferIx < psRC.bufferLength ) {
	            /* Read one byte from buffer */
//	            base_Q32 |= (long)buffer[ bufferIx++ ];
	//        	base_Q32 |= (long)buffer[ buffer_offset + bufferIx++ ];
	        	base_Q32 |= ((int)buffer[ buffer_offset + bufferIx++ ])&0xFF;
	        }
	    }

	    /* Check for zero interval length */
	    if( range_Q16 == 0 ) {
	        psRC.error = Silk_define.RANGE_CODER_ZERO_INTERVAL_WIDTH;
	        /* Set output to zero */
//            *data = 0;
            data[data_offset + 0] = 0;
	        return;
	    }

	    /* Copy structure data back */
	    psRC.base_Q32  = base_Q32;
	    psRC.range_Q16 = range_Q16;
	    psRC.bufferIx  = bufferIx;
	}

	/* Range decoder for multiple symbols */
//	void SKP_Silk_range_decoder_multi(
//	    SKP_int                         data[],             /* O    uncompressed data                [nSymbols] */
//	    SKP_Silk_range_coder_state      *psRC,              /* I/O  compressor data structure                   */
//	    const SKP_uint16 * const        prob[],             /* I    cumulative density functions                */
//	    const SKP_int                   probStartIx[],      /* I    initial (middle) entries of cdfs [nSymbols] */
//	    const SKP_int                   nSymbols            /* I    number of data symbols                      */
//	)
	static void SKP_Silk_range_decoder_multi(
		    int                         data[],             /* O    uncompressed data                [nSymbols] */
		    SKP_Silk_range_coder_state  psRC,              /* I/O  compressor data structure                   */
		    int[][]                     prob,             /* I    cumulative density functions                */
		    int                         probStartIx[],      /* I    initial (middle) entries of cdfs [nSymbols] */
		    final int                   nSymbols            /* I    number of data symbols                      */
	)
	{
	    int  k;
	    for( k = 0; k < nSymbols; k++ ) {
//	        SKP_Silk_range_decoder( &data[ k ], psRC, prob[ k ], probStartIx[ k ] );
	    	SKP_Silk_range_decoder( data, k, psRC, prob[ k ], 0, probStartIx[ k ] );
	    }
	}

	/* Initialize range encoder */
//	void SKP_Silk_range_enc_init(
//	    SKP_Silk_range_coder_state      *psRC               /* O    compressor data structure                   */
//	)
	static void SKP_Silk_range_enc_init(
		    SKP_Silk_range_coder_state      psRC               /* O    compressor data structure                   */
	)
	{
	    /* Initialize structure */
	    psRC.bufferLength = Silk_define.MAX_ARITHM_BYTES;
	    psRC.range_Q16    = 0x0000FFFF;
	    psRC.bufferIx     = 0;
	    psRC.base_Q32     = 0;
	    psRC.error        = 0;
	}

	/* Initialize range decoder */
//	void SKP_Silk_range_dec_init(
//	    SKP_Silk_range_coder_state      *psRC,              /* O    compressor data structure                   */
//	    const SKP_uint8                 buffer[],           /* I    buffer for compressed data [bufferLength]   */
//	    const int                 bufferLength        /* I    buffer length (in bytes)                    */
//	)
	static void SKP_Silk_range_dec_init(
		    SKP_Silk_range_coder_state  psRC,              /* O    compressor data structure                   */
		    byte                        buffer[],           /* I    buffer for compressed data [bufferLength]   */
		    int 						buffer_offset,
		    final int                   bufferLength        /* I    buffer length (in bytes)                    */
	)
	{
	    /* check input */
	    if( bufferLength > Silk_define.MAX_ARITHM_BYTES ) {
	        psRC.error = Silk_define.RANGE_CODER_DEC_PAYLOAD_TOO_LONG;
	        return;
	    }
	    /* Initialize structure */
	    /* Copy to internal buffer */
//	    SKP_memcpy( psRC.buffer, buffer, bufferLength * sizeof( SKP_uint8 ) ); 
	    System.arraycopy(buffer, buffer_offset, psRC.buffer, 0, bufferLength);
	    
	    psRC.bufferLength = bufferLength;
//djinn ??? consider  buffer_offset with bufferIx ???
	    psRC.bufferIx = 0;
//	    psRC.base_Q32 = 
//	        SKP_LSHIFT_uint( (long)buffer[ 0 ], 24 ) | 
//	        SKP_LSHIFT_uint( (long)buffer[ 1 ], 16 ) | 
//	        SKP_LSHIFT_uint( (long)buffer[ 2 ],  8 ) | 
//	                         (long)buffer[ 3 ];
	    psRC.base_Q32 = 
	    	(
		        ( ((int)buffer[ buffer_offset + 0 ] & 0xFF) << 24 ) | 
		        ( ((int)buffer[ buffer_offset + 1 ] & 0xFF) << 16 ) | 
		        ( ((int)buffer[ buffer_offset + 2 ] & 0xFF) <<  8 ) | 
		        ((int)buffer[ buffer_offset + 3 ] & 0xFF)
	        )&0xFFFFFFFFL;
	    psRC.range_Q16 = 0x0000FFFF;
	    psRC.error     = 0;
	}

	/* Determine length of bitstream */
//	SKP_int SKP_Silk_range_coder_get_length(                /* O    returns number of BITS in stream            */
//	    const SKP_Silk_range_coder_state    *psRC,          /* I    compressed data structure                   */
//	    SKP_int                             *nBytes         /* O    number of BYTES in stream                   */
//	)
	static int SKP_Silk_range_coder_get_length(                /* O    returns number of BITS in stream            */
		    SKP_Silk_range_coder_state    psRC,          /* I    compressed data structure                   */
		    int[]                         nBytes         /* O    number of BYTES in stream                   */
		)
	{
	    int nBits;

	    /* Number of bits in stream */
//	    nBits = SKP_LSHIFT( psRC.bufferIx, 3 ) + SKP_Silk_CLZ32( psRC.range_Q16 - 1 ) - 14;
	    nBits = ( psRC.bufferIx << 3 ) + Silk_macros.SKP_Silk_CLZ32((int) (psRC.range_Q16 - 1) ) - 14;
	    
//	    *nBytes = SKP_RSHIFT( nBits + 7, 3 );
	    nBytes [0] = (( nBits + 7)>> 3 );
	    /* Return number of bits in bitstream */
	    return nBits;
	}

	/* Write shortest uniquely decodable stream to buffer, and determine its length */
	static void SKP_Silk_range_enc_wrap_up(
	    SKP_Silk_range_coder_state      psRC               /* I/O  compressed data structure                   */
	)
	{
	    int bufferIx_tmp, bits_to_store, bits_in_stream, nBytes, mask;
	    long base_Q24;

	    /* Lower limit of interval, shifted 8 bits to the right */
	    base_Q24 = psRC.base_Q32 >>> 8;

//	    bits_in_stream = SKP_Silk_range_coder_get_length( psRC, &nBytes );
	    int[] nBytes_ptr = new int[1];
	    bits_in_stream = SKP_Silk_range_coder_get_length( psRC, nBytes_ptr );
	    nBytes = nBytes_ptr[0];

	    /* Number of additional bits (1..9) required to be stored to stream */
	    bits_to_store = bits_in_stream - psRC.bufferIx << 3 ;
	    /* Round up to required resolution */
	    base_Q24 += 0x00800000 >>> ( bits_to_store - 1 );
	    base_Q24 &= 0xFFFFFFFF << ( 24 - bits_to_store );

	    /* Check for carry */
	    if( (base_Q24 & 0x01000000) != 0 ) 
	    {
	        /* Propagate carry in buffer */
	        bufferIx_tmp = psRC.bufferIx;
	        while( ( ++( psRC.buffer[ --bufferIx_tmp ] ) ) == 0 );
	    }

	    /* Store to stream, making sure not to write beyond buffer */
	    if( psRC.bufferIx < psRC.bufferLength ) 
	    {
//	        psRC.buffer[ psRC.bufferIx++ ] = (SKP_uint8)SKP_RSHIFT_uint( base_Q24, 16 );
	        psRC.buffer[ psRC.bufferIx++ ] = (byte) ( base_Q24>>> 16 );
	        if( bits_to_store > 8 ) 
	        {
	            if( psRC.bufferIx < psRC.bufferLength ) 
	            {
//	                psRC.buffer[ psRC.bufferIx++ ] = (SKP_uint8)SKP_RSHIFT_uint( base_Q24, 8 );
	                psRC.buffer[ psRC.bufferIx++ ] = (byte) ( base_Q24 >>> 8 );
	            }
	        }
	    }

	    /* Fill up any remaining bits in the last byte with 1s */
	    if( (bits_in_stream & 7) != 0 )
	    {
	        mask = 0xFF >> ( bits_in_stream & 7 );
	        if( nBytes - 1 < psRC.bufferLength ) 
	        {
	            psRC.buffer[ nBytes - 1 ] |= mask;
	        }
	    }
	}

	/* Check that any remaining bits in the last byte are set to 1 */
//	void SKP_Silk_range_coder_check_after_decoding(
//	    SKP_Silk_range_coder_state      *psRC               /* I/O  compressed data structure                   */
//	)
	static void SKP_Silk_range_coder_check_after_decoding(
		    SKP_Silk_range_coder_state      psRC               /* I/O  compressed data structure                   */
	)
	{
	    int bits_in_stream, nBytes, mask;
	    int nBytes_ptr[] = new int[1];
//	    nBytes_ptr[0] = nBytes;
	    
	    bits_in_stream = SKP_Silk_range_coder_get_length( psRC, nBytes_ptr );
	    nBytes = nBytes_ptr[0];
	    
	    /* Make sure not to read beyond buffer */
	    if( nBytes - 1 >= psRC.bufferLength ) {
	        psRC.error = Silk_define.RANGE_CODER_DECODER_CHECK_FAILED;
	        return;
	    }

	    /* Test any remaining bits in last byte */
	    if( (bits_in_stream & 7) != 0 ) {
//	        mask = SKP_RSHIFT( 0xFF, bits_in_stream & 7 );
	    	mask = ( 0xFF >>( bits_in_stream & 7) );
	    	
	        if( ( psRC.buffer[ nBytes - 1 ] & mask ) != mask ) {
	            psRC.error = Silk_define.RANGE_CODER_DECODER_CHECK_FAILED;
	            return;
	        }
	    }
	}

}