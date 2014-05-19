package es.predictia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

/** Store a set of backups of a file in a folder
 * @author Max
 *
 */
public class SnapshotBackup implements Serializable {

	private final String id;

	/**
	 * @param id of the backup file (with extension), for example: backup-svn.tar.gz
	 */
	public SnapshotBackup(String id) {
		super();
		this.id = id;
		this.fileFilter = ioFileFilter();
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SnapshotBackup other = (SnapshotBackup) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}
	
	public File newestFile(File folder) throws NoSuchElementException{
		return NEWEST_TO_OLDEST_FILE_ORDERING.min(folderFiles(folder));
	}
	
	private Collection<File> folderFiles(File folder){
		return FileUtils.listFiles(folder, fileFilter, null);
	}
	
	public boolean saveToFolder(File in, File folder) throws IOException{
		boolean save = true;
		try{
			File newestFile = newestFile(folder);
			InputStream is = new FileInputStream(in);
			InputStream eis = new FileInputStream(newestFile);
			try{
				if(IOUtils.contentEquals(is, eis)){
					LOGGER.info("Found previous identical '" + id + "' backup");
					save = false;
				}
			}finally{
				is.close();
				eis.close();
			}
		}catch(NoSuchElementException e){
			LOGGER.debug("No previous '" + id + "' backups found");
		}
		if(save){
			File destination = new File(folder, fileNameFor(new DateTime()));
			LOGGER.info("Storing new '" + id + "' backup at: "+ destination);
			Files.createParentDirs(destination);
			InputStream is = new FileInputStream(in);
			try{
				Streams.writeToFile(is, destination);
				return true;
			}finally{
				is.close();
			}	
		}
		return false;
	}

	String fileNameFor(DateTime date){
		String name = FilenameUtils.removeExtension(id);
		String extension = FilenameUtils.getExtension(id);
		return DOT_JOINER.join(DASH_JOINER.join(name, date.getMillis()), extension);
	}
	
	static class Context{
		private Set<AgeType> presentTypes = new HashSet<AgeType>();
		/**
		 * Default empty context
		 */
		public Context() {
			super();
		}
		public void withPresentTypes(AgeType... types){
			presentTypes.addAll(Sets.newHashSet(types));
		}
		public boolean containsType(AgeType type){
			return presentTypes.contains(type);
		}
		public boolean saveNewType(AgeType type){
			return presentTypes.add(type);
		}
	}
	
	public static class Configuration{
		private final Set<AgeType> savingTypes;
		/**
		 * saving types
		 */
		public Configuration(AgeType... types) {
			this.savingTypes = Sets.newHashSet(types);
		}
		public Set<AgeType> getSavingTypes() {
			return savingTypes;
		}
	}
	
	public void cleanFolder(File folder, Configuration tbc) throws IOException{
		Multimap<AgeType, File> filesMap = createFilesMap(folderFiles(folder), tbc);
		if(!filesMap.isEmpty()){
			DateTime now = new DateTime();
			Context context = new Context();
			AgeType minAgeType = AgeType.minAgeType(filesMap.keys());
			for(Map.Entry<AgeType, Collection<File>> filesentry : filesMap.asMap().entrySet()){
				AgeType ageType = filesentry.getKey();
				Collection<File> ageFiles = filesentry.getValue();
				// we keep newest file only for minAgeType
				Ordering<File> ordering = minAgeType.equals(ageType) ? NEWEST_TO_OLDEST_FILE_ORDERING : NEWEST_TO_OLDEST_FILE_ORDERING.reverse();
				for(File file : ordering.sortedCopy(ageFiles)){
					DateTime creationDate = getCreationDate(file).get();
					if(deleteFile(now, creationDate, tbc, context)){
						LOGGER.info("Cleaning '" + id + "' backup from: " + file);
						file.delete();
					}
				}
			}
		}
	}

	private Multimap<AgeType, File> createFilesMap(Collection<File> files, Configuration tbc){
		Multimap<AgeType, File> filesMap = ArrayListMultimap.create();
		DateTime now = new DateTime();
		for(File file : files){
			Optional<DateTime> od = getCreationDate(file);
			if(od.isPresent()){
				Optional<AgeType> validAgeType = AgeType.getvalidBackupAgeType(tbc, now, od.get());
				if(validAgeType.isPresent()){
					filesMap.put(validAgeType.get(), file);
				}
			}
		}
		return filesMap;
	}
	
	public enum AgeType{
		
		day, week, month, year, any;
		
		private static final Optional<AgeType> INVALID_TYPE = Optional.absent();
		private static final Collection<AgeType> NO_TYPES = Collections.emptyList();
		
