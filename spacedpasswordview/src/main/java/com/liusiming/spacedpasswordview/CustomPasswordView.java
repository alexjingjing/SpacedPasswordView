package com.liusiming.spacedpasswordview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liusiming.spacedpasswordview.imebugfixer.ImeDelBugFixedEditText;


/**
 * Created by liusiming on 16/8/30.
 */

// TODO 1、完成空格自定义宽度 2、完成光标 3、完成光标所在位置颜色自定义
public class CustomPasswordView extends LinearLayout implements PasswordView {

    private static final int DEFAULT_PASSWORDLENGTH = 6;
    private static final int DEFAULT_TEXTSIZE = 16;
    private static final String DEFAULT_TRANSFORMATION = "●";
    private static final int DEFAULT_LINECOLOR = 0xaa888888;
    private static final int DEFAULT_GRIDCOLOR = 0xffffffff;
    private static final int DEFAULT_BLANKCOLOR = 0xffffffff;

    private ColorStateList mTextColor;
    private int mTextSize = DEFAULT_TEXTSIZE;
    private int mLineWidth;
    private int mLineColor;
    private int mGridColor;

    // lsm自定义部分
    // 空格宽度
    private int mBlankWidth;
    private float mBlankWeight;
    private boolean needBlank;
    private int mBlankColor;

    private Drawable mFoucousedDrable;

    private Drawable mLineDrawable;
    private Drawable mOuterLineDrawable;
    private int mPasswordLength;
    private String mPasswordTransformation;
    private int mPasswordType;

    private String[] mPasswordArr;
    private TextView[] mViewArr;

    private ImeDelBugFixedEditText mInputView;
    private OnPasswordChangedListener mListener;
    private PasswordTransformationMethod mTransformationMethod;

    public CustomPasswordView(Context context) {
        super(context);
        initViews(context);
        init(context, null, 0);
    }

