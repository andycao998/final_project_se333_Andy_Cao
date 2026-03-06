package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private PetTypeRepository petTypeRepository;

	@InjectMocks
	private PetController petController;

	private Owner owner;

	private Pet pet;

	private PetType petType;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("John");
		owner.setLastName("Doe");

		petType = new PetType();
		petType.setName("Dog");

		pet = new Pet();
		pet.setId(1);
		pet.setName("Buddy");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(petType);
	}

	@Test
	void testPopulatePetTypes() {
		List<PetType> petTypes = new ArrayList<>();
		petTypes.add(petType);
		when(petTypeRepository.findPetTypes()).thenReturn(petTypes);

		Collection<PetType> result = petController.populatePetTypes();

		assertThat(result).isEqualTo(petTypes);
		verify(petTypeRepository).findPetTypes();
	}

	@Test
	void testFindOwner() {
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

		Owner result = petController.findOwner(1);

		assertThat(result).isEqualTo(owner);
		verify(ownerRepository).findById(1);
	}

	@Test
	void testFindOwnerNotFound() {
		when(ownerRepository.findById(1)).thenReturn(Optional.empty());

		try {
			petController.findOwner(1);
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("Owner not found with id: 1");
		}
	}

	@Test
	void testFindPetNew() {
		Pet result = petController.findPet(1, null);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isNull();
	}

	@Test
	void testFindPetExisting() {
		owner.getPets().add(pet);
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

		Pet result = petController.findPet(1, 1);

		assertThat(result).isEqualTo(pet);
		assertThat(result.getName()).isEqualTo("Buddy");
		verify(ownerRepository).findById(1);
	}

	@Test
	void testFindPetOwnerNotFound() {
		when(ownerRepository.findById(1)).thenReturn(Optional.empty());

		try {
			petController.findPet(1, 1);
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("Owner not found with id: 1");
		}
	}

	@Test
	void testInitCreationForm() {
		ModelMap model = new ModelMap();

		String result = petController.initCreationForm(owner, model);

		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
		assertThat(owner.getPets()).hasSize(1);
	}

	@Test
	void testProcessCreationFormSuccess() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

		String result = petController.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("redirect:/owners/{ownerId}");
		verify(ownerRepository).save(owner);
		verify(redirectAttributes).addFlashAttribute("message", "New Pet has been Added");
	}

	@Test
	void testProcessCreationFormDuplicateName() {
		owner.getPets().add(pet);
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		Pet newPet = new Pet();
		newPet.setName("Buddy");

		String result = petController.processCreationForm(owner, newPet, bindingResult, redirectAttributes);

		verify(bindingResult).rejectValue("name", "duplicate", "already exists");
		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
	}

	@Test
	void testProcessCreationFormFutureBirthDate() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		pet.setBirthDate(LocalDate.now().plusDays(1));
		when(bindingResult.hasErrors()).thenReturn(true);

		String result = petController.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		verify(bindingResult).rejectValue("birthDate", "typeMismatch.birthDate");
		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
	}

	@Test
	void testProcessCreationFormHasErrors() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		String result = petController.processCreationForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
		verify(ownerRepository, never()).save(any());
	}

	@Test
	void testInitUpdateForm() {
		String result = petController.initUpdateForm();

		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
	}

	@Test
	void testProcessUpdateFormSuccess() {
		owner.getPets().add(pet);
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

		String result = petController.processUpdateForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("redirect:/owners/{ownerId}");
		verify(ownerRepository).save(owner);
		verify(redirectAttributes).addFlashAttribute("message", "Pet details has been edited");
	}

	@Test
	void testProcessUpdateFormDuplicateName() {
		Pet existingPet = new Pet();
		existingPet.setId(2);
		existingPet.setName("Buddy");
		owner.getPets().add(existingPet);
		owner.getPets().add(pet);

		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		Pet updatedPet = new Pet();
		updatedPet.setId(1);
		updatedPet.setName("Buddy");

		String result = petController.processUpdateForm(owner, updatedPet, bindingResult, redirectAttributes);

		verify(bindingResult).rejectValue("name", "duplicate", "already exists");
		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
	}

	@Test
	void testProcessUpdateFormFutureBirthDate() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		pet.setBirthDate(LocalDate.now().plusDays(1));
		when(bindingResult.hasErrors()).thenReturn(true);

		String result = petController.processUpdateForm(owner, pet, bindingResult, redirectAttributes);

		verify(bindingResult).rejectValue("birthDate", "typeMismatch.birthDate");
		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
	}

	@Test
	void testProcessUpdateFormHasErrors() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		String result = petController.processUpdateForm(owner, pet, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("pets/createOrUpdatePetForm");
		verify(ownerRepository, never()).save(any());
	}

}