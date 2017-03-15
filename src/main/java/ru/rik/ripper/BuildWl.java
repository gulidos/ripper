package ru.rik.ripper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ru.rik.ripper.services.Helper;

public class BuildWl {
	private Path res;
	int maxFrom;
	int maxTo;
	public BuildWl() {	}

	public void init( ) throws IOException {
		Properties conf = Helper.getProperties();
		String currentDir = System.getProperty("user.dir");
		res = Paths.get(
				conf.getProperty("res", currentDir + "/res"));
		maxFrom = Integer.parseInt(conf.getProperty("maxFrom", "100"));
		maxTo = Integer.parseInt(conf.getProperty("maxTo", "100"));
		System.out.println("filter: maxFrom=" + maxFrom + " maxTo=" + maxTo);
	}
	
	public void build() throws IOException {
		try (OutputStream fos =	Files.newOutputStream(res.resolve("wl.gz"));
				GZIPOutputStream zip = new GZIPOutputStream(fos);
				PrintWriter pw = new PrintWriter(new BufferedOutputStream(zip))) 
		{
			DirectoryStream<Path> partitions = Helper.getDirectoryStream(res.resolve("part*").toString());
			partitions.forEach(p -> {
				System.out.println(p);
				try (GZIPInputStream zisp = new GZIPInputStream(Files.newInputStream(p));
						BufferedReader brP = new BufferedReader(new InputStreamReader(zisp));
						Stream<String> pStream = brP.lines()) 
				{
					pStream
					.map(s -> s.split(","))
					.filter(a -> a.length ==5)
					.filter(a -> isGood(a))
					.map(a -> String.join(",", a))
					.forEach(e -> pw.println(e));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} 
	}
	
	
	private boolean isGood(String[] a) { //the first element is number
		int fromMe = Integer.parseInt(a[1]);
		int toMe = Integer.parseInt(a[3]);
//		if (0 < fromMe && fromMe < maxFrom && 0 < toMe && toMe < maxTo)
		if (0 < fromMe && fromMe < maxFrom && toMe < maxTo)
			return true;
		return false;
	}
	
	
	public static void main(String[] args) throws IOException {
		BuildWl wl = new BuildWl();
		wl.init();
		wl.build();
	}

}
