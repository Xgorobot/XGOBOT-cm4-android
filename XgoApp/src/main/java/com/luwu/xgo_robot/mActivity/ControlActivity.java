package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mFragment.ButtonFragment;
import com.luwu.xgo_robot.mFragment.RockerFragment;
import com.luwu.xgo_robot.mFragment.RockerLeftFragment;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;
import com.luwu.xgo_robot.mView.VerticalSeekBar;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIDialog;
import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;
import static com.luwu.xgo_robot.mMothed.PublicMethod.isBluetoothConnect;
import static com.luwu.xgo_robot.mMothed.PublicMethod.localeLanguage;
import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;

public class ControlActivity extends AppCompatActivity {
    public static int progress = 60;//身高滑杆控制
    public static final int progressInit = 60;//身高滑杆控制初始位置
    public static int WHOLEDEFAULT = 0,WHOLESELF = 1,SINGLEDEFAULT = 2,SINGLESELF = 3;//用来区分添加到控制界面的编程的常量 已弃用
    private static int IMUChecked = 0; //0陀螺仪未开启，1开启
    private static int SpeedMode = 0;  //0常速，1低速，2高速
    private Button controlBtnNormal, controlBtnSuperior, controlBtnXYZ, controlBtnPRY;
    private ImageButton controlBtnProgram, controlBtnMore, controlBtnExit;
    private ImageButton controlBtnConnect;
    private PopupWindow mPop;
//    private String listChoice;
//    private List<String> popType = new ArrayList<String>();//动作或动作组类型 0默认动作组 1自定义动作组 2默认动作 3自定义动作

    private RockerFragment rockerFragment;
    private ButtonFragment buttonFragment;
    private RockerLeftFragment rockerLeftFragment;
//    private PostureFragment postureFragment;
    private final int NORMALFRAGMENT = 0, SUPERIORFRAGMENT = 1, XYZFRAGMENT = 2, PRYFRAGMENT = 3;
//    private Handler mHandler;
//    private TextView controlFrameCover;
    private int nowFragment = NORMALFRAGMENT;
    public static int flagRockModeBtn = 0;//公开给摇杆的变量 0全向移动 1xyz转动 2xyz平动
//    private ViewPager viewPager;//viewPage可实现fragment滑动效果 但可能对摇杆等控件产生干扰 暂时弃用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        onClickListener mlistener = new onClickListener();

        controlBtnConnect=findViewById(R.id.controlBtnConnect);
        controlBtnExit = findViewById(R.id.controlBtnExit);
        controlBtnExit.setOnClickListener(mlistener);
        controlBtnNormal = findViewById(R.id.controlBtnNormal);
        controlBtnNormal.setOnClickListener(mlistener);
        controlBtnSuperior = findViewById(R.id.controlBtnSuperior);
        controlBtnSuperior.setOnClickListener(mlistener);
        controlBtnXYZ = findViewById(R.id.controlBtnXYZ);
        controlBtnXYZ.setOnClickListener(mlistener);
        controlBtnPRY = findViewById(R.id.controlBtnPRY);
        controlBtnPRY.setOnClickListener(mlistener);
//        controlBtnProgram = findViewById(R.id.controlBtnProgram);
//        controlBtnProgram.setOnClickListener(mlistener);
        controlBtnMore = findViewById(R.id.controlBtnMore);
        controlBtnMore.setOnClickListener(mlistener);
//        controlFrameCover = findViewById(R.id.controlFrameCover);
//        controlFrameCover.setVisibility(View.GONE);

