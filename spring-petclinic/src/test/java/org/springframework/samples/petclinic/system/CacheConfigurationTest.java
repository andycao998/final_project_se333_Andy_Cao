package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;

import javax.cache.configuration.Configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;

class CacheConfigurationTest {

	private CacheConfiguration cacheConfiguration = new CacheConfiguration();

	@Test
	void testPetclinicCacheConfigurationCustomizer() {
		JCacheManagerCustomizer customizer = cacheConfiguration.petclinicCacheConfigurationCustomizer();

		assertThat(customizer).isNotNull();
	}

}