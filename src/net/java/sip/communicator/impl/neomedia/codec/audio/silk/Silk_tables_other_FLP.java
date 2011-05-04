/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_tables_other_FLP" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_tables_other_FLP.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author djinn
 *
 */
public class Silk_tables_other_FLP 
{
	static float[] SKP_Silk_HarmShapeFIR_FLP = { 16384.0f / 65536.0f, 32767.0f / 65536.0f, 16384.0f / 65536.0f };

	float[][] SKP_Silk_Quantization_Offsets = 
	{
	    { Silk_define.OFFSET_VL_Q10 / 1024.0f,  Silk_define.OFFSET_VH_Q10 / 1024.0f  }, 
	    { Silk_define.OFFSET_UVL_Q10 / 1024.0f, Silk_define.OFFSET_UVH_Q10 / 1024.0f }
	};
}
