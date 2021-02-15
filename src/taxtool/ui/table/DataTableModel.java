package taxtool.ui.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

abstract public class DataTableModel<T> extends AbstractTableModel {

   private final DataTableModel<T> self = this;
   private final DataTable<T> table;
   private final String[] columns;
   private List<T> activeList = new ArrayList<T>();
   private List<T> unfilteredList = new ArrayList<T>();

   private List<IDataTableFilter<T>> filters = new ArrayList<IDataTableFilter<T>>();

   private final Map<Integer, Class> columnClasses = new HashMap<Integer, Class>();

   abstract protected Object getValueAt(T o, int r, int c);

   public DataTableModel(DataTable<T> table, String[] columns) {
      super();
      this.table = table;
      this.columns = columns;
   }

   @Override
   public final Object getValueAt(int r, int c) {
      r = table.convertRowIndexToModel(r);
      T o = get(r);
      return getValueAt(o, r, c);
   }

   protected void setColumnClass(Class<?> classType, int c) {
      columnClasses.put(c, classType);
   }

   @Override
   public Class<?> getColumnClass(int c) {
      Class type = columnClasses.get(c);
      if (type == null)
         return super.getColumnClass(c);
      return type;
   }

   public T get(int rowIndex) {
      return activeList.get(rowIndex);
   }

   public void addFilter(IDataTableFilter<T> filter) {
      filters.add(filter);
   }

   public void addOrUpdate(List<T> list) {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            for (T t : list) {
               addOrUpdate(t);
            }
         }
      };
      execute(r);
   }

   public void addOrUpdate(T t) {
      Runnable r = new Runnable() {
         @Override
         public void run() {

            int indexOf = activeList.indexOf(t);
            boolean passFilter = passFilter(t);
            if (indexOf == -1) {
               unfilteredList.add(t);
               if (passFilter) {
                  activeList.add(t);
                  fireTableRowsInserted(activeList.size() - 1, activeList.size() - 1);
               }
            } else {

               activeList.set(indexOf, t);
               fireTableRowsUpdated(indexOf, indexOf);
            }
         }
      };
      this.execute(r);
   }

   public void addRow(T t) {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            unfilteredList.add(t);
            if (passFilter(t)) {
               activeList.add(t);
               fireTableRowsInserted(activeList.size() - 1, activeList.size() - 1);
            }
         }
      };
      this.execute(r);
   }

   public void removeRow(T t, boolean removeAll) {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            boolean remove = activeList.remove(t) && removeAll;
            while (remove) {
               remove = activeList.remove(t);
            }
         }
      };
      execute(r);
   }

   public boolean contains(T t) {
      return activeList.contains(t);
   }

   public int indexOf(T t) {
      return activeList.indexOf(t);
   }

   public void clear() {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            int size = activeList.size();
            if (size > 0) {
               activeList.clear();
               fireTableRowsDeleted(0, size - 1);
            }
         }
      };
      execute(r);
   }

   @Override
   public int getColumnCount() {

      return columns.length;
   }

   @Override
   public int getRowCount() {

      return activeList.size();
   }

   @Override
   public String getColumnName(int c) {
      return columns[c];
   }

   private boolean passFilter(T t) {
      boolean pass = true;
      for (IDataTableFilter<T> filter : filters) {
         if (filter.isFilteredOut(t)) {
            pass = false;
            break;
         }
      }
      return pass;
   }

   protected void applyFilters() {
      Runnable r = new Runnable() {
         @Override
         public void run() {
            activeList.clear();
            for (T t : unfilteredList) {
               boolean pass = passFilter(t);
               if (pass)
                  activeList.add(t);
            }
            fireTableDataChanged();
         }
      };
      this.execute(r);
   }

   private void execute(Runnable r) {
      if (SwingUtilities.isEventDispatchThread()) {
         r.run();
      } else {
         SwingUtilities.invokeLater(r);
      }
   }
}
