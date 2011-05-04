package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import javax.media.*;
import javax.media.format.*;

import net.java.sip.communicator.impl.neomedia.codec.*;

public class JavaDecoder
    extends AbstractCodecExt
{
    // TODO:
    /**
     * Sampling frequency in Hertz of the decoder output signal. This sampling
     * frequency is independent of the internal sampling frequency of the
     * received signal. To preserve all transmitted information it should be at
     * least the maximum internal sampling frequency, that is,
     * maxInternalSampleRate of the encoder. Valid values are 8000, 12000,
     * 16000, 24000, 32000, 44100, and 48000.
     * 
     */
    private static final double[] SUPPORTED_INPUT_SAMPLE_RATE = new double[]
    { 8000, 12000, 16000, 24000, 32000, 44100, 48000 };

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
        super("SILK Decoder", AudioFormat.class, new AudioFormat[]
        { new AudioFormat("SILK encoding", AudioFormat.NOT_SPECIFIED, // sampleRate
            AudioFormat.NOT_SPECIFIED, // sampleSizeInBits
            AudioFormat.NOT_SPECIFIED, // channels
            AudioFormat.NOT_SPECIFIED, // endian
            AudioFormat.NOT_SPECIFIED, // signed
            AudioFormat.NOT_SPECIFIED, // frameSizeInBits
            AudioFormat.NOT_SPECIFIED, // frameRate
            new short[0].getClass() // datType
            ) });
        inputFormats = new AudioFormat[]
        { new AudioFormat("SILK encoding", AudioFormat.NOT_SPECIFIED, // sampleRate
            AudioFormat.NOT_SPECIFIED, // sampleSizeInBits
            AudioFormat.NOT_SPECIFIED, // channels
            AudioFormat.NOT_SPECIFIED, // endian
            AudioFormat.NOT_SPECIFIED, // signed
            AudioFormat.NOT_SPECIFIED, // frameSizeInBits
            AudioFormat.NOT_SPECIFIED, // frameRate
            new byte[0].getClass() // datType
            ) };

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
