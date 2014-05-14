package es.predictia.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import es.predictia.util.SnapshotBackup.AgeType;
import es.predictia.util.SnapshotBackup.Configuration;
import es.predictia.util.SnapshotBackup.Context;
import es.predictia.util.Streams;

public class SnapshotBackupTest {

	@Test
	public void testTimedBackupFileName() throws Exception{
		String fileName = "backup-svn-myrepo.tar";
		String extension = "gz";
		SnapshotBackup tbf = new SnapshotBackup(fileName + "." + extension);
		String name = tbf.fileNameFor(new DateTime());
		Assert.assertTrue(name.startsWith(fileName));
		Assert.assertTrue(name.endsWith(extension));	
	}
	
	@Test
	public void testOrdering() throws Exception{
		SnapshotBackup.ReverseOrdering ro = new SnapshotBackup.ReverseOrdering();
		SnapshotBackup tbf = new SnapshotBackup("mybackup.tar.gz");
		Assert.assertTrue(ro.isOrdered(Lists.newArrayList(
			getExampleFile(tbf, new DateTime()),
			getExampleFile(tbf, new DateTime().minusYears(1))
			
		)));
		Assert.assertFalse(ro.isOrdered(Lists.newArrayList(
			getExampleFile(tbf, new DateTime().minusDays(1)),
			getExampleFile(tbf, new DateTime())
			
		)));
	}
	
	@Test
	public void testFilter() throws Exception{
		DateTime now = new DateTime();
		SnapshotBackup tbf = new SnapshotBackup("mybackup.tar.gz");
		Assert.assertTrue(tbf.ioFileFilter().accept(getExampleFile(tbf, now)));
		Assert.assertFalse(tbf.ioFileFilter().accept(getExampleFile(new SnapshotBackup("myOtherbackup.tar.gz"), now)));
	}
	
	@Test
	public void testTimedBackupDate() throws Exception{
		DateTime now = new DateTime();
		File exampleFile = getExampleFile(new SnapshotBackup("mybackup.tar.gz"), now);
		Assert.assertEquals(now, SnapshotBackup.getCreationDate(exampleFile).get());
	}
	
	@Test
	public void testFileSave() throws Exception{
		String firstString = "aaaa3222222232aaaaaaaa";
		String secondString = "secondString";
		File testFile = File.createTempFile("test-", ".txt");
		Streams.writeToFile(new ByteArrayInputStream(firstString.getBytes(Charsets.UTF_8)), testFile);
		File testFile2 = File.createTempFile("test-", ".txt");
		Streams.writeToFile(new ByteArrayInputStream(secondString.getBytes(Charsets.UTF_8)), testFile2);
		SnapshotBackup tbf = new SnapshotBackup("mybackup-" + System.currentTimeMillis() + ".txt");
		Assert.assertTrue(tbf.saveToFolder(testFile, tempFolder()));
		Assert.assertEquals(firstString, Files.toString(tbf.newestFile(tempFolder()), Charsets.UTF_8));
		Assert.assertFalse(tbf.saveToFolder(testFile, tempFolder()));
		Assert.assertEquals(firstString, Files.toString(tbf.newestFile(tempFolder()), Charsets.UTF_8));
		Assert.assertTrue(tbf.saveToFolder(testFile2, tempFolder()));
		Assert.assertEquals(secondString, Files.toString(tbf.newestFile(tempFolder()), Charsets.UTF_8));
		tbf.cleanFolder(tempFolder(), new Configuration(AgeType.day));
		Assert.assertEquals(secondString, Files.toString(tbf.newestFile(tempFolder()), Charsets.UTF_8));
		testFile.delete();
		testFile2.delete();
	}
	

