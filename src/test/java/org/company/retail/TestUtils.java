package org.company.retail;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class TestUtils {

	public static String getResourceAsString(final String path) throws IOException {

		ClassLoader classLoader = TestUtils.class.getClassLoader();

		return IOUtils.toString(classLoader.getResourceAsStream(path));

	}

}
