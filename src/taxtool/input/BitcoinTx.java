package taxtool.input;

import java.awt.Color;

public class BitcoinTx extends RecordBase implements Comparable<BitcoinTx>, IRecordInterface {

   private int index = -1;
   private long time = -1;
   private String txHash = null;
   private String toAddress = null;
   private String fromAddress = null;
   private double amount = -1d;

   public BitcoinTx() {
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

   @Override
   public int compareTo(BitcoinTx o) {

      return -Long.compare(o.time, time);
   }

   @Override
   public Long getTime() {
      return time;
   }

   @Override
   public MasterRecordData getRecordType(DataRecord data) {

      String txt = null;
      txt = "BTC TX";

      return new MasterRecordData(txt, Color.orange);
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getTxHash() {
      return txHash;
   }

   public void setTxHash(String txHash) {
      this.txHash = txHash;
   }

   public String getToAddress() {
      return toAddress;
   }

   public void setToAddress(String toAddress) {
      this.toAddress = toAddress;
   }

   public String getFromAddress() {
      return fromAddress;
   }

   public void setFromAddress(String fromAddress) {
      this.fromAddress = fromAddress;
   }

   public Double getAmount() {
      return amount;
   }

   public void setAmount(double amount) {
      this.amount = amount;
   }

   @Override
   public String getCoin() {

      return "BTC";
   }

}
