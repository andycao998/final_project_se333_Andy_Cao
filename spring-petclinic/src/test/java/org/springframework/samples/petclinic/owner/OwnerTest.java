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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Andy Cao
 */
class OwnerTest {

	@Test
	void shouldReturnAddress() {
		Owner owner = new Owner();
		owner.setAddress("123 Main St");
		assertThat(owner.getAddress()).isEqualTo("123 Main St");
	}

	@Test
	void shouldReturnCity() {
		Owner owner = new Owner();
		owner.setCity("Anytown");
		assertThat(owner.getCity()).isEqualTo("Anytown");
	}

	@Test
	void shouldReturnTelephone() {
		Owner owner = new Owner();
		owner.setTelephone("1234567890");
		assertThat(owner.getTelephone()).isEqualTo("1234567890");
	}

	@Test
	void shouldReturnPets() {
		Owner owner = new Owner();
		List<Pet> pets = owner.getPets();
		assertThat(pets).isNotNull();
		assertThat(pets).isEmpty();
	}

	@Test
	void shouldAddPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getName()).isEqualTo("Buddy");
	}

	@Test
	void shouldNotAddPetIfNotNew() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setId(1);
		owner.addPet(pet);
		assertThat(owner.getPets()).isEmpty();
	}

	@Test
	void shouldGetPetByName() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPet("Buddy")).isEqualTo(pet);
	}

	@Test
	void shouldGetPetByNameIgnoreCase() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Buddy");
		owner.addPet(pet);
		assertThat(owner.getPet("buddy")).isEqualTo(pet);
	}

	@Test
	void shouldReturnNullWhenPetNotFound() {
		Owner owner = new Owner();
		assertThat(owner.getPet("Nonexistent")).isNull();
	}

	@Test
	void shouldGetPetById() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(1);
		assertThat(owner.getPet(1)).isEqualTo(pet);
	}

	@Test
	void shouldReturnNullWhenPetIdNotFound() {
		Owner owner = new Owner();
		assertThat(owner.getPet(999)).isNull();
	}

	@Test
	void shouldReturnNullWhenPetIdDoesNotMatch() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(1);
		assertThat(owner.getPet(2)).isNull();
	}

	@Test
	void shouldAddVisit() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(1);
		Visit visit = new Visit();
		owner.addVisit(1, visit);
		assertThat(pet.getVisits()).contains(visit);
	}

	@Test
	void shouldThrowExceptionWhenPetIdNotFoundForAddVisit() {
		Owner owner = new Owner();
		Visit visit = new Visit();
		assertThatThrownBy(() -> owner.addVisit(1, visit)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Invalid Pet identifier!");
	}

	@Test
	void shouldThrowExceptionWhenVisitIsNull() {
		Owner owner = new Owner();
		assertThatThrownBy(() -> owner.addVisit(1, null)).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Visit must not be null!");
	}

	@Test
	void shouldReturnToString() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Anytown");
		owner.setTelephone("1234567890");
		assertThat(owner.toString()).contains("id = [null]")
			.contains("new = true")
			.contains("lastName = 'Doe'")
			.contains("firstName = 'John'")
			.contains("address = '123 Main St'")
			.contains("city = 'Anytown'")
			.contains("telephone = '1234567890'");
	}

	@Test
	void shouldGetPetIgnoringNewFalse() {
		Owner owner = new Owner();
		Pet newPet = new Pet();
		newPet.setName("NewPet");
		owner.addPet(newPet);
		assertThat(owner.getPet("NewPet", false)).isEqualTo(newPet);
	}

	@Test
	void shouldNotGetPetIgnoringNewTrue() {
		Owner owner = new Owner();
		Pet newPet = new Pet();
		newPet.setName("NewPet");
		owner.addPet(newPet);
		assertThat(owner.getPet("NewPet", true)).isNull();
	}

	@Test
	void shouldHandlePetWithNullName() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName(null);
		owner.addPet(pet);
		assertThat(owner.getPet("SomeName")).isNull();
	}

}