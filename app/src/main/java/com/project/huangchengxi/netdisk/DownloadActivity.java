package com.project.huangchengxi.netdisk;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {
    private RecyclerView transferRecyclerView;
    private ArrayList<Transfer> transferArrayList;
    private DownloadAdapter downloadAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        transferRecyclerView=findViewById(R.id.download_recycler_view);
        transferArrayList=TransferThreadPool.arrayList;
        downloadAdapter=new DownloadAdapter(transferArrayList);
        transferRecyclerView.setLayoutManager(new LinearLayoutManager(DownloadActivity.this));
        transferRecyclerView.setAdapter(downloadAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Message message=myHandler.obtainMessage();
                    message.what=1;
                    myHandler.sendMessage(message);

                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    downloadAdapter.notifyDataSetChanged();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
