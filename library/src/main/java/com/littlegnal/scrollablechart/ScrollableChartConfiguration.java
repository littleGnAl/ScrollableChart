package com.littlegnal.scrollablechart;

/**
 * @author littlegnal
 * @date 2017/3/4
 */

public class ScrollableChartConfiguration {

  private int mItemSpace;

  private int mVisibleCount;

  private int mDataSourceSize;

  private DataSourceProvider mDataSourceProvider;

  private Drawing mDrawing;

  public int getItemSpace() {
    return mItemSpace;
  }

  public void setItemSpace(int itemSpace) {
    this.mItemSpace = itemSpace;
  }

  public int getVisibleCount() {
    return mVisibleCount;
  }

  public void setVisibleCount(int visibleCount) {
    this.mVisibleCount = visibleCount;
  }

  public int getDataSourceSize() {
    return mDataSourceSize;
  }

  public void setDataSourceSize(int dataSourceSize) {
    this.mDataSourceSize = dataSourceSize;
  }

  public DataSourceProvider getDataSourceSupplier() {
    return mDataSourceProvider;
  }

  public void setDataSourceSupplier(DataSourceProvider dataSourceProvider) {
    this.mDataSourceProvider = dataSourceProvider;
  }

  public Drawing getDrawing() {
    return mDrawing;
  }

  public void setDrawing(Drawing drawing) {
    this.mDrawing = drawing;
  }
}
