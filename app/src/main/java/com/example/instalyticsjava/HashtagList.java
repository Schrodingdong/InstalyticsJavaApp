package com.example.instalyticsjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.example.instalyticsjava.recyclerViewThings.MyAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HashtagList extends AppCompatActivity {
    private final String TAG = "HashtagList";
    private final Map<String,Integer> HashtagElementMap = new HashMap<String,Integer>();                  //the # && engagement
    private final Set<String> HashtagsSet = new HashSet<>();
    private List<String> Hashtags = new ArrayList<>();
    private RecyclerView hashtag_recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_hashtag_list);


        InitialiseHashtagMap();
        MyAdapter myAdapter = new MyAdapter(this, HashtagElementMap,Hashtags);
        Log.d(TAG, "onCreate: "+myAdapter);

        hashtag_recycler = findViewById(R.id.hashtag_recycler);
        hashtag_recycler.setAdapter(myAdapter);
        hashtag_recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void InitialiseHashtagMap(){
        Set<String> HSet = new HashSet<String>();
        for (String id :
                DataSingelton.getIg_postIDs()) {
            getPostHashtagInMap(id);
        }
        getHashtagList();
    }
    private void getHashtagList() {
        Hashtags.addAll(HashtagsSet);
        orderHashtagList();
    }
    private void getPostHashtagInMap(String id){
        String caption = Objects.requireNonNull(DataSingelton.getIg_post_data().get(id)).getCaption();
        String[] Harray = caption.split("#",100);                                         //the 0 is always useless
        int l = Harray.length;
        for (int i = 1; i < l ; i++){
            HashtagsSet.add(Harray[i]);
            if(HashtagElementMap.get(Harray[i]) == null)
                HashtagElementMap.put(Harray[i],
                        Objects.requireNonNull(Objects.requireNonNull(DataSingelton.getIg_post_data_insights().get(id)).get("engagement")).getValues().get(0).getValue());
            else{
                int engagement = HashtagElementMap.get(Harray[i]);
                engagement += Objects.requireNonNull(Objects.requireNonNull(DataSingelton.getIg_post_data_insights().get(id)).get("engagement")).getValues().get(0).getValue();
                HashtagElementMap.put(Harray[i],engagement);
            }
        }
    }
    private void orderHashtagList(){
        List<String> result = new ArrayList<>();

        for (int k = 0; k < Hashtags.size(); k++) {             //we can do O(n*log(n))
            int max = -1;
            String maxHt = "";
            for (int i = 0; i < Hashtags.size(); i++) {
                String ht = Hashtags.get(i);
                if (result.contains(ht)) continue;
                if (max <= HashtagElementMap.get(ht)) {
                    max = HashtagElementMap.get(ht);
                    maxHt = ht;
                }
            }
            result.add(maxHt);
        }
        Hashtags = result;
    }
}