package com.example.sec.whereami;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by Factory on 2016-07-29.
 */
public class Register_LOC_Dlg extends Dialog {
    String[] keys;
    String[] values;
    int valueIdx;
    String input_type;
    String input_name;
    String lat;
    String lng;
    LOC_DB_Helper helper;
    SQLiteDatabase db;

    public Register_LOC_Dlg(final Context context, String[] _keys, String[] _values, String _lat, String _lng) {
        super(context);
        keys = _keys;
        values = _values;
        lat = _lat;
        lng = _lng;

        helper = new LOC_DB_Helper(context);

        try{
            db = helper.getWritableDatabase();
        }catch (SQLiteException ex){
            db = helper.getReadableDatabase();
        }

        this.setTitle("Add_location");
        this.setContentView(R.layout.add_loc);
        final EditText loc_name = (EditText)this.findViewById(R.id.myloc_name);
        Button btnOk = (Button) this.findViewById(R.id.ok_add);
        Button btnCancel = (Button) this.findViewById(R.id.cancel_add);
        final NumberPicker type_pick = (NumberPicker) this.findViewById(R.id.type_pick);

        type_pick.setMaxValue(keys.length-1);
        type_pick.setMinValue(0);
        type_pick.setWrapSelectorWheel(true);
        type_pick.setDisplayedValues(keys);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueIdx = type_pick.getValue();
                input_type = values[valueIdx];
                input_name = loc_name.getText().toString();
                if(input_name.isEmpty()) {
                    Toast.makeText(context, "장소 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                db.execSQL("INSERT INTO locs VALUES (null, '" + input_type + "', '" + input_name + "', '" + lat + "', '" + lng + "');");
                Toast.makeText(context, "입력되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                cancel();
            }
        });
    }
}
