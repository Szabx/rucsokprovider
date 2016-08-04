package com.rucsok.test.rucsok;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.is;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rucsok.rucsok.domain.Rucsok;
import com.rucsok.rucsok.service.RucsokCrawlerService;
import com.rucsok.rucsok.view.controller.CrawlRucsokController;
import com.rucsok.rucsok.view.model.RucsokCheckRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
@WebIntegrationTest
public class CrawlRucsokControllerIntegrationTest {

	private static final String MOCK_TITLE = "rucsokTitle";

	private static final String MOCK_LINK = "rucsokLink";

	private static final String MOCK_IMAGE = "mockImage";

	private static final String TEST_URL = "http://test.test";

	@Autowired
	private CrawlRucsokController crawlRucsokController;

	private RucsokCrawlerService rucsokService;

	private MockMvc mockMvc;

	private ObjectMapper mapper;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(crawlRucsokController).build();
		rucsokService = Mockito.mock(RucsokCrawlerService.class);
		crawlRucsokController.setRucsokService(rucsokService);
		mapper = new ObjectMapper();
	}

	@Test
	public void crawlerShouldReturnCorrectCrawledObject() throws Exception {

		// Given

		RucsokCheckRequest request = new RucsokCheckRequest();
		request.setUrl(TEST_URL);
		Rucsok rucsok = Mockito.mock(Rucsok.class);

		// When

		when(rucsok.getImageUrl()).thenReturn(MOCK_IMAGE);
		when(rucsok.getLink()).thenReturn(MOCK_LINK);
		when(rucsok.getTitle()).thenReturn(MOCK_TITLE);
		when(rucsokService.crawl(TEST_URL)).thenReturn(rucsok);

		mockMvc.perform(post(CrawlRucsokController.REQUEST_MAPPING).contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk()).andDo(print())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.title", is(MOCK_TITLE))).andExpect(jsonPath("$.link", is(MOCK_LINK)))
				.andExpect(jsonPath("$.image", is(MOCK_IMAGE)));

		// Then

		verify(rucsokService).crawl(TEST_URL);

	}

}