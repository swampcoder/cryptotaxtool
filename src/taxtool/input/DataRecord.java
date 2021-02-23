package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ctc.calculator.CalculatedTransaction;
import ctc.calculator.CalculatedTransactionFile;
import ctc.calculator.IPriceInterface;
import ctc.calculator.PriceRef;
import ctc.enums.Currency;
import ctc.transactions.Transaction;

public class DataRecord {

   private final static int MAX_INDEX = 300;
   
   private final static IAddressResolver RESOLVER = PersonalData.getAddrResolver();

   public static interface IDataRecordListener {
      default public void notifyAddressLabel(Address addr, String label) {
      }

      default public void notifyAddressOwnshipChange(String address) {
      }
      default public void notifyCalcComplete() {}
   }

   public static class MasterSorter implements Comparator<CryptoRecord> {
      @Override
      public int compare(CryptoRecord o1, CryptoRecord o2) {

         return Long.compare(o1.getTime(), o2.getTime());
      }
   }

   private final Set<String> CoinSet = new HashSet<String>();
   private final List<CryptoRecord> masterRecords = new ArrayList<CryptoRecord>();
   private final List<CryptoRecord> trades = new ArrayList<CryptoRecord>();
   private final List<CryptoRecord> withdrawals = new ArrayList<CryptoRecord>();
   private final List<CryptoRecord> deposits = new ArrayList<CryptoRecord>();
   private final List<EthereumTx> allEthTxs = new ArrayList<EthereumTx>();
   private final Map<String, List<EthereumTx>> etherscanCsv = new HashMap<String, List<EthereumTx>>();
   private final List<BitcoinTx> allBitcoinTxs = new ArrayList<BitcoinTx>();
   private Map<String, CoinTotal> coinMap = null;
   private Map<String, Set<Address>> coinAddressSets = null;
   private List<IDataRecordListener> listeners = null;
   private final Set<String> myAddresses = new HashSet<String>();
   private final Set<String> exchanges = new HashSet<String>();

   // USD Table
   private final IPriceInterface usdTable = PriceRef.get();
   
   private final Set<String> depositTxHashes = new HashSet<String>();
   private final Set<String> withdrawalTxHashes = new HashSet<String>();
   
   private final Map<String,Double> coinTotals = new HashMap<String, Double>();
   private final Map<Long, Map<String, Double>> timeToCoinTotals = new HashMap<Long,Map<String,Double>>();

   private CalculatedTransactionFile taxData = null;
   
   public DataRecord() throws IOException {
      super();
      listeners = new ArrayList<IDataRecordListener>();
   }
   
   public IPriceInterface getUSDTable() 
   {
      return usdTable;
   }
   
   public void processGains() throws IOException 
   {
      ArrayList<Transaction> transactions = new ArrayList<Transaction>();
      for(CryptoRecord cr : masterRecords) 
      {
         if(cr.getTime() == 0) continue; // ignore 
         if(cr.isCrowdsale()) continue;// ignore for now
         if(cr.isSend()) continue; // 
         if(cr.isRcv()) continue;
         
         if(cr.getIndex() > MAX_INDEX) { // for testing 
            System.out.println("!!!!!!!!!!!!!!!STOPPING AT MAX_INDEX!!!!!!!!!!!!!!!!!!!!!!");
            break;
         }
         Transaction tx = cr.createTransaction(this);
         if(tx == null) continue;
         tx.setLinkedObj(cr);
         transactions.add(tx);
      }
      taxData = new CalculatedTransactionFile(transactions);
      taxData.outputAssets();
      for(Transaction ct : taxData.getTransactions()) 
      {
         CalculatedTransaction calcTx  = (CalculatedTransaction) ct;
         CryptoRecord cr = (CryptoRecord) calcTx.getLinkedObject();
         cr.setCalculatedTransaction(calcTx);
      }
      
      for(IDataRecordListener l : listeners) 
      {
         l.notifyCalcComplete();
      }
   }

   public void addListener(IDataRecordListener listener) {
      listeners.add(listener);
   }

   public List<CryptoRecord> getMasterRecords() {
      return masterRecords;
   }

   public Iterable<CoinTotal> getCoins() {
      return coinMap.values();
   }

   public List<CryptoRecord> getTrades() {
      return trades;
   }

   public List<CryptoRecord> getWithdrawals() {
      return withdrawals;
   }

   public List<CryptoRecord> getDeposits() {
      return deposits;
   }

   public List<BitcoinTx> getAllBitcoinTxs() {
      return this.allBitcoinTxs;
   }

   public void storeEthList(List<EthereumTx> txList) {
      this.allEthTxs.addAll(txList);
   }

   public List<EthereumTx> getFullEthTxList() {
      return allEthTxs;
   }

   public List<EthereumTx> getEtherscan(String address) {
      List<EthereumTx> etherscanList = etherscanCsv.get(address);
      if (etherscanList == null) {
         etherscanList = new ArrayList<EthereumTx>();
         etherscanCsv.put(address, etherscanList);
      }
      return etherscanList;
   }

   public Iterable<String> getEtherscanAddresses() {
      return etherscanCsv.keySet();
   }

   public Iterable<String> getAddressedCoins() {
      return coinAddressSets.keySet();
   }

   public Set<Address> getAddresses(String coin) {
      return coinAddressSets.getOrDefault(coin, Collections.emptySet());
   }

