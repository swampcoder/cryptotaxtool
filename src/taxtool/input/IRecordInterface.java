package taxtool.input;

public interface IRecordInterface {

   public RecordSource getRecordSource();

   public int getIndex();

   public void setIndex(int i);

   default public boolean isUSDBuySell() {
      return false;
   }

   public Long getTime();

   public String getCoin();

   public Double getAmount();

   public MasterRecordData getRecordType(DataRecord data);
}
