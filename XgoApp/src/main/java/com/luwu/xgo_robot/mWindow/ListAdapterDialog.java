package com.luwu.xgo_robot.mWindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luwu.xgo_robot.R;
/*
*蓝牙设备列表适配器
 */
public class ListAdapterDialog extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private String[] text;

    public ListAdapterDialog(Context context, String[] text) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.text = text;
    }


    @Override
    public int getCount() {
        if (this.text == null) {
            return 0;
        } else {
            return this.text.length;
        }

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_listviewdialog, null);
            viewHolder = new ViewHolder();
            viewHolder.text = convertView.findViewById(R.id.listviewNameTxt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//给控件赋值
        if (text[position] != null) {
            viewHolder.text.setText(text[position]);
        } else {
            viewHolder.text.setText("未定义动作");
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView text;
    }
}