	@Test
	public void testBackupFileAgeTypes() throws Exception{
		{
			Configuration conf = new Configuration(AgeType.day, AgeType.any);
			Set<AgeType> types = getTypesFor2Years(conf);
			Assert.assertEquals(2, types.size());
			Assert.assertTrue(types.containsAll(conf.getSavingTypes()));
		}{
			Configuration conf = new Configuration(AgeType.day, AgeType.month, AgeType.any);
			Set<AgeType> types = getTypesFor2Years(conf);
			Assert.assertEquals(3, types.size());
			Assert.assertTrue(types.containsAll(conf.getSavingTypes()));
		}{
			Configuration conf = new Configuration(AgeType.values());
			Set<AgeType> types = getTypesFor2Years(conf);
			Assert.assertEquals(AgeType.values().length, types.size());
			Assert.assertTrue(types.containsAll(conf.getSavingTypes()));
		}
		
	}
	
	private Set<AgeType> getTypesFor2Years(Configuration conf){
		Set<AgeType> types = new LinkedHashSet<AgeType>();
		DateTime now = new DateTime();
		for(int i = 0; i < (365*2); i++){
			Optional<AgeType> ot = SnapshotBackup.AgeType.getvalidBackupAgeType(
				conf, now, now.minusDays(i)
			);
			if(ot.isPresent()){
				types.add(ot.get());
			}
			
		}
		return types;
	}
	
	@Test
	public void testBackupFileAgeType() throws Exception{
		DateTime now = new DateTime();
		Assert.assertEquals(
			AgeType.any, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.any), 
				now, now.minusDays(4)
			).get()
		);
		Assert.assertEquals(
			AgeType.day, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.any), 
				now, now.minusHours(1)
			).get()
		);
		Assert.assertEquals(
			AgeType.week, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.week, AgeType.any), 
				now, now.minusDays(7)
			).get()
		);
		Assert.assertEquals(
			AgeType.any, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.week, AgeType.any), 
				now, now.minusDays(8)
			).get()
		);
		Assert.assertEquals(
			AgeType.week, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.week, AgeType.month, AgeType.any), 
				now, now.minusDays(6)
			).get()
		);
		Assert.assertEquals(
			AgeType.month, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.week, AgeType.month, AgeType.any), 
				now, now.minusDays(20)
			).get()
		);
		Assert.assertEquals(
			AgeType.month, SnapshotBackup.AgeType.getvalidBackupAgeType(
				new Configuration(AgeType.day, AgeType.month, AgeType.any), 
				now, now.minusDays(2)
			).get()
		);
	}
	
	@Test
	public void testTimedBackupFileDelete() throws Exception{
		Configuration conf = new Configuration(SnapshotBackup.AgeType.values());
		{
			SnapshotBackup.Context ctx = new Context();
			Assert.assertFalse(ctx.containsType(SnapshotBackup.AgeType.day));
			Assert.assertFalse(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusHours(1), conf, ctx));
			Assert.assertTrue(ctx.containsType(SnapshotBackup.AgeType.day));
			Assert.assertTrue(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusHours(1), conf, ctx));
			Assert.assertTrue(ctx.containsType(SnapshotBackup.AgeType.day));
		}{
			SnapshotBackup.Context ctx = new Context();
			Assert.assertFalse(ctx.containsType(SnapshotBackup.AgeType.week));
			// first one old file
			Assert.assertFalse(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusDays(3), conf, ctx));
			Assert.assertTrue(ctx.containsType(SnapshotBackup.AgeType.week));
			Assert.assertTrue(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusDays(3), conf, ctx));
			Assert.assertTrue(ctx.containsType(SnapshotBackup.AgeType.week));
			// then one recent file
			Assert.assertFalse(ctx.containsType(SnapshotBackup.AgeType.day));
			Assert.assertFalse(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusHours(1), conf, ctx));
			Assert.assertTrue(ctx.containsType(SnapshotBackup.AgeType.day));
		}{
			SnapshotBackup.Context ctx = new Context();
			Assert.assertFalse(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusYears(2), conf, ctx));
			Assert.assertTrue(SnapshotBackup.deleteFile(new DateTime(), new DateTime().minusYears(2), conf, ctx));
		}
	}

	private static File getExampleFile(SnapshotBackup tbf, DateTime age){
		return new File(tempFolder(), tbf.fileNameFor(age));
	}	
	
	private static File tempFolder(){
		return new File(System.getProperty("java.io.tmpdir"));
	}
	
}
