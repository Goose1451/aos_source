package com.advantage.order.store.model;

import javax.persistence.*;
import java.util.Date;

/**
 * The model class for {@code shopping_cart} table entity. Rows can be deleted
 * (removed) without constraints by primary key columns: {@code user_id},
 * {@code product_id} and {@code color_id}.
 *
 * @author Binyamin Regev on 03/12/2015.
 * @see ShoppingCartPK
 */
@Entity
@Table(name = "shopping_cart")
@IdClass(ShoppingCartPK.class)
@NamedQueries({
        @NamedQuery(
                name = ShoppingCart.QUERY_GET_CARTS_BY_USER_ID,
                query = "select s from ShoppingCart s" +
                        " where " + ShoppingCart.FIELD_USER_ID + " = :" + ShoppingCart.PARAM_USER_ID +
                        " order by s.lastUpdate DESC"
        )
        , @NamedQuery(
        name = ShoppingCart.QUERY_GET_CART_BY_PK_COLUMNS,
        query = "select s from ShoppingCart s " +
                "where " + ShoppingCart.FIELD_USER_ID + " = :" + ShoppingCart.PARAM_USER_ID +
                " AND " + ShoppingCart.FIELD_PRODUCT_ID + " = :" + ShoppingCart.PARAM_PRODUCT_ID +
                " AND " + ShoppingCart.FIELD_COLOR_ID + " = :" + ShoppingCart.PARAM_COLOR_ID
        )
})
public class ShoppingCart {

    public static final int MAX_NUM_OF_SHOPPING_CART_PRODUCTS = 50;

    public static final String MESSAGE_INVALID_USER_ID = "Invalid user id, not exist.";
    public static final String MESSAGE_NEW_PRODUCT_UPDATED_SUCCESSFULLY = "New product was updated in shopping cart successfully.";
    public static final String MESSAGE_EXISTING_PRODUCT_UPDATED_SUCCESSFULLY = "Existing product in shopping cart updated successfully.";
    public static final String MESSAGE_QUANTITY_OF_PRODUCT_IN_SHOPPING_CART_WAS_UPDATED_SUCCESSFULLY = "Quantity of product shopping cart was updated successfully.";
    public static final String MESSAGE_PRODUCT_NOT_FOUND_IN_CATALOG = "Product not found in products catalog.";
    public static final String MESSAGE_NO_PRODUCTS_TO_UPDATE_IN_SHOPPING_CART = "No products to update in shopping cart.";
    public static final String MESSAGE_PRODUCT_WITH_COLOR_NOT_FOUND_IN_SHOPPING_CART = "Product with this color not found in user\'s cart.";
    public static final String MESSAGE_PRODUCT_WAS_DELETED_FROM_USER_CART_SUCCESSFULLY = "product was deleted from user cart successfully.";
    public static final String MESSAGE_SHOPPING_CART_IS_EMPTY = "User\'s shopping cart is empty.";
    public static final String MESSAGE_SHOPPING_CART_UPDATED_SUCCESSFULLY = "Shopping cart and all products updated successfully.";
    public static final String MESSAGE_USER_SHOPPING_CART_WAS_CLEARED = "User shopping cart was emptied.";
    public static final String MESSAGE_REPLACE_USER_CART_FAILED = "Replace of user cart failed.";
    public static final String MESSAGE_OOPS_WE_ONLY_HAVE_X_IN_STOCK = "Oops! We only have %s in stock. We updated your order accordingly";
    public static final String MESSAGE_WE_UPDATED_YOUR_CART_BASED_ON_THE_ITEMS_IN_STOCK = "We updated your cart based on the items in stock";

    public static final String QUERY_GET_CARTS_BY_USER_ID = "shoppingCart.getCartsByUserId";
    public static final String QUERY_GET_CART_BY_PK_COLUMNS = "shoppingCart.getCartsByPkColumns";

    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_PRODUCT_ID = "product_id";
    public static final String FIELD_COLOR_ID = "color_id";

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_PRODUCT_ID = "PARAM_PRODUCT_ID";
    public static final String PARAM_COLOR_ID = "PARAM_COLOR_ID";

    @Id
    @Column(name = FIELD_USER_ID)
    private long userId;

    @Column(name = "last_update_timestamp")
    private long lastUpdate;    //  new Date.getTime()

    @Id
    @Column(name = FIELD_PRODUCT_ID)
    private Long productId;

    @Id
    @Column(name = FIELD_COLOR_ID)
    private int color;

    @Column(name = "quantity")
    private int quantity;

    public ShoppingCart() {
    }

    public ShoppingCart(long userId, Long productId, int color, int quantity) {
        this.userId = userId;
        this.setLastUpdate(new Date().getTime());
        this.productId = productId;
        this.color = color;
        this.quantity = quantity;
    }

    public ShoppingCart(long userId, long lastUpdate, Long productId, int color, int quantity) {
        this.userId = userId;
        this.lastUpdate = lastUpdate;
        this.productId = productId;
        this.color = color;
        this.quantity = quantity;
    }

    public long getUserId() {
        return this.userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLastUpdate() { return lastUpdate; }

    public void setLastUpdate(long lastUpdate) { this.lastUpdate = lastUpdate; }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Convert color in decimal (base 10) to Hex (base 16) value.
     *
     * @param intColor Color value in decimal (base 10).
     * @return {@link String}, color value in Hexadecimal (base 16).
     */
    public static String convertIntColorToHex(final int intColor) {
        return Integer.toHexString(intColor);
    }

    /**
     * Convert color in Hex (base 16) to decimal (base 10).
     *
     * @param hexColor Color value in Hexadecimal.
     * @return {@code int}, color value in decimal (base 10).
     */
    public static int convertHexColorToInt(final String hexColor) {
        return Integer.parseInt(hexColor.replaceAll("#", "").trim(), 16);
    }

    @Override
    public String toString() {
        //System.currentTimeMillis()
        //System.out.println("Milliseconds to Date: " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(this.getLastUpdate()));

        return "ShoppingCart{" +
                " userId='" + this.getUserId() + '\'' +
                ", lastUpdate=" + this.getLastUpdate() +
                ", productId=" + this.getProductId() +
                ", color='" + this.getColor() + '\'' +
                ", quantity=" + this.getQuantity() + " }";
    }

    /**
     * This method determines if 2 ShoppingCart objects are the same.
     *
     * @param compareTo
     * @return
     */
    public boolean isTheSame(ShoppingCart compareTo) {
        return ((this.getUserId() == compareTo.getUserId()) &&
                (this.getProductId() == compareTo.getProductId()) &&
                (this.getColor() == compareTo.getColor())
        );
    }

    /**
     * Compare one {@code ShoppingCart} object to another. This is
     * <b>NOT</b> to determine that 2 {@code Products} are the same.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        ShoppingCart compareTo = (ShoppingCart) obj;

        return ((this.getUserId() == compareTo.getUserId()) &&
                (this.getProductId() == compareTo.getProductId()) &&
                (this.getColor() == compareTo.getColor()) &&
                (this.getQuantity() == compareTo.getQuantity())
        );
    }

}
