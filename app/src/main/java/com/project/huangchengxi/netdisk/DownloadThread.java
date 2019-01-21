package com.project.huangchengxi.netdisk;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
public class DownloadThread implements Runnable,Transfer {
    //从客户端下载文件
    private File file;
    private String IPAddr;
    private int PORT = 8856;
    private String path;
    private Socket socket;
    private boolean done = false;
    private long fileSize;
    private long downloadedDize;
    private int percent;
    private boolean success=false;
    private int failTimes=0;
    private String fileName;

    public DownloadThread(String filePath, String FileName, String IPAddr, String size) {
        //要传输到的文件夹绝对路径
        downloadedDize = 0;
        this.fileSize = Long.parseLong(size);
        this.path = filePath;
        this.IPAddr = IPAddr;
        this.fileName=FileName;
        file = new File(path + FileName);
        if (file.exists()) {
            for (int i = 0; ; i++) {
                file = new File(path + "(" + i + ")" + FileName);
                if (!file.exists()) {
                    break;
                }
            }
        }
        System.out.println(path + FileName);

    }

    @Override
    public void run() {
        // TODO 自动生成的方法存根
        while (!done && !Thread.currentThread().isInterrupted() && failTimes<=10) {
            try {
                Thread.sleep(1000);
                socket = new Socket(IPAddr, PORT);
                done = true;
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                failTimes++;
            }
        }
        try {
            if (failTimes<=10) {
                //生成新文件
                file.createNewFile();
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                //连接到发送端
                //接收发送端文件
                InputStream is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is);

                //存储字节到缓冲区
                byte[] buffer = new byte[2048];
                int num = dis.read(buffer);
                downloadedDize += num;
                percent = (int) ((100 * downloadedDize) / fileSize);
                while (num != -1) {
                    raf.write(buffer, 0, num);
                    raf.skipBytes(num);
                    num = dis.read(buffer);
                    downloadedDize += num;
                    System.out.println(percent);
                    percent = (int) ((100 * downloadedDize) / fileSize);
                }
                this.success = true;
                dis.close();
                raf.close();
                socket.close();
            }
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

    }

    @Override
    public long getTotal() {
        return this.fileSize;
    }

    @Override
    public long getTransfered() {
        return downloadedDize;
    }

    @Override
    public int percent() {
        return this.percent;
    }

    @Override
    public boolean getState() {
        return this.success;
    }

    @Override
    public String getName() {
        return this.fileName;
    }
}
