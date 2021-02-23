package taxtool.input;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;

import taxtool.ui.TradePanel;

public class CoinParser {

   private final static File MUTEX = new File("mutexes/ " + UUID.randomUUID().toString());
   static {
      MUTEX.getParentFile().mkdirs();
   }
   private final static JFrame txFrame = new JFrame();

   public static JFrame frame() {
      return txFrame;
   }

   public CoinParser() throws IOException, ParseException, ClassNotFoundException {
      for(File f : MUTEX.getParentFile().listFiles())
      {
         f.delete();
      }
      MUTEX.createNewFile();
      FlatDarkLaf.install();
      launchUI();
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            shutdownHandling();
         }
      });
      singleRunMutex(); 
   }
   
   private static void singleRunMutex() {
      Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(new Runnable() {
         @Override
         public void run() {
            if(!MUTEX.exists()) 
            {
               System.exit(0);
            }
         }
      }, 1000L, 1000, TimeUnit.MILLISECONDS);
   }

   private void shutdownHandling() {
      // todo
   }

   private void launchUI() throws IOException, ParseException, ClassNotFoundException {

      DataRecord record = PersonalData.getRecordLoader().loadRecords();
      txFrame.setTitle("TAX TOOL");
      TradePanel panel = new TradePanel(txFrame, record);
      panel.setPreferredSize(new Dimension(800, 700));
      txFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      txFrame.getContentPane().add(panel);
      txFrame.pack();
      txFrame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            try {
               new CoinParser();
            } catch (ClassNotFoundException | IOException | ParseException e) {
               e.printStackTrace();
            }
         }
      });

   }
}
