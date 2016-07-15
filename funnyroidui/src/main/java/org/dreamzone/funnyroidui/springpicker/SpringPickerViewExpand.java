package org.dreamzone.funnyroidui.springpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.dreamzone.funnyroidui.R;


/**
 * @author bohe
 * @ClassName: SpringPickerViewExpand
 * @Description: 使用自定义PickerView，PickerView点击区域可控制
 * @date 2016/7/6 13:38
 * @version update 2016/7/13 检查动画是否结束，结束后再改变收起展开状态
 *
 */
public class SpringPickerViewExpand extends RelativeLayout implements View.OnClickListener {
    private final  String TAG = SpringPickerViewExpand.class.getSimpleName();

    private Context mContext;

    private RelativeLayout rlRoot;
    private RoundProgressBar pbPicker;
    private ImageView ivCover;
    private PickerView[] mItemViews;
    private int[] mTranslateOffset;

    private PickerStatus mCurrentState;
    private PickerLevel mCurrentLevel;
    private int mCurrentProcess = 0;
    private int mPreProcess = -1;
    private boolean[] mIsScaled;

    private OnLevelPickedListener mOnLevelPickedListener;

    private OnPickerClickListener mOnPickerClickListener;

    private final int PICKER_ANIMATION_DURATION = 200;
    private final int RECOVER_STEP = 10;// max is 30

    private int pickerWidth;
    private int pickerGap;
    private int itemCount;

    private int collapseWidth;
    private int expandWidth;

    private boolean isAnimation = false;

    public SpringPickerViewExpand(Context context) {
        this(context, null);
    }

    public SpringPickerViewExpand(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringPickerViewExpand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SpringPicker);

        //获取自定义属性和默认值
        expandWidth = mTypedArray.getDimensionPixelSize(R.styleable.SpringPicker_maxWidth, 500);
        collapseWidth = mTypedArray.getDimensionPixelSize(R.styleable.SpringPicker_minWidth, 160);
        pickerWidth = mTypedArray.getDimensionPixelSize(R.styleable.SpringPicker_pickerWidth, 50);
        pickerGap = mTypedArray.getDimensionPixelSize(R.styleable.SpringPicker_pickerGap, 24);
        mTypedArray.recycle();

