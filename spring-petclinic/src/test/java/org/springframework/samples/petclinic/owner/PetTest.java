/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;

import org.junit.jupiter.api.Test;

/**
 * @author Andy Cao
 */
class PetTest {

	@Test
	void shouldReturnBirthDate() {
		Pet pet = new Pet();
		LocalDate birthDate = LocalDate.of(2020, 1, 1);
		pet.setBirthDate(birthDate);
		assertThat(pet.getBirthDate()).isEqualTo(birthDate);
	}

	@Test
	void shouldReturnType() {
		Pet pet = new Pet();
		PetType type = new PetType();
		pet.setType(type);
		assertThat(pet.getType()).isEqualTo(type);
	}

	@Test
	void shouldReturnVisits() {
		Pet pet = new Pet();
		Collection<Visit> visits = pet.getVisits();
		assertThat(visits).isNotNull();
		assertThat(visits).isEmpty();
	}

	@Test
	void shouldAddVisit() {
		Pet pet = new Pet();
		Visit visit = new Visit();
		pet.addVisit(visit);
		assertThat(pet.getVisits()).hasSize(1);
		assertThat(pet.getVisits()).contains(visit);
	}

}