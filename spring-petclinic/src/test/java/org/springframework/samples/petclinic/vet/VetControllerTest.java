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
package org.springframework.samples.petclinic.vet;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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

/**
 * Test class for {@link VetController}
 *
 * @author Andy Cao
 */
@ExtendWith(MockitoExtension.class)
class VetControllerTest {

	@InjectMocks
	private VetController controller;

	@Mock
	private VetRepository vets;

	@Mock
	private Model model;

	private Vet vet;

	@BeforeEach
	void setup() {
		vet = new Vet();
		vet.setId(1);
		vet.setFirstName("James");
		vet.setLastName("Carter");
	}

	@Test
	void testShowVetList() {
		Page<Vet> vetPage = new PageImpl<>(List.of(vet));
		given(vets.findAll(PageRequest.of(0, 5))).willReturn(vetPage);

		String result = controller.showVetList(1, model);

		assertThat(result).isEqualTo("vets/vetList");
	}

	@Test
	void testShowResourcesVetList() {
		given(vets.findAll()).willReturn(List.of(vet));

		Vets result = controller.showResourcesVetList();

		assertThat(result).isNotNull();
		assertThat(result.getVetList()).hasSize(1);
		assertThat(result.getVetList().get(0)).isEqualTo(vet);
	}

}