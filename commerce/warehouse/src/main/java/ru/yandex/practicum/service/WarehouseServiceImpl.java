package ru.yandex.practicum.service;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import ru.yandex.practicum.address.Address;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.enums.QuantityState;
import ru.yandex.practicum.exception.OrderBookingNotFoundException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final OrderBookingRepository orderBookingRepository;
    private final WarehouseRepository warehouseRepository;
    private final ShoppingStoreClient storeClient;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newProductRequest) {
        if (warehouseRepository.existsById(newProductRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Продукт уже есть на складе");
        }

        warehouseRepository.save(WarehouseProductMapper.mapToWarehouseProduct(newProductRequest));
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        checkOrderBooking(request).setDeliveryId(request.getDeliveryId());
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(products.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        products.forEach((key, value) -> {
            if (!warehouseProducts.containsKey(key)) {
                throw new ProductNotFoundInWarehouseException("Товара нет на складе");
            }
            warehouseProducts.get(key).setQuantity(warehouseProducts.get(key).getQuantity() + value);
        });
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository
                .findAllById(shoppingCartDto.getProducts().keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        return checkQuantity(shoppingCartDto.getProducts(), warehouseProducts);
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest) {
        Map<UUID, Long> assemblyProducts = assemblyRequest.getProducts();

        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository
                .findAllById(assemblyProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        BookedProductsDto bookedProducts = checkQuantity(assemblyProducts, warehouseProducts);

        warehouseProducts.forEach((key, value) -> value.setQuantity(value.getQuantity() - assemblyProducts.get(key)));

        orderBookingRepository.save(OrderBooking.builder()
                .products(assemblyProducts)
                .orderId(assemblyRequest.getOrderId())
                .build());

        return bookedProducts;
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest addProductQuantity) {

        WarehouseProduct warehouseProduct = checkWarehouseProduct(addProductQuantity);

        warehouseProduct.setQuantity(warehouseProduct.getQuantity() + addProductQuantity.getQuantity());

        try {
            updateProductQuantityInShoppingStore(warehouseProduct);
        } catch (FeignException e) {
            if (e.status() == 404) {
                log.info("Товар ещё не добавили на витрину магазина");
            } else {
                log.error("Ошибка при обновлении количества товара в магазине", e);
            }
        }
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = Address.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    private BookedProductsDto checkQuantity(Map<UUID, Long> cartProducts,
                                            Map<UUID, WarehouseProduct> warehouseProducts) {
        Set<UUID> productIds = warehouseProducts.keySet();
        cartProducts.keySet().forEach(id -> {
            if (!productIds.contains(id)) {
                throw new ProductNotFoundInWarehouseException("Товара нет на складе");
            }
        });
        log.info("Товаров хватает на складе");

        cartProducts.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Товара не хватает на складе");
            }
        });
        log.info("Товаров хватает на складе");

        return getBookedProducts(warehouseProducts.values(), cartProducts);
    }

    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> productList,
                                                Map<UUID, Long> cartProducts) {
        return BookedProductsDto.builder()
                .fragile(productList.stream().anyMatch(WarehouseProduct::getFragile))
                .deliveryWeight(productList.stream()
                        .mapToDouble(p -> p.getWeight() * cartProducts.get(p.getProductId()))
                        .sum())
                .deliveryVolume(productList.stream()
                        .mapToDouble(p ->
                                p.getWidth() * p.getHeight() * p.getDepth() * cartProducts.get(p.getProductId()))
                        .sum())
                .build();
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long quantity = product.getQuantity();

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (quantity < 100) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.MANY;
        }

        log.info("Обновляем значение перечисления");
        storeClient.setQuantityState(productId, quantityState);
    }

    private WarehouseProduct checkWarehouseProduct(AddProductToWarehouseRequest addProductQuantity) {
        return warehouseRepository.findById(addProductQuantity.getProductId())
                .orElseThrow(() -> new ProductNotFoundInWarehouseException("Товара нет на складе"));
    }

    private OrderBooking checkOrderBooking(ShippedToDeliveryRequest  request) {
        return orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderBookingNotFoundException("Для заказа не найдено бронирование"));
    }
}
