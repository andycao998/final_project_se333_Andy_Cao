package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetTypeFormatterTest {

	@Mock
	private PetTypeRepository petTypeRepository;

	@InjectMocks
	private PetTypeFormatter petTypeFormatter;

	private PetType petType;

	@BeforeEach
	void setUp() {
		petType = new PetType();
		petType.setName("Dog");
	}

	@Test
	void testPrint() {
		String result = petTypeFormatter.print(petType, Locale.ENGLISH);

		assertThat(result).isEqualTo("Dog");
	}

	@Test
	void testPrintNullName() {
		petType.setName(null);

		String result = petTypeFormatter.print(petType, Locale.ENGLISH);

		assertThat(result).isEqualTo("<null>");
	}

	@Test
	void testParse() throws ParseException {
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(petType);
		when(petTypeRepository.findPetTypes()).thenReturn(petTypes);

		PetType result = petTypeFormatter.parse("Dog", Locale.ENGLISH);

		assertThat(result).isEqualTo(petType);
	}

	@Test
	void testParseNotFound() {
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(petType);
		when(petTypeRepository.findPetTypes()).thenReturn(petTypes);

		assertThatThrownBy(() -> petTypeFormatter.parse("Cat", Locale.ENGLISH)).isInstanceOf(ParseException.class)
			.hasMessage("type not found: Cat");
	}

}