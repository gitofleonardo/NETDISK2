package com.project.huangchengxi.netdisk;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;

public final class ToolKits {
    private ToolKits(){}
    private static String IP="192.168.0.100";
    //获取文件大小单位
    public static String getUnit(long arg){
        int count=0;
        while (arg>=1024 && count<=4){
            arg/=1024;
            count++;
        }
        switch(count){
            case 0:
                return arg+"B";
            case 1:
                return arg+"KB";
            case 2:
                return arg+"MB";
            case 3:
                return arg+"GB";
            case 4:
                return arg+"TB";
            default:
                return arg+"TB";
        }
    }
    //获取权限
    public static void getPermissions(String[] Permissions,MainActivity mainActivity){
        if (!checkPermissions(Permissions,mainActivity)){
            ActivityCompat.requestPermissions(mainActivity,Permissions,1);
        }
    }
    public static boolean checkPermissions(String[] Permissions,MainActivity mainActivity){
        for (String p:Permissions){
            if (ActivityCompat.checkSelfPermission(mainActivity,p)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    //检查sd卡是否可用
    public static boolean checkSDAvailable(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sd=new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        }else{
            return false;
        }
    }
    public static String getDirectory(){
        String appDirectory=Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"NetDisk";
        File file=new File(appDirectory);
        if (!file.exists()){
            file.mkdir();
            return appDirectory;
        }else{
            return appDirectory;
        }
    }
    public static String getIP(){
        return IP;
    }

}
