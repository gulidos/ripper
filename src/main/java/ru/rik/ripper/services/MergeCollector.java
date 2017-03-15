package ru.rik.ripper.services;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MergeCollector implements Collector<String[], int[], int[]> {

	public MergeCollector() {	}

	@Override
	public Supplier<int[]> supplier() {
		return () ->{
			return  new int[] {0, 0, 0, 0};
		};
	}

	@Override
	public BiConsumer<int[], String[]> accumulator() {
		return (int[] result, String[] a) -> {
			try {
				for (int i = 1; i < a.length; i++) { // the first (zero) element is the number. Don't use it
					int	n = Integer.parseInt(a[i]);
					if (n > result[i-1])
						result[i-1] = n;
				}
			} catch (NumberFormatException e) {

			}
		};
		
	}

	@Override
	public BinaryOperator<int[]> combiner() {
		return (int[] a1, int[] a2) -> {
			for (int i = 0; i < a1.length; i++) {
				if (a2[i] > a1[i])
					a1[i] = a2[i];
			}
			return a1;
		};
	}

	@Override
	public Function<int[], int[]> finisher() {
		return Function.identity();
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
	}

}
