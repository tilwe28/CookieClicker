package com.example.cookieclicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    ImageView iv_pizza;
    TextView tv_score;
    static int clickCounter=0;

    final ScaleAnimation increaseSize = new ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        increaseSize.setDuration(100);

        iv_pizza = findViewById(R.id.imageView_pizza);
        tv_score = findViewById(R.id.textView_score);

        iv_pizza.setImageResource(R.drawable.pizza_circle);
        iv_pizza.setScaleX(.5f);
        iv_pizza.setScaleY(.5f);

        iv_pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(increaseSize);
                addCount(1);
                Log.d("COUNT", "Clicks: " + clickCounter);

                tv_score.setText("Pizzas: " + clickCounter);
            }
        });
    }

    public static synchronized void addCount(int num) {
        clickCounter+=num;
    }
}