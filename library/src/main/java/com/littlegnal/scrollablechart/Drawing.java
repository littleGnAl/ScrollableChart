package com.littlegnal.scrollablechart;

import android.graphics.Canvas;

/**
 * @author littlegnal
 * @date 2017/3/4
 */

public interface Drawing {
  void draw(Canvas canvas, int selected, int start, int end);
}
