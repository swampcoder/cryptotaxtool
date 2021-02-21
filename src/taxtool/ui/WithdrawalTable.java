/*package taxtool.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;
import javax.swing.JTable;

import taxtool.input.DataRecord;
import taxtool.input.RecordTable;
import taxtool.input.Withdrawal;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class WithdrawalTable extends RecordTable<Withdrawal> {

   private final static DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   private final static String[] COLS = new String[] { "ID", "TIME", "COIN", "ADDRESS", "EXCHANGE", "AMOUNT", "" };

   public final static int ID = 0;
   public final static int TIME = 1;
   public final static int COIN = 2;
   public final static int ADDRESS = 3;
   public final static int EXCHANGE = 4;
   public final static int AMOUNT = 5;

   public WithdrawalTable(DataRecord data) {

      setAutoCreateRowSorter(true);
      for (Withdrawal wd : data.getWithdrawals()) {
         getModel().addRow(wd);
      }
   }

   @Override
   protected DataTableModel<Withdrawal> createModel() {

      return new WithdrawalModel(this);
   }

   @Override
   protected void updateRendering(Withdrawal rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

      JLabel l = (JLabel) c;
      if (column == TIME) {
         Date d = new Date();
         d.setTime(rowObj.getTime());
         l.setText(TIME_FORMAT.format(d));
      }

      if (rowObj.getCoinOrCoinIn().equalsIgnoreCase("eth"))
         l.setForeground(Color.cyan);
      else
         l.setForeground(UIManagerUtils.labelForeground());

   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int row, int col, Withdrawal rowObj) {
      if (col == ADDRESS) {
         StringSelection selection = new StringSelection(rowObj.getAddress());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      }
   }

   private class WithdrawalModel extends DataTableModel<Withdrawal> {

      public WithdrawalModel(DataTable<Withdrawal> table) {
         super(table, COLS);
      }

      @Override
      public Object getValueAt(Withdrawal t, int r, int c) {

         if (c == ID)
            return t.getIndex();
         else if (c == TIME)
            return t.getTime();
         if (c == COIN)
            return t.getCoinOrCoinIn();
         else if (c == ADDRESS)
            return t.getAddress();
         else if (c == EXCHANGE)
            return "TODO";
         else if (c == AMOUNT)
            return t.getAmount();

         return null;
      }

   }

}*/
