package com.littlegnal.scrollablechart.scrollablechartview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * A scrollable chart view that adjust the nearest position when fling or move end.
 * You can easy to implement your owen chart style by implement the {@link Drawing} interface.
 * And this is a solution of <blockquote>"W/OpenGLRenderer(29239): Shape path too large to be
 * rendered into a texture"</blockquote>
 */
public class ScrollableChartView extends View {

  /**
   * Interface definition for a callback to be invoked when a specific position is clicked.
   */
  public interface OnItemClickListener {

    /**
     * Called when a specific position has been clicked.
     * @param view The view that was clicked.
     * @param itemPosition The specific position that was clicked.
     */
    void onClick(ScrollableChartView view, int itemPosition);
  }

  /**
   * Interface definition for a callback to be invoked when current position changed.
   */
  public interface OnPositionChangeListener {

    /**
     * Called when current position changed.
     * @param view The view that current position was changed.
     * @param prePosition The previous position
     * @param curPosition The changed position
     */
    void onPositionChanged(ScrollableChartView view, int prePosition, int curPosition);
  }

  /**
   * The adjust item scroll duration
   */
  private static final int ADJUST_ITEM_DURATION = 300;

  private ScrollerCompat mScroller;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private int mTouchSlop;
  private int mActivePointerId;
  private int mLastTouchX;
  private boolean mIsDragging = false;

  private VelocityTracker mVelocityTracker;

  private Drawing mDrawing;

  private int mDataSourceSize;

  /**
   * The visible count on the screen
   */
  private int mVisibleCount;

  private int mItemSpace = -1;

  private boolean mIsDefaultItemSpace;

  /**
   * We want to perform a scroll when click on the specific position, this flag indicates
   * whether enable scroll to the click position
   */
  private boolean mIsEnableClickScroll;

  private int mCurrentPosition;
  private int mPrePosition;

  /**
   * The flag of fling occur
   */
  private boolean mIsFlinging;

  private final int PRE_ACTION_NONE = -1;
  private final int PRE_ACTION_MOVE = 0;
  private final int PRE_ACTION_FLING = 1;
  /**
   * The previous behavior, the value is set to {@link #PRE_ACTION_MOVE} when move behavior occur,
   * and set to {@link #PRE_ACTION_FLING} when fling behavior occur, the default value is
   * {@link #PRE_ACTION_NONE}.
   * <p> To avoid immediately click scroll, we just adjust item when {@code mPreBehavior} was equal
   * to {@code PRE_ACTION_MOVE} or {@code PRE_ACTION_FLING}
   */
  private int mPreBehavior = PRE_ACTION_NONE;

  /**
   * The delay duration of {@link #mCheckAdjustFinished} invoked. We check every 32 millis to
   * determine whether the adjust item behavior end, and reset {@link #mPreBehavior} to
   * {@link #PRE_ACTION_NONE} in the end.
   */
  private final int CHECK_ADJUST_FINISHED_DELAY = 32;

  private OnItemClickListener mOnItemClickListener;

  private List<OnPositionChangeListener> mOnPositionChangeListeners;

  public ScrollableChartView(Context context) {
    this(context, null);
  }

