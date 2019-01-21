package com.project.huangchengxi.netdisk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintStream;

public class FileDetailDialog{
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private LayoutInflater layoutInflater;
    private View view;
    private TextView sizeTextView;
    private String[] storagePermissions ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PrintStream ps;
    private String currentDiectory;

    public FileDetailDialog(final MainActivity mainActivity, final FileItem fileItem,final PrintStream ps, final String currentDirectory){
        this.ps=ps;
        this.currentDiectory=currentDirectory;
        builder=new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);
        builder.setTitle(fileItem.getName());
        layoutInflater=mainActivity.getLayoutInflater();
        view=layoutInflater.inflate(R.layout.file_dialog_layout,(ViewGroup)mainActivity.findViewById(R.id.file_detail_dialog));
        sizeTextView=view.findViewById(R.id.file_size);
        sizeTextView.setText(ToolKits.getUnit(fileItem.getSize()));
        builder.setView(view);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
                alertDialog.dismiss();
            }
        });
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //begin downloading
                if (ToolKits.checkPermissions(storagePermissions,mainActivity)){
                    Toast.makeText(mainActivity,"添加到下载列表中...正在下载",Toast.LENGTH_SHORT).show();
                    download(ps,currentDirectory,fileItem.getName());
                    DownloadThread downloadThread=new DownloadThread(ToolKits.getDirectory()+"/",fileItem.getName(),ToolKits.getIP(),new Long(fileItem.getSize()).toString());
                    TransferThreadPool.arrayList.add(downloadThread);
                    Thread thread=new Thread(downloadThread);
                    thread.start();
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(mainActivity, "请授予存储空间权限", Toast.LENGTH_SHORT).show();
                    ToolKits.getPermissions(storagePermissions,mainActivity);

                    if (ToolKits.checkPermissions(storagePermissions,mainActivity)){
                        Toast.makeText(mainActivity,"添加到下载列表中...正在下载",Toast.LENGTH_SHORT).show();
                        download(ps,currentDirectory,fileItem.getName());
                        DownloadThread downloadThread=new DownloadThread(ToolKits.getDirectory()+"/",fileItem.getName(),ToolKits.getIP(),new Long(fileItem.getSize()).toString());
                        TransferThreadPool.arrayList.add(downloadThread);
                        Thread thread=new Thread(downloadThread);
                        thread.start();
                        alertDialog.dismiss();
                    }else{
                        Toast.makeText(mainActivity, "下载失败...请检查权限", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }
    public void show(){
        alertDialog=builder.show();
    }
    public void dismiss(){
        alertDialog.dismiss();
    }

    private void download(final PrintStream ps, final String currentDirectory, final String fileName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ps.println(CommandClass._COMMAND_DOWNFILE+"&"+currentDirectory+fileName+"&");
                    ps.flush();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
