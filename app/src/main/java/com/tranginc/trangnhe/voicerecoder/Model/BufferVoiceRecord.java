package com.tranginc.trangnhe.voicerecoder.Model;

/**
 * Created by myema on 25/02/2017.
 */

public class BufferVoiceRecord {
    private short[] buffer = null;
    private int readBuffer = 0;


    public BufferVoiceRecord(short[] buffer, int readBuffer) {
        this.buffer = buffer;
        this.readBuffer = readBuffer;
    }


    public short[] getBuffer() {
        return buffer;
    }

    public int getReadBuffer() {
        return readBuffer;
    }

}