    public CustomPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CustomPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomPasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomPasswordView, defStyleAttr, 0);

        mTextColor = ta.getColorStateList(R.styleable.CustomPasswordView_cpvTextColor);
        if (mTextColor == null)
            mTextColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        int textSize = ta.getDimensionPixelSize(R.styleable.CustomPasswordView_cpvTextSize, -1);
        if (textSize != -1) {
            this.mTextSize = Util.px2sp(context, textSize);
        }

        // lsm custom
        needBlank = ta.getBoolean(R.styleable.CustomPasswordView_cpvNeedSpace, false);
        mLineWidth = (int) ta.getDimension(R.styleable.CustomPasswordView_cpvLineWidth, Util.dp2px(getContext(), 1));
        mLineColor = ta.getColor(R.styleable.CustomPasswordView_cpvLineColor, DEFAULT_LINECOLOR);
        mGridColor = ta.getColor(R.styleable.CustomPasswordView_cpvGridColor, DEFAULT_GRIDCOLOR);
        mBlankColor = ta.getColor(R.styleable.CustomPasswordView_cpvSpaceColor, DEFAULT_BLANKCOLOR);
        mLineDrawable = ta.getDrawable(R.styleable.CustomPasswordView_cpvLineColor);
        if (mLineDrawable == null)
            mLineDrawable = new ColorDrawable(mLineColor);
        mOuterLineDrawable = generateBackgroundDrawable();

        // 输入框选中的样式
        GradientDrawable drawable = new GradientDrawable();
        // 描边色和底色
        drawable.setColor(mGridColor);
        drawable.setStroke(mLineWidth, mTextColor.getDefaultColor());
        mFoucousedDrable = drawable;

        mPasswordLength = ta.getInt(R.styleable.CustomPasswordView_cpvPasswordLength, DEFAULT_PASSWORDLENGTH);
        mPasswordTransformation = ta.getString(R.styleable.CustomPasswordView_cpvPasswordTransformation);
        if (TextUtils.isEmpty(mPasswordTransformation))
            mPasswordTransformation = DEFAULT_TRANSFORMATION;

        mPasswordType = ta.getInt(R.styleable.CustomPasswordView_cpvPasswordType, 0);

        ta.recycle();

        mPasswordArr = new String[mPasswordLength];
        mViewArr = new TextView[mPasswordLength];
    }

    private void initViews(Context context) {
        setShowDividers(SHOW_DIVIDER_NONE);
        setOrientation(HORIZONTAL);

        mTransformationMethod = new CustomPasswordTransformationMethod(mPasswordTransformation);
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.gridpasswordview, this);

        mInputView = (ImeDelBugFixedEditText) findViewById(R.id.inputView);
        mInputView.setMaxEms(mPasswordLength);
        mInputView.addTextChangedListener(textWatcher);
        mInputView.setDelKeyEventListener(onDelKeyEventListener);
        setCustomAttr(mInputView);

        mViewArr[0] = mInputView;
        mViewArr[0].setBackgroundDrawable(mFoucousedDrable);

        int index = 1;
        while (index < mPasswordLength) {

            // lsm Custom
            // 密码输入框之间是否需要空格
            if (needBlank) {
                TextView blank = (TextView) inflater.inflate(R.layout.textview, null);
                // TODO 现在是默认值，后期需要继续修改可以自定义
                LayoutParams blankParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.25f);
                blank.setBackgroundColor(mBlankColor);
                addView(blank, blankParams);
            }

            TextView textView = (TextView) inflater.inflate(R.layout.textview, null);
            setCustomAttr(textView);
            LayoutParams textViewParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
            addView(textView, textViewParams);

            mViewArr[index] = textView;
            mViewArr[index].setBackgroundDrawable(mOuterLineDrawable);
            index++;
        }

        setOnClickListener(mOnClickListener);
    }

    private void setCustomAttr(TextView view) {
        if (mTextColor != null)
            view.setTextColor(mTextColor);
        view.setTextSize(mTextSize);

        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        switch (mPasswordType) {

            case 1:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;

            case 2:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;

            case 3:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
        }
        view.setInputType(inputType);
        view.setTransformationMethod(mTransformationMethod);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            forceInputViewGetFocus();
        }
    };

    public TextView[] getViewArr() {
        return mViewArr;
    }

    public void setViewArr(TextView[] mViewArr) {
        this.mViewArr = mViewArr;
    }

    private GradientDrawable generateBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        // 描边色和底色
        drawable.setColor(mGridColor);
        drawable.setStroke(mLineWidth, mLineColor);
        return drawable;
    }

    private void forceInputViewGetFocus() {
        mInputView.setFocusable(true);
        mInputView.setFocusableInTouchMode(true);
        mInputView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mInputView, InputMethodManager.SHOW_IMPLICIT);
    }

    private ImeDelBugFixedEditText.OnDelKeyEventListener onDelKeyEventListener = new ImeDelBugFixedEditText.OnDelKeyEventListener() {

        @Override
        public void onDeleteClick() {
            for (int i = mPasswordArr.length - 1; i >= 0; i--) {
                if (mPasswordArr[i] != null) {
                    mPasswordArr[i] = null;
                    mViewArr[i].setText(null);
                    notifyTextChanged();
                    break;
                } else {
                    mViewArr[i].setText(null);
                }
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null) {
                return;
            }

            String newStr = s.toString();
            if (newStr.length() == 1) {
                mPasswordArr[0] = newStr;
                notifyTextChanged();
                mViewArr[0].setBackgroundDrawable(mOuterLineDrawable);
                mViewArr[1].setBackgroundDrawable(mFoucousedDrable);
            } else if (newStr.length() == 2) {
                String newNum = newStr.substring(1);
                for (int i = 0; i < mPasswordArr.length; i++) {
                    if (mPasswordArr[i] == null) {
                        mPasswordArr[i] = newNum;
                        mViewArr[i].setText(newNum);
                        notifyTextChanged();
                        break;
                    }
                }
                mInputView.removeTextChangedListener(this);
                mInputView.setText(mPasswordArr[0]);
                if (mInputView.getText().length() >= 1) {
                    mInputView.setSelection(1);
                }
                mInputView.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Deprecated
    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                onDelKeyEventListener.onDeleteClick();
                return true;
            }
            return false;
        }
    };

    private void notifyTextChanged() {

        String currentPsw = getPassWord();
        // 根据密码长度设置边框
        for (int i = 0; i < mViewArr.length; i++) {
            if (i == currentPsw.length()) {
                mViewArr[i].setBackgroundDrawable(mFoucousedDrable);
            } else {
                mViewArr[i].setBackgroundDrawable(mOuterLineDrawable);
            }
        }
        if (mListener == null)
            return;

        mListener.onTextChanged(currentPsw);

        if (currentPsw.length() == mPasswordLength)
            mListener.onInputFinish(currentPsw);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putStringArray("passwordArr", mPasswordArr);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mPasswordArr = bundle.getStringArray("passwordArr");
            state = bundle.getParcelable("instanceState");
            mInputView.removeTextChangedListener(textWatcher);
            setPassword(getPassWord());
            mInputView.addTextChangedListener(textWatcher);
        }
        super.onRestoreInstanceState(state);
    }

    //TODO
    //@Override
    private void setError(String error) {
        mInputView.setError(error);
    }

    /**
     * Return the text the PasswordView is displaying.
     */
    @Override
    public String getPassWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mPasswordArr.length; i++) {
            if (mPasswordArr[i] != null)
                sb.append(mPasswordArr[i]);
        }
        return sb.toString();
    }

    /**
     * Clear the passwrod the PasswordView is displaying.
     */
    @Override
    public void clearPassword() {
        for (int i = 0; i < mPasswordArr.length; i++) {
            mPasswordArr[i] = null;
            mViewArr[i].setText(null);
        }
    }

    /**
     * Sets the string value of the PasswordView.
     */
    @Override
    public void setPassword(String password) {
        clearPassword();

        if (TextUtils.isEmpty(password))
            return;

        char[] pswArr = password.toCharArray();
        for (int i = 0; i < pswArr.length; i++) {
            if (i < mPasswordArr.length) {
                mPasswordArr[i] = pswArr[i] + "";
                mViewArr[i].setText(mPasswordArr[i]);
            }
        }
    }

    /**
     * Set the enabled state of this view.
     */
    @Override
    public void setPasswordVisibility(boolean visible) {
        for (TextView textView : mViewArr) {
            textView.setTransformationMethod(visible ? null : mTransformationMethod);
            if (textView instanceof EditText) {
                EditText et = (EditText) textView;
                et.setSelection(et.getText().length());
            }
        }
    }

    /**
     * Toggle the enabled state of this view.
     */
    @Override
    public void togglePasswordVisibility() {
        boolean currentVisible = getPassWordVisibility();
        setPasswordVisibility(!currentVisible);
    }

    /**
     * Get the visibility of this view.
     */
    private boolean getPassWordVisibility() {
        return mViewArr[0].getTransformationMethod() == null;
    }

    /**
     * Register a callback to be invoked when password changed.
     */
    @Override
    public void setOnPasswordChangedListener(OnPasswordChangedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setPasswordType(PasswordType passwordType) {
        boolean visible = getPassWordVisibility();
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        switch (passwordType) {

            case TEXT:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;

            case TEXTVISIBLE:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;

            case TEXTWEB:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
        }

        for (TextView textView : mViewArr)
            textView.setInputType(inputType);

        setPasswordVisibility(visible);
    }

    @Override
    public void setBackground(Drawable background) {
    }

    @Override
    public void setBackgroundColor(int color) {
    }

    @Override
    public void setBackgroundResource(int resid) {
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
    }


    /**
     * Interface definition for a callback to be invoked when the password changed or is at the maximum length.
     */
    public interface OnPasswordChangedListener {

        /**
         * Invoked when the password changed.
         *
         * @param psw new text
         */
        void onTextChanged(String psw);

        /**
         * Invoked when the password is at the maximum length.
         *
         * @param psw complete text
         */
        void onInputFinish(String psw);

    }
}
