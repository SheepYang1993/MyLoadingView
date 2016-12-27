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
        mMax = a.getInt(R.styleable.LoadingView_max, 100);
        mIsFansMove = a.getBoolean(R.styleable.LoadingView_is_fans_move, false);
        mProgress = a.getInt(R.styleable.LoadingView_progress, 0);
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

        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = PxUtils.dpToPx(300, mContext);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = PxUtils.dpToPx(40, mContext);
        }
        mRadius = mHeight / 2;
        if (mWidth - 2 * mRadius < 0) {
            mWidth = 2 * mRadius;
        }
        setMeasuredDimension(mWidth, mHeight);
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888); //生成一个bitmap
        }
        if (mCanvas == null) {
            mCanvas = new Canvas(mBitmap);//将bitmp放在我们自己的画布上，实际上mCanvas.draw的时候 改变的是这个bitmap对象
        }
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
        if (mBitmapFans == null) {
            mBitmapFans = BitmapUtil.getBitmapFromVectorDrawable(mContext, R.drawable.fan, mFansColor, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        mPath.moveTo(0, 0);
        mPath.lineTo(x, 0);
        mPath.cubicTo(x + y, mHeight / 3, x - y, 2 * mHeight / 3, x, mHeight);
        mPath.lineTo(0, mHeight);
        mPath.close();

        mBitmap.eraseColor(Color.parseColor("#00000000"));

        mCanvas.drawArc(mLeftRectf, 90, 270, true, mBgPaint);
        mCanvas.drawRect(mRadius, 0, mWidth - mRadius, mHeight, mBgPaint);
        mCanvas.drawArc(mRightRectf, 270, 180, true, mBgPaint);

        mCanvas.drawPath(mPath, mWavePaint);
//        mCanvas.drawPoint(x + y, mHeight / 3/* + y * 2*/, mWavePaint);
//        mCanvas.drawPoint(x - y, 2 * mHeight / 3/* + y * 2*/, mWavePaint);

        mMatrix.reset();
        if (mIsFansMove) {
            mMatrix.postRotate(mRotate, mRadius, mRadius);
            mRotate = (mRotate + 10) % 360;
        }
        mCanvas.translate(mWidth - 2 * mRadius, 0);
        mCanvas.drawBitmap(mBitmapFans, mMatrix, mBgPaint);
        mCanvas.translate(2 * mRadius - mWidth, 0);

        canvas.drawBitmap(mBitmap, 0, 0, mBgPaint);
        postInvalidateDelayed(5);
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
}
