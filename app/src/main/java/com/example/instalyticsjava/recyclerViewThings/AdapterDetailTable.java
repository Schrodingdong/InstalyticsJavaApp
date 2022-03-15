package com.example.instalyticsjava.recyclerViewThings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instalyticsjava.InterpretationActivity;
import com.example.instalyticsjava.R;
import com.example.instalyticsjava.interpretation.InterpretationPopup;
import com.example.instalyticsjava.interpretation.InterpretationUpdatePopup;
import com.example.instalyticsjava.interpretation.OrderDataBase;

import java.util.List;

public class AdapterDetailTable extends RecyclerView.Adapter<AdapterDetailTable.ViewHolder>{
    private final String TAG = "AdapterDetailTable";

    private Context context;
    private InterpretationActivity dependencyActivity;
    private InterpretationUpdatePopup interpretationUpdatePopup;
    private final List<Integer> ID_list;
    private final List<Integer> epochDate_value_list;
    private final List<String> date_value_list;
    private final List<Integer> reach_value_list;
    private final List<Integer> webClick_value_list;
    private final List<Integer> profileViews_value_list;
    private final List<Integer> orders_value_list;
    public AdapterDetailTable(InterpretationActivity contextActivity,
                              List<Integer> ID_list,
                              List<Integer> epochDate_value_list,
                              List<String> date_value_list,
                              List<Integer> reach_value_list,
                              List<Integer> webClick_value_list,
                              List<Integer> profileViews_value_list,
                              List<Integer> orders_value_list){
        this.context = contextActivity;
        this.dependencyActivity = contextActivity;
        this.ID_list = ID_list;
        this.epochDate_value_list = epochDate_value_list;
        this.date_value_list = date_value_list;
        this.reach_value_list = reach_value_list;
        this.webClick_value_list = webClick_value_list;
        this.profileViews_value_list = profileViews_value_list;
        this.orders_value_list = orders_value_list;

        Log.d(TAG, "AdapterDetailTable: "+date_value_list);
    }
    @NonNull
    @Override
    public AdapterDetailTable.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_detail_table,parent,false);
        return new AdapterDetailTable.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDetailTable.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //parse
        String date_value = date_value_list.get(position);
        int reach_value = reach_value_list.get(position);
        int webClick_value = webClick_value_list.get(position);
        int profileViews_value = profileViews_value_list.get(position);
        int orders_value = orders_value_list.get(position);

        holder.date.setText(date_value);
        holder.reach.setText(Integer.toString(reach_value));
        holder.webClick.setText(Integer.toString(webClick_value));
        holder.profileViews.setText(Integer.toString(profileViews_value));
        holder.orders.setText(Integer.toString(orders_value));
        holder.detail_table_row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPopupMenu(holder.itemView,position);
                return false;
            }
        });
    }

    private void showPopupMenu(View v, int positionInRecycler) {
        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                OrderDataBase db = new OrderDataBase(context);
                int elementID = ID_list.get(positionInRecycler);
                switch (menuItem.getItemId()){
                    case R.id.update_element:
                        Log.d(TAG, "onMenuItemClick: update element");
                        //gives a pop up to enter the element
                        interpretationUpdatePopup = new InterpretationUpdatePopup(dependencyActivity,elementID);
                        interpretationUpdatePopup.build();
                        //update
                        break;
                    case R.id.delete_element:
                        db.deleteRowOnId(elementID);
                        dependencyActivity.updateInterpretation();
                        Toast.makeText(context,"Item Deleted Sucessfully !",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        popupMenu.inflate(R.menu.recylcer_detail_table_popup_menu_on_longpress);
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return epochDate_value_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date,reach,webClick,profileViews,orders;
        private View detail_table_row;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_detail);
            reach = itemView.findViewById(R.id.reach_detail);
            webClick = itemView.findViewById(R.id.websiteclicks_detail);
            profileViews = itemView.findViewById(R.id.profile_views_detail);
            orders = itemView.findViewById(R.id.orders_detail);
            detail_table_row = itemView.findViewById(R.id.detail_table_row);
        }
    }
}