        //默认添加动作Fragment
        nowFragment = NORMALFRAGMENT;
        if (buttonFragment == null) {
            buttonFragment = new ButtonFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.controlFrame, buttonFragment).commitAllowingStateLoss();
        controlBtnNormal.setBackgroundResource(R.drawable.control_fragment_check);
        controlBtnNormal.setTextColor(getResources().getColor(R.color.colorWhite));
        controlBtnSuperior.setBackgroundResource(R.color.transparent);
        controlBtnSuperior.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
        controlBtnXYZ.setBackgroundResource(R.color.transparent);
        controlBtnXYZ.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
        controlBtnPRY.setBackgroundResource(R.color.transparent);
        controlBtnPRY.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
//        ControlViewpagerAdapter adapter = new ControlViewpagerAdapter(getSupportFragmentManager(), 0);
//        onPageChangedListener pageListener = new onPageChangedListener();
//        viewPager = findViewById(R.id.controlViewPager);
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(0);
//        viewPager.addOnPageChangeListener(pageListener);
        //编程和控制界面的交互 弃用
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 0) {
//                    controlFrameCover.setVisibility(View.VISIBLE);
//                } else if (msg.what == 1) {
//                    controlFrameCover.setVisibility(View.GONE);
//                    mToast.show(ControlActivity.this, "运行完毕");
//                } else if (msg.what == 2) {
//                    controlFrameCover.setVisibility(View.GONE);
//                    mToast.show(ControlActivity.this, "未找到文件");
//                }
//            }
//        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBluetoothConnect) {
            controlBtnConnect.setImageResource(R.drawable.bluetooth2);
        } else {
            controlBtnConnect.setImageResource(R.drawable.bluetooth);
        }
        hideBottomUIMenu(ControlActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.controlBtnExit:
                    finish();
                    break;
//                case R.id.controlBtnProgram:
//                    controlProgram();//配置popupWindow
//                    break;
                case R.id.controlBtnMore:
                    controlMore();
                    break;
                case R.id.controlBtnNormal:
//                    viewPager.setCurrentItem(0);
                    if (nowFragment != NORMALFRAGMENT) {
                        nowFragment = NORMALFRAGMENT;
                        if (buttonFragment == null) {
                            buttonFragment = new ButtonFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.controlFrame, buttonFragment).commitAllowingStateLoss();
                        controlBtnNormal.setBackgroundResource(R.drawable.control_fragment_check);
                        controlBtnNormal.setTextColor(getResources().getColor(R.color.colorWhite));
                        controlBtnSuperior.setBackgroundResource(R.color.transparent);
                        controlBtnSuperior.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnXYZ.setBackgroundResource(R.color.transparent);
                        controlBtnXYZ.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnPRY.setBackgroundResource(R.color.transparent);
                        controlBtnPRY.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
                case R.id.controlBtnSuperior:
//                    viewPager.setCurrentItem(1);
                    if (nowFragment != SUPERIORFRAGMENT) {
                        nowFragment = SUPERIORFRAGMENT;
                        flagRockModeBtn = 0;
                        if (rockerFragment == null) {
                            rockerFragment = new RockerFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.controlFrame, rockerFragment).commitAllowingStateLoss();
                        controlBtnSuperior.setBackgroundResource(R.drawable.control_fragment_check);
                        controlBtnSuperior.setTextColor(getResources().getColor(R.color.colorWhite));
                        controlBtnNormal.setBackgroundResource(R.color.transparent);
                        controlBtnNormal.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnXYZ.setBackgroundResource(R.color.transparent);
                        controlBtnXYZ.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnPRY.setBackgroundResource(R.color.transparent);
                        controlBtnPRY.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
                case R.id.controlBtnXYZ:
//                    viewPager.setCurrentItem(2);
                    if (nowFragment != XYZFRAGMENT) {
                        nowFragment = XYZFRAGMENT;
                        flagRockModeBtn = 2;
                        if (rockerLeftFragment == null) {
                            rockerLeftFragment = new RockerLeftFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.controlFrame, rockerLeftFragment).commitAllowingStateLoss();
                        controlBtnXYZ.setBackgroundResource(R.drawable.control_fragment_check);
                        controlBtnXYZ.setTextColor(getResources().getColor(R.color.colorWhite));
                        controlBtnSuperior.setBackgroundResource(R.color.transparent);
                        controlBtnSuperior.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnNormal.setBackgroundResource(R.color.transparent);
                        controlBtnNormal.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnPRY.setBackgroundResource(R.color.transparent);
                        controlBtnPRY.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
                case R.id.controlBtnPRY:
//                    viewPager.setCurrentItem(2);
                    if (nowFragment != PRYFRAGMENT) {
                        nowFragment = PRYFRAGMENT;
                        flagRockModeBtn = 1;
                        if (rockerFragment == null) {
                            rockerFragment = new RockerFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.controlFrame, rockerFragment).commitAllowingStateLoss();
                        controlBtnPRY.setBackgroundResource(R.drawable.control_fragment_check);
                        controlBtnPRY.setTextColor(getResources().getColor(R.color.colorWhite));
                        controlBtnSuperior.setBackgroundResource(R.color.transparent);
                        controlBtnSuperior.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnXYZ.setBackgroundResource(R.color.transparent);
                        controlBtnXYZ.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                        controlBtnNormal.setBackgroundResource(R.color.transparent);
                        controlBtnNormal.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
            }
        }
    }

//    private class onPageChangedListener implements ViewPager.OnPageChangeListener {
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {//state 0什么都没做 1正在滑动 2滑动完毕
//            if (state == 2) {
//                switch (viewPager.getCurrentItem()) {
//                    case 0:
//                        mToast.show(ControlActivity.this, "摇杆");
//                        break;
//                    case 1:
//                        mToast.show(ControlActivity.this, "按钮");
//                        break;
//                    case 2:
//                        mToast.show(ControlActivity.this, "姿态");
//                        break;
//                }
//            }
//        }
//    }

    //保存在sharedPreferences中的键值对为：<string name="pro0">0动作组:第一个系统动作</string>
//    private void controlProgram() {
//        final View popView = getLayoutInflater().inflate(R.layout.layout_popupwindowlist, null);
////                    TextView txt = view.findViewById(R.id.popTxt);
////                    txt.setText("我的程序");
//        ListView list = popView.findViewById(R.id.popListView);
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ControlActivity.this, R.layout.list_bt_device_name);
//        list.setAdapter(arrayAdapter);
//        final SharedPreferences info = getSharedPreferences(getString(R.string.controlProgramSp), MODE_PRIVATE);
//        arrayAdapter.clear();
//        popType.clear();
//        for (int i = 0; i < 5; i++) {
//            String name = info.getString(getString(R.string.controlProgramKey) + i, "none");
//            String temp = name.substring(0, 1);
//            if (!("none".equals(name))) {
//                if (name.substring(0, 1).equals(String.valueOf(WHOLEDEFAULT))) {//默认动作组
//                    popType.add(String.valueOf(WHOLEDEFAULT));
//                } else if (name.substring(0, 1).equals(String.valueOf(WHOLESELF))) {//自定义动作组
//                    popType.add(String.valueOf(WHOLESELF));
//                } else if (name.substring(0, 1).equals(String.valueOf(SINGLEDEFAULT))) {//默认动作
//                    popType.add(String.valueOf(SINGLEDEFAULT));
//                } else if (name.substring(0, 1).equals(String.valueOf(SINGLESELF))) {//自定义动作
//                    popType.add(String.valueOf(SINGLESELF));
//                } else {//该文件不被识别
//                    SharedPreferences.Editor edit = info.edit();
//                    edit.putString(getString(R.string.controlProgramKey) + i, "none");//重写为none
//                    edit.commit();
//                    continue;
//                }
//                name = name.substring(1);
//                arrayAdapter.add(name);
//            }
//        }
//        if (arrayAdapter.getCount() == 0) {
//            arrayAdapter.add("这里空空如也");
//        } else {
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mToast.show(ControlActivity.this, "执行" + ((TextView) view).getText());
//                    if (popType.get(position).equals(String.valueOf(WHOLEDEFAULT))) {//默认动作组
//
//                    } else if (popType.get(position).equals(String.valueOf(WHOLESELF))) {//自定义动作组
//                        listChoice = getString(R.string.wholexmlHead) + ((TextView) view).getText().toString().substring(4) + getString(R.string.wholexmlTail);//动作组:
//                        readXml();
//                    } else if (popType.get(position).equals(String.valueOf(SINGLEDEFAULT))) {//默认动作
//
//                    } else if (popType.get(position).equals(String.valueOf(SINGLESELF))) {//自定义动作
//                        listChoice = getString(R.string.singlexmlHead) + ((TextView) view).getText().toString().substring(3) + getString(R.string.singlexmlTail);//动作:
//                        readXml();
//                    } else {//该文件不被识别
//
//                        return;
//                    }
//                    mPop.dismiss();
//                }
//            });
//            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
//                    final int pos = position;
//                    final String passName = ((TextView) view).getText().toString();
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ControlActivity.this);
//                    alertDialog.setTitle("编辑" + passName + "？").setMessage("将去往编程界面编辑")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                    if (popType.get(pos).equals("0")) {//默认动作组
//                                        mToast.show(ControlActivity.this, "该程序不可编辑");
//                                    } else if (popType.get(pos).equals("1")) {//自定义动作组
//                                        Intent intent = new Intent(ControlActivity.this, WholeBlocklyActivity.class);
//                                        Bundle bundle = new Bundle();
//                                        if (passName.substring(0, 4).equals("动作组:")) {
//                                            bundle.putString(getString(R.string.fragment2ActivityKey), passName.substring(4));
//                                            intent.putExtras(bundle);
//                                            startActivityForResult(intent, 0);
//                                        }
//                                    } else if (popType.get(pos).equals("2")) {//默认动作
//                                        mToast.show(ControlActivity.this, "该程序不可编辑");
//                                    } else if (popType.get(pos).equals("3")) {//自定义动作
//                                        Intent intent = new Intent(ControlActivity.this, SingleBlocklyActivity.class);
//                                        Bundle bundle = new Bundle();
//                                        if (passName.substring(0, 3).equals("动作:")) {
//                                            bundle.putString(getString(R.string.fragment2ActivityKey), passName.substring(3));
//                                            intent.putExtras(bundle);
//                                            startActivityForResult(intent, 0);
//                                        }
//                                    } else {//该文件不被识别
//
//                                    }
//                                }
//                            }).setNegativeButton("直接删除", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            String fullname = ((TextView) view).getText().toString();
//                            arrayAdapter.remove(fullname);
//                            fullname = popType.get(pos) + fullname;
//                            for (int i = 0; i < 5; i++) {
//                                String name = info.getString("pro" + i, "none");
//                                if (fullname.equals(name)) {
//                                    SharedPreferences.Editor edit = info.edit();
//                                    edit.putString("pro" + i, "none");//重写为none
//                                    edit.commit();
//                                }
//                            }
////                                        arrayAdapter.notifyDataSetChanged();
//                        }
//                    }).setCancelable(true).show();
//                    return true;
//                }
//            });
//        }
//        mPop = new PopupWindow(popView, controlBtnProgram.getWidth() * 5, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mPop.setOutsideTouchable(true);
//        mPop.setFocusable(false);
//        mPop.showAsDropDown(controlBtnProgram);
//    }

    private void controlMore() {
        final View popView = getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
        Switch moveSwitch = popView.findViewById(R.id.popMoveSwitch);//动作自启动开关
        moveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//开启动作自启动
                    MainActivity.addMessage(new byte[]{0x03, 0x01});
                } else {
                    MainActivity.addMessage(new byte[]{0x03, 0x00});
                }
            }
        });

        Switch IMUSwitch = popView.findViewById(R.id.IMUSwitch);//动作自启动开关
        switch(IMUChecked){
            case 0:
                IMUSwitch.setChecked(false);
                break;
            case 1:
                IMUSwitch.setChecked(true);
                break;
        }
        IMUSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    IMUChecked = 1;
                    MainActivity.addMessage(new byte[]{0x61, 0x01});
                    switch (localeLanguage) {
                        case "zh":
                            mToast.show(ControlActivity.this, "自稳定模式已开启");
                            break;
                        default:
                            mToast.show(ControlActivity.this, "Self stable mode on");
                    }
                } else {
                    IMUChecked = 0;
                    MainActivity.addMessage(new byte[]{0x61, 0x00});
                    switch (localeLanguage) {
                        case "zh":
                            mToast.show(ControlActivity.this, "陀螺仪已关闭");
                            break;
                        default:
                            mToast.show(ControlActivity.this, "Gyroscope off");
                    }
                }
            }
        });
        //陀螺仪开启
