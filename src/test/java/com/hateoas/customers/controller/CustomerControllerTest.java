package com.hateoas.customers.controller;

import com.hateoas.customers.model.Customer;
import com.hateoas.customers.model.CustomerRepository;
import com.hateoas.link_builders.CustomerLinksBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CustomerLinksBuilder customerLinksBuilder;
    @MockBean
    private CustomerRepository customerRepository;
    private List<Customer> customers;
    private Customer ironman;

    @BeforeEach
    void setUp() {
        ironman = new Customer("Ironman");
        Customer thor = new Customer("Thor");
        Customer thanos = new Customer("Thanos");
        customers = new ArrayList<>();
        customers.add(ironman);
        customers.add(thor);
        customers.add(thanos);
    }

    @Test
    void shouldBeAbleToReturnCustomers() throws Exception {
        when(customerRepository.findAll()).thenReturn(customers);

        ResultActions result = mockMvc.perform(get("/customers"));

        result.andExpect(status().isOk()).andDo(print());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldBeAbleToReturnCustomersWithHATEOASLinks() throws Exception {
        when(customerRepository.findAll()).thenReturn(customers);
        Link link = linkTo(methodOn(CustomerController.class).customers()).withSelfRel();

        ResultActions result = mockMvc.perform(get("/customers"));

        result.andExpect(status().isOk()).
                andExpect(jsonPath("$._embedded.customerList[0].name").value(customers.get(0).getName()))
                .andExpect(jsonPath("$._embedded.customerList[1].id").value(customers.get(1).getId()))
                .andExpect(jsonPath("$._embedded.customerList[2].name").value(customers.get(2).getName()))
                .andExpect(jsonPath("$._links.self.href").value(link.getHref()))
                .andDo(print());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldBeAbleToReturnCustomersWithHATEOASLinksForEveryCustomer() throws Exception {
        when(customerRepository.findAll()).thenReturn(customers);
        EntityModel<Customer> customerEntityModel = customerLinksBuilder.toModel(ironman);
        Link selfLink = linkTo(methodOn(CustomerController.class).customers()).withSelfRel();
        Link customerSelfLink = customerEntityModel.getRequiredLink("self");
        Link customerCollectionLink = customerEntityModel.getRequiredLink("collection");
        Link customerOrdersLink = customerEntityModel.getRequiredLink("orders");

        ResultActions result = mockMvc.perform(get("/customers"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customerList[0].id").value(customers.get(0).getId()))
                .andExpect(jsonPath("$._embedded.customerList[0].name").value(customers.get(0).getName()))
                .andExpect(jsonPath("$._embedded.customerList[0]._links.self.href").value(customerSelfLink.getHref()))
                .andExpect(jsonPath("$._embedded.customerList[0]._links.collection.href").value(customerCollectionLink.getHref()))
                .andExpect(jsonPath("$._embedded.customerList[0]._links.orders.href").value(customerOrdersLink.getHref()))
                .andExpect(jsonPath("$._links.self.href").value(selfLink.getHref()))
                .andDo(print());

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldBeAbleToReturnCustomerById() throws Exception {
        when(customerRepository.findById(ironman.getId())).thenReturn(Optional.ofNullable(ironman));

        ResultActions result = mockMvc.perform(get("/customers/{customerId}", ironman.getId()));

        result.andExpect(status().isOk()).andDo(print());

        verify(customerRepository, times(1)).findById(ironman.getId());
    }

    @Test
    void shouldBeAbleToReturnCustomerByIdWithHATEOASLink() throws Exception {
        when(customerRepository.findById(ironman.getId())).thenReturn(Optional.ofNullable(ironman));
        EntityModel<Customer> customerEntityModel = customerLinksBuilder.toModel(ironman);
        Link selfLink = customerEntityModel.getRequiredLink("self");

        ResultActions result = mockMvc.perform(get("/customers/{customerId}", ironman.getId()));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ironman.getId()))
                .andExpect(jsonPath("$.name").value(ironman.getName()))
                .andExpect(jsonPath("$._links.self.href").value(selfLink.getHref()))
                .andDo(print());

        verify(customerRepository, times(1)).findById(ironman.getId());
    }

    @Test
    void shouldBeAbleToReturnCustomerByIdWithHATEOASLinkOfSelfAndCollectionAndOrders() throws Exception {
        when(customerRepository.findById(ironman.getId())).thenReturn(Optional.ofNullable(ironman));
        EntityModel<Customer> customerEntityModel = customerLinksBuilder.toModel(ironman);
        Link selfLink = customerEntityModel.getRequiredLink("self");
        Link collectionLink = customerEntityModel.getRequiredLink("collection");
        Link ordersLink = customerEntityModel.getRequiredLink("orders");

        ResultActions result = mockMvc.perform(get("/customers/{id}", ironman.getId()));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ironman.getId()))
                .andExpect(jsonPath("$.name").value(ironman.getName()))
                .andExpect(jsonPath("$._links.self.href").value(selfLink.getHref()))
                .andExpect(jsonPath("$._links.collection.href").value(collectionLink.getHref()))
                .andExpect(jsonPath("$._links.orders.href").value(ordersLink.getHref()))
                .andDo(print());

        verify(customerRepository, times(1)).findById(ironman.getId());
    }
}
