package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import ctc.calculator.CalculatedTransaction;
import ctc.enums.Currency;
import ctc.enums.TradeType;
import ctc.transactions.Transaction;

public class CryptoRecord implements Comparable<CryptoRecord> {

   private final static CryptoRecordNotes DATA = CryptoRecordNotes.get();
   private final RecordType recordType;
   private RecordSource source = null;
   private int recordIndex = -1;
   private long time = -1L;
   private int recordYear = -1;
   private String coinOrCoinIn = null;
   private Double coinInInUsd = null;
   private String coinOut = null;
   private Double coinOutInUsd = null;
   private double amountOrAmountIn = -1d;
   private double amountOut = -1d;
   private double feeAmount = 0d;
   private String feeCoin = null;
   private Double btcPriceInUSD = null; // could be indicated in coinbase CSV directly 
   private CryptoRecordNote recordData = null;
   private List<String> vectorLine;
   private String rawLine = null; // raw csv line parsed from file
   private String calcNotes = null; // attach any notes to record for debug inspection 
   private String crowdsale = null;
   private final Map<String,Double> totalsMap = new HashMap<String,Double>();
   private String exchange = null;
   private String txId = null; 
   private String exchangeId = null;
   private String addressTo = null; 
   private String addressToCS = null; // case sensitive version for block explorer inputs (original)
   private String addressFrom = null;
   private String addressFromCS = null; // case sensitive version for block explorer inputs (original)

   // gain loss processing
   private Transaction transaction = null;
   private CalculatedTransaction calcTransation = null;
   private String gainLossDescStr = null; // for debug 
   
   public CryptoRecord(RecordType recordType) {
      this.recordType = recordType;
   }
   
   public RecordType getRecordType() 
   {
      return recordType;
   }
   
   public CryptoRecordNote getData() 
   {
      if(recordData == null) {
         recordData = DATA.getRecord(this);
      }
      return recordData;
   }
   public int createRecordHash() 
   {
      int v =  37*Long.hashCode(time) +recordType.hashCode() *31 + 
            17 * Double.hashCode(amountOrAmountIn) + 57 * Double.hashCode(amountOut);
      if(this.coinOrCoinIn == null) System.out.println("Null coin for record type=" + getClass());
      else v += this.coinOrCoinIn.hashCode()*31;
      if(coinOut != null) v += coinOut.hashCode()*17;
      return v;
   }

   public void setRecordSource(RecordSource source) {
      this.source = source;
   }

   public RecordSource getRecordSource() {
      return source;
   }

   public void setRecordSource(File file, String rawLine) {
      this.source = new RecordSource(file, rawLine);
      setRawLine(rawLine);
   }
   
   public int getIndex()
   {
      return recordIndex;
   }

   public void setIndex(int i) {
      this.recordIndex = i;
   }

   public final long getTime()
   {
      return time;
   }
   
   public final void setTime(long time) 
   {
      if(time < 0) throw new IllegalArgumentException("invalid time value");
      this.time = time;
      Calendar c = Calendar.getInstance();
      c.setTimeZone(TimeZone.getTimeZone("UTC"));
      c.setTimeInMillis(time);
      recordYear = c.get(Calendar.YEAR);
   }
   
   public int getRecordYear() 
   {
      return recordYear;
   }
   
   @Override
   public int compareTo(CryptoRecord cr) 
   {
      return Long.compare(cr.time, time);
   }

   public String getCoinOrCoinIn()
   {
      return coinOrCoinIn;
   }
   
   public Currency getCurrencyOrCurrencyIn() 
   {
      if(coinOrCoinIn == null) return null;
      else if(Character.isDigit(coinOrCoinIn.charAt(0))) 
      {
         return Currency.synonymFilter(Currency.valueOf("_" + coinOrCoinIn));
      }
      return Currency.synonymFilter(Currency.valueOf(coinOrCoinIn));
   }
   
   public void setCoinOrCoinIn(String coinOrCoinIn) 
   {
      this.coinOrCoinIn = coinOrCoinIn;
   }
   
   public void setCoinInInUsd(double coinInInUsd) 
   {
      this.coinInInUsd = coinInInUsd;
   }
   
   public Double getCoinInInUsd() 
   {
      return this.coinInInUsd;
   }
   
   public String getCoinOut() 
   {
      return coinOut;
   }
   
   public Currency getCurrencyOut() 
   {
      if(coinOut == null) return null;
      else if(Character.isDigit(coinOut.charAt(0))) 
      {
         return Currency.synonymFilter(Currency.valueOf("_" + coinOut));
      }
      return Currency.synonymFilter(Currency.valueOf(coinOut));
   }
   
