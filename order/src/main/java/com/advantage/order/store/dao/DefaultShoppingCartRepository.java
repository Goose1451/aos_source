package com.advantage.order.store.dao;

import com.advantage.common.Constants;
import com.advantage.order.store.dto.ShoppingCartDto;
import com.advantage.order.store.dto.ShoppingCartResponse;
import com.advantage.order.store.dto.ShoppingCartResponseDto;
import com.advantage.order.store.model.ShoppingCart;
import com.advantage.order.store.model.ShoppingCartPK;
import com.advantage.root.util.ArgumentValidationHelper;

import com.advantage.order.store.services.*;

import com.advantage.root.util.RestApiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * Order services - Data Manager
 * @author Binyamin Regev on 03/12/2015.
 */
@Component
@Qualifier("shoppingCartRepository")
@Repository
public class DefaultShoppingCartRepository extends AbstractRepository implements ShoppingCartRepository {

    private static final Logger logger = Logger.getLogger(DefaultShoppingCartRepository.class);

    private static String NOT_FOUND = "NOT FOUND";

    private ShoppingCartResponse shoppingCartResponse;
    private String failureMessage;

    public String getFailureMessage() {
        return this.failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public ShoppingCartResponseDto.CartProduct setNotFoundCartProduct(Long productId) {
        return new ShoppingCartResponseDto()
                .createCartProduct(productId, Constants.NOT_FOUND, -999999.99, 0, Constants.NOT_FOUND, false);
    }

    @Override
    public ShoppingCartResponse getShoppingCartResponse() {
        return this.shoppingCartResponse;
    }

    /** Get all {@link ShoppingCart} lines of specific <i>registered user</i> by {@code userId}.    */
    @Override
    public List<ShoppingCart> getShoppingCartProductsByUserId(long userId) {

        /*
        //  Verify userId belongs to a registered user by calling "Account Service"
        //  REST API GET REQUEST using URI
        if (!isRegisteredUserExists(userId)) {
            shoppingCartResponse = new ShoppingCartResponse(false, ShoppingCart.MESSAGE_INVALID_USER_ID, -1);
            return null;
        }
         */

        List<ShoppingCart> shoppingCarts = entityManager.createNamedQuery(ShoppingCart.QUERY_GET_CARTS_BY_USER_ID, ShoppingCart.class)
                .setParameter(ShoppingCart.PARAM_USER_ID, userId)
                .getResultList();

        return ((shoppingCarts == null) || (shoppingCarts.isEmpty())) ? null : shoppingCarts;
    }

    /**
     * Find a specific product with specific color in user cart
     * @param userId Unique identifier of user account in <i><b>Account Service</b></i>.
     * @param productId Unique identifier of {@code Product} in <i><b>Catalog Service</b></i>.
     * @param color decimal RGB value
     * @return {@link ShoppingCart} product class
     */
    @Override
    public ShoppingCart find(long userId, Long productId, int color) {
        ShoppingCartPK shoppingCartPk = new ShoppingCartPK(userId, productId, color);
        ShoppingCart shoppingCart = entityManager.find(ShoppingCart.class, shoppingCartPk);

        return shoppingCart;
    }

    /**
     * Add a single product to a specific user shopping cart.
     */
    @Override
    public void add(ShoppingCart shoppingCart) {
        //  Validate Arguments
        ArgumentValidationHelper.validateArgumentIsNotNull(shoppingCart, "shopping cart product");

        ArgumentValidationHelper.validateLongArgumentIsPositive(shoppingCart.getUserId(), "user id");
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(shoppingCart.getLastUpdate(), "last update timestamp");
        ArgumentValidationHelper.validateArgumentIsNotNull(shoppingCart.getProductId(), "product id");
        ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(shoppingCart.getColor(), "color decimal RGB value");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(shoppingCart.getQuantity(), "quantity");


        if (shoppingCart.getLastUpdate() == 0) {
            shoppingCart.setLastUpdate(Calendar.getInstance().getTime().getTime());
        }
        String delayRequest = RestApiHelper.getDemoAppConfigParameterValue("Add_to_cart_time_delay");
        int delayRequestTime = Integer.parseInt(delayRequest);

        if(delayRequestTime>0){

            addTimeDelay(delayRequestTime);
        }

        entityManager.persist(shoppingCart);

    }

    /**
     * Update quantity of a specific product in the user cart.
     */
    @Override
    public ShoppingCartResponse update(long userId, Long productId, int color, int quantity) {

        //  Validate Arguments
        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");
        ArgumentValidationHelper.validateArgumentIsNotNull(productId, "product id");
        ArgumentValidationHelper.validateLongArgumentIsPositive(Long.valueOf(productId), "product id");
        ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(color, "color decimal RGB value");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(quantity, "quantity");

        ShoppingCart shoppingCart = this.find(userId, productId, color);

        String delayRequest = RestApiHelper.getDemoAppConfigParameterValue("Add_to_cart_time_delay");
        int delayRequestTime = Integer.parseInt(delayRequest);

        if(delayRequestTime>0){

            addTimeDelay(delayRequestTime);
        }

        if (shoppingCart != null) {
            //  Product with color was found in user cart
            shoppingCart.setQuantity(quantity);     //  Set argument quantity as product quantity in user cart
            shoppingCart.setLastUpdate(Calendar.getInstance().getTime().getTime());

            entityManager.persist(shoppingCart);    //  Update changes

            //  Set RESPONSE object
            shoppingCartResponse = new ShoppingCartResponse(
                    true,
                    ShoppingCart.MESSAGE_EXISTING_PRODUCT_UPDATED_SUCCESSFULLY,
                    productId);
        } else {
            //  Product with color NOT FOUND in user cart - Set RESPONSE object to FAILURE
            this.failureMessage = ShoppingCart.MESSAGE_PRODUCT_WITH_COLOR_NOT_FOUND_IN_SHOPPING_CART;
            shoppingCartResponse = new ShoppingCartResponse(
                    false,
                    ShoppingCart.MESSAGE_PRODUCT_WITH_COLOR_NOT_FOUND_IN_SHOPPING_CART,
                    -1);
        }

        //return shoppingCart;
        return shoppingCartResponse;
    }

    private void addTimeDelay(int timeToDelay){

        try {
            Thread.sleep(timeToDelay * 1000);
            logger.info("Add time delay activated with "+timeToDelay+ " milliseconds");
        } catch (InterruptedException e) {
            logger.fatal(e);
        }
    }

    @Override
    public int delete(ShoppingCart... entities) {
        int count = 0;
        for (ShoppingCart entity : entities) {
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
                count++;
            }
        }

        return count;
    }

