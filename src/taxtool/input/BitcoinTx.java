package taxtool.input;

import java.awt.Color;

public class BitcoinTx extends CryptoRecord {

   private int index = -1;
   private String txHash = null;

   public BitcoinTx() {
      super(RecordType.BtcTx);
      setCoinOrCoinIn("BTC");
   }

   @Override
   public int getIndex() {
      return index;
   }

   @Override
   public void setIndex(int i) {
      index = i;
   }


   public MasterRecordData getRecordType(DataRecord data) {

      String txt = null;
      txt = "BTC TX";

      return new MasterRecordData(txt, Color.orange);
   }

   public String getTxHash() {
      return txHash;
   }

   public void setTxHash(String txHash) {
      this.txHash = txHash;
   }

}
