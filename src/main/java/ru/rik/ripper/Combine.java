package ru.rik.ripper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import ru.rik.ripper.services.Helper;

public class Combine {
	Path in;
	public Combine() {
	}
	
	public void init( ) throws IOException {
		String mask = "BW-CDR-2016010";
		Properties conf = Helper.getProperties();
		String currentDir = System.getProperty("user.dir");
		Path res = Paths.get(conf.getProperty("res", currentDir + "/res"));
		in = res.getParent().resolve("in");
		System.out.println(in.toString());
		DirectoryStream<Path> stream = Helper.getDirectoryStream(in.resolve("BW-CDR-2016010*").toString());
		for (Path path : stream) 
			System.out.print("Processing file " + path.getFileName() + " ");
	}
	
	public void combine(String files) {
		
	}
	
	public static void main(String[] args) throws IOException {
		Combine c = new Combine();
		c.init();
		
			
	}

}
