package com.luwu.xgo_robot.mActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.luwu.xgo_robot.mControl.JavascriptUtil;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.R;
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

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

public class WholeBlocklyActivity extends AbstractBlocklyActivity {
    public static List<String> itemNameList = new ArrayList<>();//文件列表
    private String bundleFileName = new String();

    private static final String SAVE_FILENAME = "whole_robot_workspace.xml";
    private static final String AUTOSAVE_FILENAME = "whole_robot_workspace_temp.xml";
    private static final String INITWORKSPACE = "assets_whole/whole_robot_initworkspace.xml";
    private static final String EMPTYHTML = "file:///android_asset/whole_empty.html";
    private static final String TOOLBOX_XML_PATH = "assets_whole/whole_robot_toolbox.xml";//在BlocklyActivityHelper中的reloadToolbox中当作了判断条件
    private static final List<String> GENERATORS_JS_PATHS = Arrays.asList(//待调用的JS函数
            "assets_whole/whole_generator.js");
    private static final List<String> BLOCK_JSON_PATHS = Arrays.asList(//自定义和默认的模块
            DefaultBlocks.COLOR_BLOCKS_PATH,
            DefaultBlocks.LOGIC_BLOCKS_PATH,
            DefaultBlocks.LOOP_BLOCKS_PATH,
            DefaultBlocks.MATH_BLOCKS_PATH,
            DefaultBlocks.TEXT_BLOCKS_PATH,
            DefaultBlocks.VARIABLE_BLOCKS_PATH,
            "assets_whole/whole_robot_blocks_move.json",
            "assets_whole/whole_robot_blocks_logic.json",
            "assets_whole/whole_robot_blocks_self.json",
            "assets_whole/whole_robot_blocks_sensor.json",
            "assets_whole/whole_robot_blocks_show.json"
    );
    private boolean safeBack;//保存后可安全离开，否则弹出提示框
    private boolean isModify = false;
    private ImageButton wholeBlockBtnBack, wholeBlockBtnSave, wholeBlockBtnRun;
    private Handler mHandler;
    private static boolean flagLoop = false;//轮询查询传感器的值
    private getReadMsgThread readMsgThread;
//    private Button wholeBlockBtnClear;
//    private WebView wholeBlockWebView;//目的是实现js和Java互调 现在自己解析xml就没有必要用这个了
//    private MoveInterface moveInterface;

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
        EditText editText = new EditText(this);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);//横屏时禁止输入法全屏
        mViewListener mListener = new mViewListener();
        View viewWholeBlockly = getLayoutInflater().inflate(R.layout.activity_whole_blockly, null);
        wholeBlockBtnBack = viewWholeBlockly.findViewById(R.id.wholeBlocklyBtnBack);
        wholeBlockBtnBack.setOnClickListener(mListener);
