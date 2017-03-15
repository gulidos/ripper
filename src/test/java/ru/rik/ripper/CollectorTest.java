package ru.rik.ripper;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

import ru.rik.ripper.RipDay;
import ru.rik.ripper.services.Bdpn;
import ru.rik.ripper.services.Routes;

public class CollectorTest {

	@Test
	public void test() throws IOException {
		String[] arr = new String[]{
				"9266818268; 9031946499",
				"9140213198; 9147899891",
				"9104763574; 9143480203",
				"9853836740; 9141807044",
				"9260377333; 9119866414",
				"9284411227; 9182951246",
				"9140201762; 9147899819",
				"9141771055; 9141807570",
				"9258499840; 9856873242",
				"9164410051; 9107940892",
				"9885688006; 9149599860",
				"9167949724; 9143480879",
				"9159976372; 9140800048",
				"9884770003; 9298383023",
				"9164829828; 9868476289",
				"9266628077; 9661077824",
				"9253160007; 9143480339",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9172688225; 9107940892",
				"9164829828; 9868476289",
				"9103679430; 9147499900",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9146539139; 9143480235",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9160880639; 9147899885",
				"9164829828; 9868476289",
				"9164829828; 9868476289",
				"9164829828; 9868476289"
		};
		Routes r = new Routes();
		r.load(Paths.get("/Users/gsv/Downloads/Numbering_plan_201702050000_1174.csv"));
		RipDay rd = new RipDay(r, new Bdpn());
		Map<String, Map<String, int[]>> map = rd.rip(Arrays.stream(arr));
		
		map.entrySet().stream().forEach(e -> {
			System.out.println("file: " +  e.getKey());
			Map<String, int[]> m1 = e.getValue();
			for (String k: m1.keySet()) {
				System.out.print(k + " --> ");
				for (int i: m1.get(k)) 
					System.out.print(i + " ");
				System.out.println(" ");
			}	
			
		});

	}

}
