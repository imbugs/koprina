/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_create_init_destroy" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_create_init_destroy.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_create_init_destroy 
{
	/************************/
	/* Init Decoder State   */
	/************************/
	static int SKP_Silk_init_decoder(
	    SKP_Silk_decoder_state      psDec              /* I/O  Decoder state pointer                       */
	)
	{
//djinn TODO: no need to initialize the memory of object in Java?		
//djinn ???	    SKP_memset( psDec, 0, sizeof( SKP_Silk_decoder_state ) );
	    /* Set sampling rate to 24 kHz, and init non-zero values */
		Silk_decoder_set_fs.SKP_Silk_decoder_set_fs( psDec, 24 );

	    /* Used to deactivate e.g. LSF interpolation and fluctuation reduction */
	    psDec.first_frame_after_reset = 1;
	    psDec.prev_inv_gain_Q16 = 65536;

	    /* Reset CNG state */
//	    SKP_Silk_CNG_Reset( psDec );
	    Silk_CNG.SKP_Silk_CNG_Reset( psDec );
	    

//	    SKP_Silk_PLC_Reset( psDec );
	    Silk_PLC.SKP_Silk_PLC_Reset(psDec);
	    return(0);
	}


}
