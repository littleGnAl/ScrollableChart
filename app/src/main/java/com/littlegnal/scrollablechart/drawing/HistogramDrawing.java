package com.littlegnal.scrollablechart.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.littlegnal.scrollablechart.scrollablechartview.ClickFilter;
import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

public class HistogramDrawing extends DemoDrawing implements ClickFilter{

  private Paint mHistogramPaint;

  private Path mHistogramPath;

  private static final float RECT_RATIO = 0.8f;

  private static final int SELECTED_COLOR = 0xFFF90382;
  private static final int NORMAL_COLOR = 0x19F90382;

  private HistogramDrawing(Context context, DataSourceProvider dataSourceProvider) {
    super(context, dataSourceProvider);
    init();
  }

  public static HistogramDrawing create(Context context, DataSourceProvider dataSourceProvider) {
    return new HistogramDrawing(context, dataSourceProvider);
  }

  private void init() {
    mHistogramPath = new Path();

    mHistogramPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
    mHistogramPaint.setStyle(Paint.Style.FILL);
    mHistogramPaint.setColor(0xFFF90382);
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
    int dataSourceSize = getDataSourceSize();
    if (dataSourceSize > 0) {
      mHistogramPath.rewind();

      int nextPosition = scrollToNextPositionRatio < 0 ? selected - 1 : selected + 1;
      float ratio = Math.abs(scrollToNextPositionRatio);

      for (int i = start; i <= end; i++) {
        if (ratio != 0.0f && ratio != 1.0f) {
          if (i == selected) {
            mHistogramPaint.setColor(
                getEvaluateColor(1 - ratio, NORMAL_COLOR, SELECTED_COLOR));
          } else if (i == nextPosition) {
            mHistogramPaint.setColor(
                getEvaluateColor(ratio, NORMAL_COLOR, SELECTED_COLOR));
          } else {
            mHistogramPaint.setColor(NORMAL_COLOR);
          }
        } else if (i == selected) {
          mHistogramPaint.setColor(SELECTED_COLOR);
        } else {
          mHistogramPaint.setColor(NORMAL_COLOR);
        }

        float x = getAdjustedXByIndex(itemSpace, start, i);
        float y = getAdjustedYByIndex(height, i);
        float halfRectWith = itemSpace * RECT_RATIO / 2.0f;
        int left = (int) (x - halfRectWith);
        int top = (int) y;
        int right = (int) (x + halfRectWith);

        canvas.drawRect(left, top, right, height, mHistogramPaint);

        drawIndex(canvas, height, x, i);
      }
    }
  }

  private int getEvaluateColor(float fraction, int startColor, int endColor){
    int a, r, g, b;

    int sA = (startColor & 0xff000000) >>> 24;
    int sR = (startColor & 0x00ff0000) >>> 16;
    int sG = (startColor & 0x0000ff00) >>> 8;
    int sB = startColor & 0x000000ff;

    int eA = (endColor & 0xff000000) >>> 24;
    int eR = (endColor & 0x00ff0000) >>> 16;
    int eG = (endColor & 0x0000ff00) >>> 8;
    int eB = endColor & 0x000000ff;

    a = (int)(sA + (eA - sA) * fraction);
    r = (int)(sR + (eR - sR) * fraction);
    g = (int)(sG + (eG - sG) * fraction);
    b = (int)(sB + (eB - sB) * fraction);

    return a << 24 | r << 16 | g << 8 | b;
  }

  @Override
  public int computeClickedPosition(
      int viewWidth,
      int viewHeight,
      int itemSpace,
      int currentPosition,
      int touchSlop,
      int upX,
      int upY) {
    float halfRectWith = itemSpace * RECT_RATIO / 2.0f;
    int centerX = viewWidth / 2;
    int upOffset = centerX - upX;
    int touchRadius = (int) halfRectWith;
    if (upOffset > 0) {
      upOffset += touchRadius;
    } else {
      upOffset -= touchRadius;
    }
    int upOffsetCount = upOffset / itemSpace;
    if (upOffsetCount != 0) {
      int desPosition = currentPosition + upOffsetCount;
      float drawY = getAdjustedYByIndex(viewHeight, desPosition);
      if (upY > drawY && upY < viewHeight) {
        return desPosition;
      }
    }

    return currentPosition;
  }
}
