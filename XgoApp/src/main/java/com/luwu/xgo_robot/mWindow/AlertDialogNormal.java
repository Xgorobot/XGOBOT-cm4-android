package com.luwu.xgo_robot.mWindow;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.luwu.xgo_robot.R;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIDialog;

//自定义弹窗
public class AlertDialogNormal extends Dialog {

    private IConfirmLister confirmLister;
    private ICancleLister cancleLister;
    private Context context;
    private float mulWidth = 0.6f, mulHeight = 0.6f;
    private Button alertdialogBtnConnect;
    private ImageView alertdialogBtnExit;
    private TextView alertdialogTxt;
    private ImageView alertdialogImg;
    int idImag=0;
    private String txtTxt,txtBtnConnect;



    public AlertDialogNormal(Context context) {
        super(context);
        this.context = context;
    }
//接口必须实现
    public void setConfirmLister(IConfirmLister confirmLister) { this.confirmLister = confirmLister; }
    public void setCancleLister(ICancleLister cancleLister) {
        this.cancleLister = cancleLister;
    }
//链式set
    public AlertDialogNormal setSize(float width, float heigh) {
        this.mulWidth = width;
        this.mulHeight = heigh;
        return this;
    }
    public AlertDialogNormal setImg(int id){
        this.idImag = id;
        return this;
    }
    public AlertDialogNormal setTxt(String s){
        this.txtTxt = s;
        return this;
    }
    public AlertDialogNormal setBtnConnectTxt(String s){
        this.txtBtnConnect = s;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alertdialognormal);

        alertdialogBtnExit = findViewById(R.id.alertdialogBtnExit);
        alertdialogBtnConnect = findViewById(R.id.alertdialogBtnConnect);
        alertdialogTxt = findViewById(R.id.alertdialogTxt);
        alertdialogImg = findViewById(R.id.alertdialogImg);
        if(txtBtnConnect != null){alertdialogBtnConnect.setText(txtBtnConnect);}
        else {alertdialogBtnConnect.setText("确定");}
        if(txtTxt != null){alertdialogTxt.setText(txtTxt);}
        else{alertdialogTxt.setText("这里需要输入文字");}
        if(idImag != 0){alertdialogImg.setImageResource(idImag);}
//        else{alertdialogImg.setImageResource(R.drawable.image);}
//设置弹窗宽度
        WindowManager manager = getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        params.width = (int) (size.x * mulWidth);
        params.height = (int) (size.y * mulHeight);
        getWindow().setAttributes(params);

        AlertDialogNormal.this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//设置弹窗背景透明
        hideBottomUIDialog(AlertDialogNormal.this);//隐藏虚拟按键

        alertdialogBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cancleLister != null) {
                    cancleLister.cancleDialog();
                }
            }
        });
        alertdialogBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmLister != null) {
                    confirmLister.confirmDialog();
                }
            }
        });
    }

    public interface ICancleLister {
        void cancleDialog();
    }

    public interface IConfirmLister {
        void confirmDialog();
    }
}
