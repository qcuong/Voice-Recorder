package com.tranginc.trangnhe.voicerecoder.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tranginc.trangnhe.voicerecoder.R;

import java.util.Calendar;

public class SaveRecordDialogActivity extends Activity {

    private Context mContext;

    private EditText editName;
    private Button btnDelete, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_record_dialog);

        mContext = this;

        editName = (EditText) findViewById(R.id.editName);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSave = (Button) findViewById(R.id.btnSave);

        ButtonClickListener listener = new ButtonClickListener(SaveRecordDialogActivity.this);
        btnDelete.setOnClickListener(listener);
        btnSave.setOnClickListener(listener);

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        editName.setText(hour + "." + minute + " " + day + " thang " + month);
        editName.selectAll();
        editName.requestFocus();
    }


    class ButtonClickListener implements View.OnClickListener {

        private Activity activity;

        public ButtonClickListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnDelete) {
                setResult(RESULT_CANCELED);
                activity.finish();
                return;
            }

            if (v.getId() == R.id.btnSave) {
                String fileName = editName.getText().toString();
                if (fileName.isEmpty())
                    return;

                Intent intent = new Intent();
                intent.putExtra("FILE_NAME", fileName);
                setResult(RESULT_OK, intent);
                activity.finish();
                return;
            }
        }
    }
}
