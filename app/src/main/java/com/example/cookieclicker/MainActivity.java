package com.example.cookieclicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.*;
import android.widget.*;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout layout_main;
    LinearLayout layout_store, layout_upgrades;
    ImageView iv_pizza, iv_cursor, iv_oven, iv_oven2x;
    TextView tv_score, tv_store, tv_upgrades;

    static int count_clicks=0;
    static boolean upgrade_cursor=false, upgrade_oven=false, upgrade_oven2x=false;

    PassiveIncomeThread ovenUpgrade;

    private Context context;
    final MainActivity mainActivity = MainActivity.this;

    ConstraintLayout.LayoutParams wrapContentParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SCORE", count_clicks);
        Log.d("TAG", "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count_clicks = savedInstanceState.getInt("SCORE");
        Log.d("TAG", "onRestoreInstanceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "onCreate");
        setContentView(R.layout.activity_main);
        context = this;

        layout_main = findViewById(R.id.layout_constraint);
        layout_store = findViewById(R.id.linearLayout_store);
        layout_upgrades = findViewById(R.id.linearLayout_upgrades);
        iv_pizza = findViewById(R.id.imageView_pizza);
        iv_cursor = findViewById(R.id.imageView_cursor);
        iv_oven = findViewById(R.id.imageView_oven);
        iv_oven2x = findViewById(R.id.imageView_oven2x);
        tv_score = findViewById(R.id.textView_score);
        tv_store = findViewById(R.id.textView_store);
        tv_upgrades = findViewById(R.id.textView_upgrades);

        iv_pizza.setImageResource(R.drawable.pizza_circle);
        iv_pizza.setScaleX(.75f);
        iv_pizza.setScaleY(.75f);

        iv_cursor.setClickable(false);
        iv_oven.setClickable(false);
        iv_oven2x.setClickable(false);

        tv_store.setText(Html.fromHtml("<u>"+"Store"+"</u>"));
        tv_upgrades.setText(Html.fromHtml("<u>"+"Upgrades"+"</u>"));

        tv_score.setText("Pizzas: " + count_clicks);

        iv_pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScaleAnimation increaseSize = new ScaleAnimation(1.0f, 1.05f, 1.0f, 1.05f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                increaseSize.setDuration(100);

                v.startAnimation(increaseSize);
                Log.d("ANIMATION", "increaseSize");
                if(!upgrade_cursor) {
                    addCount(1);
                    plusOneAnimation("+1");
                } else {
                    addCount(2);
                    plusOneAnimation("+2");
                }
                tv_score.setText("Pizzas: " + count_clicks);

                checkUpgrades();
            }
        });

        iv_cursor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ANIMATION", "Cursor upgrade");
                if (count_clicks>=10) {
                    upgrade_cursor = true;
                    minusCount(10);
                    tv_score.setText("Pizzas: " + count_clicks);
                    checkUpgrades();

                    upgradeSelectedAnimation(iv_cursor);

                    if (layout_store.getChildCount() < 3)
                        layout_store.setWeightSum(0.66f);

                    v.setClickable(false);
                }
            }
        });
        iv_oven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ANIMATION", "Oven upgrade");
                if (count_clicks>=20) {
                    upgrade_oven = true;
                    minusCount(20);
                    tv_score.setText("Pizzas: " + count_clicks);
                    checkUpgrades();

                    upgradeSelectedAnimation(v);

                    if (layout_store.getChildCount() < 3)
                        layout_store.setWeightSum(0.66f);

                    v.setClickable(false);

                    ovenUpgrade = new PassiveIncomeThread(5);
                    ovenUpgrade.start();
                }
            }
        });
        iv_oven2x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ANIMATION", "Oven2x upgrade");
                if (count_clicks>=50) {
                    upgrade_oven2x = true;
                    minusCount(50);
                    tv_score.setText("Pizzas: " + count_clicks);
                    checkUpgrades();

                    upgradeSelectedAnimation(v);

                    v.setClickable(false);

                    ovenUpgrade.setNum(10);
                }
            }
        });
    }

    public void plusOneAnimation(CharSequence charSequence) {
        final TranslateAnimation movesUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -5.0f);
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.01f);
        movesUp.setDuration(1000);
        fadeOut.setDuration(1000);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(movesUp);
        animationSet.addAnimation(fadeOut);

        ConstraintLayout.LayoutParams localLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_plusOne = new TextView(context);
        tv_plusOne.setText(charSequence);
        tv_plusOne.setTextColor(Color.WHITE);
        tv_plusOne.setTextSize(18);
        tv_plusOne.setId(View.generateViewId());
        tv_plusOne.setLayoutParams(localLayoutParams);
        layout_main.addView(tv_plusOne);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout_main);
        constraintSet.connect(tv_plusOne.getId(), ConstraintSet.LEFT, iv_pizza.getId(), ConstraintSet.LEFT);
        constraintSet.connect(tv_plusOne.getId(), ConstraintSet.RIGHT, iv_pizza.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(tv_plusOne.getId(), ConstraintSet.TOP, iv_pizza.getId(), ConstraintSet.TOP);
        constraintSet.connect(tv_plusOne.getId(), ConstraintSet.BOTTOM, iv_pizza.getId(), ConstraintSet.BOTTOM);

        float rHoriz = (float)(Math.random());
        float rVert = (float)(Math.random());
        Log.d("ANIMATION", "tv_plusOne set at ("+rHoriz+", "+rVert+")");
        constraintSet.setHorizontalBias(tv_plusOne.getId(), rHoriz);
        constraintSet.setVerticalBias(tv_plusOne.getId(), rVert);
        constraintSet.applyTo(layout_main);
        Log.d("ANIMATION", "View added:" + layout_main.getChildCount());

        tv_plusOne.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_plusOne.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ANIMATION", "View removed:" + layout_main.getChildCount());
                                layout_main.removeView(tv_plusOne);
                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void upgradeSelectedAnimation(View v) {
        View tempView = v;
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.01f);
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        final RotateAnimation spin = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fadeOut.setDuration(500);
        fadeIn.setDuration(500);
        spin.setDuration(250);
        spin.setRepeatCount(2);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeOut);
        animationSet.addAnimation(spin);

        v.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout_store.post(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iv_cursor.equals(v)) {
                                    layout_store.removeView(iv_cursor);
                                } else if (iv_oven.equals(v)) {
                                    layout_store.removeView(iv_oven);
                                } else if (iv_oven2x.equals(v)) {
                                    layout_store.removeView(iv_oven2x);
                                }
                                Log.d("ANIMATION", "StoreLayout - View removed:" + layout_store.getChildCount());
                                layout_upgrades.addView(tempView);
                                Log.d("ANIMATION", "UpgradeLayout - View added:" + layout_upgrades.getChildCount());
                            }
                        });

                        if (layout_store.getChildCount() < 3)
                            layout_store.setWeightSum(0.66f);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public class PassiveIncomeThread extends Thread {
        int num;

        public PassiveIncomeThread(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                addCount(num);
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_score.setText("Pizzas: " + count_clicks);
                        checkUpgrades();
                    }
                });
            }
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    public void checkUpgrades() {
        if (count_clicks>=10 && !upgrade_cursor) {
            iv_cursor.setAlpha(1.0f);
            iv_cursor.setClickable(true);
        } else if (!upgrade_cursor){
            iv_cursor.setAlpha(0.35f);
            iv_cursor.setClickable(false);
        }

        if (count_clicks>=20 && !upgrade_oven) {
            iv_oven.setAlpha(1.0f);
            iv_oven.setClickable(true);
        } else if (!upgrade_oven){
            iv_oven.setAlpha(0.35f);
            iv_oven.setClickable(false);
        }

        if (count_clicks>=50 && upgrade_oven && !upgrade_oven2x) {
            iv_oven2x.setAlpha(1.0f);
            iv_oven2x.setClickable(true);
        } else if (!upgrade_oven2x){
            iv_oven2x.setAlpha(0.35f);
            iv_oven2x.setClickable(false);
        }
    }

    public static synchronized void addCount(int num) {
        count_clicks+=num;
        Log.d("COUNT", "Clicks: " + count_clicks);
    }

    public static synchronized void minusCount(int num) {
        count_clicks-=num;
        Log.d("COUNT", "Clicks: " + count_clicks);
    }
}