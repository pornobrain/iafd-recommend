package com.iafd.performer.extract;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.iafd.model.RawPerformer;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class PerformerValidatorTest {
	@Test
	public void validateOne() {
		PerformerValidator v = new PerformerValidator(DataSet.TEST_CONTENT);
		assertThat(v.isValid()).isTrue();
	}

	@Test
	public void validateAll() {
		List<Boolean> result = Lists.transform(DataSet.performers, new Function<RawPerformer, Boolean>() {
			@Override
			public Boolean apply(RawPerformer input) {
				return new PerformerValidator(input.getContent()).isValid();
			}
		});
		assertThat(result).containsOnly(true);
	}
}
