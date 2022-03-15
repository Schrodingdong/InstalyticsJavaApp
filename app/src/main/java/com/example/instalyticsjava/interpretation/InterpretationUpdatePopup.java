package com.example.instalyticsjava.interpretation;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instalyticsjava.InterpretationActivity;
import com.example.instalyticsjava.R;

import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;

public class InterpretationUpdatePopup extends Dialog {
    private final String TAG = "InterpretationUpdatePopup";
    private Context context;
    private InterpretationActivity dependencyActivity;
    private OrderDataBase db;


    private EditText new_value_detail_table;
    private Button UPDATEbutton;
    private int elementId;
    public InterpretationUpdatePopup(@NonNull InterpretationActivity context, int elementId) {
        super(context,com.facebook.R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.popup_detail_table_update);

        this.db = new OrderDataBase(context);
        this.elementId = elementId;
        this.context = context;
        this.dependencyActivity = context;

        new_value_detail_table = findViewById(R.id.new_value_detail_table);
        UPDATEbutton = findViewById(R.id.UPDATEbutton);
        UPDATEbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the DB
                int newValue = Integer.parseInt(getNew_value_detail_table().getText().toString());
                db.updateRowOnId(elementId,newValue);
                dependencyActivity.updateInterpretation();
                dismiss();
                Toast.makeText(context,"Item Updated Sucessfully !",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public EditText getNew_value_detail_table() {
        return new_value_detail_table;
    }

    public void build(){
        show();
    }

}