//        wholeBlockBtnClear = viewWholeBlockly.findViewById(R.id.wholeBlocklyBtnClear);
//        wholeBlockBtnClear.setOnClickListener(mListener);
        wholeBlockBtnSave = viewWholeBlockly.findViewById(R.id.wholeBlocklyBtnSave);
        wholeBlockBtnSave.setOnClickListener(mListener);
        wholeBlockBtnRun = viewWholeBlockly.findViewById(R.id.wholeBlocklyBtnRun);
        wholeBlockBtnRun.setOnClickListener(mListener);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
               if(msg.what==0){//运行结束
                   wholeBlockBtnRun.setImageResource(R.drawable.program_start);
                    mToast.show(WholeBlocklyActivity.this,"运行完毕");
               }
            }
        };
        return viewWholeBlockly;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(WholeBlocklyActivity.this);
        safeBack = false;
        isModify = false;
        initArrayList();//得到文件列表
        if (itemNameList.contains(bundleFileName)) {//如果包含才行 需要在文档加载之后 这里需要可能需要改一改 不然会先加载
            //初始页面然后再加载用户界面 有没有需要加载自动保存的界面？
            try {
                mBlocklyActivityHelper.loadWorkspaceFromAppDir(bundleFileName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BlockLoadingException e) {
                mToast.show(WholeBlocklyActivity.this,"信息丢失，文件已损坏");
            }
            isModify = true;//此时的保存界面应不同
            Log.d("Tag", "onLoadUserWorkspace: ");

        }
        flagLoop = true;
        readMsgThread = new getReadMsgThread();
        readMsgThread.start();//开始查询电池电量
    }

    @Override
    public void onPause() {
        super.onPause();
        flagLoop = false;
    }
    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("msg", "succeed");
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        if (readXmlThread != null) {
            readXmlThread.interrupt();
            if(xmlResolver!=null)
            {
                xmlResolver.setinterupt_flag(true);
            }
            readXmlThread = null;
            MainActivity.addMessage(new byte[]{0x30, (byte) 0x80, (byte) 0x80, (byte) 0x80});
            MainActivity.addMessage(new byte[]{0x39, 0x00, 0x00, 0x00});
        }
        super.onDestroy();
    }

    private class mViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wholeBlocklyBtnBack:
                    //应该提示用户是否保存
                    if ((!safeBack) && getController().getWorkspace().hasBlocks()) {
                        AlertDialog.Builder builderBack = new AlertDialog.Builder(WholeBlocklyActivity.this);
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
//                case R.id.wholeBlocklyBtnClear:
//                    onClearWorkspace();
//                    break;
                case R.id.wholeBlocklyBtnSave:
                    if (!isModify) {//若为新增文档
                        AlertDialog.Builder builderSave = new AlertDialog.Builder(WholeBlocklyActivity.this);
                        View view = LayoutInflater.from(WholeBlocklyActivity.this).inflate(R.layout.layout_dialogsetname, null);
                        final EditText editText = view.findViewById(R.id.dialog_EditTxt_name);
                        builderSave.setTitle("输入文件名：").setView(view).setPositiveButton("保存", null);
                        final AlertDialog dialogSave = builderSave.create();
                        dialogSave.show();
                        dialogSave.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//必须在show方法后调用 点击按钮后不消失
                            @Override
                            public void onClick(View v) {
                                String userName = editText.getText().toString().trim();
                                final String saveName = userToXml(userName);
                                if (userName.length() != 0) {
                                    if (ifSameName(saveName, (ArrayList<String>) itemNameList)) {//如果重复 提示是否覆盖
//                                        Toast.makeText(WholeBlocklyActivity.this, "文件已存在", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder coverBuilder = new AlertDialog.Builder(WholeBlocklyActivity.this);
                                        coverBuilder.setTitle("提示").setMessage("文件已存在，是否覆盖？").setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                File fileD = new File(WholeBlocklyActivity.this.getFilesDir(), saveName);
                                                if (fileD.exists()) {
                                                    fileD.delete();//删除已有文件
                                                }
                                                safeBack = true;
                                                mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(saveName);
                                                mToast.show(WholeBlocklyActivity.this, "保存成功");
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
                                        if (PublicMethod.verifyFilename(userName)) {
                                            safeBack = true;
                                            mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(saveName);
                                            mToast.show(WholeBlocklyActivity.this, "保存成功");
                                            dialogSave.dismiss();
                                        } else {
                                            mToast.show(WholeBlocklyActivity.this, "只能包含汉字、英文字母和数字，并且数字不能开头");
                                        }
                                    }
                                } else {
                                    mToast.show(WholeBlocklyActivity.this, "文件名不可为空");
                                }
                            }
                        });
                    } else if (isModify) {//若为修改文档
                        safeBack = true;
                        mBlocklyActivityHelper.saveWorkspaceToAppDirSafely(bundleFileName);
                        mToast.show(WholeBlocklyActivity.this, "保存成功");
                    }
                    break;
                case R.id.wholeBlocklyBtnRun:
                    if (getController().getWorkspace().hasBlocks()) {
//                        onRunCode();//不再生成js代码 转为自己解析xml
                        if(readXmlThread==null) {
                            readXml();
                            wholeBlockBtnRun.setImageResource(R.drawable.program_stop);
                        }else {
                            readXmlThread.interrupt();
                            if(xmlResolver!=null)
                            {
                                xmlResolver.setinterupt_flag(true);
                            }
                            readXmlThread=null;
                            wholeBlockBtnRun.setImageResource(R.drawable.program_start);
                            MainActivity.addMessage(new byte[]{0x30, (byte) 0x80, (byte) 0x80, (byte) 0x80});
                            MainActivity.addMessage(new byte[]{0x39, 0x00, 0x00, 0x00});
                        }
                    }
                    break;

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
            xmlResolver = new XMLResolver(WholeBlocklyActivity.this);
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
                    //运行结束
                    readXmlThread = null;
                    mHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    Log.d("Tag", "解析错误" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    //轮询读取传感器的值
    private class getReadMsgThread extends Thread {
        @Override
        public void run() {
            while (flagLoop) {
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorLed, 0x01});
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorIMU, 0x01});
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorDistence, 0x01});
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorUltrasonicH, 0x02});
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorLedR, 0x03});
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.sensorMagnet, 0x01});
                try {
                    sleep(200);//200ms更新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
            Log.d("Tag", "得到的代码\n" + generatedCode);
            final String newStr = generatedCode;
            WholeBlocklyActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String encoded = "execute("
                            + JavascriptUtil.makeJsString(newStr) + ")";//最后是否默认停止?
                    Log.e("Tag", "encoded:\n  " + encoded);
//                    wholeBlockWebView.loadUrl("javascript:" + encoded);//调用代码以执行encoded
                }
            });
//            final Handler mHandler = new Handler();
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
        }
    };

    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {
        return mCodeGeneratorCallback;
    }

    @Override
    protected boolean checkAllowRestoreBlocklyState(Bundle savedInstanceState) {
        return false;//这样就会调用onLoadInitialWorkspace加载界面
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
    protected void onInitBlankWorkspace() {//加载空页面
        Log.d("Tag", "onInitBlankWorkspace: ");
        BlocklyController controller = getController();
        try {
            controller.loadWorkspaceContents(getAssets().open(INITWORKSPACE));
        } catch (IOException | BlockLoadingException e) {
            throw new IllegalStateException("Couldn't load demo workspace from assets: " + "robot.xml", e);
        }
//        super.onInitBlankWorkspace();
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

    //保存新文件 包括xml js 新增文件命名 存在文件先读取再

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
            if (!(getString(R.string.wholexmlHead).equals(itemNameList.get(i).substring(0, 8)))) {
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
        String wholexml = new String();
        wholexml = getString(R.string.wholexmlHead) + user + getString(R.string.wholexmlTail);
        return wholexml;
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
