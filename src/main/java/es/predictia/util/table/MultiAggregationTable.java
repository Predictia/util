package es.predictia.util.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiAggregationTable<T> extends Table<T>{

	public MultiAggregationTable(Collection<Map<String, T>> tableData, Set<String> groupingColumns, T totalValue){
		super(tableData);
		this.groupingColumns = new LinkedHashSet<String>();
		if(groupingColumns != null){
			this.groupingColumns.addAll(groupingColumns);
		}
		this.groupingColumns.retainAll(getColumns());
		this.totalValue = totalValue;
	}
	
	private Set<String> groupingColumns;
	
	public Set<String> getGroupingColumns() {
		return groupingColumns;
	}
	
	private final T totalValue;
	
	public T getTotalValue() {
		return totalValue;
	}

	@Override
	public Table<T> createTableWithColumns(Collection<String> columns){
		Set<String> dropGroupingColumns = new LinkedHashSet<String>(getGroupingColumns());
		dropGroupingColumns.removeAll(columns);
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			boolean skipRow = false;
			if(!dropGroupingColumns.isEmpty()){
				// elimino las filas correspondientes a las variables de agrupacion que se quitan si estas no son sus filas totales
				for(String dropGroupingColumn : dropGroupingColumns){
					boolean isTotalValue = (getTotalValue() == null) ? (row.get(dropGroupingColumn) == null) : (getTotalValue().equals(row.get(dropGroupingColumn)));
					if(!isTotalValue){
						skipRow = true;
						break;
					}
				}
			}
			if(skipRow) continue;
			Map<String, T> newRow = new LinkedHashMap<String, T>();
			for(String column : columns){
				newRow.put(column, row.get(column));
			}
			newTableData.add(newRow);
		}
		
		Set<String> newGroupingColumns = new LinkedHashSet<String>(getGroupingColumns());
		newGroupingColumns.retainAll(columns);
		MultiAggregationTable<T> newTable = new MultiAggregationTable<T>(newTableData, newGroupingColumns, getTotalValue());
		return newTable;
	}

	@Override
	public String toString() {
		return "MultiAggregationTable [groupingColumns=" + groupingColumns + ", totalValue=" + totalValue + "] " + super.toString();
	}
	
}
