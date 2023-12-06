package com.example.rsh;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyviewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView, countVoteView, priceView;
    public void setItemBackgroundColor(int colorRes) {
        itemView.setBackgroundResource(colorRes);
    }
    public MyviewHolder(@NonNull View itemView, final MyAdapter.OnItemClickListener listener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageItem);
        nameView = itemView.findViewById(R.id.nameItem);
        countVoteView = itemView.findViewById(R.id.countVote);
        priceView = itemView.findViewById(R.id.priceItem);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, v.getContext());
                    }
                }
            }
        });

    }
}
