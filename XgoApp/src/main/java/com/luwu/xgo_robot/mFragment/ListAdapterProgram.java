package com.luwu.xgo_robot.mFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luwu.xgo_robot.R;

//为编程Activity中的List写的Adapter
public class ListAdapterProgram extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private String[] mArray;//列表中内容
    private int listChoice = -1;//被点击后改变颜色
    private String addString = "    ";

    public void setListChoice(int listChoice) {
        this.listChoice = listChoice;
    }

    public void setListArray(String[] listArray) {
        this.mArray = listArray;
    }

    public ListAdapterProgram(Context context, String[] array) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mArray = array;
        if (this.mArray == null) {
            this.mArray = new String[]{addString + "没有可展示的项目"};
        } else if (this.mArray.length == 0) {
            this.mArray = new String[]{addString + "没有可展示的项目"};
        }
    }

    @Override
    public int getCount() {
        return this.mArray.length;
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
            convertView = mInflater.inflate(R.layout.layout_listviewprogram, null);
            viewHolder = new ViewHolder();
            viewHolder.programTxt = convertView.findViewById(R.id.listAdapterProgramTxt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//给控件赋值
        viewHolder.programTxt.setText(addString + mArray[position]);
        viewHolder.programTxt.setTextSize(20);
        if (listChoice == position) {
            viewHolder.programTxt.setTextColor(mContext.getResources().getColor(R.color.programActive));
            viewHolder.programTxt.setBackgroundColor(mContext.getResources().getColor(R.color.programInactive));
        } else {
            viewHolder.programTxt.setTextColor(mContext.getResources().getColor(R.color.programInactive));
            viewHolder.programTxt.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView programTxt;
    }
}
