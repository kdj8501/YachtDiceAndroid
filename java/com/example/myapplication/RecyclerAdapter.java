package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;



public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface OnItemClickListener{
        void onItemClick(View v, int position);
    }
    private OnItemClickListener mListener = null;
    private EditText roomName;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private ArrayList<String> dataList = null;
    RecyclerAdapter(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(viewType == 1) {
            view = inflater.inflate(R.layout.room_create, parent, false);
            return new btnCreate(view);
        }
        else {
            view = inflater.inflate(R.layout.room_item, parent, false);
            return new btnRoom(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof btnCreate)
            roomName = ((btnCreate) viewHolder).text1;
        else
            ((btnRoom) viewHolder).button1.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).equals("create") ? 1 : 0;
    }

    public String getCreateRoomName() { return roomName.getText().toString(); }

    class btnCreate extends RecyclerView.ViewHolder {

        private Button button1;
        private EditText text1;
        btnCreate(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.chat);
            button1 = itemView.findViewById(R.id.button1);
            button1.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null){
                            mListener.onItemClick (view, position);
                        }
                    }
                }
            });
        }
    }

    class btnRoom extends RecyclerView.ViewHolder {

        private Button button1;
        btnRoom(View itemView) {
            super(itemView);
            button1 = itemView.findViewById(R.id.button1);
            button1.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null){
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });
        }
    }
}