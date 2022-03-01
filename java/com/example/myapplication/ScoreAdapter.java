package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class ScoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface OnItemClickListener{
        void onItemClick(View v, int position, int val);
    }
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private ArrayList<score> dataList = null;
    ScoreAdapter(ArrayList<score> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == 1) {
            view = inflater.inflate(R.layout.score_text, parent, false);
            return new categories(view);
        }
        else {
            view = inflater.inflate(R.layout.score_user, parent, false);
            return new user(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof user) {
            ((user) viewHolder).name.setText(dataList.get(position).getName());
            for (int i = 0; i < 15; i++) {
                if (i < 6) {
                    if (dataList.get(position).getScore(i) == -1)
                        ((user) viewHolder).buttons[i].setText("-");
                    else
                        ((user) viewHolder).buttons[i].setText(Integer.toString(dataList.get(position).getScore(i)));
                } else if (i == 6)
                    ((user) viewHolder).texts[0].setText(Integer.toString(dataList.get(position).getSubTotal()));
                else if (i == 7)
                    ((user) viewHolder).texts[1].setText(Integer.toString(dataList.get(position).getBonus()));
                else if (i < 14) {
                    if (dataList.get(position).getScore(i) == -1)
                        ((user) viewHolder).buttons[i - 2].setText("-");
                    else
                        ((user) viewHolder).buttons[i - 2].setText(Integer.toString(dataList.get(position).getScore(i)));
                } else if (i == 14)
                    ((user) viewHolder).texts[2].setText(Integer.toString(dataList.get(position).getTotal()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getName().equals("categories") ? 1 : 0;
    }

    class categories extends RecyclerView.ViewHolder {
        categories(View itemView) {
            super(itemView);
        }
    }

    class user extends RecyclerView.ViewHolder {
        private Button[] buttons;
        private TextView[] texts;
        private TextView name;
        user(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView5);
            texts = new TextView[3];
            buttons = new Button[12];
            texts[0] = itemView.findViewById(R.id.textView11);
            texts[1] = itemView.findViewById(R.id.textView12);
            texts[2] = itemView.findViewById(R.id.textView13);
            buttons[0] = itemView.findViewById(R.id.button7);
            buttons[1] = itemView.findViewById(R.id.button8);
            buttons[2] = itemView.findViewById(R.id.button9);
            buttons[3] = itemView.findViewById(R.id.button10);
            buttons[4] = itemView.findViewById(R.id.button11);
            buttons[5] = itemView.findViewById(R.id.button12);
            buttons[6] = itemView.findViewById(R.id.button13);
            buttons[7] = itemView.findViewById(R.id.button14);
            buttons[8] = itemView.findViewById(R.id.button15);
            buttons[9] = itemView.findViewById(R.id.button16);
            buttons[10] = itemView.findViewById(R.id.button17);
            buttons[11] = itemView.findViewById(R.id.button18);
            for (int i = 0; i < 12; i++) {
                int finalI = i;
                buttons[i].setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null){
                                mListener.onItemClick(view, position, finalI);
                            }
                        }
                    }
                });
            }
        }
    }
}