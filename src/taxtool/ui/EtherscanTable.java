package taxtool.ui;

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
import taxtool.input.EthereumTx;
import taxtool.input.RecordTable;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;

public class EtherscanTable extends RecordTable<EthereumTx> {

   private final static DateFormat TimeFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }
   private static String[] COLS = new String[] { "ID", "DATE", "FROM", "TO", "TOKEN", "CONTRACT", "IN/TOKEN AMT",
         "OUT AMT", "TX HASH"

   };
   private final static int IDX = 0;
   private final static int DATE = 1;
   private final static int FROM = 2;
   private final static int TO = 3;
   private final static int TOKEN = 4;
   private final static int CONTRACT = 5;
   private final static int IN_AMT = 6;
   private final static int OUT_AMT = 7;
   private final static int TX_HASH = 8;
   private final DataRecord data;

   public EtherscanTable(DataRecord data) {
      this.data = data;
      this.setAutoCreateRowSorter(true);
      // this.getRowSorter().setComparator(0, new NumericComparator());

      for (EthereumTx tx : data.getFullEthTxList()) {
         getModel().addRow(tx);
      }

   }

   @Override
   protected DataTableModel<EthereumTx> createModel() {

      return new EtherscanModel(this, COLS);
   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int row, int col, EthereumTx rowObj) {
      if (col == DATE) {
         StringSelection selection = new StringSelection(Long.toString(rowObj.getTimeOf()));
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      }
      if (col == TO) {
         StringSelection selection = new StringSelection(rowObj.getTo());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      } else if (col == FROM) {
         StringSelection selection = new StringSelection(rowObj.getFrom());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      } else if (col == CONTRACT) {
         StringSelection selection = new StringSelection(rowObj.getContract());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      } else if (col == TX_HASH) {
         StringSelection selection = new StringSelection(rowObj.getTxHash());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      } else if (col == TOKEN) {
         StringSelection selection = new StringSelection(rowObj.getTokenSymbol());
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(selection, selection);
      }
   }

   @Override
   protected void updateRendering(EthereumTx rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

      boolean toMine = data.isAddressMine(rowObj.getTo());
      boolean fromMine = data.isAddressMine(rowObj.getFrom());
      boolean contractMine = data.isAddressMine(rowObj.getContract());
      JLabel l = (JLabel) c;
      if (column == DATE) {
         Date d = new Date(rowObj.getTimeOf());
         l.setText(TimeFormat.format(d));
      } else if (column == TO) {
         String label = data.getAddressLabel((String) value);
         if (label.length() > 0)
            l.setText(label);
         if (toMine)
            l.setForeground(Color.green);
         else if (label.length() > 0)
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());

      } else if (column == FROM) {
         String label = data.getAddressLabel((String) value);
         if (label.length() > 0)
            l.setText(label);
         if (fromMine)
            l.setForeground(Color.green);
         else if (label.length() > 0)
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());
      } else if (column == CONTRACT) {
         String label = data.getAddressLabel((String) value);
         if (label.length() > 0)
            l.setText(label);
         if (contractMine)
            l.setForeground(Color.green);
         else if (label.length() > 0)
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());
      }
   }

   private class EtherscanModel extends DataTableModel<EthereumTx> {
      public EtherscanModel(DataTable<EthereumTx> table, String[] columns) {
         super(table, columns);

      }

      @Override
      public Object getValueAt(EthereumTx tx, int r, int c) {

         if (c == IDX)
            return tx.getIndex();
         if (c == DATE)
            return tx.getTimeOf();

         else if (c == FROM)
            return tx.getFrom();
         else if (c == TO)
            return tx.getTo();
         else if (c == TOKEN) {
            return tx.getTokenName();

         } else if (c == CONTRACT)
            return tx.getContract();
         else if (c == IN_AMT) {

            if (tx.isTokenTx())
               return tx.getTokenValue();
            else
               return tx.getValueInEth();
         } else if (c == OUT_AMT)
            return tx.getValueOutEth();
         else if (c == TX_HASH)
            return tx.getTxHash();
         return null;
      }

   }
}
