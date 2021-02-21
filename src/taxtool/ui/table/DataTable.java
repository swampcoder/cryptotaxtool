package taxtool.ui.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

abstract public class DataTable<T> extends JTable {

   private final DataTable<T> self = this;

   abstract protected DataTableModel<T> createModel();

   private final DataTableModel<T> model;

   private final List<IDataTableSelectionListener<T>> listeners = new ArrayList<IDataTableSelectionListener<T>>();

   abstract protected void updateRendering(T rowObj, Component c, JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column);

   private List<IDataTableHighlighter<T>> highlighters = new ArrayList<IDataTableHighlighter<T>>();

   private final Map<Integer, TableCellRenderer> columnRenderers = new HashMap<Integer, TableCellRenderer>();
   
   private Map<Integer,Boolean> colVisibleMap = new HashMap<Integer,Boolean>();

   public DataTable() {
      this.model = createModel();
      setModel(model);

      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent mouseEvent) {
            JTable table = (JTable) mouseEvent.getSource();
            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);
            int col = table.columnAtPoint(point);
            row = table.convertRowIndexToView(row);
            if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
               handleDoubleClick(mouseEvent, row, col, getModel().get(row));
            }
         }
      });

      this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            int[] selectedRow = getSelectedRows();
            List<T> selections = new ArrayList<T>();
            for (int i = 0; i < selectedRow.length; i++) {

               int row = convertRowIndexToView(selectedRow[i]);
               selections.add(getModel().get(row));
            }
            System.out.println(selections);
            for (IDataTableSelectionListener<T> l : listeners) {
               l.notifySelection(selections);
            }
         }
      });

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            TableColumnAdjuster tc = new TableColumnAdjuster(self);
            tc.adjustColumns();
         }
      });
   }
   
   public void showColumn(int column) 
   {
      Boolean v = this.colVisibleMap.getOrDefault(column,true);
      if(v) return;
      getColumnModel().getColumn(column).setMinWidth(00);
      getColumnModel().getColumn(column).setMaxWidth(999);
   }
   
   public void hideColumn(int column) 
   {
      Boolean v = this.colVisibleMap.getOrDefault(column,true);
      if(!v) return;
      getColumnModel().getColumn(column).setMinWidth(0);
      getColumnModel().getColumn(column).setMaxWidth(0);
      
   }
   
   public boolean isColumnVisible(int column) 
   {
      return this.colVisibleMap.getOrDefault(column,true);
   }

   public static class NumericComparator implements Comparator<Number> {

      @Override
      public int compare(Number o1, Number o2) {
         return Double.compare(o1.doubleValue(), o2.doubleValue());
      }

   }

   public static class StringComparator implements Comparator<String> {

      @Override
      public int compare(String o1, String o2) {
         return o1.compareTo(o2);
      }

   }

   protected void handleDoubleClick(MouseEvent me, int r, int c, T rowObj) {
   }

   public void addHighlighter(IDataTableHighlighter<T> highlighter) {
      this.highlighters.add(highlighter);
      getModel().fireTableRowsUpdated(0, getModel().getRowCount() - 1);
   }

   public void removeHighlighter(IDataTableHighlighter<T> highlighter) {
      this.highlighters.remove(highlighter);
      getModel().fireTableRowsUpdated(0, getModel().getRowCount() - 1);
   }

   public TableCellRenderer getCellRenderer(int r, int c) {
      return getRenderer(c);
   }

   public DataTableModel<T> getModel() {
      return model;
   }

   public void addListener(IDataTableSelectionListener<T> listener) {
      this.listeners.add(listener);
   }

   protected void applyFilters() {
      getModel().applyFilters();
   }
   
   protected void setRenderer(int c, TableCellRenderer r) 
   {
      this.columnRenderers.put(c, r);
   }

   private TableCellRenderer getRenderer(int c) {
      TableCellRenderer tcr = columnRenderers.get(c);
      if (tcr == null) {
         tcr = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                  boolean hasFocus, int row, int column) {

               row = self.convertRowIndexToModel(row);
               T rowObj = getModel().get(row);
               Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
               updateRendering(rowObj, c, table, value, isSelected, hasFocus, row, column);
               return c;
            }
         };
         columnRenderers.put(c, tcr);
      }
      return tcr;
   }
}
