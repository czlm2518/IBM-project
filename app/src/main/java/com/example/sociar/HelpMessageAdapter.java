package com.example.sociar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HelpMessageAdapter extends RecyclerView.Adapter<HelpMessageAdapter.HelpMessageViewHolder> {

    ArrayList<String> messages;

    Context context;


    public HelpMessageAdapter(Context parent, ArrayList<String> list_messages) {
        context = parent;// the big recycler view
        messages = list_messages;// list of messages to be displayed in that recycler view
    }

    // create individual view holders
    @NonNull
    @Override
    public HelpMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //inflate the individual viewholders, so the layout can be referred to afterwards
        View view = layoutInflater.inflate(R.layout.message, parent, false);
        return new HelpMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpMessageViewHolder holder, int position) {
        holder.bindData(position);
    }


    // total number of individual view holders in the recycler view
    @Override
    public int getItemCount() {
        return messages.size();
    }


    // create individual view holder items
    public class HelpMessageViewHolder extends RecyclerView.ViewHolder {
        // class variables
        TextView tv_sent_message;
        TextView tv_received_message;

        //constructor
        public HelpMessageViewHolder(@NonNull View itemView) {
            super(itemView);// itemView = the inflated individual view holder layout
            tv_sent_message = itemView.findViewById(R.id.tv_sent_message);
            tv_received_message = itemView.findViewById(R.id.tv_received_message);
        }

        public void bindData(int position) {
            if (position%2 == 0){
                tv_sent_message.setVisibility(View.GONE);
                tv_received_message.setVisibility(View.VISIBLE);
                tv_received_message.setText(messages.get(position));
            } else {
                tv_received_message.setVisibility(View.GONE);
                tv_sent_message.setVisibility(View.VISIBLE);
                tv_sent_message.setText(messages.get(position));
            }

        }
    }
}
