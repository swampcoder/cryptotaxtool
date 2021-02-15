package taxtool.input;

import java.io.File;

abstract public class RecordBase implements IRecordInterface {

   private RecordSource source = null;

   public RecordBase() {
   }

   public void setRecordSource(RecordSource source) {
      this.source = source;
   }

   public RecordSource getRecordSource() {
      return source;
   }

   public void setRecordSource(File file, String rawLine) {
      this.source = new RecordSource(file, rawLine);
   }
}
