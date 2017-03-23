package com.littlegnal.scrollablechart.drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

public class LineDrawing extends AbstractDrawing {

  private Path mLinePath;

  private Paint mLinePaint;

  private Paint mTextPaint;

  private LineDrawing(DataSourceProvider dataSourceProvider) {
    super(dataSourceProvider);
    init();
  }

  public static LineDrawing create(DataSourceProvider dataSourceProvider) {
    return new LineDrawing(dataSourceProvider);
  }

  private void init() {
    mLinePath = new Path();
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mLinePaint.setColor(0xFFF90382);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setStrokeWidth(10.0f);

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mTextPaint.setColor(Color.BLACK);
    mTextPaint.setTextSize(50.0f);
  }

  @Override
  public void draw(
      Canvas canvas,
      boolean changed,
      int width,
      int height,
      int itemSpace,
      int selected,
      int start,
      int end,
      float scrollToNextPositionRatio) {
    int dataSourceSize = mDataSourceProvider.getDataSourceSize();
    if (dataSourceSize > 0) {
      mLinePath.rewind();

      float x = getAdjustedXByIndex(itemSpace, start, start);
      float y = getAdjustedYByIndex(height, start);
      mLinePath.moveTo(x, y);
      canvas.drawText(start + "", x, y, mTextPaint);

      for (int i = start + 1; i <= end; i++) {
        x = getAdjustedXByIndex(itemSpace, start, i);
        y = getAdjustedYByIndex(height, i);
        mLinePath.lineTo(x, y);

        canvas.drawText(i + "", x, y, mTextPaint);
      }

      canvas.drawPath(mLinePath, mLinePaint);
    }
  }

}
