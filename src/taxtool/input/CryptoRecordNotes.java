package taxtool.input;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class CryptoRecordNotes implements Serializable {

   private final static Path PATH = (new File("crypto_record_notes.dat")).toPath();
   public static CryptoRecordNotes get() {
      return DATA;
   }
   private static CryptoRecordNotes DATA;
   static 
   {
      
      try {
         PATH.toFile().createNewFile();
         DATA = (CryptoRecordNotes) Utils.fromString(Files.readString(PATH));
      } catch (ClassNotFoundException | IOException e) {
         DATA = new CryptoRecordNotes();
      }
      
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            try {
               Files.write(PATH, Utils.toString(DATA).getBytes(), 
                     StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE,
                     StandardOpenOption.SYNC);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      });
   }
   
   private static final long serialVersionUID = -3329878525516208930L;
   
   private final Map<Integer, CryptoRecordNote> records = new HashMap<Integer, CryptoRecordNote>();
   
   public CryptoRecordNotes() {}
   
   public CryptoRecordNote getRecord(CryptoRecord cr) 
   {
      CryptoRecordNote anno = records.get(cr.createRecordHash());
      if(anno == null) 
      {
         anno = new CryptoRecordNote(cr); //cr.createRecordHash());
         records.put(cr.createRecordHash(), anno);
      }
      return anno;
   }
}
   

