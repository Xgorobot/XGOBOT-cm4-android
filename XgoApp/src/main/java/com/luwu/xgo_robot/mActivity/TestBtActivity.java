package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mView.SpaceEditText;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;
//按十六进制输入 不需要输入0x
public class TestBtActivity extends AppCompatActivity {

    private TextView testWriteRespondTxt, testReadTxt;
    private SpaceEditText testWriteEdit, testWriteRespondEdit, testReadEdit;
    private Button testWriteBtn, testWriteRespondBtn, testReadBtn;
    private mListener mmListener;
    private byte[] writeBytes, writeRespondBytes, readBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bt);
        mmListener = new mListener();

        testWriteRespondTxt = findViewById(R.id.testWriteRespondTxt);
        testReadTxt = findViewById(R.id.testReadTxt);
        testWriteEdit = findViewById(R.id.testWriteEdit);
        testWriteEdit.setRawInputType(Configuration.KEYBOARD_NOKEYS);//默认弹出数字键盘
        testWriteRespondEdit = findViewById(R.id.testWriteRespondEdit);
        testWriteEdit.setRawInputType(Configuration.KEYBOARD_NOKEYS);
        testReadEdit = findViewById(R.id.testReadEdit);
        testWriteEdit.setRawInputType(Configuration.KEYBOARD_NOKEYS);
        testWriteBtn = findViewById(R.id.testWriteBtn);
        testWriteRespondBtn = findViewById(R.id.testWriteRespondBtn);
        testReadBtn = findViewById(R.id.testReadBtn);
        initEditText();
        testWriteBtn.setOnClickListener(mmListener);
        testWriteRespondBtn.setOnClickListener(mmListener);
        testReadBtn.setOnClickListener(mmListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(TestBtActivity.this);
    }

    private void initEditText() {
        testWriteEdit.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(byte[] bytes) {
                if (bytes != null) {
                    writeBytes = bytes;
                    testWriteBtn.setEnabled(true);
                } else {
                    testWriteBtn.setEnabled(false);
                }
            }
        });
        testWriteRespondEdit.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(byte[] bytes) {
                if (bytes != null) {
                    writeRespondBytes = bytes;
                    testWriteRespondBtn.setEnabled(true);
                } else {
                    testWriteRespondBtn.setEnabled(false);
                }
            }
        });
        testReadEdit.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(byte[] bytes) {
                if (bytes != null) {
                    readBytes = bytes;
                    testReadBtn.setEnabled(true);
                } else {
                    testReadBtn.setEnabled(false);
                }
            }
        });
    }

    class mListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.testWriteBtn:
                    MainActivity.addMessage(writeBytes);
                    break;
                case R.id.testWriteRespondBtn:
                    MainActivity.addMessageRespond(writeRespondBytes);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            byte[] temp = MainActivity.getMessageRespond();
                            if (temp != null) {
                                testWriteRespondTxt.setText(SpaceEditText.bytesToHex(temp));
                            }
                        }
                    }, 500);
                    break;
                case R.id.testReadBtn:
                    MainActivity.addMessageRead(readBytes);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            byte[] temp = MainActivity.getMessageRead();
                            if (temp != null) {
                                testReadTxt.setText(SpaceEditText.bytesToHex(temp));
                            }
                        }
                    }, 500);
                    break;

            }
        }
    }
}
