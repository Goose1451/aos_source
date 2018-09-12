package com.advantage.order.store.model;

import javax.persistence.*;

/**
 * @author Binyamin Regev on 06/01/2016.
 */
@Entity
@Table(name = "order_header")
@IdClass(OrderHeaderPk.class)
@NamedQueries({
        @NamedQuery(
                name = OrderHeader.QUERY_GET_ORDERS_BY_USER_ID,
                query = "select o from OrderHeader o" +
                        " where " + OrderHeader.FIELD_USER_ID + " = :" + OrderHeader.PARAM_USER_ID +
                        " order by o.orderTimestamp ASC"
        )
        , @NamedQuery(
        name = OrderHeader.QUERY_GET_ORDER_BY_PK_COLUMNS,
        query = "select o from OrderHeader o " +
                "where " + OrderHeader.FIELD_USER_ID + " = :" + OrderHeader.PARAM_USER_ID +
                " AND " + OrderHeader.FIELD_ORDER_NUMBER + " = :" + OrderHeader.PARAM_ORDER_NUMBER
        )
        , @NamedQuery(
        name = OrderHeader.QUERY_GET_All_ORDERS_HISTORY,
        query = "select o from OrderHeader o "
        )
        , @NamedQuery(
        name = OrderHeader.QUERY_GET_ORDER_BY_ORDER,
        query = "select o from OrderHeader o " +
                "where " + OrderHeader.FIELD_ORDER_NUMBER + " = :" + OrderHeader.PARAM_ORDER_NUMBER
        )
        , @NamedQuery(
        name = OrderHeader.QUERY_DELETE_ORDER_BY_USER_PK,
        query = "delete from OrderHeader o " +
                "where " + OrderHeader.FIELD_USER_ID + " = :" + OrderHeader.PARAM_USER_ID
)
})
public class OrderHeader {

    public static final String QUERY_GET_ORDERS_BY_USER_ID = "orderHeader.getOrdersByUserId";
    public static final String QUERY_GET_All_ORDERS_HISTORY = "orderHeader.getAllOrdersHistory";
    public static final String QUERY_GET_ORDER_BY_PK_COLUMNS = "orderHeader.getOrderByPkColumns";
    public static final String QUERY_GET_ORDER_BY_ORDER = "orderHeader.getOrderByOrderID";
    public static final String QUERY_DELETE_ORDER_BY_ORDER_PK = "orderHeader.deleteOrder";
    public static final String QUERY_DELETE_ORDER_BY_USER_PK = "OrderHeader.deleteHeaderOrderByUserId";

    public static final String FIELD_USER_ID = "user_id";
    public static final String FIELD_ORDER_NUMBER = "order_number";
    public static final String FIELD_ORDER_TIMESTAMP = "order_timestamp";


    public static final String PARAM_USER_ID = "PARAM_USER_ID";
    public static final String PARAM_ORDER_NUMBER = "PARAM_ORDER_NUMBER";


    @Id
    @Column(name = FIELD_USER_ID)
    private long userId;

    @Id
    @Column(name = FIELD_ORDER_NUMBER)
    private long orderNumber;

    @Column(name = FIELD_ORDER_TIMESTAMP)
    private long orderTimestamp;

    /*  OrderShippingInformation - Begin */
    @Column(name = "number_of_products")
    private int numberOfProducts;       //  Numeric. 1-5 digits

    @Column(name = "shipping_cost")
    private double shippingCost;        //  "##.##"

    @Column(name = "shipping_tracking_number")
    private double shippingTrackingNumber;  //  10 digits number

    /*  ShippingAddress - Begin */
    @Column(name = "Shipping_address")
    private String shippingAddress;     //  0-100 characters (Street name, house number, floor number, apartment number)

    @Column(name = "Shipping_city")
    private String shippingCity;        //  0-25 characters

    @Column(name = "Shipping_postal_code")
    private String shippingPostalCode;	//  0-10 characters

    @Column(name = "Shipping_state")
    private String shippingState;       //  0-10 characters

    @Column(name = "Shipping_country")
    private String shippingCountry;     //  2 characters
    /*  ShippingAddress - End   */
    /*  OrderShippingInformation - End */

    @Column(name = "payment_method")
    private String paymentMethod;       //  from PaymentMethodEnum: "MasterCredit" / "SafePay"

    @Column(name = "payment_confirmation_number")
    private long paymentConfirmationNumber;     //  10 digits

    /*  paymentDetails  */
    @Column(name = "transaction_type")
    private String transactionType;     //  from TransactionTypeEnum: "Payment" / "Refund"

    @Column(name = "card_number")
    private String cardNumber;          //  MasterCredit ONLY. MCCardNumber

    @Column(name = "expiration_date")
    private String expirationDate; 		//  MasterCredit ONLY. MCExpirationDate

    @Column(name = "customer_name")
    private String customerName;		//  MasterCredit ONLY. MCCustomerName.

    @Column(name = "safe_pay_username")
    private String username;            //  SafePay ONLY. SafePay.username

    @Column(name = "customer_phone")
    private String customerPhone;       //  International phone number

    @Column(name = "cvv_number")
    private String cvvNumber;           //  MasterCredit ONLY. MCCVVNumber.

    @Column(name = "transaction_date")
    private String transactionDate;     //  TransactionDate

    @Column(name = "account_number")
    private String accountNumber;       //  ReceivingCard.AccountNumber

    @Column(name = "amount")
    private double amount;              //  ReceivingAmount.Value

    @Column(name = "currency")
    private String currency;            //  ReceivingAmount.Currency

    public OrderHeader() { }

    public OrderHeader(long userId, long orderNumber) {
        this.userId = userId;
        this.orderNumber = orderNumber;
    }

    public OrderHeader(long userId, long orderNumber, long orderTimestamp, double shippingCost, long shippingTrackingNumber) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.orderTimestamp = orderTimestamp;
        this.shippingCost = shippingCost;
        this.shippingTrackingNumber = shippingTrackingNumber;
    }

    public long getUserId() { return userId; }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(int numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getShippingTrackingNumber() {
        return shippingTrackingNumber;
    }

    public void setShippingTrackingNumber(double shippingTrackingNumber) {
        this.shippingTrackingNumber = shippingTrackingNumber;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public long getPaymentConfirmationNumber() {
        return paymentConfirmationNumber;
    }

    public void setPaymentConfirmationNumber(long paymentConfirmationNumber) {
        this.paymentConfirmationNumber = paymentConfirmationNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCvvNumber() {
        return cvvNumber;
    }

    public void setCvvNumber(String cvvNumber) {
        this.cvvNumber = cvvNumber;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderLines that = (OrderLines) o;

        if (getUserId() != that.getUserId()) return false;
        if (getOrderNumber() != that.getOrderNumber()) return false;
        else{return false;}
    }

    @Override
    public int hashCode() {
        int result = (int) (getUserId() ^ (getUserId() >>> 32));
        result = 31 * result + (int) (getOrderNumber() ^ (getOrderNumber() >>> 32));
        return result;
    }
}
