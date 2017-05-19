package com.udacity.stockhawk.widget;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import timber.log.Timber;

/**
 * Created by Igor on 02/04/2017.
 *
 * Part of the widget code was inspired from the hackathon event
 */

public class StockWidgetService extends RemoteViewsService {

  @Override public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new StockWidgetFactory(getApplicationContext());
  }

  private class StockWidgetFactory implements RemoteViewsFactory {
    private final Context mContext;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat percentageFormat;

    private List<ContentValues> mCvList = new ArrayList<>();
    public StockWidgetFactory(Context applicationContext) {

      mContext = applicationContext;

      dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
      dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
      dollarFormatWithPlus.setPositivePrefix("+$");
      percentageFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.getDefault());
      percentageFormat.setMaximumFractionDigits(2);
      percentageFormat.setMinimumFractionDigits(2);
      percentageFormat.setPositivePrefix("+");
    }

    @Override public void onCreate() {
      getData();
    }

    private void getData() {
      ContentResolver contentResolver = mContext.getContentResolver();
      mCvList.clear();

      Cursor cursor =
          contentResolver.query(Contract.Quote.URI, null, null, null, null,
              null);
      assert cursor != null;
      cursor.moveToFirst();

      while (cursor.moveToNext()) {
        String symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
        float price = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        float absoluteChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
        float percentageChange = cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));

        ContentValues cv = new ContentValues();
        cv.put(Contract.Quote.COLUMN_SYMBOL, symbol);
        cv.put(Contract.Quote.COLUMN_PRICE, price);
        cv.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, absoluteChange);
        cv.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentageChange);
        mCvList.add(cv);
      }
      cursor.close();
    }

    @Override public void onDataSetChanged() {
        getData();
    }

    @Override public void onDestroy() {

    }

    @Override public int getCount() {
      return mCvList.size();
    }

    @Override public RemoteViews getViewAt(int position) {

      ContentValues cv = mCvList.get(position);
      RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                                                R.layout.list_item_quote);

      remoteViews.setTextViewText(R.id.symbol,cv.getAsString(Contract.Quote.COLUMN_SYMBOL));
      remoteViews.setTextViewText(R.id.price,dollarFormat.format(cv.getAsFloat(Contract.Quote.COLUMN_PRICE)));

      float absoluteChange = cv.getAsFloat(Contract.Quote.COLUMN_ABSOLUTE_CHANGE);
      float percentageChange = cv.getAsFloat(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
      Timber.d(String.valueOf(percentageChange));
      if (absoluteChange>0){
        remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);
      } else
        remoteViews.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);

      remoteViews.setTextViewText(R.id.change, percentageFormat.format(percentageChange));
      return remoteViews;
    }

    @Override public RemoteViews getLoadingView() {
      return null;
    }

    @Override public int getViewTypeCount() {
      return 1;
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public boolean hasStableIds() {
      return false;
    }
  }
}
