package com.example.administrator.serversocketapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private boolean isRuning = true;

    public static final int SERVER_PORT = 8888;

    public void onclickMainStart(View view) {

        try {
            System.out.println("自己的ip地址："+getLocalHostIp(this));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "服务器开始===", Toast.LENGTH_SHORT).show();
        isRuning = true;
        new Thread() {
            @Override
            public void run() {
                while (isRuning) {
                    try {
                        ServerSocket server = new ServerSocket(SERVER_PORT);
                        while (true) {
                            System.out.println("accept=======");
                            Socket s = server.accept();
                            OutputStream os = s.getOutputStream();
                            os.write("您好，您收到了服务器的新年祝福！\n".getBytes("utf-8"));
                            os.flush();
                            os.close();
                            s.close();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void onclickMainStop(View view) {
        isRuning = false;
        Toast.makeText(this, "服务器停止受理", Toast.LENGTH_SHORT).show();
    }


    public String getLocalHostIp(Context context) throws SocketException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return ""+wifiInfo.getIpAddress();
    }
}
