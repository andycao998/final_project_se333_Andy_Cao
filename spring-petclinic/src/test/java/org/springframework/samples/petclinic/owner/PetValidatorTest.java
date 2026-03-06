package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

class PetValidatorTest {

	private PetValidator petValidator;

	private Pet pet;

	private Errors errors;

	@BeforeEach
	void setUp() {
		petValidator = new PetValidator();
		pet = new Pet();
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	void testValidateValidPet() {
		pet.setName("Buddy");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.of(2020, 1, 1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void testValidateEmptyName() {
		pet.setName("");
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.of(2020, 1, 1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateNullName() {
		pet.setName(null);
		pet.setType(new PetType());
		pet.setBirthDate(LocalDate.of(2020, 1, 1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateNullTypeForNewPet() {
		pet.setName("Buddy");
		pet.setType(null);
		pet.setBirthDate(LocalDate.of(2020, 1, 1));

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void testValidateNullBirthDate() {
		pet.setName("Buddy");
		pet.setType(new PetType());
		pet.setBirthDate(null);

		petValidator.validate(pet, errors);

		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void testSupportsPetClass() {
		assertThat(petValidator.supports(Pet.class)).isTrue();
	}

	@Test
	void testSupportsOtherClass() {
		assertThat(petValidator.supports(Owner.class)).isFalse();
	}

}