package ru.rik.ripper.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import ru.rik.ripper.domain.Route;

import java.util.TreeMap;




public class Routes {
	private TreeMap<Long, Route> map;

	public Routes() {	
		map = new TreeMap<>();
	}
	
	
	public int load(Path file) throws IOException {
		TreeMap<Long, Route> newmap = new TreeMap<>();
		try (BufferedReader br = Files.newBufferedReader(file);
				Stream<String> strm = br.lines())
		{
			strm.skip(1)
			.map(line -> line.split(","))
			.filter(a -> a.length > 2)
			.map(a -> new Route(a))
			.forEach(r -> newmap.put(r.getFromd(), r));
		} catch (IOException e) {
			e.printStackTrace();
		}
		map = newmap;
		return map.size();
	}
	
	
    public Route get(Long num) {
        Entry<Long, Route> closestEntry = map.floorEntry(num);
        if (closestEntry != null) {
        	Route r = closestEntry.getValue();
            if (r != null && r.isIn(num)) 
                return r;
        }
        return Route.NULL_ROUTE;
    }
	

	public Map<Long, Route> getAll() {
		return map;
	}

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("/Users/gsv/Downloads/Numbering_plan_201702050000_1174.csv");
		Routes rs = new Routes();
		int n = rs.load(path);
		System.out.println("loaded " + n);
		
	}
}
