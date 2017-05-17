package ru.rik.ripper.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ru.rik.ripper.utils.StringStream;

public class Bdpn {
	private Set<String > set;
	
	public Bdpn() {
		set = new HashSet<>();
	}
	
	public boolean in(String n) {
		return set.contains(n);
	}
	
	
	public int load(Path path) throws IOException {
		try (Stream<String> strm = StringStream.of(path).lines())
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
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(zip, 1024));) 
		{
			oos.writeObject(set);
		} 
	}
	
	
	@SuppressWarnings("unchecked")
	public void loadBdpnSer(Path file) throws IOException, ClassNotFoundException {
		try (  InputStream fis = Files.newInputStream(file);
				GZIPInputStream zip = new GZIPInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(zip, 1024));) 
		{
			Set<String > newBdpnMap = new HashSet<String>();
			newBdpnMap = (Set<String >) ois.readObject();
			set = newBdpnMap;
		} 
	}
}
