package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void create_shouldCreateFilmWithValidData() throws Exception {
		Film film = new Film();
		film.setName("Film Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(film)))
				.andExpect(status().isOk())
				.andExpect((ResultMatcher) jsonPath("$.id").exists())
				.andExpect((ResultMatcher) jsonPath("$.name").value("Film Name"))
				.andExpect((ResultMatcher) jsonPath("$.description").value("Description"))
				.andExpect((ResultMatcher) jsonPath("$.releaseDate").value("2000-01-01"))
				.andExpect((ResultMatcher) jsonPath("$.duration").value(120));
	}

	@Test
	void update_shouldUpdateFilmWithValidData() throws Exception {
		// Сначала создаем фильм
		Film film = new Film();
		film.setName("Film Name");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		String response = mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(film)))
				.andReturn().getResponse().getContentAsString();


		// Затем обновляем фильм
		film.setName("Updated Film Name");
		film.setDescription("Updated Description");

		mockMvc.perform(put("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(film)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(film.getId()))
				.andExpect(jsonPath("$.name").value("Updated Film Name"))
				.andExpect(jsonPath("$.description").value("Updated Description"))
				.andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
				.andExpect(jsonPath("$.duration").value(120));
	}

	private static String asJsonString(final Object obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}