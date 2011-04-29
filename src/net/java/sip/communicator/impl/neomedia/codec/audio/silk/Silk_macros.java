/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_macros" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_macros.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 * @author
 *
 */
public class Silk_macros 
{
	// (a32 * (SKP_int32)((SKP_int16)(b32))) >> 16 output have to be 32bit int
//	#define SKP_SMULWB(a32, b32)            ((((a32) >> 16) * (SKP_int32)((SKP_int16)(b32))) + ((((a32) & 0x0000FFFF) * (SKP_int32)((SKP_int16)(b32))) >> 16))
	static int SKP_SMULWB(int a32, int b32)
	{
		return ((((a32) >> 16) * (int)((short)(b32))) + ((((a32) & 0x0000FFFF) * (int)((short)(b32))) >> 16));
	}

	// a32 + (b32 * (SKP_int32)((SKP_int16)(c32))) >> 16 output have to be 32bit int
//	#define SKP_SMLAWB(a32, b32, c32)       ((a32) + ((((b32) >> 16) * (SKP_int32)((SKP_int16)(c32))) + ((((b32) & 0x0000FFFF) * (SKP_int32)((SKP_int16)(c32))) >> 16)))
	static int SKP_SMLAWB(int a32, int b32, int c32)
	{
		return ((a32) + ((((b32) >> 16) * (int)((short)(c32))) + ((((b32) & 0x0000FFFF) * (int)((short)(c32))) >> 16)));
	}

	// (a32 * (b32 >> 16)) >> 16
//	#define SKP_SMULWT(a32, b32)            (((a32) >> 16) * ((b32) >> 16) + ((((a32) & 0x0000FFFF) * ((b32) >> 16)) >> 16))
	static int SKP_SMULWT(int a32, int b32)
	{
		return (((a32) >> 16) * ((b32) >> 16) + ((((a32) & 0x0000FFFF) * ((b32) >> 16)) >> 16));
	}

	// a32 + (b32 * (c32 >> 16)) >> 16
//	#define SKP_SMLAWT(a32, b32, c32)       ((a32) + (((b32) >> 16) * ((c32) >> 16)) + ((((b32) & 0x0000FFFF) * ((c32) >> 16)) >> 16))
	static int SKP_SMLAWT(int a32, int b32, int c32)
	{
		return ((a32) + (((b32) >> 16) * ((c32) >> 16)) + ((((b32) & 0x0000FFFF) * ((c32) >> 16)) >> 16));
	}

	// (SKP_int32)((SKP_int16)(a3))) * (SKP_int32)((SKP_int16)(b32)) output have to be 32bit int
//	#define SKP_SMULBB(a32, b32)            ((SKP_int32)((SKP_int16)(a32)) * (SKP_int32)((SKP_int16)(b32)))
	static int SKP_SMULBB(int a32, int b32)
	{
		return ((int)((short)(a32)) * (int)((short)(b32)));
	}

	// a32 + (SKP_int32)((SKP_int16)(b32)) * (SKP_int32)((SKP_int16)(c32)) output have to be 32bit int
//	#define SKP_SMLABB(a32, b32, c32)       ((a32) + ((SKP_int32)((SKP_int16)(b32))) * (SKP_int32)((SKP_int16)(c32)))
	static int SKP_SMLABB(int a32, int b32, int c32)
	{
		return ((a32) + ((int)((short)(b32))) * (int)((short)(c32)));
	}

	// (SKP_int32)((SKP_int16)(a32)) * (b32 >> 16)
//	#define SKP_SMULBT(a32, b32)            ((SKP_int32)((SKP_int16)(a32)) * ((b32) >> 16))
	static int SKP_SMULBT(int a32, int b32)
	{
		return ((int)((short)(a32)) * ((b32) >> 16));
	}

	// a32 + (SKP_int32)((SKP_int16)(b32)) * (c32 >> 16)
//	#define SKP_SMLABT(a32, b32, c32)       ((a32) + ((SKP_int32)((SKP_int16)(b32))) * ((c32) >> 16))
	static int SKP_SMLABT(int a32, int b32, int c32)
	{
		return ((a32) + ((int)((short)(b32))) * ((c32) >> 16));
	}

	// a64 + (b32 * c32)
//	#define SKP_SMLAL(a64, b32, c32)        (SKP_ADD64((a64), ((SKP_int64)(b32) * (SKP_int64)(c32))))
	static long SKP_SMLAL(long a64, int b32, int c32)
	{
		return a64 + (long)b32 * (long)c32;
	}

