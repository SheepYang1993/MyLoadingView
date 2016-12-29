package me.sheepyang.loadingview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import me.sheepyang.loadingview.utils.PxUtils;

/**
 * Created by SheepYang on 2016/12/26 22:16.
 */

public class LoadingView extends View {
    public static final int WAVE_MODE_DEFAULT = 0;
    public static final int WAVE_MODE_FLOATING = 1;
    public static final int FANS_MODE_AUTO_MOVE = 0;
    public static final int FANS_MODE_PROGRESS_MOVE = 1;
    private int mWaveMode;
    private int mFansMode;
    private float mMinWaveSize;
    private float mMaxWaveSize;
    private int mMinFansSpeed;
    private int mMaxFansSpeed;
    private Context mContext;
    private int mWaveColor;
    private int mFansColor;
    private int mBgColor;
    private int mWidth;
    private int mHeight;
    private Paint mWavePaint;
    private Paint mBgPaint;
    private Paint mFansPaint;
    private int mRadius;
    private RectF mLeftRectf;
    private RectF mRightRectf;
    private int mRotate;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Path mPath;
    private Path mFansPath;
    private boolean isUp;
    private int x;
    private int y = 10;
    private float mWaveSize;// 波浪高度
    private int mFansSpeed;// 风扇速度
    private int mProgress;
    private int mMax;
    private boolean mIsFansMove;
    private Path mBladePath;
    private float mFansStrokePercent = 0.1f;// 风扇画笔线条宽度比
    private RectF mBladeRectf;
    private Path mWaveTest;
    private RectF mWaveTestRectf;

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
        mBgColor = a.getColor(R.styleable.LoadingView_bg_color, Color.parseColor("#8197ab"));
        mFansColor = a.getColor(R.styleable.LoadingView_fans_color, Color.parseColor("#e2dedc"));
        mWaveColor = a.getColor(R.styleable.LoadingView_wave_color, Color.parseColor("#9f0052"));
        mWaveSize = a.getDimension(R.styleable.LoadingView_wave_size, PxUtils.dpToPx(10, mContext));
        mMinWaveSize = a.getDimension(R.styleable.LoadingView_min_wave_size, PxUtils.dpToPx(5, mContext));
        mMaxWaveSize = a.getDimension(R.styleable.LoadingView_max_wave_size, PxUtils.dpToPx(100, mContext));
        mFansSpeed = a.getInt(R.styleable.LoadingView_fans_speed, 5);
        mMinFansSpeed = a.getInt(R.styleable.LoadingView_min_fans_speed, 1);
        mMaxFansSpeed = a.getInt(R.styleable.LoadingView_max_fans_speed, 50);
        mMax = a.getInt(R.styleable.LoadingView_max, 100);
        mIsFansMove = a.getBoolean(R.styleable.LoadingView_is_fans_move, false);
        mProgress = a.getInt(R.styleable.LoadingView_progress, 0);
        mWaveMode = a.getInt(R.styleable.LoadingView_wave_mode, WAVE_MODE_DEFAULT);
        mFansMode = a.getInt(R.styleable.LoadingView_fans_mode, FANS_MODE_AUTO_MOVE);
        a.recycle();
        init();
    }

    private void init() {
        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setFilterBitmap(true);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setXfermode(mMode);
        mWavePaint.setStrokeWidth(10);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setFilterBitmap(true);
        mBgPaint.setColor(mBgColor);

        mFansPaint = new Paint();
        mFansPaint.setAntiAlias(true);
        mFansPaint.setFilterBitmap(true);
        mFansPaint.setColor(mFansColor);
        mFansPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mFansPath = new Path();
        mBladePath = new Path();
        mWaveTest = new Path();

        mLeftRectf = new RectF();
        mRightRectf = new RectF();
        mBladeRectf = new RectF();
        mWaveTestRectf = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = PxUtils.dpToPx(40, mContext);
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = mHeight;
        }

        mRadius = mHeight / 2;
        if (mWidth - 2 * mRadius < 0) {
            mWidth = 2 * mRadius;
        }

        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888); //生成一个bitmap
        mCanvas = new Canvas(mBitmap);//将bitmp放在我们自己的画布上，实际上mCanvas.draw的时候 改变的是这个bitmap对象

        mLeftRectf.left = 0;
        mLeftRectf.top = 0;
        mLeftRectf.right = 2 * mRadius;
        mLeftRectf.bottom = mHeight;

        mRightRectf.left = mWidth - 2 * mRadius;
        mRightRectf.top = 0;
        mRightRectf.right = mWidth;
        mRightRectf.bottom = mHeight;

        mFansPath.reset();
        mBladePath.reset();
        mWaveTest.reset();

        mWaveTestRectf.left = mWidth * 0.2f;
        mWaveTestRectf.top = 0;
        mWaveTestRectf.right = mWidth * 0.5f;
        mWaveTestRectf.bottom = mHeight;
        mWaveTest.addArc(mWaveTestRectf, 270, 180);

        mFansPaint.setStrokeWidth(mRadius * mFansStrokePercent);
        mFansPath.addCircle(mRadius, mRadius, mRadius * (1 - mFansStrokePercent * 1.5f), Path.Direction.CW);// 大圆
        mFansPath.addCircle(mRadius, mRadius, mFansPaint.getStrokeWidth(), Path.Direction.CW);// 小圆

        float lineLeft = (mRadius - mFansStrokePercent - mFansPaint.getStrokeWidth()) * 0.35f;
        float lineRight = (mRadius - mFansPaint.getStrokeWidth()) * 1f;
        mBladePath.moveTo(lineLeft, mRadius);
        mBladePath.lineTo(lineRight, mRadius);

        float bladeWidth = lineRight - lineLeft;
        mBladeRectf.left = lineLeft;
        mBladeRectf.top = mRadius - bladeWidth / 2 - mFansPaint.getStrokeWidth() / 2;
        mBladeRectf.right = lineRight;
        mBladeRectf.bottom = mRadius + bladeWidth / 2 - mFansPaint.getStrokeWidth() / 2;
        mBladePath.addArc(mBladeRectf, 0, 180);

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mFansSpeed = mFansSpeed < mMinFansSpeed ? mMinFansSpeed : mFansSpeed;
        mFansSpeed = mFansSpeed > mMaxFansSpeed ? mMaxFansSpeed : mFansSpeed;

        mWaveSize = mWaveSize < mMinWaveSize ? mMinWaveSize : mWaveSize;
        mWaveSize = mWaveSize > mMaxWaveSize ? mMaxWaveSize : mWaveSize;

        x = (int) (mProgress / (float) mMax * mWidth);
        if (y > mWaveSize) {
            isUp = true;// 波浪升到顶点了
        } else if (y < -mWaveSize) {
            isUp = false;// 波浪下降到最低点了
        }
        if (isUp) {
            y = y - 1;
        } else {
            y = y + 1;
        }
        mPath.reset();

        int waveX1 = x + y;
        int waveX2 = x - y;
        int waveY1 = 0;
        int waveY2 = 0;

        if (x > 0) {
            mPath.moveTo(0, 0);
            mPath.lineTo(x, 0);
            switch (mWaveMode) {
                case WAVE_MODE_FLOATING:
                    waveY1 = mHeight / 4 + y;
                    waveY2 = 3 * mHeight / 4 + y;
                    break;
                default:
                    waveY1 = mHeight / 4;
                    waveY2 = 3 * mHeight / 4;
                    break;
            }
            mPath.cubicTo(waveX1, waveY1, waveX2, waveY2, x, mHeight);
            mPath.lineTo(0, mHeight);
            mPath.close();
        }

        mBitmap.eraseColor(Color.parseColor("#00000000"));

        mCanvas.drawArc(mLeftRectf, 90, 270, true, mBgPaint);// 绘制左半圆
        mCanvas.drawRect(mRadius, 0, mWidth - mRadius, mHeight, mBgPaint);// 绘制中间矩形
        mCanvas.drawArc(mRightRectf, 270, 180, true, mBgPaint);// 绘制右半圆

        mCanvas.drawPath(mPath, mWavePaint);//绘制波浪
        /////以下为测试部分////////////////////
        mCanvas.drawPath(mWaveTest, mFansPaint);
        mCanvas.drawPoint(waveX1, waveY1, mFansPaint);
        mCanvas.drawPoint(waveX2, waveY2, mFansPaint);
        /////以上为测试部分////////////////////
        //绘制风扇
        mCanvas.translate(mWidth - 2 * mRadius, 0);
        mCanvas.drawPath(mFansPath, mFansPaint);// 风扇的两个圆圈
        if (mIsFansMove) {
            switch (mFansMode) {
                case FANS_MODE_PROGRESS_MOVE:
                    mRotate = (int) ((mProgress / (float) mMax) * 100 * mFansSpeed);
                    break;
                default:
                    mRotate = (mRotate + mFansSpeed) % 360;
                    break;
            }
        } else {
            mRotate = 0;
        }
        mCanvas.rotate(mRotate, mRadius, mRadius);
        //绘制扇叶
        for (int i = 0; i < 4; i++) {
            mCanvas.drawPath(mBladePath, mFansPaint);
            mCanvas.rotate(90, mRadius, mRadius);
        }
        mCanvas.rotate(-mRotate, mRadius, mRadius);
        mCanvas.translate(2 * mRadius - mWidth, 0);

        canvas.drawBitmap(mBitmap, 0, 0, mBgPaint);
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMax() {
        return mMax;
    }

    public float getMaxWaveSize() {
        return mMaxWaveSize;
    }

    public float getMinWaveSize() {
        return mMinWaveSize;
    }

    public float getWaveSize() {
        return mWaveSize;
    }

    public int getWaveMode() {
        return mWaveMode;
    }

    public void setFansSpeed(int fansSpeed) {
        mFansSpeed = fansSpeed;
    }

    public void setWaveMode(int waveMode) {
        mWaveMode = waveMode;
    }

    public int getFansMode() {
        return mFansMode;
    }

    public void setFansMode(int fansMode) {
        mFansMode = fansMode;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public void setFansMove(boolean isMove) {
        mIsFansMove = isMove;
    }

    public boolean isFansMove() {
        return mIsFansMove;
    }

    public void setWaveSize(int waveSize) {
        mWaveSize = PxUtils.dpToPx(waveSize, mContext);
    }
}
