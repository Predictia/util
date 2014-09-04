package es.predictia.util.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TableTests {

	@Test
	public void tableTest() throws Exception{
		for(Table<Integer> table : getTestTables()){
			{
				Assert.assertEquals(2, table.getColumns().size());
				Assert.assertTrue(table.getColumns().containsAll(Lists.newArrayList(COL1, COL2)));
				Assert.assertEquals(3, table.getColumnData(COL1).size());
				Assert.assertEquals(3, table.getColumnData(COL2).size());
				Assert.assertTrue(table.getColumnData(COL1).containsAll(Lists.newArrayList(CELL11, CELL21, CELL31)));
				Assert.assertTrue(table.getColumnData(COL2).containsAll(Lists.newArrayList(CELL12, CELL22, CELL32)));
				Assert.assertEquals(1, table.createTableWithColumns(Lists.newArrayList(COL1)).getColumns().size());
				Assert.assertEquals(COL1, table.createTableWithColumns(Lists.newArrayList(COL1)).getColumns().iterator().next());
				Assert.assertEquals(3, table.createTableWithColumns(Lists.newArrayList(COL1)).getColumnData(COL1).size());
			}{
				Table<Integer> table2 = table.createTableWithTranslatedColumn(COL1, new Function<Integer, Integer>() {
					@Override
					public Integer apply(Integer arg0) {
						return 2*arg0;
					}
				});
				Assert.assertEquals(3, table.getColumnData(COL1).size());
				Assert.assertEquals(3, table.getColumnData(COL2).size());
				Assert.assertTrue(table2.getColumnData(COL1).containsAll(Lists.newArrayList(2*CELL11, 2*CELL21)));
				Assert.assertTrue(table2.getColumnData(COL2).containsAll(Lists.newArrayList(CELL12, CELL22)));
			}
		}
	}
	
	private static final String COL1 = "col1", COL2 = "col2";
	private static final Integer CELL11 = 11, CELL12 = 12, CELL21 = 21, CELL22 = 22, CELL31 = 31, CELL32 = null;
	
	private static Iterable<Table<Integer>> getTestTables(){
		List<Table<Integer>> tables = new ArrayList<Table<Integer>>();
		tables.add(new Table<Integer>(getTestData()));
		tables.add(new IndexedTable<Integer>(getTestData(), Lists.newArrayList(COL1)));
		tables.add(new IndexedTable<Integer>(getTestData(), Lists.newArrayList(COL1, COL2)));
		return tables;
	}
	
	private static Collection<Map<String, Integer>> getTestData(){
		List<Map<String, Integer>> data = new ArrayList<Map<String,Integer>>();
		Map<String, Integer> row1 = new LinkedHashMap<String, Integer>();
		row1.put(COL1, CELL11);
		row1.put(COL2, CELL12);
		data.add(row1);
		Map<String, Integer> row2 = new LinkedHashMap<String, Integer>();
		row2.put(COL1, CELL21);
		row2.put(COL2, CELL22);
		data.add(row2);
		Map<String, Integer> row3 = new LinkedHashMap<String, Integer>();
		row3.put(COL1, CELL31);
		row3.put(COL2, CELL32);
		data.add(row3);
		return data;
	}
	
	@Test
	public void tableExpandColumn() throws Exception{
		int numberOfCols = 5;
		int numberOfRows = 1000;
		
		List<String> cols = new ArrayList<String>();
		for(int i=0;i<numberOfCols;i++) cols.add("col" + i);
		Collection<Map<String, Integer>> tableData = getRandomData(numberOfRows, cols);
		IndexedTable<Integer> table = new IndexedTable<Integer>(tableData, cols);
		
		final String[] labels = new String[]{"col0_1","col0_10","col0_100","col0_1000","col0_other"};
		
		Table<Integer> newTable = table.createTableWithExpandedColumn("col0", new Function<Integer,Map<String,Integer>>() {
			@Override
			public Map<String, Integer> apply(Integer input) {				
				Map<String,Integer> values = new HashMap<String,Integer>();
				for(String label : labels){
					values.put(label,null);					
				}
				if(input<1){
					values.put(labels[0], input);
				}else if(input<10){
					values.put(labels[1], input);
				}else if(input<100){
					values.put(labels[2], input);
				}else if(input<1000){
					values.put(labels[3], input);
				}else{
					values.put(labels[4], input);
				}
				return values;
			}
		}, false);
		
		Assert.assertEquals(newTable.getColumns().size(),cols.size()-1+labels.length);
	}
	
	@Test
	public void tableIndexPerformance() throws Exception{
		int numberOfIdxCols = 3;
		int numberOfOtherCols = 20;
		int numberOfRows = 1000;
		int numberOfQuerys = numberOfOtherCols*numberOfRows;
		
		List<String> idxCols = new ArrayList<String>(), otherCols = new ArrayList<String>();
		for(int i=0;i<numberOfIdxCols;i++) idxCols.add("idxCol" + i);
		for(int i=0;i<numberOfOtherCols;i++) otherCols.add("otherCol" + i);
		Collection<Map<String, Integer>> tableData = getRandomData(numberOfRows, Iterables.concat(idxCols, otherCols));
		IndexedTable<Integer> table = new IndexedTable<Integer>(tableData, idxCols);
		
		Stopwatch c = Stopwatch.createStarted();
		Random random = new Random();
		for(int i=0;i<numberOfQuerys;i++){
			Map<String, Integer> randomSearch = new HashMap<String, Integer>();
			for(String idxCol : idxCols){
				randomSearch.put(idxCol, random.nextInt());
			}
			table.getValue(randomSearch, otherCols.get(random.nextInt(otherCols.size())));
		}
		logger.debug("buscar por la tabla toook " + c);
	}
	
	private static Collection<Map<String, Integer>> getRandomData(int numRows, Iterable<String> cols){
		List<Map<String, Integer>> data = new ArrayList<Map<String,Integer>>();
		Random random = new Random();
		for(int i=0; i<numRows; i++){
			Map<String, Integer> row = new LinkedHashMap<String, Integer>();
			for(String col : cols){
				row.put(col, random.nextInt());
			}
			data.add(row);
		}
		return data;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(TableTests.class);

}