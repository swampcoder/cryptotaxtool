package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataRecord {

   private final static IAddressResolver RESOLVER = IRankedService.resolveService(IAddressResolver.class);

   public static interface IDataRecordLoader {
      public DataRecord loadData(File f) throws ClassNotFoundException, IOException, ParseException;
   }

   public static interface IDataRecordListener {
      default public void notifyAddressLabel(Address addr, String label) {
      }

      default public void notifyAddressOwnshipChange(String address) {
      }
   }

   public static class MasterSorter implements Comparator<IRecordInterface> {
      @Override
      public int compare(IRecordInterface o1, IRecordInterface o2) {

         return Long.compare(o1.getTime(), o2.getTime());
      }

   }

   private final List<IRecordInterface> masterRecords = new ArrayList<IRecordInterface>();
   private final List<Trade> trades = new ArrayList<Trade>();
   private final List<Withdrawal> withdrawals = new ArrayList<Withdrawal>();
   private final List<Deposit> deposits = new ArrayList<Deposit>();
   private final List<EthereumTx> allEthTxs = new ArrayList<EthereumTx>();
   private final Map<String, List<EthereumTx>> etherscanCsv = new HashMap<String, List<EthereumTx>>();
   private final List<BitcoinTx> allBitcoinTxs = new ArrayList<BitcoinTx>();
   private Map<String, CoinTotal> coinMap = null;
   private Map<String, Set<Address>> coinAddressSets = null;
   private List<IDataRecordListener> listeners = null;
   private final Set<String> myAddresses = new HashSet<String>();
   private final Set<String> exchanges = new HashSet<String>();

   private final Set<String> depositTxHashes = new HashSet<String>();
   private final Set<String> withdrawalTxHashes = new HashSet<String>();

   public DataRecord() {
      super();
      listeners = new ArrayList<IDataRecordListener>();
   }

   public void addListener(IDataRecordListener listener) {
      listeners.add(listener);
   }

   public List<IRecordInterface> getMasterRecords() {
      return masterRecords;
   }

   public Iterable<CoinTotal> getCoins() {
      return coinMap.values();
   }

   public List<Trade> getTrades() {
      return trades;
   }

   public List<Withdrawal> getWithdrawals() {
      return withdrawals;
   }

   public List<Deposit> getDeposits() {
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

   public void backupToFile() {

   }

   public void initState() throws IOException {
      listeners = new ArrayList<IDataRecordListener>();
      if (coinMap != null)
         return; // coinMap.size() > 0) return;
      coinMap = new HashMap<String, CoinTotal>();
      coinAddressSets = new HashMap<String, Set<Address>>();
      int i = 0;
      for (Trade trade : trades) {
         initCoin(trade.buyCoin);
         initCoin(trade.sellCoin);
         inputTrade(trade);
         trade.setTradeIndex(i);
         trade.initPriceData();
         masterRecords.add(trade);
         i++;
      }

      i = 0;
      for (Withdrawal wd : withdrawals) {
         inputAddress(wd.getCoin(), wd.getCaseSensitiveAddress(), "WITHDRAWAL CSV");
         wd.setIndex(i);
         i++;
         masterRecords.add(wd);
         withdrawalTxHashes.add(wd.getTxHash());
      }

      i = 0;
      for (Deposit depo : deposits) {
         inputAddress(depo.getCoin(), depo.getAddress(), "DEPOSIT CSV");
         i++;
         masterRecords.add(depo);
         depositTxHashes.add(depo.getTxId());
      }

      i = 0;
      for (EthereumTx tx : allEthTxs) {
         tx.setIndex(i);
         i++;
         masterRecords.add(tx);
      }

      Collections.sort(masterRecords, new MasterSorter());
      i = 0;
      for (IRecordInterface rec : masterRecords) {
         rec.setIndex(i);
         i++;
      }
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

   private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
      is.defaultReadObject();
      initState();
   }

   private void writeObject(ObjectOutputStream os) throws IOException {
      os.defaultWriteObject();

   }

   private void initCoin(String coin) {
      CoinTotal coinTotal = coinMap.get(coin);
      if (coinTotal == null) {
         coinTotal = new CoinTotal(coin);
         coinMap.put(coin, coinTotal);
      }
   }

   private void inputTrade(Trade trade) {
      CoinTotal coinTotal = coinMap.get(trade.buyCoin);
      coinTotal.buy(trade.buyAmount);
      double buyTotal = coinTotal.getTotal();
      coinTotal = coinMap.get(trade.sellCoin);
      coinTotal.sell(trade.sellTotal);
      double sellTotal = coinTotal.getTotal();
      trade.setTotals(buyTotal, sellTotal);
   }
}
