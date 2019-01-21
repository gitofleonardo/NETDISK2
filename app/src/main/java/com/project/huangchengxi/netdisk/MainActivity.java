package com.project.huangchengxi.netdisk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Socket socket=(Socket)ObjectClass.Object.get(0);
    private RecyclerView fileListView;
    private ArrayList<FileItem> fileItemArrayList;
    private InputStream is;
    private OutputStream os;
    private User user;
    private Stack<ListAdapter> listAdapterStack;
    private PrintStream ps;
    private String IPAddr;
    private String currentDir;
    private TextView IDTextView;
    private TextView SizeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //my code
        Intent intent=getIntent();
        user=new User(intent.getStringExtra("ID"),intent.getStringExtra("password"));
        currentDir=user.getUserID()+"\\";
        fileListView=(RecyclerView)findViewById(R.id.fileList);
        fileListView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        listAdapterStack=new Stack<>();
        IDTextView=(TextView) navigationView.getHeaderView(0).findViewById(R.id.id);
        SizeTextView=(TextView)navigationView.getHeaderView(0).findViewById(R.id.size);
        IDTextView.setText(user.getUserID());

        Thread procThread=new Thread(new UserProcThread());
        procThread.start();
    }

    //用于处理客户端和服务器之间消息传送的主线程
    private class UserProcThread implements Runnable{
        //缓冲区数据
        private byte[] buffer;
        //转换为字符串消息
        private String msg;
        private Message message;
        @Override
        public void run() {
            // TODO 自动生成的方法存根
            try {
                IPAddr=Inet4Address.getLocalHost().getHostAddress();
                //尝试获取流
                os=socket.getOutputStream();
                is=socket.getInputStream();
                ps=new PrintStream(os,false,"gbk");

                //获取用户信息
                getUserInfo();
                getDirs(user.getUserID());
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted() && socket!=null){
                try {
                    //to do
                    buffer=new byte[1024];
                    //阻塞等待服务器信息
                    is.read(buffer);
                    msg=new String(buffer,"gbk");
                    System.out.println(msg);
                    //处理数据
                    switch (msg.substring(0,20)) {
                        case CommandClass._COMMAND_USERINFO:
                            String[] infos;
                            infos=msg.split("&");
                            user.setTotalSize(Long.parseLong(infos[1]));
                            user.setSizeUsed(Long.parseLong(infos[2]));
                            Message message=MyHandler.obtainMessage();
                            message.what=2;
                            message.obj=ToolKits.getUnit(user.getSizeUsed())+"/"+ToolKits.getUnit(user.getTotalSize());
                            MyHandler.sendMessage(message);
                            break;
                        case CommandClass._COMMAND_GETDIRS:
                            //服务器给客户端发送一个文件夹字符串对象
                            //等待客户端接收
                            //载入当前目录
                            message=MyHandler.obtainMessage();
                            message.what=1;
                            message.obj=msg;
                            MyHandler.sendMessage(message);
                            break;
                        case CommandClass._COMMAND_DOWN_START:
                            String[] msgs=msg.split("&");
                            break;

                        default:
                            break;
                    }
                    System.out.println(msg);
                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    if (e instanceof TimeoutException){
                        message=MyHandler.obtainMessage();
                        message.what=3;
                        MyHandler.sendMessage(message);
                    }
                    break;
                }
            }
        }

    }
    //封装起来的功能
    //获取用户网盘信息
    //返回一个长整形数组
    private void getUserInfo(){
        //发送用户信息
        //并接收服务器发送回来的反馈信息
        //包括网盘总容量和已经使用的容量
        ps.println(CommandClass._COMMAND_LOGIN+"&"+user.getUserID()+"&"+user.getPasswd()+"&");
        ps.flush();
    }

    //新建文件夹操作
    private void newDir(String dirName){
        ps.println(CommandClass._COMMAND_NEWDIR+"&"+currentDir+dirName+"&");
        ps.flush();
    }
    //将文件列表载入JList中
    //接收参数：字符串数组是
    @SuppressWarnings("unchecked")
    private void loadFileList(String files){
        String[] singleDoc=files.split("&");
        String[] fileType;
        fileItemArrayList=new ArrayList<>();
        ListAdapter listAdapter=new ListAdapter(fileItemArrayList);
        fileListView.setAdapter(listAdapter);
        FileItem fileItem;
        for (int i=1;i<singleDoc.length-1;i++){
            fileType=singleDoc[i].split("\\+");
            if (fileType[0].equals("isDir")){
                fileItem=new FileItem(fileType[1],FileItem.TYPEDIRECTORY,-1);
            }else{
                fileItem=new FileItem(fileType[1],FileItem.TYPEFILE,Long.parseLong(fileType[2]));
            }
            fileItemArrayList.add(fileItem);
        }
        listAdapter.setOnClickListener(new ListAdapter.OnClickListener() {
            @Override
            public void onItemClick(View v, ArrayList<FileItem> fileItemArrayList, int position,ListAdapter listAdapter) {
                MainActivity.this.fileItemArrayList=fileItemArrayList;
                FileItem item=fileItemArrayList.get(position);
                if (item.getType()==FileItem.TYPEDIRECTORY){
                    getDirs(currentDir+item.getName());
                    listAdapterStack.push(listAdapter);
                }else{
                    //是一个文件
                    FileDetailDialog fileDetailDialog=new FileDetailDialog(MainActivity.this,item,ps,currentDir);
                    fileDetailDialog.show();
                }
            }
        });
        listAdapter.setOnLongClickListener(new ListAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(View v, ArrayList<FileItem> fileItemArrayList, int position, ListAdapter listAdapter) {

            }
        });
        Collections.sort(fileItemArrayList);
        listAdapter.notifyDataSetChanged();
    }
    //向服务器请求获取文件夹
    private void getDirs(final String currentDirectory){
        new Thread(new Runnable() {
            String currentDir=currentDirectory;
            @Override
            public void run() {
                ps.println(CommandClass._COMMAND_GETDIRS+"&"+currentDir+"&");
                ps.flush();
            }
        }).start();
    }
    private void delDir(String dirName){
        ps.println(CommandClass._CONNAMD_DELDIR+"&"+currentDir+dirName+"&");
    }
    private void rename(String OldDirName,String NewDirName){
        ps.println(CommandClass._COMMAND_RENAME+"&"+currentDir+OldDirName+"&"+currentDir+NewDirName+"&");
        ps.flush();
    }
    private void getNewInfo(){
        ps.println(CommandClass._COMMAND_USERINFO);
        ps.flush();
    }
    private void upload(String path,String filename){
        //file为本地文件绝对路径
        ps.println(CommandClass._COMMAND_UPFILE+"&"+currentDir+"&"+filename+"&"+IPAddr+"&");
        ps.flush();
        Thread thread=new Thread(new TransThread(path+filename));
        thread.start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!listAdapterStack.empty()){
            ListAdapter listAdapter=listAdapterStack.peek();
            listAdapterStack.pop();
            fileListView.setAdapter(listAdapter);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //todo
            getDirs(currentDir);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //todo
        switch (id){
            case R.id.download:
                Intent intent=new Intent(MainActivity.this,DownloadActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    Handler MyHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1:
                    loadFileList((String)msg.obj);
                    break;
                case 2:
                    SizeTextView.setText((String)msg.obj);
                    break;
                case 3:
                    //连接超时
                    Toast.makeText(MainActivity.this, "连接超时，与服务器断开连接...请重新登陆", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
