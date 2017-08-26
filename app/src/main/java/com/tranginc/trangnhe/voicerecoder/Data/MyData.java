package com.tranginc.trangnhe.voicerecoder.Data;

import android.media.AudioFormat;
import android.os.Environment;

import com.tranginc.trangnhe.voicerecoder.Model.BufferSectionVoiceRecord;
import com.tranginc.trangnhe.voicerecoder.Model.BufferVoiceRecord;
import com.tranginc.trangnhe.voicerecoder.Model.VoiceRecord;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by myema on 19/02/2017.
 */

public class MyData {
    public static ArrayList<BufferSectionVoiceRecord> BUFFER_VOICE_RECORD_DATA = new ArrayList<>();
    //public static ArrayList<BufferVoiceRecord> BUFFER_WHOLE_VOICE_RECORD_DATA = new ArrayList<>();

    public static ArrayList<VoiceRecord> VOICE_RECORD_DATA = new ArrayList<>();

    public static int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    public static int BytesPerElement = 2; // 2 bytes in 16bit format
    public static final int RECORDER_SAMPLERATE = 8000; //44100, 22050, 11025, 8000
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final long DUARATION_OF_SECTION = 5000;

    public static String VOICE_RECORD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VoiceRecorder";
}
