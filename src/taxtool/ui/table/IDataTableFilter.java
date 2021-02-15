package taxtool.ui.table;

public interface IDataTableFilter<T> {

   public boolean isFilteredOut(T t);
}
