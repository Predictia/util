package es.predictia.util.storage;

import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
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
		HashCode hashByte = Files.hash(input, Hashing.md5());
		String hash = hashByte.toString();
		File firstDir = new File(baseFolder,hash.substring(0,2));
		File secondDir = new File(firstDir,hash.substring(2,4));
		createDir(secondDir);
		File result = new File(secondDir,hash + "." + getExtension(input.getAbsolutePath()));
		Files.asByteSource(input).copyTo(Files.asByteSink(result));
		if(!result.exists()){
			throw new IOException("Output file does not exist: "+result.getAbsolutePath());
		}
		return result;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);
	
}
