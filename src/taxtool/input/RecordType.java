package taxtool.input;

public enum RecordType {

   BtcTx,
   EthTx,
   Trade,
   Stake,
   Deposit,
   Withdraw,
   Crowdsale,
   Send,
   Receive;
   
   public boolean visibleByDefault() 
   {
      return this != Send && this != Receive;
   }
}
