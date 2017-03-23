package com.littlegnal.scrollablechart.scrollablechartview;

/**
 * The configuration of the {@link ScrollableChartView}, you can provide your owen {@link Drawing}
 * to implement your owen chart style.
 */
public class ScrollableChartConfiguration {

  private int mItemSpace;

  private int mVisibleCount;

  private int mDataSourceSize;

  private Drawing mDrawing;

  private boolean mIsDefaultItemSpace;

  private boolean mIsEnableClickScroll;

  private ScrollableChartConfiguration(Builder builder) {
    mItemSpace = builder.mItemSpace;
    mVisibleCount = builder.mVisibleCount;
    mDrawing = builder.mDrawing;
    mDataSourceSize = mDrawing.getDataSourceProvider().getDataSourceSize();
    mIsDefaultItemSpace = builder.mIsDefaultItemSpace;
    mIsEnableClickScroll = builder.mIsEnableClickScroll;
  }

  /**
   * Set the space between items
   * @param itemSpace the space between items
   */
  public void setItemSpace(int itemSpace) {
    this.mItemSpace = itemSpace;
  }

  public int getItemSpace() {
    return mItemSpace;
  }

  /**
   * Set the visible count on the screen
   * @param visibleCount the visible count on the screen
   */
  public void setVisibleCount(int visibleCount) {
    this.mVisibleCount = visibleCount;
  }

  public int getVisibleCount() {
    return mVisibleCount;
  }

  /**
   * Set the custom owen chart style {@link Drawing}
   * @param drawing {@link Drawing}
   */
  public void setDrawing(Drawing drawing) {
    this.mDrawing = drawing;
    this.mDataSourceSize = mDrawing.getDataSourceProvider().getDataSourceSize();
  }

  public Drawing getDrawing() {
    return mDrawing;
  }

  public int getDataSourceSize() {
    return mDataSourceSize;
  }

  /**
   * Set the flag to use the {@link #setItemSpace(int)} set by the user or not.
   * @param isDefaultItemSpace {@code true} to use the value of {@link #setItemSpace(int)},
*                              or use default.
   */
  public void setIsDefaultItemSpace(boolean isDefaultItemSpace) {
    this.mIsDefaultItemSpace = isDefaultItemSpace;
  }

  public boolean isDefaultItemSpace() {
    return this.mIsDefaultItemSpace;
  }

  /**
   * Set this flag indicates whether enable scroll to the click position
   * @param isEnableClickScroll {@code true} to enable scroll to the click position
   */
  public void setIsEnableClickScroll(boolean isEnableClickScroll) {
    this.mIsEnableClickScroll = isEnableClickScroll;
  }

  public boolean isEnableClickScroll() {
    return this.mIsEnableClickScroll;
  }

  public static class Builder {

    private int mItemSpace;

    private int mVisibleCount;

    private Drawing mDrawing;

    private boolean mIsDefaultItemSpace;

    private boolean mIsEnableClickScroll;

    public Builder setItemSpace(int itemSpace) {
      this.mItemSpace = itemSpace;
      return this;
    }

    public Builder setVisibleCount(int visibleCount) {
      this.mVisibleCount = visibleCount;
      return this;
    }

    public Builder setDrawing(Drawing drawing) {
      this.mDrawing = drawing;
      return this;
    }

    public Builder setIsDefaultItemSpace(boolean isDefaultItemSpace) {
      this.mIsDefaultItemSpace = isDefaultItemSpace;
      return this;
    }

    public Builder setIsEnableClickScroll(boolean isEnableClickScroll) {
      this.mIsEnableClickScroll = isEnableClickScroll;
      return this;
    }

    public ScrollableChartConfiguration build() {
      return new ScrollableChartConfiguration(this);
    }
  }
}
