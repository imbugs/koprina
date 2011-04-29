/**
 * 
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author djinn
 *
 */
public class Silk_bwexpander_FLP 
{
	/* Chirp (bw expand) LP AR filter */
	static void SKP_Silk_bwexpander_FLP( 
	    float[]           ar,        /* I/O  AR filter to be expanded (without leading 1)    */
	    int ar_offset,
	    final int       d,          /* I    length of ar                                    */
	    final float     chirp       /* I    chirp factor (typically in range (0..1) )       */
	)
	{
	    int   i;
	    float cfac = chirp;

	    for( i = 0; i < d - 1; i++ ) 
	    {
	        ar[ ar_offset+i ] *=  cfac;
	        cfac    *=  chirp;
	    }
	    ar[ ar_offset + d - 1 ] *=  cfac;
	}
}



