package com.luwu.xgo_robot.mActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.blockly.model.Block;
import com.google.blockly.model.BlockFactory;
import com.google.blockly.model.BlockTemplate;
import com.google.blockly.model.Workspace;
import com.luwu.xgo_robot.mControl.JsonControl;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mControl.ToolboxControl;
import com.luwu.xgo_robot.mControl.XMLResolver;
import com.luwu.xgo_robot.mMothed.mToast;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.model.DefaultBlocks;
import com.google.blockly.utils.BlockLoadingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_VALUE;
import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

//todo:写写说明文档 之后改起来方便 把变量名都宏定义起来
/*
 *single和whole不同的地方就在于此处保存、重命名、删除时不仅要操作xml文件（此时二者相同）
 * 还要在whole中动态增加block块，因此需要更改.json和toolbox.xml
 * 核心代码在SingleBlocklyActivity SingleFragment 以及BlocklyActivityHelper #resetBlockFactory #reloadToolbox中
 * 关于动态添加的block 好像名字不能有数字？？按照变量的命名规则不知可否 之后找bug的时候搞吧 还有中文
 * !!关于文件处理 如果抛出异常 应当立刻删除toolbox里的内容，并从json中重新加载，这样更安全 其实toolbox不应该自己独立 应当从json中加载才对 之后改
 */
public class SingleBlocklyActivity extends AbstractBlocklyActivity {
    private JsonControl jsonControl;//用来动态添加block的两个变量
    private ToolboxControl toolboxControl;

    public static List<String> itemNameList = new ArrayList<>();//文件列表
    private String bundleFileName = new String();
    private int motorPos[] = {128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128};//记录舵机位置

    private static final String SAVE_FILENAME = "single_robot_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "single_robot_workspace_temp.xml";
    private static final String INITWORKSPACE = "assets_single/single_robot_initworkspace.xml";
    private static final String EMPTYHTML = "file:///android_asset/single_empty.html";
    private static final String TOOLBOX_XML_PATH = "assets_single/single_robot_toolbox.xml";
    private static final List<String> GENERATORS_JS_PATHS = Arrays.asList(//待调用的JS函数
            "assets_single/single_generator.js");
    private static final List<String> BLOCK_JSON_PATHS = Arrays.asList(//自定义和默认的模块
            DefaultBlocks.COLOR_BLOCKS_PATH,
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.TEXT_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "assets_single/single_robot_blocks.json"
    );
    private boolean safeBack;//保存后可安全离开，否则弹出提示框
    private boolean isModify = false;
    //    private Button  singleBlockBtnClear;
    private ImageButton singleBlockBtnBack, singleBlockBtnSave, singleBlockBtnRun;
    private Handler mHandler;
    private TextView[] singleBlocklyTxtMotor = new TextView[12];
    private LinearLayout singleBlocklyLinearMotor, singleBlocklyLinearTeach;
    private Boolean flagLoop = false;//查询舵机位置变量
    private getMotorThread motorThread;
    //    private WebView singleBlockWebView;
//    private MoveInterface moveInterface;
    private Boolean teachMode = false;//新增示教模式
    private CheckBox legCheckbox1, legCheckbox2, legCheckbox3, legCheckbox4;//示教模式是否要生成对应腿的模块
    private Button singleBlocklyBtnMotor, singleBlocklyBtnRecord, singleBlocklyBtnTeach;
    private RadioGroup singleBlocklyRG1, singleBlocklyRG2, singleBlocklyRG3, singleBlocklyRG4;
    private RadioButton singleBlocklyRBX1, singleBlocklyRBH1, singleBlocklyRBX2, singleBlocklyRBH2, singleBlocklyRBX3, singleBlocklyRBH3, singleBlocklyRBX4, singleBlocklyRBH4;
    private TextView singleBlocklyBtnRecordLeg1, singleBlocklyBtnRecordLeg2, singleBlocklyBtnRecordLeg3, singleBlocklyBtnRecordLeg4;

