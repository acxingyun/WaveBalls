package acxingyun.cetcs.com.myviews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xing.Yun on 2017/9/26.
 */

public class WaitingBallView extends View {

    /**
     * 球个数
     */
    private int ballCount;
    /**
     * 球半径
     */
    private float ballRadius;

    private int ballColor;

    private int lineColor;

    private int barColor;

    private float barWidth;

    private float lineWidth;

    private Paint linePaint;

    private Paint ballPaint;

    private float animationPeriod;

    /**
     * 最大摆动角度
     */
    private float waveAngleMax;

    /**
     * 摆动中的角度
     */
    private float wavingAngle;

    private int wavingBallCount;

    private int mWidth;

    private int mHeight;

    /**
     * 保存没有摆动时每个球的坐标
     */
    private Map<Integer , List<Float>> locationMap = new HashMap<>();
    private float lineLength;

    public WaitingBallView(Context context){
        this(context, null, 0);
    }

    public WaitingBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WaitingBallView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaitingBallView, defStyleAttr, 0);

        ballCount = a.getInteger(R.styleable.WaitingBallView_ballCount, 0);
        ballRadius = a.getDimension(R.styleable.WaitingBallView_ballRadius, 0);
        ballColor = a.getColor(R.styleable.WaitingBallView_ballColor, 0);
        lineColor = a.getColor(R.styleable.WaitingBallView_lineColor, 0);
        waveAngleMax = a.getFloat(R.styleable.WaitingBallView_waveAngle, 0);
        lineWidth = a.getDimension(R.styleable.WaitingBallView_lineWidth, 0);
        barColor = a.getColor(R.styleable.WaitingBallView_barColor, 0);
        barWidth = a.getDimension(R.styleable.WaitingBallView_barWidth, 0);
        animationPeriod = a.getFloat(R.styleable.WaitingBallView_animationPeriod, 0);
        wavingBallCount = a.getInteger(R.styleable.WaitingBallView_wavingBallCount, 0);

        a.recycle();

        linePaint = new Paint();
        ballPaint = new Paint();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(getClass().getSimpleName(), "onMeasure called...");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int computeSize;

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int spenSize = MeasureSpec.getSize(widthMeasureSpec);

        Log.d(getClass().getSimpleName(), "spenSize:" + spenSize);

        switch (specMode){
            case MeasureSpec.EXACTLY:
                mWidth = spenSize;
                break;

            case MeasureSpec.AT_MOST:
                computeSize = getPaddingLeft() + getPaddingRight() + spenSize;
                Log.d(getClass().getSimpleName(), "computeSize:" + computeSize);
                mWidth = computeSize < spenSize?computeSize:spenSize;
                break;

            case MeasureSpec.UNSPECIFIED:
                computeSize = getPaddingLeft() + getPaddingRight() + spenSize;
                Log.d(getClass().getSimpleName(), "computeSize:" + computeSize);
                mWidth = computeSize < spenSize?computeSize:spenSize;
                break;
        }

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        spenSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.d(getClass().getSimpleName(), "spenSize:" + spenSize);

        switch (specMode){
            case MeasureSpec.EXACTLY:
                mHeight = spenSize;
                break;

            case MeasureSpec.AT_MOST:
                computeSize = getPaddingLeft() + getPaddingRight() + spenSize;
                Log.d(getClass().getSimpleName(), "computeSize:" + computeSize);
                mHeight = computeSize < spenSize?computeSize:spenSize;
                break;

            case MeasureSpec.UNSPECIFIED:
                computeSize = getPaddingLeft() + getPaddingRight() + spenSize;
                Log.d(getClass().getSimpleName(), "computeSize:" + computeSize);
                mHeight = computeSize < spenSize?computeSize:spenSize;
                break;
        }

        Log.d(getClass().getSimpleName(), "mWidth:" + mWidth);
        Log.d(getClass().getSimpleName(), "mHeight:" + mHeight);

        //如果小球数量太多无法画完，减小小球半径
