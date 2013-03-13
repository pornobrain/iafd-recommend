package com.iafd.performer.transform;

import com.iafd.model.RawPerformer;
import com.iafd.performer.PerformerDataSet;
import org.jsoup.Jsoup;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import static com.google.common.io.CharStreams.readLines;
import static org.apache.commons.lang3.text.WordUtils.wrap;

public class TwoPrinterTest {
	@Ignore
	@Test
	public void test() throws IOException {
		int WRAP_LENGTH = 90;
		String aDoc = wrap(PerformerDataSet.DOCUMENT.toString(), WRAP_LENGTH, null, true);
		List<String> aLines = readLines(new StringReader(aDoc));
		Iterator<String> ai = aLines.iterator();
		RawPerformer otherPerformer = PerformerDataSet.DATASET.get(1);
		String bDoc = wrap(Jsoup.parse(otherPerformer.getContent(), otherPerformer.getUrl()).toString(), WRAP_LENGTH, null, true);
		List<String> bLines = readLines(new StringReader(bDoc));
		Iterator<String> bi = bLines.iterator();
		String FORMAT = String.format("%%-%ds|%%-%1$ds", WRAP_LENGTH);
		System.out.println(FORMAT);
		while(ai.hasNext() || bi.hasNext()) {
			String a = ai.hasNext()?ai.next():"";
			String b = bi.hasNext()?bi.next():"";
			System.out.println(String.format(FORMAT, a, b));
		}
	}
}
