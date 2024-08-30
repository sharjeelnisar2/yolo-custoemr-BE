package com.yolo.customer;

import com.yolo.customer.idea.IdeaRepository;
import com.yolo.customer.idea.IdeaService;
import com.yolo.customer.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import util.SecurityTestUtil;

import java.util.Map;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IdeaTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private IdeaService ideaService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IdeaRepository ideaRepository;

    @BeforeEach
    public void setUp() {
        SecurityTestUtil.setJwtAuthenticationToken("ahmad",
                Set.of("ROLE_UPDATE_IDEA_STATUS"),
                Map.of("preferred_username", "admin")
        );
    }

    @Order(1)
    @Test
    public void testSubmitIdeaToVendor_Success() throws Exception {
        String ideaRequestPayload = "{"
                + "\"status\": \"draft\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/ideas/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaRequestPayload))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Idea submitted and status updated successfully."));
    }

    @Order(2)
    @Test
    public void testSubmitIdeaToVendor_Failure_InvalidStatus() throws Exception {
        String ideaRequestPayload = "{"
                + "\"status\": \"\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/ideas/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaRequestPayload))
                .andDo(MockMvcResultHandlers.print())
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").value("Status cannot be empty."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An error occurred"));

    }

    @Order(3)
    @Test
    public void testSubmitIdeaToVendor_Failure_IdeaNotFound() throws Exception {
        String ideaRequestPayload = "{"
                + "\"status\": \"Draft\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/ideas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaRequestPayload))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").value("Ideas not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An error occurred"));
    }

    @Order(4)
    @Test
    public void testSubmitIdeaToVendor_Failure_Unauthorized() throws Exception {
        String ideaRequestPayload = "{"
                + "\"status\": \"Draft\""
                + "}";

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/ideas/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ideaRequestPayload))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").value("Unauthorized to update idea."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("An error occurred"));

    }

}