    public static List<String> getItemNameList() {//得到自动刷新后的列表
        return itemNameList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View onCreateContentView(int parentId) {
        Bundle bundle = getIntent().getExtras();
        String bundleS = bundle.getString(getString(R.string.fragment2ActivityKey));
        bundleFileName = userToXml(bundleS);

        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//隐藏Google自带的选择框
        mViewListener mListener = new mViewListener();
//        EditText editText = new EditText(this);
//        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);//横屏时禁止输入法全屏
        View viewSingleBlockly = getLayoutInflater().inflate(R.layout.activity_single_blockly, null);
        singleBlockBtnBack = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnBack);
        singleBlockBtnBack.setOnClickListener(mListener);
//        singleBlockBtnClear = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnClear);
//        singleBlockBtnClear.setOnClickListener(mListener);
        singleBlockBtnSave = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnSave);
        singleBlockBtnSave.setOnClickListener(mListener);
        singleBlockBtnRun = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRun);
        singleBlockBtnRun.setOnClickListener(mListener);
        jsonControl = new JsonControl();
        toolboxControl = new ToolboxControl();

        //示教模式
        singleBlocklyBtnMotor = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnMotor);
        singleBlocklyBtnMotor.setOnClickListener(mListener);
        singleBlocklyBtnRecord = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRecord);
        singleBlocklyBtnRecord.setOnClickListener(mListener);
        singleBlocklyBtnTeach = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnTeach);
        singleBlocklyBtnTeach.setOnClickListener(mListener);
        singleBlocklyTxtMotor[0] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor1);
        singleBlocklyTxtMotor[1] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor2);
        singleBlocklyTxtMotor[2] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor3);
        singleBlocklyTxtMotor[3] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor4);
        singleBlocklyTxtMotor[4] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor5);
        singleBlocklyTxtMotor[5] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor6);
        singleBlocklyTxtMotor[6] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor7);
        singleBlocklyTxtMotor[7] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor8);
        singleBlocklyTxtMotor[8] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor9);
        singleBlocklyTxtMotor[9] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor10);
        singleBlocklyTxtMotor[10] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor11);
        singleBlocklyTxtMotor[11] = viewSingleBlockly.findViewById(R.id.singleBlocklyTxtMotor12);
        singleBlocklyLinearMotor = viewSingleBlockly.findViewById(R.id.singleBlocklyLinearMotor);
        singleBlocklyLinearTeach = viewSingleBlockly.findViewById(R.id.singleBlocklyLinearTeach);
        //CheckBox
        legCheckbox1 = viewSingleBlockly.findViewById(R.id.layoutCheckBoxLeg1);
        legCheckbox2 = viewSingleBlockly.findViewById(R.id.layoutCheckBoxLeg2);
        legCheckbox3 = viewSingleBlockly.findViewById(R.id.layoutCheckBoxLeg3);
        legCheckbox4 = viewSingleBlockly.findViewById(R.id.layoutCheckBoxLeg4);
        //RadioButton
        singleBlocklyRBX1 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBX1);
        singleBlocklyRBH1 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBH1);
        singleBlocklyRBX2 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBX2);
        singleBlocklyRBH2 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBH2);
        singleBlocklyRBX3 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBX3);
        singleBlocklyRBH3 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBH3);
        singleBlocklyRBX4 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBX4);
        singleBlocklyRBH4 = viewSingleBlockly.findViewById(R.id.singleBlocklyRBH4);
        //RadioGroup
        singleBlocklyRG1 = viewSingleBlockly.findViewById(R.id.singleBlocklyRG1);
        singleBlocklyRG2 = viewSingleBlockly.findViewById(R.id.singleBlocklyRG2);
        singleBlocklyRG3 = viewSingleBlockly.findViewById(R.id.singleBlocklyRG3);
        singleBlocklyRG4 = viewSingleBlockly.findViewById(R.id.singleBlocklyRG4);
        //单独记录舵机Textview
        singleBlocklyBtnRecordLeg1 = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRecordLeg1);
        singleBlocklyBtnRecordLeg1.setOnClickListener(mListener);
        singleBlocklyBtnRecordLeg2 = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRecordLeg2);
        singleBlocklyBtnRecordLeg2.setOnClickListener(mListener);
        singleBlocklyBtnRecordLeg3 = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRecordLeg3);
        singleBlocklyBtnRecordLeg3.setOnClickListener(mListener);
        singleBlocklyBtnRecordLeg4 = viewSingleBlockly.findViewById(R.id.singleBlocklyBtnRecordLeg4);
        singleBlocklyBtnRecordLeg4.setOnClickListener(mListener);
        setSelected();//radiobutton初始化
        setListener();//radiogroup监听事件

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {//运行结束
                    singleBlockBtnRun.setImageResource(R.drawable.program_start);
                    mToast.show(SingleBlocklyActivity.this, "运行完毕");
                } else if (msg.what == 1) {//舵机查询
                    motorPos[0] = XGORAM_VALUE.motor_11;
                    motorPos[1] = XGORAM_VALUE.motor_12;
                    motorPos[2] = XGORAM_VALUE.motor_13;
                    motorPos[3] = XGORAM_VALUE.motor_21;
                    motorPos[4] = XGORAM_VALUE.motor_22;
                    motorPos[5] = XGORAM_VALUE.motor_23;
                    motorPos[6] = XGORAM_VALUE.motor_31;
                    motorPos[7] = XGORAM_VALUE.motor_32;
                    motorPos[8] = XGORAM_VALUE.motor_33;
                    motorPos[9] = XGORAM_VALUE.motor_41;
                    motorPos[10] = XGORAM_VALUE.motor_42;
                    motorPos[11] = XGORAM_VALUE.motor_43;
                    for (int i = 0; i < 12; i++) {
                        singleBlocklyTxtMotor[i].setText(String.valueOf(motorPos[i]));
                    }
                }
            }
        };
        return viewSingleBlockly;
    }

    @Override
    protected void onResume() {
        super.onResume();
        singleBlocklyBtnMotor.setText("观测角度");
        singleBlocklyLinearMotor.setVisibility(View.GONE);
        hideBottomUIMenu(SingleBlocklyActivity.this);
        safeBack = false;
        isModify = false;
        initArrayList();//得到文件列表
        if (itemNameList.contains(bundleFileName)) {//如果包含才行 需要在文档加载之后 这里需要可能需要改一改 不然会先加载
            //初始页面然后再加载用户界面 有没有需要加载自动保存的界面 实际实验并不卡顿
            mBlocklyActivityHelper.loadWorkspaceFromAppDirSafely(bundleFileName);
            isModify = true;//此时的保存界面应不同
            Log.d("Tag", "onLoadUserWorkspace: ");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flagLoop = false;
        teachMode = false;
        MainActivity.addMessage(new byte[]{0x20, 0x00});//发送停止卸载的指令
        MainActivity.addMessage(new byte[]{0x5D, 0x01});//恢复站立
        singleBlocklyLinearTeach.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (readXmlThread != null) {
            readXmlThread.interrupt();
            if (xmlResolver != null) {
                xmlResolver.setinterupt_flag(true);
            }
            readXmlThread = null;
            MainActivity.addMessage(new byte[]{0x5D, (byte) 0x01});//恢复站立姿势 退出舵机模式
        }
        super.onDestroy();
    }

    private void setSelected() {
        singleBlocklyRBH1.setChecked(true);
        singleBlocklyRBH2.setChecked(true);
        singleBlocklyRBH3.setChecked(true);
        singleBlocklyRBH4.setChecked(true);
    }

    private void setListener() {
        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {//发送卸载指令
                    case R.id.singleBlocklyRBX1:
                        MainActivity.addMessage(new byte[]{0x20, 0x11});
                        break;
                    case R.id.singleBlocklyRBH1:
                        MainActivity.addMessage(new byte[]{0x20, 0x21});
                        break;
                    case R.id.singleBlocklyRBX2:
                        MainActivity.addMessage(new byte[]{0x20, 0x12});
                        break;
                    case R.id.singleBlocklyRBH2:
                        MainActivity.addMessage(new byte[]{0x20, 0x22});
                        break;
                    case R.id.singleBlocklyRBX3:
                        MainActivity.addMessage(new byte[]{0x20, 0x13});
                        break;
                    case R.id.singleBlocklyRBH3:
                        MainActivity.addMessage(new byte[]{0x20, 0x23});
                        break;
                    case R.id.singleBlocklyRBX4:
                        MainActivity.addMessage(new byte[]{0x20, 0x14});
                        break;
                    case R.id.singleBlocklyRBH4:
                        MainActivity.addMessage(new byte[]{0x20, 0x24});
                        break;
                }
            }
        };
        singleBlocklyRG1.setOnCheckedChangeListener(listener);
        singleBlocklyRG2.setOnCheckedChangeListener(listener);
        singleBlocklyRG3.setOnCheckedChangeListener(listener);
        singleBlocklyRG4.setOnCheckedChangeListener(listener);
    }

    private class mViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.singleBlocklyBtnBack:
                    //应该提示用户是否保存
                    if ((!safeBack) && getController().getWorkspace().hasBlocks()) {
                        AlertDialog.Builder builderBack = new AlertDialog.Builder(SingleBlocklyActivity.this);
                        builderBack.setTitle("警告").setMessage("系统可能不会保留您所做的更改").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        builderBack.show();
                    } else {
                        finish();
                    }
                    break;
