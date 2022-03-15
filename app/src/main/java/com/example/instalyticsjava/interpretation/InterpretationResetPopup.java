package com.example.instalyticsjava.interpretation;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.instalyticsjava.InterpretationActivity;
import com.example.instalyticsjava.R;

public class InterpretationResetPopup extends Dialog {

    private Context context;
    private InterpretationActivity dependencyActivity;
    private Button yes_delete_db_button;
    private Button no_delete_db_button;
    private OrderDataBase db;
    public InterpretationResetPopup(@NonNull Context context, InterpretationActivity dependencyActivity) {
        super(context);
        setContentView(R.layout.popup_detail_table_reset);
        yes_delete_db_button = findViewById(R.id.yes_delete_db_button);
        no_delete_db_button = findViewById(R.id.no_delete_db_button);
        this.context = dependencyActivity;
        db = new OrderDataBase(context);

        yes_delete_db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deletes
                db.resetDatabase();
                dependencyActivity.updateInterpretation();
                dismiss();
            }
        });
        no_delete_db_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    public void build(){
        show();
    }

}
