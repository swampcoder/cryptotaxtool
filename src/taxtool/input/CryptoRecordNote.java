package taxtool.input;

import java.io.Serializable;

public class CryptoRecordNote implements Serializable {

   private static final long serialVersionUID = 662409166124510971L;

   private boolean isTaxable = false;
   private boolean isHiddenFromTable = false;
   private String notes = null;
   
   public CryptoRecordNote(CryptoRecord record) 
   {
      isTaxable = record.isTrade();
      isHiddenFromTable = false;
   }
   
   public void setIsTaxable(boolean isTaxable) 
   {
      this.isTaxable = isTaxable;
   }
   
   public boolean isTaxable() 
   {
      return this.isTaxable;
   }
   
   public void setHiddenFromTable(boolean isHiddenFromTable) 
   {
      this.isHiddenFromTable = isHiddenFromTable;
   }
   
   public boolean isHiddenFromTable() 
   {
      return isHiddenFromTable;
   }
   
   public String getNotes() 
   {
      return notes;
   }
   
   public void setNotes(String notes) 
   {
      this.notes = notes;
   }
}
