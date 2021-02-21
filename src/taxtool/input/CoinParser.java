package taxtool.input;

import java.awt.Dimension;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;

import taxtool.ui.TradePanel;

public class CoinParser {

   private final static JFrame txFrame = new JFrame();

   public static JFrame frame() {
      return txFrame;
   }

   public CoinParser() throws IOException, ParseException, ClassNotFoundException {
      FlatDarkLaf.install();
      launchUI();
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            shutdownHandling();
         }
      });
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
