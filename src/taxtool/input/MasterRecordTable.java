package taxtool.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;

import ctc.enums.Currency;
import taxtool.input.DataRecord.IDataRecordListener;
import taxtool.ui.EtherscanTable;
import taxtool.ui.UIManagerUtils;
import taxtool.ui.table.DataTable;
import taxtool.ui.table.DataTableModel;
import taxtool.ui.table.IDataTableFilter;
import taxtool.ui.table.IDataTableSelectionListener;

public class MasterRecordTable extends DataTable<CryptoRecord> implements DocumentListener, IDataRecordListener {

   private final static IAddressResolver ADDR_RESOLVER = PersonalData.getAddrResolver();
   
   private final static DecimalFormat USD_PREC = new DecimalFormat("0.00");
   private final static DecimalFormat MAX_COIN_PREC = new DecimalFormat("#.#####");
   private final static DateFormat TimeFormat = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
   static {
      TimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
   }

   private final MasterRecordTable self = this;
   private final static String[] COLS = new String[] { "H","T", "ID", "DATE", "RECTYPE", "COIN/IN", "PRICE/IN",
         "COIN/OUT", "PRICE/USD", "AMT/IN", "AMT/OUT", "T/V OUT",
         "TOT/IN", "TOT/OUT", "G/L", "EXCH", "TOKEN", "FROM", "TO", "" };
   
   private static int HIDE = 0;
   private static int TRANSFER = 1;
   private static int ID = 2;
   private static int DATE = 3;
   private static int RECTYPE = 4;
   private static int COIN_IN = 5;
   private static int PRICE_USD_IN = 6;
   private static int COIN_OUT = 7;
   private static int PRICE_USD_OUT = 8;
   private static int IN_AMT = 9;
   private static int OUT_AMT = 10;
   private static int TOT_TRADE_VAL = 11;
   private static int IN_TOT = 12;
   private static int OUT_TOT = 13;
   private static int GAINLOSS = 14;
   private static int EXCHANGE = 15;
   private static int TOKEN = 16;
   private static int FROM_ADDR = 17;
   private static int TO_ADDR = 18;
   private static int HOLDING_IN =17;
   private static int HOLDING_OUT = 10;
   private static int TX_HASH = 13;

   
   // private static int DESC = 2;
   private final DataRecord record;
   private final EtherscanTable ethTable;
   private final JFrame frame;
   private final JTextField filterIn;
   private boolean applyHiddenFilter = false;
   private Set<RecordType> visibleRecordTypes = new HashSet<RecordType>();
   private Set<Integer> hiddenYears = new HashSet<Integer>();
   private MasterTableMode tableMode = MasterTableMode.Default;
   private boolean applyIgnoreFilter = true; // dont show spam transactions 
   public MasterRecordTable(JFrame frame, JTextField filterIn, DataRecord record, EtherscanTable ethTable) {
      this.frame = frame;
      this.filterIn = filterIn;
      this.record = record;
      this.ethTable = ethTable;
      for (CryptoRecord rec : record.getMasterRecords()) {
         if(!isIgnored(rec)) getModel().addRow(rec);
      }
   
      this.setShowGrid(true);
      
      for(RecordType rt : RecordType.values()) 
      {  
         if(rt.visibleByDefault()) 
            visibleRecordTypes.add(rt);
      }

      addListener(new IDataTableSelectionListener<CryptoRecord>() {

         @Override
         public void notifySelection(List<CryptoRecord> selections) {
            List<EthereumTx> ethTxs = new ArrayList<EthereumTx>();
            for (CryptoRecord rec : selections) {
               if (rec instanceof EthereumTx) {
                  ethTxs.add((EthereumTx) rec);
               }
            }

            ethTable.setSelections(ethTxs);
         }

      });

      setupTextFilter();
      
      setRenderer(HIDE, new DefaultTableCellRenderer());
      
      applyFilters();
      
      record.addListener(this);
   }
   
