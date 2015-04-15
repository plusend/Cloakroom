package com.plusend.cloakroom;

import android.content.pm.ApplicationInfo;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class AppListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppInfo> mListAppInfo;
//    PackageManager pm = this.getActivity().getPackageManager(); // 获得PackageManager对象

    public static final int FILTER_ALL_APP = 0; // 所有应用程序
    public static final int FILTER_SYSTEM_APP = 1; // 系统程序
    public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
    public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序
    private int filter = FILTER_THIRD_APP;

    public AppListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mListAppInfo = new ArrayList<AppInfo>();
        queryAppInfo();

        mAdapter = new AppAdapter(mListAppInfo);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    private void queryAppInfo() {
        PackageManager pm = this.getActivity().getPackageManager(); // 获得PackageManager对象

        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        mListAppInfo = new ArrayList<AppInfo>(); // 保存过滤查到的AppInfo
        // 根据条件来过滤
        switch (filter) {
            case FILTER_ALL_APP: // 所有应用程序
                mListAppInfo.clear();
                for (ApplicationInfo app : listAppcations) {
                    mListAppInfo.add(getAppInfo(app));
                }
             break;
            case FILTER_SYSTEM_APP: // 系统程序
                mListAppInfo.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        mListAppInfo.add(getAppInfo(app));
                    }
                }
                break;
            case FILTER_THIRD_APP: // 第三方应用程序
                mListAppInfo.clear();
                for (ApplicationInfo app : listAppcations) {
                    //非系统程序
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        mListAppInfo.add(getAppInfo(app));
                    }
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                        mListAppInfo.add(getAppInfo(app));
                    }
                }
                break;
            case FILTER_SDCARD_APP: // 安装在SDCard的应用程序
                mListAppInfo.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        mListAppInfo.add(getAppInfo(app));
                    }
                }
                break;
            default:
                break;
        }

    }
    // 构造一个AppInfo对象 ，并赋值
    private AppInfo getAppInfo(ApplicationInfo app) {
        PackageManager pm = this.getActivity().getPackageManager(); // 获得PackageManager对象
        AppInfo appInfo = new AppInfo();
        appInfo.setAppLabel((String) app.loadLabel(pm));
        appInfo.setAppIcon(app.loadIcon(pm));
        appInfo.setPkgName(app.packageName);
        return appInfo;
    }
}

