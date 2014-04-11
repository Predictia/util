package es.predictia.util.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class IndexedTable<T> extends Table<T> {

	public IndexedTable(Collection<Map<String, T>> tableData, Collection<String> rKeyColumns) {
		super(tableData);
		Set<String> keyColumns = Sets.newLinkedHashSet(rKeyColumns);
		if(!getColumns().containsAll(keyColumns)){
			logger.debug("Input table does not have all indexing columns: " + keyColumns + ". Indexing with available columns: " + getColumns());
			keyColumns.retainAll(getColumns());
		}
		this.keyColumns = keyColumns;
		this.values = new HashMap<CellIndex<T>, IndexedCell<T>>();
		this.indexedTableData = new ArrayList<Map<String, IndexedCell<T>>>();
		this.indexedValues = new HashMap<String, Set<T>>();
		for(Map<String, T> row : tableData){
			Map<String, IndexedCell<T>> indexedRow = new LinkedHashMap<String, IndexedCell<T>>();
			for(String column : row.keySet()){
				T rowValue = row.get(column);
				CellIndex<T> idx = new CellIndex<T>(column, keyColumns, row);
				IndexedCell<T> ic = new IndexedCell<T>(idx, rowValue);
				if(!keyColumns.contains(column)){
					// no es una columna de las de indices, indexo el contenido
					values.put(idx, ic);
				}else{
					// es una columna con indices, guardo los valores de los indices
					Set<T> indexedColumnValues;
					if(indexedValues.containsKey(column)){
						indexedColumnValues = indexedValues.get(column);
					}else{
						indexedColumnValues = new LinkedHashSet<T>();
						indexedValues.put(column, indexedColumnValues);
					}
					indexedColumnValues.add(ic.getCellValue());
				}
				indexedRow.put(column, ic);
			}
			indexedTableData.add(indexedRow);
		}
	}
	
	private final Map<String, Set<T>> indexedValues;
	
	/**
	 * @param indexedColumn Name of indexing column
	 * @return Set of elements found in this column
	 */
	public Set<T> getIndexedValues(String indexedColumn){
		if(keyColumns.contains(indexedColumn)){
			return indexedValues.get(indexedColumn);
		}else{
			logger.debug("Input table does not indexing column: " + indexedColumn);
			return Collections.emptySet();
		}
	}
	
	@Override
	public List<T> getColumnData(String column){
		if(keyColumns.contains(column)){
			return Lists.newArrayList(indexedValues.get(column));
		}else{
			return super.getColumnData(column);
		}
	}
	
	private final List<Map<String, IndexedCell<T>>> indexedTableData;
	
	public Table<IndexedCell<T>> getIndexedDataTable(){
		return new Table<IndexedCell<T>>(indexedTableData);
	}

	private final Map<CellIndex<T>, IndexedCell<T>> values;
	
	private final Collection<String> keyColumns;
	
	public Collection<String> getKeyColumns() {
		return keyColumns;
	}
	
	public IndexedCell<T> getIndexedCell(Map<String, T> keyColumnValues, String oColumn){
		Map<String, T> keyColumns = new LinkedHashMap<String, T>(keyColumnValues);
		CellIndex<T> idx = new CellIndex<T>(oColumn, keyColumns);
		if(this.values.containsKey(idx)){
			return this.values.get(idx);
		}return null;
	}
	
	public T getValue(Map<String, T> keyColumnValues, String oColumn){
		IndexedCell<T> ic = getIndexedCell(keyColumnValues, oColumn);
		return (ic != null) ? ic.getCellValue() : null;
	}
	
	@Override
	public String toString() {
		return "IndexedTable [keyColumns=" + keyColumns + "] " + super.toString();
	}

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(IndexedTable.class);

}
