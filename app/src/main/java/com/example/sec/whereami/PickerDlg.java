package com.example.sec.whereami;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

/**
 * Created by songjonghun on 2016. 7. 17..
 */
public class PickerDlg extends Dialog {
    String[] keys;
    String[] values;
    int valueIdx;
    final int number_of_values = 20;
    final int range = 100;
    String[] displayedValues;
    public PickerDlg(final Context context, String[] _keys, String[] _values) {
        super(context);
        keys = _keys;
        values = _values;

        displayedValues = new String[number_of_values];
        for(int i=0; i<number_of_values; i++){
            displayedValues[i] = String.valueOf(range * (i+1));
        }

        this.setTitle("Setting");
        this.setContentView(R.layout.picker);
        Button btnOk = (Button) this.findViewById(R.id.button1);
        Button btnCancel = (Button) this.findViewById(R.id.button2);
        final NumberPicker np1 = (NumberPicker) this.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) this.findViewById(R.id.numberPicker2);

        np2.setMaxValue(displayedValues.length-1);
        np2.setMinValue(0);
        np2.setDisplayedValues(displayedValues);
        np2.setWrapSelectorWheel(true);

        np1.setMaxValue(keys.length-1);
        np1.setMinValue(0);
        np1.setWrapSelectorWheel(true);
        np1.setDisplayedValues(keys);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valueIdx = np1.getValue();
                MainActivity.types = values[valueIdx];
                MainActivity.typename = keys[valueIdx]; //
                MainActivity.radius = String.valueOf(np2.getValue());
                Toast.makeText(context, "설정되었습니다.", Toast.LENGTH_SHORT).show();
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
