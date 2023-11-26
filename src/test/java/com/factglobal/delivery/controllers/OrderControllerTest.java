package com.factglobal.delivery.controllers;

import com.factglobal.delivery.dto.OrderDTO;
import com.factglobal.delivery.models.Courier;
import com.factglobal.delivery.models.Customer;
import com.factglobal.delivery.models.Order;
import com.factglobal.delivery.services.CourierService;
import com.factglobal.delivery.services.CustomerService;
import com.factglobal.delivery.services.OrderService;
import com.factglobal.delivery.util.common.Mapper;
import com.factglobal.delivery.util.common.OrderBPM;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    @MockBean
    private OrderService orderService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CourierService courierService;

    @MockBean
    private Mapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Order order;
    Courier courier;
    Customer customer;
    OrderDTO orderDTO;
    Principal principal;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setSenderAddress("Sender Address");
        order.setDeliveryAddress("Delivery Address");
        order.setWeight(5);
        order.setDescription("Description");
        order.setPaymentMethod(OrderBPM.PaymentMethod.CASH);
        order.setFragileCargo(true);

        orderDTO = new OrderDTO();
        orderDTO.setSenderAddress("Sender Address");
        orderDTO.setDeliveryAddress("Delivery Address");
        orderDTO.setWeight(5);
        orderDTO.setDescription("Description");
        orderDTO.setPaymentMethod(OrderBPM.PaymentMethod.CASH);
        orderDTO.setFragileCargo(true);

        principal = mock(Principal.class);
    }

    @AfterEach
    void tearDown() {
        courier = null;
        customer = null;
        order = null;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_AdminRole_ReturnsListOfOrderDTOs() throws Exception {
        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(orderDTO);

        when(orderService.findAllOrders()).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders"));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderDtoList)));
        verify(orderService, times(1)).findAllOrders();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrder_AdminRole_ReturnsOrderDTO() throws Exception {
        when(orderService.findOrder(1)).thenReturn(order);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders/{order_id}", 1)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.senderAddress").value(orderDTO.getSenderAddress()))
                .andExpect(jsonPath("$.deliveryAddress").value(orderDTO.getDeliveryAddress()))
                .andExpect(jsonPath("$.weight").value(orderDTO.getWeight()));
        verify(orderService, times(1)).findOrder(1);
        verify(mapper, times(1)).convertToOrderDTO(any(Order.class));

    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createOrder_CustomerRole_ValidInput_ReturnsOkResponse() throws Exception {
        customer = new Customer();
        customer.setId(1);
        customer.setPhoneNumber("+79999999999");

        when(customerService.findCustomerByPhoneNumber(anyString())).thenReturn(customer);
        when(principal.getName()).thenReturn(customer.getPhoneNumber());
        when(orderService.saveOrder(any(Order.class), eq(customer.getId()))).thenReturn(ResponseEntity.ok(HttpStatus.OK));
        when(mapper.convertToOrder(any(OrderDTO.class))).thenReturn(order);

        var result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO))
                .principal(principal));

        result.andExpect(status().isOk());
        verify(customerService, times(1)).findCustomerByPhoneNumber(anyString());
        verify(orderService, times(1)).saveOrder(any(Order.class), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.deleteOrder(1)).thenReturn(ResponseEntity.ok(any()));

        var result = mockMvc.perform(delete("/orders/{order_id}", 1));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).deleteOrder(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editOrderByAdmin_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(mapper.convertToOrder(any(OrderDTO.class))).thenReturn(order);
        when(orderService.editOrderByAdmin(any(Order.class), anyInt())).thenReturn(ResponseEntity.ok(HttpStatus.OK));

        var result = mockMvc.perform(put("/orders/{order_id}/admin", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO))
                .principal(principal));

        result.andExpect(status().isOk());
        verify(mapper, times(1)).convertToOrder(any(OrderDTO.class));
        verify(orderService, times(1)).editOrderByAdmin(any(Order.class), anyInt());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void editOrderByCustomer_CustomerRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(mapper.convertToOrder(any(OrderDTO.class))).thenReturn(order);
        when(orderService.editOrderByCustomer(any(Order.class), eq(1), any(Principal.class))).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDTO))
                .principal(principal));

        result.andExpect(status().isOk());
        verify(mapper, times(1)).convertToOrder(any(OrderDTO.class));
        verify(orderService, times(1)).editOrderByCustomer(any(Order.class), anyInt(), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getOrdersByCustomer_CustomerRole_ReturnsListOfOrderDTOs() throws Exception {
        customer = new Customer();
        customer.setId(1);
        customer.setPhoneNumber("+79999999999");
        OrderDTO orderDTO = mapper.convertToOrderDTO(order);

        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(orderDTO);

        when(principal.getName()).thenReturn(customer.getPhoneNumber());
        when(customerService.findCustomerByPhoneNumber(anyString())).thenReturn(customer);
        when(orderService.findOrdersByCustomer(customer.getId())).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders/customers")
                .principal(principal));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderDtoList)));
        verify(customerService, times(1)).findCustomerByPhoneNumber(anyString());
        verify(orderService, times(1)).findOrdersByCustomer(anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersByCustomerForAdmin_AdminRole_ValidInput_ReturnsListOfOrderDTOs() throws Exception {
        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(orderDTO);

        when(orderService.findOrdersByCustomer(1)).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders/customers/{customer_id}", 1));

        result.andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderDtoList)));
        verify(orderService, times(1)).findOrdersByCustomer(anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersByCourierForAdmin_AdminRole_ValidInput_ReturnsListOfOrderDTOs() throws Exception {
        OrderDTO orderDTO = mapper.convertToOrderDTO(order);

        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(orderDTO);

        when(orderService.findOrdersByCourier(1)).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders/couriers/{courier_id}", 1));

        result.andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderDtoList)));
        verify(orderService, times(1)).findOrdersByCourier(anyInt());
    }

    @Test
    @WithMockUser(roles = "COURIER")
    void getOrdersByCourier_CourierRole_ReturnsListOfOrderDTOs() throws Exception {
        courier = new Courier();
        courier.setPhoneNumber("+79999999999");
        courier.setId(1);

        OrderDTO orderDTO = mapper.convertToOrderDTO(order);

        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(orderDTO);

        when(principal.getName()).thenReturn(courier.getPhoneNumber());
        when(courierService.findCourierByPhoneNumber(anyString())).thenReturn(courier);
        when(orderService.findOrdersByCourier(anyInt())).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(orderDTO);

        var result = mockMvc.perform(get("/orders/couriers")
                .principal(principal));

        result.andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderDtoList)));
        verify(courierService, times(1)).findCourierByPhoneNumber(anyString());
        verify(orderService, times(1)).findOrdersByCourier(anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrderByAdmin_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.cancelOrderByAdmin(anyInt())).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/cancel/admin", 1));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).cancelOrderByAdmin(anyInt());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void cancelOrderByCustomer_CustomerRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.cancelOrderByCustomer(anyInt(), any(Principal.class))).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/cancel", 1)
                .principal(principal));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).cancelOrderByCustomer(anyInt(), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deliveredOrderByAdmin_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.deliveredOrder(anyInt())).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/delivered/admin", 1));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).deliveredOrder(1);
    }

    @Test
    @WithMockUser(roles = "COURIER")
    void deliveredOrderByCourier_CourierRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.deliveredOrder(eq(1), any(Principal.class))).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/delivered", 1)
                .principal(principal));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).deliveredOrder(eq(1), any(Principal.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignCourierForOrderByAdmin_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.assignCourierToOrder(anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/couriers/{courier_id}/assign", 1, 1));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).assignCourierToOrder(1, 1);
    }

    @Test
    @WithMockUser(roles = "COURIER")
    void assignCourierForOrderByCourier_CourierRole_ValidInput_ReturnsOkResponse() throws Exception {
        courier = new Courier();
        courier.setId(1);

        when(principal.getName()).thenReturn("+79999999424");
        when(courierService.findCourierByPhoneNumber(anyString())).thenReturn(courier);
        when(orderService.assignCourierToOrder(anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/couriers/assign", 1)
                .principal(principal));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).assignCourierToOrder(1, courier.getId());
        verify(courierService, times(1)).findCourierByPhoneNumber(anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void releaseCourierFromOrder_AdminRole_ValidInput_ReturnsOkResponse() throws Exception {
        when(orderService.releaseCourierFromOrder(anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        var result = mockMvc.perform(put("/orders/{order_id}/couriers/{courier_id}/release", 1, 1));

        result.andExpect(status().isOk());
        verify(orderService, times(1)).releaseCourierFromOrder(1, 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersByOrderStatus_AdminRole_ValidInput_ReturnsListOfOrderDTOs() throws Exception {
        List<Order> orderList = Collections.singletonList(order);
        List<OrderDTO> orderDtoList = Collections.singletonList(mapper.convertToOrderDTO(order));

        when(orderService.findOrdersByStatus(anyString())).thenReturn(orderList);
        when(mapper.convertToOrderDTO(any(Order.class))).thenReturn(any(OrderDTO.class));

        var result = mockMvc.perform(get("/orders/status")
                .param("status", String.valueOf(OrderBPM.State.NEW)));

        result.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderDtoList)));
        verify(orderService, times(1)).findOrdersByStatus(OrderBPM.State.NEW.toString());
    }
}