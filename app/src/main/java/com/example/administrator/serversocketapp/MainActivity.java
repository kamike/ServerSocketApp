package com.example.administrator.serversocketapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.main_imageView);
    }

    private boolean isRuning = true;

    public static final int SERVER_PORT = 8888;

    public void onclickMainStart(View view) {

        System.out.println("自己的ip地址：" + getWIfiLocalIp(this));

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
                            Socket socket = server.accept();
//                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//                            String line = br.readLine();
//                            System.out.println("来自客户端的数据：" + line);

                            InputStream in = socket.getInputStream();
                            DataInputStream dataInput = new DataInputStream(in);
                            int size = dataInput.readInt();
                            System.out.println("读取到的长度：" + size);
                            byte[] data = new byte[size];
                            dataInput.readFully(data);

                            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Message msg = new Message();
                            msg.obj = bmp;
                            handler.sendMessage(msg);
                            in.close();
                            dataInput.close();
                            socket.close();
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj == null) {
                imageView.setImageResource(R.mipmap.ic_launcher);
                return;
            }

            imageView.setImageBitmap((Bitmap) msg.obj);
        }
    };


    public void onclickMainStop(View view) {
        isRuning = false;
        Toast.makeText(this, "服务器停止受理", Toast.LENGTH_SHORT).show();
    }


    public static String getWIfiLocalIp(final Context context) {
        String ip = null;
        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
        android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        // wifi
        android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();

        // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
        if (mobile == android.net.NetworkInfo.State.CONNECTED
                || mobile == android.net.NetworkInfo.State.CONNECTING) {
            ip = getLocalIpAddress();
        }
        if (wifi == android.net.NetworkInfo.State.CONNECTED
                || wifi == android.net.NetworkInfo.State.CONNECTING) {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    (ipAddress >> 24 & 0xFF);
        }
        return ip;

    }

    private static String getLocalIpAddress() {
        try {
            //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {//获取IPv4的IP地址
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }


        return null;
    }

}
