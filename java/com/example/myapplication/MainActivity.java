package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 7778;
    Socket socket;
    TextView nickText;
    Button btn;
    String nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nickText = findViewById(R.id.text1);
        btn = findViewById(R.id.button);
    }
    public void onClick1(View v) {
        nickname = nickText.getText().toString().trim();
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra("message", nickname);
        startService(intent);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        processCommand(intent);
        super.onNewIntent(intent);
    }

    private void processCommand(Intent intent) {
        if (intent != null) {
            String msg = intent.getStringExtra("message");
            String tokens[] = msg.split(":");
            if (tokens[0].equals("connect")) {
                if (tokens[1].equals("able")) {
                    Intent secIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                    startActivity(secIntent);
                    finish();
                }
                else if (tokens[0].equals("unable")){
                    showMessage("이미 접속중인 닉네임입니다.");
                    Intent newIntent = new Intent(getApplicationContext(), SocketService.class);
                    stopService(newIntent);
                }
                else {
                    showMessage("서버와의 연결이 끊어졌습니다.");
                    Intent newIntent = new Intent(getApplicationContext(), SocketService.class);
                    stopService(newIntent);
                    finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                }
            }
        }
    }

    public void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}