//        lineLength = mHeight - ballRadius;
//        double maxWidthInAni = ballCount * 2 * ballRadius + lineLength * Math.sin(waveAngleMax * Math.PI / 180) * 2;
//        if (maxWidthInAni > mWidth){
//            ballRadius = (float) ((mWidth - lineLength * Math.sin(waveAngleMax * Math.PI / 180) * 2) / (2 * ballCount));
//        }
//        lineLength = mHeight - ballRadius;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(getClass().getSimpleName(), "onSizeChanged called,w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Log.i(getClass().getSimpleName(), "wavingAngle:" + wavingAngle);

        linePaint.setColor(barColor);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(barWidth);

        canvas.drawLine(0, 0, mWidth, 0, linePaint);

        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

        ballPaint.setColor(ballColor);
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setAntiAlias(true);

        float ballX;
        float ballY;

        float lineStartX;
        float lineStartY;
        float lineStopX;
        float lineStopY;

        lineLength = mHeight - ballRadius;
        List<Float> locationList;
        if (wavingAngle == 0){
            for (int i = 0; i< ballCount; i++){
                locationList = locationMap.get(i);
                ballX = locationList.get(0);
                ballY = locationList.get(1);

                //先画线再花球，球上不会看到线，更好看
                canvas.drawLine(ballX, 0, ballX, ballY, linePaint);
                canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);

            }
        }else if (wavingAngle < 0){
            for (int i = 0; i < wavingBallCount; i++){

                locationList = locationMap.get(i);
                ballX = (float) (locationList.get(0) + lineLength * Math.sin(wavingAngle * Math.PI/180));
                ballY = (float) (lineLength * Math.cos(wavingAngle * Math.PI / 180));

                lineStartX = ballX;
                lineStartY = ballY;
                lineStopX = locationList.get(0);
                lineStopY = 0;

                canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
                canvas.drawCircle( ballX, ballY, ballRadius, ballPaint);

            }

            for (int i = wavingBallCount; i < ballCount; i++){
                locationList = locationMap.get(i);
                ballX = locationList.get(0);
                ballY = locationList.get(1);

                canvas.drawLine(ballX, 0, ballX, ballY, linePaint);
                canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);
            }

        }else if (wavingAngle > 0){

            for (int i = ballCount - 1; i > ballCount - wavingBallCount - 1; i--){
                locationList = locationMap.get(i);
                ballX = (float) (locationList.get(0) + lineLength * Math.sin(wavingAngle * Math.PI/180));
                ballY = (float) (lineLength * Math.cos(wavingAngle * Math.PI / 180));

                lineStartX = locationMap.get(i).get(0);
                lineStartY = 0;
                lineStopX = ballX;
                lineStopY = ballY;

                canvas.drawLine( lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
                canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);

            }

            for (int i = 0; i < ballCount - wavingBallCount; i++){

                locationList = locationMap.get(i);
                ballX = locationList.get(0);
                ballY = locationList.get(1);

                lineStartX = ballX;
                lineStartY = ballY;
                lineStopX = ballX;
                lineStopY = 0;

                canvas.drawLine( lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
                canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);
            }

//            if (ballCount % 2 == 0){
//                for (int i = wavingBallCount; i< ballCount; i++){
//
//                    locationList = locationMap.get(i);
//                    ballX = (float) (locationList.get(0) + lineLength * Math.sin(wavingAngle * Math.PI/180));
//                    ballY = (float) (lineLength * Math.cos(wavingAngle * Math.PI / 180));
//
//                    lineStartX = locationList.get(0);
//                    lineStartY = 0;
//                    lineStopX = ballX;
//                    lineStopY = ballY;
//
//                    canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
//                    canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);
//                }
//            }else {
//                locationList = locationMap.get((ballCount - 1) / 2);
//                ballX = locationList.get(0);
//                ballY = locationList.get(1);
//
//                lineStartX = ballX;
//                lineStartY = ballY;
//                lineStopX = ballX;
//                lineStopY = 0;
//
//                canvas.drawLine( lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
//                canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);
//
//                for (int i = (ballCount + 1)/2; i< ballCount; i++){
//                    locationList = locationMap.get(i);
//                    ballX = (float) (locationList.get(0) + lineLength * Math.sin(wavingAngle * Math.PI/180));
//                    ballY = (float) (lineLength * Math.cos(wavingAngle * Math.PI / 180));
//
//                    lineStartX = locationList.get(0);
//                    lineStartY = 0;
//                    lineStopX = ballX;
//                    lineStopY = ballY;
//
//                    canvas.drawLine(lineStartX, lineStartY, lineStopX, lineStopY, linePaint);
//                    canvas.drawCircle( ballX,  ballY, ballRadius, ballPaint);
//                }
//            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(getClass().getSimpleName(), "changed:" + changed + " left:" + left + " top:" + top
        + " right:" + right + " bottom:" + bottom);

        float ballX;
        float ballY;
        for (int i = 0; i < ballCount; i++){
            ballX = (float) ((mHeight - ballRadius*2 + ballRadius) * Math.sin(waveAngleMax * Math.PI / 180) + ballRadius + ballRadius * 2 * i);
            ballY = mHeight - ballRadius*2 + ballRadius;
            List<Float> locationArray = new ArrayList<>();
            locationArray.add(0, ballX);
            locationArray.add(1, ballY);
            locationMap.put(i, locationArray);
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void startAnimation(){
        final ValueAnimator angleValue = ValueAnimator.ofFloat(-waveAngleMax, 0 , waveAngleMax, 0, -waveAngleMax);
        angleValue.setInterpolator(new LinearInterpolator());
        wavingAngle = -waveAngleMax;
        angleValue.setDuration((long) (animationPeriod));
        angleValue.setRepeatCount(ValueAnimator.INFINITE);
//        angleValue.setRepeatCount(1);
        angleValue.start();
        angleValue.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                wavingAngle = (float) angleValue.getAnimatedValue();
                invalidate();
            }
        });
    }
}
