package es.predictia.util.table;

public class IndexedCell<T> {

	public IndexedCell(CellIndex<T> cellIndex, T cellValue){
		this.cellIndex = cellIndex;
		this.cellValue = cellValue;
	}
	
	private final CellIndex<T> cellIndex;
	private final T cellValue;
	
	public CellIndex<T> getCellIndex() {
		return cellIndex;
	}
	public T getCellValue() {
		return cellValue;
	}
	public boolean isIndexCell(){
		return cellIndex.isCellColumnIndex();
	}
	public String getColumn(){
		return cellIndex.getCellColumn();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellIndex == null) ? 0 : cellIndex.hashCode());
		result = prime * result + ((cellValue == null) ? 0 : cellValue.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		IndexedCell<?> other = (IndexedCell<?>) obj;
		if (cellIndex == null) {
			if (other.cellIndex != null) return false;
		} else if (!cellIndex.equals(other.cellIndex)) return false;
		if (cellValue == null) {
			if (other.cellValue != null) return false;
		} else if (!cellValue.equals(other.cellValue)) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ((cellValue == null) ? cellValue + "" : cellValue.toString());
	}
	
}