//        Button IMUOnBtn = popView.findViewById(R.id.popIMUOnBtn);//陀螺仪控制
//        IMUOnBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.addMessage(new byte[]{0x61, 0x01});
//                switch (localeLanguage) {
//                    case "zh":
//                        mToast.show(ControlActivity.this, "自稳定模式已开启");
//                        break;
//                    default:
//                        mToast.show(ControlActivity.this, "Self stable mode on");
//                }
//            }
//        });
//        //陀螺仪关闭
//        Button IMUOffBtn = popView.findViewById(R.id.popIMUOffBtn);//陀螺仪控制
//        IMUOffBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.addMessage(new byte[]{0x61, 0x00});
//                switch (localeLanguage) {
//                    case "zh":
//                        mToast.show(ControlActivity.this, "陀螺仪已关闭");
//                        break;
//                    default:
//                        mToast.show(ControlActivity.this, "Gyroscope off");
//                }
//            }
//        });
        final Button ModeLowBtn = popView.findViewById(R.id.popSpeedLowBtn);//陀螺仪控制
        final Button ModeNormalBtn = popView.findViewById(R.id.popSpeedNormalBtn);//陀螺仪控制
        final Button ModeHighBtn = popView.findViewById(R.id.popSpeedHighBtn);//陀螺仪控制
        switch(SpeedMode){
            case 0:
                ModeNormalBtn.setActivated(true);
                break;
            case 1:
                ModeLowBtn.setActivated(true);
                break;
            case 2:
                ModeHighBtn.setActivated(true);
                break;
        }
        //低速模式
        ModeLowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.addMessage(new byte[]{0x3D, 0x01});
                SpeedMode = 1;
                ModeLowBtn.setActivated(true);
                ModeNormalBtn.setActivated(false);
                ModeHighBtn.setActivated(false);
                progress = 0;
                MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
                switch (nowFragment){
                    case NORMALFRAGMENT:
                        buttonFragment.updateProgress();
                        break;
                    case SUPERIORFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                    case XYZFRAGMENT:
                        rockerLeftFragment.updateProgress();
                        break;
                    case PRYFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                }
                switch (localeLanguage) {
                    case "zh":
                        mToast.show(ControlActivity.this, "低速运动模式已开启");
                        break;
                    default:
                        mToast.show(ControlActivity.this, "Low speed mode on");
                }
            }
        });
        //常速模式
        ModeNormalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.addMessage(new byte[]{0x3D, 0x00});
                SpeedMode = 0;
                ModeLowBtn.setActivated(false);
                ModeNormalBtn.setActivated(true);
                ModeHighBtn.setActivated(false);
                progress = progressInit;
                MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.bodyZ, toOrderRange(progressInit, 0, 100)});
                switch (nowFragment){
                    case NORMALFRAGMENT:
                        buttonFragment.updateProgress();
                        break;
                    case SUPERIORFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                    case XYZFRAGMENT:
                        rockerLeftFragment.updateProgress();
                        break;
                    case PRYFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                }
                switch (localeLanguage) {
                    case "zh":
                        mToast.show(ControlActivity.this, "常速运动模式已开启");
                        break;
                    default:
                        mToast.show(ControlActivity.this, "Normal speed mode on");
                }
            }
        });
        //高速模式
        ModeHighBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.addMessage(new byte[]{0x3D, 0x02});
                SpeedMode = 2;
                ModeLowBtn.setActivated(false);
                ModeNormalBtn.setActivated(false);
                ModeHighBtn.setActivated(true);
                progress = 80;
                MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.bodyZ, toOrderRange(progress, 0, 100)});
                switch (nowFragment){
                    case NORMALFRAGMENT:
                        buttonFragment.updateProgress();
                        break;
                    case SUPERIORFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                    case XYZFRAGMENT:
                        rockerLeftFragment.updateProgress();
                        break;
                    case PRYFRAGMENT:
                        rockerFragment.updateProgress();
                        break;
                }
                switch (localeLanguage) {
                    case "zh":
                        mToast.show(ControlActivity.this, "高速运动模式已开启");
                        break;
                    default:
                        mToast.show(ControlActivity.this, "High speed mode on");
                }
            }
        });
