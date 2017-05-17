package ru.rik.ripper.services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Helper {
	
	public static BufferedReader zipToBufferedReader(ZipFile zipFile) throws IOException {
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		if (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			InputStream zis = zipFile.getInputStream(zipEntry);
			return new BufferedReader(new InputStreamReader(zis), 1024);
		}
		throw new IllegalArgumentException("zip file is empty");
	}
	

	public static DirectoryStream<Path> getDirectoryStream(String files) throws IOException {
		Path maskPath = FileSystems.getDefault().getPath(files);
//		System.out.println("Mask: " + maskPath.toString());
		Path dir = maskPath.getParent();
		if (dir == null) 
		  dir = Paths.get(System.getProperty("user.dir"));
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir, maskPath.getFileName().toString());
		return stream;
	}
	
	
	public static void save(Path path, Map<String, int[]>  map)  {
		try (OutputStream fos =	Files.newOutputStream(path);
				GZIPOutputStream zip = new GZIPOutputStream(fos);
				PrintWriter pw = new PrintWriter(
						new BufferedOutputStream(zip))) 
		{
			map.entrySet().stream()
			.forEach(e -> pw.println(Helper.entryToString(e)));
		} catch (Exception e) {
			e.printStackTrace();	
		}
	}

    
	
	public static Path getPartitionFile(Path source, Path out, String str) {
		String srcFileName = source.getFileName().toString();
		int pos = srcFileName.lastIndexOf('.');
		String dst = pos != -1 ?
				srcFileName.substring(0, pos) + "_"+ str + srcFileName.substring(pos) :
					srcFileName + "_"+ str; 
		return out.resolve(Paths.get(dst));
	}
    

	public static Properties getProperties() throws IOException {
		Properties properties = new Properties();
		Path confFile = Paths.get("ripper.conf");
		try (BufferedReader br = Files.newBufferedReader(confFile)) {
			properties.load(br);
		} catch (IOException e) {
			throw e;
		}
		return properties;
	}
	
	
	public static String entryToString(Entry<String, int[]> e ) {
		int[] v = e.getValue();
		StringBuilder sb = new StringBuilder(e.getKey());
		for (int i = 0; i < v.length; i++) 
			sb.append(",").append(v[i]);
		return sb.toString();		
	}
	
}

