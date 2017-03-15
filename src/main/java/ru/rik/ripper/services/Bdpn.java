package ru.rik.ripper.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipFile;

public class Bdpn {
	private Set<String > set;
	
	public Bdpn() {
		set = new HashSet<>();
	}
	
	public boolean in(String n) {
		return set.contains(n);
	}
	
	
	public int load(Path path) throws IOException {
		try (ZipFile zipFile = new ZipFile(path.toFile());
				BufferedReader br = Helper.zipToBufferedReader(zipFile);
				Stream<String> strm = br.lines())
		{
			strm
			.skip(1)
		    .map(line -> line.split(","))
		    .filter(a -> a.length > 2)
		    .forEach(a -> set.add(a[0]));
		} 
		return set.size();
	}
	
	
	public void dumpBdpn(Path file) throws IOException {
		try (  OutputStream fos =	Files.newOutputStream(file);
				GZIPOutputStream zip = new GZIPOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(zip, 1024));) {
			oos.writeObject(set);
		} catch (IOException ioe) {
			throw ioe;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void loadBdpnSer(Path file) {
		try (  InputStream fis = Files.newInputStream(file);
				GZIPInputStream zip = new GZIPInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(zip, 1024));) {
			Set<String > newBdpnMap = new HashSet<String>();
			newBdpnMap = (Set<String >) ois.readObject();
			set = newBdpnMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	


	
	public static void main(String[] args) throws IOException {
		Path path = Paths.get("/opt/ripper/Port_All_201703070000_1203.zip");
		Bdpn b = new Bdpn();
		
		long start = System.nanoTime();
		int n = b.load(path);
		System.out.println("loaded " + n + " in " + String.valueOf(System.nanoTime() - start));
		start = System.nanoTime();
		b.dumpBdpn(Paths.get("/opt/ripper/bdpnMap.ser.gz"));
		System.out.println("written " + " in " + String.valueOf(System.nanoTime() - start));
//		
//		b.loadBdpnSer(Paths.get("/opt/ripper/bdpnMap.ser.gz"));
//		System.out.println("read " + " in " + String.valueOf(System.nanoTime() - start));
	}
}
