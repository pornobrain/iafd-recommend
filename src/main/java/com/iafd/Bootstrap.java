package com.iafd;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

public class Bootstrap {
	Bootstrap() {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}
}
