package com.luwu.xgo_robot.mFragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.blockly.model.Block;
import com.google.blockly.model.Workspace;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.mToast;
import com.google.blockly.android.AbstractBlocklyFragment;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.blockly.android.control.BlocklyController;
import com.google.blockly.model.DefaultBlocks;
import com.google.blockly.utils.BlockLoadingException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
*之后可以设置两手指实现缩放和双击居中
 */
public class PreviewFragment extends AbstractBlocklyFragment {
    private Button previewBtn;
    private ImageButton previewZoomInBtn,previewZoomOutBtn,previewCenterBtn;
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
            "assets_whole/whole_robot_blocks_show.json",
            "assets_single/single_robot_blocks.json"
    );

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected ViewGroup onCreateSubViews(LayoutInflater inflater) {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_preview, null);
        previewBtn = view.findViewById(R.id.previewBtn);
        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToast.show(PreviewFragment.this.getActivity(),"修改文档");//todo:可以修改该文档
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=this.getArguments();
        String filename=bundle.getString(getString(R.string.previewKey));
        if(filename!=null){
            mBlocklyActivityHelper.loadWorkspaceFromAppDirSafely(filename);
        }
        //设置blockly不可移动 防止预览界面误触
        BlocklyController mController= getController();
        Workspace mWorkspace = mController.getWorkspace();
        long begintime= SystemClock.currentThreadTimeMillis();
        for(Block mBlock:mWorkspace.getRootBlocks()) {//todo: while中的仍可移动
            if (mBlock != null) {
                mBlock.setMovable(false);
                mBlock.setEditable(false);
                Block block=mBlock.getNextBlock();
                while (block != null) {
                    block.setMovable(false);
                    block.setEditable(false);
                    block=block.getNextBlock();
                }
            }
            if(SystemClock.currentThreadTimeMillis()-begintime>1500){//超时限制
                break;
            }
        }
        previewZoomInBtn = view.findViewById(R.id.previewZoomInBtn);
        previewZoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlocklyController controller = getController();
                if(controller!=null){
                    controller.zoomIn();
                }
            }
        });
        previewZoomOutBtn = view.findViewById(R.id.previewZoomOutBtn);
        previewZoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlocklyController controller = getController();
                if(controller!=null){
                    controller.zoomOut();
                }
            }
        });
        previewCenterBtn = view.findViewById(R.id.previewCenterBtn);
        previewCenterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlocklyController controller = getController();
                if(controller!=null){
                    controller.recenterWorkspace();
                }
            }
        });

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
        BlocklyController controller = getController();
        try {
            controller.loadWorkspaceContents(PreviewFragment.this.getActivity().getAssets().open(INITWORKSPACE));
        } catch (IOException | BlockLoadingException e) {
            throw new IllegalStateException("Couldn't load demo workspace from assets: " + "robot.xml", e);
        }
//        super.onLoadInitialWorkspace();
    }

    @Override
    protected void onInitBlankWorkspace() {//加载空页面
        BlocklyController controller = getController();
        try {
            controller.loadWorkspaceContents(PreviewFragment.this.getActivity().getAssets().open(INITWORKSPACE));
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
        super.onLoadWorkspace();
    }

    @Override
    public void onSaveWorkspace() {//保存工作区
        super.onSaveWorkspace();
    }

}
