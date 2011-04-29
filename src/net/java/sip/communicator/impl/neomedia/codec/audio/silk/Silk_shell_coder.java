/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_shell_coder" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_shell_coder.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_shell_coder 
{
	/* shell coder; pulse-subframe length is hardcoded */

//	SKP_INLINE void combine_pulses(
//	    SKP_int         *out,   /* O:   combined pulses vector [len] */
//	    const SKP_int   *in,    /* I:   input vector       [2 * len] */
//	    const SKP_int   len     /* I:   number of OUTPUT samples     */
//	)
	static void combine_pulses(
			int         []out,   /* O:   combined pulses vector [len] */
			int         out_offset,
			int         []in,    /* I:   input vector       [2 * len] */
			int         in_offset,
			final int   len     /* I:   number of OUTPUT samples     */
    )
	{
	    int k;
	    for( k = 0; k < len; k++ ) {
	        out[ out_offset + k ] = in[ in_offset + 2 * k ] + in[ in_offset + 2 * k + 1 ];
	    }
	}

	static void encode_split(
	    SKP_Silk_range_coder_state  sRC,           /* I/O: compressor data structure                   */
	    final int               p_child1,       /* I:   pulse amplitude of first child subframe     */
	    final int               p,              /* I:   pulse amplitude of current subframe         */
//	    const SKP_uint16            *shell_table    /* I:   table of shell cdfs                         */
	    int[]             shell_table
	)
	{
//	    const SKP_uint16 *cdf;
		int[] cdf;
		int cdf_offset;

	    if( p > 0 ) 
	    {
//	        cdf = &shell_table[ SKP_Silk_shell_code_table_offsets[ p ] ];
	        cdf = shell_table;
	        cdf_offset =  Silk_tables_pulses_per_block.SKP_Silk_shell_code_table_offsets[ p ] ;
	        Silk_range_coder.SKP_Silk_range_encoder( sRC, p_child1, cdf, cdf_offset );
	    }
	}

//	SKP_INLINE void decode_split(
//	    SKP_int                     *p_child1,      /* O:   pulse amplitude of first child subframe     */
//	    SKP_int                     *p_child2,      /* O:   pulse amplitude of second child subframe    */
//	    SKP_Silk_range_coder_state  *sRC,           /* I/O: compressor data structure                   */
//	    const SKP_int               p,              /* I:   pulse amplitude of current subframe         */
//	    const SKP_uint16            *shell_table    /* I:   table of shell cdfs                         */
//	)
	static  void decode_split(
		    int                    []p_child1,      /* O:   pulse amplitude of first child subframe     */
		    int                    p_child1_offset,
		    int                    []p_child2,      /* O:   pulse amplitude of second child subframe    */
		    int                    p_child2_offset,
		    SKP_Silk_range_coder_state  sRC,           /* I/O: compressor data structure                   */
		    final int               p,              /* I:   pulse amplitude of current subframe         */
		    int[]                  shell_table    /* I:   table of shell cdfs                         */
	)
	{
	    int cdf_middle;
//	    const SKP_uint16 *cdf;
	    int[] cdf;
	    int   cdf_offset;

	    if( p > 0 )
	    {
//	        cdf_middle = SKP_RSHIFT( p, 1 );
	    	cdf_middle = ( p >> 1 );
//	        cdf = &shell_table[ SKP_Silk_shell_code_table_offsets[ p ] ];
	    	cdf = shell_table;
	    	cdf_offset = Silk_tables_pulses_per_block.SKP_Silk_shell_code_table_offsets[ p ];
	    	
//	        SKP_Silk_range_decoder( p_child1, sRC, cdf, cdf_middle );
	        Silk_range_coder.SKP_Silk_range_decoder( p_child1, p_child1_offset, sRC, cdf, cdf_offset, cdf_middle );
	        p_child2[ p_child2_offset + 0 ] = p - p_child1[ p_child1_offset + 0 ];
	    } 
	    else 
	    {
	        p_child1[ p_child1_offset + 0 ] = 0;
	        p_child2[ p_child2_offset + 0 ] = 0;
	    }
	}

	/* Shell encoder, operates on one shell code frame of 16 pulses */
	void SKP_Silk_shell_encoder(
	    SKP_Silk_range_coder_state      sRC,               /* I/O  compressor data structure                   */
	    int[]                   pulses0            /* I    data: nonnegative pulse amplitudes          */
	)
	{
	    int[] pulses1 = new int[ 8 ], pulses2 = new int[ 4 ], pulses3 = new int[ 2 ], pulses4 = new int[ 1 ];

	    /* this function operates on one shell code frame of 16 pulses */
	    assert( Silk_define.SHELL_CODEC_FRAME_LENGTH == 16 );

	    /* tree representation per pulse-subframe */
	    combine_pulses( pulses1,0, pulses0,0, 8 );
	    combine_pulses( pulses2,0, pulses1,0, 4 );
	    combine_pulses( pulses3,0, pulses2,0, 2 );
	    combine_pulses( pulses4,0, pulses3,0, 1 );

	    encode_split( sRC, pulses3[  0 ], pulses4[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table3 );

	    encode_split( sRC, pulses2[  0 ], pulses3[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table2 );

	    encode_split( sRC, pulses1[  0 ], pulses2[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    encode_split( sRC, pulses0[  0 ], pulses1[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    encode_split( sRC, pulses0[  2 ], pulses1[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );

	    encode_split( sRC, pulses1[  2 ], pulses2[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    encode_split( sRC, pulses0[  4 ], pulses1[ 2 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    encode_split( sRC, pulses0[  6 ], pulses1[ 3 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );

	    encode_split( sRC, pulses2[  2 ], pulses3[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table2 );

	    encode_split( sRC, pulses1[  4 ], pulses2[ 2 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    encode_split( sRC, pulses0[  8 ], pulses1[ 4 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    encode_split( sRC, pulses0[ 10 ], pulses1[ 5 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );

	    encode_split( sRC, pulses1[  6 ], pulses2[ 3 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    encode_split( sRC, pulses0[ 12 ], pulses1[ 6 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    encode_split( sRC, pulses0[ 14 ], pulses1[ 7 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	}


	/* Shell decoder, operates on one shell code frame of 16 pulses */
//	void SKP_Silk_shell_decoder(
//	    SKP_int                         *pulses0,           /* O    data: nonnegative pulse amplitudes          */
//	    SKP_Silk_range_coder_state      *sRC,               /* I/O  compressor data structure                   */
//	    const SKP_int                   pulses4             /* I    number of pulses per pulse-subframe         */
//	)
	static void SKP_Silk_shell_decoder(
		    int                         []pulses0,           /* O    data: nonnegative pulse amplitudes          */
		    int                         pulses0_offset,
		    SKP_Silk_range_coder_state  sRC,               /* I/O  compressor data structure                   */
		    final int                   pulses4             /* I    number of pulses per pulse-subframe         */
	)
	{
//	    int pulses3[ 2 ], pulses2[ 4 ], pulses1[ 8 ];
		int[] pulses3 = new int[ 2 ], pulses2 = new int[ 4 ], pulses1 = new int[ 8 ];

	    /* this function operates on one shell code frame of 16 pulses */
	    Silk_typedef.SKP_assert( Silk_define.SHELL_CODEC_FRAME_LENGTH == 16 );

//	    decode_split( &pulses3[  0 ], &pulses3[  1 ], sRC, pulses4,      SKP_Silk_shell_code_table3 );
	    decode_split( pulses3, 0, pulses3, 1, sRC, pulses4, Silk_tables_pulses_per_block.SKP_Silk_shell_code_table3 );

//	    decode_split( &pulses2[  0 ], &pulses2[  1 ], sRC, pulses3[ 0 ], SKP_Silk_shell_code_table2 );
	    decode_split( pulses2, 0, pulses2, 1, sRC, pulses3[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table2 );

//	    decode_split( &pulses1[  0 ], &pulses1[  1 ], sRC, pulses2[ 0 ], SKP_Silk_shell_code_table1 );
	    decode_split( pulses1, 0, pulses1, 1, sRC, pulses2[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
//	    decode_split( &pulses0[  0 ], &pulses0[  1 ], sRC, pulses1[ 0 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 0, pulses0, pulses0_offset + 1, sRC, pulses1[ 0 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
//	    decode_split( &pulses0[  2 ], &pulses0[  3 ], sRC, pulses1[ 1 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 2, pulses0, pulses0_offset + 3, sRC, pulses1[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );

//	    decode_split( &pulses1[  2 ], &pulses1[  3 ], sRC, pulses2[ 1 ], SKP_Silk_shell_code_table1 );
	    decode_split( pulses1, 2, pulses1, 3, sRC, pulses2[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
//	    decode_split( &pulses0[  4 ], &pulses0[  5 ], sRC, pulses1[ 2 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 4, pulses0, pulses0_offset + 5, sRC, pulses1[ 2 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
//	    decode_split( &pulses0[  6 ], &pulses0[  7 ], sRC, pulses1[ 3 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 6, pulses0, pulses0_offset + 7, sRC, pulses1[ 3 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );

//	    decode_split( &pulses2[  2 ], &pulses2[  3 ], sRC, pulses3[ 1 ], SKP_Silk_shell_code_table2 );
	    decode_split( pulses2, 2, pulses2, 3, sRC, pulses3[ 1 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table2 );

//	    decode_split( &pulses1[  4 ], &pulses1[  5 ], sRC, pulses2[ 2 ], SKP_Silk_shell_code_table1 );
//	    decode_split( &pulses0[  8 ], &pulses0[  9 ], sRC, pulses1[ 4 ], SKP_Silk_shell_code_table0 );
//	    decode_split( &pulses0[ 10 ], &pulses0[ 11 ], sRC, pulses1[ 5 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses1, 4, pulses1, 5, sRC, pulses2[ 2 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    decode_split( pulses0, pulses0_offset + 8, pulses0, pulses0_offset + 9, sRC, pulses1[ 4 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 10,pulses0, pulses0_offset + 11, sRC, pulses1[ 5 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    
//	    decode_split( &pulses1[  6 ], &pulses1[  7 ], sRC, pulses2[ 3 ], SKP_Silk_shell_code_table1 );
//	    decode_split( &pulses0[ 12 ], &pulses0[ 13 ], sRC, pulses1[ 6 ], SKP_Silk_shell_code_table0 );
//	    decode_split( &pulses0[ 14 ], &pulses0[ 15 ], sRC, pulses1[ 7 ], SKP_Silk_shell_code_table0 );
	    decode_split( pulses1, 6, pulses1, 7, sRC, pulses2[ 3 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table1 );
	    decode_split( pulses0, pulses0_offset + 12, pulses0, pulses0_offset + 13, sRC, pulses1[ 6 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	    decode_split( pulses0, pulses0_offset + 14, pulses0, pulses0_offset + 15, sRC, pulses1[ 7 ], Silk_tables_pulses_per_block.SKP_Silk_shell_code_table0 );
	}

}
