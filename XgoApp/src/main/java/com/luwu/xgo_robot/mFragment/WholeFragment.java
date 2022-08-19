package com.luwu.xgo_robot.mFragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.luwu.xgo_robot.mActivity.ControlActivity;
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mControl.XMLResolver;
import com.luwu.xgo_robot.mActivity.WholeBlocklyActivity;
import com.luwu.xgo_robot.mMothed.mToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//每次添加新的动作时 需要 1.mDefaultArray中添加名字 2.assets_whole/中添加xml文件
//注意命名格式不能变
// 删除动作时同上
//todo:动作删除之后已经用了动作组的怎么办？
public class WholeFragment extends Fragment {

    private String listChoice;//选中了list中的哪个文件
    private int listChoiceType;//文件类型 0默认项目 1自定义项目
    private String[] mDefaultArray = new String[]{"前进","传感器"};
    private String[] mSelfArray = new String[]{};
    private View mView;
    private PreviewFragment previewFragment;
    private ImageButton fragmentWholeBtnAdd, fragmentWholeBtnModify, fragmentWholeBtnDelete, fragmentWholeBtnRename, fragmentWholeBtnStart;
    private ImageButton fragmentWholeBtnControl;
    private Button wholeDefaultBtn, wholeSelfBtn;
    private TextView wholeDefaultLine, wholeSelfLine;
    private ViewPager viewPager;
    private ProgramViewpagerAdapter viewPagerAdapter;
    private Handler mHandler;
    private static boolean flagLoop = false;//轮询查询传感器的值
    private getReadMsgThread readMsgThread;

    public WholeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whole, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mView = view;
        mViewListener viewListener = new mViewListener();
        fragmentWholeBtnAdd = view.findViewById(R.id.fragmentWholeBtnAdd);
        fragmentWholeBtnAdd.setOnClickListener(viewListener);
        fragmentWholeBtnDelete = view.findViewById(R.id.fragmentWholeBtnDelete);
        fragmentWholeBtnDelete.setOnClickListener(viewListener);
        fragmentWholeBtnModify = view.findViewById(R.id.fragmentWholeBtnModify);
        fragmentWholeBtnModify.setOnClickListener(viewListener);
        fragmentWholeBtnRename = view.findViewById(R.id.fragmentWholeBtnRename);
        fragmentWholeBtnRename.setOnClickListener(viewListener);
        fragmentWholeBtnStart = view.findViewById(R.id.fragmentWholeBtnStart);
        fragmentWholeBtnStart.setOnClickListener(viewListener);
        fragmentWholeBtnControl = view.findViewById(R.id.fragmentWholeBtnControl);
        fragmentWholeBtnControl.setOnClickListener(viewListener);
        wholeDefaultBtn = view.findViewById(R.id.wholeDefaultBtn);
        wholeDefaultBtn.setOnClickListener(viewListener);
        wholeSelfBtn = view.findViewById(R.id.wholeSelfBtn);
        wholeSelfBtn.setOnClickListener(viewListener);
        wholeDefaultLine = view.findViewById(R.id.wholeDefaultLine);
        wholeSelfLine = view.findViewById(R.id.wholeSelfLine);
        fragmentWholeBtnDelete.setVisibility(View.INVISIBLE);//设置按钮不可点击
        fragmentWholeBtnModify.setVisibility(View.INVISIBLE);
        fragmentWholeBtnRename.setVisibility(View.INVISIBLE);
        viewPager = view.findViewById(R.id.fragmentWholePager);
        viewPagerAdapter = new ProgramViewpagerAdapter(getChildFragmentManager(), 0, mDefaultArray, mSelfArray);
        viewPagerAdapter.setItemClickListener(new ProgramViewpagerAdapter.ItemClickListener() {
            @Override
            public void defaultItemClick(int position) {
                listChoice = mDefaultArray[position];
                listChoiceType = ControlActivity.WHOLEDEFAULT;
                refreshPreview();//刷新预览界面
            }

            @Override
            public void selfItemClick(int position) {
                listChoice = mSelfArray[position];
                listChoiceType = ControlActivity.WHOLESELF;
                refreshPreview();//刷新预览界面
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        onPageChangedListener pageListener = new onPageChangedListener();
        viewPager.addOnPageChangeListener(pageListener);
        copyAssetsToFile();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {//运行结束
                    fragmentWholeBtnStart.setImageResource(R.drawable.program_start);
                    mToast.show(WholeFragment.this.getActivity(),"运行完毕");
                }else if(msg.what==1){
                    fragmentWholeBtnStart.setImageResource(R.drawable.program_start);
                    mToast.show(WholeFragment.this.getActivity(),"未找到文件");
                }
            }
        };
    }

