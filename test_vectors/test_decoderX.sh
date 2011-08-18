#!/bin/bash

KOPRINA_HOME=..
INPUTPATH=${KOPRINA_HOME}/test_vectors/test_input/
BITSTREAMPATH=${KOPRINA_HOME}/test_vectors/test_bitstream/
OUTPUTPATH=${KOPRINA_HOME}/test_vectors/test_output/
DEC=net.java.sip.communicator.impl.neomedia.codec.audio.silk.Decoder
CLASSPATH=${KOPRINA_HOME}/lib/installer-exclude/fmj.jar:${KOPRINA_HOME}/lib/installer-exclude/jmf.jar:$CLASSPATH

cd ../classes/

PARAMS=44kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  


PARAMS=48kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 48000  
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}_44100.pcm.out -Fs_API 44100

PARAMS=32kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 32000

PARAMS=24kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 24000

PARAMS=16kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 16000

PARAMS=8kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 8000

PARAMS=44kHz_40ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_60ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_80ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_100ms_25kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_20ms_15kbps
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_20ms_25kbps_10loss
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}_10loss.pcm.out -Fs_API 44100 -loss 10

PARAMS=44kHz_20ms_25kbps_inbandFEC
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  


PARAMS=44kHz_20ms_25kbps_DTX
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  


PARAMS=44kHz_20ms_25kbps_10loss_1comp
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_20ms_25kbps_10loss_1comp_1DTX
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_20ms_25kbps_10loss_1FEC_2comp_1DTX
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

PARAMS=44kHz_20ms_25kbps_10loss_1FEC_1comp_1DTX
java -classpath ${CLASSPATH} ${DEC} ${BITSTREAMPATH}payload_${PARAMS}.bit.out ${OUTPUTPATH}sample_audio_${PARAMS}.pcm.out -Fs_API 44100  

echo ""
echo "encoder test finished"
echo ""
