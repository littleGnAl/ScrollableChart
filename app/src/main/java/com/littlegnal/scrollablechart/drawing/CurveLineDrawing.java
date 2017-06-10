package com.littlegnal.scrollablechart.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.littlegnal.scrollablechart.scrollablechartview.ClickFilter;
import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

public class CurveLineDrawing extends DemoDrawing implements ClickFilter {

  private Path mCurvePath;

  private Paint mCurvePaint;
  private Paint mCirclePaint;

  private float SELECTED_CIRCLE_RADIUS;
  private float NORMAL_CIRCLE_RADIUS;

  private CurveLineDrawing(Context context, DataSourceProvider dataSourceProvider) {
    super(context, dataSourceProvider);
    init();
  }

  public static CurveLineDrawing create(Context context, DataSourceProvider dataSourceProvider) {
    return new CurveLineDrawing(context, dataSourceProvider);
  }

  private void init() {
    SELECTED_CIRCLE_RADIUS = Util.dp2px(mContext, 10.0f);
    NORMAL_CIRCLE_RADIUS = Util.dp2px(mContext, 5.0f);

    mCurvePath = new Path();

    mCurvePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mCurvePaint.setColor(0xFFF90382);
    mCurvePaint.setStrokeWidth(Util.dp2px(mContext, 2.0f));
    mCurvePaint.setStyle(Paint.Style.STROKE);

    mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mCirclePaint.setColor(0xFFF90382);
    mCirclePaint.setStyle(Paint.Style.FILL);
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
      mCurvePath.rewind();
      float preX;
      float preY;
      float curX;
      float curY;
      curX = getAdjustedXByIndex(itemSpace, start, start);
      curY = getAdjustedYByIndex(height, start);
      mCurvePath.moveTo(curX, curY);
      drawIndex(canvas, height, curX, start);

      int nextPosition = scrollToNextPositionRatio < 0 ? selected - 1 : selected + 1;
      float ratio = Math.abs(scrollToNextPositionRatio);
      drawCircle(canvas, curX, curY, start, ratio, selected, nextPosition);

      for (int i = start + 1; i <= end; i++) {
        preX = curX;
        preY = curY;
        curX = getAdjustedXByIndex(itemSpace, start, i);
        curY = getAdjustedYByIndex(height, i);
        float cpx = preX + (curX - preX) / 2.0f;
        mCurvePath.cubicTo(cpx, preY, cpx, curY, curX, curY);
        drawIndex(canvas, height, curX, i);
        drawCircle(canvas, curX, curY, i, ratio, selected, nextPosition);
      }
      canvas.drawPath(mCurvePath, mCurvePaint);
    }
  }

  private void drawCircle(
      Canvas canvas,
      float x,
      float y,
      int index,
      float ratio,
      int selected,
      int next) {
    float radius;
    if (ratio != 0.0f && ratio != 1.0f) {
      if (index == selected) {
        radius = getEvaluateRadius(1 - ratio, NORMAL_CIRCLE_RADIUS, SELECTED_CIRCLE_RADIUS);
      } else if (index == next) {
        radius = getEvaluateRadius(ratio, NORMAL_CIRCLE_RADIUS, SELECTED_CIRCLE_RADIUS);
      } else {
        radius = NORMAL_CIRCLE_RADIUS;
      }
    } else if (index == selected) {
      radius = SELECTED_CIRCLE_RADIUS;
    } else {
      radius = NORMAL_CIRCLE_RADIUS;
    }

    canvas.drawCircle(x, y, radius, mCirclePaint);
  }

  private float getEvaluateRadius(float ratio, float startRadius, float endRadius) {
    float offset = endRadius - startRadius;
    return ratio * offset + startRadius;
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
    int centerX = viewWidth / 2;
    int upOffset = centerX - upX;
    int touchRadius = (int) SELECTED_CIRCLE_RADIUS;
    if (upOffset > 0) {
      upOffset += touchRadius;
    } else {
      upOffset -= touchRadius;
    }
    int upOffsetCount = upOffset / itemSpace;
    if (upOffsetCount != 0) {
      int desPosition = currentPosition + upOffsetCount;
      float drawY = getAdjustedYByIndex(viewHeight, desPosition);
      if (upY > drawY - touchRadius && upY < drawY + touchRadius) {
        return desPosition;
      }
    }

    return currentPosition;
  }
}
