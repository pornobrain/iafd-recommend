package com.iafd;

import com.github.jmkgreen.morphia.logging.MorphiaLoggerFactory;
import com.github.jmkgreen.morphia.logging.slf4j.SLF4JLogrImplFactory;

public class Bootstrap {
	Bootstrap() {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}
}