    @Override
    public int removeProductFromUserCart(long userId, Long productId, int color) {

        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");
        ArgumentValidationHelper.validateLongArgumentIsPositive(Long.valueOf(productId), "product id");
        ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(color, "color RGB decimal value");

        ShoppingCart shoppingCart = this.find(userId, productId, color);
        int result = 0;


        if (shoppingCart != null) {

            entityManager.remove(shoppingCart);

            result = 1;

            //final StringBuilder hql = new StringBuilder("DELETE FROM ")
            //        .append(ShoppingCart.class.getName())
            //        .append(" WHERE ")
            //        .append(ShoppingCart.FIELD_USER_ID).append(" = ").append(userId).append(" AND ")
            //        .append(ShoppingCart.FIELD_PRODUCT_ID).append(" = ").append(productId).append(" AND ")
            //        .append(ShoppingCart.FIELD_COLOR_ID).append(" = ").append(color);
            //
            //Query query = entityManager.createQuery(hql.toString());
            //
            //result = query.executeUpdate();

            entityManager.remove(shoppingCart);

            result = 1;

            //final StringBuilder hql = new StringBuilder("DELETE FROM ")
            //        .append(ShoppingCart.class.getName())
            //        .append(" WHERE ")
            //        .append(ShoppingCart.FIELD_USER_ID).append(" = ").append(userId).append(" AND ")
            //        .append(ShoppingCart.FIELD_PRODUCT_ID).append(" = ").append(productId).append(" AND ")
            //        .append(ShoppingCart.FIELD_COLOR_ID).append(" = ").append(color);
            //
            //Query query = entityManager.createQuery(hql.toString());
            //
            //result = query.executeUpdate();

            shoppingCartResponse = new ShoppingCartResponse(
                    true,
                    ShoppingCart.MESSAGE_PRODUCT_WAS_DELETED_FROM_USER_CART_SUCCESSFULLY,
                    productId);
        } else {
            shoppingCartResponse = new ShoppingCartResponse(
                    false,
                    ShoppingCart.MESSAGE_PRODUCT_WITH_COLOR_NOT_FOUND_IN_SHOPPING_CART,
                    productId);
        }

        return result;
    }

