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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mControl.gyrControl;
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mMothed.mToast;
import com.luwu.xgo_robot.mView.ThreeDimensionView;
import com.luwu.xgo_robot.mView.VerticalSeekBar;

import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;
import static com.luwu.xgo_robot.mActivity.ControlActivity.progress;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_ADDR;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_VALUE;
public class PostureFragment extends Fragment {
    private ThreeDimensionView postureView;
    private ImageButton postureLockBtn;
    private ImageView postureTxt;
    private gyrControl myGRY;
    private boolean gryFlagThread = false;//线程启动标志量
    private float pitch, roll, yaw;
    private VerticalSeekBar seekBar;
    private ImageView postureTxtBattery, postureTxtSpeed;
    private static boolean flagLoop = false;//循环查询电量
    private getBatteryThread batteryThread;
    private Handler mHandler;
    private long saveTime1 = 0,saveTime2=0;
    private long nowTime = 0;

    public PostureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postureView = view.findViewById(R.id.postureView);
        postureLockBtn = view.findViewById(R.id.postureLockBtn);

        postureTxt = view.findViewById(R.id.postureTxt);
        postureLockBtn.setImageResource(R.drawable.posture_lock);
        postureTxt.setVisibility(View.VISIBLE);
        postureLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gryFlagThread = !gryFlagThread;
                if (gryFlagThread) {//当flagThread变为true的时候，启动线程内含循环，变为false的时候，已存在线程退出循环，run()方法运行完线程被释放
                    //为了防止误差累计 每次按按钮应当重新初始化
                    myGRY = new gyrControl(PostureFragment.this.getActivity());
                    Thread gryThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (gryFlagThread) {
                                pitch = -myGRY.pitch * 90 / 30.0f;
                                roll = myGRY.roll * 90 / 30.0f;
                                yaw = myGRY.yaw * 60 / 30.0f;
                                if (pitch < -90.0f) {
                                    pitch = -90.0f;
                                } else if (pitch > 90.0f) {
                                    pitch = 90.0f;
                                }
                                if (roll < -90.0f) {
                                    roll = -90.0f;
                                } else if (roll > 90.0f) {
                                    roll = 90.0f;
                                }
                                if (yaw < -60.0f) {
                                    yaw = -60.0f;
                                } else if (yaw > 60.0f) {
                                    yaw = 60.0f;
                                }
                                postureView.setThreeDimension((int) pitch, (int) roll, (int) yaw);
                                nowTime = System.currentTimeMillis();
                                if ((nowTime - saveTime1) > 200) {
                                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyRoll,  toOrderRange((int) pitch, -90, 90),toOrderRange((int) roll, -90, 90), toOrderRange((int) (-yaw), -60, 60)});
                                    saveTime1 = nowTime;
                                }
                                //线程延时
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    gryThread.start();
                    postureLockBtn.setImageResource(R.drawable.posture_unlock);
                    postureTxt.setVisibility(View.GONE);
                    mToast.show(PostureFragment.this.getActivity(), "姿态控制已开启");
                } else {
                    postureView.setThreeDimension(0, 0, 0);
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyRoll, (byte) 0x80, (byte) 0x80, (byte) 0x80});
                    postureLockBtn.setImageResource(R.drawable.posture_lock);
                    postureTxt.setVisibility(View.VISIBLE);
                    mToast.show(PostureFragment.this.getActivity(), "姿态控制已停止");
                }
            }
        });
        postureTxtBattery = view.findViewById(R.id.postureTxtBattery);
        postureTxtSpeed = view.findViewById(R.id.postureTxtSpeed);
        seekBar = view.findViewById(R.id.postureSeekBar);
        seekBar.setProgress(progress);
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
                if ((nowTime - saveTime2) > 200) {
                    MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
                    saveTime2 = nowTime;
                }
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    changeBatteryView(XGORAM_VALUE.battery * 100 / 256);
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
        gryFlagThread = false;
        MainActivity.addMessage(new byte[]{XGORAM_ADDR.bodyRoll, (byte) 0x80, (byte) 0x80, (byte) 0x80});
        flagLoop = false;
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

    private void changeBatteryView(int battery) {//范围 0-100
        if (battery <= 0) {
            postureTxtBattery.setImageResource(R.drawable.buttery0);
        } else if (battery <= 7) {
            postureTxtBattery.setImageResource(R.drawable.buttery1);
        } else if (battery <= 14) {
            postureTxtBattery.setImageResource(R.drawable.buttery2);
        } else if (battery <= 21) {
            postureTxtBattery.setImageResource(R.drawable.buttery3);
        } else if (battery <= 28) {
            postureTxtBattery.setImageResource(R.drawable.buttery4);
        } else if (battery <= 35) {
            postureTxtBattery.setImageResource(R.drawable.buttery5);
        } else if (battery <= 42) {
            postureTxtBattery.setImageResource(R.drawable.buttery6);
        } else if (battery <= 49) {
            postureTxtBattery.setImageResource(R.drawable.buttery7);
        } else if (battery <= 56) {
            postureTxtBattery.setImageResource(R.drawable.buttery8);
        } else if (battery <= 63) {
            postureTxtBattery.setImageResource(R.drawable.buttery9);
        } else if (battery <= 70) {
            postureTxtBattery.setImageResource(R.drawable.buttery10);
        } else if (battery <= 77) {
            postureTxtBattery.setImageResource(R.drawable.buttery11);
        } else if (battery <= 84) {
            postureTxtBattery.setImageResource(R.drawable.buttery12);
        } else if (battery <= 91) {
            postureTxtBattery.setImageResource(R.drawable.buttery13);
        } else {
            postureTxtBattery.setImageResource(R.drawable.buttery14);
        }
    }
}
