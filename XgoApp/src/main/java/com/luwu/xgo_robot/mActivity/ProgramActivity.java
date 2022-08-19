package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mFragment.SingleFragment;
import com.luwu.xgo_robot.mFragment.WholeFragment;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

//todo:用户建的文件如果卸载app或者更新app则文件会丢失 导出文件或创建账号
public class ProgramActivity extends AppCompatActivity {

    private Button programBtnWhole, programBtnSingle;
    private ImageButton programBtnHelp,programBtnExit;
    private mViewListener mListener;
    private WholeFragment wholeFragment;
    private SingleFragment singleFragment;
    private final int WHOLEFRAGMENT = 0, SINGLEFRAGMENT = 1;
    private int nowFragment = WHOLEFRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//隐藏Google自带的选择框

        mListener = new mViewListener();
        programBtnWhole = findViewById(R.id.programBtnWhole);
        programBtnSingle = findViewById(R.id.programBtnSingle);
        programBtnWhole.setOnClickListener(mListener);
        programBtnSingle.setOnClickListener(mListener);
        programBtnHelp = findViewById(R.id.programBtnHelp);
        programBtnExit = findViewById(R.id.programBtnExit);
        programBtnHelp.setOnClickListener(mListener);
        programBtnExit.setOnClickListener(mListener);
//        programBtnDevelop.setVisibility(View.GONE);//发布时设置不可见
//默认添加动作Fragment
        nowFragment = WHOLEFRAGMENT;
        if (wholeFragment == null) {
            wholeFragment = new WholeFragment();
        }
        getSupportFragmentManager().beginTransaction().add(R.id.programFrame, wholeFragment).commitAllowingStateLoss();
        programBtnWhole.setBackgroundResource(R.drawable.control_fragment_check);
        programBtnWhole.setTextColor(getResources().getColor(R.color.colorWhite));
        programBtnSingle.setBackgroundResource(R.color.transparent);
        programBtnSingle.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));

        //在这里把assets中的系统定义动作组和动作拷贝到file文件里



    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(ProgramActivity.this);
    }

    private class mViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.programBtnWhole:
                    if (nowFragment == SINGLEFRAGMENT) {
                        nowFragment = WHOLEFRAGMENT;
                        if (wholeFragment == null) {
                            wholeFragment = new WholeFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.programFrame, wholeFragment).commitAllowingStateLoss();
                        programBtnWhole.setBackgroundResource(R.drawable.control_fragment_check);
                        programBtnWhole.setTextColor(getResources().getColor(R.color.colorWhite));
                        programBtnSingle.setBackgroundResource(R.color.transparent);
                        programBtnSingle.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
                case R.id.programBtnSingle:
                    if (nowFragment == WHOLEFRAGMENT) {
                        nowFragment = SINGLEFRAGMENT;
                        if (singleFragment == null) {
                            singleFragment = new SingleFragment();
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.programFrame, singleFragment).commitAllowingStateLoss();
                        programBtnSingle.setBackgroundResource(R.drawable.control_fragment_check);
                        programBtnSingle.setTextColor(getResources().getColor(R.color.colorWhite));
                        programBtnWhole.setBackgroundResource(R.color.transparent);
                        programBtnWhole.setTextColor(getResources().getColor(R.color.controlFragmentUnchecked));
                    }
                    break;
                case R.id.programBtnExit:
                    finish();
                    break;
                case R.id.programBtnHelp:
                    break;
            }
        }
    }
}
