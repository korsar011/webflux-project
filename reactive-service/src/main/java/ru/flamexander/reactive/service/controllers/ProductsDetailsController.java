package ru.flamexander.reactive.service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.DetailedProductDto;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.services.ProductDetailsService;
import ru.flamexander.reactive.service.services.ProductsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detailed")
@RequiredArgsConstructor
public class ProductsDetailsController {
    private final ProductDetailsService productDetailsService;
    private final ProductsService productsService;


    @GetMapping("/{id}")
    public Mono<ResponseEntity<DetailedProductDto>> getProductDetailsById(@PathVariable Long id) {
        return productsService.findById(id)
                .flatMap(product ->
                        productDetailsService.getProductDetailsById(id)
                                .map(details -> new DetailedProductDto(product.getId(), product.getName(), details.getDescription()))
                                .onErrorResume(e -> Mono.just(new DetailedProductDto(product.getId(), product.getName(), "")))
                                .map(ResponseEntity::ok)
                )
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }


    @GetMapping("/bulk/{ids}")
    public Flux<DetailedProductDto> getBulkProductDetails(@PathVariable List<Long> ids) {
        return Flux.fromIterable(ids)
                .flatMap(id -> productsService.findById(id)
                        .flatMap(product -> productDetailsService.getProductDetailsById(id)
                                .map(details -> new DetailedProductDto(product.getId(), product.getName(), details.getDescription()))
                                .defaultIfEmpty(new DetailedProductDto(product.getId(), product.getName(), ""))))
                .onErrorResume(Exception.class, e -> Flux.empty());
    }

    @GetMapping("/demo")
    public Flux<ProductDetailsDto> getManySlowProducts() {
        Mono<ProductDetailsDto> p1 = productDetailsService.getProductDetailsById(1L);
        Mono<ProductDetailsDto> p2 = productDetailsService.getProductDetailsById(2L);
        Mono<ProductDetailsDto> p3 = productDetailsService.getProductDetailsById(3L);
        return p1.mergeWith(p2).mergeWith(p3);
    }
    @GetMapping("/all")
    public Flux<DetailedProductDto> getAllProductDetails() {
        return productsService.findAll()
                .flatMap(product -> productDetailsService.getProductDetailsById(product.getId())
                        .map(details -> new DetailedProductDto(product.getId(), product.getName(), details.getDescription()))
                        .defaultIfEmpty(new DetailedProductDto(product.getId(), product.getName(), "")));
    }

}
