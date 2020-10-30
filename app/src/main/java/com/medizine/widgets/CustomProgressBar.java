package com.medizine.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.medizine.Constants;
import com.medizine.R;
import com.medizine.utils.Utils;


/**
 * Created by ramraj on 02-06-2017.
 * has 3 type of views which is being depended on the download status.
 * 1:DOWNLOAD_IN_PROGRESS
 * 2:NOT_DOWNLOADED
 * 3:DOWNLOAD_FINISHED
 */

public class CustomProgressBar extends View {

    private final Rect textBounds = new Rect();
    int color = 0xff44C8E5;
    private int max = 100;
    private int progress = 50;
    @NonNull
    private Path path = new Path();
    private Paint paint;
    private Paint mPaintProgress;
    private RectF mRectF;
    private Paint textPaint;
    @NonNull
    private String text = "0";
    private int centerY;

    private int centerX;

    private int swipeAngle = 0;

    private Bitmap mIconImageNotDownloaded, mIconImageCompleted;

    @Constants.DownloadStatus
    private String DOWNLOAD_STATUS = Constants.NOT_DOWNLOADED;

    @NonNull
    private Matrix matrix = new Matrix();

    public CustomProgressBar(Context context) {
        super(context);
        initUI();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI();
    }

    private void initUI() {
        color = getContext().getResources().getColor(R.color.secondary);
        setClickable(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(Utils.dpToPixels(1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);


        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(Utils.dpToPixels(2));
        mPaintProgress.setColor(color);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(color);
        textPaint.setStrokeWidth(2);

        mIconImageNotDownloaded = Utils.getBitmapFromVectorDrawable(getContext(), R.drawable.download);
        mIconImageCompleted = Utils.getBitmapFromVectorDrawable(getContext(), R.drawable.done);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        int radius = (Math.min(viewWidth, viewHeight) - Utils.dpToPixels(2)) / 2;

        path.reset();

        centerX = viewWidth / 2;
        centerY = viewHeight / 2;
        path.addCircle(centerX, centerY, radius, Path.Direction.CW);

        int smallCirclRadius = radius - Utils.dpToPixels(7);

        /*uncomment if you want inner circle outline*/
//        path.addCircle(centerX, centerY, smallCirclRadius, Path.Direction.CW);
        smallCirclRadius += Utils.dpToPixels(4);

        mRectF = new RectF(centerX - smallCirclRadius, centerY - smallCirclRadius, centerX + smallCirclRadius, centerY + smallCirclRadius);

        textPaint.setTextSize(radius * 0.5f);
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {


        super.onDraw(canvas);

        switch (DOWNLOAD_STATUS) {
            case Constants.DOWNLOAD_FINISHED:
                drawBitmapOnCanvas(canvas, matrix, mIconImageCompleted);
                break;
            case Constants.DOWNLOAD_IN_PROGRESS:
                canvas.drawPath(path, paint);

                canvas.drawArc(mRectF, 270, swipeAngle, false, mPaintProgress);

                drawTextCentred(canvas);
                break;
            default:
            case Constants.NOT_DOWNLOADED:
                drawBitmapOnCanvas(canvas, matrix, mIconImageNotDownloaded);
                break;
        }
    }

    @Override
    public boolean hasOnClickListeners() {
        return true;
    }

    /*draws text in center showing progress of download*/
    public void drawTextCentred(@NonNull Canvas canvas) {

        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        canvas.drawText(text, centerX - textBounds.exactCenterX(), centerY - textBounds.exactCenterY(), textPaint);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(int progress) {
        this.progress = progress;

        int percentage = progress * 100 / max;

        swipeAngle = percentage * 360 / 100;

        text = percentage + "";

        this.DOWNLOAD_STATUS = Constants.DOWNLOAD_IN_PROGRESS;

        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void downloadIconChange(@Constants.DownloadStatus String status) {
        this.DOWNLOAD_STATUS = status;
        invalidate();
    }

    private void drawBitmapOnCanvas(Canvas canvas, @NonNull Matrix matrix, @NonNull Bitmap bitmap) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawBitmap(bitmap, matrix, paint);
    }
}