		static Optional<AgeType> getvalidBackupAgeType(Configuration tbc, DateTime now, DateTime creationDate){
			AgeType type = getBackupAgeType(now, creationDate);
			if(!tbc.savingTypes.contains(type)){
				List<AgeType> regularTypes = regularTypes(NO_TYPES);
				List<AgeType> availableRegularTypes = regularTypes(tbc.savingTypes);
				if(!availableRegularTypes.isEmpty()){
					AgeType maxRegularType = availableRegularTypes.get(availableRegularTypes.size() - 1);
					if(regularTypes.indexOf(type) >= regularTypes.indexOf(maxRegularType)){
						type = AgeType.any;
					}else{
						type = maxRegularType;
					}
				}
			}
			return (tbc.savingTypes.contains(type)) ? Optional.of(type) : INVALID_TYPE;
		}
		
		private static List<AgeType> regularTypes(Collection<AgeType> availableTypes){
			List<AgeType> regularTypes = Lists.newArrayList(AgeType.values());
			regularTypes.remove(AgeType.any);
			if(!availableTypes.isEmpty()){
				regularTypes.retainAll(availableTypes);
			}
			return regularTypes;
		}
		
		private static AgeType getBackupAgeType(DateTime now, DateTime creationDate){
			int daysOld = Days.daysBetween(creationDate, now).getDays();
			if(daysOld == 0){
				return day;
			}else if(daysOld <= 7){
				return week;
			}else if(daysOld <= 30){
				return month;
			}else if(daysOld <= 365){
				return year;
			}else{
				return any;
			}
		}
		
		static AgeType minAgeType(Collection<AgeType> presentTypes){
			return AGE_TYPE_ORDERING.min(presentTypes);
		}
		
		private static final Ordering<AgeType> AGE_TYPE_ORDERING = Ordering.explicit(
			Lists.newArrayList(Sets.newTreeSet(Lists.newArrayList(AgeType.values())))
		);
		
	}
	
	static boolean deleteFile(DateTime now, DateTime creationDate, Configuration tbc, Context context){
		Optional<AgeType> type = AgeType.getvalidBackupAgeType(tbc, now, creationDate);
		if(type.isPresent()){
			return !context.saveNewType(type.get());
		}else{
			return true;
		}
	}
	
	IOFileFilter ioFileFilter(){
		final String idName = FilenameUtils.removeExtension(id);
		final String idExtension = FilenameUtils.getExtension(id);
		return new AbstractFileFilter() {
			@Override
			public boolean accept(File file) {
				String name = FilenameUtils.removeExtension(file.getName());
				String extension = FilenameUtils.getExtension(file.getName());
				List<String> nameParts = Lists.newArrayList(DASH_SPLITTER.split(name));
				if(nameParts.size() > 1){
					if(idName.equals(DASH_JOINER.join(nameParts.subList(0, nameParts.size() - 1))) && idExtension.equals(extension)){
						return getCreationDate(file).isPresent();
					}
				}
				return false;
			}
		};
	}

	static Optional<DateTime> getCreationDate(File file){
		String name = FilenameUtils.removeExtension(file.getName());
		List<String> nameParts = Lists.newArrayList(DASH_SPLITTER.split(name));
		if(nameParts.size() > 1){
			try{
				return Optional.of(new DateTime(Long.valueOf(nameParts.get(nameParts.size() - 1))));
			}catch(Exception e){
				return Optional.absent();
			}
		}else{
			return Optional.absent();
		}
	}
	
	static class FileCreationDateOrdering extends Ordering<File>{
		@Override
		public int compare(File left, File right) {
			Optional<DateTime> od1 = getCreationDate(left);
			Optional<DateTime> od2 = getCreationDate(right);
			if(od1.isPresent() && od2.isPresent()){
				return -od1.get().compareTo(od2.get());
			}else if(od1.isPresent()){
				return od2.isPresent() ? -1 : 0;
			}
			return 0;
		}
	}
	
	private final transient IOFileFilter fileFilter;
	
	/**
	 * From newest (first) to oldest
	 */
	private static final transient FileCreationDateOrdering NEWEST_TO_OLDEST_FILE_ORDERING = new FileCreationDateOrdering();
	private static final transient Joiner DASH_JOINER = Joiner.on("-");
	private static final transient Joiner DOT_JOINER = Joiner.on(".");
	private static final transient Splitter DASH_SPLITTER = Splitter.on("-").trimResults().omitEmptyStrings();
	private static final transient Logger LOGGER = LoggerFactory.getLogger(SnapshotBackup.class);
	private static final long serialVersionUID = -6086984431750546409L;
	
}
