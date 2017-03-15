package ru.rik.ripper.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import ru.rik.ripper.domain.Pair;
import ru.rik.ripper.domain.Pair.Direction;

public class PairCollector implements Collector<Pair, Map<Direction, List<String>>, int[]> {

	public PairCollector() {	}

	@Override
	public Supplier<Map<Direction, List<String>>> supplier() {
		return () ->{ 
			Map<Direction, List<String>> map = new HashMap<>();
			map.put(Direction.FROM_ME, new ArrayList<String>());
			map.put(Direction.TO_ME, new ArrayList<String>());
			return map;
		};
	}

	@Override
	public BiConsumer<Map<Direction, List<String>>, Pair> accumulator() {
		return (Map<Direction, List<String>> map, Pair pair) -> {
			map.get(pair.getD()).add(pair.getN2());
		};
	}

	@Override
	public BinaryOperator<Map<Direction, List<String>>> combiner() {
		return (Map<Direction, List<String>> m1, Map<Direction, List<String>> m2) -> {
			m1.get(Direction.FROM_ME).addAll(m2.get(Direction.FROM_ME));
			m1.get(Direction.TO_ME).addAll(m2.get(Direction.TO_ME));
			return m1;
		};
	}

	@Override
	public Function<Map<Direction, List<String>>, int[]> finisher() {
		return (Map<Direction, List<String>> map) -> {
			int countFromMe = map.get(Direction.FROM_ME).size();
			int distFromMe = new HashSet<String>(map.get(Direction.FROM_ME)).size();
			int countToMe = map.get(Direction.TO_ME).size();
			int distToMe = new HashSet<String>(map.get(Direction.TO_ME)).size();
			return new int[]{countFromMe, distFromMe, countToMe, distToMe};
		};
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
	}
}
