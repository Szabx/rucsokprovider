package com.rucsok.test.rucsok;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rucsok.rucsok.repository.dao.RucsokDao;
import com.rucsok.rucsok.repository.domain.RucsokEntity;
import com.rucsok.rucsok.view.controller.PostRucsokController;
import com.rucsok.rucsok.view.model.RucsokInsertRequest;
import com.rucsok.rucsok.view.model.RucsokPost;
import com.rucsok.test.config.RepositoryConfig;
import com.rucsok.test.config.TestConfig;
import com.rucsok.user.repository.domain.UserEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RepositoryConfig.class, TestConfig.class })
@WebIntegrationTest
public class PostRucsokControllerIntegrationTest {

	private static final String TEST_USER_NAME = "rucsok";

	private static final String TEST_TITLE = "epic rucsok";

	private static final String TEST_IMAGE = "http://rucsok.com/rucsok.jpg";

	private static final String TEST_URL = "http://rucsok.com/epic-rucsok";

	private static final String TEST_UNIQUE_URL = "http://rucsok.com/epic-url-rucsok";
	
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private RucsokDao rucsokDao;

	private MockMvc mockMvc;

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		
		mapper = new ObjectMapper();
	}
	
	@Test
	public void postShouldReturnFoundWhenUserNotLoggedIn() throws Exception {

		// Given

		// When

		// Then

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(""))
				.andExpect(status()
				.isFound());

	}

	@Test
	@WithUserDetails(TEST_USER_NAME)
	public void postShouldReturnBadGatewayWhenRequestIsNull() throws Exception {

		// Given

		// When

		// Then

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(null)))
				.andExpect(status()
				.isBadRequest());

	}

	@Test
	@WithUserDetails(TEST_USER_NAME)
	public void postShouldReturnBadGatewayWhenLinkIsNull() throws Exception {

		// Given

		RucsokPost rucsok = createRucsokPostHelper();
		rucsok.setLink(null);
		RucsokInsertRequest request = createRucsokInsertRequestHelper(rucsok);

		// When

		// Then

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest());

	}

	@Test
	@WithUserDetails(TEST_USER_NAME)
	public void postShouldReturnBadGatewayWhenImageIsNull() throws Exception {

		// Given

		RucsokPost rucsok = createRucsokPostHelper();
		rucsok.setImageUrl(null);
		rucsok.setLink("http://asdasdasd.asd");
		RucsokInsertRequest request = createRucsokInsertRequestHelper(rucsok);

		// When

		// Then

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status()
				.isBadRequest());

	}

	@Test
	@WithUserDetails(TEST_USER_NAME)
	@Transactional
	public void postShouldCreateNewEntity() throws Exception {

		// Given

		RucsokPost rucsok = createRucsokPostHelper();
		RucsokInsertRequest request = createRucsokInsertRequestHelper(rucsok);

		// When

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status()
				.isCreated());

		RucsokEntity rucsokEntity = rucsokDao.findByLink(TEST_URL);
		UserEntity userEntity = rucsokEntity.getUser();

		// Then

		Assert.assertNotNull("Entity should'nt be null", rucsokEntity);
		Assert.assertEquals("Link should match", rucsokEntity.getLink(), rucsok.getLink());
		Assert.assertEquals("Image should match", rucsokEntity.getImageUrl(), rucsok.getImageUrl());
		Assert.assertEquals("Title should match", rucsokEntity.getTitle(), rucsok.getTitle());
		Assert.assertNotNull("User shouldn't be null", userEntity);
		Assert.assertEquals("Username should match", userEntity.getName(), TEST_USER_NAME);
	}

	@Test
	@WithUserDetails(TEST_USER_NAME)
	public void postShouldNotSaveTheSameUrlAgain() throws Exception {

		// Given

		RucsokPost rucsok = createRucsokPostHelper();
		rucsok.setLink(TEST_UNIQUE_URL);
		RucsokInsertRequest request = createRucsokInsertRequestHelper(rucsok);

		// When

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status().isCreated());

		mockMvc.perform(post(PostRucsokController.REQUEST_MAPPING)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
				.andExpect(status()
				.isConflict());

		RucsokEntity entity = rucsokDao.findByLink(TEST_UNIQUE_URL);

		// Then

		Assert.assertNotNull("Entity should'nt be null", entity);
	}

	private RucsokInsertRequest createRucsokInsertRequestHelper(RucsokPost rucsok) {
		RucsokInsertRequest request = new RucsokInsertRequest();
		request.setRucsok(rucsok);
		return request;
	}

	private RucsokPost createRucsokPostHelper() {
		String link = TEST_URL;
		String image = TEST_IMAGE;
		String title = TEST_TITLE;

		RucsokPost rucsok = new RucsokPost();
		rucsok.setImageUrl(image);
		rucsok.setLink(link);
		rucsok.setTitle(title);
		return rucsok;
	}
}
