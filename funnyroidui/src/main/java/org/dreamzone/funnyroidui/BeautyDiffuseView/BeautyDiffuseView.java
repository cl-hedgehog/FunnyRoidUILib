package org.dreamzone.funnyroidui.beautydiffuseview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import org.dreamzone.funnyroidui.R;

import java.util.Random;

/**
 * @author bohe
 * @ClassName: BeautyDiffuseView
 * @Description 带扩散动画的View蒙层，动画结束后隐藏
 * @date 2016/7/8 11:40
 */
public class BeautyDiffuseView extends RelativeLayout {
    private Context mContext;
    private View viewLeft;
    private View viewRight;

    private Random rand;
    private Interpolator dce = new DecelerateInterpolator();//减速
    // 在init中初始化
    private Drawable drawable;
    private int dHeight;
    private int dWidth;
    private LayoutParams layoutParams;
    private PointF bezierStart;
    private PointF bezierEnd;

    private int mWidth;
    private int mHeight;
    private final int ANIM_DURATION = 1200;

    public BeautyDiffuseView(Context context) {
        this(context, null, 0);
    }

    public BeautyDiffuseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public BeautyDiffuseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_diffuse_view, this, true);
        viewLeft = view.findViewById(R.id.iv_left);
        viewRight = view.findViewById(R.id.iv_right);
        drawable = getResources().getDrawable(R.drawable.beauty_ok_light);
        rand = new Random();
        dHeight = drawable.getIntrinsicHeight() * 3 / 2;
        dWidth = drawable.getIntrinsicWidth() * 3 / 2;

        layoutParams = new LayoutParams(dWidth, dHeight);
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);
        layoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }


    /**s
     * 调用该方法开始动画
     */
    public void startAnimation() {
        viewLeft.setVisibility(VISIBLE);
        viewRight.setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(viewLeft, "translationX", 0.0f, mWidth / 2),
                //ObjectAnimator.ofFloat(viewLeft, "translationY", 0.0f, 0.f - mHeight / 2),
                ObjectAnimator.ofFloat(viewLeft, "alpha", 0.8f, 0.1f), ObjectAnimator.ofFloat(viewLeft, "scaleX", 0, 9.0f), ObjectAnimator.ofFloat
                        (viewLeft, "scaleY", 0, 9.0f),

                ObjectAnimator.ofFloat(viewRight, "translationX", 0.0f, 0.f - mWidth / 2), ObjectAnimator.ofFloat(viewRight, "alpha", 1.0f, 0.2f),
                ObjectAnimator.ofFloat(viewRight, "scaleX", 0.2f, 2.0f), ObjectAnimator.ofFloat(viewRight, "scaleY", 0.2f, 2.0f));
        set.setDuration(ANIM_DURATION).start();
        set.setInterpolator(dce);
        for (int i = 0; i < 5; i++) {
            addDecorate(mContext);
        }
    }


    private void addDecorate(Context context) {
        // 添加drawable
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(drawable);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);
        setupStartAnim(imageView).start();
        ValueAnimator valueAnimator = setupRunAnim(imageView);
        valueAnimator.addListener(new AnimEndListener(imageView));
        valueAnimator.start();
    }

    private AnimatorSet setupStartAnim(View tag) {
        AnimatorSet enter = new AnimatorSet();
        enter.playTogether(ObjectAnimator.ofFloat(tag, "alpha", 0.2f, 1.0f), ObjectAnimator.ofFloat(tag, "scaleX", 0.2f, 2.0f), ObjectAnimator
                .ofFloat(tag, "scaleY", 0.2f, 2.0f));
        enter.setDuration(ANIM_DURATION);
        enter.setTarget(tag);
        return enter;
    }

    private ValueAnimator setupRunAnim(View tag) {
        //初始化一个BezierEvaluator
        bezierStart = new PointF(0, mHeight * 0.8f);
        bezierEnd = new PointF(0.75f * mWidth, mHeight * 0.5f);
        BezierEvaluator evaluator = new BezierEvaluator(bezierStart, bezierEnd);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, getPointF(1), getPointF(2));//随机
        animator.addUpdateListener(new BezierListener(tag));
        animator.setInterpolator(dce);
        animator.setTarget(tag);
        animator.setDuration(ANIM_DURATION);
        return animator;
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
            viewLeft.setVisibility(GONE);
            viewRight.setVisibility(GONE);
        }
    }

    /**
     * cubic贝塞尔方程
     */
    private class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF start;//起点
        private PointF end;//终点

        private PointF pointF;

        public BezierEvaluator(PointF start, PointF end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public PointF evaluate(float time, PointF p1, PointF p2) {
            float timeLeft = 1.0f - time;
            pointF = new PointF();//结果
            PointF point1 = p1;// 控制点1
            PointF point2 = p2;// 控制点2
            pointF.x = timeLeft * timeLeft * timeLeft * (start.x) + 3 * timeLeft * timeLeft * time * (point1.x) + 3 * timeLeft * time * time *
                    (point2.x) + time * time * time * (end.x);

            pointF.y = timeLeft * timeLeft * timeLeft * (start.y) + 3 * timeLeft * timeLeft * time * (point1.y) + 3 * timeLeft * time * time *
                    (point2.y) + time * time * time * (end.y);
            return pointF;
        }
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            target.setAlpha( 1 - animation.getAnimatedFraction());
//            ViewHelper.setX(target, pointF.x);
//            ViewHelper.setY(target, pointF.y);
//            ViewHelper.setAlpha(target, 1 - animation.getAnimatedFraction());
        }
    }

    /**
     * 获取中间的两个点
     *
     * @param scale
     */
    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = rand.nextInt((mWidth - 100));
        pointF.y = rand.nextInt((mHeight - 100)) / scale;
        return pointF;
    }

}
