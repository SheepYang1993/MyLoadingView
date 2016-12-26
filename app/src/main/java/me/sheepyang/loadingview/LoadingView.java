package me.sheepyang.loadingview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by SheepYang on 2016/12/26 22:16.
 */

public class LoadingView extends View {
    private Context mContext;
    private int mWaveColor;
    private int mBgColor;
    private int mWidth;
    private int mHeight;
    private Paint mWavePaint;
    private Paint mBgPaint;
    private int mRadius;
    private RectF mLeftRectf;
    private RectF mRightRectf;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);
        mBgColor = a.getColor(R.styleable.LoadingView_bg_color, Color.parseColor("#e6590d"));
        mWaveColor = a.getColor(R.styleable.LoadingView_wave_color, Color.parseColor("#ff7930"));
        a.recycle();
        initPaint();
    }

    private void initPaint() {
        mWavePaint = new Paint();
        mWavePaint.setColor(mWaveColor);

        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = PxUtils.dpToPx(300, mContext);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = PxUtils.dpToPx(80, mContext);
        }
        mRadius = mHeight / 2;
        if (mWidth - 2 * mRadius < 0) {
            mWidth = 2 * mRadius;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLeftRectf == null) {
            mLeftRectf = new RectF();
            mLeftRectf.left = 0;
            mLeftRectf.top = 0;
            mLeftRectf.right = 2 * mRadius;
            mLeftRectf.bottom = mHeight;
        }
        if (mRightRectf == null) {
            mRightRectf = new RectF();
            mRightRectf.left = mWidth - 2 * mRadius;
            mRightRectf.top = 0;
            mRightRectf.right = mWidth;
            mRightRectf.bottom = mHeight;
        }
        canvas.drawArc(mLeftRectf, 90, 270, true, mBgPaint);
        canvas.drawRect(mRadius, 0, mWidth - mRadius, mHeight, mBgPaint);
        canvas.drawArc(mRightRectf, 270, 180, true, mBgPaint);
        super.onDraw(canvas);
    }
}
