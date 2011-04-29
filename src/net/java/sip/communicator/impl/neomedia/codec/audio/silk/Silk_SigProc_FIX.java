/**
 * Translated from the C code of Skype SILK codec (ver. 1.0.6)
 * Downloaded from http://developer.skype.com/silk/
 * 
 * Class "Silk_SigProc_FIX" is mainly based on 
 * ../SILK_SDK_SRC_FLP_v1.0.6/src/SKP_Silk_SigProc_FIX.h
 */
package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

/**
 *
 * @author Jing Dai
 */
class Silk_SigProc_FIX_constants
{
	static final int SKP_Silk_MAX_ORDER_LPC =           16;                    /* max order of the LPC analysis in schur() and k2a()    */
	static final int SKP_Silk_MAX_CORRELATION_LENGTH =  640;                   /* max input length to the correlation                   */

	/* Pitch estimator */
	static final int SKP_Silk_PITCH_EST_MIN_COMPLEX =       0;
	static final int SKP_Silk_PITCH_EST_MID_COMPLEX =       1;
	static final int SKP_Silk_PITCH_EST_MAX_COMPLEX =       2;
	
	/* parameter defining the size and accuracy of the piecewise linear  */
	/* cosine approximatin table.                                        */
	static final int LSF_COS_TAB_SZ_FIX =     128;
	/* rom table with cosine values */
//	(to see rom table value, refer to Silk_LSF_cos_table.java)
}

