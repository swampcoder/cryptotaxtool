package taxtool.input;

import java.awt.Color;

//"Txhash","Blockno","UnixTimestamp","DateTime","From","To","ContractAddress",
//"Value_IN(ETH)","Value_OUT(ETH)","CurrentValue @ $1836.92/Eth","TxnFee(ETH)","TxnFee(USD)","Historical $Price/Eth","Status","ErrCode"
public class EthereumTx extends RecordBase implements Comparable<EthereumTx>, IRecordInterface {

   private final static IAddressResolver RESOLVER = IRankedService.resolveService(IAddressResolver.class);
   private String txHash = null;
   private long timeOf = 0L;
   private String from = null;
   private String to = null;
   private String contract = null;
   private double valueInEth = -1;
   private double valueOutEth = -1;
   private int index = -1;
   private String tokenSymbol = null;
   private String tokenName = null;

   private double tokenValue = -1;

   public EthereumTx() {

   }

   @Override
   public Long getTime() {

      return timeOf;
   }

   @Override
   public MasterRecordData getRecordType(DataRecord data) {

      String txt = null;
      Color clr = null;
      if (this.isTokenTx())
         txt = "TOKEN TX";
      else
         txt = "TX";

      if (data.isWithdrawal(txHash)) {
         txt += "  [WITHDRAWAL]";
      }

      else if (data.isDeposit(txHash)) {
         txt += "   [DEPOSIT]";
      }

      boolean toIsMine = data.isAddressMine(to);
      boolean fromIsMine = data.isAddressMine(from);

      if (toIsMine && fromIsMine) {
         txt = "TX [SELF -> SELF]";
         clr = Color.green;
      }
      // TODO could indicate if tx is to self or not

      return new MasterRecordData(txt, clr);
   }

   @Override
   public Double getAmount() {
      if (this.valueInEth > 0)
         return valueInEth;
      else if (valueOutEth > 0)
         return valueOutEth;
      else
         return null;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   @Override
   public String toString() {
      return "Etherscan date=" + timeOf + "  to=" + to + "   from=" + from;
   }

   public String getTxHash() {
      return txHash;
   }

   public void setTxHash(String txHash) {
      this.txHash = txHash;
   }

   public long getTimeOf() {
      return timeOf;
   }

   public void setTimeOf(long timeOf) {
      this.timeOf = timeOf;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from.toUpperCase();
   }

   public String getTo() {
      return to;
   }

   public void setTo(String to) {
      this.to = to.toUpperCase();
   }

   public double getValueInEth() {
      return valueInEth;
   }

   public void setValueInEth(double valueInEth) {
      this.valueInEth = valueInEth;
   }

   public double getValueOutEth() {
      return valueOutEth;
   }

   public void setValueOutEth(double valueOutEth) {
      this.valueOutEth = valueOutEth;
   }

   public String getContract() {
      return contract;
   }

   public void setContract(String contract) {
      this.contract = contract;
   }

   public boolean isTokenTx() {
      return tokenSymbol != null;
   }

   public void setTokenValue(double tokenValue) {
      this.tokenValue = tokenValue;
   }

   public double getTokenValue() {
      return this.tokenValue;
   }

   public boolean isValid() {
      if (tokenSymbol != null && tokenSymbol.length() > 0 && RESOLVER.isETHTokenIgnored(tokenSymbol))
         return false;
      if (tokenName != null && tokenName.length() > 0 && RESOLVER.isETHTokenIgnored(tokenName))
         return false;
      return true;

   }

   public void setToken(String tokenSymbol) {
      this.tokenSymbol = tokenSymbol;
   }

   public String getTokenSymbol() {
      return tokenSymbol;
   }

   public void setTokenName(String tokenName) {
      this.tokenName = tokenName;
   }

   public String getTokenName() {
      return tokenName;
   }

   @Override
   public int compareTo(EthereumTx o) {

      return -Long.compare(o.timeOf, timeOf);
   }

   @Override
   public String getCoin() {

      return "ETH";
   }

}
