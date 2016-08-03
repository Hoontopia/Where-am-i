package com.example.sec.whereami;

import android.app.Dialog;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Created by songjonghun on 2016. 7. 17..
 */
public class PickerDlg extends Dialog implements TextToSpeech.OnInitListener {
    String[] keys;
    String[] values;
    int valueIdx;
    final int number_of_values = 20;
    final int range = 100;
    String[] meters;
    ViewFlipper picker_flipper;
    public PickerDlg(final Context context, final String[] _keys, final String[] _values, final TextToSpeech _tts) {
        super(context);
        keys = _keys;
        values = _values;

        meters = new String[number_of_values];
        for(int i=0; i<number_of_values; i++){
            meters[i] = String.valueOf(range * (i+1));
        }


        this.setTitle("타입 설정");
        this.setContentView(R.layout.picker);
        Button btnOk = (Button) this.findViewById(R.id.ok_search);
        Button btnCancel = (Button) this.findViewById(R.id.cancel_search);
        final NumberPicker np1 = (NumberPicker) this.findViewById(R.id.type_picker);
        final NumberPicker np2 = (NumberPicker) this.findViewById(R.id.radius_picker);
        picker_flipper = (ViewFlipper) this.findViewById(R.id.picker_flipper);

        np2.setMaxValue(meters.length-1);
        np2.setMinValue(0);
        np2.setDisplayedValues(meters);
        np2.setWrapSelectorWheel(true);

        np1.setMaxValue(keys.length-1);
        np1.setMinValue(0);
        np1.setWrapSelectorWheel(true);
        np1.setDisplayedValues(keys);

        np1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                _tts.speak(_keys[newVal], TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                _tts.speak(String.valueOf(meters[newVal]), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_flipper.showNext();
                setTitle("검색반경 설정");
                /*
                valueIdx = np1.getValue();
                MainActivity.types = values[valueIdx];
                MainActivity.typename = keys[valueIdx];
                MainActivity.radius = String.valueOf(np2.getValue());
                Toast.makeText(context, "설정되었습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
                */

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

    @Override
    public void onInit(int status) {}
}
