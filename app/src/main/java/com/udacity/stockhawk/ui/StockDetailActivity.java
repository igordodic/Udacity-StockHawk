package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;

import static android.R.attr.textColor;


public class StockDetailActivity extends AppCompatActivity {

  @BindView(R.id.stock_symbol_name) TextView tvSymbolName;
  @BindView(R.id.line_chart) LineChart lineChart;
  @BindView(R.id.tv_min_value) TextView tvMinValue;
  @BindView(R.id.tv_max_value) TextView tvMaxValue;
  @BindView(R.id.tv_avg_value) TextView tvAverageValue;

  String symbol;
  List<Entry> mEntries = new ArrayList<>();
  public LineData mLineData;
  private String yMin;
  private String yMax;
  private String yAverage;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_stock_detail);
    ButterKnife.bind(this);
    symbol = getIntent().getStringExtra("symbol");
    Timber.d(symbol);

    if (null != symbol) {
      tvSymbolName.setText(symbol);
      getHistory(symbol);
    }
  }

  private String getHistory(String symbol) {
    Cursor cursor =
        getContentResolver().query(Contract.Quote.makeUriForStock(symbol), null, null, null, null);
    String history = "";
    if (cursor != null && cursor.moveToFirst()) {
      history = cursor.getString(Contract.Quote.POSITION_HISTORY);
      cursor.close();
    }
    Timber.d(history);
    drawChart(history);
    generateStats();
    return history;
  }


  private void drawChart(String history) {

    CSVReader reader = new CSVReader(new StringReader(history));
    mEntries = new ArrayList<>();
    final List<Long> xAxisValues = new ArrayList<>();
    try {
      String[] nextLine;
      int xAxisPosition = 0;
      while ((nextLine = reader.readNext()) != null ) {
        xAxisValues.add(Long.valueOf(nextLine[0]));
        Float fY = Float.parseFloat(String.valueOf(nextLine[1]));
        mEntries.add(new Entry(xAxisPosition,fY));
        xAxisPosition++;
      }

      } catch (IOException e1) {
      e1.printStackTrace();
    }

    mLineData = new LineData(new LineDataSet(mEntries, symbol));
    mLineData.setValueTextColor(Color.WHITE);
    XAxis xAxis = lineChart.getXAxis();
    xAxis.setTextColor(Color.WHITE);
    xAxis.setValueFormatter(new IAxisValueFormatter() {
      @Override public String getFormattedValue(float value, AxisBase axis) {
        Date date = new Date(xAxisValues.get(xAxisValues.size() - (int) value - 1));
        return new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE).format(date);
      }
    });



    YAxis yAxisLeft = lineChart.getAxisLeft();
    yAxisLeft.setTextColor(Color.WHITE);

    YAxis yAxisRight = lineChart.getAxisRight();
    yAxisRight.setTextColor(Color.WHITE);

    Legend legend = lineChart.getLegend();
    legend.setTextColor(Color.WHITE);

    lineChart.getLegend().setTextColor(Color.WHITE);
    lineChart.setData(mLineData);
    }


  private void generateStats(){

    DecimalFormat df = new DecimalFormat("#.##");


    yMin = String.valueOf(df.format(mLineData.getYMin()));
    tvMinValue.setText(yMin);

    yMax = String.valueOf(df.format(mLineData.getYMax()));
    tvMaxValue.setText(yMax);

    Double total = 0.0;

    for (Entry entry : mEntries) {
      total += (Double.parseDouble(String.valueOf(entry.getY())));
    }
    yAverage = String.valueOf(df.format(total/mEntries.size()));
    tvAverageValue.setText(yAverage);
  }
}
