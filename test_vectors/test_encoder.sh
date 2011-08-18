#!/bin/bash

KOPRINA_HOME=..
INPUTPATH=${KOPRINA_HOME}/test_vectors/test_input/
BITSTREAMPATH=${KOPRINA_HOME}/test_vectors/test_bitstream/
ENC=net.java.sip.communicator.impl.neomedia.codec.audio.silk.Encoder
CLASSPATH=${KOPRINA_HOME}/lib/installer-exclude/fmj.jar:${KOPRINA_HOME}/lib/installer-exclude/jmf.jar:$CLASSPATH

cd ../classes/


# 8 kHz

# 8 kHz, 60 ms, 8 kbps, complexity 0
PARAMS=8_kHz_60_ms_8_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_8_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 8000 -packetlength 60 -rate 8000 -complexity 0

# 8 kHz, 40 ms, 12 kbps, complexity 1
PARAMS=8_kHz_40_ms_12_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_8_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 8000 -packetlength 40 -rate 12000 -complexity 1


# 8 kHz, 20 ms, 20 kbps, 10% packet loss, FEC
PARAMS=8_kHz_20_ms_20_kbps_10_loss_FEC
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_8_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 8000 -packetlength 20 -rate 20000 -loss 10 -inbandFEC 1 


# 12 kHz

# 12 kHz, 60 ms, 10 kbps, complexity 0
PARAMS=12_kHz_60_ms_10_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_12_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 12000 -packetlength 60 -rate 10000 -complexity 0


# 12 kHz, 40 ms, 16 kbps, complexity 1
PARAMS=12_kHz_40_ms_16_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_12_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 12000 -packetlength 40 -rate 16000 -complexity 1

# 12 kHz, 20 ms, 24 kbps, 10% packet loss, FEC
PARAMS=12_kHz_20_ms_24_kbps_10_loss_FEC
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_12_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 12000 -packetlength 20 -rate 24000 -loss 10 -inbandFEC 1


# 16 kHz

# 16 kHz, 60 ms, 12 kbps, complexity 0
PARAMS=16_kHz_60_ms_12_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_16_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 16000 -packetlength 60 -rate 12000 -complexity 0


# 16 kHz, 40 ms, 20 kbps, complexity 1
PARAMS=16_kHz_40_ms_20_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_16_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 16000 -packetlength 40 -rate 20000 -complexity 1

# 16 kHz, 20 ms, 32 kbps, 10% packet loss, FEC
PARAMS=16_kHz_20_ms_32_kbps_10_loss_FEC
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_16_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 16000 -packetlength 20 -rate 32000 -loss 10 -inbandFEC 1


# 24 kHz

# 24 kHz, 60 ms, 16 kbps, complexity 0
PARAMS=24_kHz_60_ms_16_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_24_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 24000 -packetlength 60 -rate 16000 -complexity 0

# 24 kHz, 40 ms, 24 kbps, complexity 1
PARAMS=24_kHz_40_ms_24_kbps
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_24_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 24000 -packetlength 40 -rate 24000 -complexity 1

# 24 kHz, 20 ms, 40 kbps, 10% packet loss, FEC
PARAMS=24_kHz_20_ms_40_kbps_10_loss_FEC
java -classpath ${CLASSPATH} ${ENC} ${INPUTPATH}testvector_input_24_kHz.pcm ${BITSTREAMPATH}payload_${PARAMS}.bit -Fs_API 24000 -packetlength 20 -rate 40000 -loss 10 -inbandFEC 1


echo ""
echo "encoder test finished"
echo ""
