package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.udacity.stockhawk.sync.AsyncResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public class AddStockDialog extends DialogFragment implements AsyncResponse {

    @SuppressWarnings("WeakerAccess") @BindView(R.id.dialog_stock) EditText stockEditText;
    public Boolean isValid = false;
    StockIsValidCheck stockAsyncTask = new StockIsValidCheck();
  private Context mContext;
  private Activity mActivity;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom =
            inflater.inflate(R.layout.add_stock_dialog, null);
        mContext = getActivity().getApplicationContext();
        mActivity = getActivity();

        ButterKnife.bind(this, custom);

        stockEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addStock();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void addStock() {
        isValid = false;

        String stockName = stockEditText.getText().toString();

      if(!containsInvalidCharacters(stockName)) {
        stockAsyncTask.delegate = this;
        stockAsyncTask.execute(stockName);
      } else {
        Toast.makeText(mContext, R.string.stock_invalid, Toast.LENGTH_LONG).show();
        isValid = false;
      }
    }

  private boolean containsInvalidCharacters(String stockName) {
    if (stockName == null || stockName.isEmpty()) return false;

    Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
    Matcher m = pattern.matcher(stockName);
    return m.find();
  }

  // Since we want to avoid waiting for the AsyncTask and block the UI thread,
    // this method will add a stock if it exists
  @Override public void stockVerification(String stockName) {

      if (mActivity instanceof MainActivity) {
        ((MainActivity) mActivity).addStock(stockName);
        dismissAllowingStateLoss();
      }
  }


  private class StockIsValidCheck extends AsyncTask<String, Void, Stock> {
    public AsyncResponse delegate = null;

    @Override protected Stock doInBackground(String... params) {
      try {
        return YahooFinance.get(params[0]);
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override protected void onPostExecute(Stock stock) {
      if (stock != null) {
        if (stock.isValid()) {
          isValid = true;
          delegate.stockVerification(stock.getSymbol());
        } else {
          Toast.makeText(mContext, R.string.stock_invalid, Toast.LENGTH_LONG).show();
          isValid = false;
        }
      }
    }
  }
}



