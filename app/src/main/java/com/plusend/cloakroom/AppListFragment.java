package com.plusend.cloakroom;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yulore on 2015/4/14.
 */
public class AppListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AppAdapter mAdapter;
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
                appInfo.setPkgName(packageInfo.packageName);
                Log.d("Aaron", packageInfo.packageName + "");
            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            appInfo.setAppLabel(packageInfo.applicationInfo.loadLabel(pm).toString());
            String path = packageInfo.applicationInfo.sourceDir;
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

        mAdapter = new AppAdapter(mListAppInfo);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AppAdapter.OnItemClickListener(){

            @Override
            public void onItemClickListener(View view, int position) {

              //  Toast.makeText(,"Hello", Toast.LENGTH_SHORT).show();
                Log.d("Aaron", "position" + position);
                Uri uri = Uri.parse("package:"+ mListAppInfo.get(position).getPkgName());
                Log.d("Aaron", "Uri: " + uri.toString());
                Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //系统函数，字符串转换 long -String (kb)
    private String formateFileSize(long size){
        return Formatter.formatFileSize(getActivity(), size);
    }
}

