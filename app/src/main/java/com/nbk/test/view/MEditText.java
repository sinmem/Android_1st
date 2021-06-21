package com.nbk.test.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class MEditText extends androidx.appcompat.widget.AppCompatEditText implements TextWatcher {
    private boolean focus;

    public MEditText(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public MEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
        disableShowInput();
        MEditText mEditText = this;
    }

    public void disableShowInput() {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            this.setInputType(InputType.TYPE_NULL);

        } else {
            this.setShowSoftInputOnFocus(false);
        }
    }


    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
//        this.setSelection(s.length(), this.length());
    }
}


