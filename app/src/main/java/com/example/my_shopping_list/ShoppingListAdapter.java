package com.example.my_shopping_list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private ArrayList<String> list;
    public ShoppingListAdapter(ArrayList<String> list) {
        this.list = list;
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position) {
        holder.itemName.setText(list.get(position));
    }

    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_item_template,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView itemName;
        public ViewHolder(View v){
            super(v);
            itemName = v.findViewById(R.id.item_name);
        }
    }
}
