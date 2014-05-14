package es.predictia.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;

public class Files{

	private Files() {}
	
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	public static String getPath(String directorio) throws URISyntaxException {
		ClassLoader classLoader = null;
		if (directorio.startsWith(CLASSPATH_URL_PREFIX)) {
			classLoader = Thread.currentThread().getContextClassLoader();
			directorio = directorio.substring(CLASSPATH_URL_PREFIX.length());
			URL url = classLoader.getResource(directorio);
			File file = new File(url.toURI());
			directorio = file.getAbsolutePath() + File.separatorChar;
		}
		return directorio;
	}
	
	/**
	 * Get the parent of the given path.
	 * 
	 * @param path
	 *            The path for which to retrieve the parent
	 * 
	 * @return The parent path. /sub/sub2/index.html --TO-- /sub/sub2/ If the given
	 *         path is the root path ("/" or ""), return "/".
	 */
	public static String getDirectoryPath(String path) {
		if ((path == null) || path.equals("") || path.equals("/")) {
			return "/";
		}
		int lastSlashPos = path.lastIndexOf('/');
		if (lastSlashPos >= 0){
			return path.substring(0,lastSlashPos+1);
		} else {
			return "/"; // we expect people to add + "/somedir on their own
		}
	}
	
	public static String getDirectoryPath(URL url) {
		return getDirectoryPath(url.toString());
	}
	
	public static String getLocationFileName(String location){
		String name = String.valueOf(location);
		final String[] separators = new String[]{"/", "\\"};
		for(String separator : separators){
			if(name.contains(separator)){
				name = name.substring(name.lastIndexOf(separator) + 1);
			}
		}
		return name;
	}
	
	/**
	 * Fetch the entire contents of a text file, and return it in a String
	 * 
	 * @param aFile is a file which already exists and can be read.
	 */
	public static String getContents(File aFile) throws IOException {
		StringBuilder contents = new StringBuilder();
		BufferedReader input = getFileReader(aFile);
		String line = null;
		while ((line = input.readLine()) != null) {
			contents.append(line);
			contents.append(System.getProperty("line.separator"));
		}
		return contents.toString();
	}
	
	 /**
     * Get file reader that corresponds to file extension.
     * @param file the file name
     * @return a file reader that uncompresses data if needed
     * @throws IOException
     */
    public static BufferedReader getFileReader(final File file) throws IOException{
    	if(file.getName() != null){
    		// support compressed files
            if(file.getName().toLowerCase().endsWith(".gz")){
                return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
            }else if(file.getName().toLowerCase().endsWith(".zip")){
                return new BufferedReader(new InputStreamReader(new ZipInputStream(new FileInputStream(file))));
            }
    	}
    	return new BufferedReader(new FileReader(file));
    }
	
	/** Saca un archivo a partir de una URL. Capaz de trabajar con URL-unsafe characters
	 * @param url
	 * @return
	 */
	public static File getFile(URL url){
		try{
			return new File(url.toURI());
		}
		catch(URISyntaxException e) {
			return new File(url.getPath());
		}
	}
	
	public static File getFolder(File file) throws IOException{
		if(!file.exists()){
			com.google.common.io.Files.createParentDirs(file);
			boolean success = file.mkdir();
			if(!success){
				throw new IOException("Error creando la carpeta de almacenamiento: " + file.getAbsolutePath());
			}
			return file;
		}
		else{
			if(!file.isDirectory()){
				throw new IOException("La carpeta de almacenamiento no es un directorio: " + file.getAbsolutePath());
			}
			return file;
		}
	}
	
	/**
	 * @param fileName
	 * @return Nombre del archivo sin la extension, o "" si hay algun problema
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}
	
	/**
	 * @param fileName String con el nombre del archivo, por ejemplo hola.txt
	 * @return extension (en el ejemplo seria "txt"), o "" si no tiene
	 */
	public static String getExtension(String fileName){
		if(fileName.contains(".")){
			String extension = "";
			StringTokenizer st = new StringTokenizer(fileName, ".");
			while(st.hasMoreTokens()){
				extension = st.nextToken();
			}
			return extension;
		}
		return "";
	}
	
	public static String setExtension(String fileName, String extension){
		String oldExtension = getExtension(fileName);
		if(oldExtension.length() > 0){
			fileName = fileName.substring(0, fileName.length() - oldExtension.length());
			return fileName + extension;
		}
		else return fileName + "." + extension;
	}
	
