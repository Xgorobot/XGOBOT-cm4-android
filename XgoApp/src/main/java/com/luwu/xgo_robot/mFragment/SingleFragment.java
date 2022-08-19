package com.luwu.xgo_robot.mFragment;

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
import com.luwu.xgo_robot.mControl.JsonControl;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mControl.ToolboxControl;
import com.luwu.xgo_robot.mControl.XMLResolver;
import com.luwu.xgo_robot.mActivity.SingleBlocklyActivity;
import com.luwu.xgo_robot.mMothed.mToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/*说明：系统自带文件命名格式 autosinglexml名字.xml
 * 用户自定义文件命名格式singlexml名字.xml
 * 除了 名字 之外的被用作文件的判断条件 不要更改*/
//每次添加新的动作时 需要 1.mDefaultArray中添加名字 2.assets_single/中添加xml文件
// 3.assets_whole/whole_robot_blocks_self.json和assets_whole/whole_robot_toolbox.xml中添加相应内容
//注意命名格式不能变
// 删除动作时同上

public class SingleFragment extends Fragment {
    private JsonControl jsonControl;//用来动态添加block的两个变量
    private ToolboxControl toolboxControl;

    private String listChoice;//选中了list中的哪个文件
    private int listChoiceType;//文件类型 2默认项目 3自定义项目

    private String[] mDefaultArray = new String[]{"招手", "撒尿"};
    String[] mSelfArray = new String[]{};
    private View mView;
    private PreviewFragment previewFragment;
    private ImageButton fragmentSingleBtnAdd, fragmentSingleBtnModify, fragmentSingleBtnDelete, fragmentSingleBtnRename, fragmentSingleBtnStart;
    private ImageButton fragmentSingleBtnControl;
    private Button singleDefaultBtn, singleSelfBtn;
    private TextView singleDefaultLine, singleSelfLine;
    private ViewPager viewPager;
    private ProgramViewpagerAdapter viewPagerAdapter;
    private Handler mHandler;

    public SingleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mView = view;
        mViewListener viewListener = new mViewListener();
        fragmentSingleBtnAdd = view.findViewById(R.id.fragmentSingleBtnAdd);
        fragmentSingleBtnDelete = view.findViewById(R.id.fragmentSingleBtnDelete);
        fragmentSingleBtnModify = view.findViewById(R.id.fragmentSingleBtnModify);
        fragmentSingleBtnRename = view.findViewById(R.id.fragmentSingleBtnRename);
        fragmentSingleBtnStart = view.findViewById(R.id.fragmentSingleBtnStart);
        fragmentSingleBtnControl = view.findViewById(R.id.fragmentSingleBtnControl);
        fragmentSingleBtnAdd.setOnClickListener(viewListener);
        fragmentSingleBtnDelete.setOnClickListener(viewListener);
        fragmentSingleBtnModify.setOnClickListener(viewListener);
        fragmentSingleBtnRename.setOnClickListener(viewListener);
        fragmentSingleBtnStart.setOnClickListener(viewListener);
        fragmentSingleBtnControl.setOnClickListener(viewListener);
        fragmentSingleBtnDelete.setVisibility(View.INVISIBLE);//设置按钮不可点击
        fragmentSingleBtnModify.setVisibility(View.INVISIBLE);
        fragmentSingleBtnRename.setVisibility(View.INVISIBLE);
        jsonControl = new JsonControl();
        toolboxControl = new ToolboxControl();
        singleDefaultBtn = view.findViewById(R.id.singleDefaultBtn);
        singleDefaultBtn.setOnClickListener(viewListener);
        singleSelfBtn = view.findViewById(R.id.singleSelfBtn);
        singleSelfBtn.setOnClickListener(viewListener);
        singleDefaultLine = view.findViewById(R.id.singleDefaultLine);
        singleSelfLine = view.findViewById(R.id.singleSelfLine);
        viewPager = view.findViewById(R.id.fragmentSinglePager);
        viewPagerAdapter = new ProgramViewpagerAdapter(getChildFragmentManager(), 0, mDefaultArray, mSelfArray);
        viewPagerAdapter.setItemClickListener(new ProgramViewpagerAdapter.ItemClickListener() {
            @Override
            public void defaultItemClick(int position) {
                listChoice = mDefaultArray[position];
                listChoiceType = ControlActivity.SINGLEDEFAULT;
                refreshPreview();//刷新预览界面
            }

            @Override
            public void selfItemClick(int position) {
                listChoice = mSelfArray[position];
                listChoiceType = ControlActivity.SINGLESELF;
                refreshPreview();//刷新预览界面
            }
        });
        viewPager.setAdapter(viewPagerAdapter);
        onPageChangedListener pageListener = new onPageChangedListener();
        viewPager.addOnPageChangeListener(pageListener);
        copyAssetsToFile();//拷贝系统文件
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {//运行结束
                    fragmentSingleBtnStart.setImageResource(R.drawable.program_start);
                    mToast.show(SingleFragment.this.getActivity(), "运行完毕");
                } else if (msg.what == 1) {
                    fragmentSingleBtnStart.setImageResource(R.drawable.program_start);
                    mToast.show(SingleFragment.this.getActivity(), "未找到文件");
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        initArrayList();
        initListView(mView);
    }

