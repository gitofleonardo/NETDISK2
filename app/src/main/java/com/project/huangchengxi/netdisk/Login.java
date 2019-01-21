package com.project.huangchengxi.netdisk;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Login extends AppCompatActivity {
    private EditText userId;
    private EditText password;
    private Button loginButton;
    private int PORT=8866;
    private Socket socket;
    private Handler MyHandler;
    private InputStream is;
    private AlertDialog.Builder alertDialog;
    private AlertDialog dialog;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();

        userId=findViewById(R.id.userID);
        password=findViewById(R.id.password);
        loginButton=findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserID=userId.getText().toString();
                String passwd=password.getText().toString();
                if (UserID.equals("") || password.getText().toString().equals("")) {
                    Toast.makeText(Login.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog=new AlertDialog.Builder(Login.this);
                    alertDialog.setView(new ProgressBar(Login.this));
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("连接服务器中...请稍后");
                    dialog=alertDialog.show();
                    Thread thread=new Thread(new Runnable() {
                        byte[] buffer=new byte[1024];

                        @Override
                        public void run() {
                            try{
                                socket=new Socket();
                                SocketAddress socketAddress=new InetSocketAddress(ToolKits.getIP(),PORT);
                                socket.connect(socketAddress,5000);
                                is=socket.getInputStream();
                                is.read(buffer);
                                if ((new String(buffer)).substring(0,20).equals(CommandClass._COMMAND_READY)){
                                    Message msg=MyHandler.obtainMessage();
                                    msg.what=1;
                                    MyHandler.sendMessage(msg);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                Message msg=MyHandler.obtainMessage();
                                msg.what=2;
                                MyHandler.sendMessage(msg);
                            }
                        }
                    });
                    thread.start();
                }
            }
        });

        MyHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case 1:
                        ObjectClass.Object.add(socket);
                        user=new User(userId.getText().toString(),password.getText().toString());
                        Intent intent=new Intent(Login.this,MainActivity.class);
                        intent.putExtra("ID",userId.getText().toString());
                        intent.putExtra("password",password.getText().toString());
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Toast.makeText(Login.this,"连接超时...",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
