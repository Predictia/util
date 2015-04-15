package es.predictia.util.storage;

import java.io.File;
import java.io.IOException;

/**
 * Store files in the file-system, giving them a relative path in order to
 * facilitate moving the contents to another place
 */
public interface StorageService {

	public String relativeFilePath(File realPath);
	
	public File expandFilePath(String relativePath);
	
	/**
	 * @param input file to save
	 * @return New file in storage folder
	 * @throws IOException
	 */
	public File storeFile(File externalFile) throws IOException;
	
	
}
