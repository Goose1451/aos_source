package com.advantage.order.store.dao;

import com.advantage.common.Constants;
import com.advantage.common.dto.CatalogResponse;
import com.advantage.common.enums.PaymentMethodEnum;
import com.advantage.common.enums.TransactionTypeEnum;
import com.advantage.order.store.dto.OrderPaymentInformation;
import com.advantage.order.store.dto.OrderPurchaseResponse;
import com.advantage.order.store.dto.OrderPurchasedProductInformation;
import com.advantage.order.store.dto.OrderShippingInformation;
import com.advantage.order.store.model.OrderHeader;
import com.advantage.order.store.model.OrderHeaderPk;
import com.advantage.order.store.model.OrderLines;
import com.advantage.order.store.model.ShoppingCart;
import com.advantage.root.util.ArgumentValidationHelper;
import com.advantage.root.util.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Binyamin Regev on 07/01/2016.
 */
@Component
@Qualifier("orderManagementRepository")
@Repository
public class DefaultOrderManagementRepository extends AbstractRepository implements OrderManagementRepository {

    private static final String MESSAGE_SHIPPING_TRACKING_NUMBER_UPDATED_SUCCESSFULLY = "Purchase order, shipping tracking number was updated successfully";
    private static final String MESSAGE_ORDER_NOT_FOUND_TRACKING_NUMBER_NOT_UPDATED = "Purchase order not found, tracking number not updated";
    private static final Logger logger = Logger.getLogger(DefaultOrderManagementRepository.class);

    @Autowired
    @Qualifier("historyOrderHeaderRepository")
    public HistoryOrderHeaderRepository historyOrderHeaderRepository;

    @Autowired
    @Qualifier("historyOrderLineRepository")
    public HistoryOrderLineRepository historyOrderLineRepository;

    @Autowired
    @Qualifier("shoppingCartRepository")
    public ShoppingCartRepository shoppingCartRepository;

    private OrderPurchaseResponse orderPurchaseResponse = null;

