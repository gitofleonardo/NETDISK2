package com.project.huangchengxi.netdisk;

public interface Transfer {
    int percent();
    long getTotal();
    long getTransfered();
    boolean getState();
    String getName();
}
