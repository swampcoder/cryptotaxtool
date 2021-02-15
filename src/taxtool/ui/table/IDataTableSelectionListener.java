package taxtool.ui.table;

import java.util.List;

public interface IDataTableSelectionListener<T> {

   public void notifySelection(List<T> selections);
}
