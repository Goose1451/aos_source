package com.advantage.order.store.model;

import javax.persistence.*;

/**
 * @author Binyamin Regev on 06/01/2016.
 */
@Entity
@Table(name = "order_lines")
@IdClass(OrderLinesPk.class)
@NamedQueries({
        @NamedQuery(
                name = OrderLines.QUERY_GET_ORDERS_LINES_BY_USER_ID,
                query = "select o from OrderLines o" +
                        " where " + OrderLines.FIELD_USER_ID + " = :" + OrderLines.PARAM_USER_ID +
                        " order by o.orderNumber ASC, o.productName ASC, o.productColorName ASC"
        )
        , @NamedQuery(
        name = OrderLines.QUERY_GET_ORDER_LINES_BY_ORDER_PK,
        query = "select o from OrderLines o " +
                "where " + OrderLines.FIELD_USER_ID + " = :" + OrderLines.PARAM_USER_ID +
                " and " + OrderLines.FIELD_ORDER_NUMBER + " = :" + OrderLines.PARAM_ORDER_NUMBER +
                " order by o.orderNumber ASC, o.productName ASC, o.productColorName ASC"
        )
        , @NamedQuery(
        name = OrderLines.QUERY_GET_ORDERS_LINES_BY_PRODUCT_PK,
        query = "select o from OrderLines o " +
                "where " + OrderLines.FIELD_USER_ID + " = :" + OrderLines.PARAM_USER_ID +
                " and " + OrderLines.FIELD_PRODUCT_ID + " = :" + OrderLines.PARAM_PRODUCT_ID +
                " and " + OrderLines.FIELD_PRODUCT_COLOR + " = :" + OrderLines.PARAM_PRODUCT_COLOR
        )
        , @NamedQuery(
        name = OrderLines.QUERY_GET_ORDER_LINES_BY_ORDER,
        query = "select o from OrderLines o " +
                "where " + OrderLines.FIELD_ORDER_NUMBER + " = :" + OrderLines.PARAM_ORDER_NUMBER +
                " order by o.orderNumber ASC, o.productName ASC, o.productColorName ASC"
        )
        , @NamedQuery(
        name = OrderLines.QUERY_DELETE_ORDERS_LINE_BY_ORDER_PK,
        query = "delete from OrderLines o " +
                "where " + OrderLines.FIELD_ORDER_NUMBER + " = :" + OrderLines.PARAM_ORDER_NUMBER
        ), @NamedQuery(
        name = OrderLines.QUERY_DELETE_ORDERS_LINES_BY_USER_ID_PK,
        query = "delete from OrderLines o " +
                "where " + OrderLines.FIELD_USER_ID + " = :" + OrderLines.PARAM_USER_ID
)
})
public class OrderLines {

    /* Get all orders of registered user */
    public static final String QUERY_GET_ORDERS_LINES_BY_USER_ID = "OrderLines.getOrdersLinesByUserId";
    /* Get all order lines of a specific order by user-id and order-number  */
    public static final String QUERY_GET_ORDER_LINES_BY_ORDER_PK = "OrderLines.getOrderLinesByOrderPK";
    /* Get all order lines of a specific product by product-id and product-color made by specific user-id   */
    public static final String QUERY_GET_ORDERS_LINES_BY_PRODUCT_PK = "OrderLines.getOrdersLinesByProductId";

    /* Get all order lines of a specific order order-number  */
    public static final String QUERY_GET_ORDER_LINES_BY_ORDER = "OrderLines.getOrderLinesByOrder";

    public static final String QUERY_DELETE_ORDERS_LINE_BY_ORDER_PK = "OrderLines.deleteOrder";

    public static final String QUERY_DELETE_ORDERS_LINES_BY_USER_ID_PK = "OrderLines.deleteAllOrdersLinesForUser";

    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_ORDER_NUMBER = "order_number";
    public static final String FIELD_PRODUCT_ID = "product_id";
    public static final String FIELD_PRODUCT_COLOR = "product_color_code";

    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_ORDER_NUMBER = "PARAM_ORDER_NUMBER";
    public static final String PARAM_PRODUCT_ID = "PARAM_PRODUCT_ID";
    public static final String PARAM_PRODUCT_COLOR = "PARAM_PRODUCT_COLOR";

    @Id
    @Column(name = FIELD_USER_ID)
    private long userId;

    @Id
    @Column(name = FIELD_ORDER_NUMBER)
    private long orderNumber;
    /*  OrderProduct    */

    @Id
    @Column(name = FIELD_PRODUCT_ID)
    private Long productId;             //  From Product table in CATALOG schema

    @Column(name = "product_name")
    private String productName;         //  From Product table in CATALOG schema

    @Id
    @Column(name = FIELD_PRODUCT_COLOR)
    private int productColor;           //  RGB decimal value

    @Column(name = "product_color_name")
    private int productColorName;           //  RGB decimal value

    @Column(name = "price_per_item")
    private double pricePerItem;        //  From Product table in CATALOG schema

    @Column(name = "quantity")
    private int quantity;//  From ShoppingCart table in ORDER schema

    @Column(name = "product_image_url")
    private String productImageUrl;

    public OrderLines() { }

    public OrderLines(long userId, long orderNumber, Long productId, int productColor) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productColor = productColor;
    }

    public OrderLines(long userId, long orderNumber, Long productId, String productName, int productColor, double pricePerItem, int quantity) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productName = productName;
        this.productColor = productColor;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
    }

    public OrderLines(long userId, long orderNumber, Long productId, String productName, int productColor, int productColorName, double pricePerItem, int quantity) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productName = productName;
        this.productColor = productColor;
        this.productColorName = productColorName;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
    }

    public OrderLines(long userId, long orderNumber, Long productId, String productName, int productColor, double pricePerItem, int quantity, String productImageUrl) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.productName = productName;
        this.productColor = productColor;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
        this.productImageUrl = productImageUrl;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductColor() {
        return productColor;
    }

    public void setProductColor(int productColor) {
        this.productColor = productColor;
    }

    public void setProductColor(String hexColor) {
        this.productColor = ShoppingCart.convertHexColorToInt(hexColor);
    }

    public int getProductColorName() {
        return productColorName;
    }

    public void setProductColorName(int productColorName) {
        this.productColorName = productColorName;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(double pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductImageUrl() {return productImageUrl;}

    public void setProductImageUrl(String productImageUrl) {this.productImageUrl = productImageUrl;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLines that = (OrderLines) o;

        if (getUserId() != that.getUserId()) return false;
        if (getOrderNumber() != that.getOrderNumber()) return false;
        if (getProductColor() != that.getProductColor()) return false;
        return getProductId().equals(that.getProductId());

    }

    @Override
    public int hashCode() {
        int result = (int) (getUserId() ^ (getUserId() >>> 32));
        result = 31 * result + (int) (getOrderNumber() ^ (getOrderNumber() >>> 32));
        result = 31 * result + getProductId().hashCode();
        result = 31 * result + getProductColor();
        return result;
    }
}
