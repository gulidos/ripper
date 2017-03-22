package ru.rik.ripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import ru.rik.ripper.services.Helper;
import ru.rik.ripper.services.MergeCollector;

public class Merge {
	private Path out;
	private Path res;
	
	public Merge() {	
		
	}
	
	public void init( ) throws IOException {
		Properties conf = Helper.getProperties();
		String currentDir = System.getProperty("user.dir");
		res = Paths.get(
				conf.getProperty("res", currentDir + "/res"));
		out = Paths.get(
				conf.getProperty("out", currentDir + "/out"));
	}
	
	

	public void merge() throws IOException {
		IntStream.rangeClosed(0, 9).parallel().forEach( i -> 
//		for ( i = 0; i <= 9; i++) 
		{
			Path partition = res.resolve(Paths.get("part_" + i + ".gz"));
			DirectoryStream<Path> ds = null;
			try {
				ds = Helper.getDirectoryStream(out.toString() + "/*_" + i + ".gz");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<String> result = null;
			try (GZIPInputStream zisp = new GZIPInputStream(Files.newInputStream(partition));
					BufferedReader brPartition = new BufferedReader(new InputStreamReader(zisp));
					Stream<String> partitionStream = brPartition.lines()) 
			{
				for (Path newFile : ds) {
					System.out.print("  " + newFile.getFileName().toString());
					try (GZIPInputStream zisNew = new GZIPInputStream(Files.newInputStream(newFile));
							BufferedReader brNew = new BufferedReader(new InputStreamReader(zisNew));
							Stream<String> newStream = brNew.lines())
					{
						if (result == null) 
							result = Stream.concat(partitionStream, newStream)
								.collect(Collectors.toList());
						else
							result = Stream.concat(result.stream(), newStream)
								.collect(Collectors.toList());	
					}
				}
				
				System.out.println(" partition: " + 
						partition.getFileName().toString() + " count: " 
						+ result.size());
				Map<String, int[]> map = result.stream()
					.map(s -> s.split(","))
					.filter(a -> a.length ==5 && a[0].length() == 10)
					.filter(a -> a[0].substring(9).equals(String.valueOf(i)))
					.collect(Collectors.groupingBy(a -> a[0], new MergeCollector()));
				
				Path newPartition = Paths.get(partition.toString() + ".new");
				Helper.save(newPartition, map);
				Files.move(newPartition, partition, StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		);
	}

	
	
	public static void main(String[] args) throws IOException {
		Merge m = new Merge();
		m.init();
		m.merge();
	}

}
