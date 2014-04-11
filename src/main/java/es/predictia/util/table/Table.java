package es.predictia.util.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Table<T> {

	public Table(Collection<Map<String, T>> tableData){
		this.tableData = new ArrayList<Map<String,T>>();
		this.columns = new LinkedHashSet<String>();
		if(tableData != null){
			this.tableData.addAll(tableData);
			if(!tableData.isEmpty()){
				this.columns.addAll(tableData.iterator().next().keySet());
			}
		}
	}
	
	private final List<Map<String, T>> tableData;
	
	private final Set<String> columns;

	public Set<String> getColumns() {
		return columns;
	}

	public List<Map<String, T>> getTableData() {
		return tableData;
	}
	
	public List<T> getColumnData(String column){
		List<T> columnData = new ArrayList<T>();
		for(Map<String, T> row : getTableData()){
			if(row.containsKey(column)){
				columnData.add(row.get(column));
			}
		}
		return columnData;
	}
	
	/** Apply one opperation on each of table's column values
	 * @param <N>
	 * @param columnSummaryFunction
	 * @return Map of <column,summary of column>
	 */
	public <N> Map<String, N> getTableSummary(Function<Iterable<T>, N> columnSummaryFunction){
		Map<String, N> summary = new LinkedHashMap<String, N>();
		for(String column : getColumns()){
			summary.put(column, columnSummaryFunction.apply(getColumnData(column)));
		}
		return summary;
	}
	
	/**
	 * @param predicate
	 * @return Table containing rows predicate(row) == true
	 */
	public Table<T> createTableWithFilter(Predicate<Map<String, T>> predicate){
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			if(predicate.apply(row)){
				Map<String, T> newRow = new LinkedHashMap<String, T>();
				for(String column : getColumns()){
					newRow.put(column, row.get(column));
				}
				newTableData.add(newRow);
			}
		}
		Table<T> newTable = new Table<T>(newTableData);
		return newTable;
	}
	
	/**
	 * @param filterColumn
	 * @param value
	 * @return Table containing rows with row[filterColumn] == value (null safe)
	 */
	public Table<T> createTableWithFilter(String filterColumn, T value){
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			if(row.get(filterColumn) == null){
				if(value != null) continue;
			}else{
				if(!row.get(filterColumn).equals(value)) continue;
			}
			Map<String, T> newRow = new LinkedHashMap<String, T>();
			for(String column : getColumns()){
				newRow.put(column, row.get(column));
			}
			newTableData.add(newRow);
		}
		Table<T> newTable = new Table<T>(newTableData);
		return newTable;
	}
	
	/**
	 * @param filterColumn
	 * @param value
	 * @return Table containing rows with row[filterColumn] != value (null safe)
	 */
	public Table<T> createTableWithExcludingFilter(String filterColumn, T value){
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			if(value == null){
				if(row.get(filterColumn) == null) continue;
			}else{
				if(value.equals(row.get(filterColumn))) continue;
			}
			Map<String, T> newRow = new LinkedHashMap<String, T>();
			for(String column : getColumns()){
				newRow.put(column, row.get(column));
			}
			newTableData.add(newRow);
		}
		Table<T> newTable = new Table<T>(newTableData);
		return newTable;
	}
	
	public Table<T> createTableWithTranslatedColumn(String translatedColumn, final Map<T, T> mappedValues, final T notFoundValue){
		return createTableWithTranslatedColumn(translatedColumn, new Function<T, T>() {
			@Override
			public T apply(T value) {
				if(value != null){
					return (mappedValues.containsKey(value)) ? mappedValues.get(value) : notFoundValue;
				}
				return null;
			}
		});
	}
	
	public <N> Table<N> createTranslatedTable(Function<T, N> mappingFunction){
		List<Map<String, N>> newRows = new ArrayList<Map<String, N>>();
		for(Map<String, T> row : getTableData()){
			Map<String, N> newRow = new LinkedHashMap<String, N>();
			for(Map.Entry<String, T> rowEntry : row.entrySet()){
				newRow.put(rowEntry.getKey(), mappingFunction.apply(rowEntry.getValue()));
			}
			newRows.add(newRow);
		}
		Table<N> newTable = new Table<N>(newRows);
		return newTable;
	}

	/**
	 * @param <N>
	 * @param mappingFunction
	 * @return Nueva tabla a partir de una transformacion a nievel de filas. Si
	 *         mappingFunction devuelve nulo para una fila, la fila se omite en
	 *         la tabla destino
	 */
	public <N> Table<N> createTableWithTranslatedRows(Function<Map<String, T>, Map<String, N>> mappingFunction){
		List<Map<String, N>> newRows = new ArrayList<Map<String, N>>();
		for(Map<String, T> row : getTableData()){
			Map<String, N> nRow = mappingFunction.apply(row);
			if(nRow != null){
				newRows.add(nRow);
			}
		}
		Table<N> newTable = new Table<N>(newRows);
		return newTable;
	}
	
	public Table<T> createTableWithTranslatedColumn(String translatedColumn, Function<T, T> mappingFunction){
		if(!getColumns().contains(translatedColumn)){
			logger.debug("Column to translate not found: " + translatedColumn + ". Columns present: " + getColumns() + ". Table data: " + getTableData());
			return new Table<T>(getTableData());
		}
		List<Map<String, T>> newRows = new ArrayList<Map<String, T>>();
		for(Map<String, T> row : getTableData()){
			Map<String, T> newRow = new LinkedHashMap<String, T>(row);
			newRow.put(translatedColumn, mappingFunction.apply(row.get(translatedColumn)));
			newRows.add(newRow);
		}
		Table<T> newTable = new Table<T>(newRows);
		return newTable;
	}
	
	/**
	 * @param oTable
	 * @param newColumn
	 * @param newColumnAllias
	 * @return Nueva tabla con una columna adicional de la tabla oTable
	 * @throws IllegalArgumentException si la tabla no dispone de la columna indice de otable 
	 */
	public Table<T> createTableWithJoinedColumn(IndexedTable<T> oTable, String newColumn, String newColumnAllias) throws IllegalArgumentException{
		if(!oTable.getColumns().contains(newColumn)){
			logger.debug("Input new column (" + newColumn + ") is not present in this table. Columns present: " + getColumns() + ". Table data: " + getTableData());
			throw new IllegalArgumentException("Input new column (" + newColumn + ") is not present in this table.");
		}
		List<Map<String,T>> newData = new ArrayList<Map<String,T>>(getTableData());
		for(Map<String, T> row : newData){
			Map<String, T> rowQuery = new LinkedHashMap<String, T>(row);
			for(String column : row.keySet()){
				if(!oTable.getKeyColumns().contains(column)) rowQuery.remove(column);
			}
			T newValue = oTable.getValue(rowQuery, newColumn);
			row.put(newColumnAllias, newValue);
		}
		return new Table<T>(newData);
	}
	
	public Table<T> createTableWithColumns(Collection<String> columns){
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			Map<String, T> newRow = new LinkedHashMap<String, T>();
			for(String column : columns){
				newRow.put(column, row.get(column));
			}
			newTableData.add(newRow);
		}
		Table<T> newTable = new Table<T>(newTableData);
		return newTable;
	}
	
	/**
	 * @param newColumn
	 * @param newColumnAllias
	 * @return Expande una columna de la tabla utilizando una funcion
	 * @throws IllegalArgumentException si la tabla no dispone de la columna indice de otable 
	 */
	public Table<T> createTableWithExpandedColumn(String columnToExpand, Function<T,Map<String,T>> expandFunction, Boolean preserveColumn) throws IllegalArgumentException{
		if(!this.getColumns().contains(columnToExpand)){
			logger.debug("Input column (" + columnToExpand + ") is not present in this table. Columns present: " + getColumns() + ". Table data: " + getTableData());
			throw new IllegalArgumentException("Input new column (" + columnToExpand + ") is not present in this table.");
		}
		List<Map<String, T>> newTableData = new ArrayList<Map<String,T>>();
		for(Map<String, T> row : getTableData()){
			Map<String, T> newRow = new LinkedHashMap<String, T>();
			for(String column : columns){
				if(!column.equals(columnToExpand)){
					newRow.put(column, row.get(column));
				}else{
					if(preserveColumn){
						newRow.put(column, row.get(column));
					}
					// expand column
					Map<String,T> newColumns = expandFunction.apply(row.get(column));
					for(Map.Entry<String,T> newColumn : newColumns.entrySet()){
						newRow.put(newColumn.getKey(),newColumn.getValue());
					}
				}
			}
			newTableData.add(newRow);
		}
		return new Table<T>(newTableData);
	}
	
	@Override
	public String toString() {
		return "Table [columns=" + columns + ", tableData=" + tableData + "]";
	}
	
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Table.class);

}
