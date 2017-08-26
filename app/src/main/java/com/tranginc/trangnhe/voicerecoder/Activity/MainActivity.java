package com.tranginc.trangnhe.voicerecoder.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.naman14.androidlame.AndroidLame;
import com.naman14.androidlame.LameBuilder;
import com.tranginc.trangnhe.voicerecoder.Data.MyData;
import com.tranginc.trangnhe.voicerecoder.Model.BufferSectionVoiceRecord;
import com.tranginc.trangnhe.voicerecoder.Model.SubVoiceRecord;
import com.tranginc.trangnhe.voicerecoder.R;
import com.tranginc.trangnhe.voicerecoder.Model.BufferVoiceRecord;
import com.tranginc.trangnhe.voicerecoder.Model.VoiceRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private Activity mActivity;

    private TextView tvTitle, tvTimeKeeper, tvDone;
    private Button btnRecorder;
    private AnimatedExpandableListView lvVoiceRecord;
    private MediaPlayer mediaPlayer;

    private ExampleAdapter adapter;


    private static final int SAVE_RECORD_DIALOG_RESULT_CODE = 1;

    //ENCODING_PCM_16BIT – 16 bit per sample
    //ENCODING_PCM_8BIT – 8 bit per sample
    //ENCODING_DEFAUL – default encoding

    private AudioRecord recorder = null;
    private boolean isRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = MainActivity.this;

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTimeKeeper = (TextView) findViewById(R.id.tvTimeKeeper);
        tvDone = (TextView) findViewById(R.id.tvDone);
        btnRecorder = (Button) findViewById(R.id.btnRecorder);
        lvVoiceRecord = (AnimatedExpandableListView) findViewById(R.id.lvVoiceRecord);

        btnRecorder.setOnClickListener(new ButtonListener());
        tvDone.setOnClickListener(new ButtonListener());
        lvVoiceRecord.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (lvVoiceRecord.isGroupExpanded(groupPosition)) {
                    lvVoiceRecord.collapseGroupWithAnimation(groupPosition);
                } else {
                    lvVoiceRecord.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });

        LoadVoiceRecord loadVoiceRecord = new LoadVoiceRecord();
        loadVoiceRecord.execute();
    }

    private void stopRecording() {
        if (recorder == null) {
            return;
        }

        isRecording = false;
    }


    @Override
    protected void onStop() {
        stopRecording();
        if (mediaPlayer != null)
            mediaPlayer.stop();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText((Context) mActivity, "requestCode=" + requestCode + "---resultCode=" + resultCode, Toast.LENGTH_LONG).show();

        if (requestCode != SAVE_RECORD_DIALOG_RESULT_CODE) {
            return;
        }

        if (resultCode != RESULT_OK)
            return;

        String FileName = data.getStringExtra("FILE_NAME");
        SaveVoiceRecord saveVoiceRecord = new SaveVoiceRecord(FileName);
        saveVoiceRecord.execute();
    }

    /*
    class ItemRecordListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VoiceRecord voiceRecord = MyData.VOICE_RECORD_DATA.get(position);

            if (mediaPlayer != null)
                mediaPlayer.stop();

            mediaPlayer = MediaPlayer.create(mActivity, Uri.parse(voiceRecord.getSubfilePath().get(0)));
            mediaPlayer.start();
        }
    }
    */
    class TimeRecordingKeeper extends AsyncTask<Void, String, Void> {
        private long startTime = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            startTime = System.currentTimeMillis();

            while (isRecording) {
                long recordingTime = System.currentTimeMillis();
                String text = "";
                long total = recordingTime - startTime;

                long milisecond = (total % 1000) / 10;

                long totalSecond = total / 1000;
                long second = totalSecond % 60;
                long minute = totalSecond / 60;

                if (minute == 0)
                    text = "00:";
                else if (minute < 10)
                    text = "0" + minute + ":";
                else text = "" + minute + ":";

                if (second == 0)
                    text = text + "00.";
                else if (second < 10)
                    text = text + "0" + second + ".";
                else text = text + second + ".";

                if (milisecond == 0)
                    text = text + "00";
                else if (milisecond < 10)
                    text = text + "0" + milisecond;
                else text = text + milisecond;


                publishProgress(text);

                try {
                    Thread.sleep(5);
                } catch (Exception e) {

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            tvTimeKeeper.setText(text);
        }
    }

    class StartRecording extends Thread {

        @Override
        public void run() {

            TimeRecordingKeeper timeRecordingKeeper = new TimeRecordingKeeper();

            MyData.BUFFER_VOICE_RECORD_DATA = new ArrayList<>();
            MyData.BufferElements2Rec = AudioRecord.getMinBufferSize(MyData.RECORDER_SAMPLERATE, MyData.RECORDER_CHANNELS, MyData.RECORDER_AUDIO_ENCODING);

            short[] buffer = null;


            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    MyData.RECORDER_SAMPLERATE, MyData.RECORDER_CHANNELS,
                    MyData.RECORDER_AUDIO_ENCODING, MyData.BufferElements2Rec * MyData.BytesPerElement);

            long timestart = 0;
            long timerecord = 0;

            try {
                int bytesRead = 0;
                isRecording = true;
                timeRecordingKeeper.execute();
                recorder.startRecording();
                timestart = System.currentTimeMillis();

                BufferSectionVoiceRecord bufferSectionVoiceRecord = new BufferSectionVoiceRecord();
                BufferSectionVoiceRecord bufferWholeVoiceRecord = new BufferSectionVoiceRecord();

                while (isRecording) {
                    buffer = new short[MyData.RECORDER_SAMPLERATE * 2 * 5];
                    bytesRead = recorder.read(buffer, 0, MyData.BufferElements2Rec);

                    if (bytesRead > 0) {
                        bufferSectionVoiceRecord.addBufferData(new BufferVoiceRecord(buffer, bytesRead));
                        bufferWholeVoiceRecord.addBufferData(new BufferVoiceRecord(buffer, bytesRead));
                    }

                    timerecord = System.currentTimeMillis();

                    if (timerecord - timestart >= MyData.DUARATION_OF_SECTION) {
                        Log.i("nhetrang",timestart + "-----" + timerecord + "-----" + bufferSectionVoiceRecord.getBufferData().size());
                        MyData.BUFFER_VOICE_RECORD_DATA.add(bufferSectionVoiceRecord);
                        bufferSectionVoiceRecord = new BufferSectionVoiceRecord();
                        timestart = timerecord;
                    }
                }

                if (bufferSectionVoiceRecord.getBufferData().size() > 0){
                    MyData.BUFFER_VOICE_RECORD_DATA.add(bufferSectionVoiceRecord);
                }

                MyData.BUFFER_VOICE_RECORD_DATA.add(bufferWholeVoiceRecord);

                recorder.stop();
                recorder.release();
                recorder = null;

            } catch (Exception e) {

            }
        }
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tvDone) {
                Intent intent = new Intent((Context) mActivity, SaveRecordDialogActivity.class);
                mActivity.startActivityForResult(intent, SAVE_RECORD_DIALOG_RESULT_CODE);
                return;
            }


            if (isRecording) {
                btnRecorder.setBackground(getDrawable(R.drawable.start_button_backgound));
                stopRecording();
                return;
            }

            btnRecorder.setBackground(getDrawable(R.drawable.pause_button_background));

            StartRecording startRecording = new StartRecording();
            startRecording.start();
        }
    }

    class SaveVoiceRecord extends AsyncTask<Void, String, Void> {
        private String fileName;

        public SaveVoiceRecord(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText((Context) mActivity, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // create folder
                VoiceRecord voiceRecord = new VoiceRecord(fileName);

                File voiceRecordFolder = new File(MyData.VOICE_RECORD_FOLDER + File.separator + fileName);
                voiceRecordFolder.mkdir();

                String filePath = "";
                File fileRecord = null;
                FileOutputStream outputStream = null;

                AndroidLame androidLame = null;

                int bytesEncoded = 0;
                BufferVoiceRecord bufferRead = null;
                byte[] mp3buffer = new byte[(int) (7200 + (MyData.RECORDER_SAMPLERATE * 2 * 5) * 2 * 1.25)];

                // for each section record
                BufferSectionVoiceRecord bufferSectionVoiceRecord;
                for (int sectionindex = 0; sectionindex < MyData.BUFFER_VOICE_RECORD_DATA.size(); sectionindex++) {
                    androidLame = new LameBuilder()
                            .setInSampleRate(MyData.RECORDER_SAMPLERATE)
                            .setOutChannels(1)
                            .setOutBitrate(32)
                            .setOutSampleRate(MyData.RECORDER_SAMPLERATE)
                            .build();
                    if (sectionindex + 1 == MyData.BUFFER_VOICE_RECORD_DATA.size()){
                        filePath = voiceRecordFolder.getAbsolutePath() + File.separator + "Whole Voice Record.mp3";
                    }
                    else {
                        filePath = voiceRecordFolder.getAbsolutePath() + File.separator + "Sub record " + (sectionindex + 1) + ".mp3";
                    }

                    fileRecord = new File(filePath);
                    outputStream = new FileOutputStream(fileRecord);

                    bufferSectionVoiceRecord = MyData.BUFFER_VOICE_RECORD_DATA.get(sectionindex);

                    // for each buffer data in each section
                    for (int i = 0; i < bufferSectionVoiceRecord.getBufferData().size(); i++) {
                        bufferRead = bufferSectionVoiceRecord.getBufferData().get(i);
                        bytesEncoded = androidLame.encode(bufferRead.getBuffer(), bufferRead.getBuffer(), bufferRead.getReadBuffer(), mp3buffer);
                        if (bytesEncoded > 0) {
                            outputStream.write(mp3buffer, 0, bytesEncoded);
                        }
                    }

                    int outputMp3buf = androidLame.flush(mp3buffer);
                    if (outputMp3buf > 0) {
                        outputStream.write(mp3buffer, 0, outputMp3buf);
                        outputStream.close();
                    }
                    androidLame.close();

                    publishProgress("Sub file " + sectionindex);

                    SubVoiceRecord sub = new SubVoiceRecord(fileRecord.getName(), fileRecord.getAbsolutePath());
                    voiceRecord.addSubVoiceRecord(sub);
                }

                MyData.VOICE_RECORD_DATA.add(voiceRecord);
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress("can not save record");
                return null;
            }
            publishProgress("successful save record");
            return null;
        }
    }

    class LoadVoiceRecord extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                MyData.VOICE_RECORD_DATA = new ArrayList<>();

                ArrayList<File> listFolder = new ArrayList<>();
                File voiceRecordFolder = new File(MyData.VOICE_RECORD_FOLDER);

                // if Voice Recorder folder is not exist
                // create Voice Recorder folder
                if (!voiceRecordFolder.exists()) {
                    voiceRecordFolder.mkdir();
                    return null;
                }

                // reading all sub folder in Voice Record folder
                File[] files = voiceRecordFolder.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        listFolder.add(file);
                    }
                }

                for (File folder : listFolder) {
                    files = folder.listFiles();
                    VoiceRecord voiceRecord = new VoiceRecord(folder.getName());
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".mp3")) {
                            SubVoiceRecord sub = new SubVoiceRecord(file.getName(), file.getAbsolutePath());
                            voiceRecord.addSubVoiceRecord(sub);
                        }
                    }
                    MyData.VOICE_RECORD_DATA.add(voiceRecord);
                    publishProgress();
                }

            } catch (Exception e) {
                Log.e("nhetrang", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new ExampleAdapter((Context) mActivity);
            adapter.setData(MyData.VOICE_RECORD_DATA);
            lvVoiceRecord.setAdapter(adapter);
        }
    }

    /*
    Using expandable List View
     */

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    /**
     * Adapter for our list of {@link VoiceRecord}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<VoiceRecord> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<VoiceRecord> items) {
            this.items = items;
        }

        @Override
        public SubVoiceRecord getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).getSubVoiceRecords().get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            final SubVoiceRecord item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                holder.hint = (TextView) convertView.findViewById(R.id.textHint);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.getName());
            holder.hint.setText(item.getFilePath());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer != null)
                        mediaPlayer.stop();

                    mediaPlayer = MediaPlayer.create(mActivity, Uri.parse(item.getFilePath()));
                    mediaPlayer.start();
                }
            });

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).getSubVoiceRecords().size();
        }

        @Override
        public VoiceRecord getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            VoiceRecord item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            holder.title.setText(item.getRecordName());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }
}