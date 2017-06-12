package com.littlegnal.scrollablechart;

import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

import java.util.Random;

public class MainDataSource implements DataSourceProvider<Float> {

  private float[] mSource;

  public MainDataSource() {
    int size = 1000;
    mSource = new float[size];
    float max = 0.8f;
    float min = 0.5f;
    Random random = new Random();
    for (int i = 0; i < size; i++) {
      mSource[i] = (random.nextFloat() % (max - min) + min) * 100.0f;
    }
  }

  @Override
  public Float getValueByIndex(int index) {
    if (index < 0 || index >= mSource.length) {
      throw new IndexOutOfBoundsException();
    }
    return mSource[index];
  }

  @Override
  public int getDataSourceSize() {
    return mSource.length;
  }
}
