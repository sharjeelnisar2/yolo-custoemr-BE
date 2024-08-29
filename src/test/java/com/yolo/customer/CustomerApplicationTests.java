package com.yolo.customer;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithUserDetails;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	public void LoginSuccess() throws Exception {
		String loginPayload = "{\"username\":\"FATIMA\",\"password\":\"12345\"}";

	}

	@Order(1)
	@Test
	@WithMockUser(username = "admin", authorities = {"VIEW_ORDER_HISTORY"})
	public void testGetOrderList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/users/orders?page=0&size=10")
						.header("Authorization", "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJd21qQ3RSMXdHOWEtSXVFelA3SEFYN2tkZ2hXZHpzSm8zSzc2b01meW9JIn0.eyJleHAiOjE3MjQ5MTg3MjUsImlhdCI6MTcyNDkxODQyNSwiYXV0aF90aW1lIjoxNzI0OTA5NzQxLCJqdGkiOiIwMDQ3Y2I2NC00NTgyLTQ3OWQtYTViNy05NjkyYmU2YTJmZDIiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvcmVhbG1zL1lPTE8tQ3VzdG9tZXIiLCJzdWIiOiJmMGFjNWU5Mi1jNGZkLTRjZjUtYmFjNC02Zjk2ZTViMTk5N2MiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJDdXN0b21lclZ1ZSIsInNpZCI6IjllZWJjYzlhLTA5NjYtNGI3NC05YTRlLTI4ZGYzNmQwMDc3ZSIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDozMDAxIl0sInJlc291cmNlX2FjY2VzcyI6eyJDdXN0b21lclZ1ZSI6eyJyb2xlcyI6WyJWSUVXX09SREVSX0hJU1RPUlkiLCJVUERBVEVfSURFQV9TVEFUVVMiLCJWSUVXX1VTRVJfSU5GTyIsIlZJRVdfSURFQV9ERVRBSUxTIiwiUExBQ0VfT1JERVIiLCJBUkNISVZFX0lERUEiLCJWSUVXX0FMTF9JREVBUyIsIlZJRVdfUkVDSVBFX0RFVEFJTFMiLCJDUkVBVEVfUFJPRklMRSIsIlZJRVdfQUxMX1JFQ0lQRVMiLCJERUFDVElWQVRFX1VTRVIiLCJDVVNUT01FUiIsIkNSRUFURV9BQ0NPVU5UIiwiQ1JFQVRFX1JFQ0lQRSIsIlNVQk1JVF9JREVBIiwiQ1JFQVRFX0lERUEiLCJVUERBVEVfUFJPRklMRSJdfX0sInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJWYWxlZW5hIEFmemFsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiVmFsZWVuYSIsImZhbWlseV9uYW1lIjoiQWZ6YWwiLCJlbWFpbCI6InZhbGVlbmFhZnphbGZyZWVsYW5jaW5nQGdtYWlsLmNvbSJ9.RLmc2LS3mE5eNh2zgN7mP5SkPMuQ4RmaaQLEK73cfaBaXTROFyF_zu4OD6VKuw0QOaUaY-a4Y0huVJkKM5aAfG-2kfj33T2jAt5wAhK1OPSx6YLuFWcQKiPdBDrOdu2a7VbiyTNApg6_3RkPqLYJU6DJrgaiat5lCj02cM5w-mmjCmRzhVUw4zTRCArmspn_B9UtXBfSYjGa5IEdJkM56lBFoaZHID3lVgybobXqNv3cMx7SPAc3Zb-yJeWrCPH_SuQ38UkOn51U4iVnDxGb_q_q9B-57owvQiKHERCJx2KZd7kS09s-EN_T8IL0VO8Hhreg95WNWA_wP5_bTIMKRA")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.orders", Matchers.hasSize(Matchers.greaterThan(0))));
	}

	@Order(2)
	@Test
	@WithMockUser(username = "admin", authorities = {"ROLE_VIEW_ORDER_HISTORY"} )
	public void testGetOrderDetail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/users/orders/1/orderitems")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.orderItems", Matchers.notNullValue()));
	}

	@Order(3)
	@Test
	@WithMockUser(username = "admin", authorities = {"ROLE_UPDATE_ORDER_STATUS"} )
	public void testUpdateOrderStatus() throws Exception {
		String updatePayload = "{\"order_status\":\"DISPATCHED\"}";

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/orders/ORD001")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatePayload))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.jsonPath("$.data.orders").value("DISPATCHED"));
	}
}