   public String getAddressLabel(String address) {
      String coin = "only used for eth right now";
      String label = RESOLVER.getLabel(coin, address);

      return label;
   }

   public Iterable<String> getExchanges() {
      return exchanges;
   }

   public void addExchange(String exchange) {
      this.exchanges.add(exchange);
   }

   public boolean isWithdrawal(String txHash) {
      return this.withdrawalTxHashes.contains(txHash);
   }

   public boolean isDeposit(String txHash) {
      return this.depositTxHashes.contains(txHash);
   }

   public void writeToCsv(File outputFile) throws IOException {
      outputFile.createNewFile();
   }

   public void initState() throws IOException {
      
      Collections.sort(getTrades() );
      Collections.sort(getWithdrawals());
      Collections.sort(getFullEthTxList());
      
      listeners = new ArrayList<IDataRecordListener>();
      if (coinMap != null)
         return; // coinMap.size() > 0) return;
      coinMap = new HashMap<String, CoinTotal>();
      coinAddressSets = new HashMap<String, Set<Address>>();
      int i = 0;
      for (CryptoRecord trade : trades) {
         initCoin(trade.getCoinOrCoinIn());
         initCoin(trade.getCoinOut());
         //trade.initPriceData();
         masterRecords.add(trade);
         i++;
      }

      i = 0;
      for (CryptoRecord wd : withdrawals) {
         inputAddress(wd.getCoinOrCoinIn(), wd.getToAddressCS(), "WITHDRAWAL CSV");
         wd.setIndex(i);
         i++;
         masterRecords.add(wd);
         withdrawalTxHashes.add(wd.getTxId());
      }

      for (CryptoRecord depo : deposits) {
         inputAddress(depo.getCoinOrCoinIn(), depo.getToAddressCS(), "DEPOSIT CSV");
         masterRecords.add(depo);
         depositTxHashes.add(depo.getTxId());
      }

      for (EthereumTx tx : allEthTxs) {
         masterRecords.add(tx);
      }
      
      for(BitcoinTx tx : allBitcoinTxs) 
      {
         masterRecords.add(tx);
      }

      Map<Currency, Double> holdAmounts = new HashMap<Currency, Double>();
      Collections.sort(masterRecords, new MasterSorter());
      i = 0;
      for (CryptoRecord rec : masterRecords) {
         rec.setIndex(i);
         i++;
         inputRecord(rec);
         
         // if trade, update in/out totals with trade values
         if(rec.isTrade()) 
         {
            Currency inCoin = rec.getCurrencyOrCurrencyIn();
            Currency outCoin = rec.getCurrencyOut();
            Utils.notNull(inCoin, outCoin);
            double outHolding = holdAmounts.getOrDefault(outCoin, 0d);
            double inHolding= holdAmounts.getOrDefault(inCoin, 0d);
            outHolding -= rec.getAmountOut();
            inHolding += rec.getAmountOrAmountIn();
            holdAmounts.put(outCoin,outHolding);
            holdAmounts.put(inCoin, inHolding);
            rec.setInCoinHolding(inHolding);
            rec.setOutCoinHolding(outHolding);
         }
         // print the enum syntax to update the CalcGain tool file
         if(rec.getCoinOut() != null) CoinSet.add(rec.getCoinOut());
         if(rec.getCoinOrCoinIn() != null) CoinSet.add(rec.getCoinOrCoinIn());
         
         if(rec.getTime() < 0) throw new IllegalStateException("Invalid time for=" + rec.getRawLine());
      }
      
      String enumSyntax = "";
      for(String coin : CoinSet) 
      {
         enumSyntax +="," + coin.toUpperCase();
      }
      //System.out.println(enumSyntax);  
   }

   private void inputAddress(String coin, String address, String source) {
      Set<Address> addressSet = coinAddressSets.get(coin);
      if (addressSet == null) {
         addressSet = new HashSet<Address>();
         coinAddressSets.put(coin, addressSet);
      }
      Address addr = new Address(coin, address);

      if (addressSet.add(addr)) {
         addr.setSource(source);
      }
   }

   public boolean isAddressMine(String address) {
      return myAddresses.contains(address.toUpperCase());
   }

   public void setAddressOwnership(String address, boolean myowned) {
      boolean ok = false;
      address = address.toUpperCase();
      if (myowned)
         ok = myAddresses.add(address);
      else
         ok = myAddresses.remove(address);
      if (ok) {
         for (IDataRecordListener listener : listeners) {
            listener.notifyAddressOwnshipChange(address);
         }
      }
   }

   private void initCoin(String coin) {
      CoinTotal coinTotal = coinMap.get(coin);
      if (coinTotal == null) {
         coinTotal = new CoinTotal(coin);
         coinMap.put(coin, coinTotal);
      }
   }

   private void inputRecord(CryptoRecord rec) {
      
      if(rec.isTrade())
      {
         double inValue = coinTotals.getOrDefault(rec.getCoinOrCoinIn(), 0d);
         inValue += rec.getAmountOrAmountIn();
         
         double outValue = coinTotals.getOrDefault(rec.getCoinOrCoinIn(), 0d);
         outValue -= rec.getAmountOut();
         
         coinTotals.put(rec.getCoinOut(), outValue);
         coinTotals.put(rec.getCoinOrCoinIn(), inValue);
      }
      
      rec.setTotals(coinTotals); // each record has a copy of coin totals at that time 
   }
}
