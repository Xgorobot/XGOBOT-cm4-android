package com.luwu.xgo_robot.mFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mView.ButtonView;
import com.luwu.xgo_robot.mView.VerticalSeekBar;

import static com.luwu.xgo_robot.mActivity.ControlActivity.progressInit;
import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;
import static com.luwu.xgo_robot.mActivity.ControlActivity.progress;
import static com.luwu.xgo_robot.mView.ButtonView.DOWNPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.LEFTPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.RIGHTPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.UPPRESS;
import static java.lang.Math.sqrt;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_ADDR;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_VALUE;

public class ButtonFragment extends Fragment {


    private ButtonView buttonBtnLeft, buttonBtnRight;
    private VerticalSeekBar seekBar;
    private ImageView buttonTxtBattery, buttonTxtSpeed;
    private Button btnReset;
    private TextView textHeight;
    private static boolean flagLoop = false;//循环查询电量
    private getBatteryThread batteryThread;
    private Handler mHandler;
    private long saveTime = 0;
    private long nowTime = 0;
    private int speedLeft, speedRight;


    public ButtonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonBtnLeft = view.findViewById(R.id.buttonButtonLeft);
        buttonBtnRight = view.findViewById(R.id.buttonButtonRight);

        buttonTxtBattery = view.findViewById(R.id.buttonTxtBattery);
        buttonTxtSpeed = view.findViewById(R.id.buttonTxtSpeed);

        seekBar = view.findViewById(R.id.heightSeekBar);
        seekBar.setProgress(progress);

        btnReset = view.findViewById(R.id.buttonBtnReset);
        textHeight = view.findViewById(R.id.textHeight);
        textHeight.setText(String.valueOf(progress));