    /**
     * Delete all {@link ShoppingCart} lines of specific <i>application user</i>
     * by {@code userId}. <br/>
     * Step #1: Use method {@link #getShoppingCartProductsByUserId} to get user's shopping carts. <br/>
     * Step #2: For each {@link ShoppingCart} get its ID and use method
     * {@link #removeProductFromUserCart} to delete it. <br/>
     */
    @Override
    public ShoppingCartResponse clearUserCart(long userId) {

        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");

        System.out.println("clearUserCart.userId=" + userId);

        /*
        //  Verify userId belongs to a registered user by calling "Account Service"
        //  REST API GET REQUEST using URI
        if (!isRegisteredUserExists(userId)) {
            return new ShoppingCartResponse(false, ShoppingCart.MESSAGE_INVALID_USER_ID, -1);
        }
         */

        //  Get user's shopping carts
        List<ShoppingCart> shoppingCarts = getShoppingCartProductsByUserId(userId);

        if ((shoppingCarts == null) || (shoppingCarts.size() == 0)) {
            //  If shopping cart is empty means successful - exit method
            return new ShoppingCartResponse(true, ShoppingCart.MESSAGE_SHOPPING_CART_IS_EMPTY, -1);
        }

        for (ShoppingCart cart : shoppingCarts) {
            //this.removeProductFromUserCart(userId, cart.getProductId(), cart.getColor());
            //if (!shoppingCartResponse.isSuccess()) {
            //    return new ShoppingCartResponse(false, ShoppingCart.MESSAGE_USER_SHOPPING_CART_WAS_CLEARED, -1);
            //}
            ShoppingCart shoppingCart = this.find(userId, cart.getProductId(), cart.getColor());
            if (shoppingCart != null) {
                this.delete(shoppingCart);
            }
        }
        return new ShoppingCartResponse(true, ShoppingCart.MESSAGE_USER_SHOPPING_CART_WAS_CLEARED, -1);
    }

    @Override
    public ShoppingCartResponse replace(long userId, Collection<ShoppingCartDto> cartProducts) {

        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");

        /*
        //  Verify userId belongs to a registered user by calling "Account Service"
        //  REST API GET REQUEST using URI
        if (!isRegisteredUserExists(userId)) {
            return new ShoppingCartResponse(false, ShoppingCart.MESSAGE_INVALID_USER_ID, -1);
        }
         */

        shoppingCartResponse = clearUserCart(userId);

        if (shoppingCartResponse.isSuccess()) {
            //  Clear user cart was successful - add new cart to user
            shoppingCartResponse = new ShoppingCartResponse(true, ShoppingCart.MESSAGE_SHOPPING_CART_UPDATED_SUCCESSFULLY, -1);

            long lastUpdate = cartProducts.size();

            for (ShoppingCartDto cartProduct : cartProducts) {

                add(new ShoppingCart(userId,
                        lastUpdate,
                        cartProduct.getProductId(),
                        ShoppingCart.convertHexColorToInt(cartProduct.getHexColor()),
                        cartProduct.getQuantity()));

                lastUpdate--;
            }
        }

        return shoppingCartResponse;
    }

    @Override
    public List<ShoppingCart> getAll() {
        List<ShoppingCart> shoppingCartList = entityManager.createNamedQuery(ShoppingCart.QUERY_GET_CART_ALL, ShoppingCart.class)
                .getResultList();
        return shoppingCartList;

    }

    @Override
    public ShoppingCart get(Long entityId) {
        return null;
    }

    @Override
    public String toString() {
        return "DefaultShoppingCartRepository{" +
                "shoppingCartResponse=" + shoppingCartResponse +
                ", failureMessage='" + failureMessage + '\'' +
                '}';
    }
}
