package com.yolo.customer;

import org.junit.jupiter.api.*;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;
import util.SecurityTestUtil;

import java.util.Map;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"api.security.max_limit=3","api.security.title_length=64","api.security.description_length=128"})
class IdeaTests {

    @Autowired
    private MockMvc mockMvc;

//    @BeforeEach
//    public void setUp() {
//        SecurityTestUtil.setJwtAuthenticationToken("admin",
//                Set.of("VIEW_ALL_IDEAS"),
//                Map.of("preferred_username", "admin")
//        );
//    }

    @Order(1)
    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_ALL_IDEAS"})
    public void testGetIdeas() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/ideas")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isMap())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()", Matchers.greaterThan(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.ideas.content.length()").value(1));
    }

    @Order(2)
    @Test
    @WithMockUser(username = "admin")
    public void testGetIdeas_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/ideas")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

//    @Order(3)
//    @Test
//    public void testGetIdeas_NotAuthenticated() throws Exception {
//        SecurityTestUtil.clearAuthentication();
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/users/ideas")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "createdAt,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
//    }

    @Order(4)
    @Test
    @WithMockUser(username = "admin", authorities = {"VIEW_ALL_IDEAS"})
    public void testGetIdeas_ServiceException() throws Exception {
        SecurityTestUtil.setJwtAuthenticationToken("viewer",
                Set.of("VIEW_ALL_IDEAS"),
                Map.of("preferred_username", "viewer")
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/users/ideas")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "createdAt,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unexpected error"));
    }
}
