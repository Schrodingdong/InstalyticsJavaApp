package com.example.instalyticsjava.dataposts;

import android.util.Log;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class PostData {                                                                             //stories unsupported here
    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";
    public static final String CAROUSEL_ALBUM = "CAROUSEL_ALBUM";

    private final String TAG = "PostData";
    public PostData(@NonNull JSONObject JSONPostData){
        try {
            Log.d(TAG, "PostData: Started initialisation...");
            this.id = JSONPostData.get("id").toString();
            this.media_product_type = JSONPostData.get("media_product_type").toString();
            this.media_type = JSONPostData.get("media_type").toString();
            this.permalink = JSONPostData.get("permalink").toString();
            this.timestamp = JSONPostData.get("timestamp").toString();
            this.caption = JSONPostData.get("caption").toString();
            String temp_comments_count =JSONPostData.get("comments_count").toString();
            String temp_like_count =JSONPostData.get("like_count").toString();
            this.comments_count = Integer.parseInt(temp_comments_count) ;
            this.like_count = Integer.parseInt(temp_like_count);
            Log.d(TAG, "PostData: finished !");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public PostData(){
        this.id = "";
        this.media_product_type = "";
        this.media_type = "";
        this.permalink = "";
        this.timestamp = "";
        this.comments_count = 0;
        this.like_count = 0;
    }


    private String id = "";
    private String media_product_type = "";
    private String media_type = "";
    private String permalink = "";
    private String timestamp = "";
    private String caption = "";
    private int comments_count = 0;
    private int like_count = 0;

    public String getId() {
        return id;
    }

    public String getMedia_product_type() {
        return media_product_type;
    }

    public String getMedia_type() {
        return media_type;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCaption() {
        return caption;
    }

    public int getComments_count() {
        return comments_count;
    }

    public int getLike_count() {
        return like_count;
    }

    @Override
    public String toString() {
        return "PostData{" +
                "TAG='" + TAG + '\'' +
                ", id='" + id + '\'' +
                ", media_product_type='" + media_product_type + '\'' +
                ", media_type='" + media_type + '\'' +
                ", permalink='" + permalink + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", caption='" + caption + '\'' +
                ", comments_count=" + comments_count +
                ", like_count=" + like_count +
                '}';
    }
}

//sample api request [...]/17885545445614810?fields=caption,comments_count,like_count,media_type,media_product_type,permalink,timestamp
