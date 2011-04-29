/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_NLSF_MSVQ_decode" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_NLSF_MSVQ_decode.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;
/**
 * 
 * @author 
 *
 */
public class Silk_NLSF_MSVQ_decode 
{
	/* NLSF vector decoder */
//	void SKP_Silk_NLSF_MSVQ_decode(
//	    SKP_int                         *pNLSF_Q15,     /* O    Pointer to decoded output vector [LPC_ORDER x 1]    */
//	    const SKP_Silk_NLSF_CB_struct   *psNLSF_CB,     /* I    Pointer to NLSF codebook struct                     */
//	    const SKP_int                   *NLSFIndices,   /* I    Pointer to NLSF indices          [nStages x 1]      */
//	    const SKP_int                   LPC_order       /* I    LPC order used                                      */
//	) 
	static void SKP_Silk_NLSF_MSVQ_decode(
		    int                         []pNLSF_Q15,     /* O    Pointer to decoded output vector [LPC_ORDER x 1]    */
		    SKP_Silk_NLSF_CB_struct     psNLSF_CB,     /* I    Pointer to NLSF codebook struct                     */
		    int                         []NLSFIndices,   /* I    Pointer to NLSF indices          [nStages x 1]      */
		    final int                   LPC_order       /* I    LPC order used                                      */
	)
	{
//	    const short *pCB_element;
	    short[] pCB_element;
	    int     pCB_element_offset;
	          int    s;
	          int    i;

	    /* Check that each index is within valid range */
	    Silk_typedef.SKP_assert( 0 <= NLSFIndices[ 0 ] && NLSFIndices[ 0 ] < psNLSF_CB.CBStages[ 0 ].nVectors );

	    /* Point to the first vector element */
//	    pCB_element = &psNLSF_CB.CBStages[ 0 ].CB_NLSF_Q15[ SKP_MUL( NLSFIndices[ 0 ], LPC_order ) ];
	    pCB_element = psNLSF_CB.CBStages[ 0 ].CB_NLSF_Q15;
	    pCB_element_offset =  ( NLSFIndices[ 0 ] * LPC_order );
	    
	    /* Initialize with the codebook vector from stage 0 */
	    for( i = 0; i < LPC_order; i++ ) {
	        pNLSF_Q15[ i ] = ( int )pCB_element[ pCB_element_offset + i ];
	    }
	          
	    for( s = 1; s < psNLSF_CB.nStages; s++ ) {
	        /* Check that each index is within valid range */
	        Silk_typedef.SKP_assert( 0 <= NLSFIndices[ s ] && NLSFIndices[ s ] < psNLSF_CB.CBStages[ s ].nVectors );

	        if( LPC_order == 16 ) {
	            /* Point to the first vector element */
//	            pCB_element = &psNLSF_CB.CBStages[ s ].CB_NLSF_Q15[ SKP_LSHIFT( NLSFIndices[ s ], 4 ) ];
	        	pCB_element = psNLSF_CB.CBStages[ s ].CB_NLSF_Q15;
	        	pCB_element_offset =  ( NLSFIndices[ s ] << 4 );

	            /* Add the codebook vector from the current stage */
	            pNLSF_Q15[  0 ] += pCB_element[  pCB_element_offset + 0 ];
	            pNLSF_Q15[  1 ] += pCB_element[  pCB_element_offset + 1 ];
	            pNLSF_Q15[  2 ] += pCB_element[  pCB_element_offset + 2 ];
	            pNLSF_Q15[  3 ] += pCB_element[  pCB_element_offset + 3 ];
	            pNLSF_Q15[  4 ] += pCB_element[  pCB_element_offset + 4 ];
	            pNLSF_Q15[  5 ] += pCB_element[  pCB_element_offset + 5 ];
	            pNLSF_Q15[  6 ] += pCB_element[  pCB_element_offset + 6 ];
	            pNLSF_Q15[  7 ] += pCB_element[  pCB_element_offset + 7 ];
	            pNLSF_Q15[  8 ] += pCB_element[  pCB_element_offset + 8 ];
	            pNLSF_Q15[  9 ] += pCB_element[  pCB_element_offset + 9 ];
	            pNLSF_Q15[ 10 ] += pCB_element[ pCB_element_offset + 10 ];
	            pNLSF_Q15[ 11 ] += pCB_element[ pCB_element_offset + 11 ];
	            pNLSF_Q15[ 12 ] += pCB_element[ pCB_element_offset + 12 ];
	            pNLSF_Q15[ 13 ] += pCB_element[ pCB_element_offset + 13 ];
	            pNLSF_Q15[ 14 ] += pCB_element[ pCB_element_offset + 14 ];
	            pNLSF_Q15[ 15 ] += pCB_element[ pCB_element_offset + 15 ];
	        } else {
	            /* Point to the first vector element */
//	            pCB_element = &psNLSF_CB.CBStages[ s ].CB_NLSF_Q15[ SKP_SMULBB( NLSFIndices[ s ], LPC_order ) ];
	        	pCB_element = psNLSF_CB.CBStages[ s ].CB_NLSF_Q15;
	        	pCB_element_offset = Silk_macros.SKP_SMULBB( NLSFIndices[ s ], LPC_order );

	            /* Add the codebook vector from the current stage */
	            for( i = 0; i < LPC_order; i++ ) {
	                pNLSF_Q15[ i ] += pCB_element[ pCB_element_offset + i ];
	            }
	        }
	    }

	    /* NLSF stabilization */
//	    SKP_Silk_NLSF_stabilize( pNLSF_Q15, psNLSF_CB.NDeltaMin_Q15, LPC_order );
	    Silk_NLSF_stabilize.SKP_Silk_NLSF_stabilize( pNLSF_Q15, 0, psNLSF_CB.NDeltaMin_Q15, LPC_order );
	}

}
