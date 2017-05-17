package ru.rik.ripper;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import ru.rik.ripper.domain.Oper;
import ru.rik.ripper.domain.Pair;
import ru.rik.ripper.domain.Pair.Direction;
import ru.rik.ripper.domain.Route;
import ru.rik.ripper.services.Bdpn;
import ru.rik.ripper.services.Helper;
import ru.rik.ripper.services.PairCollector;
import ru.rik.ripper.services.Routes;
import ru.rik.ripper.utils.StringStream;

public class RipDay {
	private final Pattern mob = Pattern.compile("^9\\d{9}$");
	public AtomicInteger inBdpn;
	public AtomicInteger wrongRoute;
	private Routes routes;
	private Bdpn bdpn;
	
	public boolean excludeBdpn = true;
	private int aField = 0;
	private int bField = 1;
	private String delimiter = ";" ;
	private boolean peek = true;
	private boolean parallel = false;
	private Path out;
	
	public RipDay() {
		inBdpn = new AtomicInteger(0); 
		wrongRoute = new AtomicInteger(0);
	}

	public RipDay(Routes routes, Bdpn bdpn) {
		this();
		this.routes = routes;
		this.bdpn = bdpn;
	}
	
	public String normalize(String n) {
		if (n.length() == 11 && (n.startsWith("89") || n.startsWith("79"))) 
			return n.substring(1);
		if (n.length() == 12 && n.startsWith("+"))
			return n.substring(2);
		return n;
	}
	
	
	public boolean isMobile(String n) {
		if (mob.matcher(n).find()) return true;		
		return false;		
	}
	
	
	private boolean isGood(String num) {
		String n = normalize(num);
		if (!isMobile(n)) return false;
		
		if (excludeBdpn && bdpn.in(n)) {
			inBdpn.incrementAndGet();
			return false;
		}
		Route r = routes.get(Long.decode(n));
		if (r.getOper() == Oper.UNKNOWN) {
			wrongRoute.incrementAndGet();
			return false;
		}
		return true;
	}
	
	
	public Map<String, Map<String, int[]>> rip(Stream<String> stream) {
		Map<String, Map<String, int[]>> m = stream 
		.map(s -> s.split(delimiter))
		.filter(s -> s.length > bField)
		.peek(a -> {if (peek) System.out.println(a[aField] + "->" + a[bField]);})
		.map(a -> getPairs(a))
		.flatMap(pair -> Arrays.stream(pair))
		.filter(pair -> pair != null)
		.collect(groupingBy(p -> p.getPartition(),
					groupingBy(pair -> pair.getN1(),
							new PairCollector()))
		);
		return m;
	}
	
	
	private void processFiles(String files) throws IOException {
		List<Path> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = Helper.getDirectoryStream(files)) {
			Iterator<Path> i = ds.iterator();
			while (i.hasNext())
				paths.add(i.next());
		}
		paths.parallelStream().forEach(path -> processFile(path));
	}

	private void processFile(Path path) {
		System.out.print("Processing file " + path.getFileName() + " ");
		try (Stream<String> strm = StringStream.of(path).lines())
		{
			Map<String, Map<String, int[]>>  map = rip(strm);
			System.out.println("saving results for " + path.getFileName());
			map.entrySet().stream()
				.forEach(m -> 
					Helper.save(Helper.getPartitionFile(path, out, m.getKey()), m.getValue()));
			
			System.out.println("bdpn excluded: " 
					+ inBdpn.get() + " wrong route " + wrongRoute.get());
			inBdpn.set(0);
			wrongRoute.set(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private Pair[] getPairs(String[] a) {
		Pair[] pairs = new Pair[2];
		if (isGood(a[aField]))
			pairs[0] = new Pair(normalize(a[aField]), normalize(a[bField]), Direction.FROM_ME);
		if (isGood(a[bField]))
			pairs[1] =new Pair(normalize(a[bField]), normalize(a[aField]), Direction.TO_ME);
		return pairs;
	}
	
	
	public void init() throws IOException {
		Properties conf = Helper.getProperties();
		aField = Integer.parseInt(conf.getProperty("aField", "1"));
		bField = Integer.parseInt(conf.getProperty("bField", "2"));
		delimiter = conf.getProperty("delimiter", ",");
		excludeBdpn = "true".equals(conf.getProperty("excludeBdpn", "true").trim());
		peek = "true".equals(conf.getProperty("peek", "true").trim());
		
		parallel = "true".equals(conf.getProperty("parallel", "true").trim());
		out = Paths.get(conf.getProperty("out", 
				System.getProperty("user.dir") + "/out"));
		
		System.out.println("parameters: a=" + aField + " b=" + bField 
				+ " delim=" + delimiter + " excludeBdpn=" + excludeBdpn + " peek=" + peek + " parallel=" + parallel);
		String routes_file = conf.getProperty("routes_file", 
							"Numbering_plan_201702050000_1174.csv");
		routes = new Routes();
		System.out.println("routes_file: " + routes_file);
		int n =routes.load(Paths.get(routes_file));
		System.out.println("loaded " + n);
		
		String bdpn_file = conf.getProperty("bdpn_file", "eee");
		bdpn = new Bdpn();
		n = bdpn.load(Paths.get(bdpn_file));
		System.out.println("loaded bdpn: " + n);
	}
	
	
	public static void main(String[] args) throws Exception {
		String mask = args[0];
		System.out.println("files " + mask);
		if (mask == null) {
			System.err.println("File mask is needed ");
			return;
		}
		
		RipDay rd = new RipDay();
		rd.init();		
		rd.processFiles(mask);
	}


	

}