//                case R.id.singleBlocklyBtnClear:
//                    onClearWorkspace();
//                    break;
                case R.id.singleBlocklyBtnSave:
                    if (!isModify) {//若为新增文档
                        AlertDialog.Builder builderSave = new AlertDialog.Builder(SingleBlocklyActivity.this);
                        View view = LayoutInflater.from(SingleBlocklyActivity.this).inflate(R.layout.layout_dialogsetname, null);
                        final EditText editText = view.findViewById(R.id.dialog_EditTxt_name);
                        builderSave.setTitle("输入文件名：").setView(view).setPositiveButton("保存", null);
                        final AlertDialog dialogSave = builderSave.create();
                        dialogSave.show();
                        dialogSave.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//必须在show方法后调用 点击按钮后不消失
                            @Override
                            public void onClick(View v) {
                                final String userName = editText.getText().toString().trim();
                                final String saveName = userToXml(userName);
                                if (userName.length() != 0) {
                                    if (ifSameName(saveName, (ArrayList<String>) itemNameList)) {//如果重复 提示是否覆盖
//                                        Toast.makeText(WholeBlocklyActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder coverBuilder = new AlertDialog.Builder(SingleBlocklyActivity.this);
                                        coverBuilder.setTitle("提示").setMessage("文件已存在，是否覆盖？").setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                File fileD = new File(SingleBlocklyActivity.this.getFilesDir(), saveName);
                                                if (fileD.exists()) {
                                                    fileD.delete();//删除已有文件
                                                }
                                                safeBack = true;
                                                mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(saveName);
                                                //动态添加block 此处不用
                                                mToast.show(SingleBlocklyActivity.this, "保存成功");
                                                dialog.dismiss();
                                                dialogSave.dismiss();
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    } else {
                                        if (PublicMethod.verifyFilename(userName)) {//如果文件合法
                                            safeBack = true;
                                            mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(saveName);
                                            //动态添加block
                                            File fileB = new File(SingleBlocklyActivity.this.getFilesDir(), "whole_file_blocks_self.json");
                                            if (fileB.exists()) {
                                                jsonControl.addJson(fileB, userName);
                                                File fileT = new File(SingleBlocklyActivity.this.getFilesDir(), "whole_file_toolbox.xml");
                                                if (fileT.exists()) {
                                                    toolboxControl.addBlock(fileT, jsonControl.getTypeByName(fileB, userName));
                                                    Log.d("Tag", "onClick: 成功");
                                                }
                                            }
                                            mToast.show(SingleBlocklyActivity.this, "保存成功");
                                            dialogSave.dismiss();
                                        } else {
                                            mToast.show(SingleBlocklyActivity.this, "只能包含汉字、英文字母和数字，并且数字不能开头");
                                        }
                                    }
                                } else {
                                    mToast.show(SingleBlocklyActivity.this, "文件名不可为空");
                                }
                            }
                        });
                    } else if (isModify) {//若为修改文档
                        safeBack = true;
                        mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(bundleFileName);
                        //动态添加block 此处不需要
                        mToast.show(SingleBlocklyActivity.this, "保存成功");
                    }
                    break;
                case R.id.singleBlocklyBtnRun:
                    if (getController().getWorkspace().hasBlocks()) {
                        if (readXmlThread == null) {
//                        onRunCode();
                            readXml();
                            singleBlockBtnRun.setImageResource(R.drawable.program_stop);
                        } else {
                            readXmlThread.interrupt();
                            if (xmlResolver != null) {
                                xmlResolver.setinterupt_flag(true);
                            }
                            readXmlThread = null;
                            singleBlockBtnRun.setImageResource(R.drawable.program_start);
                            MainActivity.addMessage(new byte[]{0x5D, (byte) 0x01});//恢复站立姿势 退出舵机模式
                        }
                    }

                    break;
                case R.id.singleBlocklyBtnMotor:
                    if (singleBlocklyBtnMotor.getText().equals("观测角度")) {
                        singleBlocklyBtnMotor.setText("隐藏角度");
                        singleBlocklyLinearMotor.setVisibility(View.VISIBLE);
                    } else {
                        singleBlocklyBtnMotor.setText("观测角度");
                        singleBlocklyLinearMotor.setVisibility(View.GONE);
                    }
                    break;
                case R.id.singleBlocklyBtnRecord:
