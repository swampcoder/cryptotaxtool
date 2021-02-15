package taxtool.input;

public class Deposit extends RecordBase implements Comparable<Deposit>, IRecordInterface {

   private int index = -1;
   private String exchange = null;
   private int exchangeId = -1;
   private double amount = -1;
   private String coin = null;
   private long time = 0L;
   private String txId = null;
   private String address = null;

   public Deposit(String exchange) {
      this.exchange = exchange;
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
   public Long getTime() {

      return time;
   }

   @Override
   public MasterRecordData getRecordType(DataRecord data) {

      return new MasterRecordData("DEPOSIT [" + coin + " / " + exchange + "]");
   }

   @Override
   public int compareTo(Deposit o) {

      return -Long.compare(o.time, time);
   }

   public int getExchangeId() {
      return exchangeId;
   }

   public void setExchangeId(int exchangeId) {
      this.exchangeId = exchangeId;
   }

   @Override
   public Double getAmount() {
      return amount;
   }

   public void setAmount(double amount) {
      this.amount = amount;
   }

   public String getCoin() {
      return coin;
   }

   public void setCoin(String coin) {
      this.coin = coin;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public String getTxId() {
      return txId;
   }

   public void setTxId(String txId) {
      this.txId = txId;
   }

   public String getAddress() {
      return address;
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public String getExchange() {
      return exchange;
   }

}