  public ScrollableChartView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ScrollableChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mScroller = ScrollerCompat.create(context, new DecelerateInterpolator());
    ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
    mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    mTouchSlop = viewConfiguration.getScaledTouchSlop();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    handleDraw(canvas);
  }

  /**
   * We translate the {@code selectedPosition - (visibleCount / 2 + 2)} to ensure the path
   * coordinate x between [0, (mVisibleCount + 4) * itemSpace], we add extra 2 counts each side to
   * ensure the consequent drawing
   * @param canvas the canvas on which the background will be drawn
   */
  protected void handleDraw(Canvas canvas) {
    int selectedPosition = computePosition();
    mCurrentPosition = selectedPosition;

    // we add extra 2 counts each side to ensure the consequent drawing
    int visibleCount = getVisibleCount();
    int halfVisibleCount = (visibleCount / 2 + 2);
    int end = selectedPosition + halfVisibleCount;
    if (end > getDataSourceSize() - 1) {
      end = getDataSourceSize() - 1;
    }
    int start = selectedPosition - halfVisibleCount;
    if (start < 0) {
      start = 0;
    }

    boolean changed = mPrePosition != mCurrentPosition;
    int itemSpace = getItemSpace();
    int scrollToNextOffset = -(mPrePosition * itemSpace) - getScrollX();
    float scrollToNextPositionRatio = (scrollToNextOffset * 100.0f / itemSpace) / 100.0f;
    // Ensure the scrollToNextPositionRatio is between [0.0f, 1.0f] or [-1.0f, 0.0f], and the value
    // scrollToNextPositionRatio is less than 0.0f means that it is scrolling to right, or scrolling
    // to left.
    scrollToNextPositionRatio = scrollToNextPositionRatio > 0.0f ?
        (scrollToNextPositionRatio > 1.0f ? 1.0f : scrollToNextPositionRatio) :
        (scrollToNextPositionRatio < -1.0f ? -1.0f : scrollToNextPositionRatio);

    canvas.save();
    canvas.translate(-(start * itemSpace - getDefaultScrollXOffset()), 0.0f);
    if (mDrawing != null) {
      mDrawing.draw(
          canvas,
          changed,
          getWidth(),
          getHeight(),
          itemSpace,
          selectedPosition,
          start,
          end,
          scrollToNextPositionRatio);
    }
    canvas.restore();
    if (changed) {
      notifyOnPositionChange(mPrePosition, mCurrentPosition);
      mPrePosition = mCurrentPosition;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(event);

    int action = event.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mIsFlinging = false;
        if (!mScroller.isFinished()) {
          stopCheckAdjustFinished();
          mScroller.abortAnimation();
          postInvalidate();
        }

        mVelocityTracker.addMovement(event);
        mActivePointerId = event.getPointerId(0);
        mLastTouchX = (int) event.getX();
        break;
      case MotionEvent.ACTION_MOVE:
        mIsFlinging = false;
        int deltaX = (int) (mLastTouchX - event.getX(mActivePointerId));
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
          mPreBehavior = PRE_ACTION_MOVE;
        }

        if (mIsDragging) {
          if (canScroll(deltaX)) {
            scrollBy(deltaX, 0);
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

          mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
          int velocity = (int) mVelocityTracker.getXVelocity(mActivePointerId);
          if (Math.abs(velocity) > mMinimumVelocity) {
            // fling
            mScroller.fling(
                getScrollX(),
                getScrollY(),
                -velocity,
                0,
                getMinimumScrollX(),
                getMaximumScrollX(),
                0,
                0);
            postInvalidate();
            mPreBehavior = PRE_ACTION_FLING;
            mIsFlinging = true;
          } else {
            // adjust item
            adjustItem();
          }
        } else {
          // To avoid the immediately click scroll, we just adjust item when
          // mPreBehavior == PRE_ACTION_FLING or mPreBehavior == PRE_ACTION_MOVE
          if (mPreBehavior == PRE_ACTION_FLING || mPreBehavior == PRE_ACTION_MOVE) {
            adjustItem();
            return true;
          }

          // handle click
          int upX = (int) event.getX(mActivePointerId);
          handleClick(upX);

        }
        recycleVelocityTracker();
        break;
      case MotionEvent.ACTION_CANCEL:
        if (mIsDragging) {
          // adjust item
          adjustItem();
          mIsDragging = false;
          mIsFlinging = false;
        }
        recycleVelocityTracker();
        break;
      default:
        break;
    }
    return true;
  }

  private void handleClick(int upX) {
    int centerX = getWidth() / 2;
    int upOffset = centerX - upX;
    if (upOffset > 0) {
      upOffset += (mTouchSlop + getItemSpace() / 2);
    } else {
      upOffset -= (mTouchSlop + getItemSpace() / 2);
    }
    int upOffsetCount = upOffset / getItemSpace();
    if (upOffsetCount != 0) {
      int scrollToPosition = mCurrentPosition + upOffsetCount;
      if (scrollToPosition >= 0) {
        int scrollToX = -(scrollToPosition * getItemSpace());
        scrollToX -= getScrollX();
        if (scrollToX != 0) {
          if (mIsEnableClickScroll) {
            smoothScrollTo(scrollToX, 0);
            postInvalidate();
          }

          if (mOnItemClickListener != null) {
            mOnItemClickListener.onClick(this, scrollToPosition);
          }
        }
      }
    } else {
      if (mOnItemClickListener != null) {
        mOnItemClickListener.onClick(this, mCurrentPosition);
      }
      adjustItem();
    }
  }

  private void stopCheckAdjustFinished() {
    removeCallbacks(mCheckAdjustFinished);
  }

  @Override
  public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      int x = mScroller.getCurrX();
      int y = mScroller.getCurrY();
      scrollTo(x, y);
      postInvalidate();
    } else if (mScroller.isFinished() && mIsFlinging) {
      adjustItem();
    }
  }

  private void recycleVelocityTracker() {
    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
    }

    mVelocityTracker = null;
  }

  private int getMinimumScrollX() {
    return -((getDataSourceSize() - 1) * getItemSpace());
  }

  private int getMaximumScrollX() {
    return 0;
  }

  private boolean canScroll(int deltaX) {
    int scrollX = getScrollX() + deltaX;
    int max = 0;
    int min = getMinimumScrollX();
    return scrollX <= max && scrollX >= min;
  }

  /**
   * The {@code Runnable} to check whether adjust item finished, set the {@code mPreBehavior} to
   * {@code PRE_ACTION_NONE} if finished.
   */
  private Runnable mCheckAdjustFinished = new Runnable() {
    @Override
    public void run() {
      if (mScroller.isFinished()) {
        removeCallbacks(this);
        mPreBehavior = PRE_ACTION_NONE;
      } else {
        removeCallbacks(this);
        postDelayed(this, CHECK_ADJUST_FINISHED_DELAY);
      }
    }
  };

  /**
   * Adjust item by scroll to the nearest position
   */
  private void adjustItem() {
    removeCallbacks(mCheckAdjustFinished);
    int position = computeAdjustedPosition();
    int left = -position * getItemSpace();
    int scrollX = left - getScrollX();
    if (scrollX != 0) {
      smoothScrollTo(scrollX, 0);
      postInvalidate();
    }
    postDelayed(mCheckAdjustFinished, CHECK_ADJUST_FINISHED_DELAY);
  }

  private void smoothScrollTo(int x, int y) {
    smoothScrollTo(x, y, ADJUST_ITEM_DURATION);
  }

  private void smoothScrollTo(int x, int y, int duration) {
    mScroller.startScroll(getScrollX(), getScrollY(), x, y, duration);
  }

  /**
   * Compute the current position of the {@link #getScrollX()}
   * @return current position of the {@link #getScrollX()}
   */
  private int computePosition() {
    int itemSpace = getItemSpace();
    int scrollX = getScrollX();
    int prePositionX = -(mPrePosition * itemSpace);
    if (Math.abs(prePositionX - scrollX) >= itemSpace) {
      int position = Math.abs(scrollX / itemSpace);
      if (scrollX > prePositionX && position < mPrePosition - 1) {
        ++position;
      }
      return position;
    }
    return mPrePosition;
  }

  /**
   * Compute the nearest position when scroll over itemSpace / 2.
   * @return the adjusted position
   */
  private int computeAdjustedPosition() {
    int itemSpace = getItemSpace();
    int scrollX = getScrollX() - itemSpace / 2;
    return Math.abs(scrollX / itemSpace);
  }

  /**
   * The spacing between two items <br/>
   * <pre>
   *    | <- itemSpace -> | <- itemSpace -> |
   *    |                 |                 |
   *  item3             item2             item1
   * </pre>
   *
   * @return if {@link #mIsDefaultItemSpace} is {@code true}, return
   *         {@code getWidth() / getVisibleCount()}, if {@link #mIsDefaultItemSpace} is
   *         {@code false}, return the space set by user
   */
  private int getItemSpace() {
    if (!mIsDefaultItemSpace) {
      return mItemSpace;
    }
    if (mItemSpace == -1) {
      mItemSpace = getWidth() / getVisibleCount();
    }
    return mItemSpace;
  }

  /**
   * The visible item count on the screen
   * @return The visible item count on the screen
   */
  private int getVisibleCount() {
    return mVisibleCount;
  }

  /**
   * The total item count of data source
   * @return The total item count of data source
   */
  private int getDataSourceSize() {
    return mDataSourceSize;
  }

  /**
   * The default scroll x offset that first seen on the screen
   * @return The default scroll x offset first seen on the screen
   */
  private int getDefaultScrollXOffset() {
    return getWidth() / 2;
  }

  /**
   * Set the {@link ScrollableChartConfiguration}
   * @param configuration the {@link ScrollableChartConfiguration} for the view.
   */
  public void setScrollableChartConfiguration(ScrollableChartConfiguration configuration) {
    if (configuration == null) {
      throw new NullPointerException("The param configuration can't not be null.");
    }
    reset();
    this.mDrawing = configuration.getDrawing();
    this.mDataSourceSize = configuration.getDataSourceSize();
    this.mVisibleCount = configuration.getVisibleCount();
    this.mIsDefaultItemSpace = configuration.isDefaultItemSpace();
    if (!mIsDefaultItemSpace) {
      this.mItemSpace = configuration.getItemSpace();
    }
    this.mIsEnableClickScroll = configuration.isEnableClickScroll();
    postInvalidate();
  }

  /**
   * Re-initial the value
   */
  public void reset() {
    removeCallbacks(mCheckAdjustFinished);
    mPreBehavior = PRE_ACTION_NONE;
    mPrePosition = mCurrentPosition = 0;
  }

  /**
   * Register a callback to be invoked when a specific position was clicked.
   * @param listener The callback that will run
   */
  public void setOnItemClickListener(OnItemClickListener listener) {
    mOnItemClickListener = listener;
  }

  /**
   * Add a listener that will be notified when current position changed.
   * @param listener the listener that will be notified when current position changed.
   */
  public void addOnPositionChangeListener(OnPositionChangeListener listener) {
    if (mOnPositionChangeListeners == null) {
      mOnPositionChangeListeners = new ArrayList<>();
    }

    mOnPositionChangeListeners.add(listener);
  }

  /**
   * Remove a listener that was notified when current position changed.
   * @param listener the listener that will be notified when current position changed.
   */
  public void removeOnPositionChangeListener(OnPositionChangeListener listener) {
    if (mOnPositionChangeListeners != null) {
      mOnPositionChangeListeners.remove(listener);
    }
  }

  /**
   * Remove all secondary listener that were notified when current position changed.
   */
  public void clearOnPositionChangeListeners() {
    if (mOnPositionChangeListeners != null) {
      mOnPositionChangeListeners.clear();
    }
  }

  private void notifyOnPositionChange(int prePosition, int curPosition) {
    if (mOnPositionChangeListeners != null) {
      for (int i = mOnPositionChangeListeners.size() - 1; i >= 0; i--) {
        mOnPositionChangeListeners.get(i).onPositionChanged(this, prePosition, curPosition);
      }
    }
  }
}
