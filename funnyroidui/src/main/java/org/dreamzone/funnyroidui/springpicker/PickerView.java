package org.dreamzone.funnyroidui.springpicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import org.dreamzone.funnyroidui.R;

/**
 * @author bohe
 * @ClassName: PickerView
 * @Description: 自定义的可放缩的圆形PickerView，可显示的数字，可以扩大点击区域
 * @date 2016/7/12 23:25
 */
public class PickerView extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * View的默认颜色
     */
    private int normalColor;

    /**
     * View的选中颜色
     */
    private int selectedColor;

    /**
     * 中间文字的颜色
     */
    private int textNormalColor;

    /**
     * 选中文字的颜色
     */
    private int textSelectedColor;

    /**
     * 中间文字
     */
    private String numberText;

    /**
     * 中间文字的大小
     */
    private float numberTextSize;

    /**
     * 默认半径
     */
    private float radiusMin;

    /**
     * 放大后的最大半径
     */
    private float radiusMax;

    /**
     * 控件的最大size，控制点击区域，正方形的边长
     */
    private int maxSize;

    private int mAnimCount = 0;
    private float center;
    private float ratio = 1.f;// 选中与normal时大小的倍率
    private final int ANIMATION_COUNT_DILATE = 12; // 放大时动画次数
    private boolean isDilating = false;
    private float radiusNow;

    private int styleType = 0;// 0 normal ;1 selected


    public PickerView(Context context) {
        this(context, null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PickerView);

        //获取自定义属性和默认值
        normalColor = mTypedArray.getColor(R.styleable.PickerView_normalColor, Color.WHITE);
        selectedColor = mTypedArray.getColor(R.styleable.PickerView_selectedColor, Color.RED);
        textNormalColor = mTypedArray.getColor(R.styleable.PickerView_textNormalColor, Color.BLACK);
        textSelectedColor = mTypedArray.getColor(R.styleable.PickerView_textSelectedColor, Color.WHITE);
        numberText = mTypedArray.getString(R.styleable.PickerView_numberText);
        numberTextSize = mTypedArray.getDimensionPixelSize(R.styleable.PickerView_numberTextSize, 14);
        radiusMin = mTypedArray.getDimensionPixelSize(R.styleable.PickerView_radiusMin, 13);
        radiusMax = mTypedArray.getDimensionPixelSize(R.styleable.PickerView_radiusMax, 17);
        maxSize = mTypedArray.getDimensionPixelSize(R.styleable.PickerView_maxSize, 37);
        mTypedArray.recycle();

        center = maxSize * 0.5f;
        if (radiusMin != 0) {
            ratio = radiusMax / radiusMin;
        }
        if (TextUtils.isEmpty(numberText)) {
            numberText = "0";
        }
        radiusNow = radiusMin;
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackgroundRect(canvas);
        if (isDilating) {
            // 如果动画没进行完就设置为0，则停止动画，恢复0的状态
            if (styleType == 0) {
                drawNormalCircle(canvas, radiusMin);
                drawNormalNumberText(canvas);
                isDilating = false;
                return;
            }
            radiusNow = radiusMin + (radiusMax - radiusMin) / ANIMATION_COUNT_DILATE * mAnimCount;
            if (mAnimCount >= ANIMATION_COUNT_DILATE) {
                drawSelectedCircle(canvas, radiusNow);
                drawSelectedNumberText(canvas);
                isDilating = false;
            }
            if (isDilating && (radiusNow < radiusMax) && mAnimCount < ANIMATION_COUNT_DILATE) {
                drawSelectedCircle(canvas, radiusNow);
                drawSelectedNumberText(canvas);
                postInvalidate();
                mAnimCount++;
            }
        } else {
            // no scaling
            if (styleType == 0) {
                drawNormalCircle(canvas, radiusMin);
                drawNormalNumberText(canvas);
            }

            if (styleType == 1) {
                drawSelectedCircle(canvas, radiusMax);
                drawSelectedNumberText(canvas);
            }
        }


    }

    /**
     * 绘制整个view的背景，透明
     *
     * @param canvas
     */
    private void drawBackgroundRect(Canvas canvas) {
        // 画最底层的背景
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);  //消除锯齿
        Rect rect = new Rect(0, 0, maxSize, maxSize);
        canvas.drawRect(rect, paint);
    }


    /**
     * 绘制normal状态的圆形
     * @param canvas
     * @param radius
     */
    private void drawNormalCircle(Canvas canvas, float radius) {
        paint.setColor(normalColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(center, center, radius, paint);
    }


    /**
     * 绘制selected状态的圆形
     * @param canvas
     * @param radius
     */
    private void drawSelectedCircle(Canvas canvas, float radius) {
        paint.setColor(selectedColor);
        paint.setStyle(Paint.Style.FILL); //设置空心
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(center, center, radius, paint); //画出圆环
    }

    /**
     * 绘制normal状态的显示文字
     * @param canvas
     */
    private void drawNormalNumberText(Canvas canvas) {
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textNormalColor);
        paint.setTextSize(numberTextSize);
        paint.setTypeface(Typeface.DEFAULT); //设置字体
        float textWidth = paint.measureText(numberText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        paint.setAntiAlias(true);  //消除锯齿
        // 字体垂直居中
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float textBaseLine = center - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2;
        //画出进度百分比
        canvas.drawText(numberText, center - textWidth / 2, textBaseLine, paint);
    }


    /**
     * 绘制selected状态的显示文字
     * @param canvas
     */
    private void drawSelectedNumberText(Canvas canvas) {
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textSelectedColor);
        paint.setTextSize(numberTextSize * ratio);
        paint.setTypeface(Typeface.DEFAULT); //设置字体
        float textWidth = paint.measureText(numberText);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        paint.setAntiAlias(true);  //消除锯齿
        // 字体垂直居中
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float textBaseLine = center - fontMetrics.descent + (fontMetrics.descent - fontMetrics.ascent) / 2;
        //画出进度百分比
        canvas.drawText(numberText, center - textWidth / 2, textBaseLine, paint);
    }


    /**
     * 动态膨胀
     */
    public void doDilate() {
        // 如果膨胀状态则不进行,跟setStyle有先后问题，要先用动画再设置属性
        if(this.styleType == 1 || isDilating){
            return;
        }
        // radius 从radiusMin增长到radiusMax
        mAnimCount = 0;
        isDilating = true;
        // dilate后将style设置为1
        this.styleType = 1;
        postInvalidate();
    }


    /**
     * 无动画绘制View显示样式，初始为0 normal
     * @param styleType 0 normal，1 selected
     */
    public void setStyleType(int styleType) {
        if(this.styleType == styleType){
            return;
        }
        this.styleType = styleType;
        postInvalidate();
    }


}
