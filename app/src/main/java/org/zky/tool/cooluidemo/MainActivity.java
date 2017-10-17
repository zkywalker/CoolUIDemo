package org.zky.tool.cooluidemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.zky.tool.cooluidemo.widget.PraiseView;

public class MainActivity extends AppCompatActivity {
    PraiseView praiseView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        praiseView = (PraiseView) findViewById(R.id.praise_view);
        editText = (EditText) findViewById(R.id.et_num);

        praiseView.setNum(599);
        praiseView.setListener(new PraiseView.OnPraiseListener() {
            @Override
            public void onPraise(Boolean b, int num) {
                Log.d("test", "state:" + b + ",num:" + num);
//                editText.setText("state:" + b + ",num:" + num);
            }
        });
    }

    public void button1(View view) {
    }

    public void button2(View view) {
        praiseView.setNum(4309);
    }

    public void button3(View view) {
        praiseView.setNum(25);

    }

    public void button4(View view) {
        String text = editText.getText().toString();
        try {
            int i = Integer.parseInt(text);
            praiseView.setNum(i);
        }catch (Exception e){
            Toast.makeText(this,"非数字",Toast.LENGTH_SHORT).show();
        }

    }
}
