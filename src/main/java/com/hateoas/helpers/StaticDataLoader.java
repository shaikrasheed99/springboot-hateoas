package com.hateoas.helpers;

import com.hateoas.customers.model.Customer;
import com.hateoas.customers.model.CustomerRepository;
import com.hateoas.orders.model.Order;
import com.hateoas.orders.model.OrderRepository;
import com.hateoas.products.model.Product;
import com.hateoas.products.model.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class StaticDataLoader {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public StaticDataLoader(CustomerRepository customerRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            Customer ironman = new Customer("Ironman");
            Customer thor = new Customer("Thor");

            customerRepository.saveAll(Arrays.asList(ironman, thor));

            Product iPhone = new Product("iPhone", 80000);
            Product macBook_pro = new Product("MacBook Pro", 200000);

            productRepository.saveAll(Arrays.asList(iPhone, macBook_pro));

            Order ironmanOrdersIphone = new Order(ironman, iPhone, 2, 2 * iPhone.getPrice());
            Order ironmanOrdersMacbook = new Order(ironman, macBook_pro, 1, macBook_pro.getPrice());
            Order thorOrdersIphone = new Order(thor, iPhone, 1, iPhone.getPrice());

            orderRepository.saveAll(Arrays.asList(ironmanOrdersIphone, ironmanOrdersMacbook, thorOrdersIphone));
        };
    }
}
