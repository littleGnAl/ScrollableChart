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
