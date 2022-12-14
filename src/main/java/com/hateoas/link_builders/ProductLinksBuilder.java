package com.hateoas.link_builders;

import com.hateoas.orders.controller.OrderController;
import com.hateoas.products.controller.ProductController;
import com.hateoas.products.model.Product;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductLinksBuilder implements RepresentationModelAssembler<Product, EntityModel<Product>> {
    @Override
    public EntityModel<Product> toModel(Product product) {
        EntityModel<Product> productEntity = EntityModel.of(product);

        Link selfLink = linkTo(methodOn(ProductController.class).productById(product.getId())).withSelfRel();
        Link collectionLink = linkTo(methodOn(ProductController.class).products()).withRel(IanaLinkRelations.COLLECTION);
        Link ordersLink = linkTo(methodOn(OrderController.class).ordersOfProduct(product.getId())).withRel("orders");

        productEntity.add(selfLink);
        productEntity.add(collectionLink);
        productEntity.add(ordersLink);

        return productEntity;
    }
}
