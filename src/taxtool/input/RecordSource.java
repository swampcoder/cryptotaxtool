package taxtool.input;

import java.io.File;
import java.io.Serializable;

public class RecordSource implements Serializable {

   private static final long serialVersionUID = -7402998257396469712L;
   public final File file;
   public final String rawLine;

   public RecordSource(File file, String rawLine) {
      this.file = file;
      this.rawLine = rawLine;
   }
}