	public static void unzip(BufferedInputStream in, File rutaDestino) throws IOException {
		File fileToUnzip = File.createTempFile("unzip-", ".zip");
		{
			FileOutputStream fos = new FileOutputStream(fileToUnzip);
			IOUtils.copy(in, fos);
			fos.close();
		}
		
		ZipFile zipfile = new ZipFile(fileToUnzip);
		Enumeration<?> e = zipfile.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			if (entry.isDirectory()) {
				// Assume directories are stored parents first then children.
				// This is not robust, just for demonstration purposes.
				(new File(rutaDestino, entry.getName())).mkdir();
			} else {
				FileOutputStream fos = new FileOutputStream(new File(rutaDestino, entry.getName()));
				BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry));
				IOUtils.copy(is, fos);
				is.close();
				fos.close();
			}
		}
		fileToUnzip.delete();
		zipfile.close();
	}
	
	/**
	 * @param contents Map with filename and file contents
	 * @param os Output stream to write zip into
	 * @throws IOException
	 */
	public static void zip(Map<String, InputStream> contents, OutputStream os) throws IOException {
		byte b[] = new byte[512];
		ZipOutputStream zout = new ZipOutputStream(os);
		for(Map.Entry<String, InputStream> content : contents.entrySet()) {
			InputStream in = content.getValue();
			ZipEntry e = new ZipEntry(CharMatcher.ASCII.retainFrom(content.getKey()));
			zout.putNextEntry(e);
			int len = 0;
			while ((len = in.read(b)) != -1) {
				zout.write(b, 0, len);
			}
			zout.closeEntry();
		}
		zout.close();
	}
	
	private static class RelativePath {
		/**
		 * break a path down into individual elements and add to a list. example
		 * : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
		 * 
		 * @param f
		 *            input file
		 * @return a List collection with the individual elements of the path in
		 *         reverse order
		 */
		private static List<String> getPathList(File f) {
			List<String> l = new ArrayList<String>();
			File r;
			try {
				r = f.getCanonicalFile();
				while (r != null) {
					l.add(r.getName());
					r = r.getParentFile();
				}
			} catch (IOException e) {
				LOGGER.warn(e.getMessage());
				l = Collections.emptyList();
			}
			return l;
		}

		/**
		 * figure out a string representing the relative path of 'f' with respect to 'r'
		 * 
		 * @param r home path
		 * @param f path of file
		 */
		private static String matchPathLists(List<String> r, List<String> f) {
			int i;
			int j;
			StringBuilder s = new StringBuilder();
			// start at the beginning of the lists
			// iterate while both lists are equal
			i = r.size() - 1;
			j = f.size() - 1;

			// first eliminate common root
			while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
				i--;
				j--;
			}

			// for each remaining level in the home path, add a ..
			for (; i >= 0; i--) {
				s.append(".." + File.separator);
			}

			// for each level in the file path, add the path
			for (; j >= 1; j--) {
				s.append(f.get(j) + File.separator);
			}

			// file name
			s.append(f.get(j));
			return s.toString();
		}

		/**
		 * get relative path of File 'f' with respect to 'home' directory example : home = /a/b/c f = /a/d/e/x.txt s = getRelativePath(home,f) = ../../d/e/x.txt
		 * 
		 * @param home base path, should be a directory, not a file, or it doesn't make sense
		 * @param f file to generate path for
		 * @return path from home to f as a string
		 */
		public static String getRelativePath(File home, File f) {
			List<String> homelist = getPathList(home);
			List<String> filelist = getPathList(f);
			return matchPathLists(homelist, filelist);
		}
	}
	
	public static String getRelativePath(File home, File file){
		return RelativePath.getRelativePath(home, file);
	}
	
	public static Collection<File> filterFilesByStartName(File folder, String nameStart){
		IOFileFilter fileFilter;
	    fileFilter = FileFilterUtils.trueFileFilter();
	    fileFilter = FileFilterUtils.prefixFileFilter(nameStart);
	    return FileUtils.listFiles(folder, fileFilter, null);
	}
	
	public static Collection<File> filterFilesByAgeFile(File folder, DateTime dateLimit){
		IOFileFilter fileFilter;
		fileFilter = FileFilterUtils.trueFileFilter();
		fileFilter = FileFilterUtils.ageFileFilter(dateLimit.toDate());		
		return FileUtils.listFiles(folder, fileFilter, null);
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Files.class);
	
}
