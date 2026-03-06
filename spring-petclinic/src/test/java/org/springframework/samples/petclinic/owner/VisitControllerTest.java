import org.springframework.web.bind.WebDataBinder;

/**
 * Test class for {@link VisitController}
 *
 * @author Andy Cao
 */
@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

	@Mock
	private OwnerRepository ownerRepository;

	@InjectMocks
	private VisitController visitController;

	private Owner owner;

	private Pet pet;

	private Visit visit;

	@BeforeEach
	void setUp() {
		owner = new Owner();
		owner.setId(1);
		owner.setFirstName("John");
		owner.setLastName("Doe");

		pet = new Pet();
		pet.setId(1);
		pet.setName("Buddy");

		visit = new Visit();
		visit.setDescription("Checkup");

		owner.getPets().add(pet);
	}

	@Test
	void testLoadPetWithVisit() {
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

		Map<String, Object> model = mock(Map.class);

		Visit result = visitController.loadPetWithVisit(1, 1, model);

		assertThat(result).isNotNull();
		assertThat(pet.getVisits()).contains(result);
		verify(model).put("pet", pet);
		verify(model).put("owner", owner);
		verify(ownerRepository).findById(1);
	}

	@Test
	void testLoadPetWithVisitOwnerNotFound() {
		when(ownerRepository.findById(1)).thenReturn(Optional.empty());

		Map<String, Object> model = mock(Map.class);

		try {
			visitController.loadPetWithVisit(1, 1, model);
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("Owner not found with id: 1");
		}
	}

	@Test
	void testLoadPetWithVisitPetNotFound() {
		when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

		Map<String, Object> model = mock(Map.class);

		try {
			visitController.loadPetWithVisit(1, 2, model);
		}
		catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("Pet with id 2 not found for owner with id 1");
		}
	}

	@Test
	void testInitNewVisitForm() {
		String result = visitController.initNewVisitForm();

		assertThat(result).isEqualTo("pets/createOrUpdateVisitForm");
	}

	@Test
	void testProcessNewVisitFormSuccess() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(false);

		String result = visitController.processNewVisitForm(owner, 1, visit, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("redirect:/owners/{ownerId}");
		verify(ownerRepository).save(owner);
		verify(redirectAttributes).addFlashAttribute("message", "Your visit has been booked");
	}

	@Test
	void testProcessNewVisitFormHasErrors() {
		BindingResult bindingResult = mock(BindingResult.class);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		when(bindingResult.hasErrors()).thenReturn(true);

		String result = visitController.processNewVisitForm(owner, 1, visit, bindingResult, redirectAttributes);

		assertThat(result).isEqualTo("pets/createOrUpdateVisitForm");
		verify(ownerRepository, org.mockito.Mockito.never()).save(owner);
	}

	@Test
	void testSetAllowedFields() {
		WebDataBinder dataBinder = mock(WebDataBinder.class);

		visitController.setAllowedFields(dataBinder);

		verify(dataBinder).setDisallowedFields("id");
	}

}