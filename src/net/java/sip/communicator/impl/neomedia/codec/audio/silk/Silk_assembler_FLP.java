package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * Declaration of assembler functions.
 * @author Dingxin Xu
 */
public class Silk_assembler_FLP 
{
    /**
     * Declaration of assembler functions
     * @param r_LPC LPC residual signal
     * @param coefs LPC coefficients
     * @param speech Input signal
     * @param len Length of input signal
     */
	static void SKP_Silk_LPC_ana_sse( 
		float		[]r_LPC,		    /* O	LPC residual signal						*/
		final float	[]coefs, 			/* I	LPC coefficients						*/
		final float	[]speech,			/* I	Input signal							*/
		int			len					/* I	Length of input signal					*/
	)
	{
		
	}
}