    @Override
    public void onResume() {//在这里更新列表
        super.onResume();
        initArrayList();//刷新列表
        initListView(mView);//刷新控件
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
    public void onDestroy() {
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

    private void initListView(View view) {
        viewPager.setCurrentItem(0);
    }

    private void refreshPreview() {
        if (previewFragment != null) {
            getChildFragmentManager().beginTransaction().remove(previewFragment).commitAllowingStateLoss();
            previewFragment = null;
        }
        previewFragment = new PreviewFragment();
        Bundle bundle = new Bundle();
        if(listChoiceType==ControlActivity.WHOLEDEFAULT){
            bundle.putString(getString(R.string.previewKey), userToXmlAuto(listChoice));
        }else if(listChoiceType==ControlActivity.WHOLESELF){
            bundle.putString(getString(R.string.previewKey), userToXml(listChoice));
        }
        previewFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.previewWhole, previewFragment).commitAllowingStateLoss();
    }

    private class mViewListener implements View.OnClickListener {
        Intent intent;
        Bundle bundle = new Bundle();

        @Override
        public void onClick(View v) {//想把这里改成QQ那种形式，有时间试试看
            switch (v.getId()) {
                case R.id.wholeDefaultBtn:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.wholeSelfBtn:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.fragmentWholeBtnAdd:
                    intent = new Intent(WholeFragment.this.getContext(), WholeBlocklyActivity.class);
                    bundle.putString(getString(R.string.fragment2ActivityKey), "0");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.fragmentWholeBtnModify:
                    if (listChoice != null) {
                        intent = new Intent(WholeFragment.this.getContext(), WholeBlocklyActivity.class);
                        bundle.putString(getString(R.string.fragment2ActivityKey), listChoice);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    } else {
                        mToast.show(WholeFragment.this.getActivity(), "请选择要修改的动作组");
                    }
                    break;
                case R.id.fragmentWholeBtnDelete://提示用户是否删除
                    if (listChoice != null) {
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(WholeFragment.this.getActivity());
                        deleteBuilder.setTitle("提示").setMessage("是否删除该文件：" + listChoice).setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File fileD = new File(WholeFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                if (fileD.exists()) {
                                    fileD.delete();
                                    initArrayList();//刷新列表
                                } else {
                                    mToast.show(WholeFragment.this.getActivity(), "未找到文件");
                                }
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        mToast.show(WholeFragment.this.getActivity(), "请选择要删除的动作组");
                    }
                    break;
                case R.id.fragmentWholeBtnRename:
                    if (listChoice != null) {
                        AlertDialog.Builder builderReName = new AlertDialog.Builder(WholeFragment.this.getActivity());
                        View view = LayoutInflater.from(WholeFragment.this.getActivity()).inflate(R.layout.layout_dialogsetname, null);
                        final EditText editText = view.findViewById(R.id.dialog_EditTxt_name);
                        builderReName.setTitle("输入文件名：").setView(view).setPositiveButton("保存", null);
                        final AlertDialog dialogReName = builderReName.create();
                        dialogReName.show();
                        dialogReName.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {//必须在show方法后调用 点击按钮后不消失
                            @Override
                            public void onClick(View v) {
                                String userName = editText.getText().toString().trim();
                                final String saveName = userToXml(userName);
                                if (userName.length() != 0) {
                                    if (userName.equals(listChoice)) {//如果保存的名字相同
                                        mToast.show(WholeFragment.this.getActivity(), "不能重命名为相同文件名");
                                    } else if (ifSameName(userName, mSelfArray)) {//如果重复 提示是否覆盖
//                                    Toast.makeText(WholeFragment.this.getActivity(), "文件已存在", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder coverBuilder = new AlertDialog.Builder(WholeFragment.this.getActivity());
                                        coverBuilder.setTitle("提示").setMessage("文件已存在，是否覆盖？").setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                File fileR = new File(WholeFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                                if (fileR.exists()) {
                                                    File fileS = new File(WholeFragment.this.getActivity().getFilesDir(), saveName);
                                                    if (fileS.exists()) {
                                                        fileS.delete();//删除已有文件
                                                    }
                                                    fileR.renameTo(new File(WholeFragment.this.getActivity().getFilesDir(), saveName));
                                                    dialog.dismiss();
                                                    dialogReName.dismiss();
                                                    initArrayList();//刷新列表
                                                } else {
                                                    mToast.show(WholeFragment.this.getActivity(), "请选择要重命名的项目");
                                                }
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    } else {
                                        if (PublicMethod.verifyFilename(userName)) {//如果文件名合法
                                            File fileR = new File(WholeFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                            if (fileR.exists()) {
                                                fileR.renameTo(new File(WholeFragment.this.getActivity().getFilesDir(), saveName));
                                                dialogReName.dismiss();
                                                initArrayList();//刷新列表
                                            } else {
                                                mToast.show(WholeFragment.this.getActivity(), "请选择要重命名的项目");
                                            }
                                        } else {
                                            mToast.show(WholeFragment.this.getActivity(), "只能包含汉字、英文字母和数字，并且数字不能开头");
                                        }
                                    }
                                } else {
                                    mToast.show(WholeFragment.this.getActivity(), "文件名不可为空");
                                }
                            }
                        });
                    } else {
                        mToast.show(WholeFragment.this.getActivity(), "请选择要重命名的动作组");
                    }
                    break;
                case R.id.fragmentWholeBtnStart:
                    if (listChoice != null) {
                        if (readXmlThread == null) {
                            readXml();
                            fragmentWholeBtnStart.setImageResource(R.drawable.program_stop);
                        } else {
                            readXmlThread.interrupt();
                            if(xmlResolver!=null)
                            {
                                xmlResolver.setinterupt_flag(true);
                            }
                            readXmlThread = null;
                            fragmentWholeBtnStart.setImageResource(R.drawable.program_start);
                            MainActivity.addMessage(new byte[]{0x30, (byte) 0x80, (byte) 0x80, (byte) 0x80});
                            MainActivity.addMessage(new byte[]{0x39, 0x00, 0x00, 0x00});
                        }
                    } else {
                        mToast.show(WholeFragment.this.getActivity(), "请选择要执行的动作组");
                    }
                    break;
                case R.id.fragmentWholeBtnControl://把动作组添加到控制界面
                    if (listChoice != null) {
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WholeFragment.this.getActivity());
//                        alertDialog.setTitle("添加到：").setMessage("添加动作组“" + listChoice + "”到控制器-我的程序")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        SharedPreferences info = WholeFragment.this.getActivity().getSharedPreferences(getString(R.string.controlProgramSp), MODE_PRIVATE);
//                                        SharedPreferences.Editor edit = info.edit();
//                                        for (int i = 0; i < 5; i++) {//如果重复
//                                            String name = info.getString(getString(R.string.controlProgramKey) + i, "none");
//                                            if ((listChoiceType + "动作组:" + listChoice).equals(name)) {
//                                                mToast.show(WholeFragment.this.getActivity(), "重复添加：该程序已被添加");
//                                                return;
//                                            }
//                                        }
//                                        for (int i = 0; i < 5; i++) {
//                                            String name = info.getString(getString(R.string.controlProgramKey) + i, "none");
//                                            if (("none".equals(name))) {
//                                                edit.putString(getString(R.string.controlProgramKey) + i, listChoiceType + "动作组:" + listChoice);//为了区别whole和single传递完整文件
//                                                edit.commit();
//                                                break;
//                                            }
//                                            if (i == 4) {//说明全都被占用
//                                                mToast.show(WholeFragment.this.getActivity(), "添加失败\n已达上限，最多添加5个程序");
//                                            }
//                                        }
//                                    }
//                                }).setCancelable(true).show();
                        mToast.show(WholeFragment.this.getActivity(),"该功能已弃用");
                    } else {
                        mToast.show(WholeFragment.this.getActivity(), "请选择要添加的动作组");
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
            xmlResolver = new XMLResolver(WholeFragment.this.getActivity());
            File xmlFile;
            if(listChoiceType==ControlActivity.WHOLEDEFAULT){
                xmlFile = new File(getActivity().getFilesDir(), userToXmlAuto(listChoice));
            }else{
                xmlFile = new File(getActivity().getFilesDir(), userToXml(listChoice));
            }
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
            } else {
                mHandler.sendEmptyMessage(1);
                readXmlThread = null;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {//toolbox.xml加载失败的处理办法
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0) && (data != null)) {//本页面跳转的
            String result = data.getExtras().getString("msg");
            switch (result) {
                case "succeed"://正常退出
                    break;
                case "error"://报错
                    AlertDialog.Builder builder = new AlertDialog.Builder(WholeFragment.this.getActivity());
                    builder.setCancelable(false);
                    builder.setTitle("错误").setMessage("toolbox文件丢失：正在尝试删除错误数据……\n删除：删除可疑文件，您的已有数据将丢失\n修复：部分修复您的数据，也可能修复失败")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {//删除所有用户自定义json和toolbox文件
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File file = new File(WholeFragment.this.getActivity().getFilesDir(), "whole_file_toolbox.xml");
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    file = new File(WholeFragment.this.getActivity().getFilesDir(), "whole_file_blocks_self.json");
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    final ProgressDialog progress = new ProgressDialog(WholeFragment.this.getActivity());
                                    progress.setMessage("正在删除");
                                    progress.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                            mToast.show(WholeFragment.this.getActivity(), "删除成功");
                                        }
                                    }, 2000);
                                }
                            }).setNegativeButton("修复", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//目前看来无法修复
                            final ProgressDialog progress = new ProgressDialog(WholeFragment.this.getActivity());
                            progress.setMessage("正在修复");
                            progress.show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progress.dismiss();
                                    mToast.show(WholeFragment.this.getActivity(), "修复失败");
                                }
                            }, 2000);
                        }
                    }).show();
                    break;
            }
        }
    }

    private class onPageChangedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {//state 0什么都没做 1正在滑动 2滑动完毕
            if (state == 2) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        listChoice = null;
                        viewPagerAdapter.setDefaultNoListChioce();
                        wholeDefaultBtn.setTextColor(getResources().getColor(R.color.programActive));
                        wholeDefaultLine.setBackgroundColor(getResources().getColor(R.color.programActive));
                        wholeSelfBtn.setTextColor(getResources().getColor(R.color.programInactive));
                        wholeSelfLine.setBackgroundColor(getResources().getColor(R.color.transparent));
                        fragmentWholeBtnDelete.setVisibility(View.INVISIBLE);//设置按钮不可点击
                        fragmentWholeBtnModify.setVisibility(View.INVISIBLE);
                        fragmentWholeBtnRename.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        listChoice = null;
                        viewPagerAdapter.setSelfNoListChioce();
                        wholeSelfBtn.setTextColor(getResources().getColor(R.color.programActive));
                        wholeSelfLine.setBackgroundColor(getResources().getColor(R.color.programActive));
                        wholeDefaultBtn.setTextColor(getResources().getColor(R.color.programInactive));
                        wholeDefaultLine.setBackgroundColor(getResources().getColor(R.color.transparent));
                        fragmentWholeBtnDelete.setVisibility(View.VISIBLE);
                        fragmentWholeBtnModify.setVisibility(View.VISIBLE);
                        fragmentWholeBtnRename.setVisibility(View.VISIBLE);
                        break;
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
    /*
     *下面为文件输入输出操作
     */

    //本页面重命名 删除等时手动更新 初始化时自动更新
    //这里应该还涉及到一个排序问题
    private void initArrayList() {
        List<String> item = new ArrayList<>();
        String assestFileName = this.getActivity().getFilesDir().getAbsolutePath();
        File xmlFile = new File(assestFileName);
        File result[] = xmlFile.listFiles();
        for (int i = 0; i < result.length; i++) {
            item.add(i, result[i].getName());
        }
        for (int i = 0; i < item.size(); i++) {
            if (!(getString(R.string.wholexmlHead).equals(item.get(i).substring(0, 8)))) {
                item.remove(i);
                i--;
            }
        }
        mSelfArray = item.toArray(new String[item.size()]);
        for (int i = 0; i < mSelfArray.length; i++) {//去格式化
            mSelfArray[i] = xmlToUser(mSelfArray[i]);
            Log.d("Tag", "initArrayList: " + mSelfArray[i]);
        }
        viewPagerAdapter.refreshSelfFrgment(mSelfArray);//更新界面
        listChoice = null;
    }
    void copyAssetsToFile() {//在这里更新了系统文件
        for(String name:mDefaultArray)
        {
            File file = new File(WholeFragment.this.getActivity().getFilesDir(), userToXmlAuto(name));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream assets = WholeFragment.this.getActivity().getAssets().open("assets_whole/whole_"+name+"_workspace.xml");//拷贝assets中的文件
                    byte[] buffer = new byte[assets.available()];
                    assets.read(buffer);
                    fos.write(buffer);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //将系统自带的名字格式化
    private String userToXmlAuto(String user) {
        String autowholexml = new String();
        if (user == null) {
            user = "";
        }
        autowholexml = getString(R.string.wholexmlHeadAuto) + user + getString(R.string.wholexmlTail);
        return autowholexml;
    }

    //将系统自带文件名去格式化
    private String xmlToUserAuto(String autowholexmlauto) {
        String user = new String();
        if (getString(R.string.wholexmlHeadAuto).equals(autowholexmlauto.substring(0, 12))) {
            user = autowholexmlauto.substring(12, (autowholexmlauto.length() - 4));
        }
        return user;
    }
    //将用户输入的名字格式化
    private String userToXml(String user) {
        String wholexml = new String();
        if (user == null) {
            user = "";
        }
        wholexml = getString(R.string.wholexmlHead) + user + getString(R.string.wholexmlTail);
        return wholexml;
    }

    //将文件名去格式化
    private String xmlToUser(String wholexml) {
        String user = new String();
        if (getString(R.string.wholexmlHead).equals(wholexml.substring(0, 8))) {
            user = wholexml.substring(8, (wholexml.length() - 4));
        }
        return user;
    }

    //判断文件是否已经存在
    private boolean ifSameName(String newName, String[] ListOfItemsName) {
        boolean ifSameName = false;
        for (int i = 0; i < ListOfItemsName.length; i++) {
            if (newName.trim().equals((ListOfItemsName[i]).trim())) {
                ifSameName = true;
                break;
            }
        }
        return ifSameName;
    }
}
