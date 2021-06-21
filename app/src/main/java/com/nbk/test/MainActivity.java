package com.nbk.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.changeTextBtn);
        TextView text = findViewById(R.id.HellText);
        MyBtnListener click = new MyBtnListener(btn, text);
        btn.setOnClickListener(click);
        findViewById(R.id.RestBtn).setOnClickListener(v -> click.reset());
        MainActivity activity = this;
        findViewById(R.id.Cal).setOnClickListener(a->FragmentAct.FargeStart(activity,1,2,3));
    }

    class MyBtnListener implements View.OnClickListener{
        private Button btn;
        private TextView text;
        private int clickCount = 0;

        public MyBtnListener(Button btn, TextView text) {
            this.btn = btn;
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            clickCount++;
            btn.setText("设置样式");
            Log.d("Main", (String.format(getString(R.string.countText), clickCount)));
            refreshTextView();
        }

        private void refreshTextView() {
            text.setText(String.format(getString(R.string.countText), clickCount));
        }

        public void reset(){
            clickCount = 0;
            refreshTextView();
        }
    }
}