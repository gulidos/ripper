package ru.rik.ripper.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.rik.ripper.domain.Route;
import ru.rik.ripper.utils.StringStream;

public class Routes {
	private TreeMap<Long, Route> map;

	public Routes() {	
		map = new TreeMap<>();
	}
	
	
	public int load(Path file) throws IOException {
		int res = 0;
		try (Stream<String> strm = StringStream.of(file).lines())
		{
			TreeMap<Long, Route> newmap =
			strm.skip(1)
			.map(line -> line.split(","))
			.filter(a -> a.length > 2)
			.map(a -> Route.parse(a))
			.filter(optRoute -> optRoute.isPresent())
			.map(optR -> optR.get())
			.collect(Collectors.toMap(r -> r.getFromd(), Function.identity(), 
					(v1,v2) -> { throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));},
					TreeMap::new));
			map = newmap;
			res = map.size();
		} 
		return res;
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
}
