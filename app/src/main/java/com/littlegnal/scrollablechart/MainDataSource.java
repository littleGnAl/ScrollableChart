package com.littlegnal.scrollablechart;

import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;

import java.util.Random;

public class MainDataSource implements DataSourceProvider {

  private float[] mSource;

  private final int SIZE = 1000;

  public MainDataSource() {
    mSource = new float[SIZE];
    float max = 0.8f;
    float min = 0.5f;
    Random random = new Random();
    for (int i = 0; i < SIZE; i++) {
      mSource[i] = (random.nextFloat() % (max - min) + min) * 100.0f;
    }
  }

  @Override
  public float getValueByIndex(int index) {
    if (index < 0 || index >= mSource.length) {
      throw new IndexOutOfBoundsException();
    }
    return mSource[index];
  }

  @Override
  public int getDataSourceSize() {
    return SIZE;
  }
}