   public void setCoinOut(String coinOut)
   {
      this.coinOut = coinOut;
   }

   public Double getAmountOrAmountIn() {
      
      return amountOrAmountIn;
   }
   
   public double getAmountOut() 
   {
      return amountOut;
   }
   
   public void setAmountOut(double amountOut)
   {
      this.amountOut = amountOut;
   }
   
   public void setAmountOrAmountIn(double amount)
   {
      this.amountOrAmountIn = amount;
   }
   
   public String getTokenSymbol() 
   {
      return null; // overridden in EthereumTx
   }
   
   public Double getTotalTradeValue(USDTable table) 
   {
      Double outUsdPrice = table.getPriceInUSD(getCurrencyOut(), time);
      if(outUsdPrice != null) {
         return outUsdPrice * amountOut;
      }
      return null;
   }
   
   public void setCoinOutInUsd(double coinOutInUsd) 
   {
      this.coinOutInUsd = coinOutInUsd;
   }
   
   public double getCoinOutInUsd() 
   {
      return this.coinOutInUsd;
   }
   
   public boolean isInCoinUSD() 
   {
      return "USD".equalsIgnoreCase(coinOrCoinIn);
   }
   
   public boolean isOutCoinUSD() 
   {
      return "USD".equalsIgnoreCase(coinOut);
   }
   
   public void setFee(String feeCoin, double feeAmount) 
   {
      this.feeCoin = feeCoin;
      this.feeAmount = feeAmount;
   }
   
   public double getFeeAmount() 
   {
      return feeAmount;
   }
   
   public String getFeeCoin() {
      return feeCoin;
   }
   
   public void setBtcPriceInUSD(double btcPriceInUSD) 
   {
      this.btcPriceInUSD = btcPriceInUSD;
   }
   
   public Double getBtcPriceInUSD() 
   {
      return btcPriceInUSD;
   }
   
   public String getToAddress() 
   {
      return this.addressTo;
   }
   
   public String getToAddressCS() 
   {
      return this.addressToCS;
   }
   
   public String getFromAddress() 
   {
      return addressFrom;
   }
   
   public String getFromAddressCS() 
   {
      return addressFromCS;
   }
   
   public void setToAddress(String _addressTo)
   {
      this.addressToCS = _addressTo;
      this.addressTo = _addressTo.toUpperCase();
   }
   
   public void setFromAddress(String _addressFrom) 
   {
      this.addressFromCS = _addressFrom;
      this.addressFrom = addressFromCS.toUpperCase();
   }

   public List<String> getVectorLine() {
      return vectorLine;
   }
   
   public void setRawLine(String rawLine) 
   {
      Utils.notNull(rawLine);
      this.rawLine = rawLine;
      vectorLine = Arrays.asList(rawLine.split(","));
   }
   
   public void setCalcNotes(String calcNotes) 
   {
      this.calcNotes = calcNotes;
   }
   
   public String getCalcNotes() 
   {
      return this.calcNotes;
   }
   
   public void setExchange(String _exchange) 
   {
      exchange = _exchange;
   }
   
   public String getExchange() 
   {
      return exchange;
   }
   
   public void setExchangeId(String exchangeId) 
   {
      this.exchangeId = exchangeId;
   }
   
   public String getExchangeId() 
   {
      return exchangeId;
   }
   
   public void setTxId(String txId) 
   {
      this.txId = txId;
   }
   
   public String getTxId() 
   {
      return this.txId;
   }
   
   public String getRawLine() 
   {
      return rawLine;
   }
   
   public int getYear() {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(time);
      return cal.get(Calendar.YEAR);
   }
   
   public boolean isCrowdsale() 
   {
      return crowdsale != null;
   }
   
   public String getCrowdsale() 
   {
      return crowdsale;
   }

   public void setCrowdsale(String crowdsale, int year, int month, int day) 
   {
      this.crowdsale = crowdsale;
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      c.set(Calendar.DAY_OF_MONTH, day - 1);
      c.set(Calendar.YEAR, year);
      c.set(Calendar.MONTH, month - 1);
      c.setTimeZone(TimeZone.getTimeZone("UTC"));
      this.time = c.getTimeInMillis();
   }
   
   public void setTotals(Map<String, Double> currTotals) 
   {
      this.totalsMap.putAll(currTotals);
   }
   
   public boolean isTrade() 
   {
      return getRecordType() == RecordType.Trade; //|| getData().isTaxable();   
   }
   
