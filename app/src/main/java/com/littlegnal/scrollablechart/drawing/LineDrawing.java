package com.littlegnal.scrollablechart.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.littlegnal.scrollablechart.scrollablechartview.ClickFilter;
import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

public class LineDrawing extends DemoDrawing implements ClickFilter {

  private Path mLinePath;

  private Paint mLinePaint;

  private Paint mTextPaint;

  private Paint mBubblePaint;
  private Path mBubblePath;
  private RectF mBubbleRect;

  private LineDrawing(Context context, DataSourceProvider dataSourceProvider) {
    super(context, dataSourceProvider);
    init();
  }

  public static LineDrawing create(Context context, DataSourceProvider dataSourceProvider) {
    return new LineDrawing(context, dataSourceProvider);
  }

  private void init() {
    mLinePath = new Path();
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mLinePaint.setColor(0xFFF90382);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setStrokeWidth(10.0f);

    mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mTextPaint.setColor(Color.WHITE);
    mTextPaint.setTextSize(Util.sp2px(mContext, 13.0f));

    mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mBubblePaint.setColor(0xFF3B4DEC);

    mBubblePath = new Path();
    mBubbleRect = new RectF();
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
      drawBubble(canvas, itemSpace, start + "", x, y);


      for (int i = start + 1; i <= end; i++) {
        x = getAdjustedXByIndex(itemSpace, start, i);
        y = getAdjustedYByIndex(height, i);
        mLinePath.lineTo(x, y);

        drawBubble(canvas, itemSpace, i + "", x, y);
      }

      canvas.drawPath(mLinePath, mLinePaint);
    }
  }

  private void drawBubble(Canvas canvas, int itemSpace, String text, float x, float y) {
    float triangleWidth = itemSpace * 0.2f;
    float triangleHeight = itemSpace * 0.05f;
    float bubbleMargin = itemSpace * 0.4f;
    float bubbleWidth = itemSpace * 0.7f;
    float bubbleHeight = itemSpace * 0.4f;

    mBubblePath.reset();
    mBubblePath.moveTo(x, y - bubbleMargin);
    mBubblePath.lineTo(x + triangleWidth / 2.0f, y - bubbleMargin - triangleHeight);
    mBubblePath.lineTo(x - triangleWidth / 2.0f, y - bubbleMargin - triangleHeight);

    mBubbleRect.setEmpty();
    mBubbleRect.bottom = y - bubbleMargin - triangleHeight;
    mBubbleRect.left = x - bubbleWidth / 2.0f;
    mBubbleRect.top = mBubbleRect.bottom - bubbleHeight;
    mBubbleRect.right = mBubbleRect.left + bubbleWidth;
    mBubblePath.addRoundRect(
        mBubbleRect,
        bubbleHeight / 2.0f,
        bubbleHeight / 2.0f,
        Path.Direction.CW);
    canvas.drawPath(mBubblePath, mBubblePaint);

    float textWidth = getTextWidth(text, mTextPaint);
    float textHeight = getTextHeight(text, mTextPaint);
    canvas.drawText(
        text,
        x - textWidth / 2.0f,
        mBubbleRect.bottom - (bubbleHeight - textHeight) / 2.0f,
        mTextPaint);
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

    float triangleHeight = itemSpace * 0.05f;
    float bubbleMargin = itemSpace * 0.4f;
    float bubbleWidth = itemSpace * 0.7f;
    float bubbleHeight = itemSpace * 0.4f;

    int touchRadius = (int) bubbleWidth;
    if (upOffset > 0) {
      upOffset += touchRadius;
    } else {
      upOffset -= touchRadius;
    }
    int upOffsetCount = upOffset / itemSpace;
    if (upOffsetCount != 0) {
      int desPosition = currentPosition + upOffsetCount;
      float drawY = getAdjustedYByIndex(viewHeight, desPosition);
      if (upY < drawY - bubbleMargin - triangleHeight &&
          upY > drawY - bubbleMargin - triangleHeight - bubbleHeight) {
        return desPosition;
      }
    }

    return currentPosition;
  }
}
