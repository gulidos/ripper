package ru.rik.ripper;

import java.io.IOException;
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

import ru.rik.ripper.services.Helper;
import ru.rik.ripper.services.MergeCollector;
import ru.rik.ripper.utils.StringStream;

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
		IntStream.rangeClosed(0, 9)
			.parallel()
			.forEach( i -> mergePart(i));
	}
	
	private void mergePart(int i) {
		try {
			tryToMergePart(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void tryToMergePart(int i) throws IOException {
		Path partition = res.resolve(Paths.get("part_" + i + ".gz"));
		String fileMask = out.toString() + "/*_" + i + ".gz";
		DirectoryStream<Path>  ds = Helper.getDirectoryStream(fileMask);
		
		List<String> result = null;
		try (Stream<String> partitionStream = StringStream.of(partition).lines()) 
		{
			for (Path newFile : ds) {
				System.out.print("  " + newFile.getFileName().toString());
				try (Stream<String> newStream = StringStream.of(newFile).lines())
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
					partition.getFileName().toString() + " count: " + result.size());
			Map<String, int[]> map = getNumbersAsMap(i, result);
			Path newPartition = Paths.get(partition.toString() + ".new");
			Helper.save(newPartition, map);
			Files.move(newPartition, partition, StandardCopyOption.REPLACE_EXISTING);
		} 
	}

	private Map<String, int[]> getNumbersAsMap(int i, List<String> result) {
		Map<String, int[]> map = result.stream()
			.map(s -> s.split(","))
			.filter(a -> a.length ==5 && a[0].length() == 10)
			.filter(a -> a[0].substring(9).equals(String.valueOf(i)))
			.collect(Collectors.groupingBy(a -> a[0], new MergeCollector()));
		return map;
	}

	
	public static void main(String[] args) throws IOException {
		Merge m = new Merge();
		m.init();
		m.merge();
	}

}
