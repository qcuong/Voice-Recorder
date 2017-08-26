package com.tranginc.trangnhe.voicerecoder.Model;

/**
 * Created by myema on 26/02/2017.
 */

public class SubVoiceRecord {
    private String name;
    private String filePath;

    public SubVoiceRecord(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }
}
