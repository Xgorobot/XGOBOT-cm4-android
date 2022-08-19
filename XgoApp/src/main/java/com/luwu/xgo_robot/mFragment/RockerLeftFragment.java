package com.luwu.xgo_robot.mFragment;

import static com.luwu.xgo_robot.mActivity.ControlActivity.progress;
import static com.luwu.xgo_robot.mActivity.ControlActivity.progressInit;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_ADDR;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_VALUE;
import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mActivity.ControlActivity;
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mView.RockerView;
import com.luwu.xgo_robot.mView.VerticalSeekBar;

public class RockerLeftFragment extends Fragment {
    private RockerView rockerViewLeft;
    private VerticalSeekBar seekBar;
    private ImageView rockerTxtBattery, rockerTxtSpeed;
    private Button btnReset;
    private TextView textHeight;
    private static boolean flagLoop = false;//循环查询电量
    private getBatteryThread batteryThread;
    private Handler mHandler;
    private long saveTime1 = 0, saveTime2 = 0, saveTime3 = 0;
    private long nowTime = 0;
    private int speedLeft, speedRight;
    public RockerLeftFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rockerleft, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rockerTxtBattery = view.findViewById(R.id.rockerTxtBattery);
        rockerTxtSpeed = view.findViewById(R.id.rockerTxtSpeed);
        rockerViewLeft = view.findViewById(R.id.controlRockViewLeft);
        seekBar = view.findViewById(R.id.heightSeekBar);
        seekBar.setProgress(progress);
        btnReset = view.findViewById(R.id.rockerLeftBtnReset);
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
    public void onStart() {
        super.onStart();
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
        rockerViewLeft.setRockViewListener(new RockerView.IRockViewListener() {
            @Override
            public void actionDown() {
            }

            @Override
            public void actionUp() {
//                flagRockLoop=false;
                if (ControlActivity.flagRockModeBtn == 0) {//全向移动
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, (byte) 0x80});
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, (byte) 0x80});
                } else if (ControlActivity.flagRockModeBtn == 1) {//xyz转动
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyPitch, (byte) 0x80});
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyRoll, (byte) 0x80});
                } else if (ControlActivity.flagRockModeBtn == 2) {//xyz平动
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyX, (byte) 0x80});
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyY, (byte) 0x80});
                }
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = 0;
                mHandler.sendMessage(msg);
            }

            @Override
            public void actionMove() {
                nowTime = System.currentTimeMillis();
                if ((nowTime - saveTime1) > 300) {//500
                    Point speed = rockerViewLeft.getSpeed();
                    if (ControlActivity.flagRockModeBtn == 0) {//全向移动
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVx, toOrderRange(-speed.y, -100, 100)});
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.speedVyaw, toOrderRange(-speed.x, -100, 100)});
                    } else if (ControlActivity.flagRockModeBtn == 1) {//xyz转动
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyPitch, toOrderRange(-speed.y, -100, 100)});
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyRoll, toOrderRange(speed.x, -100, 100)});
                    } else if (ControlActivity.flagRockModeBtn == 2) {//xyz平动
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyX, toOrderRange(-speed.y, -100, 100)});
                        MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyY, toOrderRange(-speed.x, -100, 100)});
                    }
                    saveTime1 = nowTime;
//                    leftRockPoint = rockerViewLeft.getSpeed();
                }
                Message msg = new Message();
                msg.what = 1;
                int x = rockerViewLeft.getSpeed().x;
                int y = rockerViewLeft.getSpeed().y;
                msg.arg1 = (int) abs(sqrt(x * x + y * y));
                mHandler.sendMessage(msg);
            }
        });

        seekBar.setListener(new VerticalSeekBar.ISeekBarListener() {
            @Override
            public void actionDown() {
            }

            @Override
            public void actionUp() {
                MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
            }

            @Override
            public void actionMove() {
                nowTime = System.currentTimeMillis();
                progress = seekBar.getProgress();
                textHeight.setText(String.valueOf(progress));
                if ((nowTime - saveTime3) > 200) {//200ms刷新
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
                    saveTime3 = nowTime;
                }
            }
        });
    }

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
            rockerTxtSpeed.setImageResource(R.drawable.speed0);
        } else if (speed <= 7) {
            rockerTxtSpeed.setImageResource(R.drawable.speed1);
        } else if (speed <= 14) {
            rockerTxtSpeed.setImageResource(R.drawable.speed2);
        } else if (speed <= 21) {
            rockerTxtSpeed.setImageResource(R.drawable.speed3);
        } else if (speed <= 28) {
            rockerTxtSpeed.setImageResource(R.drawable.speed4);
        } else if (speed <= 35) {
            rockerTxtSpeed.setImageResource(R.drawable.speed5);
        } else if (speed <= 42) {
            rockerTxtSpeed.setImageResource(R.drawable.speed6);
        } else if (speed <= 49) {
            rockerTxtSpeed.setImageResource(R.drawable.speed7);
        } else if (speed <= 56) {
            rockerTxtSpeed.setImageResource(R.drawable.speed8);
        } else if (speed <= 63) {
            rockerTxtSpeed.setImageResource(R.drawable.speed9);
        } else if (speed <= 70) {
            rockerTxtSpeed.setImageResource(R.drawable.speed10);
        } else if (speed <= 77) {
            rockerTxtSpeed.setImageResource(R.drawable.speed11);
        } else if (speed <= 84) {
            rockerTxtSpeed.setImageResource(R.drawable.speed12);
        } else if (speed <= 91) {
            rockerTxtSpeed.setImageResource(R.drawable.speed13);
        } else {
            rockerTxtSpeed.setImageResource(R.drawable.speed14);
        }
    }

    private void changeBatteryView(int battery) {//范围 0-100
        if (battery <= 0) {
            rockerTxtBattery.setImageResource(R.drawable.buttery0);
        } else if (battery <= 7) {
            rockerTxtBattery.setImageResource(R.drawable.buttery1);
        } else if (battery <= 14) {
            rockerTxtBattery.setImageResource(R.drawable.buttery2);
        } else if (battery <= 21) {
            rockerTxtBattery.setImageResource(R.drawable.buttery3);
        } else if (battery <= 28) {
            rockerTxtBattery.setImageResource(R.drawable.buttery4);
        } else if (battery <= 35) {
            rockerTxtBattery.setImageResource(R.drawable.buttery5);
        } else if (battery <= 42) {
            rockerTxtBattery.setImageResource(R.drawable.buttery6);
        } else if (battery <= 49) {
            rockerTxtBattery.setImageResource(R.drawable.buttery7);
        } else if (battery <= 56) {
            rockerTxtBattery.setImageResource(R.drawable.buttery8);
        } else if (battery <= 63) {
            rockerTxtBattery.setImageResource(R.drawable.buttery9);
        } else if (battery <= 70) {
            rockerTxtBattery.setImageResource(R.drawable.buttery10);
        } else if (battery <= 77) {
            rockerTxtBattery.setImageResource(R.drawable.buttery11);
        } else if (battery <= 84) {
            rockerTxtBattery.setImageResource(R.drawable.buttery12);
        } else if (battery <= 91) {
            rockerTxtBattery.setImageResource(R.drawable.buttery13);
        } else {
            rockerTxtBattery.setImageResource(R.drawable.buttery14);
        }
    }
}
