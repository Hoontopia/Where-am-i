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

        Button btnOk = (Button) this.findViewById(R.id.set_ok);
        Button btnCancel = (Button) this.findViewById(R.id.cancel_search);
        Button btnPrev = (Button) this.findViewById(R.id.previous);
        Button btnNext = (Button) this.findViewById(R.id.next);

        final NumberPicker typePicker = (NumberPicker) this.findViewById(R.id.type_picker);
        final NumberPicker radiousPicker = (NumberPicker) this.findViewById(R.id.radius_picker);
        picker_flipper = (ViewFlipper) this.findViewById(R.id.picker_flipper);
        radiousPicker.setMaxValue(meters.length-1);
        radiousPicker.setMinValue(0);
        radiousPicker.setDisplayedValues(meters);
        radiousPicker.setWrapSelectorWheel(true);

        typePicker.setMaxValue(keys.length-1);
        typePicker.setMinValue(0);
        typePicker.setWrapSelectorWheel(true);
        typePicker.setDisplayedValues(keys);

        typePicker.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        radiousPicker.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);

        typePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                _tts.speak(_keys[newVal], TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        radiousPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                _tts.speak(String.valueOf(meters[newVal]), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.types = values[valueIdx];
                MainActivity.typename = keys[valueIdx];
                MainActivity.radius = String.valueOf(radiousPicker.getValue());
                Toast.makeText(context, "설정되었습니다.", Toast.LENGTH_SHORT).show();
                cancel();
                picker_flipper.showPrevious();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                cancel();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_flipper.showNext();
                setTitle("검색반경 설정");
                Toast.makeText(context, "스크롤하여 검색반경을 설정해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker_flipper.showPrevious();
                setTitle("타입 설정");
                Toast.makeText(context, "스크롤하여 타입을 설정해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onInit(int status) {}
}
