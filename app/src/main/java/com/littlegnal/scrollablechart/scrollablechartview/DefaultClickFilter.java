package com.littlegnal.scrollablechart.scrollablechartview;

import android.view.ViewConfiguration;

/**
 * A default implementation of {@link ClickFilter}, which touch radius is
 * {@link ViewConfiguration#getScaledTouchSlop()}
 */
class DefaultClickFilter implements ClickFilter{

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
    // Adjust the touch radius, the radius of the default implementation is touchSlop
    if (upOffset > 0) {
      upOffset += touchSlop;
    } else {
      upOffset -= touchSlop;
    }
    int upOffsetCount = upOffset / itemSpace;

    return upOffsetCount != 0 ? currentPosition + upOffsetCount : currentPosition;
  }
}