        mCurrentLevel = PickerLevel.LEVEL_ZERO;
        mCurrentState = PickerStatus.COLLAPSED;
        mPreProcess = -1;
        itemCount = 4;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_spring_picker_expand, this, true);
        rlRoot = (RelativeLayout) view.findViewById(R.id.rl_root);
        pbPicker = (RoundProgressBar) view.findViewById(R.id.pb_level);
        ivCover = (ImageView) view.findViewById(R.id.iv_cover);

        mItemViews = new PickerView[itemCount];
        mTranslateOffset = new int[itemCount];
        mIsScaled = new boolean[itemCount];
        mItemViews[0] = (PickerView) view.findViewById(R.id.btn_level_zero);
        mItemViews[1] = (PickerView) view.findViewById(R.id.btn_level_one);
        mItemViews[2] = (PickerView) view.findViewById(R.id.btn_level_two);
        mItemViews[3] = (PickerView) view.findViewById(R.id.btn_level_three);

        pbPicker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isAnimation){
                    return;
                }
                PickerStatus status;
                if (mCurrentState == PickerStatus.COLLAPSED) {
                    status = PickerStatus.EXPANDED;
                } else {
                    status = PickerStatus.COLLAPSED;
                }
                if (mOnPickerClickListener != null) {
                    mOnPickerClickListener.onPickerClick(status);
                }
                                updatePickerStatus(status);
            }
        });
        ivCover.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isAnimation){
                    return;
                }
                PickerStatus status;
                if (mCurrentState == PickerStatus.COLLAPSED) {
                    status = PickerStatus.EXPANDED;
                } else {
                    status = PickerStatus.COLLAPSED;
                }
                if (mOnPickerClickListener != null) {
                    mOnPickerClickListener.onPickerClick(status);
                }
                                updatePickerStatus(status);
            }
        });
        for (int i = 0; i < itemCount; i++) {
            mItemViews[i].setOnClickListener(this);
            mIsScaled[i] = false;
            mTranslateOffset[i] = (pickerWidth + pickerGap) * (i + 1) - pickerWidth;
        }
        setPickerGone();
        // 更新各个level控件的样式
        updateLevelStatus();
        setPickerSelected(mCurrentProcess);
    }

    public PickerStatus getCurrentStatus(){
        return mCurrentState;
    }

    public void setShowCover(boolean showCover) {
        if (ivCover != null) {
            ivCover.setVisibility(showCover ? View.VISIBLE : View.GONE);
        }
        if (pbPicker != null) {
            pbPicker.setVisibility(showCover ? View.GONE : View.VISIBLE);
        }
    }

    private void setPickerVisible() {
        for (int i = 0; i < itemCount; i++) {
            mItemViews[i].setVisibility(VISIBLE);
            pbPicker.setVisibility(View.VISIBLE);
            if (ivCover.getVisibility() != View.GONE) {
                ivCover.setVisibility(View.GONE);
            }
        }
    }

    private void setPickerGone() {
        for (int i = 0; i < itemCount; i++) {
            mItemViews[i].setVisibility(GONE);
        }
    }

    /**
     * 设置Picker选择后的背景和文字属性
     *
     * @param index
     */
    private void setPickerSelected(int index) {
        if (index < 0 || index > itemCount) {
            return;
        }
        for (int i = 0; i < itemCount; i++) {
            if (i == index) {
                mItemViews[i].setStyleType(1);
            } else {
                mItemViews[i].setStyleType(0);
            }
        }
    }


    public void setOnLevelPickedListener(OnLevelPickedListener onLevelPickedListener) {
        mOnLevelPickedListener = onLevelPickedListener;
    }

    public void setOnPickerClickListener(OnPickerClickListener listener) {
        mOnPickerClickListener = listener;
    }

    /**
     * 对外的level选择
     *
     * @param level                 需要重置的初始状态
     * @param isTriggerLevelChanged 是否触发状态更改回调
     */
    public void setLevel(int level, boolean isTriggerLevelChanged) {
        if (mCurrentProcess == level) {
            return;
        }
        View growthView = null;
        View recoverView = null;
        mPreProcess = -1;
        for (int i = 0; i < mIsScaled.length; i++) {
            if (mIsScaled[i]) {
                mPreProcess = i;
                break;
            }
        }
        mCurrentProcess = level;
        if (mCurrentProcess >= 0 && mCurrentProcess < itemCount) {
            growthView = mItemViews[mCurrentProcess];
        }
        if (mPreProcess >= 0 && mPreProcess < itemCount) {
            recoverView = mItemViews[mPreProcess];
        }
        startPickerAnimation(growthView, recoverView);
        pbPicker.setProgress(mCurrentProcess * RECOVER_STEP);
        setPickerSelected(mCurrentProcess);
        mCurrentLevel = PickerLevel.fromInt(mCurrentProcess);
        if (mOnLevelPickedListener != null && isTriggerLevelChanged) {
            mOnLevelPickedListener.onLevelPicked(mCurrentLevel);
        }

        for (int i = 0; i < mIsScaled.length; i++) {
            mIsScaled[i] = false;
        }
        mIsScaled[mCurrentProcess] = true;
    }

    /**
     * level选择，考虑currentProgress和preProgress
     */
    private void updateLevelStatus() {
        View growthView = null;
        View recoverView = null;
        mPreProcess = -1;
        for (int i = 0; i < mIsScaled.length; i++) {
            if (mIsScaled[i]) {
                mPreProcess = i;
                break;
            }
        }
        if (mCurrentProcess >= 0 && mCurrentProcess < itemCount) {
            growthView = mItemViews[mCurrentProcess];
        }
        if (mPreProcess >= 0 && mPreProcess < itemCount) {
            recoverView = mItemViews[mPreProcess];
        }
        startPickerAnimation(growthView, recoverView);
        pbPicker.setProgress(mCurrentProcess * RECOVER_STEP);
        for (int i = 0; i < mIsScaled.length; i++) {
            mIsScaled[i] = false;
        }
        mIsScaled[mCurrentProcess] = true;
    }

    /**
     * picker的放缩
     *
     * @param growthView
     * @param recoverView
     */
    private void startPickerAnimation(View growthView, View recoverView) {
        if(growthView != null){
            // 被选中的动画要扩大
            ((PickerView)growthView).doDilate();
        }
        if(recoverView != null){
            // 之前选中的要还原
            ((PickerView)recoverView).setStyleType(0);
        }
    }

    /**
     * 弹性展开
     */
    private void performCollapsedAnimation() {
        isAnimation = true;
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animatorLast = ObjectAnimator.ofFloat(mItemViews[3], "translationX", mTranslateOffset[3], 0.0f);
        set.playTogether(
                ObjectAnimator.ofFloat(mItemViews[0], "translationX", mTranslateOffset[0], 0.0f),
                ObjectAnimator.ofFloat(mItemViews[1], "translationX", mTranslateOffset[1], 0.0f),
                ObjectAnimator.ofFloat(mItemViews[2], "translationX", mTranslateOffset[2], 0.0f),
                animatorLast
        );
        set.setDuration(PICKER_ANIMATION_DURATION).start();
        LayoutParams layoutParams = (LayoutParams) rlRoot.getLayoutParams();
        layoutParams.width = collapseWidth;
        rlRoot.setLayoutParams(layoutParams);

        animatorLast.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimation = false;
            }
        });
    }


    /**
     * 弹性收起
     */
    private void performExpandAnimation() {
        isAnimation = true;
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(mItemViews[0], "translationX", 0.0f, mTranslateOffset[0]);
        ObjectAnimator animatorLast = ObjectAnimator.ofFloat(mItemViews[3], "alpha", 0.0f, 1.0f);
        set.playTogether(
                animator,
                ObjectAnimator.ofFloat(mItemViews[0], "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(mItemViews[1], "translationX", 0.0f, mTranslateOffset[1]),
                ObjectAnimator.ofFloat(mItemViews[1], "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(mItemViews[2], "translationX", 0.0f, mTranslateOffset[2]),
                ObjectAnimator.ofFloat(mItemViews[2], "alpha", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(mItemViews[3], "translationX", 0.0f, mTranslateOffset[3]),
                animatorLast

        );
        set.setDuration(PICKER_ANIMATION_DURATION).start();
        // 实现宽度的动态增长
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curr = (Float) animation.getAnimatedValue();
                float percent = curr / mTranslateOffset[0];
                LayoutParams layoutParams = (LayoutParams) rlRoot.getLayoutParams();
                layoutParams.width = Math.max((int) (expandWidth * percent), collapseWidth);
                rlRoot.setLayoutParams(layoutParams);
            }
        });
        animatorLast.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimation = false;
            }
        });
    }


    /**
     * 点击ProgressBar后更新View的状态，收起或者展开
     *
     * @param pickerStatus
     */
    public void updatePickerStatus(PickerStatus pickerStatus) {
        if (pickerStatus == mCurrentState) {
            return;
        }
        if (pickerStatus == PickerStatus.EXPANDED) {
            performExpandAnimation();
            setPickerVisible();
        } else {
            performCollapsedAnimation();
            setPickerGone();
        }
        mOnLevelPickedListener.onPickerStatusChanged(pickerStatus);
        mCurrentState = pickerStatus;
    }

    @Override
    public void onClick(View v) {
        mPreProcess = mCurrentProcess;
        if (v.getId() == R.id.btn_level_zero) {
            mCurrentProcess = 0;
        } else if (v.getId() == R.id.btn_level_one) {
            mCurrentProcess = 1;
        } else if (v.getId() == R.id.btn_level_two) {
            mCurrentProcess = 2;
        } else if (v.getId() == R.id.btn_level_three) {
            mCurrentProcess = 3;
        }
        // 重复选择，不处理
        if (mCurrentProcess == mPreProcess) {
            return;
        }
        mCurrentLevel = PickerLevel.fromInt(mCurrentProcess);
        // update picker status
        if (mOnLevelPickedListener != null) {
            mOnLevelPickedListener.onLevelPicked(mCurrentLevel);
        }
        // update progress status, Picker的限制，必须采用这个顺序
        updateLevelStatus();
        setPickerSelected(mCurrentProcess);
    }

    public enum PickerStatus {

        COLLAPSED(0),
        EXPANDED(1);
        private int asInt;

        PickerStatus(int i) {
            this.asInt = i;
        }

        static PickerStatus fromInt(int i) {
            switch (i) {
                case 0:
                    return COLLAPSED;
                default:
                case 1:
                    return EXPANDED;
            }
        }

        public int toInt() {
            return asInt;
        }
    }

    public enum PickerLevel {

        LEVEL_ZERO(0),
        LEVEL_ONE(1),
        LEVEL_TWO(2),
        LEVEL_THREE(3);
        private int asInt;


        PickerLevel(int i) {
            this.asInt = i;
        }

        static PickerLevel fromInt(int i) {
            switch (i) {
                case 1:
                    return LEVEL_ONE;
                case 2:
                    return LEVEL_TWO;
                case 3:
                    return LEVEL_THREE;
                default:
                case 0:
                    return LEVEL_ZERO;
            }
        }
    }


    public interface OnLevelPickedListener {
        /**
         * 级别选择更换
         *
         * @param pickerLevel
         */
        void onLevelPicked(PickerLevel pickerLevel);

        /**
         * 级别选择器展开或者收起
         *
         * @param pickerStatus
         */
        void onPickerStatusChanged(PickerStatus pickerStatus);
    }

    public interface OnPickerClickListener {
        void onPickerClick(PickerStatus status);
    }
}
