package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.PublicMethod;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

public class ActorActivity extends AppCompatActivity {

    private Button  actorResetBtn;
    private ImageButton actorBtnExit;
    private Switch actorWhileSwitch;
    private ButtonClickListener mButtonClickListener;
    private Button actorBtn1, actorBtn2, actorBtn3, actorBtn4, actorBtn5, actorBtn6, actorBtn7, actorBtn8, actorBtn9, actorBtn10, actorBtn11, actorBtn12, actorBtn13, actorBtn14, actorBtn15, actorBtn16, actorBtn17, actorBtn18, actorBtn19, actorBtn20;
    private byte[] action = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        actorWhileSwitch = findViewById(R.id.actorWhileSwitch);
        actorWhileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//动作轮播
                    MainActivity.addMessage(new byte[]{0x03, 0x01});
                } else {
                    MainActivity.addMessage(new byte[]{0x03, 0x00});
                }
            }
        });
        mButtonClickListener = new ButtonClickListener();
        actorBtnExit = findViewById(R.id.actorBtnExit);
        actorBtnExit.setOnClickListener(mButtonClickListener);
        actorResetBtn = findViewById(R.id.actorResetBtn);
        actorResetBtn.setOnClickListener(mButtonClickListener);
        actorBtn1 = findViewById(R.id.actorBtn1);
        actorBtn1.setOnClickListener(mButtonClickListener);
        actorBtn2 = findViewById(R.id.actorBtn2);
        actorBtn2.setOnClickListener(mButtonClickListener);
        actorBtn3 = findViewById(R.id.actorBtn3);
        actorBtn3.setOnClickListener(mButtonClickListener);
        actorBtn4 = findViewById(R.id.actorBtn4);
        actorBtn4.setOnClickListener(mButtonClickListener);
        actorBtn5 = findViewById(R.id.actorBtn5);
        actorBtn5.setOnClickListener(mButtonClickListener);
        actorBtn6 = findViewById(R.id.actorBtn6);
        actorBtn6.setOnClickListener(mButtonClickListener);
        actorBtn7 = findViewById(R.id.actorBtn7);
        actorBtn7.setOnClickListener(mButtonClickListener);
        actorBtn8 = findViewById(R.id.actorBtn8);
        actorBtn8.setOnClickListener(mButtonClickListener);
        actorBtn9 = findViewById(R.id.actorBtn9);
        actorBtn9.setOnClickListener(mButtonClickListener);
        actorBtn10 = findViewById(R.id.actorBtn10);
        actorBtn10.setOnClickListener(mButtonClickListener);
        actorBtn11 = findViewById(R.id.actorBtn11);
        actorBtn11.setOnClickListener(mButtonClickListener);
        actorBtn12 = findViewById(R.id.actorBtn12);
        actorBtn12.setOnClickListener(mButtonClickListener);
        actorBtn13 = findViewById(R.id.actorBtn13);
        actorBtn13.setOnClickListener(mButtonClickListener);
        actorBtn14 = findViewById(R.id.actorBtn14);
        actorBtn14.setOnClickListener(mButtonClickListener);
        actorBtn15 = findViewById(R.id.actorBtn15);
        actorBtn15.setOnClickListener(mButtonClickListener);
        actorBtn16 = findViewById(R.id.actorBtn16);
        actorBtn16.setOnClickListener(mButtonClickListener);
        actorBtn17 = findViewById(R.id.actorBtn17);
        actorBtn17.setOnClickListener(mButtonClickListener);
        actorBtn18 = findViewById(R.id.actorBtn18);
        actorBtn18.setOnClickListener(mButtonClickListener);
        actorBtn19 = findViewById(R.id.actorBtn19);
        actorBtn19.setOnClickListener(mButtonClickListener);
        actorBtn20 = findViewById(R.id.actorBtn20);
        actorBtn20.setOnClickListener(mButtonClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(ActorActivity.this);
    }


    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.actorBtnExit:
                    finish();
                    break;
                case R.id.actorResetBtn:
                    MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.action, (byte)0xff});
                    break;
                case R.id.actorBtn1:
                    sendAction(action[0]);
                    break;
                case R.id.actorBtn2:
                    sendAction(action[1]);
                    break;
                case R.id.actorBtn3:
                    sendAction(action[2]);
                    break;
                case R.id.actorBtn4:
                    sendAction(action[3]);
                    break;
                case R.id.actorBtn5:
                    sendAction(action[4]);
                    break;
                case R.id.actorBtn6:
                    sendAction(action[5]);
                    break;
                case R.id.actorBtn7:
                    sendAction(action[6]);
                    break;
                case R.id.actorBtn8:
                    sendAction(action[7]);
                    break;
                case R.id.actorBtn9:
                    sendAction(action[8]);
                    break;
                case R.id.actorBtn10:
                    sendAction(action[9]);
                    break;
                case R.id.actorBtn11:
                    sendAction(action[10]);
                    break;
                case R.id.actorBtn12:
                    sendAction(action[11]);
                    break;
                case R.id.actorBtn13:
                    sendAction(action[12]);
                    break;
                case R.id.actorBtn14:
                    sendAction(action[13]);
                    break;
                case R.id.actorBtn15:
                    sendAction(action[14]);
                    break;
                case R.id.actorBtn16:
                    sendAction(action[15]);
                    break;
                case R.id.actorBtn17:
                    sendAction(action[16]);
                    break;
                case R.id.actorBtn18:
                    sendAction(action[17]);
                    break;
                case R.id.actorBtn19:
                    sendAction(action[18]);
                    break;
                case R.id.actorBtn20:
                    sendAction(action[19]);
                    break;
            }
        }
        private void sendAction(byte action){
            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.action, (byte) action});
        }
    }
}