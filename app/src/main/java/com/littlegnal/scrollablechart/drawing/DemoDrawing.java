package com.littlegnal.scrollablechart.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

public abstract class DemoDrawing extends AbstractDrawing {

  private Rect mTextBounds;

  private Paint mTextPaint;

  protected Context mContext;

  public DemoDrawing(Context context, DataSourceProvider dataSourceProvider) {
    super(dataSourceProvider);
    this.mContext = context;

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mTextPaint.setColor(0xFF888888);
    mTextPaint.setTextSize(Util.sp2px(mContext, 12.0f));
  }

  protected int getTextHeight(String text, Paint paint) {
    if (mTextBounds == null) {
      mTextBounds = new Rect();
    }
    mTextBounds.setEmpty();
    paint.getTextBounds(text, 0, text.length(), mTextBounds);

    return mTextBounds.height();
  }

  protected float getTextWidth(String text, Paint paint) {
    if (text != null && text.length() > 0) {
      return paint.measureText(text, 0, text.length());
    }

    return 0;
  }

  protected void drawIndex(Canvas canvas, int height, float x, int index) {
    String text = index + "";
    int textHeight = getTextHeight(text, mTextPaint);
    float textWidth = getTextWidth(text, mTextPaint);
    x -= textWidth / 2.0f;
    float y = height - textHeight;
    canvas.drawText(text, x, y, mTextPaint);
  }
}
