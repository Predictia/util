package es.predictia.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

public class FileLister {

	private final File startDir;
	
	public FileLister(File startDir) throws FileNotFoundException{
		if(startDir == null) throw new IllegalArgumentException("Directory should not be null.");
		if(!startDir.exists()) throw new FileNotFoundException("Directory does not exist: " + startDir);
		if(!startDir.isDirectory()) throw new IllegalArgumentException("Is not a directory: " + startDir);
		if(!startDir.canRead()) throw new IllegalArgumentException("Directory cannot be read: " + startDir);
		this.startDir = startDir;
	}
	
	public List<File> getFileList(){
		List<File> result = getFileListingNoSort(startDir);
		if(sortResult != null){
			if(sortResult){
				Collections.sort(result);
			}
		}
		return result;
	}
	
	private Boolean sortResult;
	
	public Boolean getSortResult() {
		return sortResult;
	}
	public void setSortResult(Boolean sortResult) {
		this.sortResult = sortResult;
	}

	private Duration maxDirectoryAge, maxFileAge;
	
	public Duration getMaxDirectoryAge() {
		return maxDirectoryAge;
	}
	public void setMaxDirectoryAge(Duration maxDirectoryAge) {
		this.maxDirectoryAge = maxDirectoryAge;
	}
	public Duration getMaxFileAge() {
		return maxFileAge;
	}
	public void setMaxFileAge(Duration maxFileAge) {
		this.maxFileAge = maxFileAge;
	}
	public File getStartDir() {
		return startDir;
	}
	
	private Pattern filePattern, dirPattern;
	
	public Pattern getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(Pattern filePattern) {
		this.filePattern = filePattern;
	}
	public Pattern getDirPattern() {
		return dirPattern;
	}
	public void setDirPattern(Pattern dirPattern) {
		this.dirPattern = dirPattern;
	}

	private List<File> getFileListingNoSort(File aStartingDir){
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for (File file : filesDirs) {
			if (!file.isFile()) {
				boolean explore = true;
				if(maxDirectoryAge != null){
					Duration dirAge = getAge(new DateTime(file.lastModified()));
					if(dirAge.isLongerThan(maxDirectoryAge)) explore = false;
				}
				if(explore){
					if(dirPattern != null){
						Matcher m = dirPattern.matcher(file.getName());
						if(!m.find()) explore = false;
					}
				}
				if(explore){
					List<File> deeperList = getFileListingNoSort(file);
					result.addAll(deeperList);						
				}
			} else{
				// añado si no es un directorio
				boolean add = true;
				if(maxFileAge != null){
					Duration fileAge = getAge(new DateTime(file.lastModified()));
					if(fileAge.isLongerThan(maxFileAge)) add = false;
				}
				if(add){
					if(filePattern != null){
						Matcher m = filePattern.matcher(file.getName());
						if(!m.find()) add = false;
					}
				}
				if(add) result.add(file); 
			}
		}
		return result;
	}
		
	private static Duration getAge(DateTime creationDate){
		Interval i = new Interval(creationDate, new DateTime());
		return i.toDuration();
	}
	
}