    /**
     * Add an order to {@code Entities} {@link OrderHeader} and {@link OrderLines} in <b><i>ORDER</i></b> Schema.
     * @param userId
     * @param orderNumber
     * @param orderTimestamp
     * @param totalAmount
     * @param orderShippingInformation
     * @param orderPaymentInformation
     * @param purchasedProducts
     */
    @Override
    public void addUserOrder(long userId, long orderNumber, long orderTimestamp, double totalAmount,
                             OrderShippingInformation orderShippingInformation,
                             OrderPaymentInformation orderPaymentInformation,
                             List<OrderPurchasedProductInformation> purchasedProducts) {

        OrderHeader orderHeader = new OrderHeader();

        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");
        ArgumentValidationHelper.validateLongArgumentIsPositive(orderNumber, "order number");
        ArgumentValidationHelper.validateLongArgumentIsPositive(orderTimestamp, "order timestamp");

        ArgumentValidationHelper.validateNumberArgumentIsPositive(orderShippingInformation.getNumberOfProducts(), "number of products");
        ArgumentValidationHelper.validateDoubleArgumentIsPositive(orderShippingInformation.getShippingCost(), "shipping cost");
        //ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderShippingInformation.getAddress(), "shipping address");
        //ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderShippingInformation.getCity(), "shipping city");
        //ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderShippingInformation.getPostalCode(), "shipping postal code");
        //ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderShippingInformation.getState(), "shipping state");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderShippingInformation.getCountryCode(), "shipping country");

        this.validatePaymentMethod(orderPaymentInformation.getPaymentMethod(), "payment method");
        this.validateTransactionType(StringHelper.toInitCap(orderPaymentInformation.getTransactionType()), "transaction type");
        ArgumentValidationHelper.validateLongArgumentIsPositive(orderPaymentInformation.getReferenceNumber(), "payment confirmation number");
        //ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getCustomerPhone(), "customer phone");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getTransactionDate(), "transaction date");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getAccountNumber(), "account number");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getCurrency(), "currency");
        ArgumentValidationHelper.validateDoubleArgumentIsPositive(totalAmount, "payment amount");

        if (orderPaymentInformation.getPaymentMethod().equalsIgnoreCase(PaymentMethodEnum.MASTER_CREDIT.getName())) {
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getCardNumber(), "MasterCredit Card Number");
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getExpirationDate(), "MasterCredit card expiration date");
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getCustomerName(), "MasterCredit customer name on card");
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getCvvNumber(), "MasterCredit card CVV number");
        }
        else if (orderPaymentInformation.getPaymentMethod().equalsIgnoreCase(PaymentMethodEnum.SAFE_PAY.getName())) {
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(orderPaymentInformation.getUsername(), "SafePay Card Number");
        }

        orderHeader.setUserId(userId);
        orderHeader.setOrderNumber(orderNumber);
        orderHeader.setOrderTimestamp(orderTimestamp);

        orderHeader.setNumberOfProducts(orderShippingInformation.getNumberOfProducts());
        orderHeader.setShippingCost(orderShippingInformation.getShippingCost());
        //orderHeader.setShippingAddress(orderShippingInformation.getAddress());
        //orderHeader.setShippingCity(orderShippingInformation.getCity());
        //orderHeader.setShippingPostalCode(orderShippingInformation.getPostalCode());
        //orderHeader.setShippingState(orderShippingInformation.getState());
        orderHeader.setShippingCountry(orderShippingInformation.getCountryCode());

        orderHeader.setPaymentMethod(orderPaymentInformation.getPaymentMethod());
        orderHeader.setTransactionType(orderPaymentInformation.getTransactionType());
        orderHeader.setPaymentConfirmationNumber(orderPaymentInformation.getReferenceNumber());
        orderHeader.setCustomerPhone(orderPaymentInformation.getCustomerPhone());
        orderHeader.setTransactionDate(orderPaymentInformation.getTransactionDate());
        orderHeader.setAccountNumber(orderPaymentInformation.getAccountNumber());
        orderHeader.setCurrency(orderPaymentInformation.getCurrency());
        orderHeader.setAmount(totalAmount);

        orderHeader.setCardNumber(orderPaymentInformation.getCardNumber());
        orderHeader.setExpirationDate(orderPaymentInformation.getExpirationDate());
        orderHeader.setCustomerName(orderPaymentInformation.getCustomerName());
        orderHeader.setCvvNumber(orderPaymentInformation.getCvvNumber());
        orderHeader.setUsername(orderPaymentInformation.getUsername());

        //  Order Header - persist
        entityManager.persist(orderHeader);

        //  Validate OrderLines information
        //List<OrderPurchasedProductInformation> purchasedProducts
        for (OrderPurchasedProductInformation purchasedProduct: purchasedProducts) {
            ArgumentValidationHelper.validateArgumentIsNotNull(purchasedProduct.getProductId(), "product id");
            ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(purchasedProduct.getProductName(), "product name");
            ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(ShoppingCart.convertHexColorToInt(purchasedProduct.getHexColor()), "color decimal RGB value");
            ArgumentValidationHelper.validateDoubleArgumentIsPositiveOrZero(purchasedProduct.getPricePerItem(), "price per item");
            ArgumentValidationHelper.validateNumberArgumentIsPositive(purchasedProduct.getQuantity(), "quantity");

            OrderLines orderLines = new OrderLines(userId, orderNumber,
                    purchasedProduct.getProductId(),
                    purchasedProduct.getProductName(),
                    ShoppingCart.convertHexColorToInt(purchasedProduct.getHexColor()),
                    purchasedProduct.getPricePerItem(),
                    purchasedProduct.getQuantity(),
                    purchasedProduct.getProductImageUrl());

            entityManager.persist(orderLines);
        }

    }

    @Override
    public void updateUserOrderTrackingNumber(long userId, long orderNumber, long shippingTrackingNumber) {
        OrderHeaderPk orderHeaderPk = new OrderHeaderPk(userId, orderNumber);
        OrderHeader orderHeader = entityManager.find(OrderHeader.class, orderHeaderPk);

        //  Check if there is this ShoppingCart already exists
        if (orderHeader != null) {

            //  Existing product in user shopping cart (the same productId + color)
            orderHeader.setShippingTrackingNumber(shippingTrackingNumber);

            entityManager.persist(orderHeader);

            orderPurchaseResponse = new OrderPurchaseResponse(true,
                    "",
                    MESSAGE_SHIPPING_TRACKING_NUMBER_UPDATED_SUCCESSFULLY,
                    orderNumber);

        } else {
            orderPurchaseResponse = new OrderPurchaseResponse(false,
                    "",
                    MESSAGE_ORDER_NOT_FOUND_TRACKING_NUMBER_NOT_UPDATED,
                    orderNumber);
        }
    }
    @Transactional
    @Override
    public boolean restoreToDefaultDb () {

        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        //  region TRUNCATE_CATALOG_TABLES()
        String resultTruncate = (String) entityManager.createNativeQuery("SELECT public.truncate_order_tables()")
                .getSingleResult();
        transaction.commit();
        session.flush();
        session.close();

        StringBuilder sb = new StringBuilder("Database Restore Factory Settings - CATALOG schema truncated successfully. ");
        logger.info("Database Restore Factory Settings - CATALOG schema truncated successfully.");
        System.out.println("Database Restore Factory Settings - CATALOG schema truncated successfully");
        //  endregion
        //Check all tables have 0 rows.
        if (historyOrderHeaderRepository.getAll().size() != 0){
            logger.warn("FAIL- Database Restore Factory Settings FAIL - table 'order_header'");
            return false;
        }
        if (historyOrderLineRepository.getAll().size() != 0){
            logger.warn("FAIL- Database Restore Factory Settings FAIL - table 'order_lines'");
            return false;
        }
        if (shoppingCartRepository.getAll().size() != 0){
            logger.warn("FAIL- Database Restore Factory Settings FAIL - table 'shopping_cart'");
            return false;
        }
            sb.append("Country").append(Constants.COMMA).append(Constants.SPACE);
            System.out.println("Database Restore Factory Settings successful - adv_order");
            logger.info("Database Restore Factory Settings successful - adv_order");

            return true;
        }

    private void validatePaymentMethod(final String paymentMethod, final String argumentInformativeName) {

        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(paymentMethod, "payment method");

        if (paymentMethod.trim().length() == 0) {
            final String messageString = getBlankStringArgumentMessage(argumentInformativeName);
            throw new IllegalArgumentException(messageString);
        }
        else if (!PaymentMethodEnum.contains(paymentMethod)) {
            final String stringMessage = getNotInListArgumentMessage(paymentMethod, argumentInformativeName);
            throw new IllegalArgumentException(stringMessage);
        }
    }

    private void validateTransactionType(final String transactionType, final String argumentInformativeName) {

        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(transactionType, argumentInformativeName);

        if (transactionType.trim().length() == 0) {
            final String messageString = getBlankStringArgumentMessage(argumentInformativeName);
            throw new IllegalArgumentException(messageString);
        }
        else if (TransactionTypeEnum.contains(transactionType)) {
            final String stringMessage = getNotInListArgumentMessage(transactionType, argumentInformativeName);
            throw new IllegalArgumentException(stringMessage);
        }
    }

    private String getBlankStringArgumentMessage(final String argumentInformativeName) {

        assert StringUtils.isNoneBlank(argumentInformativeName);

        final StringBuilder message = new StringBuilder("Could not accept a blank or empty string as argument [");
        message.append(argumentInformativeName);
        message.append("]");
        return message.toString();
    }

    private String getNotInListArgumentMessage(final String argumentValue, final String argumentInformativeName) {

        assert StringUtils.isNoneBlank(argumentInformativeName);

        final StringBuilder message = new StringBuilder("Could not process ")
                .append(argumentInformativeName)
                .append(" \'")
                .append(argumentValue)
                .append("\' not in list as an argument");
        return message.toString();
    }
}
