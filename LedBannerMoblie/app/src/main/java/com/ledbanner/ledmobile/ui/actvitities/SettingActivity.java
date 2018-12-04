package com.ledbanner.ledmobile.ui.actvitities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.ledbanner.ledmobile.R;
import com.ledbanner.ledmobile.data.local.sharedprf.SharedPrefsImpl;
import com.ledbanner.ledmobile.data.local.sharedprf.SharedPrefsKey;
import com.ledbanner.ledmobile.databinding.ActivitySettingBinding;
import com.ledbanner.ledmobile.models.TextLed;


public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySettingBinding mBinding;
    private TextLed mTextLed;
    private SharedPrefsImpl mSharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initAction();
    }

    private void initUI() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mSharedPrefs = new SharedPrefsImpl(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mBinding.textContent.clearFocus();
        mBinding.textContent.setTypeface(mBinding.textContent.getTypeface(), Typeface.BOLD);
        String content = mSharedPrefs.get(SharedPrefsKey.PREF_CONTENT, String.class);
        if (TextUtils.isEmpty(content)) {
            content = "Hi!";
        }

        long speed = mSharedPrefs.get(SharedPrefsKey.PREF_TEXT_SPEED, Long.class);
        if (speed == 0) {
            speed = 4000;
        }

        int textSize = mSharedPrefs.get(SharedPrefsKey.PREF_TEXT_SIZE, Integer.class);
        if (textSize == 0) {
            textSize = 115;
        }
        mTextLed = new TextLed.Builder().setLed(mSharedPrefs.get(SharedPrefsKey.PREF_STYLE_LED, Boolean.class))
                .setBlinking(mSharedPrefs.get(SharedPrefsKey.PREF_IS_BLINK, Boolean.class))
                .setContent(content)
                .setRightToLeft(mSharedPrefs.get(SharedPrefsKey.PREF_STYLE_SHOW, Boolean.class))
                .setTextSpeed(speed)
                .setRunning(true)
                .setSize(textSize)
                .build();
        mBinding.setTextLed(mTextLed);
        mBinding.textContent.setRndDuration((int) mTextLed.getTextSpeed());
        if (mTextLed.isBlinking()) {
            mBinding.textContent.addAnimationBlinking();
            mBinding.textContent.startAnimation(mBinding.textContent.getAnimationSet());
        }
        mBinding.textContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        mBinding.textContent.startScroll();
        mBinding.textContent.setSelected(false);

    }

    private void initAction() {
        mBinding.clearContentButton.setOnClickListener(this);
        mBinding.btnScrollRtl.setOnClickListener(this);
        mBinding.btnBlink.setOnClickListener(this);
        mBinding.btnBgColor.setOnClickListener(this);
        mBinding.btnDes.setOnClickListener(this);
        mBinding.btnHdLed.setOnClickListener(this);
        mBinding.btnHelp.setOnClickListener(this);
        mBinding.btnScrollLtr.setOnClickListener(this);
        mBinding.btnTextColor.setOnClickListener(this);
        mBinding.btnPause.setOnClickListener(this);
        mBinding.btnInc.setOnClickListener(this);
        mBinding.btnPlay.setOnClickListener(this);
        setUiOnFocusEditText(mBinding.edtContent, mBinding.clearContentButton);
        mBinding.edtContent.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            hideSoftKeyboard();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        mBinding.sbTextSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long speed = 4500 - seekBar.getProgress();
                mBinding.textContent.setRndDuration((int) speed);
                mSharedPrefs.put(SharedPrefsKey.PREF_TEXT_SPEED, speed);
            }
        });
    }

    public void setUiOnFocusEditText(final EditText editText, final ImageButton imageButton) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!TextUtils.isEmpty(editText.getText())) {
                        imageButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    imageButton.setVisibility(View.GONE);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    imageButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    imageButton.setVisibility(View.VISIBLE);
                    mTextLed.setContent(editText.getText().toString());
                } else {
                    imageButton.setVisibility(View.GONE);
                    mTextLed.setContent("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void showLeftToRight() {
        mTextLed.setRightToLeft(false);
        mTextLed.setRunning(true);
        mBinding.textContent.pauseScroll();
        mBinding.textContent.resumeScrollRight();
    }

    public void showRightToLeft() {
        mTextLed.setRightToLeft(true);
        mTextLed.setRunning(true);
        mBinding.textContent.pauseScroll();
        mBinding.textContent.resumeScroll();
    }

    public void setRun(boolean isRun) {
        if (isRun) {
            mBinding.textContent.pauseScroll();
        } else {
            if (mBinding.textContent.isRTL()) {
                mBinding.textContent.resumeScroll();
            } else {
                mBinding.textContent.resumeScrollRight();
            }
        }
        mTextLed.setRunning(!isRun);
    }

    public void increaseTextSize() {
        int size = mTextLed.getSize();
        if (size >= 155) {
            return;
        }
        size += 10;
        mTextLed.setSize(size);
        mSharedPrefs.put(SharedPrefsKey.PREF_TEXT_SIZE, size);
    }

    public void decreaseTextSize() {
        int size = mTextLed.getSize();
        if (size < 65) {
            return;
        }
        size -= 10;
        mTextLed.setSize(size);
        mSharedPrefs.put(SharedPrefsKey.PREF_TEXT_SIZE, size);
    }

    public void setColorOfText(int color) {

    }

    public void setColorOfBackground(int color) {

    }

    public void setBlink(boolean isBlinking) {
        boolean blinking = !isBlinking;
        if (blinking) {
            mBinding.textContent.addAnimationBlinking();
            mBinding.textContent.startAnimation(mBinding.textContent.getAnimationSet());
            mSharedPrefs.put(SharedPrefsKey.PREF_IS_BLINK, true);
        } else {
            mBinding.textContent.clearAnimation();
            mSharedPrefs.put(SharedPrefsKey.PREF_IS_BLINK, false);
        }
        mTextLed.setBlinking(blinking);
    }

    public void setStyleShow(boolean isLed) {
        if (isLed) {
            mSharedPrefs.put(SharedPrefsKey.PREF_STYLE_LED, false);
        } else {
            mSharedPrefs.put(SharedPrefsKey.PREF_STYLE_LED, true);
        }
        mTextLed.setLed(!isLed);
    }

    public void help() {

    }

    public void play() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard();
        return true;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        mBinding.edtContent.clearFocus();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear_content_button:
                mBinding.edtContent.setText("");
                break;
            case R.id.btn_scroll_rtl:
                showRightToLeft();
                break;
            case R.id.btn_scroll_ltr:
                showLeftToRight();
                break;
            case R.id.btn_pause:
                setRun(mTextLed.isRunning());
                break;
            case R.id.btn_des:
                decreaseTextSize();
                break;
            case R.id.btn_inc:
                increaseTextSize();
                break;
            case R.id.btn_text_color:
                setColorOfText(0);
                break;
            case R.id.btn_bg_color:
                setColorOfBackground(0);
                break;
            case R.id.btn_blink:
                setBlink(mTextLed.isBlinking());
                break;
            case R.id.btn_hd_led:
                setStyleShow(mTextLed.isLed());
                break;
            case R.id.btn_help:
                help();
                break;

            case R.id.btn_play:
                play();
                break;

        }
    }

    @Override
    protected void onStop() {
        mSharedPrefs.put(SharedPrefsKey.PREF_CONTENT, mTextLed.getContent());
        super.onStop();
    }
}
