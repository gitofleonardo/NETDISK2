package com.project.huangchengxi.netdisk;


import android.support.annotation.NonNull;


public class FileItem implements Comparable<FileItem> {
    private int type;
    public static int TYPEFILE=0;
    public static int TYPEDIRECTORY=1;
    private String name;
    private long size;

    public long getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
    public FileItem(String name,int type,long size){
        this.name=name;
        this.type=type;
        this.size=size;
    }
    public FileItem(String name,int type){
        this.name=name;
        this.type=type;
    }

    @Override
    public int compareTo(@NonNull FileItem o) {
        if (type>o.getType()){
            return -1;
        }else if (type<o.getType()){
            return 1;
        }
        return 0;
    }
}
