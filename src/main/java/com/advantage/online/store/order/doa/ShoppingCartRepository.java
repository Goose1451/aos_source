package com.advantage.online.store.order.doa;

import com.advantage.online.store.dao.DefaultCRUDOperations;
import com.advantage.online.store.order.model.ShoppingCart;

import java.util.List;

/**
 * @author Binyamin Regev on 03/12/2015.
 */
public interface ShoppingCartRepository extends DefaultCRUDOperations<ShoppingCart> {

    ShoppingCart createShoppingCart(final String loginName, final Long productId, final String managedImageId, final String colorName, final String colorImageUrl, double price, final int quantity, double productTotal);
    int deleteShoppingCart(final Long id);
    int deleteShoppingCartsByLogin(final String loginName);
    List<ShoppingCart> getAllShoppingCards();
    List<ShoppingCart> getShoppingCardsByLogin();

}