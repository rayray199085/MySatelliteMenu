package com.project.stephencao.mysatellitemenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import com.project.stephencao.mysatellitemenu.R;

public class SatelliteMenu extends ViewGroup implements View.OnClickListener {
    private static final int POS_TOP_LEFT = 0;
    private static final int POS_BOTTOM_LEFT = 1;
    private static final int POS_TOP_RIGHT = 2;
    private static final int POS_BOTTOM_RIGHT = 3;
    private Position mPosition = Position.BOTTOM_RIGHT;
    private int mRadius;
    private Status mStatus = Status.CLOSE;
    // main button
    private View mCentreButton;
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    /**
     * for sub-buttons
     */
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public enum Status {
        OPEN, CLOSE
    }

    // menu position
    public enum Position {
        TOP_LEFT, BOTTOM_LEFT, TOP_RIGHT, BOTTOM_RIGHT
    }

    public SatelliteMenu(Context context) {
        this(context, null);
    }

    public SatelliteMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatelliteMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // get user-defined values
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SatelliteMenu);
        int pos = array.getInt(R.styleable.SatelliteMenu_position, POS_BOTTOM_RIGHT);
        switch (pos) {
            case POS_TOP_LEFT: {
                mPosition = Position.TOP_LEFT;
                break;
            }
            case POS_BOTTOM_LEFT: {
                mPosition = Position.BOTTOM_LEFT;
                break;
            }
            case POS_TOP_RIGHT: {
                mPosition = Position.TOP_RIGHT;
                break;
            }
            case POS_BOTTOM_RIGHT: {
                mPosition = Position.BOTTOM_RIGHT;
                break;
            }
        }
        mRadius = (int) array.getDimension(R.styleable.SatelliteMenu_radius,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        array.recycle();
    }

    @Override
    public void onClick(View v) {
        centreButtonRotateAnimation(v);
        toggleMenu(300);
    }

    public boolean isOpen(){
        return mStatus == Status.OPEN;
    }


    public void toggleMenu(int duration) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View child = getChildAt(i + 1);
            child.setVisibility(VISIBLE);
            int childLeft = (int) (mRadius * Math.sin(Math.toRadians((90.0 / (count - 2) * i))));
            int childTop = (int) (mRadius * Math.cos(Math.toRadians((90.0 / (count - 2) * i))));

            int xFlag = 1;
            int yFlag = 1;
            if (mPosition == Position.TOP_LEFT || mPosition == Position.BOTTOM_LEFT) {
                xFlag = -1;
            }
            if (mPosition == Position.TOP_LEFT || mPosition == Position.TOP_RIGHT) {
                yFlag = -1;
            }
            AnimationSet animationSet = new AnimationSet(true);
            RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 720.0f, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            TranslateAnimation translateAnimation = null;
            if (mStatus == Status.CLOSE) {// to open
                translateAnimation = new TranslateAnimation(xFlag * childLeft, 0, yFlag * childTop, 0);
                child.setClickable(true);
                child.setFocusable(true);
            } else {// to close
                translateAnimation = new TranslateAnimation(0f, xFlag * childLeft, 0f, yFlag
                        * childTop);
                child.setClickable(false);
                child.setFocusable(false);
            }
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mStatus == Status.CLOSE) {
                        child.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            translateAnimation.setFillAfter(true);
            rotateAnimation.setFillAfter(true);
            translateAnimation.setStartOffset(100 * i / count);
            translateAnimation.setDuration(duration);
            rotateAnimation.setDuration(duration);
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            child.startAnimation(animationSet);
            final int pos = i + 1;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onClick(v, pos);
                        menuItemAnimation(pos - 1);
                        updateStatus();
                    }
                }
            });
        }
        updateStatus();
    }

    private void menuItemAnimation(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View child = getChildAt(i + 1);
            AnimationSet animationSet = new AnimationSet(true);
            ScaleAnimation scaleAnimation = null;
            AlphaAnimation alphaAnimation = null;
            if (i == pos) {
                scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f,
                        4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            } else {
                scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            }
            scaleAnimation.setDuration(300);
            alphaAnimation.setDuration(300);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setFillAfter(true);
            child.startAnimation(animationSet);
            child.setClickable(false);
            child.setFocusable(false);
        }
    }

    private void updateStatus() {
        if (mStatus == Status.CLOSE) {
            mStatus = Status.OPEN;
        } else {
            mStatus = Status.CLOSE;
        }
    }


    private void centreButtonRotateAnimation(View v) {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        v.startAnimation(rotateAnimation);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            mCentreButton = getChildAt(0);
            mCentreButton.setOnClickListener(this);
            layoutCentreButton();
            // place satellite buttons
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                View view = getChildAt(i + 1);
                view.setVisibility(GONE);
                int childLeft = (int) (mRadius * Math.sin(Math.toRadians((90.0 / (count - 2) * i))));
                int childTop = (int) (mRadius * Math.cos(Math.toRadians((90.0 / (count - 2) * i))));
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();
                if (mPosition == Position.BOTTOM_LEFT || mPosition == Position.BOTTOM_RIGHT) {
                    childTop = getMeasuredHeight() - childHeight - childTop;
                }
                if (mPosition == Position.TOP_RIGHT || mPosition == Position.BOTTOM_RIGHT) {
                    childLeft = getMeasuredWidth() - childWidth - childLeft;
                }
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }
        }
    }

    /**
     * place the main menu button
     */
    private void layoutCentreButton() {
        int left = 0;
        int top = 0;
        int width = mCentreButton.getMeasuredWidth();
        int height = mCentreButton.getMeasuredHeight();
        switch (mPosition) {
            case TOP_LEFT: {
                left = 0;
                top = 0;
                break;
            }
            case BOTTOM_LEFT: {
                left = 0;
                top = getMeasuredHeight() - height;
                break;
            }
            case TOP_RIGHT: {
                left = getMeasuredWidth() - width;
                top = 0;
                break;
            }
            case BOTTOM_RIGHT: {
                left = getMeasuredWidth() - width;
                top = getMeasuredHeight() - height;
                break;
            }
        }
        mCentreButton.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
