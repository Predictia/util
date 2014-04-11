package es.predictia.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.OutputSupplier;

public class FileLineSplitter {

	private final int numberOfLines;
	private final List<File> outFiles;
	
	public int getNumberOfLines() {
		return numberOfLines;
	}
	public List<File> getOutFiles() {
		return outFiles;
	}

	public FileLineSplitter(File in, File outDir, int numLines) throws IOException{
		String fileName = Files.getFileNameWithoutExtension(in.getName());
		String fileExtension = Files.getExtension(in.getName());
		Set<File> outFiles = new LinkedHashSet<File>();
		Charset charset = CharsetDetection.detectEncoding(in);
		LineNumberReader reader = new LineNumberReader(new FileReader(in));
		String line = null;
		int numberLine = 0;
	    while ((line = reader.readLine()) != null){
	    	numberLine = reader.getLineNumber();
	    	final File out = new File(outDir, fileName + "." + outFileNumber(numberLine, numLines) +  "." + fileExtension);
	    	OutputSupplier<OutputStreamWriter> writer = CharStreams.newWriterSupplier(new OutputSupplier<OutputStream>() {
				public OutputStream getOutput() throws IOException {
					return new FileOutputStream(out, true);
				}
			}, charset);
	    	CharStreams.write(line + "\n", writer);
	    	outFiles.add(out);
	    }
	    this.outFiles = Lists.newArrayList(outFiles);
	    this.numberOfLines = numberLine;
	    reader.close();
	}
	
	private static int outFileNumber(int currentLine, int numberLines){
		return (currentLine-1) / numberLines;
	}

}