	// (a32 * b32) >> 16
//	#define SKP_SMULWW(a32, b32)            SKP_MLA(SKP_SMULWB((a32), (b32)), (a32), SKP_RSHIFT_ROUND((b32), 16))
	static int SKP_SMULWW(int a32, int b32)
	{
		return SKP_SMULWB(a32, b32) + a32 * Silk_SigProc_FIX.SKP_RSHIFT_ROUND(b32, 16);
	}

	// a32 + ((b32 * c32) >> 16)
//	#define SKP_SMLAWW(a32, b32, c32)       SKP_MLA(SKP_SMLAWB((a32), (b32), (c32)), (b32), SKP_RSHIFT_ROUND((c32), 16))
	static int SKP_SMLAWW(int a32, int b32, int c32)
	{
		return SKP_SMLAWB(a32, b32, c32) + b32 * Silk_SigProc_FIX.SKP_RSHIFT_ROUND(c32, 16);
	}

	/* add/subtract with output saturated */
//	#define SKP_ADD_SAT32(a, b)             ((((a) + (b)) & 0x80000000) == 0 ?                              \
//	                                        ((((a) & (b)) & 0x80000000) != 0 ? SKP_int32_MIN : (a)+(b)) :   \
//	                                        ((((a) | (b)) & 0x80000000) == 0 ? SKP_int32_MAX : (a)+(b)) )
	static int SKP_ADD_SAT32(int a, int b)
	{
		if( ((a + b) & 0x80000000) == 0 )
			return ((a & b) & 0x80000000) != 0 ? Integer.MIN_VALUE : a+b;
		else
			return ((a | b) & 0x80000000) == 0 ? Integer.MAX_VALUE : a+b;
	}

//	#define SKP_SUB_SAT32(a, b)             ((((a)-(b)) & 0x80000000) == 0 ?                                        \
//	                                        (( (a) & ((b)^0x80000000) & 0x80000000) ? SKP_int32_MIN : (a)-(b)) :    \
//	                                        ((((a)^0x80000000) & (b)  & 0x80000000) ? SKP_int32_MAX : (a)-(b)) )
	static int SKP_SUB_SAT32(int a, int b)
	{
		if( ((a - b) & 0x80000000) == 0 )
			return ( a & (b^0x80000000) & 0x80000000) != 0 ? Integer.MIN_VALUE : a-b;
		else
			return ( (a^0x80000000) & b & 0x80000000) != 0 ? Integer.MAX_VALUE : a-b;
	}
	    
	static int SKP_Silk_CLZ16(short in16)
	{
//	    int out32 = 0;
//	    if( in16 == 0 )
//	    {
//	        return 16;
//	    }
//	    /* test nibbles */
//	    if( (in16 & 0xFF00) != 0 )
//	    {
//	        if( (in16 & 0xF000) != 0 ) 
//	        {
//	            in16 >>= 12;
//	        }
//	        else 
//	        {
//	            out32 += 4;
//	            in16 >>= 8;
//	        }
//	    } 
//	    else 
//	    {
//	        if( (in16 & 0xFFF0) != 0 ) 
//	        {
//	            out32 += 8;
//	            in16 >>= 4;
//	        }
//	        else 
//	        {
//	            out32 += 12;
//	        }
//	    }
//	    /* test bits and return */
//	    if( (in16 & 0xC) != 0 )
//	    {
//	        if( (in16 & 0x8) != 0 )
//	            return out32 + 0;
//	        else
//	            return out32 + 1;
//	    }
//	    else 
//	    {
//	        if( (in16 & 0xE) !=0 )
//	            return out32 + 2;
//	        else
//	            return out32 + 3;
//	    }
		return Integer.numberOfLeadingZeros((int)in16 & 0x0000FFFF) - 16;
	}

	static int SKP_Silk_CLZ32(int in32)
	{
//	    /* test highest 16 bits and convert to SKP_int16 */
//	    if( (in32 & 0xFFFF0000) !=0 ) 
//	    {
//	        return SKP_Silk_CLZ16((short)(in32 >> 16));
//	    }
//	    else
//	    {
//	        return SKP_Silk_CLZ16((short)in32) + 16;
//	    }
		return Integer.numberOfLeadingZeros(in32);
	}
}


