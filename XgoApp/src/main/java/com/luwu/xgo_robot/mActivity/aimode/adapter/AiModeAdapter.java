package com.luwu.xgo_robot.mActivity.aimode.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.data.AiModeBean;

import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/12<p>
 */
public class AiModeAdapter extends RecyclerView.Adapter<AiModeAdapter.ViewHolder> {
    private List<AiModeBean> mList;

    public AiModeAdapter(List<AiModeBean> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_aimode, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mImage.setImageResource(mList.get(i).getImage());
        viewHolder.mTv.setText(mList.get(i).getName());
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.item_aimode_img);
            mTv = itemView.findViewById(R.id.item_aimode_tv);
        }
    }
}
