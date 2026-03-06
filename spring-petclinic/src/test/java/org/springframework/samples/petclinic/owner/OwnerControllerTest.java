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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 * Test class for {@link OwnerController}
 *
 * @author Andy Cao
 */
@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

	@InjectMocks
	private OwnerController controller;

	@Mock
	private OwnerRepository owners;

	@Mock
	private Model mockModel;

	private Owner owner;

	@BeforeEach
	void setup() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("Joe");
		owner.setLastName("Bloggs");
		owner.setAddress("123 Caramel Street");
		owner.setCity("London");
		owner.setTelephone("01316761638");
	}

	@Test
	void testInitCreationForm() {
		String result = controller.initCreationForm();
		assertThat(result).isEqualTo("owners/createOrUpdateOwnerForm");
	}

	@Test
	void testProcessCreationFormSuccess() {
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String response = controller.processCreationForm(owner, result, redirectAttributes);

		assertThat(response).isEqualTo("redirect:/owners/" + owner.getId());
	}

	@Test
	void testProcessCreationFormHasErrors() {
		owner.setTelephone("invalid");
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");
		result.reject("telephone", "invalid");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String response = controller.processCreationForm(owner, result, redirectAttributes);

		assertThat(response).isEqualTo("owners/createOrUpdateOwnerForm");
	}

	@Test
	void testInitFindForm() {
		String result = controller.initFindForm();
		assertThat(result).isEqualTo("owners/findOwners");
	}

	@Test
	void testProcessFindFormSuccess() {
		Owner second = new Owner();
		second.setId(2);
		second.setFirstName("Jane");
		second.setLastName("Doe");
		Page<Owner> ownersPage = new PageImpl<>(List.of(owner, second));
		given(owners.findByLastNameStartingWith(anyString(), any(PageRequest.class))).willReturn(ownersPage);

		BindingResult result = new BeanPropertyBindingResult(owner, "owner");

		String response = controller.processFindForm(1, owner, result, mockModel);

		assertThat(response).isEqualTo("owners/ownersList");
	}

	@Test
	void testProcessFindFormNotFound() {
		Page<Owner> ownersPage = new PageImpl<>(List.of());
		given(owners.findByLastNameStartingWith(anyString(), any(PageRequest.class))).willReturn(ownersPage);

		owner.setLastName("Unknown");
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");

		String response = controller.processFindForm(1, owner, result, mockModel);

		assertThat(response).isEqualTo("owners/findOwners");
		assertThat(result.hasErrors()).isTrue();
	}

	@Test
	void testProcessFindFormOneResult() {
		Page<Owner> ownersPage = new PageImpl<>(List.of(owner));
		given(owners.findByLastNameStartingWith(anyString(), any(PageRequest.class))).willReturn(ownersPage);

		owner.setLastName("Bloggs");
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");

		String response = controller.processFindForm(1, owner, result, mockModel);

		assertThat(response).isEqualTo("redirect:/owners/" + owner.getId());
	}

	@Test
	void testInitUpdateOwnerForm() {
		String result = controller.initUpdateOwnerForm();

		assertThat(result).isEqualTo("owners/createOrUpdateOwnerForm");
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() {
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String response = controller.processUpdateOwnerForm(owner, result, 1, redirectAttributes);

		// method returns a URI template; Spring would expand it during dispatch
		assertThat(response).isEqualTo("redirect:/owners/{ownerId}");
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() {
		owner.setTelephone("invalid");
		BindingResult result = new BeanPropertyBindingResult(owner, "owner");
		result.reject("telephone", "invalid");
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		String response = controller.processUpdateOwnerForm(owner, result, 1, redirectAttributes);

		assertThat(response).isEqualTo("owners/createOrUpdateOwnerForm");
	}

	@Test
	void testShowOwner() {
		given(owners.findById(1)).willReturn(Optional.of(owner));

		ModelAndView mav = controller.showOwner(1);

		assertThat(mav.getViewName()).isEqualTo("owners/ownerDetails");
		assertThat(mav.getModel().get("owner")).isEqualTo(owner);
	}

}