   public void setTableMode(MasterTableMode tableMode) 
   {
      this.tableMode = tableMode;
      if(tableMode == MasterTableMode.Default) 
      {
         
      }
      else if(tableMode == MasterTableMode.EtherView)
      {
         
      }
   }
   
   public void setVisible(RecordType type, boolean visible) 
   {
      if(visible) visibleRecordTypes.add(type);
      else visibleRecordTypes.remove(type);
      applyFilters();
   }
   
   public void setVisible(int year, boolean visible) 
   {
      if(visible) hiddenYears.remove(year);
      else hiddenYears.add(year);
      applyFilters();
   }
   
   public void setApplyHiddenFilter(boolean applyHiddenFilter) 
   {
      this.applyHiddenFilter= applyHiddenFilter;
      this.applyFilters();
   }

   @Override
   protected DataTableModel<CryptoRecord> createModel() {

      return new MasterModel(this, COLS);
   }

   public void addObject(Object o) {
      if (o instanceof CryptoRecord) {
         CryptoRecord rec = (CryptoRecord) o;
         getModel().addRow(rec);
      }
   }
   
   @Override
   public void notifyCalcComplete() {
      for(int i =0 ; i < getModel().getRowCount(); i++) 
      {
         getModel().fireTableCellUpdated(i, GAINLOSS);
      }
   }

   @Override
   protected void handleDoubleClick(MouseEvent me, int r, int c, CryptoRecord rowObj) {
      
      if(c == GAINLOSS) 
      {
         JOptionPane.showMessageDialog(frame, rowObj.getGainLossDesc(), "Gain/Loss Debug", JOptionPane.INFORMATION_MESSAGE);
      }
      else
      {
         String fileStr = rowObj.getRecordSource().file.toString();
         String rawStr = rowObj.getRecordSource().rawLine.toString();
         String msg = fileStr + "\n" + rawStr;
         String calcDebug = rowObj.getCalcNotes();
         if(calcDebug != null) msg += calcDebug + "\n";
         JOptionPane.showMessageDialog(frame, msg);
      }
   }

   @Override
   protected void updateRendering(CryptoRecord rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column) {

      JLabel l = (JLabel) c;
      
      if(column == RECTYPE) 
      {
         if("USD".equalsIgnoreCase(rowObj.getCoinOut())) 
         {
            l.setText("BUY");
         }
         else if("USD".equalsIgnoreCase(rowObj.getCoinOrCoinIn())) 
         {
            l.setText("SELL");
         }
      }
      else if (column == DATE) {
         Date date = new Date((Long) value);
         l.setText(TimeFormat.format(date));
      } else if (column == COIN_IN || column == COIN_OUT) {
         String coin = (String) value;
         if(coin == null) return;
         
         
         if (coin.equalsIgnoreCase("ETH"))
         {
            l.setForeground(Color.cyan);
            if(rowObj instanceof EthereumTx) 
            {
               EthereumTx ethTx = (EthereumTx) rowObj;
               if(ethTx.isTokenTx()) 
               {
                  String tokenSym = "???";
                  if(ethTx.getTokenSymbol() != null) tokenSym = ethTx.getTokenSymbol();
                  l.setText("ETH/" + tokenSym);
               }
            }
         }
         else if (coin.equalsIgnoreCase("BTC"))
            l.setForeground(Color.orange);
         else if("USD".equalsIgnoreCase(coin)) 
         {
            l.setForeground(Color.green);
         }
         else
            l.setForeground(UIManagerUtils.labelForeground());
      } else if (column == IN_AMT) {
         Double amt = (Double) value;
         if (amt != null) {
            l.setText(MAX_COIN_PREC.format(amt));
         }
      } 
      
      else if (column == TO_ADDR && value != null) {
         boolean toMine = record.isAddressMine((String) value);
         String label = record.getAddressLabel((String) value);
         if (label.length() > 0)
            l.setText(label);
         if (toMine)
            l.setForeground(Color.green);
         else if (label.length() > 0)
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());
      }
      else if (column == FROM_ADDR && value != null) {
        boolean fromMine = record.isAddressMine((String) value);
         String label = record.getAddressLabel((String) value);
         if (label.length() > 0)
            l.setText(label);
         if (fromMine)
            l.setForeground(Color.green);
         else if (label.length() > 0)
            l.setForeground(Color.orange);
         else
            l.setForeground(UIManagerUtils.labelForeground());
      }
      else if((column == IN_AMT || column == IN_TOT) && value != null) 
      {
         if(rowObj.isInCoinUSD()) 
         {
            l.setText("$" + USD_PREC.format((Double)value));
         }
         else 
         {
            l.setText(MAX_COIN_PREC.format((Double) value));
         }
      }
      
