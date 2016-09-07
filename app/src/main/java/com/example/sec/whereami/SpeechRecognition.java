package com.example.sec.whereami;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by CodeFactory on 2016-08-11.
 */

public class SpeechRecognition implements TextToSpeech.OnInitListener {
    SpeechRecognizer mRecognizer;
    EditText nameText;
    TextToSpeech _tts;
    public SpeechRecognition(Context context, final EditText nameText) {
        this.nameText = nameText;
        _tts = new TextToSpeech(context, this); //인스턴스 생성
        _tts.setLanguage(Locale.KOREA);//tts 언어 설정
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mRecognizer.setRecognitionListener(new RecognitionListener() {
            Count count = new Count();
            @Override
            public void onReadyForSpeech(Bundle params) { count.execute();}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float rmsdB) {}
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int error) {}
            @Override
            public void onResults(Bundle results) {
                String key = "";
                key = SpeechRecognizer.RESULTS_RECOGNITION;
                ArrayList<String> mResult = results.getStringArrayList(key);
                String[] rs = new String[mResult.size()];
                mResult.toArray(rs);
                rs[0] = rs[0].replaceAll("\\s", "");
                nameText.setText(""+rs[0]);
                String temp = nameText.getText().toString();
                if(temp.isEmpty()){_tts.speak("입력된 내용이 없습니다.", TextToSpeech.QUEUE_FLUSH, null);}
                else{_tts.speak("입력된 내용은 "+temp+" 입니다.", TextToSpeech.QUEUE_FLUSH, null);}
                mRecognizer.destroy();
            }
            @Override
            public void onPartialResults(Bundle partialResults) {}
            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
        mRecognizer.startListening(i);
    }
    @Override
    public void onInit(int status) {}
    class Count extends AsyncTask<Void, Void, Void> {
        int time;
        @Override
        protected Void doInBackground(Void... params) {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000); time++;
                    if (time == 5)
                        return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRecognizer.stopListening();
        }
    }
}
