package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketService extends Service {
    public static final String SERVER_IP = "125.185.193.29";
    public static final int SERVER_PORT = 7778;
    Socket socket;
    boolean usable;
    int curAct = 0;
    class SocketThread extends Thread {
        String nickname;
        public SocketThread(String nickname) {
            this.nickname = nickname;
            usable = false;
        }
        @Override
        public void run() {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                usable = true;
                try {
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                    writer.println(nickname);
                    writer.flush();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    while (usable) {
                        String buf = br.readLine();
                        if (buf == null)
                            break;
                        else if (buf.split(":")[0].equals("chact")) {
                            curAct = Integer.parseInt(buf.split(":")[1]);
                            Thread.sleep(200);
                            continue;
                        }
                        Intent intent;
                        if (curAct == 1)
                            intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        else if (curAct == 2)
                            intent  = new Intent(getApplicationContext(), RoomActivity.class);
                        else
                            intent  = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("message", buf);
                        startActivity(intent);
                        if (buf.equals("connect:unable"))
                            break;
                    }
                    usable = false;
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    usable = false;
                }
            } catch (IOException g) {
                usable = false;
            }
        }
    }
    public SocketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null)
            return Service.START_STICKY;
        else
            processCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent) {
        String msg = intent.getStringExtra("message");
        if (!usable) {
            new SocketThread(msg).start();
        }
        else {
            new Thread(() -> {
                try {
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                    writer.println(msg);
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}