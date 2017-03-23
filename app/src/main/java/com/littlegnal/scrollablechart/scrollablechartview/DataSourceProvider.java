package com.littlegnal.scrollablechart.scrollablechartview;

/**
 * Interface definition for provide the data. We always cache the data to the array or list, and we
 * can get the value by index. We use this to provide data to the drawing.
 */
public interface DataSourceProvider {

  /**
   * Get the value of the index
   * @param index the drawing index
   * @return the value of the index
   */
  float getValueByIndex(int index);

  /**
   * The data source size.
   * @return The data source size.
   */
  int getDataSourceSize();
}