   public double priceOf1BtcInUsd() 
   {
      if(coinOrCoinIn.equalsIgnoreCase("usd") && 
               coinOut.equalsIgnoreCase("btc"))
      {
         return this.amountOrAmountIn / amountOut;
      }
      else if(coinOrCoinIn.equalsIgnoreCase("btc") && 
            coinOut.equalsIgnoreCase("usd")) 
      {
         return this.amountOut / this.amountOrAmountIn;
      }
      throw new IllegalStateException("Call check func first");
   }
   
   public boolean canComputeBtcPriceFromTrade() 
   {
      if( coinOrCoinIn != null && coinOut != null) 
      {
         return (coinOrCoinIn.equalsIgnoreCase("usd") && 
               coinOut.equalsIgnoreCase("btc")) || 
               
               (coinOrCoinIn.equalsIgnoreCase("btc") && 
                     coinOut.equalsIgnoreCase("usd"));
               
      }
      return false;
   }
   
   public Transaction getTransaction() 
   {
      return transaction;
   }
   
   /*
    * btc1.type(TradeType.BUY).amount(0.5).localRate(100).major("BTC").minor("ETH").date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)).feeAmount(0).feeCurrency("USD").build();
      btc2.type(TradeType.SELL).amount(0.4).localRate(4000).major("BTC").minor("ETH").date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(10)).feeAmount(0).feeCurrency("USD").build();
      
    */
   
   public Transaction createTransaction() throws IOException 
   {
      if(!this.isTrade()) {
         transaction = null;
         return null;
      }
      transaction = new Transaction();
      transaction.date(time);
      System.out.println("Creating transaction for index=" + this.recordIndex + "    raw=" + getRawLine());
      
      Currency sellCurrency = this.getCurrencyOut(); // minor 
      Currency buyCurrency = this.getCurrencyOrCurrencyIn();// major
      Utils.notNull(sellCurrency);
      Utils.notNull(buyCurrency);

      String descStr = "";
      if (sellCurrency.isFiat()) {
         transaction.major(buyCurrency);
         transaction.minor("USD");
         transaction.type(TradeType.BUY);
         transaction.amount(getAmountOrAmountIn());
         transaction.localRate(this.getBtcPriceInUSD());
         descStr += "Created USD -> BTC (BUY) @ RATE " + transaction.getLocalRate() + "   Major=" + transaction.getMajor() + "   Minor=" + 
               transaction.getMinor()  + "  Amount=" + transaction.getAmount();
      }
      else if(buyCurrency.isFiat())
      {
         Thread.dumpStack();
         transaction.major(sellCurrency);
         transaction.minor("USD");
         transaction.type(TradeType.SELL);
         transaction.localRate(this.getBtcPriceInUSD());
         transaction.amount(getAmountOut());
         descStr += "Created BTC -> USD (SELL) @ RATE " + transaction.getLocalRate()  + "   Major=" + transaction.getMajor() + "   Minor=" + 
               transaction.getMinor() + "  Amount=" + transaction.getAmount();
      }
      else
      {
         double btcInUsd = 0d; // USD price of 1 BTC
         double buyCoinInBtc = 0; // BTC amount in terms of BUY COIN amount 
         
         double sellCoinInBtc = 0; // BTC amount in terms of SELL COIN amount
         double buyCoinInUsd1 = 0; // USD value of 1 BUY COIN 
         double sellCoinUsd1 = 0; // USD value of 1 SELL COIN
         throw new RuntimeException("TODO");
      }

      transaction.feeAmount(0);
      transaction.feeCurrency(Currency.USD);
      
      gainLossDescStr = descStr;
      
      transaction.build();
      
      return transaction;
   }
   
   public void setCalculatedTransaction(CalculatedTransaction ct) 
   {
      this.calcTransation = ct;
   }
   
   public CalculatedTransaction getCalculatedTransaction() 
   {
      return this.calcTransation;
   }
   
   public String getGainLossDesc() 
   {
      return this.gainLossDescStr;
   }
   

   // from old Trade class 
   /*
   public void initPriceData() throws IOException {
      this.buyCoinBtcPrice = HistoricalPriceTable.get("BTC").findPrice(getCoinOrCoinIn(), getTime(), false);
      this.sellCoinBtcPrice = HistoricalPriceTable.get("BTC").findPrice(getCoinOut(), getTime(), false);

      Double oneDollarInBtc = HistoricalPriceTable.get("BTC").findPrice("USD", getTime(), false);
      if (oneDollarInBtc == null)
         return;

      this.btcPriceInUsd = 1d / oneDollarInBtc;

   }*/
}
