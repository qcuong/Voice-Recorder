package com.tranginc.trangnhe.voicerecoder.Model;

import java.util.ArrayList;

/**
 * Created by myema on 27/02/2017.
 */

public class BufferSectionVoiceRecord {
    public ArrayList<BufferVoiceRecord> recordData = new ArrayList<>();

    public void addBufferData(BufferVoiceRecord bufferData) {
        recordData.add(bufferData);
    }

    public ArrayList<BufferVoiceRecord> getBufferData(){
        return recordData;
    }
}
