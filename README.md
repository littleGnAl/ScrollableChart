# ScrollableChart
 A scrollable chart view that adjust the nearest position when fling or move end.
 You can easy to implement your owen chart style by implement the `Drawing` interface.
 And this is a solution of `"W/OpenGLRenderer(29239): Shape path too large to be rendered into a texture"`.

 ![screentshot](./screenshot/screenshot.gif)
 # Usage
 ```java
 mScrollableChartView = (ScrollableChartView) findViewById(R.id.view);

ScrollableChartConfiguration.Builder builder = new ScrollableChartConfiguration.Builder()
        .setDrawing(HistogramDrawing.create(getApplicationContext(), mSource))
        .setIsDefaultItemSpace(true)
        .setIsEnableClickScroll(true)
        .setVisibleCount(7);
    mConfiguration = builder.build();
    mScrollableChartView.setScrollableChartConfiguration(mConfiguration);
    mScrollableChartView.setOnItemClickListener(new ScrollableChartView.OnItemClickListener() {
      @Override
      public void onClick(ScrollableChartView view, int itemPosition) {
        ...
      }
    });

    mScrollableChartView.addOnPositionChangeListener(
        new ScrollableChartView.OnPositionChangeListener() {
          @Override
          public void onPositionChanged(ScrollableChartView view, int prePosition, int curPosition) {
            ...
          }
    });
 ```
 You can easy to implement your owen chart style by implement the `Drawing` interface
 ```java
 public class CurveLineDrawing implements Drawing {

  @Override
  public void draw(
      Canvas canvas,
      boolean changed,
      int width,
      int height,
      int itemSpace,
      int selected,
      int start,
      int end,
      float scrollToNextPositionRatio) {
    ...
  }
}
 ```
 Sometimes you may want to implement your own touch logic to trigger the click event, which often need to touch on a specific area, such, touch the circle area in the curve line chart, touch the bubble in the line chart, or touch the histogram in the histogram chart in the demo drawing. So you can implement your own touch logic by implement the `ClickFilter` interface like the `DefaultClickFilter`.
 ```java
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
 ```
# Contribute
Welcome for issues or PR
# License

    Copyright (C) 2017 littlegnal

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
