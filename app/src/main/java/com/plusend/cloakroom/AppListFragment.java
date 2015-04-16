package com.plusend.cloakroom;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class AppListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppInfo> mListAppInfo;
    private List<PackageInfo> mListPackageInfo;

    private PackageManager pm;

    public AppListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        pm = this.getActivity().getPackageManager();
        mListAppInfo = new ArrayList<AppInfo>();
        mListPackageInfo = new ArrayList<PackageInfo>();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mListPackageInfo = pm.getInstalledPackages(0);

        for(PackageInfo packageInfo : mListPackageInfo){
            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
            AppInfo appInfo = new AppInfo();
            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            appInfo.setAppLabel(packageInfo.applicationInfo.loadLabel(pm).toString());
            String path = packageInfo.applicationInfo.sourceDir;
            try {
                appInfo.setSize(formateFileSize(new FileInputStream(new File(path)).available()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mListAppInfo.add(appInfo);}
        }

        mAdapter = new AppAdapter(mListAppInfo);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    //系统函数，字符串转换 long -String (kb)
    private String formateFileSize(long size){
        return Formatter.formatFileSize(getActivity(), size);
    }
}

