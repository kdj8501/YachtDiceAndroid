package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    private static final String SPLITER = "#!#";
    private long backpressedTime = 0;
    TextView chat;
    TextView send;
    TextView turn;
    TextView rollCount;
    ScoreAdapter adapter;
    ArrayList<score> users;
    Button gameBtn;
    ImageView[] dices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        dices = new ImageView[10];
        dices[0] = findViewById(R.id.imageView25);
        dices[1] = findViewById(R.id.imageView26);
        dices[2] = findViewById(R.id.imageView27);
        dices[3] = findViewById(R.id.imageView28);
        dices[4] = findViewById(R.id.imageView29);
        dices[5] = findViewById(R.id.imageView30);
        dices[6] = findViewById(R.id.imageView31);
        dices[7] = findViewById(R.id.imageView32);
        dices[8] = findViewById(R.id.imageView33);
        dices[9] = findViewById(R.id.imageView34);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            dices[i].setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SocketService.class);
                    intent.putExtra("message", "fixbtn:" + Integer.toString(finalI));
                    startService(intent);
                }
            });
        }
        chat = findViewById(R.id.textView);
        send = findViewById(R.id.chat);
        turn = findViewById(R.id.textView2);
        rollCount = findViewById(R.id.textView3);
        chat.setMovementMethod(new ScrollingMovementMethod());
        users = new ArrayList<score>();
        users.add(new score("categories"));
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new ScoreAdapter(users);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ScoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position, int val) {
                if (val > 5)
                    val += 2;
                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.putExtra("message", "btn:" + Integer.toString(position - 1) + SPLITER + Integer.toString(val));
                startService(intent);
            }
        });
        gameBtn = findViewById(R.id.button5);
        gameBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                if (gameBtn.getText().toString().equals("Start"))
                    intent.putExtra("message", "command:start");
                else
                    intent.putExtra("message", "command:stop");
                startService(intent);
            }
        });
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

    public void rollDice(View v) {
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        intent.putExtra("message", "btn:roll");
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
            if (tokens[0].equals("message")) {
                chat.setText(chat.getText().toString() + "\n" + msg.substring(msg.indexOf(":") + 1));
                scrollBottom(chat);
            }
            else if (tokens[0].equals("users")) {
                users.clear();
                users.add(new score("categories"));
                String tmp[] = msg.substring(msg.indexOf(":") + 1).split(SPLITER);
                for (int i = 0; i < tmp.length; i++)
                    users.add(new score(tmp[i]));
                adapter.notifyDataSetChanged();
            }
            else if (tokens[0].equals("game")) {
                if (tokens[1].equals("start")) {
                    gameBtn.setText("Stop");
                    turn.setText(users.get(1).getName() + "'s Turn");
                    rollCount.setText("RollCount: 0");
                }
                else if (tokens[1].equals("reset")) {
                    gameBtn.setText("Start");
                    turn.setText("Ready");
                    rollCount.setText("RollCount");
                    for (int i = 0; i < 10; i++)
                        dices[i].setImageResource(R.drawable.dice0);
                }
                else {
                    String tmp[] = tokens[1].split(SPLITER);
                    for (int i = 0; i < 10; i++) {
                        int val = Integer.parseInt(tmp[i]);
                        if (val == 0)
                            dices[i].setImageResource(R.drawable.dice0);
                        else if (val == 1)
                            dices[i].setImageResource(R.drawable.dice1);
                        else if (val == 2)
                            dices[i].setImageResource(R.drawable.dice2);
                        else if (val == 3)
                            dices[i].setImageResource(R.drawable.dice3);
                        else if (val == 4)
                            dices[i].setImageResource(R.drawable.dice4);
                        else if (val == 5)
                            dices[i].setImageResource(R.drawable.dice5);
                        else if (val == 6)
                            dices[i].setImageResource(R.drawable.dice6);
                    }
                    for (int i = 10; i < (users.size() - 1) * 15 + 10; i++)
                        users.get((i - 10) / 15 + 1).setScore((i - 10) % 15, Integer.parseInt(tmp[i]));
                    adapter.notifyDataSetChanged();
                    rollCount.setText("RollCount: " + tmp[(users.size() - 1) * 15 + 10]);
                    turn.setText(users.get(Integer.parseInt(tmp[(users.size() - 1) * 15 + 11]) + 1).getName() + "'s Turn");
                }
            }
            else if (tokens[0].equals("error")) {
                if (tokens[1].equals("auth"))
                    showMessage("당신은 방장(1픽)이 아닙니다.");
                else if (tokens[1].equals("start"))
                    showMessage("이미 시작한 게임입니다.");
                else if (tokens[1].equals("notstart"))
                    showMessage("아직 게임이 시작하지 않았습니다.");
                else if (tokens[1].equals("notyet"))
                    showMessage("두 명 이상부터 게임을 시작할 수 있습니다.");
                else if (tokens[1].equals("turn"))
                    showMessage("당신의 턴이 아닙니다.");
                else if (tokens[1].equals("cannot"))
                    showMessage("현재 사용할 수 없는 버튼입니다.");
                else if (tokens[1].equals("fullroll"))
                    showMessage("더 이상 주사위를 굴릴 수 없습니다.");
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
            Intent secIntent = new Intent(getApplicationContext(), LobbyActivity.class);
            startActivity(secIntent);
            Intent intent = new Intent(getApplicationContext(), SocketService.class);
            intent.putExtra("message", "quit");
            startService(intent);
        }
    }

    public void showMessage(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
