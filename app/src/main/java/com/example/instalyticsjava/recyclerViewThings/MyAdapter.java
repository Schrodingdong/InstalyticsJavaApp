package com.example.instalyticsjava.recyclerViewThings;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalyticsjava.HashtagList;
import com.example.instalyticsjava.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Map<String,Integer> HashtagElementMap = new HashMap<>();
    private List<String> Hashtags = new ArrayList<>();
    private Context context;
    public MyAdapter(Context context, Map<String,Integer> HashtagElement, List<String> Hashtags){
        this.HashtagElementMap = HashtagElement;
        this.context = context;
        this.Hashtags = Hashtags;
        Log.d(TAG, "MyAdapter: "+Hashtags);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_hashtag_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String text = Hashtags.get(position);
        String Value = String.format("%5d",HashtagElementMap.get(text));
        holder.hashtag.setText("#"+text);
        holder.engagement.setText(Value);
    }

    @Override
    public int getItemCount() {
        return Hashtags.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView hashtag;
        TextView engagement;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hashtag = itemView.findViewById(R.id.hashtag);
            engagement = itemView.findViewById(R.id.hashtag_eng);
        }
    }
}
