package ru.rik.ripper.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class StringStream implements AutoCloseable {
	
	private final InputStream is;
	private final GZIPInputStream zisp;
	private final InputStreamReader isr;
	private final BufferedReader br;
	private final Stream<String> stream;

	
	private StringStream(Path source, InputStream is, GZIPInputStream zisp, InputStreamReader isr,
			BufferedReader br, Stream<String> stream) {
		super();
		this.is = is;
		this.zisp = zisp;
		this.isr = isr;
		this.br = br;
		this.stream = stream;
	}

	public static StringStream of(Path source) throws IOException {
			InputStream is = Files.newInputStream(source);
			GZIPInputStream zisp = new GZIPInputStream(is);
			InputStreamReader isr = new InputStreamReader(zisp);
			BufferedReader br = new BufferedReader(isr);
			Stream<String> stream = br.lines();
			return new StringStream(source, is, zisp, isr,br, stream);
	}

	@Override
	public void close() throws Exception {
		if (stream != null) stream.close();
		br.close();
		isr.close();
		zisp.close();
		is.close();
	}
	
	public Stream<String> lines() {
		return stream;
	}

}
