package es.predictia.util.storage;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * Implementation of {@link StorageService} with a base folder. After setting
 * {@link #setBaseFolder(String)}, {@link #init()} must be called in order to
 * start using the service
 */
public class StorageServiceImpl implements StorageService{
	
	@Override
	public String relativeFilePath(File realPath){
		return baseFolder.toURI().relativize(realPath.toURI()).getPath();
	}
	
	@Override
	public File expandFilePath(String relativePath){
		return new File(baseFolder, relativePath);
	}
	
	@Override
	public File storeFile(File input) throws IOException{
		if(!input.exists()){
			throw new FileNotFoundException("Input file does not exist: "+input.getAbsolutePath());
		}
		String extension = getExtension(input.getAbsolutePath());
		File result = createNewRandomFile(extension);
		LOGGER.debug("Storing " + input + " at: " + result);
		Files.asByteSource(input).copyTo(Files.asByteSink(result));
		if(!result.exists()){
			throw new IOException("Output file does not exist: "+result.getAbsolutePath());
		}
		return result;
	}
	
	private File createNewRandomFile(String extension) throws IOException{
		File result;
		do{
			result = createRandomFile(extension);
		}while(result.exists());
		return result;
	}
	
	private File createRandomFile(String extension) throws IOException{
		String hash = UUID.randomUUID().toString().replaceAll("-", "");
		File dir = baseFolder;
		for(int i = 0; i < dirLevels ; i++){
			dir = new File(dir, hash.substring(i * dirLength, (i+1) * dirLength));
		}
		createDir(dir);
		return new File(dir, hash.substring(dirLevels * dirLength) + "." + extension);
	}
	
	private static void createDir(File dir) throws IOException{
		if(dir.exists()){
			if(!dir.isDirectory()){
				throw new IOException("Not a directory: " + dir);
			}
		}else{
			Files.createParentDirs(dir);
			boolean result = dir.mkdir();
			if(!result){
				throw new IOException("Unable to create directory " + dir);
			}
		}		
	}
	
	public void init() throws Exception {
		if(dirLevels < 0) throw new IllegalArgumentException("Number of dir levels must be positive");
		if(dirLevels > 0){
			if(dirLength < 1) throw new IllegalArgumentException("Number of dir length must be greater than zero");
		}
		initBaseFolder();
	}

	private File baseFolder;
	
	private String baseFolderParameter;
	
	public void setBaseFolder(String baseFolder) {
		this.baseFolderParameter = baseFolder;
	}

	private void initBaseFolder() throws IOException{
		if(baseFolderParameter == null){
			throw new NullPointerException("baseFolderParameter is null");
		}
		File baseFolder = new File(baseFolderParameter);
		try{
			createDir(baseFolder);
		}catch(IOException e){
			baseFolder = new File(System.getProperty("user.home"), baseFolderParameter);
			createDir(baseFolder);
		}
		this.baseFolder = baseFolder;
		LOGGER.info("Using storage folder: " + baseFolder);
	}

	
	private int dirLevels = 2;
	
	/** Level of directory depth for file storage 
	 * @param dirLevels
	 */
	public void setDirLevels(int dirLevels) {
		this.dirLevels = dirLevels;
	}

	private int dirLength = 2;
	
	/**
	 * @param dirLength Length of directory names
	 */
	public void setDirLength(int dirLength) {
		this.dirLength = dirLength;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);
	
}