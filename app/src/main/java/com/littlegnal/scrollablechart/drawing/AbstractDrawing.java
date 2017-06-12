package com.littlegnal.scrollablechart.drawing;

import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;
import com.littlegnal.scrollablechart.scrollablechartview.Drawing;
import com.littlegnal.scrollablechart.scrollablechartview.ScrollableChartView;

public abstract class AbstractDrawing implements Drawing {

  protected DataSourceProvider mDataSourceProvider;

  public AbstractDrawing(DataSourceProvider dataSourceProvider) {
    this.mDataSourceProvider = dataSourceProvider;
  }

  @Override
  public void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
    this.mDataSourceProvider = dataSourceProvider;
  }

  @Override
  public DataSourceProvider getDataSourceProvider() {
    return this.mDataSourceProvider;
  }

  /**
   * Make the coordinate x of the path between [0, (mVisibleCount + 4) * itemSpace]
   * @param itemSpace {@link ScrollableChartView#getItemSpace()}
   * @param start the start position
   * @param index the drawing index
   * @return return the value between [0, (mVisibleCount + 4) * itemSpace]
   */
  protected float getAdjustedXByIndex(int itemSpace, int start, int index) {
    return -(index * itemSpace - start * itemSpace);
  }

  protected float getAdjustedYByIndex(int height, int index) {
    float value = (float) mDataSourceProvider.getValueByIndex(index);
    float ratio = value / 100.0f;
    return height - height * ratio;
  }

  protected int getDataSourceSize() {
    return mDataSourceProvider.getDataSourceSize();
  }
}
