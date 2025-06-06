package ru.yandex.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingcart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.shoppingcart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotFoundShoppingCartException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundInWarehouseException;
import ru.yandex.practicum.feign.WarehouseClient;

import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.model.ShoppingCartState;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return ShoppingCartMapper.mapToShoppingCartDto(shoppingCartRepository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseGet(() -> createNewShoppingCart(username)));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        Optional<ShoppingCart> shoppingCartOpt = shoppingCartRepository
                .findByUsernameAndState(username, ShoppingCartState.ACTIVE);

        ShoppingCartDto shoppingCartDto;

        if (shoppingCartOpt.isPresent()) {
            ShoppingCart shoppingCart = shoppingCartOpt.get();

            shoppingCart.getCartProducts().putAll(products);

            shoppingCartDto = ShoppingCartMapper.mapToShoppingCartDto(shoppingCart);
        } else {
            shoppingCartDto = ShoppingCartMapper.mapToShoppingCartDto(shoppingCartRepository.save(ShoppingCart.builder()
                    .cartProducts(products)
                    .state(ShoppingCartState.ACTIVE)
                    .username(username)
                    .build()));
        }

        try {
            warehouseClient.checkProductsQuantity(shoppingCartDto);
            log.info("Проверка наличия товаров прошла успешно");
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public void deactivateShoppingCart(String username) {
        ShoppingCart shoppingCart = getActiveCartOfUser(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeShoppingCart(String username, List<UUID> productIds) {
        ShoppingCart shoppingCart = getActiveCartOfUser(username);

        Map<UUID, Long> oldProducts = shoppingCart.getCartProducts();

        if (!productIds.stream().allMatch(oldProducts::containsKey)) {
            throw new NoProductsInShoppingCartException("Продуктов нет в корзине");
        }
        Map<UUID, Long> newProducts = oldProducts.entrySet().stream()
                .filter(c -> !productIds.contains(c.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        shoppingCart.setCartProducts(newProducts);

        return ShoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart shoppingCart = getActiveCartOfUser(username);

        Map<UUID, Long> products = shoppingCart.getCartProducts();

        if (!products.containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Продукта нет в корзине");
        }

        products.put(request.getProductId(), request.getNewQuantity());

        return ShoppingCartMapper.mapToShoppingCartDto(shoppingCart);
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .username(username)
                .state(ShoppingCartState.ACTIVE)
                .cartProducts(new HashMap<>())
                .build();
        shoppingCartRepository.save(shoppingCart);
        return shoppingCart;
    }

    private ShoppingCart getActiveCartOfUser(String username) {
        return shoppingCartRepository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("Нет активной корзины"));
    }
}
