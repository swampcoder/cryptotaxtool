package taxtool.input;

import java.io.File;

public class RecordSource {

   public final File file;
   public final String rawLine;

   public RecordSource(File file, String rawLine) {
      this.file = file;
      this.rawLine = rawLine;
   }
}