//                    addPositionBlockly((int)(Math.random()*4),(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
                    if (teachMode) {
                        if (legCheckbox1.isChecked()) {
                            addPositionBlockly(1, motorPos[0], motorPos[1], motorPos[2]);
                        }
                        if (legCheckbox2.isChecked()) {
                            addPositionBlockly(2, motorPos[3], motorPos[4], motorPos[5]);
                        }
                        if (legCheckbox3.isChecked()) {
                            addPositionBlockly(3, motorPos[6], motorPos[7], motorPos[8]);
                        }
                        if (legCheckbox4.isChecked()) {
                            addPositionBlockly(4, motorPos[9], motorPos[10], motorPos[11]);
                        }
                    }
                    break;
                case R.id.singleBlocklyBtnRecordLeg1:
                    addPositionBlockly(1, motorPos[0], motorPos[1], motorPos[2]);
                    break;
                case R.id.singleBlocklyBtnRecordLeg2:
                    addPositionBlockly(2, motorPos[3], motorPos[4], motorPos[5]);
                    break;
                case R.id.singleBlocklyBtnRecordLeg3:
                    addPositionBlockly(3, motorPos[6], motorPos[7], motorPos[8]);
                    break;
                case R.id.singleBlocklyBtnRecordLeg4:
                    addPositionBlockly(4, motorPos[9], motorPos[10], motorPos[11]);
                    break;
                case R.id.singleBlocklyBtnTeach://示教模式
                    if (teachMode) {
                        teachMode = false;
                        flagLoop = false;
                        singleBlocklyLinearTeach.setVisibility(View.GONE);
                        MainActivity.addMessage(new byte[]{0x20, 0x00});//发送停止卸载的指令
                        MainActivity.addMessage(new byte[]{0x5D, 0x01});//恢复站立
                    } else {
                        teachMode = true;
                        flagLoop = true;
                        motorThread = new getMotorThread();
                        motorThread.start();
                        setSelected();
                        singleBlocklyLinearTeach.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    private class getMotorThread extends Thread {
        @Override
        public void run() {
            while (flagLoop) {
                //查询各舵机位置并更新
                MainActivity.addMessageRead(new byte[]{0x50, 0x0C});
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessageDelayed(message, 200);//200ms以后拿结果
                try {
                    sleep(200);//200ms更新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ReadXmlThread readXmlThread;

    public void readXml() {
        readXmlThread = new ReadXmlThread();
        readXmlThread.start();
    }

    XMLResolver xmlResolver;

    class ReadXmlThread extends Thread {
        @Override
        public void run() {
            xmlResolver = new XMLResolver(SingleBlocklyActivity.this);
            File xmlFile = new File(getFilesDir(), SAVE_FILENAME);
            if (xmlFile.exists()) {//如果存在先删除
                xmlFile.delete();
                xmlFile = null;
            }
            mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(SAVE_FILENAME);
            xmlFile = new File(getFilesDir(), SAVE_FILENAME);
            if (xmlFile.exists()) {
//                xmlResolver.readXMLString(xmlFile);//打印xml文件
                try {
                    xmlResolver.resolveXML(xmlFile);//解析xml文件
                    mHandler.sendEmptyMessage(0);
                    readXmlThread = null;
                } catch (Exception e) {
                    Log.d("Tag", "解析错误" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    //用于示教模式 自动添加blockly
    private void addPositionBlockly(int which, int top, int middle, int bottom) {
        if ((0 < which && which < 5) && (0 < top && top < 256) && (0 < middle && middle < 256) && (0 < bottom && bottom < 256)) {
            try {
                testAddRootBlock(String.valueOf(which), String.valueOf(top), String.valueOf(middle), String.valueOf(bottom));
            } catch (BlockLoadingException e) {
                Log.d("Tag", "示教模式：自动添加blockly失败");
            }
        }
    }

    private void testAddRootBlock(String which, String top, String middle, String bottom) throws BlockLoadingException {
        float x = 0, y = 0;
        BlocklyController mController = getController();
        BlockFactory mBlockFactory = mController.getBlockFactory();
        Workspace mWorkspace = mController.getWorkspace();
        BlockTemplate mTemplate = new BlockTemplate().ofType("input_position").withId("teaching_mode");
        mTemplate.setmFieldValues(which, top, middle, bottom);//改源代码 自己添加的public方法
        final Block mBlock = mBlockFactory.obtainBlockFrom(mTemplate);
        if (mBlock != null) {
            if (mWorkspace.getRootBlocks() != null && !mWorkspace.getRootBlocks().isEmpty()) {//如果已经有了，就自己设置位置 否则用系统位置
                for (Block block : mWorkspace.getRootBlocks()) {//得到所有根block
                    x = Math.max(block.getPosition().x, x);
                    y = Math.max(block.getPosition().y, y);
                }
                mBlock.setPosition(x + 15, y + 15);
            }
        }
        mController.addRootBlock(mBlock);
    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return TOOLBOX_XML_PATH;
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return BLOCK_JSON_PATHS;
    }

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return GENERATORS_JS_PATHS;
    }

    @NonNull
    protected CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback = new CodeGenerationRequest.CodeGeneratorCallback() {
        @Override
        public void onFinishCodeGeneration(String generatedCode) {//得到的代码自己处理
            Log.d("Tag", "得到的代码" + generatedCode);
        }
    };

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    protected boolean checkAllowRestoreBlocklyState(Bundle savedInstanceState) {
        return false;//这样就会调用onLoadInitialWorkspace加载自己想要的界面
    }

    @Override
    protected void onLoadInitialWorkspace() {
        Log.d("Tag", "onLoadInitialWorkspace: ");
        BlocklyController controller = getController();
        try {
            controller.loadWorkspaceContents(getAssets().open(INITWORKSPACE));
        } catch (IOException | BlockLoadingException e) {
            throw new IllegalStateException("Couldn't load demo workspace from assets: " + "robot.xml", e);
        }
//        super.onLoadInitialWorkspace();
    }

    @Override
    protected void onInitBlankWorkspace() {
        Log.d("Tag", "onInitBlankWorkspace: ");
        BlocklyController controller = getController();
        try {
            controller.loadWorkspaceContents(getAssets().open(INITWORKSPACE));
        } catch (IOException | BlockLoadingException e) {
            throw new IllegalStateException("Couldn't load demo workspace from assets: " + "robot.xml", e);
        }
        super.onInitBlankWorkspace();
    }

    @NonNull
    @Override
    protected String getWorkspaceSavePath() {
        return SAVE_FILENAME;
    }

    @NonNull
    @Override
    protected String getWorkspaceAutosavePath() {
        return AUTOSAVE_FILENAME;
    }

    @Override
    public void onLoadWorkspace() {//加载工作区
        Log.d("Tag", "onLoadWorkspace: ");
        super.onLoadWorkspace();
    }

    @Override
    public void onSaveWorkspace() {//保存工作区
        Log.d("Tag", "onSaveWorkspace: ");
        super.onSaveWorkspace();
    }

    /*
     *下面为文件输入输出操作
     */

    //得到更新文件列表 进入本页面时自动更新
    private void initArrayList() {
        String assestFileName = getFilesDir().getAbsolutePath();
        File xmlFile = new File(assestFileName);
        File result[] = xmlFile.listFiles();
        itemNameList.clear();//清空重新读取
        for (int i = 0; i < result.length; i++) {
            itemNameList.add(i, result[i].getName());
        }
        for (int i = 0; i < itemNameList.size(); i++) {
            if (!(getString(R.string.singlexmlHead).equals(itemNameList.get(i).substring(0, 9)))) {
                itemNameList.remove(i);
                i--;
            }
        }
        if (itemNameList.size() == 0) {
            Log.d("Tag", "initArrayList: " + "itemNameList为空");
        } else {
            for (int i = 0; i < itemNameList.size(); i++) {
                Log.d("Tag", itemNameList.get(i));
            }
        }
    }

    //将用户输入的名字格式化
    private String userToXml(String user) {
        String singlexml = new String();
        singlexml = getString(R.string.singlexmlHead) + user + getString(R.string.singlexmlTail);
        return singlexml;
    }

    //判断文件是否已经存在
    private boolean ifSameName(String newName, ArrayList<String> ListOfItemsName) {
        boolean ifSameName = false;
        for (int i = 0; i < ListOfItemsName.size(); i++) {
            if (newName.trim().equals((ListOfItemsName.get(i)).trim())) {
                ifSameName = true;
                break;
            }
        }
        return ifSameName;
    }

}
