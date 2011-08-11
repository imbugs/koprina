package net.java.sip.communicator.impl.neomedia.codec.audio.silk;

import javax.media.*;
import javax.media.format.*;

import net.java.sip.communicator.impl.neomedia.codec.*;

/**
 * The SILK encoder.
 * 
 * @author Dingxin Xu
 */
public class JavaEncoder
    extends AbstractCodecExt
{

    /**
     * Equals peak bitrate of 100 kbps
     */
    static final int MAX_BYTES_PER_FRAME = 250;
    
    /**
     * Maximum input frames in each packet
     */
    static final int MAX_INPUT_FRAMES = 5;
    
    /**
     * Maximum LBRR delay.
     */
    static final int MAX_LBRR_DELAY = 2;
    
    /**
     * Maximum frame length in samples.
     */
    static final int MAX_FRAME_LENGTH = 480;
    
    /**
     * frame length in ms.
     */
    static final int FRAME_LENGTH_MS = 20;
    
    /**
     * Maximum supported input sampling rate.
     */
    static final int MAX_API_FS_KHZ = 48;
    
    /**
     * The list of <tt>Format</tt>s of audio data supported as input by
     * <tt>JavaEncoder</tt> instances.
     */
    private static final Format[] SUPPORTED_INPUT_FORMATS;

    /**
     * The list of sample rates of audio data supported as input by
     * <tt>JavaEncoder</tt> instances.
     * API sampling frequency in Hertz of the encoder. Valid values are: 8000,
     * 12000, 16000, 24000, 32000, 44100, and 48000. This sampling frequency
     * represents the sampling frequency of the input signal to the encoder
     */
    static final double[] SUPPORTED_INPUT_SAMPLE_RATES
        = new double[] { 8000, 12000, 16000, 24000, 32000, 44100, 48000  };
    
    //TODO: Please check the supported input data format.
    static
    {
        int supportedInputCount = SUPPORTED_INPUT_SAMPLE_RATES.length;

        SUPPORTED_INPUT_FORMATS = new Format[supportedInputCount];
        for (int i = 0; i < supportedInputCount; i++)
        {
            SUPPORTED_INPUT_FORMATS[i]
                = new AudioFormat(
                        AudioFormat.LINEAR,//TODO
                        SUPPORTED_INPUT_SAMPLE_RATES[i],//sample rate
                        16,//sample size in bits
                        AudioFormat.NOT_SPECIFIED,//channels TODO 1 or 2 or??
                        AudioFormat.LITTLE_ENDIAN,
                        AudioFormat.SIGNED,
                        Format.NOT_SPECIFIED,
                        Format.NOT_SPECIFIED,
                        Format.shortArray); //data type TODO:
        }
    }
    
    /**
     * The list of <tt>Format</tt>s of audio data supported as output by
     * <tt>JavaEncoder</tt> instances.
     */
    private static final Format[] SUPPORTED_OUTPUT_FORMATS;
    
    //TODO: please check the supported output data format.
    static
    {
        int supportedOutputCount = SUPPORTED_INPUT_SAMPLE_RATES.length;

        SUPPORTED_OUTPUT_FORMATS = new Format[supportedOutputCount];
        for (int i = 0; i < supportedOutputCount; i++)
        {
            SUPPORTED_OUTPUT_FORMATS[i]
                = new AudioFormat(
                        "SILK codec",//TODO
                        SUPPORTED_INPUT_SAMPLE_RATES[i],//sample rate
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
     * Encoder state
     */
    SKP_Silk_encoder_state_FLP psEnc;
    
    /**
     * Encoder control struct.
     */
    private SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder

    /**
     * Initializes a new <code>JavaEncoder</code> instance.
     */
    public JavaEncoder()
    {
        super(
            "SILK Encoder",
            AudioFormat.class,
            SUPPORTED_OUTPUT_FORMATS);
        
        inputFormats = SUPPORTED_INPUT_FORMATS;
    }
    @Override
    protected void doClose()
    {
        psEnc = null;
        encControl = null;
    }

    @Override
    protected void doOpen() throws ResourceUnavailableException
    {
        psEnc = new SKP_Silk_encoder_state_FLP();
        encControl = new SKP_SILK_SDK_EncControlStruct();

        /* Reset Encoder */
        int ret = Silk_enc_API.SKP_Silk_SDK_InitEncoder(psEnc, encControl);
        if (ret != 0)
        {
            System.out.printf("\nSKP_Silk_reset_encoder returned %d", ret);
        }
        //TODO:how to set the encControl data elements???
        /*Set encoder control parameters. */
        //setEncoderControlParameters(encControl, )
    }

    @Override
    protected int doProcess(Buffer inputBuffer, Buffer outputBuffer)
    {
        //TODO: how to make sure input data is of type short[]???
        short[] inputData = (short[]) inputBuffer.getData();
        int inputLength = inputBuffer.getLength();
        int inputOffset = inputBuffer.getOffset();
        
        int outputOffset = outputBuffer.getOffset();
        int outputLength = outputBuffer.getLength();
        byte[] outputData
        = validateByteArraySize(
                outputBuffer,
                outputOffset + MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES);
        
        short[] nBytes = new short[1];
        /* max payload size */
        nBytes[0] = MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES;
        /* Silk Encoder */
        int ret =
            Silk_enc_API.SKP_Silk_SDK_Encode(psEnc, encControl, inputData, inputOffset,
                inputLength, outputData, outputOffset, nBytes);
        if (ret != 0)
        {
            System.out.printf("\nSKP_Silk_Encode returned %d", ret);
            return BUFFER_PROCESSED_FAILED;
        }
        
        /*TODO: update input buffer attribute */
        //inputBuffer.setLength();
        //inputBuffer.setOffset();
        
        /*update output buffer attribute. */
        outputOffset += nBytes[0];
        outputLength += nBytes[0];
        outputBuffer.setLength(outputLength);
        outputBuffer.setOffset(outputOffset);
        return BUFFER_PROCESSED_OK;     
    }

    /**
     * Set the encoder control struct parameters.
     * @param encCtrl the encoder control struct.
     * @param API_sampleRate Input signal sampling rate in Hertz; 8000/12000/16000/24000/32000/44100/48000.
     * @param maxInternalSampleRate  Maximum internal sampling rate in Hertz; 8000/12000/16000/24000.
     * @param packetSize Number of samples per packet; must be equivalent of 20, 40, 60, 80 or 100 ms.
     * @param bitRate Bitrate during active speech in bits/second; internally limited.
     * @param packetLossPercentage Uplink packet loss in percent (0-100).
     * @param complexity Complexity mode; 0 is lowest; 1 is medium and 2 is highest complexity.
     * @param useInBandFEC Flag to enable in-band Forward Error Correction (FEC); 0/1
     * @param useDTX Flag to enable discontinuous transmission (DTX); 0/1
     * @return
     */
    private int setEncoderControlParameters(SKP_SILK_SDK_EncControlStruct encCtrl, int API_sampleRate, int maxInternalSampleRate,
        int packetSize, int bitRate, int packetLossPercentage, int complexity, int useInBandFEC, int useDTX)
    {
        encCtrl.API_sampleRate = API_sampleRate;
        encCtrl.maxInternalSampleRate = maxInternalSampleRate;
        encCtrl.packetSize = packetSize;
        encCtrl.bitRate = bitRate;
        encCtrl.packetLossPercentage = packetLossPercentage;
        encCtrl.complexity = complexity;
        encCtrl.useInBandFEC = useInBandFEC;
        encCtrl.useDTX = useDTX;
        return 0;
        
    }
}