        mViewListener();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    changeBatteryView(XGORAM_VALUE.battery);
                } else if (msg.what == 1) {
                    speedLeft = msg.arg1;
                    changeSpeedView((int) sqrt(speedLeft * speedLeft + speedRight * speedRight));
                } else if (msg.what == 2) {
                    speedRight = msg.arg1;
                    changeSpeedView((int) sqrt(speedLeft * speedLeft + speedRight * speedRight));
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        flagLoop = true;
        batteryThread = new getBatteryThread();
        batteryThread.start();//开始查询电池电量
    }

    @Override
    public void onPause() {
        super.onPause();
        flagLoop = false;
    }

    //自定义控件设置监听事件
    private void mViewListener() {
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.action, (byte)0xff});
                seekBar.updateProgress(progressInit);
                textHeight.setText(String.valueOf(progressInit));
            }
        });
        seekBar.setListener(new VerticalSeekBar.ISeekBarListener() {
            @Override
            public void actionDown() {
            }

            @Override
            public void actionUp() {
            }

            @Override
            public void actionMove() {
                nowTime = System.currentTimeMillis();
                progress = seekBar.getProgress();
                textHeight.setText(String.valueOf(progress));
                if ((nowTime - saveTime) > 200) {
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
                    saveTime = nowTime;
                }
            }
        });
        buttonBtnLeft.setButtonViewListener(new ButtonView.IButtonViewListener() {
            @Override
            public void actionDown(int num) {
                switch (num) {
                    case UPPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, (byte) 0xDA});//小于最大速度
                        break;
                    case DOWNPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, (byte) 0x25});
                        break;
                    case LEFTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVy, (byte) 0xDA});
                        break;
                    case RIGHTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVy, (byte) 0x25});
                        break;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = 60;
                mHandler.sendMessage(msg);
            }

            @Override
            public void actionUp(int num) {
                switch (num) {
                    case UPPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, (byte) 0x80});//停下
                        break;
                    case DOWNPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, (byte) 0x80});
                        break;
                    case LEFTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVy, (byte) 0x80});
                        break;
                    case RIGHTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVy, (byte) 0x80});
                        break;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = 0;
                mHandler.sendMessage(msg);
            }
        });
        buttonBtnRight.setButtonViewListener(new ButtonView.IButtonViewListener() {
            @Override
            public void actionDown(int num) {
                switch (num) {
                    case LEFTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, (byte) 0xDA});
                        break;
                    case RIGHTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, (byte) 0x25});
                        break;
                }
                Message msg = new Message();
                msg.what = 2;
                msg.arg1 = 60;
                mHandler.sendMessage(msg);
            }

            @Override
            public void actionUp(int num) {
                switch (num) {
                    case LEFTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, (byte) 0x80});
                        break;
                    case RIGHTPRESS:
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, (byte) 0x80});
                        break;
                }
                Message msg = new Message();
                msg.what = 2;
                msg.arg1 = 0;
                mHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 手动更新（非seekbar）progress
     */
    public void updateProgress(){
        seekBar.updateProgress(progress);
        textHeight.setText(String.valueOf(progress));
    }

    private class getBatteryThread extends Thread {
        @Override
        public void run() {
            while (flagLoop) {
                //查询电池电量并更新
                MainActivity.addMessageRead(new byte[]{XGORAM_ADDR.battery, 0x01});
//                MainActivity.addMessageRead(new byte[]{XGORAM_ADDR.versions, 0x01});
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessageDelayed(message, 200);//200ms以后拿结果
                try {
                    sleep(5000);//5s更新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeSpeedView(int speed) {//范围 0-100
        if (speed <= 0) {
            buttonTxtSpeed.setImageResource(R.drawable.speed0);
        } else if (speed <= 7) {
            buttonTxtSpeed.setImageResource(R.drawable.speed1);
        } else if (speed <= 14) {
            buttonTxtSpeed.setImageResource(R.drawable.speed2);
        } else if (speed <= 21) {
            buttonTxtSpeed.setImageResource(R.drawable.speed3);
        } else if (speed <= 28) {
            buttonTxtSpeed.setImageResource(R.drawable.speed4);
        } else if (speed <= 35) {
            buttonTxtSpeed.setImageResource(R.drawable.speed5);
        } else if (speed <= 42) {
            buttonTxtSpeed.setImageResource(R.drawable.speed6);
        } else if (speed <= 49) {
            buttonTxtSpeed.setImageResource(R.drawable.speed7);
        } else if (speed <= 56) {
            buttonTxtSpeed.setImageResource(R.drawable.speed8);
        } else if (speed <= 63) {
            buttonTxtSpeed.setImageResource(R.drawable.speed9);
        } else if (speed <= 70) {
            buttonTxtSpeed.setImageResource(R.drawable.speed10);
        } else if (speed <= 77) {
            buttonTxtSpeed.setImageResource(R.drawable.speed11);
        } else if (speed <= 84) {
            buttonTxtSpeed.setImageResource(R.drawable.speed12);
        } else if (speed <= 91) {
            buttonTxtSpeed.setImageResource(R.drawable.speed13);
        } else {
            buttonTxtSpeed.setImageResource(R.drawable.speed14);
        }
    }

    private void changeBatteryView(int battery) {//范围 0-100
        if (battery <= 0) {
            buttonTxtBattery.setImageResource(R.drawable.buttery0);
        } else if (battery <= 7) {
            buttonTxtBattery.setImageResource(R.drawable.buttery1);
        } else if (battery <= 14) {
            buttonTxtBattery.setImageResource(R.drawable.buttery2);
        } else if (battery <= 21) {
            buttonTxtBattery.setImageResource(R.drawable.buttery3);
        } else if (battery <= 28) {
            buttonTxtBattery.setImageResource(R.drawable.buttery4);
        } else if (battery <= 35) {
            buttonTxtBattery.setImageResource(R.drawable.buttery5);
        } else if (battery <= 42) {
            buttonTxtBattery.setImageResource(R.drawable.buttery6);
        } else if (battery <= 49) {
            buttonTxtBattery.setImageResource(R.drawable.buttery7);
        } else if (battery <= 56) {
            buttonTxtBattery.setImageResource(R.drawable.buttery8);
        } else if (battery <= 63) {
            buttonTxtBattery.setImageResource(R.drawable.buttery9);
        } else if (battery <= 70) {
            buttonTxtBattery.setImageResource(R.drawable.buttery10);
        } else if (battery <= 77) {
            buttonTxtBattery.setImageResource(R.drawable.buttery11);
        } else if (battery <= 84) {
            buttonTxtBattery.setImageResource(R.drawable.buttery12);
        } else if (battery <= 91) {
            buttonTxtBattery.setImageResource(R.drawable.buttery13);
        } else {
            buttonTxtBattery.setImageResource(R.drawable.buttery14);
        }
    }
}
