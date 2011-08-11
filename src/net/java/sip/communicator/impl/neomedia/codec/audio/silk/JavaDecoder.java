package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import javax.media.*;
import javax.media.format.*;

import net.java.sip.communicator.impl.neomedia.codec.*;

/**
 * The SILK decoder.
 * 
 * @author Dingxin Xu
 */
public class JavaDecoder
    extends AbstractCodecExt
{
    /**
     * Sampling frequency in Hertz of the decoder output signal. This sampling
     * frequency is independent of the internal sampling frequency of the
     * received signal. To preserve all transmitted information it should be at
     * least the maximum internal sampling frequency, that is,
     * maxInternalSampleRate of the encoder. Valid values are 8000, 12000,
     * 16000, 24000, 32000, 44100, and 48000.
     * 
     */
    private static final double[] SUPPORTED_OUTPUT_SAMPLE_RATES = new double[]
    { 8000, 12000, 16000, 24000, 32000, 44100, 48000 };

    /**
     * The list of <tt>Format</tt>s of audio data supported as input by
     * <tt>JavaDecoder</tt> instances.
     */
    private static final Format[] SUPPORTED_INPUT_FORMATS;
    
    //TODO: Please check the supported input data format.
    static
    {
        int supportedInputCount = JavaEncoder.SUPPORTED_INPUT_SAMPLE_RATES.length;

        SUPPORTED_INPUT_FORMATS = new Format[supportedInputCount];
        for (int i = 0; i < supportedInputCount; i++)
        {
            SUPPORTED_INPUT_FORMATS[i]
                = new AudioFormat(
                        "SILK codec",//TODO
                        JavaEncoder.SUPPORTED_INPUT_SAMPLE_RATES[i],//sample rate
                        16,//sample size in bits
                        AudioFormat.NOT_SPECIFIED,//channels TODO 1 or 2 or??
                        AudioFormat.LITTLE_ENDIAN,
                        AudioFormat.SIGNED,
                        Format.NOT_SPECIFIED,
                        Format.NOT_SPECIFIED,
                        Format.byteArray); //data type TODO:
        }
    }
    
    /**
     * The list of <tt>Format</tt>s of audio data supported as output by
     * <tt>JavaDecoder</tt> instances.
     */
    private static final Format[] SUPPORTED_OUTPUT_FORMATS;
    
   //TODO: please check the supported output data format.
    static
    {
        int supportedOutputCount = SUPPORTED_OUTPUT_SAMPLE_RATES.length;

        SUPPORTED_OUTPUT_FORMATS = new Format[supportedOutputCount];
        for (int i = 0; i < supportedOutputCount; i++)
        {
            SUPPORTED_OUTPUT_FORMATS[i]
                = new AudioFormat(
                        "SILK codec",//TODO
                        SUPPORTED_OUTPUT_SAMPLE_RATES[i],//sample rate
                        16,//sample size in bits
                        AudioFormat.NOT_SPECIFIED,//channels TODO 1 or 2 or??
                        AudioFormat.LITTLE_ENDIAN,
                        AudioFormat.SIGNED,
                        Format.NOT_SPECIFIED,
                        Format.NOT_SPECIFIED,
                        Format.shortArray); //data type TODO:
        }
    }
    
    static final int MAX_BYTES_PER_FRAME = 1024;

    static final int MAX_INPUT_FRAMES = 5;

    static final int MAX_FRAME_LENGTH = 480;

    static final int FRAME_LENGTH_MS = 20;

    static final int MAX_API_FS_KHZ = 48;

    static final int MAX_LBRR_DELAY = 2;

    short[] out =
        new short[((FRAME_LENGTH_MS * MAX_API_FS_KHZ) << 1) * MAX_INPUT_FRAMES];

    /**
     * Sampling rate of output signal in Hz; default: 24000
     */
    private int API_Fs_Hz = 24000;

    /**
     * decoder state
     */
    private SKP_Silk_decoder_state psDec;

    /**
     * Decoder controller
     */
    private SKP_SILK_SDK_DecControlStruct DecControl;

    /**
     * Initializes a new <code>JavaDecoder</code> instance.
     */
    public JavaDecoder()
    {
        super("SILK Decoder", AudioFormat.class, SUPPORTED_OUTPUT_FORMATS);
        
        inputFormats = SUPPORTED_INPUT_FORMATS;
    }

    @Override
    protected void doClose()
    {
        // TODO Auto-generated method stub
        psDec = null;
        DecControl = null;
    }

    @Override
    protected void doOpen() throws ResourceUnavailableException
    {
        // TODO Auto-generated method stub
        psDec = new SKP_Silk_decoder_state();
        /* Reset decoder */
        int ret = Silk_dec_API.SKP_Silk_SDK_InitDecoder(psDec);
        if (ret != 0)
        {
            System.out.printf("SKP_Silk_InitDecoder returned %d\n", ret);
        }
        DecControl = new SKP_SILK_SDK_DecControlStruct();
        //TODO: how to set decoder control struct parameters???
        DecControl.API_sampleRate = API_Fs_Hz;

    }

    @Override
    protected int doProcess(Buffer inputBuffer, Buffer outputBuffer)
    {
        // TODO Auto-generated method stub
        byte[] inputData = (byte[]) inputBuffer.getData();
        int inputOffset = inputBuffer.getOffset();
        int inputLength = inputBuffer.getLength();

        int outOffset = 0;
        short[] lenPtr = new short[1];
        int len = 0;
        int frames = 0;
        int totLen = 0;
        do
        {
            int ret =
                Silk_dec_API
                    .SKP_Silk_SDK_Decode(psDec, DecControl, 0, inputData,
                        inputOffset, inputLength, out, outOffset, lenPtr);
            len = lenPtr[0];
            if (ret != 0)
            {
                System.out.printf("\nSKP_Silk_SDK_Decode returned %d", ret);
                return BUFFER_PROCESSED_FAILED;
            }

            frames++;
            outOffset += len;
            totLen += len;
            if (frames > MAX_INPUT_FRAMES)
            {
                /* Hack for corrupt stream that could generate too many frames */
                outOffset = 0;
                totLen = 0;
                frames = 0;
            }
        }
        while (DecControl.moreInternalDecoderFrames != 0);

        short[] outputData =
            validateShortArraySize(outputBuffer, outputBuffer.getOffset()
                + totLen);
        System.arraycopy(out, 0, outputData, outputBuffer.getOffset(), totLen);
        outputBuffer.setLength(outputBuffer.getOffset() + totLen);

        return BUFFER_PROCESSED_OK;
    }

    public void setFs(int FsHz)
    {
        API_Fs_Hz = FsHz;
    }

    private static short[] validateShortArraySize(Buffer buffer, int newSize)
    {
        Object data = buffer.getData();
        short[] newShorts;

        if (data instanceof short[])
        {
            short[] shorts = (short[]) data;

            if (shorts.length >= newSize)
                return shorts;

            newShorts = new short[newSize];
            System.arraycopy(shorts, 0, newShorts, 0, shorts.length);
        }
        else
        {
            newShorts = new short[newSize];
            buffer.setLength(0);
            buffer.setOffset(0);
        }

        buffer.setData(newShorts);
        return newShorts;
    }
}
