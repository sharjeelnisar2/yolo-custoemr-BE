package com.yolo.customer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.security.test.context.support.WithMockUser;


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
	@WithMockUser(username = "admin", authorities = {"ROLE_VIEW_ORDER_HISTORY"} )
	public void testGetOrderList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/users/orders?page=0&size=10")
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
	public void testUpdateOrderStatus() throws Exception {
		String updatePayload = "{\"order_status\":\"DISPATCHED\"}";

		mockMvc.perform(MockMvcRequestBuilders.patch("/users/orders/ORD001")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updatePayload))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Order status updated successfully"));
	}
}
