package es.predictia.util.storage;

import java.io.File;
import java.io.IOException;

/**
 * Store files in the file-system, giving them a relative path in order to
 * facilitate moving the contents to another place
 */
public interface StorageService {

	/**
	 * @param realPath
	 *            as returned from {@link #storeFile(File)}
	 * @return path in a portable fashion, that can be restored back to a real
	 *         {@link File} using {@link #expandFilePath(String)}
	 * @see #expandFilePath(String)
	 */
	public String relativeFilePath(File realPath);
	
	/** Complementary method to {@link #relativeFilePath(File)}
	 * @see #relativeFilePath(File)
	 */
	public File expandFilePath(String relativePath);
	
	/**
	 * @param externalFile input file to save
	 * @return New file in storage folder
	 * @throws IOException
	 * @see #relativeFilePath(File)
	 */
	public File storeFile(File externalFile) throws IOException;
	
}
