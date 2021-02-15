package taxtool.input;

import java.io.Serializable;

public class Withdrawal extends RecordBase implements Serializable, Comparable<Withdrawal>, IRecordInterface {
   private static final long serialVersionUID = 6872715302157074814L;

   private int index = -1;

   // Id,Time,Symbol,Name,Amount,Status,Address,TxHash
   private String id = null;
   private long time = 0L;
   private String coin = null;
   private String coinFullName = null;
   private Double amount = null;
   private String address = null;
   private String caseSensitiveAddress = null;
   private String txHash = null;
   private String exchange = null;

   public Withdrawal() {
      super();
   }

   @Override
   public int getIndex() {
      return index;
   }

   @Override
   public void setIndex(int i) {
      index = i;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public Long getTime() {
      return time;
   }

   @Override
   public MasterRecordData getRecordType(DataRecord data) {

      return new MasterRecordData("WITHDRAWAL [" + coin + " / " + exchange + "]");
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getCoin() {
      return coin;
   }

   public void setCoin(String coin) {
      this.coin = coin;
   }

   public String getCoinFullName() {
      return coinFullName;
   }

   public void setCoinFullName(String coinFullName) {
      this.coinFullName = coinFullName;
   }

   public Double getAmount() {
      return amount;
   }

   public void setAmount(Double amount) {
      this.amount = amount;
   }

   public String getAddress() {
      return address;
   }

   public String getCaseSensitiveAddress() {
      return caseSensitiveAddress;
   }

   public void setAddress(String address) {
      this.caseSensitiveAddress = address;
      this.address = address.toUpperCase();
   }

   public String getTxHash() {
      return txHash;
   }

   public void setTxHash(String txHash) {
      this.txHash = txHash;
   }

   public void setExchange(String ex) {
      this.exchange = ex;
   }

   public String getExchange() {
      return exchange;
   }

   @Override
   public int compareTo(Withdrawal o) {

      return -Long.compare(o.time, time);
   }

}