      else if((column == OUT_TOT || column == OUT_AMT) && value != null) 
      {
         if(rowObj.isOutCoinUSD()) 
         {
            l.setText("$" + USD_PREC.format((Double)value));
         }
         else 
         {
            l.setText(MAX_COIN_PREC.format((Double) value));
         }
      }
      else if(column == GAINLOSS) {
         if(value == null) 
         {
            l.setText("N/A");
         }
         else 
         {
            l.setText("$" + USD_PREC.format((Double)value));
            Double gl = (Double) value;
            if(gl.doubleValue() == 0) l.setForeground(UIManagerUtils.labelForeground());
            else if(gl.doubleValue() < 0) l.setForeground(Color.RED);
            else if(gl.doubleValue() > 0) l.setForeground(Color.GREEN);
         }
      }
      /*else if(column == BTC_PRICE) 
      {
         Double v = (Double) value;
         if(v == null) 
         {
            l.setText("MISSING");
            l.setForeground(Color.red);
         }
         else if(v < 0) 
         {
            l.setText("ERROR");
            l.setForeground(Color.RED);
         }
         else
         {
            l.setText("$" + USD_PREC.format(v));
            l.setForeground(UIManagerUtils.labelForeground());
         }
      }
      */
      else if (column == PRICE_USD_IN || column == PRICE_USD_OUT || column == TOT_TRADE_VAL)
      {
         Double v = (Double) value;
         if(v == null) return;
         l.setText("$" + USD_PREC.format(v));
         
         if(column == TOT_TRADE_VAL) 
         {
            if(v > 100000) 
            {
               l.setForeground(Color.RED);
            }
            else
            {
               l.setForeground(UIManagerUtils.labelForeground());
            }
         }
         else
         {
            l.setForeground(UIManagerUtils.labelForeground());
         }
      }
   }

   private class MasterModel extends DataTableModel<CryptoRecord> {

      public MasterModel(DataTable<CryptoRecord> table, String[] columns) {
         super(table, columns);
      }
      
      @Override
      public boolean isCellEditable(int r, int c) 
      {
         return c== HIDE;
      }
      
      @Override
      public Class<?> getColumnClass(int c) 
      {
         if( c== HIDE) return Boolean.class;
         else return super.getColumnClass(c);
      }
      
      @Override
      public void setValueAt(CryptoRecord ro, Object o, int r, int c) 
      {
         if(c == HIDE) 
         {
            Boolean hide = (Boolean) o;
            ro.getData().setHiddenFromTable(hide);
         }
      }

      @Override
      protected Object getValueAt(CryptoRecord o, int r, int c) {

         if(c == HIDE) return o.getData().isHiddenFromTable();
         else if (c == ID)
            return o.getIndex();
         else if (c == DATE) {
            return o.getTime();
         }
         else if (c == COIN_IN)
            return o.getCoinOrCoinIn();
         else if(c == COIN_OUT) 
            return o.getCoinOut();
         else if(c == PRICE_USD_OUT) 
         {
            if(o.getCoinOut() == null) return null;
            return record.getUSDTable().getPrice(o.getCurrencyOut(), Currency.USD, o.getTime(), false); // , timeOf)
         }
         else if(c == PRICE_USD_IN) 
         {
            String coin = o.getTokenSymbol();
            if(coin == null || coin.length() == 0) coin = o.getCoinOrCoinIn();
            Currency currency = Currency.lookup(coin);
            System.out.println(o.getRawLine());
            return record.getUSDTable().getPrice(currency, Currency.USD, o.getTime(), false);
         }
         else if(c == TOT_TRADE_VAL) 
         {
            if(!o.isTrade()) return null;
            return o.getTotalTradeValue(record.getUSDTable());
         }
         else if (c == IN_AMT)
            return getInAmt(o);
         else if(c == OUT_AMT) 
         {
            return getOutAmt(o);
         }
         else if(c == OUT_TOT) 
         {
            return o.getOutCoinHolding();
         }
         else if( c== IN_TOT) 
         {
            return o.getInCoinHolding();
         }
         else if (c == RECTYPE)
            return o.getRecordType();
         else if(c == FROM_ADDR) 
         {
            return getFromAddr(o);
         }
         else if(c == TO_ADDR) 
         {
            return getToAddr(o);
         }
         else  if(c == GAINLOSS) 
         {
            return getGainLoss(o);
         }
         else if(c == EXCHANGE) 
         {
            return o.getExchange();
         }
         else if(c == TOKEN) 
         {
            if(o instanceof EthereumTx) 
            {
               
            }
         }
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
      
      this.getModel().addFilter(filter);
   }
   
   private String getFromAddr(CryptoRecord rec) 
   {
      if(rec instanceof EthereumTx) 
      {
         EthereumTx ethTx = (EthereumTx) rec;
         return ethTx.getFrom();
      } 
      return rec.getFromAddressCS();
   }
   
   private final IDataTableFilter<CryptoRecord> filter = new IDataTableFilter<CryptoRecord>() {

      @Override
      public boolean isFilteredOut(CryptoRecord t) {

         if(applyIgnoreFilter) 
         {
            if(isIgnored(t)) return true;
         }
         if(hiddenYears.contains(t.getRecordYear())) 
         {
            return true;
         }
         if(!visibleRecordTypes.contains(t.getRecordType()))
         {
            return true;
         }
         if(applyHiddenFilter && t.getData().isHiddenFromTable()) return true;
         
         if (filterText.trim().length() == 0)
            return false;

         if (t.getCoinOrCoinIn().contains(filterText))
            return false;

         return false;
      }

   };
   
   private boolean isIgnored(CryptoRecord t) 
   {
   // some of my eth addresses have spam tokens 
      if(t instanceof EthereumTx) 
      {
         EthereumTx ethTx = (EthereumTx) t;
         if(ADDR_RESOLVER.isETHTokenIgnored(ethTx.getTokenSymbol())) 
         {
            return true;
         }
      }
      return false;
   }
   
   private String getToAddr(CryptoRecord rec) 
   {
      if(rec instanceof EthereumTx) 
      {
         EthereumTx ethTx = (EthereumTx) rec;
         return ethTx.getTo();
      }
      else return rec.getToAddressCS();
   }
   
   private Double getInAmt(CryptoRecord record) 
   {
      if(record instanceof EthereumTx) 
      {
         EthereumTx ethTx = (EthereumTx) record;
         if(ethTx.isTokenTx()) return ethTx.getAmountOut();
         else return ethTx.getValueInEth();
      }
      else if(record.getRecordType() == RecordType.BtcTx) 
      {
         return record.getAmountOrAmountIn();
      }
      else if(record.isTrade())
      {
         return record.getAmountOrAmountIn();
      }
      else if(record.getRecordType() == RecordType.Withdraw)
      {
         return record.getAmountOrAmountIn();
      }
      return null;
   }
   
   private Double getOutAmt(CryptoRecord record) 
   {
      if(record instanceof EthereumTx) 
      {
         EthereumTx ethTx = (EthereumTx) record;
         if(ethTx.isTokenTx()) return ethTx.getAmountOut();
         else return ethTx.getValueOutEth();
      }
      else if(record.isTrade()) 
      {
         return record.getAmountOut();
      }
      
      return null;
   }
   
   private Double getGainLoss(CryptoRecord cr) 
   {
      if(cr.getCalculatedTransaction() == null) return null;
      else return cr.getCalculatedTransaction().getGainLoss().doubleValue();
   }
}
