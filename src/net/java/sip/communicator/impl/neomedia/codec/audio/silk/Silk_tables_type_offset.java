/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from  http://developer.skype.com/silk/
 * 
 * Class "Silk_tables_type_offset" is mainly based on 
 *../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_tables_type_offset.c
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
public class Silk_tables_type_offset
{	

//	const SKP_uint16 SKP_Silk_type_offset_CDF[ 5 ] = {
	static final int[] SKP_Silk_type_offset_CDF = {
	         0,  37522,  41030,  44212,  65535
	};

//	const SKP_int SKP_Silk_type_offset_CDF_offset = 2;
	static final int SKP_Silk_type_offset_CDF_offset = 2;


//	const SKP_uint16 SKP_Silk_type_offset_joint_CDF[ 4 ][ 5 ] = 
	static final int[][] SKP_Silk_type_offset_joint_CDF = 
	{
	{
	         0,  57686,  61230,  62358,  65535
	},
	{
	         0,  18346,  40067,  43659,  65535
	},
	{
	         0,  22694,  24279,  35507,  65535
	},
	{
	         0,   6067,   7215,  13010,  65535
	}
	};


}
