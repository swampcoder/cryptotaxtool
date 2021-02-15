package taxtool.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import taxtool.ui.EtherscanTable;
import taxtool.ui.UIManagerUtils;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;
import taxtool.ui.table.IDataTableFilter;
import taxtool.ui.table.IDataTableSelectionListener;

public class MasterRecordTable extends DataTable<IRecordInterface> implements DocumentListener {

   private final static DecimalFormat MAX_COIN_PREC = new DecimalFormat("#.#####");
   private final static DateFormat TimeFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   private final MasterRecordTable self = this;
   private final static String[] COLS = new String[] { "ID", "DATE", "FLAG", "COIN", "AMT", "RECORD TYPE" };
   private static int ID = 0;
   private static int DATE = 1;
   private static int FLAG = 2;
   private static int COIN = 3;
   private static int AMOUNT = 4;
   private static int TYPE = 5;
   // private static int DESC = 2;
   private final DataRecord record;
   private final EtherscanTable ethTable;
   private final JFrame frame;
   private final JTextField filterIn;

   public MasterRecordTable(JFrame frame, JTextField filterIn, DataRecord record, EtherscanTable ethTable) {
      this.frame = frame;
      this.filterIn = filterIn;
      this.record = record;
      this.ethTable = ethTable;
      for (IRecordInterface rec : record.getMasterRecords()) {
         getModel().addRow(rec);
      }
      this.setShowGrid(true);

      this.addListener(new IDataTableSelectionListener<IRecordInterface>() {

         @Override
         public void notifySelection(List<IRecordInterface> selections) {
            List<EthereumTx> ethTxs = new ArrayList<EthereumTx>();
            for (IRecordInterface rec : selections) {
               if (rec instanceof EthereumTx) {
                  ethTxs.add((EthereumTx) rec);
               }
            }

            ethTable.setSelections(ethTxs);
         }

      });

      setupTextFilter();
   }

   @Override
   protected DataTableModel<IRecordInterface> createModel() {

      return new MasterModel(this, COLS);
   }

   public void addObject(Object o) {
      if (o instanceof IRecordInterface) {
         IRecordInterface rec = (IRecordInterface) o;
         getModel().addRow(rec);
      }
   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int r, int c, IRecordInterface rowObj) {
      String fileStr = rowObj.getRecordSource().file.toString();
      String rawStr = rowObj.getRecordSource().rawLine.toString();

      JOptionPane.showMessageDialog(frame, fileStr + "\n" + rawStr);

   }

   @Override
   protected void updateRendering(IRecordInterface rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

      JLabel l = (JLabel) c;
      if (column == DATE) {
         Date date = new Date((Long) value);
         l.setText(TimeFormat.format(date));
      } else if (column == COIN) {
         String coin = (String) value;
         if (coin.equalsIgnoreCase("ETH"))
            l.setForeground(Color.cyan);
         else if (coin.equalsIgnoreCase("BTC"))
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());
      } else if (column == TYPE) {
         MasterRecordData rec = (MasterRecordData) value;
         l.setText(rec.txt);
         if (rec.clr == null)
            l.setForeground(UIManagerUtils.labelForeground());
         else
            l.setForeground(rec.clr);
      } else if (column == AMOUNT) {
         Double amt = (Double) value;
         if (amt != null) {
            l.setText(MAX_COIN_PREC.format(amt));
         }
      } else if (column == FLAG && value != null) {
         String flag = (String) value;
         if (flag.contains("$"))
            l.setForeground(Color.green);
         else
            l.setForeground(UIManagerUtils.labelForeground());
         ;
      }
   }

   private class MasterModel extends DataTableModel<IRecordInterface> {

      public MasterModel(DataTable<IRecordInterface> table, String[] columns) {
         super(table, columns);
      }

      @Override
      protected Object getValueAt(IRecordInterface o, int r, int c) {

         if (c == ID)
            return o.getIndex();
         else if (c == DATE)
            return o.getTime();
         else if (c == FLAG) {
            if (o.isUSDBuySell())
               return "$";
            else
               return null;
         } else if (c == COIN)
            return o.getCoin();
         else if (c == AMOUNT)
            return o.getAmount();
         else if (c == TYPE)
            return o.getRecordType(record);
         // else if(c == DESC) return o.getRecordDesc(record);
         return null;
      }

   }

   private String filterText = "";

   private void setFilterText(String filterText) {
      this.filterText = filterText;
   }

   @Override
   public void insertUpdate(DocumentEvent e) {
      setFilterText(filterIn.getText());
   }

   @Override
   public void removeUpdate(DocumentEvent e) {
      setFilterText(filterIn.getText());
   }

   @Override
   public void changedUpdate(DocumentEvent e) {
      setFilterText(filterIn.getText());
   }

   private void setupTextFilter() {
      IDataTableFilter<IRecordInterface> filter = new IDataTableFilter<IRecordInterface>() {

         @Override
         public boolean isFilteredOut(IRecordInterface t) {

            if (filterText.trim().length() == 0)
               return false;

            if (t.getCoin().contains(filterText))
               return false;

            return false;
         }

      };
      this.getModel().addFilter(filter);
   }
}
