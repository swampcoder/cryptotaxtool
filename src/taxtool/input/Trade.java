package taxtool.input;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ctc.enums.Currency;
import ctc.enums.TradeType;
import ctc.transactions.Transaction;

public class Trade extends RecordBase implements Comparable<Trade>, Serializable, IRecordInterface {

   private static final long serialVersionUID = 9101714017002104316L;

   public final static String FAKED_INPUT = "FAKED";

   private String crowdsaleIndicator = null;
   public Date date = null;
   public final long tradeTime;
   public final String sellCoin;
   public final double sellTotal;
   public final String buyCoin;
   public final double buyAmount;
   // public final double buyRate;
   public String exchange = null;
   private String notes = null;
   private String rawLine = null;
   private List<String> vectorLine;
   private String sellCoinTx = null;
   private String buyCoinTx = null;

   // transient state
   private transient double sellCoinTotal = 0d;
   private transient double buyCoinTotal = 0d;
   private transient int tradeIndex = 0;
   private transient Transaction ctcTX = null; // CTC class

   // price data for trade time/coins
   private transient Double btcPriceInUsd = null;
   private transient Double sellCoinBtcPrice = null;
   private transient Double buyCoinBtcPrice = null;

   public boolean isCrowdsale() {
      return this.crowdsaleIndicator != null;
   }

   public String getCrowdsale() {
      return this.crowdsaleIndicator;
   }

   public void setTotals(double buy, double sell) {
      this.sellCoinTotal = sell;
      this.buyCoinTotal = buy;
   }

   public Transaction getCTCTx() {
      return ctcTX;
   }

   public void setCTCTx(Transaction transaction) {
      this.ctcTX = transaction;
   }

   public void setSellCoinTx(String tx) {
      this.sellCoinTx = tx;
   }

   public String getSellCoinTx() {
      return sellCoinTx;
   }

   public void setBuyCoinTx(String tx) {
      this.buyCoinTx = tx;
   }

   public String getBuyCoinTx() {

      return this.buyCoinTx;
   }

   public void setTradeIndex(int i) {
      this.tradeIndex = i;
   }

   public int getTradeIndex() {
      return tradeIndex;
   }

   public double getBuyTotal() {
      return buyCoinTotal;
   }

   public double getSellTotal() {

      return sellCoinTotal;
   }

   public Trade(long tradetime, String inCoin, double inAmount, String outCoin, double outAmount, String exchange) {
      this.tradeTime = tradetime;
      this.buyCoin = inCoin;
      this.sellCoin = outCoin;
      this.sellTotal = outAmount;
      this.buyAmount = inAmount;
      this.exchange = exchange;
   }

   public Trade(String crowdsale, int year, int month, int day) {
      this.crowdsaleIndicator = crowdsale;
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      c.set(Calendar.DAY_OF_MONTH, day - 1);
      c.set(Calendar.YEAR, year);
      c.set(Calendar.MONTH, month - 1);
      c.setTimeZone(TimeZone.getTimeZone("UTC"));
      date = c.getTime();
      this.tradeTime = c.getTimeInMillis();
      this.rawLine = null;
      this.vectorLine = null;
      this.sellCoin = null;
      this.buyCoin = null;
      this.buyAmount = 0;
      this.exchange = null;
      this.sellTotal = 0d;
   }

   public Trade(String rawLine, long tradeTime, String sellCoin, double sellAmount, String buyCoin, double buyAmount,
         /* double buyRate, */ String exchange) {
      this.rawLine = rawLine;
      this.crowdsaleIndicator = null;
      vectorLine = Arrays.asList(rawLine.split(","));
      this.tradeTime = tradeTime;
      this.sellCoin = sellCoin;
      this.sellTotal = sellAmount;
      this.buyCoin = buyCoin;
      this.buyAmount = buyAmount;
      // this.buyRate = buyRate;
      this.exchange = exchange;

      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      c.setTimeInMillis(tradeTime);
      date = c.getTime();
   }

   public String getRawLine() {
      return rawLine;
   }

   public List<String> getVectorLine() {
      return vectorLine;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public String getNotes() {
      return notes;
   }

   public int getYear() {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(tradeTime);
      return cal.get(Calendar.YEAR);
   }

   @Override
   public String toString() {
      return "Trade index=" + tradeIndex + " year=" + getYear() + "  time=" + tradeTime + "  sell_coin=" + sellCoin
            + "  sell_total=" + sellTotal + "   buy_coin=" + buyCoin + "  buy_total=" + buyAmount + "    exchange="
            + exchange;
   }

   @Override
   public int compareTo(Trade o) {

      return -Long.compare(o.tradeTime, tradeTime);
   }

   public Transaction createCtcTx() throws IOException {
      Transaction tx = new Transaction();

      tx.date(tradeTime);
      Currency sellCurrency = Currency.valueOf(sellCoin);
      Currency buyCurrency = Currency.valueOf(buyCoin);

      tx.amount(this.buyAmount);

      tx.feeAmount(0);
      tx.feeCurrency(Currency.USD);

      // tx.localRate(this.buyRate);

      if (sellCurrency.isFiat()) {
         tx.major(buyCurrency);
         tx.minor(sellCurrency);
         tx.type(TradeType.BUY);
      } else {
         tx.major(sellCurrency);
         tx.minor(buyCurrency);
         tx.type(TradeType.SELL);
      }

      System.out.println("BUILDING " + this);
      tx.build();
      return tx;
   }

   public Double getBtcPriceAtTimeOfTrade() {
      return this.btcPriceInUsd;
   }

   public void initPriceData() throws IOException {
      this.buyCoinBtcPrice = HistoricalPriceTable.get("BTC").findPrice(buyCoin, tradeTime, false);
      this.sellCoinBtcPrice = HistoricalPriceTable.get("BTC").findPrice(sellCoin, tradeTime, false);

      Double oneDollarInBtc = HistoricalPriceTable.get("BTC").findPrice("USD", tradeTime, false);
      if (oneDollarInBtc == null)
         return;

      this.btcPriceInUsd = 1d / oneDollarInBtc;

   }

   private int masterIndex = -1;

   @Override
   public int getIndex() {

      return this.masterIndex;
   }

   @Override
   public void setIndex(int i) {
      this.masterIndex = i;
   }

   @Override
   public Long getTime() {

      return this.tradeTime;
   }

   @Override
   public String getCoin() {

      return buyCoin + "/" + sellCoin;
   }

   // Trades right now use "sell" amount for record interface impl
   @Override
   public Double getAmount() {
      return this.sellTotal;
   }

   @Override
   public MasterRecordData getRecordType(DataRecord data) {

      String txt = null;
      Color clr = null;
      txt = buyCoin + " / " + sellCoin + "  @  " + exchange;
      return new MasterRecordData(txt, clr);
   }

   @Override
   public boolean isUSDBuySell() {
      return "USD".equalsIgnoreCase(buyCoin) || "USD".equalsIgnoreCase(sellCoin);
   }

}
