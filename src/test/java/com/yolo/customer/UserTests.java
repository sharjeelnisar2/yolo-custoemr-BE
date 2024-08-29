package com.yolo.customer;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    @Order(2)
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_CREATE_USER","ROLE_VIEW_USER_INFO", "ROLE_CREATE_ACCOUNT"})
    public void testCreateUserProfileSuccess() throws Exception {


        String userJson = "{\"username\":\"testUser\",\"email\":\"testuser@example.com\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        String userProfileJson = "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"contactNumber\":\"1234567890\",\"house\":\"12\",\"street\":\"Main Street\",\"area\":\"Downtown\",\"zipCode\":\"12345\",\"city\":\"Metropolis\",\"country\":\"Country\",\"currencyCode\":\"USD\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users/testUser/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userProfileJson))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User profile created successfully."));
    }

}
