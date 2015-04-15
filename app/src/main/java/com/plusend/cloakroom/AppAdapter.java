package com.plusend.cloakroom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private List<AppInfo> appInfoList;

    public AppAdapter(List<AppInfo> list){
        appInfoList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(appInfoList.get(position).getAppLabel());
    }

    @Override
    public int getItemCount() {
        return appInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView)v.findViewById(R.id.name);
        }
    }
}
