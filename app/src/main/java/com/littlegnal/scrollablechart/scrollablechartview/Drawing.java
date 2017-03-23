package com.littlegnal.scrollablechart.scrollablechartview;

import android.graphics.Canvas;

/**
 * The drawing for {@link ScrollableChartView}, you can easily implement your owen chart style
 * by implement this interface.
 */
public interface Drawing {

  /**
   * This will be invoked by {@link ScrollableChartView#onDraw(Canvas)}, draw your owen chart style
   * here.
   * @param canvas the {@code canvas} of {@link ScrollableChartView#onDraw(Canvas)}
   * @param changed indicate whether current position changed
   * @param width {@link ScrollableChartView#getWidth()}
   * @param height {@link ScrollableChartView#getHeight()}
   * @param itemSpace {@link ScrollableChartView#getItemSpace()}
   * @param selected the selected position (current position)
   * @param start {@code selectedPosition - (visibleCount / 2 + 2)}
   * @param end {@code selectedPosition + (visibleCount / 2 + 2)}
   * @param scrollToNextPositionRatio the ratio of the current scroll distance to the next position,
   *                                  the negative value means that scrolling to right, or scrolling
   *                                  to left.
   */
  void draw(
      Canvas canvas,
      boolean changed,
      int width,
      int height,
      int itemSpace,
      int selected,
      int start,
      int end,
      float scrollToNextPositionRatio);

  /**
   * Set the {@link DataSourceProvider} to provide drawing value.
   * @param dataSourceProvider the {@link DataSourceProvider}
   */
  void setDataSourceProvider(DataSourceProvider dataSourceProvider);

  /**
   * Get the {@link DataSourceProvider} set for this {@code Drawing}
   * @return the {@link DataSourceProvider} set for this {@code Drawing}
   */
  DataSourceProvider getDataSourceProvider();
}
