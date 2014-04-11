package es.predictia.util.table;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CellIndex<T>{
	
	private final Map<String, T> keyCells;
	private final String cellColumn;

	public CellIndex(Map<String, T> keyCells){
		this.keyCells = new LinkedHashMap<String, T>(keyCells);
		this.cellColumn = null;
	}
	
	public CellIndex(String cellColumn, Map<String, T> keyCells){
		this.keyCells = new LinkedHashMap<String, T>(keyCells);
		this.cellColumn = cellColumn;
	}
	
	public CellIndex(String cellColumn, Collection<String> keyColumns, Map<String, T> row) {
		super();
		this.keyCells = new LinkedHashMap<String, T>(row);
		for(String column : row.keySet()){
			if(!keyColumns.contains(column)) this.keyCells.remove(column);
		}
		this.cellColumn = cellColumn;
	}

	public boolean isCellColumnIndex(){
		return getKeyCells().keySet().contains(getCellColumn());
	}
	
	public Map<String, T> getKeyCells() {
		return new LinkedHashMap<String, T>(keyCells);
	}
	
	public String getCellColumn() {
		return cellColumn;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cellColumn == null) ? 0 : cellColumn.hashCode());
		result = prime * result + ((keyCells == null) ? 0 : keyCells.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass())
			return false;
		CellIndex<?> other = (CellIndex<?>) obj;
		if (cellColumn == null) {
			if (other.cellColumn != null) return false;
		} else if (!cellColumn.equals(other.cellColumn)) return false;
		if (keyCells == null) {
			if (other.keyCells != null) return false;
		} else if (!keyCells.equals(other.keyCells)) return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("cellColumn = " + getCellColumn());
		for(Map.Entry<String, T> cell : keyCells.entrySet()){
			sb.append("&");
			sb.append("columns" + "=" + cell.getKey());
			sb.append("&");
			sb.append("values" + "=" + cell.getValue());
		}
		return sb.toString();
	}
		
}