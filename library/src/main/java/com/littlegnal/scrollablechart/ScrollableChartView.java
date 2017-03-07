package com.littlegnal.scrollablechart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;

/**
 * @author littlegnal
 * @date 2017/3/1
 */

public class ScrollableChartView extends View {

  private OverScroller mOverScroller;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private int mTouchSlop;
  private int mActivePointerId;
  private int mLastTouchX;
  private boolean mIsDragging = false;

  private VelocityTracker mVelocityTracker;

  private Drawing mDrawing;

  public ScrollableChartView(Context context) {
    this(context, null);
  }

  public ScrollableChartView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ScrollableChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ScrollableChartView(
      Context context,
      AttributeSet attrs,
      int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);

    mOverScroller = new OverScroller(context, new DecelerateInterpolator());
    ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
    mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    mTouchSlop = viewConfiguration.getScaledTouchSlop();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.save();
    canvas.translate(getDefaultScrollXOffset(), 0);
    handleDraw(canvas);
    canvas.restore();
  }

  protected void handleDraw(Canvas canvas) {
    int selectedPosition = computePosition();
    int left = selectedPosition + (getVisibleCount() / 2 + 1);
    if (left > getDataSourceSize() - 1) {
      left = getDataSourceSize() - 1;
    }
    int right = selectedPosition - (getDataSourceSize() / 2 + 1);
    if (right < 0) {
      right = 0;
    }
    if (mDrawing != null) {
      mDrawing.draw(canvas, selectedPosition, left, right);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mVelocityTracker != null) {
      mVelocityTracker.addMovement(event);
    }
    int action = event.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        if (!mOverScroller.isFinished()) {
          mOverScroller.abortAnimation();
        }
        if (mVelocityTracker == null) {
          mVelocityTracker = VelocityTracker.obtain();
        } else {
          mVelocityTracker.clear();
        }
        mVelocityTracker.addMovement(event);
        mActivePointerId = event.getPointerId(0);
        mLastTouchX = (int) event.getX();
        break;
      case MotionEvent.ACTION_MOVE:
        int deltaX = (int) (mLastTouchX - event.getX(mActivePointerId));
//          FDDebug.e("gg", "mIsDragging - " + mIsDragging);
//          FDDebug.e("gg", "Math.abs(deltaX) > mTouchSlop - " + (Math.abs(deltaX) > mTouchSlop));
//          FDDebug.e("gg", "deltaX - " + deltaX);
//          FDDebug.e("gg", "mTouchSlop - " + mTouchSlop);
        if (!mIsDragging && Math.abs(deltaX) > mTouchSlop) {
          final ViewParent parent = getParent();
          if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
          }
          if (deltaX > 0) {
            deltaX -= mTouchSlop;
          } else {
            deltaX += mTouchSlop;
          }
          mIsDragging = true;
        }

        if (mIsDragging) {
          if (canScroll(deltaX)) {
            scrollBy(deltaX, 0);
//            FDDebug.e("gg", "getScrollX() - " + getScrollX());
          }
          mLastTouchX = (int) event.getX();
        }
        break;
      case MotionEvent.ACTION_UP:
        if (mIsDragging) {
          mIsDragging = false;

          final ViewParent viewParent = getParent();
          if (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(false);
          }

//            mVelocityTracker.addMovement(event);
          mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
          int velocity = (int) mVelocityTracker.getXVelocity(mActivePointerId);
          if (Math.abs(velocity) > mMinimumVelocity) {
            // fling
            mOverScroller.fling(
                getScrollX(),
                getScrollY(),
                -velocity,
                0,
                getMinimumScrollX(),
                getMaximumScrollX(),
                0,
                0);
            postInvalidate();
          } else {
            // adjust item
            adjustItem();
          }
          recycleVelocityTracker();
        } else {
          // handle click
        }
        break;
      case MotionEvent.ACTION_CANCEL:
        if (mIsDragging) {
          // adjust item
          adjustItem();
          mIsDragging = false;
        }
        recycleVelocityTracker();
        break;
      default:
        break;
    }
    return true;
  }

  private void recycleVelocityTracker() {
    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
    }

    mVelocityTracker = null;
  }

  private int getMinimumScrollX() {
//    return (getWidth() - getItemSpace()) / 2;
//    if (getDataSourceSize() > 1) {
////      return -((getDataSourceSize() - 1) * getItemSpace() + getDefaultScrollXOffset());
//      return -((getDataSourceSize() - 1) * getItemSpace());// + getItemSpace() / 2;
//    }
//    return -getDefaultScrollXOffset();
//    return 0;
    return -((getDataSourceSize() - 1) * getItemSpace());
  }

  private int getMaximumScrollX() {
    return 0;//getDefaultScrollXOffset();//-((getDataSourceSize() - 1) * getItemSpace() + getMinimumScrollX());
  }

  private boolean canScroll(int deltaX) {
    int scrollX = getScrollX() + deltaX;
    int max = 0;//getDefaultScrollXOffset();
    int min = getMinimumScrollX();
    return scrollX <= max && scrollX >= min;
  }

  private void adjustItem() {
    int position = computePosition();
    int left = -position * getItemSpace();
//    FDDebug.e("gg", "position - " + position + ", left - " + left + ", getScrollX() - " + getScrollX());
    int scrollX = left - getScrollX();
    if (scrollX != 0) {
      mOverScroller.startScroll(getScrollX(), getScrollY(), scrollX, 0);
      postInvalidate();
    }
  }

  private int computePosition() {
//    int rightOffset = getDefaultScrollXOffset();
//    int scrollX = getScrollX() - rightOffset;
    int scrollX = getScrollX() - getItemSpace() / 2;
    return Math.abs(scrollX) / getItemSpace();
  }

  /**
   * The spacing between two items <br/>
   * <pre>
   *    | <- itemSpace -> | <- itemSpace -> |
   *  item1             item2             item3
   * </pre>
   *
   * @return The spacing between two items
   */
  private int getItemSpace() {
    return getWidth() / getVisibleCount();
  }

  /**
   * The visible item count on the screen
   * @return The visible item count on the screen
   */
  private int getVisibleCount() {
    return 0;
  }

  /**
   * The total item count of data source
   * @return The total item count of data source
   */
  private int getDataSourceSize() {
    return 0;
  }

  private int getDefaultScrollXOffset() {
    return (getWidth() - getItemSpace()) / 2;
  }

  public Drawing getDrawing() {
    return mDrawing;
  }

  public void setDrawing(Drawing drawing) {
    this.mDrawing = drawing;
  }
}
