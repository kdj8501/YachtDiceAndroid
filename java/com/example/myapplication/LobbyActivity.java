package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {
    private static final String SPLITER = "#!#";
    private long backpressedTime = 0;
    TextView chat;
    TextView send;
    RecyclerAdapter adapter;
    ArrayList<String> rooms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        chat = findViewById(R.id.textView);
        send = findViewById(R.id.chat);
        chat.setMovementMethod(new ScrollingMovementMethod());
        rooms = new ArrayList<String>();
        rooms.add("create");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(rooms);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener () {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                if (position == 0) {
                    String str = adapter.getCreateRoomName().trim();
                    if (str.isEmpty())
                        showMessage("생성할 방 이름을 입력해주세요.");
                    else {
                        intent.putExtra("message", "create:" + str);
                        startService(intent);
                    }
                }
                else {
                    intent.putExtra("message", "tojoin:" + (position - 1));
                    startService(intent);
                }
            }
        });
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra("message", "request:rooms:");
        startService(intent);
    }

    public void sendBtn(View v) {
        if (send.getText().toString().trim().isEmpty())
            showMessage("내용을 입력해주세요.");
        else {
            Intent intent = new Intent(getApplicationContext(), SocketService.class);
            intent.putExtra("message", "message:" + send.getText().toString());
            startService(intent);
            send.setText("");
        }
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
            if (tokens[0].equals("message")) {
                chat.setText(chat.getText().toString() + "\n" + msg.substring(msg.indexOf(":") + 1));
                scrollBottom(chat);
            }
            else if (tokens[0].equals("join")) {
                if (tokens[1].equals("error"))
                    showMessage("에러가 발생했습니다.");
                else if (tokens[1].equals("full"))
                    showMessage("방이 꽉 찼습니다.");
                else if (tokens[1].equals("started"))
                    showMessage("이미 시작한 방입니다.");
                else {
                    Intent secIntent = new Intent(getApplicationContext(), RoomActivity.class);
                    startActivity(secIntent);
                    finish();
                }
            }
            else if (tokens[0].equals("rooms")) {
                rooms.clear();
                rooms.add("create");
                String tmp[] = msg.substring(msg.indexOf(":") + 1).split(SPLITER);
                if (!tmp[0].isEmpty())
                    for (int i = 0; i < tmp.length; i++)
                        rooms.add(tmp[i]);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void scrollBottom(TextView textView) {
        int lineTop =  textView.getLayout().getLineTop(textView.getLineCount()) ;
        int scrollY = lineTop - textView.getHeight();
        textView.scrollTo(0, Math.max(scrollY, 0));
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            showMessage("\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.");
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            Intent intent = new Intent(getApplicationContext(), SocketService.class);
            intent.putExtra("message", "quit");
            startService(intent);
            stopService(intent);
            moveTaskToBack(true);
            finishAndRemoveTask();
            System.exit(0);
        }
    }

    public void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
