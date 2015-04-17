package com.plusend.cloakroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppInfo> appInfoList;

    /**
     * Item的回调接口
     *
     */
    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    private OnItemClickListener listener; // 点击Item的回调对象

    /**
     * 设置回调监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AppAdapter(List<AppInfo> list){
        appInfoList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view, viewGroup, false);
        v.setTag(appInfoList.get(i).getPkgName());
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.mImageView.setImageDrawable(appInfoList.get(position).getAppIcon());
        viewHolder.mTextView.setText(appInfoList.get(position).getAppLabel());
        viewHolder.size.setText(appInfoList.get(position).getSize() == null ? "": appInfoList.get(position).getSize());

        if (listener != null) {
            viewHolder.size.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public TextView size;
        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView)v.findViewById(R.id.pic);
            mTextView = (TextView)v.findViewById(R.id.name);
            size = (TextView)v.findViewById(R.id.size);
        }
    }
}