    @Override
    public void onDestroy() {
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
        if (listChoiceType == ControlActivity.SINGLEDEFAULT) {
            bundle.putString(getString(R.string.previewKey), userToXmlAuto(listChoice));
        } else if (listChoiceType == ControlActivity.SINGLESELF) {
            bundle.putString(getString(R.string.previewKey), userToXml(listChoice));
        }
        previewFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.previewSingle, previewFragment).commitAllowingStateLoss();
    }

    private class mViewListener implements View.OnClickListener {
        Intent intent;
        Bundle bundle = new Bundle();

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.singleDefaultBtn:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.singleSelfBtn:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.fragmentSingleBtnAdd:
                    intent = new Intent(SingleFragment.this.getContext(), SingleBlocklyActivity.class);
                    bundle.putString(getString(R.string.fragment2ActivityKey), "0");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.fragmentSingleBtnModify:
                    if (listChoice != null) {
                        intent = new Intent(SingleFragment.this.getContext(), SingleBlocklyActivity.class);
                        bundle.putString(getString(R.string.fragment2ActivityKey), listChoice);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    } else {
                        mToast.show(SingleFragment.this.getActivity(), "请选择要修改的动作");
                    }
                    break;
                //以下几项要比wholefragment多一些操作，即除xml文件外，还要更改toolbox文件和json文件
                case R.id.fragmentSingleBtnDelete:
                    if (listChoice != null) {
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(SingleFragment.this.getActivity());
                        deleteBuilder.setTitle("提示").setMessage("是否删除该文件：" + listChoice).setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File fileD = new File(SingleFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                if (fileD.exists()) {
                                    fileD.delete();
                                    //动态添加block 注意这里应先删除toolbox.xml 再删除json
                                    File fileT = new File(SingleFragment.this.getActivity().getFilesDir(), "whole_file_toolbox.xml");
                                    File fileB = new File(SingleFragment.this.getActivity().getFilesDir(), "whole_file_blocks_self.json");
                                    if (fileT.exists() && fileB.exists()) {
                                        toolboxControl.deleteBlock(fileT, jsonControl.getTypeByName(fileB, listChoice));
                                        jsonControl.deleteJson(fileB, listChoice);
                                        Log.d("Tag", "onClick: 成功");
                                    }
                                    initArrayList();//刷新列表
                                } else {
                                    mToast.show(SingleFragment.this.getActivity(), "未找到文件");
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
                        mToast.show(SingleFragment.this.getActivity(), "请选择要删除的动作");
                    }
                    break;
                case R.id.fragmentSingleBtnRename:
                    if (listChoice != null) {
                        AlertDialog.Builder builderReName = new AlertDialog.Builder(SingleFragment.this.getActivity());
                        View view = LayoutInflater.from(SingleFragment.this.getActivity()).inflate(R.layout.layout_dialogsetname, null);
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
                                        mToast.show(SingleFragment.this.getActivity(), "不能重命名为相同文件名");
                                    } else if (ifSameName(userName, mSelfArray)) {//如果重复 提示是否覆盖
//                                    Toast.makeText(WholeFragment.this.getActivity(), "文件已存在", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder coverBuilder = new AlertDialog.Builder(SingleFragment.this.getActivity());
                                        coverBuilder.setTitle("提示").setMessage("文件已存在，是否覆盖？").setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                File fileR = new File(SingleFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                                if (fileR.exists()) {
                                                    File fileS = new File(SingleFragment.this.getActivity().getFilesDir(), saveName);
                                                    if (fileS.exists()) {
                                                        fileS.delete();//删除已有文件
                                                    }
                                                    fileR.renameTo(new File(SingleFragment.this.getActivity().getFilesDir(), saveName));
                                                    //动态添加block 此处不用

                                                    dialog.dismiss();
                                                    dialogReName.dismiss();
                                                    initArrayList();//刷新列表
                                                } else {
                                                    mToast.show(SingleFragment.this.getActivity(), "请选择要重命名的项目");
                                                }
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                    } else {
                                        if (PublicMethod.verifyFilename(userName)) {
                                            File fileR = new File(SingleFragment.this.getActivity().getFilesDir(), userToXml(listChoice));
                                            if (fileR.exists()) {
                                                fileR.renameTo(new File(SingleFragment.this.getActivity().getFilesDir(), saveName));
                                                //动态添加block
                                                File fileB = new File(SingleFragment.this.getActivity().getFilesDir(), "whole_file_blocks_self.json");
                                                if (fileB.exists()) {
                                                    jsonControl.renameJson(fileB, userName, listChoice);
                                                    //toolbox.xml不用重命名
                                                    Log.d("Tag", "onClick: 成功");
                                                }
                                                dialogReName.dismiss();
                                                initArrayList();//刷新列表
                                            } else {
                                                mToast.show(SingleFragment.this.getActivity(), "请选择要重命名的项目");
                                            }
                                        } else {
                                            mToast.show(SingleFragment.this.getActivity(), "只能包含汉字、英文字母和数字，并且数字不能开头");
                                        }
                                    }
                                } else {
                                    mToast.show(SingleFragment.this.getActivity(), "文件名不可为空");
                                }
                            }
                        });
                    } else {
                        mToast.show(SingleFragment.this.getActivity(), "请选择要重命名的动作");
                    }
                    break;
                case R.id.fragmentSingleBtnStart:
                    if (listChoice != null) {
                        if (readXmlThread == null) {
                            readXml();
                            fragmentSingleBtnStart.setImageResource(R.drawable.program_stop);
                        } else {
                            readXmlThread.interrupt();
                            if (xmlResolver != null) {
                                xmlResolver.setinterupt_flag(true);
                            }
                            readXmlThread = null;
                            fragmentSingleBtnStart.setImageResource(R.drawable.program_start);
                            MainActivity.addMessage(new byte[]{0x5D, (byte) 0x01});//恢复站立姿势 退出舵机模式
                        }
                    } else {
                        mToast.show(SingleFragment.this.getActivity(), "请选择要执行的动作");
                    }
                    break;
                case R.id.fragmentSingleBtnControl://把动作组添加到控制界面
                    if (listChoice != null) {
//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SingleFragment.this.getActivity());
//                        alertDialog.setTitle("添加到：").setMessage("添加动作“" + listChoice + "”到控制器-我的程序")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        SharedPreferences info = SingleFragment.this.getActivity().getSharedPreferences(getString(R.string.controlProgramSp), MODE_PRIVATE);
//                                        SharedPreferences.Editor edit = info.edit();
//                                        for (int i = 0; i < 5; i++) {//如果重复
//                                            String name = info.getString(getString(R.string.controlProgramKey) + i, "none");
//                                            if ((listChoiceType + "动作:" + listChoice).equals(name)) {
//                                                mToast.show(SingleFragment.this.getActivity(), "重复添加：该程序已被添加");
//                                                return;
//                                            }
//                                        }
//                                        for (int i = 0; i < 5; i++) {
//                                            String name = info.getString(getString(R.string.controlProgramKey) + i, "none");
//                                            if (("none".equals(name))) {
//                                                edit.putString(getString(R.string.controlProgramKey) + i, listChoiceType + "动作:" + listChoice);//为了区别whole和single传递完整文件
//                                                edit.commit();
//                                                break;
//                                            }
//                                            if (i == 4) {//说明全都被占用
//                                                mToast.show(SingleFragment.this.getActivity(), "添加失败\n已达上限，最多添加5个程序");
//                                            }
//                                        }
//                                    }
//                                }).setCancelable(true).show();
                        mToast.show(SingleFragment.this.getActivity(),"该功能已弃用");
                    } else {
                        mToast.show(SingleFragment.this.getActivity(), "请选择要添加的动作");
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
            xmlResolver = new XMLResolver(SingleFragment.this.getActivity());
            File xmlFile;
            if (listChoiceType == ControlActivity.SINGLEDEFAULT) {
                xmlFile = new File(getActivity().getFilesDir(), userToXmlAuto(listChoice));
            } else {
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
                        singleDefaultBtn.setTextColor(getResources().getColor(R.color.programActive));
                        singleDefaultLine.setBackgroundColor(getResources().getColor(R.color.programActive));
                        singleSelfBtn.setTextColor(getResources().getColor(R.color.programInactive));
                        singleSelfLine.setBackgroundColor(getResources().getColor(R.color.transparent));
                        fragmentSingleBtnDelete.setVisibility(View.INVISIBLE);//设置按钮不可点击
                        fragmentSingleBtnModify.setVisibility(View.INVISIBLE);
                        fragmentSingleBtnRename.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        listChoice = null;
                        viewPagerAdapter.setSelfNoListChioce();
                        singleSelfBtn.setTextColor(getResources().getColor(R.color.programActive));
                        singleSelfLine.setBackgroundColor(getResources().getColor(R.color.programActive));
                        singleDefaultBtn.setTextColor(getResources().getColor(R.color.programInactive));
                        singleDefaultLine.setBackgroundColor(getResources().getColor(R.color.transparent));
                        fragmentSingleBtnDelete.setVisibility(View.VISIBLE);
                        fragmentSingleBtnModify.setVisibility(View.VISIBLE);
                        fragmentSingleBtnRename.setVisibility(View.VISIBLE);
                        break;
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
            if (!(getString(R.string.singlexmlHead).equals(item.get(i).substring(0, 9)))) {
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
        for (String name : mDefaultArray) {
            File file = new File(SingleFragment.this.getActivity().getFilesDir(), userToXmlAuto(name));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream assets = SingleFragment.this.getActivity().getAssets().open("assets_single/single_" + name + "_workspace.xml");//拷贝assets中的文件
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
        String autosinglexml = new String();
        if (user == null) {
            user = "";
        }
        autosinglexml = getString(R.string.singlexmlHeadAuto) + user + getString(R.string.singlexmlTail);
        return autosinglexml;
    }

    //将系统文件名去格式化
    private String xmlToUserAuto(String autosinglexml) {
        String user = new String();
        if (getString(R.string.singlexmlHeadAuto).equals(autosinglexml.substring(0, 13))) {
            user = autosinglexml.substring(13, (autosinglexml.length() - 4));
        }
        return user;
    }

    //将用户输入的名字格式化
    private String userToXml(String user) {
        String singlexml = new String();
        if (user == null) {
            user = "";
        }
        singlexml = getString(R.string.singlexmlHead) + user + getString(R.string.singlexmlTail);
        return singlexml;
    }

    //将文件名去格式化
    private String xmlToUser(String wholexml) {
        String user = new String();
        if (getString(R.string.singlexmlHead).equals(wholexml.substring(0, 9))) {
            user = wholexml.substring(9, (wholexml.length() - 4));
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
