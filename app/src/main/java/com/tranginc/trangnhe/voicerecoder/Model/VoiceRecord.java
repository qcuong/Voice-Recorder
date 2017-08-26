package com.tranginc.trangnhe.voicerecoder.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by myema on 26/02/2017.
 */

public class VoiceRecord implements Serializable {
    private String recordName = null;
    private ArrayList<SubVoiceRecord> subVoiceRecords = new ArrayList<>();

    public VoiceRecord(String recordName) {
        this.recordName = recordName;
    }

    public String getRecordName() {
        return recordName;
    }

    public ArrayList<SubVoiceRecord> getSubVoiceRecords() {
        return subVoiceRecords;
    }

    public void addSubVoiceRecord(SubVoiceRecord sub){
        this.subVoiceRecords.add(sub);
    }
}
