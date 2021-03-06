package com.littlegnal.scrollablechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.littlegnal.scrollablechart.drawing.CurveLineDrawing;
import com.littlegnal.scrollablechart.drawing.HistogramDrawing;
import com.littlegnal.scrollablechart.drawing.LineDrawing;
import com.littlegnal.scrollablechart.scrollablechartview.DataSourceProvider;
import com.littlegnal.scrollablechart.scrollablechartview.ScrollableChartConfiguration;
import com.littlegnal.scrollablechart.scrollablechartview.ScrollableChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

  ScrollableChartConfiguration mConfiguration;

  DataSourceProvider mSource = new MainDataSource();

  ScrollableChartView mScrollableChartView;

  TextView mIndexTv;

  TextView mPositionChangeTv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.line).setOnClickListener(this);
    findViewById(R.id.curve_line).setOnClickListener(this);
    findViewById(R.id.histogram).setOnClickListener(this);

    mIndexTv = (TextView) findViewById(R.id.index);
    mPositionChangeTv = (TextView) findViewById(R.id.positionChange);
    mScrollableChartView = (ScrollableChartView) findViewById(R.id.view);

    CurveLineDrawing curveLineDrawing = CurveLineDrawing.create(getApplicationContext(), mSource);
    ScrollableChartConfiguration.Builder builder = new ScrollableChartConfiguration.Builder()
        .setDrawing(curveLineDrawing)
        .setClickFilter(curveLineDrawing)
        .setIsDefaultItemSpace(true)
        .setIsEnableClickScroll(true)
        .setVisibleCount(7);
    mConfiguration = builder.build();
    mScrollableChartView.setScrollableChartConfiguration(mConfiguration);
    mScrollableChartView.setOnItemClickListener(new ScrollableChartView.OnItemClickListener() {
      @Override
      public void onClick(ScrollableChartView view, int itemPosition) {
        String index = "click position : " + itemPosition;
        mIndexTv.setText(index);
      }
    });

    mScrollableChartView.addOnPositionChangeListener(
        new ScrollableChartView.OnPositionChangeListener() {
          @Override
          public void onPositionChanged(ScrollableChartView view, int prePosition, int curPosition) {
            String text = "{ curPosition : " + curPosition + ", prePosition : " +
                prePosition + " }";
            mPositionChangeTv.setText(text);
          }
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.line:
        LineDrawing lineDrawing = LineDrawing.create(getApplicationContext(), mSource);
        mConfiguration.setDrawing(lineDrawing);
        mConfiguration.setClickFilter(lineDrawing);
        break;
      case R.id.curve_line:
        CurveLineDrawing curveLineDrawing =
            CurveLineDrawing.create(getApplicationContext(), mSource);
        mConfiguration.setDrawing(curveLineDrawing);
        mConfiguration.setClickFilter(curveLineDrawing);
        break;
      case R.id.histogram:
        HistogramDrawing histogramDrawing =
            HistogramDrawing.create(getApplicationContext(), mSource);
        mConfiguration.setDrawing(histogramDrawing);
        mConfiguration.setClickFilter(histogramDrawing);
        break;
      default:
        break;
    }
    mIndexTv.setText("");
    mPositionChangeTv.setText("");
    scrollToBegin();
    mScrollableChartView.setScrollableChartConfiguration(mConfiguration);
  }

  private void scrollToBegin() {
    mScrollableChartView.scrollTo(0, 0);
    mScrollableChartView.postInvalidate();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mScrollableChartView.clearOnPositionChangeListeners();
  }
}
