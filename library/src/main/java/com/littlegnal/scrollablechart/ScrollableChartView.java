package com.littlegnal.scrollablechart;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author littlegnal
 * @date 2017/3/1
 */

public class ScrollableChartView extends View {
  public ScrollableChartView(Context context) {
    super(context);
  }

  public ScrollableChartView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ScrollableChartView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ScrollableChartView(
      Context context,
      AttributeSet attrs,
      int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }
}
