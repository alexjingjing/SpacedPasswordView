package com.liusiming.spacedpasswordview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by liusiming on 16/8/30.
 */
public class SpacedPasswordView extends FrameLayout {

    private static final int DEFAULT_PASSWORDLENGTH = 6;
    private static final int DEFAULT_TEXTSIZE = 16;

    private boolean mNeedCursor;

    private ColorStateList mTextColor;
    private int mTextSize;
    private CustomPasswordView mCustomPasswordView;

    private int mPasswordLength;

    private int mMaxLength;

    private ImageView[] mCursorArr;
    private ObjectAnimator[] mAnimatorArr;

    private CustomListener mListener;

    public SpacedPasswordView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SpacedPasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SpacedPasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpacedPasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    /**
     * 调用初始化函数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mCustomPasswordView = new CustomPasswordView(context, attrs, defStyleAttr);
        mCustomPasswordView.setOnPasswordChangedListener(new CustomPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                if (mNeedCursor) {
                    for (int i = 0; i < mCursorArr.length; i++) {
                        if (i != psw.length()) {
                            mCursorArr[i].setVisibility(INVISIBLE);
                        } else {
                            mCursorArr[i].setVisibility(VISIBLE);
                        }
                    }
                }
                if (mListener != null) {
                    mListener.onTextChanged(psw);
                }
            }

            @Override
            public void onInputFinish(String psw) {
                if (mNeedCursor) {
                    for (ImageView iv : mCursorArr
                            ) {
                        iv.setVisibility(INVISIBLE);
                    }
                }

                if (mListener != null) {
                    mListener.onInputFinish(psw);
                }
            }
        });
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    /**
     * 获取相应的自定义参数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomPasswordView, defStyleAttr, 0);

        mTextColor = ta.getColorStateList(R.styleable.CustomPasswordView_cpvTextColor);
        if (mTextColor == null)
            mTextColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));

        mPasswordLength = ta.getInt(R.styleable.CustomPasswordView_cpvPasswordLength, DEFAULT_PASSWORDLENGTH);
        mNeedCursor = ta.getBoolean(R.styleable.CustomPasswordView_cpvNeedCursor, true);

        ta.recycle();

        mCursorArr = new ImageView[mPasswordLength];
        mAnimatorArr = new ObjectAnimator[mPasswordLength];
    }

    private void initViews(Context context) {
        inflaterViews(context);

        // 需要新建光标视图
        if (mNeedCursor) {
            for (int i = 0; i < mPasswordLength; i++) {
                ImageView cursorIV = new ImageView(context);
                mCursorArr[i] = cursorIV;
                PropertyValuesHolder p = PropertyValuesHolder.ofFloat("alpha", 0, 1.0f);
                ObjectAnimator a = ObjectAnimator.ofPropertyValuesHolder(mCursorArr[i], p).setDuration(500);
                // a.setInterpolator(new DecelerateInterpolator());
                a.setRepeatCount(ValueAnimator.INFINITE);
                a.setRepeatMode(ValueAnimator.REVERSE);
                mAnimatorArr[i] = a;
            }
        }
    }

    /**
     * 注入视图
     *
     * @param context
     */
    private void inflaterViews(Context context) {
        addView(mCustomPasswordView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mNeedCursor) {
            if (changed) {
                TextView[] tvArr = mCustomPasswordView.getViewArr();
                for (int i = 0; i < tvArr.length; i++) {
                    TextView tv = tvArr[i];
                    ImageView cursorIV = mCursorArr[i];
                    cursorIV.setBackgroundColor(mTextColor.getDefaultColor());
                    LayoutParams layoutParams = new LayoutParams(6, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.leftMargin = (tv.getLeft() + tv.getWidth() / 2);
                    layoutParams.topMargin = 10;
                    layoutParams.bottomMargin = 10;
                    addView(cursorIV, layoutParams);
                    cursorIV.setVisibility(INVISIBLE);
                    mAnimatorArr[i].start();
                }
                // 初始化后第一个光标闪烁
                mCursorArr[0].setVisibility(VISIBLE);
            }
        }
    }

    public void setPasswordVisibility(boolean visible) {
        mCustomPasswordView.setPasswordVisibility(visible);
    }

    public String getPassword() {
        return mCustomPasswordView.getPassWord();
    }

    public void clearPassword() {mCustomPasswordView.clearPassword();}

    public void setPasswordListener(CustomListener mListener) {
        this.mListener = mListener;
    }

    public interface CustomListener{
        void onTextChanged(String psw);

        void onInputFinish(String psw);
    }
}
