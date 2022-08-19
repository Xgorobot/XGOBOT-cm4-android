package com.luwu.xgo_robot.mWindow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.luwu.xgo_robot.R;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIDialog;

/*
 *自定义弹窗显示列表项
 */
public class AlertDialogList extends Dialog {

    private IItemLister itemLister;
    private ICancelLister cancelLister;
    private Context context;
    private float mulWidth = 0.6f, mulHeight = 0.6f;
    private TextView alertdialogListTxt;
    private ListView alertdialogListView;
    private String txtTxt;
    private String[] listTxt = {"none"};


    public AlertDialogList(Context context) {
        super(context);
        this.context = context;
    }

    //必须实现该接口
    public void setItemLister(IItemLister itemLister) {
        this.itemLister = itemLister;
    }

    public void setcancelLister(ICancelLister cancelLister) {
        this.cancelLister = cancelLister;
    }

    //链式set
    public AlertDialogList setSize(float width, float heigh) {
        this.mulWidth = width;
        this.mulHeight = heigh;
        return this;
    }

    public AlertDialogList setTxt(String s) {
        this.txtTxt = s;
        return this;
    }

    public AlertDialogList setListTxt(String[] s) {
        this.listTxt = s;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alertdialoglist);

        alertdialogListTxt = findViewById(R.id.alertdialogListTxt);
        alertdialogListView = findViewById(R.id.alertdialogListView);
        alertdialogListView.setAdapter(new ListAdapterDialog(this.context, listTxt));

        if (txtTxt != null) {
            alertdialogListTxt.setText(txtTxt);
        } else {
            alertdialogListTxt.setText("设置标题文字：");
        }
//设置弹窗宽度
        WindowManager manager = getWindow().getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        params.width = (int) (size.x * mulWidth);
        params.height = (int) (size.y * mulHeight);
        getWindow().setAttributes(params);

        AlertDialogList.this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);//设置弹窗背景透明
        hideBottomUIDialog(AlertDialogList.this);//隐藏虚拟按键

        alertdialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (itemLister != null) {
                    itemLister.itemDialog(position);
                }
            }
        });
        this.setCancelable(true);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(cancelLister != null){
                    cancelLister.cancelDialog();
                }
            }
        });
    }

    public interface ICancelLister {
        void cancelDialog();
    }

    public interface IItemLister {
        void itemDialog(int position);
    }
}
