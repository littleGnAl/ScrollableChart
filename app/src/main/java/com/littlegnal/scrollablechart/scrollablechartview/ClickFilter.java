package com.littlegnal.scrollablechart.scrollablechartview;

import android.view.MotionEvent;

/**
 * Interface definition for determine current coordinate x and y can trigger the click event or not.
 */
public interface ClickFilter {

  /**
   * <p>Compute the clicked position of the {@link ScrollableChartView#onTouchEvent(MotionEvent)}.</p>
   *
   * It will return the {@code currentPosition} if the position not change or not click in the
   * correct area. You can define your logic to determine whether trigger the click event.
   *
   * @param viewWidth {@link ScrollableChartView#getWidth()}
   * @param viewHeight {@link ScrollableChartView#getHeight()}
   * @param itemSpace {@link ScrollableChartView#getItemSpace()}
   * @param currentPosition {@link ScrollableChartView#mCurrentPosition}
   * @param touchSlop {@link ScrollableChartView#mTouchSlop}
   * @param upX coordinate x of the {@link MotionEvent#ACTION_UP}
   * @param upY coordinate Y of the {@link MotionEvent#ACTION_UP}
   * @return the clicked position of {@link ScrollableChartView#onTouchEvent(MotionEvent)}
   */
  int computeClickedPosition(
      int viewWidth,
      int viewHeight,
      int itemSpace,
      int currentPosition,
      int touchSlop,
      int upX,
      int upY);
}
