package taxtool.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import taxtool.input.CSVHeaders;
import taxtool.input.CSVTable;
import taxtool.input.CoinParser;
import taxtool.input.CoinTotal;
import taxtool.input.DataRecord;
import taxtool.input.Trade;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;
import taxtool.ui.table.IDataTableFilter;
import taxtool.ui.table.IDataTableSelectionListener;

public class TradeTable extends DataTable<Trade> implements IDataTableSelectionListener<CoinTotal> {

   private final static DecimalFormat MAX_COIN_PREC = new DecimalFormat("#.######");
   private final static DecimalFormat USD_FORMAT = new DecimalFormat("#.00");
   private final static DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   private boolean usdOnly = false;
   private boolean showUsdBuys = true;
   private boolean showUsdSells = true;
   private boolean btcHighlight = true;
   private boolean ethHighlight = true;

   private final List<String> visibleCoins = new ArrayList<String>();

   public TradeTable(DataRecord record) throws IOException, ParseException {
      super();

      setAutoCreateRowSorter(true);
      for (Trade trade : record.getTrades()) {
         getModel().addOrUpdate(trade);
      }

      this.createFilter();
      setShowGrid(true);
   }

   public void setBtcHighlight(boolean btcHighlight) {
      this.btcHighlight = btcHighlight;
      this.getModel().fireTableDataChanged();
   }

   public void setEthHighlight(boolean ethHighlight) {
      this.ethHighlight = ethHighlight;
      this.getModel().fireTableDataChanged();
   }

   public void setUsdOnly(boolean show) {
      this.usdOnly = show;
      applyFilters();
   }

   public void setShowUsdSells(boolean show) {
      this.showUsdSells = show;
      this.applyFilters();
      System.out.println("SHOW USD SELLS=" + showUsdSells);
   }

   public void setShowUsdBuys(boolean show) {
      this.showUsdBuys = show;
      this.applyFilters();
      System.out.println("SHOW USD BYS=" + show);
   }

   public void setVisibleCoins(List<String> coins) {
      this.visibleCoins.clear();
      visibleCoins.addAll(coins);
      applyFilters();
   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int row, int col, Trade rowObj) {
      JTable table = new CSVTable(CSVHeaders.getTradeHeaders(rowObj.exchange), rowObj.getVectorLine());
      JPanel p = new JPanel(new BorderLayout());
      p.add(new JScrollPane(table), BorderLayout.CENTER);
      p.setPreferredSize(new Dimension(1350, 50));
      JOptionPane.showMessageDialog(CoinParser.frame(), p, rowObj.exchange, JOptionPane.INFORMATION_MESSAGE);
   }

   @Override
   protected DataTableModel<Trade> createModel() {

      return new TradeTableModel(this);
   }

   @Override
   public void notifySelection(List<CoinTotal> selections) {

      List<String> coins = new ArrayList<String>();
      for (CoinTotal ct : selections) {
         coins.add(ct.name);
      }
      setVisibleCoins(coins);

   }

   @Override
   protected void updateRendering(Trade trade, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {
      if (trade.isCrowdsale()) {
         if (column == TradeTableModel.DATE) {
            JLabel l = (JLabel) c;
            l.setText(TIME_FORMAT.format((Long) value));
         }
         c.setBackground(Color.magenta);
         c.setForeground(Color.black);

         return;
      }

      if (isSelected) {
         c.setForeground(UIManagerUtils.tableSelectionForeground());
         c.setBackground(UIManagerUtils.tableSelectionBackground());
      } else {
         c.setForeground(UIManagerUtils.tableForeground());
         c.setBackground(UIManagerUtils.tableBackground());
      }
      if (column == TradeTableModel.DATE && value != null) {
         JLabel l = (JLabel) c;
         Date date = new Date((Long) value);
         l.setText(TIME_FORMAT.format(date));
      } else if (column == TradeTableModel.OUT_HOLDING) {
         JLabel l = (JLabel) c;
         Double v = (Double) value;
         if (trade.getCoinOut().equalsIgnoreCase("USD")) {
            l.setText("$" + USD_FORMAT.format((Double) value));
         } else {
            if (v < 0)
               l.setForeground(Color.RED);
            else
               l.setForeground(UIManagerUtils.labelForeground());
            l.setText(MAX_COIN_PREC.format((Double) value));
         }

      } else if (column == TradeTableModel.IN_HOLDING) {
         JLabel l = (JLabel) c;
         Double v = (Double) value;
         if (trade.getCoinOrCoinIn().equalsIgnoreCase("USD")) {
            l.setText("$" + USD_FORMAT.format((Double) value));
         } else {
            if (v < 0)
               l.setForeground(Color.RED);
            else
               l.setForeground(UIManagerUtils.labelForeground());
            l.setText(MAX_COIN_PREC.format((Double) value));
         }
      } else if (column == TradeTableModel.OUT_AMT) {
         JLabel l = (JLabel) c;
         if (trade.getCoinOut().equalsIgnoreCase("USD")) {
            l.setText("$" + USD_FORMAT.format((Double) value));
            l.setForeground(Color.GREEN);
         } else {
            l.setForeground(UIManagerUtils.labelForeground());
         }
         l.setText(MAX_COIN_PREC.format((Double) value));
      } else if (column == TradeTableModel.IN_AMT) {
         JLabel l = (JLabel) c;
         if (trade.getCoinOrCoinIn().equalsIgnoreCase("USD")) {
            l.setText("$" + USD_FORMAT.format((Double) value));
            l.setForeground(Color.GREEN);
         } else {
            l.setForeground(UIManagerUtils.labelForeground());
         }
         l.setText(MAX_COIN_PREC.format((Double) value));
      } else if (column == TradeTableModel.IN_TYPE) {
         JLabel l = (JLabel) c;
         String type = (String) value;
         if (btcHighlight && type.equalsIgnoreCase("btc")) {
            l.setForeground(Color.orange);
         } else if (ethHighlight && type.equalsIgnoreCase("eth")) {
            l.setForeground(Color.cyan);
         } else {
            setForeground(l, isSelected);
         }
      }

      else if (column == TradeTableModel.OUT_TYPE) {
         JLabel l = (JLabel) c;
         String type = (String) value;
         if (btcHighlight && type.equalsIgnoreCase("btc")) {
            l.setForeground(Color.orange);
         } else if (ethHighlight && type.equalsIgnoreCase("eth")) {
            l.setForeground(Color.cyan);
         } else {
            setForeground(l, isSelected);
         }
      }
   }

   private static void setForeground(JLabel l, boolean isSelected) {
      if (isSelected)
         l.setForeground(UIManagerUtils.tableSelectionForeground());
      else
         l.setForeground(UIManagerUtils.labelForeground());
   }

   private void createFilter() {
      IDataTableFilter<Trade> filter = new IDataTableFilter<Trade>() {
         @Override
         public boolean isFilteredOut(Trade t) {
            if (visibleCoins.size() > 0) {
               if (!visibleCoins.contains(t.getCoinOrCoinIn()) && !visibleCoins.contains(t.getCoinOut())) {
                  return true;
               }
            }

            if (!showUsdSells && t.getCoinOrCoinIn().equalsIgnoreCase("usd")) {
               return true;
            }
            if (!showUsdBuys && t.getCoinOut().equalsIgnoreCase("usd")) {
               return true;
            }

            return false;
         }
      };
      this.getModel().addFilter(filter);
   }

}
