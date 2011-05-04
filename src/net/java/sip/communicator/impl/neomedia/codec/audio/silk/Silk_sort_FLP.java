/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_sort_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_sort_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_sort_FLP 
{
	static void SKP_Silk_insertion_sort_increasing_FLP
	(
		    float[]            a,          /* I/O:  Unsorted / Sorted vector                */
		    int a_offset,
		    int[]              index,      /* O:    Index vector for the sorted elements    */
		    final int        L,           /* I:    Vector length                           */
		    final int        K            /* I:    Number of correctly sorted positions    */
		)
		{
		    float value;
		    int   i, j;

		    /* Safety checks */
		    assert( K >  0 );
		    assert( L >  0 );
		    assert( L >= K );

		    /* Write start indices in index vector */
		    for( i = 0; i < K; i++ ) 
		    {
		        index[ i ] = i;
		    }

		    /* Sort vector elements by value, increasing order */
		    for( i = 1; i < K; i++ ) 
		    {
		        value = a[ a_offset+i ];
		        for( j = i - 1; ( j >= 0 ) && ( value < a[a_offset+ j ] ); j-- ) 
		        {
		            a[a_offset+ j + 1 ]     = a[a_offset+ j ];     /* Shift value */
		            index[ j + 1 ] = index[ j ]; /* Shift index */
		        }
		        a[a_offset+ j + 1 ]     = value; /* Write value */
		        index[ j + 1 ] = i;     /* Write index */
		    }

		    /* If less than L values are asked check the remaining values,      */
		    /* but only spend CPU to ensure that the K first values are correct */
		    for( i = K; i < L; i++ ) 
		    {
		        value = a[ a_offset+i ];
		        if( value < a[a_offset+ K - 1 ] ) 
		        {
		            for( j = K - 2; ( j >= 0 ) && ( value < a[a_offset+ j ] ); j-- ) {
		                a[ a_offset+j + 1 ]     = a[ a_offset+j ];     /* Shift value */
		                index[ j + 1 ] = index[ j ]; /* Shift index */
		            }
		            a[a_offset+ j + 1 ]     = value; /* Write value */
		            index[ j + 1 ] = i;        /* Write index */
		        }
		    }
		}

		static void SKP_Silk_insertion_sort_decreasing_FLP
		(
		    float[]            a,          /* I/O:  Unsorted / Sorted vector                */
		    int a_offset,
		    int[]              index,      /* O:    Index vector for the sorted elements    */
		    final int        L,           /* I:    Vector length                           */
		    final int        K            /* I:    Number of correctly sorted positions    */
		)
		{
		    float value;
		    int   i, j;

		    /* Safety checks */
		    assert( K >  0 );
		    assert( L >  0 );
		    assert( L >= K );

		    /* Write start indices in index vector */
		    for( i = 0; i < K; i++ ) {
		        index[ i ] = i;
		    }

		    /* Sort vector elements by value, decreasing order */
		    for( i = 1; i < K; i++ ) {
		        value = a[ a_offset+i ];
		        for( j = i - 1; ( j >= 0 ) && ( value > a[ a_offset+j ] ); j-- ) {
		            a[a_offset+ j + 1 ]     = a[ a_offset+j ];     /* Shift value */
		            index[ j + 1 ] = index[ j ]; /* Shift index */
		        }
		        a[a_offset+ j + 1 ]     = value; /* Write value */
		        index[ j + 1 ] = i;     /* Write index */
		    }

		    /* If less than L values are asked check the remaining values,      */
		    /* but only spend CPU to ensure that the K first values are correct */
		    for( i = K; i < L; i++ ) 
		    {
		        value = a[ a_offset+i ];
		        if( value > a[a_offset+ K - 1 ] ) 
		        {
		            for( j = K - 2; ( j >= 0 ) && ( value > a[ a_offset+j ] ); j-- ) 
		            {
		                a[a_offset+ j + 1 ]     = a[ a_offset+j ];     /* Shift value */
		                index[ j + 1 ] = index[ j ]; /* Shift index */
		            }
		            a[a_offset+ j + 1 ]     = value; /* Write value */
		            index[ j + 1 ] = i;     /* Write index */
		        }
		    }
		}

		static void SKP_Silk_insertion_sort_increasing_all_values_FLP
		(
		    float[]            a,          /* I/O:  Unsorted / Sorted vector                */
		    int a_offset,
		    final int        L            /* I:    Vector length                           */
		)
		{
		    float value;
		    int   i, j;

		    /* Safety checks */
		    assert( L >  0 );

		    /* Sort vector elements by value, increasing order */
		    for( i = 1; i < L; i++ ) 
		    {
		        value = a[ a_offset+i ];
		        for( j = i - 1; ( j >= 0 ) && ( value < a[ a_offset+j ] ); j-- ) 
		        {    
		            a[a_offset+ j + 1 ] = a[ a_offset+j ]; /* Shift value */
		        }
		        a[a_offset+ j + 1 ] = value; /* Write value */
		    }
		}
}



