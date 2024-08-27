//package com.yolo.customer;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yolo.customer.user.UserController;
//import com.yolo.customer.user.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class UserTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private UserService userService;
//
//    @InjectMocks
//    private UserController userController;
//
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    public void testDecodeJwt() throws Exception {
//        // Arrange
//        // Obtain access token
//        String accessToken = obtainAccessToken();
//
//        // Act and Assert
//        mockMvc.perform(MockMvcRequestBuilders.get("/users/jwtToken")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "Bearer " + accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.user_details.name").value("Test User"));
//    }
//
//    private String obtainAccessToken() throws Exception {
//        // Set up the request body
//        String response = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/realms/YOLO-Customer/protocol/openid-connect/token")
//                        .param("grant_type", "password")
//                        .param("client_id", "CustomerApp")
//                        .param("client_secret", "pod9i7QRMzUXbu1e3eP4pRhdwm3DmpAJ")
//                        .param("username", "user")
//                        .param("password", "user1")
//                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//
//        // Parse the response to get the access token
//        JsonNode responseJson = objectMapper.readTree(response);
//        return responseJson.get("access_token").asText();
//    }
//}
