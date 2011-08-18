#!/bin/bash

KOPRINA_HOME=..
INPUTPATH=${KOPRINA_HOME}/test_vectors/test_input/
BITSTREAMPATH=${KOPRINA_HOME}/test_vectors/test_bitstream/
ENC=net.java.sip.communicator.impl.neomedia.codec.audio.silk.Encoder
CLASSPATH=${KOPRINA_HOME}/lib/installer-exclude/fmj.jar:${KOPRINA_HOME}/lib/installer-exclude/jmf.jar:$CLASSPATH

cd ../classes/

PARAMS=44kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=48kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 48000 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=32kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 32000 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=24kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 24000 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=16kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 16000 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=8kHz_20ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 8000 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_40ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 40 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_60ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 60 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_80ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 80 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_100ms_25kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 100 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_20ms_15kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 15000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 0 

PARAMS=44kHz_20ms_25kbps_10loss
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 10 

PARAMS=44kHz_20ms_25kbps_inbandFEC
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 1 -complexity 0 -DTX 0 

PARAMS=44kHz_20ms_25kbps_DTX
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 0 -inbandFEC 0 -complexity 0 -DTX 1

PARAMS=44kHz_20ms_25kbps_10loss_1comp
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 10 -complexity 1

PARAMS=44kHz_20ms_25kbps_10loss_1comp_1DTX
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 10 -complexity 1 -DTX 1

PARAMS=44kHz_20ms_25kbps_10loss_1FEC_2comp_1DTX
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 10 -inbandFEC 1 -complexity 2 -DTX 1 

PARAMS=44kHz_20ms_25kbps_10loss_1FEC_1comp_1DTX
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}sample_audio.in ${BITSTREAMPATH}payload_${PARAMS}.bit.out -Fs_API 44100 -Fs_maxInternal 24000 -packetlength 20 -rate 25000 -loss 10 -inbandFEC 1 -complexity 1 -DTX 1 

echo ""
echo "encoder test finished"
echo ""
