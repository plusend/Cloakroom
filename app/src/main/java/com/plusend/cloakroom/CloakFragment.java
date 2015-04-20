package com.plusend.cloakroom;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class CloakFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AppAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AppInfo> mListAppInfo;
    private List<ApplicationInfo> mListPackageInfo;
    private Context context;

    private PackageManager pm;

    public CloakFragment(Context context) {
        super();
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        pm = this.getActivity().getPackageManager();
        mListAppInfo = new ArrayList<AppInfo>();
        mListPackageInfo = new ArrayList<ApplicationInfo>();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mListPackageInfo = getCloakApk();

        for(ApplicationInfo packageInfo : mListPackageInfo){
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                AppInfo appInfo = new AppInfo();
                appInfo.setPkgName(packageInfo.packageName);
                Log.d("Aaron", packageInfo.packageName + "");
                appInfo.setAppIcon(packageInfo.loadIcon(pm));
                appInfo.setAppLabel(packageInfo.loadLabel(pm).toString());
                String path = packageInfo.sourceDir;
                appInfo.setPath(path);
                try {
                    appInfo.setSize(formateFileSize(new FileInputStream(new File(path)).available()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mListAppInfo.add(appInfo);}
        }

        Collections.sort(mListAppInfo, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo aa, AppInfo ab) {
                CharSequence  sa = aa.getAppLabel();
                //if (sa == null) sa = aa.name;
                CharSequence  sb = ab.getAppLabel();
                //if (sb == null) sb = ab.name;
                return sCollator.compare(sa.toString(), sb.toString());
            }
            private final Collator   sCollator = Collator.getInstance();
        });

        mAdapter = new AppAdapter(mListAppInfo,"领取");
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener(){

            @Override
            public void onItemClickListener(View view, int position) {

                installAPK(mListAppInfo.get(position).getPath());
            }
        });

        return rootView;
    }

    //系统函数，字符串转换 long -String (kb)
    private String formateFileSize(long size){
        return Formatter.formatFileSize(getActivity(), size);
    }

    //获取备份的App信息
    private List<ApplicationInfo> getCloakApk(){
        File CloakRoom = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File[] ApkList = CloakRoom.listFiles();
        if(ApkList != null){
            for(File file : ApkList){
                ApplicationInfo info = getApkIcon(context, file.getAbsolutePath());
                mListPackageInfo.add(info);
            }
        }
        return mListPackageInfo;
    }

    public static ApplicationInfo getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo;
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }
    private void installAPK(String apkPath)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}