//        Button IMUBtn = popView.findViewById(R.id.popIMUBtn);//陀螺仪控制
//        IMUBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
//                AlertDialog dialog = builder.create();
//                //为了解决onResume不调用的问题
//                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        hideBottomUIMenu(ControlActivity.this);
//                    }
//                });
//                hideBottomUIDialog(dialog);
//                switch(localeLanguage){
//                    case "zh":
//                        builder.setMessage("姿态：请选择陀螺仪控制模式：").setNegativeButton("自稳定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x61, 0x01});
//                                mToast.show(ControlActivity.this, "自稳定模式已开启");
//                            }
//                        }).setNeutralButton("关闭", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x61, 0x00});
//                                mToast.show(ControlActivity.this, "陀螺仪已关闭");
//                            }
//                        })/*.setPositiveButton("柔顺", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //MainActivity.addMessage(new byte[]{0x61, 0x02});
//                                //mToast.show(ControlActivity.this, "柔顺模式已开启");
//                                mToast.show(ControlActivity.this, "柔顺模式暂未开放");
//                            }
//                        })*/.show();
//                        break;
//                    default:
//                        builder.setMessage("Attitude:Select gyro control mode:").setNegativeButton("Self stable", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x61, 0x01});
//                                mToast.show(ControlActivity.this, "Self stable mode on");
//                            }
//                        }).setNeutralButton("Close", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x61, 0x00});
//                                mToast.show(ControlActivity.this, "Gyroscope off");
//                            }
//                        })/*.setPositiveButton("柔顺", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //MainActivity.addMessage(new byte[]{0x61, 0x02});
//                                //mToast.show(ControlActivity.this, "柔顺模式已开启");
//                                mToast.show(ControlActivity.this, "Compliance mode is not open yet");
//                            }
//                        })*/.show();
//
//                }
//                mPop.dismiss();
//            }
//        });
//        Button speedModeBtn = popView.findViewById(R.id.popSpeedModeBtn);//运动模式控制
//        speedModeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
//                switch(localeLanguage){
//                    case "zh":
//                        builder.setMessage("请选择模式：").setNegativeButton("高速模式", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x02});
//                                mToast.show(ControlActivity.this, "高速运动模式已开启");
//                            }
//                        }).setNeutralButton("普通模式", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x00});
//                                mToast.show(ControlActivity.this, "常速运动模式已开启");
//                            }
//                        }).setPositiveButton("低速模式", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x01});
//                                mToast.show(ControlActivity.this, "低速运动模式已开启");
//                            }
//                        }).show();
//                        break;
//                    default:
//                        builder.setMessage("Select mode:").setNegativeButton("High speed", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x02});
//                                mToast.show(ControlActivity.this, "High speed mode on");
//                            }
//                        }).setNeutralButton("Normal speed", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x00});
//                                mToast.show(ControlActivity.this, "Normal speed mode on");
//                            }
//                        }).setPositiveButton("Low speed", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                MainActivity.addMessage(new byte[]{0x3D, 0x01});
//                                mToast.show(ControlActivity.this, "Low speed mode on");
//                            }
//                        }).show();
//                }
//
//                mPop.dismiss();
//            }
//        });
        mPop = new PopupWindow(popView, controlBtnMore.getWidth() * 12, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPop.setOutsideTouchable(true);
        mPop.setFocusable(false);
        mPop.showAsDropDown(controlBtnMore);
    }

//    ReadXmlThread readXmlThread;
//
//    public void readXml() {
//        readXmlThread = new ReadXmlThread();
//        readXmlThread.start();
//    }
//
//    XMLResolver xmlResolver;
//
//    class ReadXmlThread extends Thread {
//        @Override
//        public void run() {
//            xmlResolver = new XMLResolver(ControlActivity.this);
//            File xmlFile = new File(getFilesDir(), listChoice);
//            if (xmlFile.exists()) {
////                xmlResolver.readXMLString(xmlFile);//打印xml文件
//                try {
//                    mHandler.sendEmptyMessage(0);
//                    xmlResolver.resolveXML(xmlFile);//解析xml文件
//                    mHandler.sendEmptyMessage(1);
//                } catch (Exception e) {
//                    mHandler.sendEmptyMessage(2);
//                    Log.d("Tag", "解析错误" + e.getMessage());
//                    e.printStackTrace();
//                }
//            } else {
//                mHandler.sendEmptyMessage(2);
//            }
//
//        }
//    }

}
