package ru.rik.ripper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import ru.rik.ripper.RipDay;
import ru.rik.ripper.services.Helper;

public class PathTests {

	@Test
	public void test() {
//		Path p = Paths.get("/Users/gsv/pgadmin.log");
//		Assert.assertEquals(Helper.getFileExtension(p), "log");
//		System.out.println(p.getFileName());
//		System.out.println(p.toString());
//		System.out.println(p.getNameCount());
//		System.out.println(p.getRoot());
	}
	
	@Test
	public void partitionTest() {
		Path p = Paths.get("mvnw");
		String workingDir = "/Users/gsv/Downloads";
		Path out = Paths.get(workingDir+"/demo");
		Path dst = Helper.getPartitionFile(p, out, "0");
		Assert.assertEquals(dst, Paths.get("/Users/gsv/Downloads/demo/mvnw_0"));
		
		out = Paths.get(workingDir+"/demo");
		dst = Helper.getPartitionFile(p, out, "0");
		Assert.assertEquals(dst, Paths.get("/Users/gsv/Downloads/demo/mvnw_0"));
	}
	
	@Test
	public void directoryTest() {
		Path p = Paths.get("/Users/gsv/Downloads");
		
		Path pFile = p.resolve(Paths.get("TeamViewer.dmg"));
		System.out.println(pFile.toString());
	}
	
	@Test
	public void normalizeTest() {
		RipDay r = new RipDay();
		Assert.assertEquals(r.normalize("79123456789"), "9123456789");
	}
	
	@Test
	public void lastChar() {
		String a = "9123456789";
		Assert.assertEquals(a.substring(9), "9");
		int i = 9;
		Assert.assertEquals(a.substring(9), String.valueOf(i));
	}
	
}