public class Silk_SigProc_FIX 
	extends Silk_SigProc_FIX_constants
{
	/********************************************************************/
	/*                                MACROS                            */
	/********************************************************************/

	/* Rotate a32 right by 'rot' bits. Negative rot values result in rotating
	   left. Output is 32bit int.
	   Note: contemporary compilers recognize the C expression below and
	   compile it into a 'ror' instruction if available. No need for inline ASM! */
	static int SKP_ROR32( int a32, int rot )
	{
	    if(rot <= 0)
	        return ((a32 << -rot) | (a32 >>> (32 + rot)));
	    else
	        return ((a32 << (32 - rot)) | (a32 >>> rot));
	}

	/* Define 4-byte aligned array of SKP_int16 */
//	#define SKP_array_of_int16_4_byte_aligned( arrayName, nElements )    \
//	    SKP_int32 dummy_int32 ## arrayName;                                \
//	    SKP_int16 arrayName[ (nElements) ]

	/* Useful Macros that can be adjusted to other platforms */
//	#define SKP_memcpy(a, b, c)                memcpy((a), (b), (c))    /* Dest, Src, ByteCount */
//	#define SKP_memset(a, b, c)                memset((a), (b), (c))    /* Dest, value, ByteCount */
//	#define SKP_memmove(a, b, c)               memmove((a), (b), (c))    /* Dest, Src, ByteCount */
	/* fixed point macros */

	// (a32 * b32) output have to be 32bit int
//	#define SKP_MUL(a32, b32)                  ((a32) * (b32))
	static int SKP_MUL(int a32, int b32)
	{
		return a32*b32;
	}

	// (a32 * b32) output have to be 32bit uint
//	#define SKP_MUL_uint(a32, b32)             SKP_MUL(a32, b32)
	static long SKP_MUL_uint(long a32, long b32)
	{
		return a32*b32;
	}

	// a32 + (b32 * c32) output have to be 32bit int
//	#define SKP_MLA(a32, b32, c32)             SKP_ADD32((a32),((b32) * (c32)))
	static int SKP_MLA(int a32, int b32, int c32)
	{
		return a32 + b32*c32;
	}

	// a32 + (b32 * c32) output have to be 32bit uint
//	#define SKP_MLA_uint(a32, b32, c32)        SKP_MLA(a32, b32, c32)
	static long SKP_MLA_uint(long a32, long b32, long c32)
	{
		return a32 + b32*c32;
	}

	// ((a32 >> 16)  * (b32 >> 16)) output have to be 32bit int
//	#define SKP_SMULTT(a32, b32)               (((a32) >> 16) * ((b32) >> 16))
	static int SKP_SMULTT(int a32, int b32)
	{
		return (((a32) >> 16) * ((b32) >> 16));
	}

	// a32 + ((a32 >> 16)  * (b32 >> 16)) output have to be 32bit int
//	#define SKP_SMLATT(a32, b32, c32)          SKP_ADD32((a32),((b32) >> 16) * ((c32) >> 16))
	static int SKP_SMLATT(int a32, int b32, int c32)
	{
		return (a32) + ((b32) >> 16) * ((c32) >> 16);
	}

//	#define SKP_SMLALBB(a64, b16, c16)         SKP_ADD64((a64),(SKP_int64)((SKP_int32)(b16) * (SKP_int32)(c16)))
	static long SKP_SMLALBB(long a64, short b16, short c16)
	{
		return (a64) + (long)((int)(b16) * (int)(c16));
	}

	// (a32 * b32)
//	#define SKP_SMULL(a32, b32)                ((SKP_int64)(a32) * /*(SKP_int64)*/(b32))
	static long SKP_SMULL(int a32, int b32)
	{
		return ((long)(a32) * /*(SKP_int64)*/(b32));
	}

	// multiply-accumulate macros that allow overflow in the addition (ie, no asserts in debug mode)
//	#define SKP_MLA_ovflw(a32, b32, c32)       SKP_MLA(a32, b32, c32)
	static int SKP_MLA_ovflw(int a32, int b32, int c32)
	{
		return a32 + b32*c32;
	}
//	#ifndef SKP_SMLABB_ovflw
//	#    define SKP_SMLABB_ovflw(a32, b32, c32)    SKP_SMLABB(a32, b32, c32)
//	#endif
	static int SKP_SMLABB_ovflw(int a32, int b32, int c32)
	{
		return ((a32) + ((int)((short)(b32))) * (int)((short)(c32)));
	}
//	#define SKP_SMLABT_ovflw(a32, b32, c32)    SKP_SMLABT(a32, b32, c32)
	static int SKP_SMLABT_ovflw(int a32, int b32, int c32)
	{
		return ((a32) + ((int)((short)(b32))) * ((c32) >> 16));
	}
	//#define SKP_SMLATT_ovflw(a32, b32, c32)    SKP_SMLATT(a32, b32, c32)
	static int SKP_SMLATT_ovflw(int a32, int b32, int c32)
	{
		return (a32) + ((b32) >> 16) * ((c32) >> 16);
	}
//	#define SKP_SMLAWB_ovflw(a32, b32, c32)    SKP_SMLAWB(a32, b32, c32)
	static int SKP_SMLAWB_ovflw(int a32, int b32, int c32)
	{
		return ((a32) + ((((b32) >> 16) * (int)((short)(c32))) + ((((b32) & 0x0000FFFF) * (int)((short)(c32))) >> 16)));
	}
//	#define SKP_SMLAWT_ovflw(a32, b32, c32)    SKP_SMLAWT(a32, b32, c32)
	static int SKP_SMLAWT_ovflw(int a32, int b32, int c32)
	{
		return ((a32) + (((b32) >> 16) * ((c32) >> 16)) + ((((b32) & 0x0000FFFF) * ((c32) >> 16)) >> 16));
	}

//	#define SKP_DIV64_32(a64, b32)             ((a64)/(b32))        /* TODO: rewrite it as a set of SKP_DIV32.*/
	static long SKP_DIV64_32(long a64, int b32)
	{
		return a64/b32;
	}

//	#define SKP_DIV32_16(a32, b16)             ((SKP_int32)((a32) / (b16)))
	static int SKP_DIV32_16(int a32, short b16)
	{
		return ((int)((a32) / (b16)));
	}
//	#define SKP_DIV32(a32, b32)                ((SKP_int32)((a32) / (b32)))
	static int SKP_DIV32_32(int a32, int b32)
	{
		return ((int)((a32) / (b32)));
	}

	// These macros enables checking for overflow in SKP_Silk_API_Debug.h
//	#define SKP_ADD16(a, b)                    ((a) + (b))
	static short SKP_ADD16(short a, short b)
	{
		return (short)(a+b);
	}
//	#define SKP_ADD32(a, b)                    ((a) + (b))
	static int SKP_ADD32(int a, int b)
	{
		return a+b;
	}
//	#define SKP_ADD64(a, b)                    ((a) + (b))
	static long SKP_ADD64(long a, long b)
	{
		return a+b;
	}

//	#define SKP_SUB16(a, b)                    ((a) - (b))
	static short SKP_SUB16(short a, short b)
	{
		return (short)(a-b);
	}
//	#define SKP_SUB32(a, b)                    ((a) - (b))
	static int SKP_SUB32(int a, int b)
	{
		return a-b;
	}
//	#define SKP_SUB64(a, b)                    ((a) - (b))
	static long SKP_SUB64(long a, long b)
	{
		return a-b;
	}

//	#define SKP_SAT8(a)                        ((a) > SKP_int8_MAX ? SKP_int8_MAX  : \
//	                                           ((a) < SKP_int8_MIN ? SKP_int8_MIN  : (a)))
	static int SKP_SAT8(int a)
	{
		return ((a) > Byte.MAX_VALUE ? Byte.MAX_VALUE  : ((a) < Byte.MIN_VALUE ? Byte.MIN_VALUE  : (a)));
	}
//	#define SKP_SAT16(a)                       ((a) > SKP_int16_MAX ? SKP_int16_MAX : \
//	                                           ((a) < SKP_int16_MIN ? SKP_int16_MIN : (a)))
	static int SKP_SAT16(int a)
	{
		return ((a) > Short.MAX_VALUE ? Short.MAX_VALUE : ((a) < Short.MIN_VALUE ? Short.MIN_VALUE : (a)));
	}
//	#define SKP_SAT32(a)                       ((a) > SKP_int32_MAX ? SKP_int32_MAX : \
//	                                           ((a) < SKP_int32_MIN ? SKP_int32_MIN : (a)))
	static long SKP_SAT32(long a)
	{
		return ((a) > Integer.MAX_VALUE ? Integer.MAX_VALUE : ((a) < Integer.MIN_VALUE ? Integer.MIN_VALUE : (a)));
	}

//	#define SKP_CHECK_FIT8(a)                  (a)
	static byte SKP_CHECK_FIT8(int a)
	{
		return (byte)a;
	}
//	#define SKP_CHECK_FIT16(a)                 (a)
	static short SKP_CHECK_FIT16(int a)
	{
		return (short)a;
	}
//	#define SKP_CHECK_FIT32(a)                 (a)
	static int SKP_CHECK_FIT32(int a)
	{
		return a;
	}

//	#define SKP_ADD_SAT16(a, b)                (SKP_int16)SKP_SAT16( SKP_ADD32( (SKP_int32)(a), (b) ) )
	static short SKP_ADD_SAT16(short a, short b)
	{
		return (short)SKP_SAT16( (int)(a) + (b) );
	}
//	#define SKP_ADD_SAT64(a, b)                ((((a) + (b)) & 0x8000000000000000LL) == 0 ?                            \
//	                                           ((((a) & (b)) & 0x8000000000000000LL) != 0 ? SKP_int64_MIN : (a)+(b)) :    \
//	                                           ((((a) | (b)) & 0x8000000000000000LL) == 0 ? SKP_int64_MAX : (a)+(b)) )
	static long SKP_ADD_SAT64(long a, long b)
	{
		if( ((a + b) & 0x8000000000000000L) == 0 )
			return ((a & b) & 0x8000000000000000L) != 0 ? Long.MIN_VALUE : a+b;
		else
			return ((a | b) & 0x8000000000000000L) == 0 ? Long.MAX_VALUE : a+b;
	}

//	#define SKP_SUB_SAT16(a, b)                (SKP_int16)SKP_SAT16( SKP_SUB32( (SKP_int32)(a), (b) ) )
	static short SKP_SUB_SAT16(short a, short b)
	{
		return (short)SKP_SAT16( (int)(a) - (b) );
	}
//	#define SKP_SUB_SAT64(a, b)                ((((a)-(b)) & 0x8000000000000000LL) == 0 ?                                                    \
//	                                           (( (a) & ((b)^0x8000000000000000LL) & 0x8000000000000000LL) ? SKP_int64_MIN : (a)-(b)) :    \
//	                                           ((((a)^0x8000000000000000LL) & (b)  & 0x8000000000000000LL) ? SKP_int64_MAX : (a)-(b)) )
	static long SKP_SUB_SAT64(long a, long b)
	{
		if( ((a - b) & 0x8000000000000000L) == 0 )
			return ( a & (b^0x8000000000000000L) & 0x8000000000000000L) != 0 ? Long.MIN_VALUE : a-b;
		else
			return ( (a^0x8000000000000000L) & b & 0x8000000000000000L) != 0 ? Long.MAX_VALUE : a-b;
	}
	
	/* Saturation for positive input values */ 
//	#define SKP_POS_SAT32(a)                   ((a) > SKP_int32_MAX ? SKP_int32_MAX : (a))
	static long SKP_POS_SAT32(long a)
	{
		return ((a) > Integer.MAX_VALUE ? Integer.MAX_VALUE : (a));
	}

	/* Add with saturation for positive input values */ 
//	#define SKP_ADD_POS_SAT8(a, b)             ((((a)+(b)) & 0x80)                 ? SKP_int8_MAX  : ((a)+(b)))
	static byte SKP_ADD_POS_SAT8(byte a, byte b)
	{
		return ((a+b) & 0x80) != 0                 ? Byte.MAX_VALUE  : (byte)(a+b);
	}
//	#define SKP_ADD_POS_SAT16(a, b)            ((((a)+(b)) & 0x8000)               ? SKP_int16_MAX : ((a)+(b)))
	static short SKP_ADD_POS_SAT16(short a, short b)
	{
		return ((a+b) & 0x8000) != 0               ? Short.MAX_VALUE : (short)(a+b);
	}
//	#define SKP_ADD_POS_SAT32(a, b)            ((((a)+(b)) & 0x80000000)           ? SKP_int32_MAX : ((a)+(b)))
	static int SKP_ADD_POS_SAT32(int a, int b)
	{
		return ((((a)+(b)) & 0x80000000) != 0           ? Integer.MAX_VALUE : ((a)+(b)));
	}
//	#define SKP_ADD_POS_SAT64(a, b)            ((((a)+(b)) & 0x8000000000000000LL) ? SKP_int64_MAX : ((a)+(b)))
	static long SKP_ADD_POS_SAT64(long a, long b)
	{
		return ((((a)+(b)) & 0x8000000000000000L) != 0 ? Long.MAX_VALUE : ((a)+(b)));
	}

//	#define SKP_LSHIFT8(a, shift)              ((a)<<(shift))                // shift >= 0, shift < 8
	static byte SKP_LSHIFT8(byte a, int shift)
	{
		return (byte)(a<<shift);
	}
//	#define SKP_LSHIFT16(a, shift)             ((a)<<(shift))                // shift >= 0, shift < 16
	static short SKP_LSHIFT16(short a, int shift)
	{
		return (short)(a<<shift);
	}
//	#define SKP_LSHIFT32(a, shift)             ((a)<<(shift))                // shift >= 0, shift < 32
	static int SKP_LSHIFT32(int a, int shift)
	{
		return a<<shift;
	}
//	#define SKP_LSHIFT64(a, shift)             ((a)<<(shift))                // shift >= 0, shift < 64
	static long SKP_LSHIFT64(long a, int shift)
	{
		return a<<shift;
	}
//	#define SKP_LSHIFT(a, shift)               SKP_LSHIFT32(a, shift)        // shift >= 0, shift < 32
	static int SKP_LSHIFT(int a, int shift)
	{
		return a<<shift;
	}

//	#define SKP_RSHIFT8(a, shift)              ((a)>>(shift))                // shift >= 0, shift < 8
	static byte SKP_RSHIFT8(byte a, int shift)
	{
		return (byte)(a>>shift);
	}
//	#define SKP_RSHIFT16(a, shift)             ((a)>>(shift))                // shift >= 0, shift < 16
	static short SKP_RSHIFT16(short a, int shift)
	{
		return (short)(a>>shift);
	}
//	#define SKP_RSHIFT32(a, shift)             ((a)>>(shift))                // shift >= 0, shift < 32
	static int SKP_RSHIFT32(int a, int shift)
	{
		return a>>shift;
	}
//	#define SKP_RSHIFT64(a, shift)             ((a)>>(shift))                // shift >= 0, shift < 64
	static long SKP_RSHIFT64(long a, int shift)
	{
		return a>>shift;
	}
//	#define SKP_RSHIFT(a, shift)               SKP_RSHIFT32(a, shift)        // shift >= 0, shift < 32
	static int SKP_RSHIFT(int a, int shift)
	{
		return a>>shift;
	}

	/* saturates before shifting */
//	#define SKP_LSHIFT_SAT16(a, shift)         (SKP_LSHIFT16( SKP_LIMIT( (a), SKP_RSHIFT16( SKP_int16_MIN, (shift) ),    \
//	                                                                          SKP_RSHIFT16( SKP_int16_MAX, (shift) ) ), (shift) ))
	static short SKP_LSHIFT_SAT16(short a, int shift)
	{
		return SKP_LSHIFT16( SKP_LIMIT_16( a, (short)(Short.MIN_VALUE>>shift), (short)(Short.MAX_VALUE>>shift) ), shift );
	}
//	#define SKP_LSHIFT_SAT32(a, shift)         (SKP_LSHIFT32( SKP_LIMIT( (a), SKP_RSHIFT32( SKP_int32_MIN, (shift) ),    \
//	                                                                          SKP_RSHIFT32( SKP_int32_MAX, (shift) ) ), (shift) ))
	static int SKP_LSHIFT_SAT32(int a, int shift)
	{
		return SKP_LSHIFT32( SKP_LIMIT( a, Integer.MIN_VALUE>>shift, Integer.MAX_VALUE>>shift ), shift );
	}

//	#define SKP_LSHIFT_ovflw(a, shift)        ((a)<<(shift))        // shift >= 0, allowed to overflow
	static int SKP_LSHIFT_ovflw(int a, int shift)
	{
		return a<<shift;
	}
//	#define SKP_LSHIFT_uint(a, shift)         ((a)<<(shift))        // shift >= 0
	static int SKP_LSHIFT_uint(int a, int shift)
	{
		return a<<shift;
	}
//	#define SKP_RSHIFT_uint(a, shift)         ((a)>>(shift))        // shift >= 0
	static int SKP_RSHIFT_uint(int a, int shift)
	{
		return a>>>shift;
	}

//	#define SKP_ADD_LSHIFT(a, b, shift)       ((a) + SKP_LSHIFT((b), (shift)))            // shift >= 0
	static int SKP_ADD_LSHIFT(int a, int b, int shift)
	{
		return a + (b<<shift);
	}
//	#define SKP_ADD_LSHIFT32(a, b, shift)     SKP_ADD32((a), SKP_LSHIFT32((b), (shift)))    // shift >= 0
	static int SKP_ADD_LSHIFT32(int a, int b, int shift)
	{
		return a + (b<<shift);
	}
//	#define SKP_ADD_LSHIFT_uint(a, b, shift)  ((a) + SKP_LSHIFT_uint((b), (shift)))        // shift >= 0
	static int SKP_ADD_LSHIFT_uint(int a, int b, int shift)
	{
		return a + (b<<shift);
	}
//	#define SKP_ADD_RSHIFT(a, b, shift)       ((a) + SKP_RSHIFT((b), (shift)))            // shift >= 0
	static int SKP_ADD_RSHIFT(int a, int b, int shift)
	{
		return a + (b>>shift);
	}
//	#define SKP_ADD_RSHIFT32(a, b, shift)     SKP_ADD32((a), SKP_RSHIFT32((b), (shift)))    // shift >= 0
	static int SKP_ADD_RSHIFT32(int a, int b, int shift)
	{
		return a + (b>>shift);
	}
//	#define SKP_ADD_RSHIFT_uint(a, b, shift)  ((a) + SKP_RSHIFT_uint((b), (shift)))        // shift >= 0
	static int SKP_ADD_RSHIFT_uint(int a, int b, int shift)
	{
		return a + (b>>>shift);
	}
//	#define SKP_SUB_LSHIFT32(a, b, shift)     SKP_SUB32((a), SKP_LSHIFT32((b), (shift)))    // shift >= 0
	static int SKP_SUB_LSHIFT32(int a, int b, int shift)
	{
		return a - (b<<shift);
	}
//	#define SKP_SUB_RSHIFT32(a, b, shift)     SKP_SUB32((a), SKP_RSHIFT32((b), (shift)))    // shift >= 0
	static int SKP_SUB_RSHIFT32(int a, int b, int shift)
	{
		return a - (b>>shift);
	}

	/* Requires that shift > 0 */
//	#define SKP_RSHIFT_ROUND(a, shift)        ((shift) == 1 ? ((a) >> 1) + ((a) & 1) : (((a) >> ((shift) - 1)) + 1) >> 1)
	static int SKP_RSHIFT_ROUND(int a, int shift)
	{
		return shift == 1 ? (a >> 1) + (a & 1) : ((a >> (shift - 1)) + 1) >> 1;
	}
//	#define SKP_RSHIFT_ROUND64(a, shift)      ((shift) == 1 ? ((a) >> 1) + ((a) & 1) : (((a) >> ((shift) - 1)) + 1) >> 1)
	static long SKP_RSHIFT_ROUND64(long a, int shift)
	{
		return shift == 1 ? (a >> 1) + (a & 1) : ((a >> (shift - 1)) + 1) >> 1;
	}

	/* Number of rightshift required to fit the multiplication */
//	#define SKP_NSHIFT_MUL_32_32(a, b)        ( -(31- (32-SKP_Silk_CLZ32(SKP_abs(a)) + (32-SKP_Silk_CLZ32(SKP_abs(b))))) )
	static int SKP_NSHIFT_MUL_32_32(int a, int b)
	{
		return -(31- (32-Silk_macros.SKP_Silk_CLZ32(Math.abs(a)) + (32-Silk_macros.SKP_Silk_CLZ32(Math.abs(b)))));
	}
//	#define SKP_NSHIFT_MUL_16_16(a, b)        ( -(15- (16-SKP_Silk_CLZ16(SKP_abs(a)) + (16-SKP_Silk_CLZ16(SKP_abs(b))))) )
	static int SKP_NSHIFT_MUL_16_16(short a, short b)
	{
		return -(15- (16-Silk_macros.SKP_Silk_CLZ16((short)Math.abs(a)) + (16-Silk_macros.SKP_Silk_CLZ16((short)Math.abs(b)))));
	}


//	#define SKP_min(a, b)                     (((a) < (b)) ? (a) : (b))
	static int SKP_min(int a, int b)
	{
		return a<b ? a:b;
	}
//	#define SKP_max(a, b)                     (((a) > (b)) ? (a) : (b))
	static int SKP_max(int a, int b)
	{
		return a>b ? a:b;
	}

	/* Macro to convert floating-point constants to fixed-point */
//	#define SKP_FIX_CONST( C, Q )             ((SKP_int32)((C) * (1 << (Q)) + 0.5))
	static int SKP_FIX_CONST( float C, int Q )
	{
		return (int)(C * (1 << Q) + 0.5);
	}

	/* SKP_min() versions with typecast in the function call */
	static int SKP_min_int(int a, int b)
	{
	    return (((a) < (b)) ? (a) : (b));
	}
	static short SKP_min_16(short a, short b)
	{
	    return (((a) < (b)) ? (a) : (b));
	}
	static int SKP_min_32(int a, int b)
	{
	    return (((a) < (b)) ? (a) : (b));
	}
	static long SKP_min_64(long a, long b)
	{
	    return (((a) < (b)) ? (a) : (b));
	}

	/* SKP_min() versions with typecast in the function call */
	static int SKP_max_int(int a, int b)
	{
	    return (((a) > (b)) ? (a) : (b));
	}
	static short SKP_max_16(short a, short b)
	{
	    return (((a) > (b)) ? (a) : (b));
	}
	static int SKP_max_32(int a, int b)
	{
	    return (((a) > (b)) ? (a) : (b));
	}
	static long SKP_max_64(long a, long b)
	{
	    return (((a) > (b)) ? (a) : (b));
	}

//	#define SKP_LIMIT( a, limit1, limit2)    ((limit1) > (limit2) ? ((a) > (limit1) ? (limit1) : ((a) < (limit2) ? (limit2) : (a))) \
//	                                                             : ((a) > (limit2) ? (limit2) : ((a) < (limit1) ? (limit1) : (a))))
	static int SKP_LIMIT( int a, int limit1, int limit2)
	{
		if( limit1 > limit2 )
			return a > limit1 ? limit1 : (a < limit2 ? limit2 : a);
		else
			return a > limit2 ? limit2 : (a < limit1 ? limit1 : a);
	}
	static float SKP_LIMIT( float a, float limit1, float limit2)
	{
		if( limit1 > limit2 )
			return a > limit1 ? limit1 : (a < limit2 ? limit2 : a);
		else
			return a > limit2 ? limit2 : (a < limit1 ? limit1 : a);
	}

//	#define SKP_LIMIT_int SKP_LIMIT
	static int SKP_LIMIT_int( int a, int limit1, int limit2)
	{
		if( limit1 > limit2 )
			return a > limit1 ? limit1 : (a < limit2 ? limit2 : a);
		else
			return a > limit2 ? limit2 : (a < limit1 ? limit1 : a);
	}
//	#define SKP_LIMIT_16 SKP_LIMIT
	static short SKP_LIMIT_16( short a, short limit1, short limit2)
	{
		if( limit1 > limit2 )
			return a > limit1 ? limit1 : (a < limit2 ? limit2 : a);
		else
			return a > limit2 ? limit2 : (a < limit1 ? limit1 : a);
	}
//	#define SKP_LIMIT_32 SKP_LIMIT
	static int SKP_LIMIT_32( int a, int limit1, int limit2)
	{
		if( limit1 > limit2 )
			return a > limit1 ? limit1 : (a < limit2 ? limit2 : a);
		else
			return a > limit2 ? limit2 : (a < limit1 ? limit1 : a);
	}

	//#define SKP_non_neg(a)                 ((a) & ((-(a)) >> (8 * sizeof(a) - 1)))   /* doesn't seem faster than SKP_max(0, a);

//	#define SKP_abs(a)                       (((a) >  0)  ? (a) : -(a))            // Be careful, SKP_abs returns wrong when input equals to SKP_intXX_MIN
	static int SKP_abs(int a)
	{
		return  (((a) >  0)  ? (a) : -(a));
	}
//	#define SKP_abs_int(a)                   (((a) ^ ((a) >> (8 * sizeof(a) - 1))) - ((a) >> (8 * sizeof(a) - 1)))
	static int SKP_abs_int(int a)
	{
		return (((a) ^ ((a) >> (Integer.SIZE - 1))) - ((a) >> (Integer.SIZE - 1)));
	}
//	#define SKP_abs_int32(a)                 (((a) ^ ((a) >> 31)) - ((a) >> 31))
	static int SKP_abs_int32(int a)
	{
		return (((a) ^ ((a) >> 31)) - ((a) >> 31));
	}
//	#define SKP_abs_int64(a)                 (((a) >  0)  ? (a) : -(a))  
	static long SKP_abs_int64(long a)
	{
		return (((a) >  0)  ? (a) : -(a));
	}

//	#define SKP_sign(a)                      ((a) > 0 ? 1 : ( (a) < 0 ? -1 : 0 ))
	static int SKP_sign(int a)
	{
		return ((a) > 0 ? 1 : ( (a) < 0 ? -1 : 0 ));
	}

//	#define SKP_sqrt(a)                      (sqrt(a))
	static double SKP_sqrt(int a)
	{
		return Math.sqrt(a);
	}

	/* PSEUDO-RANDOM GENERATOR                                                          */
	/* Make sure to store the result as the seed for the next call (also in between     */
	/* frames), otherwise result won't be random at all. When only using some of the    */
	/* bits, take the most significant bits by right-shifting. Do not just mask off     */
	/* the lowest bits.                                                                 */
//	#define SKP_RAND(seed)                   (SKP_MLA_ovflw(907633515, (seed), 196314165))
	static int SKP_RAND(int seed)
	{
		return 907633515 + seed*196314165;
	}

	// Add some multiplication functions that can be easily mapped to ARM.

//	    SKP_SMMUL: Signed top word multiply. 
//	        ARMv6        2 instruction cycles. 
//	        ARMv3M+        3 instruction cycles. use SMULL and ignore LSB registers.(except xM) 
	//#define SKP_SMMUL(a32, b32)            (SKP_int32)SKP_RSHIFT(SKP_SMLAL(SKP_SMULWB((a32), (b32)), (a32), SKP_RSHIFT_ROUND((b32), 16)), 16)
	// the following seems faster on x86
//	#define SKP_SMMUL(a32, b32)              (SKP_int32)SKP_RSHIFT64(SKP_SMULL((a32), (b32)), 32)
	static int SKP_SMMUL(int a32, int b32)
	{
		return (int)( ( (long)a32*b32 )>>32 );
	}
}


