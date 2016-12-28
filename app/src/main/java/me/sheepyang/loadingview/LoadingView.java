package me.sheepyang.loadingview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import me.sheepyang.loadingview.utils.BitmapUtil;
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
    private Bitmap mBitmapFans;
    private Context mContext;
    private int mWaveColor;
    private int mFansColor;
    private int mBgColor;
    private int mWidth;
    private int mHeight;
    private Paint mWavePaint;
    private Paint mBgPaint;
    private int mRadius;
    private RectF mLeftRectf;
    private RectF mRightRectf;
    private Matrix mMatrix;
    private int mRotate;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Path mPath;
    private boolean isUp;
    private int x;
    private int y = 10;
    private float mWaveSize;// 波浪高度
    private int mProgress;
    private int mMax;
    private boolean mIsFansMove;

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

        mMatrix = new Matrix();
        mPath = new Path();
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
        mBitmapFans = BitmapUtil.getBitmapFromVectorDrawable(mContext, R.drawable.fan, mFansColor, mHeight);

        if (mLeftRectf == null) {
            mLeftRectf = new RectF();
        }
        if (mRightRectf == null) {
            mRightRectf = new RectF();
        }
        mLeftRectf.left = 0;
        mLeftRectf.top = 0;
        mLeftRectf.right = 2 * mRadius;
        mLeftRectf.bottom = mHeight;

        mRightRectf.left = mWidth - 2 * mRadius;
        mRightRectf.top = 0;
        mRightRectf.right = mWidth;
        mRightRectf.bottom = mHeight;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWaveSize = mWaveSize < mMinWaveSize ? mMinWaveSize : mWaveSize;
        mWaveSize = mWaveSize > mMaxWaveSize ? mMaxWaveSize : mWaveSize;
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
        x = (int) (mProgress / (float) mMax * mWidth);
        if (x > 0) {
            mPath.moveTo(0, 0);
            mPath.lineTo(x, 0);
            switch (mWaveMode) {
                case WAVE_MODE_FLOATING:
                    mPath.cubicTo(x + y, mHeight / 3 + y, x - y, 2 * mHeight / 3 + y, x, mHeight);
                    break;
                default:
                    mPath.cubicTo(x + y, mHeight / 3, x - y, 2 * mHeight / 3, x, mHeight);
                    break;
            }
            mPath.lineTo(0, mHeight);
            mPath.close();
        }

        mBitmap.eraseColor(Color.parseColor("#00000000"));

        mCanvas.drawArc(mLeftRectf, 90, 270, true, mBgPaint);
        mCanvas.drawRect(mRadius, 0, mWidth - mRadius, mHeight, mBgPaint);
        mCanvas.drawArc(mRightRectf, 270, 180, true, mBgPaint);

        mCanvas.drawPath(mPath, mWavePaint);

        mMatrix.reset();
        if (mIsFansMove) {
            switch (mFansMode) {
                case FANS_MODE_PROGRESS_MOVE:
                    mRotate = (int) ((mProgress / (float) mMax) * 100 * 10);
                    mMatrix.postRotate(mRotate, mRadius, mRadius);
                    break;
                default:
                    mMatrix.postRotate(mRotate, mRadius, mRadius);
                    mRotate = (mRotate + 10) % 360;
                    break;
            }
        }
        mCanvas.translate(mWidth - 2 * mRadius, 0);
        mCanvas.drawBitmap(mBitmapFans, mMatrix, mBgPaint);
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
