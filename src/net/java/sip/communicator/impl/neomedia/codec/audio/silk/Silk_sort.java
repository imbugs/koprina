/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_sort" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_sort.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_sort 
{
	
	/* Insertion sort (fast for already almost sorted arrays):   */
	/* Best case:  O(n)   for an already sorted array            */
	/* Worst case: O(n^2) for an inversely sorted array          */
	/*                                                           */
	/* Shell short:    http://en.wikipedia.org/wiki/Shell_sort   */



//	void SKP_Silk_insertion_sort_increasing(
//	    SKP_int32           *a,             /* I/O:  Unsorted / Sorted vector               */
//	    SKP_int             *index,         /* O:    Index vector for the sorted elements   */
//	    const SKP_int       L,              /* I:    Vector length                          */
//	    const SKP_int       K               /* I:    Number of correctly sorted positions   */
//	)
	static void SKP_Silk_insertion_sort_increasing(
		    int           []a,             /* I/O:  Unsorted / Sorted vector               */
		    int           []index,         /* O:    Index vector for the sorted elements   */
		    final int       L,              /* I:    Vector length                          */
		    final int       K               /* I:    Number of correctly sorted positions   */
		)
	{
	    int    value;
	    int        i, j;

	    /* Safety checks */
	    Silk_typedef.SKP_assert( K >  0 );
	    Silk_typedef.SKP_assert( L >  0 );
	    Silk_typedef.SKP_assert( L >= K );

	    /* Write start indices in index vector */
	    for( i = 0; i < K; i++ ) {
	        index[ i ] = i;
	    }

	    /* Sort vector elements by value, increasing order */
	    for( i = 1; i < K; i++ ) {
	        value = a[ i ];
	        for( j = i - 1; ( j >= 0 ) && ( value < a[ j ] ); j-- ) {
	            a[ j + 1 ]     = a[ j ];     /* Shift value */
	            index[ j + 1 ] = index[ j ]; /* Shift index */
	        }
	        a[ j + 1 ]     = value; /* Write value */
	        index[ j + 1 ] = i;     /* Write index */
	    }

	    /* If less than L values are asked for, check the remaining values, */
	    /* but only spend CPU to ensure that the K first values are correct */
	    for( i = K; i < L; i++ ) {
	        value = a[ i ];
	        if( value < a[ K - 1 ] ) {
	            for( j = K - 2; ( j >= 0 ) && ( value < a[ j ] ); j-- ) {
	                a[ j + 1 ]     = a[ j ];     /* Shift value */
	                index[ j + 1 ] = index[ j ]; /* Shift index */
	            }
	            a[ j + 1 ]     = value; /* Write value */
	            index[ j + 1 ] = i;        /* Write index */
	        }
	    }
	}

//	void SKP_Silk_insertion_sort_decreasing(
//	    int             *a,             /* I/O: Unsorted / Sorted vector                */
//	    int             *index,         /* O:   Index vector for the sorted elements    */
//	    const int       L,              /* I:   Vector length                           */
//	    const int       K               /* I:   Number of correctly sorted positions    */
//	)
	static void SKP_Silk_insertion_sort_decreasing(
		    int             []a,             /* I/O: Unsorted / Sorted vector                */
		    int             []index,         /* O:   Index vector for the sorted elements    */
		    final int       L,              /* I:   Vector length                           */
		    final int       K               /* I:   Number of correctly sorted positions    */
		)
	{
	    int    value;
	    int    i, j;

	    /* Safety checks */
	    Silk_typedef.SKP_assert( K >  0 );
	    Silk_typedef.SKP_assert( L >  0 );
	    Silk_typedef.SKP_assert( L >= K );

	    /* Write start indices in index vector */
	    for( i = 0; i < K; i++ ) {
	        index[ i ] = i;
	    }

	    /* Sort vector elements by value, decreasing order */
	    for( i = 1; i < K; i++ ) {
	        value = a[ i ];
	        for( j = i - 1; ( j >= 0 ) && ( value > a[ j ] ); j-- ) {
	            a[ j + 1 ]     = a[ j ];     /* Shift value */
	            index[ j + 1 ] = index[ j ]; /* Shift index */
	        }
	        a[ j + 1 ]     = value; /* Write value */
	        index[ j + 1 ] = i;     /* Write index */
	    }

	    /* If less than L values are asked for, check the remaining values, */
	    /* but only spend CPU to ensure that the K first values are correct */
	    for( i = K; i < L; i++ ) {
	        value = a[ i ];
	        if( value > a[ K - 1 ] ) {
	            for( j = K - 2; ( j >= 0 ) && ( value > a[ j ] ); j-- ) {
	                a[ j + 1 ]     = a[ j ];     /* Shift value */
	                index[ j + 1 ] = index[ j ]; /* Shift index */
	            }
	            a[ j + 1 ]     = value; /* Write value */
	            index[ j + 1 ] = i;     /* Write index */
	        }
	    }
	}

//	void SKP_Silk_insertion_sort_decreasing_int16(
//	    short           *a,             /* I/O: Unsorted / Sorted vector                */
//	    int             *index,         /* O:   Index vector for the sorted elements    */
//	    const int       L,              /* I:   Vector length                           */
//	    const int       K               /* I:   Number of correctly sorted positions    */
//	)
	static void SKP_Silk_insertion_sort_decreasing_int16(
		    short           []a,             /* I/O: Unsorted / Sorted vector                */
		    int             []index,         /* O:   Index vector for the sorted elements    */
		    final int       L,              /* I:   Vector length                           */
		    final int       K               /* I:   Number of correctly sorted positions    */
		)
	{
	    int i, j;
	    int value;

	    /* Safety checks */
	    Silk_typedef.SKP_assert( K >  0 );
	    Silk_typedef.SKP_assert( L >  0 );
	    Silk_typedef.SKP_assert( L >= K );

	    /* Write start indices in index vector */
	    for( i = 0; i < K; i++ ) {
	        index[ i ] = i;
	    }

	    /* Sort vector elements by value, decreasing order */
	    for( i = 1; i < K; i++ ) {
	        value = a[ i ];
	        for( j = i - 1; ( j >= 0 ) && ( value > a[ j ] ); j-- ) {    
	            a[ j + 1 ]     = a[ j ];     /* Shift value */
	            index[ j + 1 ] = index[ j ]; /* Shift index */
	        }
//djinn ??	        a[ j + 1 ]     = value; /* Write value */
	        a[ j + 1 ]     = (short) value; /* Write value */
	        index[ j + 1 ] = i;     /* Write index */
	    }

	    /* If less than L values are asked for, check the remaining values, */
	    /* but only spend CPU to ensure that the K first values are correct */
	    for( i = K; i < L; i++ ) {
	        value = a[ i ];
	        if( value > a[ K - 1 ] ) {
	            for( j = K - 2; ( j >= 0 ) && ( value > a[ j ] ); j-- ) {    
	                a[ j + 1 ]     = a[ j ];     /* Shift value */
	                index[ j + 1 ] = index[ j ]; /* Shift index */
	            }
//djinn ??	            a[ j + 1 ]     = value; /* Write value */
	            a[ j + 1 ]     = (short) value; /* Write value */
	            index[ j + 1 ] = i;     /* Write index */
	        }
	    }
	}

//	void SKP_Silk_insertion_sort_increasing_all_values(
//	    int             *a,             /* I/O: Unsorted / Sorted vector                */
//	    const int       L               /* I:   Vector length                           */
//	)
	static void SKP_Silk_insertion_sort_increasing_all_values(
		    int             []a,             /* I/O: Unsorted / Sorted vector                */
		    int				  a_offset,
		    final int         L               /* I:   Vector length                           */
		)
	{
	    int    value;
	    int    i, j;

	    /* Safety checks */
	    Silk_typedef.SKP_assert( L >  0 );

	    /* Sort vector elements by value, increasing order */
	    for( i = 1; i < L; i++ ) {
	        value = a[ a_offset+i ];
	        for( j = i - 1; ( j >= 0 ) && ( value < a[ a_offset+j ] ); j-- ) {
	            a[ a_offset+j + 1 ] = a[ a_offset+j ]; /* Shift value */
	        }
	        a[ a_offset+j + 1 ] = value; /* Write value */
	    }
	}

//	void SKP_Silk_shell_insertion_sort_increasing(
//	    int           *a,             /* I/O:  Unsorted / Sorted vector               */
//	    int             *index,         /* O:    Index vector for the sorted elements   */
//	    const int       L,              /* I:    Vector length                          */
//	    const int       K               /* I:    Number of correctly sorted positions   */
//	)
	static void SKP_Silk_shell_insertion_sort_increasing(
		    int           []a,             /* I/O:  Unsorted / Sorted vector               */
		    int           []index,         /* O:    Index vector for the sorted elements   */
		    final int       L,              /* I:    Vector length                          */
		    final int       K               /* I:    Number of correctly sorted positions   */
		)
	{
	    int    value, inc_Q16_tmp;
	    int      i, j, inc, idx;
	   
	    /* Safety checks */
	    Silk_typedef.SKP_assert( K >  0 );
	    Silk_typedef.SKP_assert( L >  0 );
	    Silk_typedef.SKP_assert( L >= K );
	    
	    /* Calculate initial step size */
//	    inc_Q16_tmp = SKP_LSHIFT( (int)L, 15 );
	    inc_Q16_tmp = ( (int)L << 15 );
//	    inc = SKP_RSHIFT( inc_Q16_tmp, 16 );
	    inc = ( inc_Q16_tmp >> 16 );

	    /* Write start indices in index vector */
	    for( i = 0; i < K; i++ ) {
	        index[ i ] = i;
	    }

	    /* Shell sort first values */
	    while( inc > 0 ) {
	        for( i = inc; i < K; i++ ) {
	            value = a[ i ];
	            idx   = index[ i ];
	            for( j = i - inc; ( j >= 0 ) && ( value < a[ j ] ); j -= inc ) {
	                a[ j + inc ]     = a[ j ];     /* Shift value */
	                index[ j + inc ] = index[ j ]; /* Shift index */
	            }
	            a[ j + inc ]     = value; /* Write value */
	            index[ j + inc ] = idx;   /* Write index */
	        }
//	        inc_Q16_tmp = SKP_SMULWB( inc_Q16_tmp, 29789 ); // 29789_Q16 = 2.2^(-1)_Q0
	        inc_Q16_tmp = Silk_macros.SKP_SMULWB( inc_Q16_tmp, 29789 ); // 29789_Q16 = 2.2^(-1)_Q0

//	        inc = SKP_RSHIFT_ROUND( inc_Q16_tmp, 16 );
	        inc = Silk_SigProc_FIX.SKP_RSHIFT_ROUND( inc_Q16_tmp, 16 );

	    }

	    /* If less than L values are asked for, check the remaining values, */
	    /* but only spend CPU to ensure that the K first values are correct */
	    /* Insertion sort remaining values */
	    for( i = K; i < L; i++ ) {
	        value = a[ i ];
	        if( value < a[ K - 1 ] ) {
	            for( j = K - 2; ( j >= 0 ) && ( value < a[ j ] ); j-- ) {
	                a[ j + 1 ]     = a[ j ];     /* Shift value */
	                index[ j + 1 ] = index[ j ]; /* Shift index */
	            }
	            a[ j + 1 ]     = value; /* Write value */
	            index[ j + 1 ] = i;     /* Write index */
	        }
	    }
	}

//	void SKP_Silk_shell_sort_increasing_all_values(
//	    int           *a,             /* I/O:  Unsorted / Sorted vector               */
//	    int             *index,         /* O:    Index vector for the sorted elements   */
//	    const int       L               /* I:    Vector length                          */
//	)
	static void SKP_Silk_shell_sort_increasing_all_values(
		    int           []a,             /* I/O:  Unsorted / Sorted vector               */
		    int           []index,         /* O:    Index vector for the sorted elements   */
		    final int       L               /* I:    Vector length                          */
		)
	{
	    int    value, inc_Q16_tmp;
	    int      i, j, inc, idx;
	   
	    /* Safety checks */
	    Silk_typedef.SKP_assert( L >  0 );
	 
	    /* Calculate initial step size */
//	    inc_Q16_tmp = SKP_LSHIFT( (int)L, 15 );
	    inc_Q16_tmp = ( (int)L << 15 );
//	    inc = SKP_RSHIFT( inc_Q16_tmp, 16 );
	    inc = ( inc_Q16_tmp >> 16 );

	    
	    /* Write start indices in index vector */
	    for( i = 0; i < L; i++ ) {
	        index[ i ] = i;
	    }

	    /* Sort vector elements by value, increasing order */
	    while( inc > 0 ) {
	        for( i = inc; i < L; i++ ) {
	            value = a[ i ];
	            idx = index[ i ];
	            for( j = i - inc; ( j >= 0 ) && ( value < a[ j ] ); j -= inc ) {
	                a[ j + inc ]     = a[ j ];     /* Shift value */
	                index[ j + inc ] = index[ j ]; /* Shift index */
	            }
	            a[ j + inc ] = value;   /* Write value */
	            index[ j + inc ] = idx; /* Write index */
	        }
//	        inc_Q16_tmp = SKP_SMULWB( inc_Q16_tmp, 29789 ); // 29789_Q16 = 2.2^(-1)_Q0
	        inc_Q16_tmp = Silk_macros.SKP_SMULWB( inc_Q16_tmp, 29789 ); // 29789_Q16 = 2.2^(-1)_Q0

//	        inc = SKP_RSHIFT_ROUND( inc_Q16_tmp, 16 );
	        inc = Silk_SigProc_FIX.SKP_RSHIFT_ROUND( inc_Q16_tmp, 16 );

	    }
	}
	
}