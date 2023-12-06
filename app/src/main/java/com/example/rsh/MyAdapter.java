package com.example.rsh;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyviewHolder> {
    public int selectedPosition = RecyclerView.NO_POSITION;
    public interface OnItemClickListener {
        void onItemClick(int position, Context context);
    }

    Context context;
    List<Item> items;
    private OnItemClickListener listener;

    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }
    public Item getItem(int position) {
        return items.get(position);
    }
    public void updateItem(int position, String t1, String t2, String t3, String t4, boolean vote) {
        Item it = new Item("Название: " + t1, "Кол-во голосов: " + t2, "Цена: " + t3,t4, vote);
        items.set(position, it);
        notifyItemChanged(position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_of_items, parent, false);
        return new MyviewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        Item item = items.get(position);
        holder.nameView.setText(items.get(position).getName());
        holder.priceView.setText(items.get(position).getPrice());
        holder.countVoteView.setText(items.get(position).getCountVote());
        Picasso.get().load(items.get(position).getImageUrl()).into(holder.imageView);
        if (item.isSpecial()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
        } else {
            // Если элемент не специальный, используйте обычный цвет фона
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
