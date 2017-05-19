package com.udacity.stockhawk.sync;

import yahoofinance.YahooFinance;

/**
 * Created by Igor on 17/04/2017.
 */

public class YahooFinance2 extends YahooFinance {
  public static final String QUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.quotes", "https://finance.yahoo.com/d/quotes.csv");
  public static final String HISTQUOTES_BASE_URL = System.getProperty("yahoofinance.baseurl.histquotes", "https://ichart.yahoo.com/table.csv");

}
