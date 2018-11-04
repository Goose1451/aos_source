package com.advantage.order.store.services;

import ShipExServiceClient.*;
import com.advantage.common.Constants;
import com.advantage.common.Url_resources;
import com.advantage.common.enums.PaymentMethodEnum;
import com.advantage.common.enums.ResponseEnum;
import com.advantage.common.utils.LoggerUtils;
import com.advantage.order.store.dao.HistoryOrderLineRepository;
import com.advantage.order.store.dao.HistoryOrderHeaderRepository;
import com.advantage.order.store.dao.OrderManagementRepository;
import com.advantage.order.store.dao.ShoppingCartRepository;
import com.advantage.order.store.dto.*;
import com.advantage.order.store.model.*;
import com.advantage.root.util.ArgumentValidationHelper;
import com.advantage.root.util.JsonHelper;
import com.advantage.root.util.RestApiHelper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Binyamin Regev on 07/01/2016.
 */
@Service
public class OrderManagementService {

    public static final String MESSAGE_ORDER_COMPLETED_SUCCESSFULLY = "order completed successfully";
    public static final String ERROR_SHIPEX_GET_SHIPPING_COST_REQUEST_IS_EMPTY = "Get shipping cost request is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY = "Get ShipEx response failure, currency is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT = "Get ShipEx response failure, shipping cost amount invalid empty ";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH = "Get ShipEx response failure, transaction type mismatch";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY = "Get ShipEx response failure, transaction date is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY = "Get ShipEx response failure, transaction reference is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH = "Get ShipEx response failure, invalid transaction reference length";

    final String charset = "UTF-8";
    final String contentType = "application/json";

    private static AtomicLong orderNumber;
    private double totalAmount = 0.0;

    private static final Logger logger = Logger.getLogger(OrderManagementService.class);

    @Autowired
    @Qualifier("orderManagementRepository")
    public OrderManagementRepository orderManagementRepository;

    @Autowired
    @Qualifier("historyOrderHeaderRepository")
    public HistoryOrderHeaderRepository historyOrderHeaderRepository;

    @Autowired
    @Qualifier("historyOrderLineRepository")
    public HistoryOrderLineRepository historyOrderLineRepository;

    @Autowired
    @Qualifier("shoppingCartRepository")
    public ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;
    //private ShoppingCartService shoppingCartService = new ShoppingCartService();

    public OrderManagementService() {

        long result = new Date().getTime();

        //  If more than 10 digits than take the 10 right-most digits
        if (result > 9999999999L) {
            result %= 10000000000L;
        }

        //  10 - 10 = 0 => Math.pow(10, 0) = 1 => result *= 1 = result
        int power = 10 - String.valueOf(result).length();
        result *= Math.pow(10, power);

        orderNumber = new AtomicLong(result);
    }

    public ShippingCostResponse getShippingCostFromShipEx(ShippingCostRequest costRequest) {

        ShippingCostResponse costResponse = null;

        if (costRequest == null) {
            return generateShippingCostResponseError(costRequest.getSETransactionType().value(), ERROR_SHIPEX_GET_SHIPPING_COST_REQUEST_IS_EMPTY);
        }

        URL urlWsdlLocation = Url_resources.getUrlSoapShipEx();

        //QName serviceName = new QName("https://www.AdvantageOnlineBanking.com/ShipEx/", "ShipExPortService");
        //ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation, serviceName);
        ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation);

        ShipExPort shipExPort = shipExPortService.getShipExPortSoap11();

        costResponse = shipExPort.shippingCost(costRequest);

        if (!costResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
            if (logger.isInfoEnabled()) {
                logger.info("Response returned \'" + costResponse.getCode() + "\', Reason: \'" + costResponse.getReason() + "\'");
            }
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType().value(), "Shipping Express: getShippingCost() --> Response returned \'" + costResponse.getCode() + "\', Reason: \'" + costResponse.getReason() + "\'");
        } else if (costResponse.getAmount().isEmpty()) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType().value(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
        } else if (costResponse.getCurrency().isEmpty()) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType().value(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
        } else if (!costResponse.getSETransactionType().value().equalsIgnoreCase(costRequest.getSETransactionType().value())) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType().value(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
        } else {
            costResponse.setReason(ResponseEnum.OK.getStringCode());
        }

        return costResponse;
    }

    private ShippingCostResponse generateShippingCostResponseError(String transactionType, String errorText) {

        ShippingCostResponse costResponse = new ShippingCostResponse();

        costResponse.setSETransactionType(ShippingCostTransactionType.SHIPPING_COST);
        costResponse.setReason(errorText);
        costResponse.setAmount("0");
        costResponse.setCode(ResponseEnum.ERROR.getStringCode());

        return costResponse;
    }

    /**
     * Do the order purchase process.
     * Evgeny: CHILL!!! This is just for me!!!!!!!
     * <p>
     * (V)  Step #1: Get products info
     * (V)  Step #2: Generate OrderNumber.
     * (V)  Step #3: Do payment (MasterCredit or SafePay) - send REST POST request, receive Payment confirmation number.
     * (V)  Step #4: INSERT: Save order header and lines (NO TRACKING NUMBER YET!!!)
     * (V)  Step #5: Send request to get Tracking Number from ShipEx.
     * (V)  Step #6: UPDATE: Save shipping express tracking number to order header.
     */
    @Transactional
    public OrderPurchaseResponse doPurchase(long userId, OrderPurchaseRequest purchaseRequest) {
        //Moti Ostrovski: reset total Amount before new purchase
        totalAmount = 0.0;

        long orderNumber = 0;

        OrderPurchaseResponse purchaseResponse = new OrderPurchaseResponse();

        OrderShippingInformation shippingInfo = purchaseRequest.getOrderShippingInformation();
        OrderPaymentInformation paymentInfo = purchaseRequest.getOrderPaymentInformation();

        //  Step #1: Get products info
        List<OrderPurchasedProductInformation> purchasedProducts = getPurchasedProductsInformation(userId, purchaseRequest.getPurchasedProducts());
        //  Sort purchased products
        Collections.sort(purchasedProducts,
                new Comparator<OrderPurchasedProductInformation>() {
                    public int compare(OrderPurchasedProductInformation product1, OrderPurchasedProductInformation product2) {
                        return (int) (product1.getProductId() - product2.getProductId());
                    }
                });

        //  Step #2: Generate order number
        orderNumber = generateOrderNumberNextValue();

        //  Step #3: Do payment MasterCredit / SafePay
        boolean paymentSuccessful = true;
        if (purchaseRequest.getOrderPaymentInformation().getPaymentMethod().equals(PaymentMethodEnum.MASTER_CREDIT.getName())) {
            MasterCreditRequest masterCreditRequest = new MasterCreditRequest(
                    paymentInfo.getTransactionType(),
                    Long.valueOf(paymentInfo.getCardNumber()),
                    paymentInfo.getExpirationDate(),
                    paymentInfo.getCustomerName(),
                    paymentInfo.getCustomerPhone(),
                    Integer.valueOf(paymentInfo.getCvvNumber()),
                    paymentInfo.getTransactionDate(),
                    Long.valueOf(paymentInfo.getAccountNumber()),
                    totalAmount,
                    paymentInfo.getCurrency());

            MasterCreditResponse masterCreditResponse = payWithMasterCredit(masterCreditRequest);

            // Check "masterCreditResponse"
            if (masterCreditResponse.getResponseCode().equalsIgnoreCase(ResponseEnum.APPROVED.getStringCode())) {
                paymentInfo.setReferenceNumber(masterCreditResponse.getReferenceNumber());
                purchaseResponse.setPaymentConfirmationNumber(masterCreditResponse.getReferenceNumber());
            } else {
                paymentSuccessful = false;
                purchaseResponse.setSuccess(paymentSuccessful);
                purchaseResponse.setCode(masterCreditResponse.getResponseCode());
                purchaseResponse.setReason(masterCreditResponse.getResponseReason());
                purchaseResponse.setOrderNumber(orderNumber);
                purchaseResponse.setPaymentConfirmationNumber(masterCreditResponse.getReferenceNumber());
                purchaseResponse.setTrackingNumber(0L);
            }
        } else if (purchaseRequest.getOrderPaymentInformation().getPaymentMethod().equals(PaymentMethodEnum.SAFE_PAY.getName())) {
            SafePayRequest safePayRequest = new SafePayRequest(
                    paymentInfo.getTransactionType(),
                    paymentInfo.getUsername(),
                    paymentInfo.getPassword(),
                    paymentInfo.getCustomerPhone(),
                    paymentInfo.getTransactionDate(),
                    Long.valueOf(paymentInfo.getAccountNumber()),
                    totalAmount,
                    paymentInfo.getCurrency());

            SafePayResponse safePayResponse = payWithSafePay(safePayRequest);

            if (safePayResponse.getResponseCode().equalsIgnoreCase(ResponseEnum.APPROVED.getStringCode())) {
                paymentInfo.setReferenceNumber(safePayResponse.getReferenceNumber());
                purchaseResponse.setPaymentConfirmationNumber(safePayResponse.getReferenceNumber());
            } else {
                paymentSuccessful = false;
                purchaseResponse.setSuccess(paymentSuccessful);
                purchaseResponse.setCode(safePayResponse.getResponseCode());
                purchaseResponse.setReason(safePayResponse.getResponseReason());
                purchaseResponse.setOrderNumber(orderNumber);
                purchaseResponse.setPaymentConfirmationNumber(safePayResponse.getReferenceNumber());
                purchaseResponse.setTrackingNumber(0L);
            }
        }

        if (paymentSuccessful) {
            //  Set order header and lines for data persist
            long orderTimestamp = new Date().getTime();

            //  Step #4: INSERT: Save order header and lines (NO TRACKING NUMBER YET!!!)
            orderManagementRepository.addUserOrder(userId, orderNumber, orderTimestamp, totalAmount,
                    shippingInfo,
                    paymentInfo,
                    purchasedProducts);

            //  Step #5: Get Shipping Express tracking number
            SEAddress seAddress = new SEAddress();
            if (shippingInfo.getAddress().length() > 50) {
                seAddress.setAddressLine1(shippingInfo.getAddress().substring(0, 49));
                seAddress.setAddressLine2(shippingInfo.getAddress().substring(50));
            } else {
                seAddress.setAddressLine1(shippingInfo.getAddress());
                seAddress.setAddressLine2("");
            }

            seAddress.setCity(shippingInfo.getCity());
            seAddress.setPostalCode(shippingInfo.getPostalCode());
            seAddress.setState(shippingInfo.getState());
            seAddress.setCountry(shippingInfo.getCountryCode());

            //  Assemble shipping products list: product name and quantity summary by product-id
            SEProducts products = new SEProducts();
            long productId = 0;
            Product product = null;
            for (OrderPurchasedProductInformation purchasedProduct : purchasedProducts) {
                if (productId != purchasedProduct.getProductId()) {
                    product = new Product();
                    product.setProductName(purchasedProduct.getProductName());
                    product.setProductQuantity(purchasedProduct.getQuantity());
                    products.getProduct().add(product);

                    productId = purchasedProduct.getProductId();

                } else {
                    int index = products.getProduct().size() - 1;
                    products.getProduct().get(index).setProductQuantity(products.getProduct().get(index).getProductQuantity() + purchasedProduct.getQuantity());
                }
            }

            PlaceShippingOrderRequest orderRequest = new PlaceShippingOrderRequest();

            orderRequest.setSEAddress(seAddress);
            orderRequest.setSEProducts(products);
            orderRequest.setOrderNumber(Long.toString(orderNumber));
            orderRequest.setSECustomerName(shippingInfo.getCustomerName());
            orderRequest.setSECustomerPhone(shippingInfo.getCustomerPhone());
            orderRequest.setSETransactionType(PlaceOrderTransactionType.PLACE_SHIPPING_ORDER);

            PlaceShippingOrderResponse orderResponse = placeShippingOrder(orderRequest);

            //  Check response
            if (orderResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
                //  Step #6: UPDATE: Save shipping express tracking number to order header.
                updateUserOrderTrackingNumber(userId, orderNumber, Long.valueOf(orderResponse.getTransactionReference()));

                purchaseResponse.setSuccess(true);
                purchaseResponse.setCode(ResponseEnum.OK.getStringCode());
                purchaseResponse.setReason(MESSAGE_ORDER_COMPLETED_SUCCESSFULLY);
                purchaseResponse.setOrderNumber(orderNumber);
                purchaseResponse.setTrackingNumber(Long.valueOf(orderResponse.getTransactionReference()));
                if (logger.isInfoEnabled()) {
                    logger.info(orderResponse.toString());
                    logger.info(purchaseResponse);
                }
            } else {
                purchaseResponse.setSuccess(false);
                purchaseResponse.setCode(orderResponse.getCode());
                purchaseResponse.setReason(orderResponse.getReason());
                purchaseResponse.setOrderNumber(orderNumber);
                purchaseResponse.setTrackingNumber(Long.valueOf(orderResponse.getTransactionReference()));
                logger.warn(orderResponse.toString());
                logger.warn(purchaseResponse);
            }
        }

        return purchaseResponse;
    }

    /**
     * For each purchased product:
     * Retrieve product-name, product-color-name and price-per-item.
     */
    private List<OrderPurchasedProductInformation> getPurchasedProductsInformation(long userId,
                                                                                   List<ShoppingCartDto> cartProducts) {
        List<OrderPurchasedProductInformation> purchasedProducts = new ArrayList<>();

        for (ShoppingCartDto cartProduct : cartProducts) {

            //ShoppingCartResponseDto.CartProduct product =
            //        shoppingCartRepository.getCartProductDetails(cartProduct.getProductId(),
            //                                                    cartProduct.getHexColor().toUpperCase());
            ShoppingCartResponseDto.CartProduct product = shoppingCartService.getCartProductDetails(cartProduct.getProductId(),
                    cartProduct.getHexColor().toUpperCase(), cartProduct.getQuantity());

            if (!product.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {
                /*  Add a product to user shopping cart response class  */
                purchasedProducts.add(new OrderPurchasedProductInformation(cartProduct.getProductId(),
                        product.getProductName(),
                        product.getColor().getCode(),
                        product.getColor().getName(),
                        product.getPrice(),
                        cartProduct.getQuantity(),
                        product.getImageUrl()));

                totalAmount += product.getPrice() * cartProduct.getQuantity();

            } else {
                //  Product from cart was not found in CATALOG
                purchasedProducts.add(new OrderPurchasedProductInformation(cartProduct.getProductId(),
                        Constants.NOT_FOUND,
                        "000000",
                        "BLACK",
                        -999999.99,                     //  Price-per-item
                        cartProduct.getQuantity(),
                        ""));
            }
        }

        return purchasedProducts;
    }

    /**
     * Generate the next value of order number.
     */
    public static long generateOrderNumberNextValue() {
        return orderNumber.getAndIncrement();
    }

    /**
     * Send REST POST request to pay for order using <b>MasterCredit</b> service.
     * Request JSON:
     * {
     * "MCCVVNumber": 0,
     * "MCCardNumber": 0,
     * "MCCustomerName": "string",
     * "MCCustomerPhone": "string",
     * "MCExpirationDate": "string",
     * "MCRecevingAmount.Value": 0,
     * "MCRecevingCard.AccountNumber": 0,
     * "MCRecevingCard.Currency": "string",
     * "MCTransactionDate": "string",
     * "MCTransactionType": "string"
     * }
     *
     * @param masterCreditRequest
     * @return
     */
    public MasterCreditResponse payWithMasterCredit(MasterCreditRequest masterCreditRequest) {
        URL urlPayment = null;

        MasterCreditResponse masterCreditResponse = new MasterCreditResponse();

        try {
            urlPayment = new URL(Url_resources.getUrlMasterCredit(), "payments/payment");
            logger.info("urlPayment=" + urlPayment.toString());
            HttpURLConnection conn = (HttpURLConnection) urlPayment.openConnection();

            if (logger.isTraceEnabled()) {
                try {
                    Object connectedStatus = FieldUtils.readField(conn, "connected", true);
                    logger.trace("HttpURLConnection.connected=" + connectedStatus);
                } catch (Throwable e) {
                    logger.warn("Read \"connected\" field by reflection for HttpURLConnection failed", e);
                }
            }

            //region WORK AROUND (encapsulation breaking - REMOVE all region)
            try {
                boolean connected = (Boolean) FieldUtils.readField(conn, "connected", true);
                if (connected) {
                    conn = null;
                    conn = (HttpURLConnection) urlPayment.openConnection();
                    if (logger.isTraceEnabled()) {
                        try {
                            Object connectedStatus = FieldUtils.readField(conn, "connected", true);
                            logger.trace("After WORK AROUND HttpURLConnection.connected=" + connectedStatus);
                        } catch (Throwable e) {
                            logger.warn("After WORK AROUND \"connected\" field by reflection for HttpURLConnection failed", e);
                        }
                    }
                }
            } catch (Throwable e) {
                logger.warn("WORK AROUND: Read \"connected\" field by reflection for HttpURLConnection failed", e);
            }
            //endregion

            conn.setDoOutput(true);
            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            conn.setRequestProperty(HttpHeaders.USER_AGENT, "AdvantageService/order");

            String input = "{" + "\"MCCVVNumber\":" + masterCreditRequest.getCvvNumber() + "," +
                    "\"MCCardNumber\":\"" + masterCreditRequest.getCardNumber() + "\"," +
                    "\"MCCustomerName\": \"" + masterCreditRequest.getCustomerName() + "\"," +
                    "\"MCCustomerPhone\": \"" + masterCreditRequest.getCustomerPhone() + "\"," +
                    "\"MCExpirationDate\": \"" + masterCreditRequest.getExpirationDate() + "\"," +
                    "\"MCRecevingAmount.Value\": " + masterCreditRequest.getValue() + "," +
                    "\"MCRecevingCard.AccountNumber\": " + masterCreditRequest.getAccountNumber() + "," +
                    "\"MCRecevingCard.Currency\": \"" + masterCreditRequest.getCurrency() + "\"," +
                    "\"MCTransactionDate\": \"" + masterCreditRequest.getTransactionDate() + "\"," +
                    "\"MCTransactionType\": \"" + masterCreditRequest.getTransactionType() + "\"" +
                    "}";

            logger.debug(input);
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_CONFLICT) {
                RuntimeException e = new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + Constants.SPACE + "MasterCredit JSON string sent: '" + input + "'");
                if (logger.isDebugEnabled()) {
                    logger.fatal(conn.getResponseCode(), e);
                } else {
                    logger.fatal(LoggerUtils.getMinThrowableDescription(conn.getResponseCode(), e));
                }
                throw e;
            }

            BufferedReader br = null;

            if(conn.getResponseCode() == HttpURLConnection.HTTP_CONFLICT)
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            else
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            StringBuilder sb = new StringBuilder();
            StringBuffer sbLog = new StringBuffer("Output from Server...").append(System.lineSeparator());
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
                sbLog.append("\t").append(output).append(System.lineSeparator());
            }
            logger.debug(sbLog.toString());

            Map<String, Object> jsonMap = JsonHelper.jsonStringToMap(sb.toString());

            masterCreditResponse.setResponseCode((String) jsonMap.get("MCResponse.Code"));
            masterCreditResponse.setResponseReason((String) jsonMap.get("MCResponse.Reason"));
            masterCreditResponse.setReferenceNumber(Long.valueOf(String.valueOf(jsonMap.get("MCRefNumber"))));
            masterCreditResponse.setTransactionType((String) jsonMap.get("TransactionType"));
            masterCreditResponse.setTransactionDate((String) jsonMap.get("TransactionDate"));

            conn.disconnect();
        } catch (MalformedURLException e) {
            if (logger.isDebugEnabled()) {
                logger.fatal(masterCreditResponse, e);
            } else {
                logger.fatal(LoggerUtils.getMinThrowableDescription(masterCreditResponse, e));
            }
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.fatal(masterCreditResponse, e);
            } else {
                logger.fatal(LoggerUtils.getMinThrowableDescription(masterCreditResponse, e));
            }
        }

        return masterCreditResponse;
    }

    /**
     * Send REST POST request to pay for order using <b>SafePay</b> service.
     *
     * @param safePayRequest
     * @return
     */
    public SafePayResponse payWithSafePay(SafePayRequest safePayRequest) {
        URL urlPayment = null;

        SafePayResponse safePayResponse = new SafePayResponse();

        try {
            urlPayment = new URL(Url_resources.getUrlSafePay(), "payments/payment");

            HttpURLConnection conn = (HttpURLConnection) urlPayment.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            conn.setRequestProperty(HttpHeaders.USER_AGENT, "AdvantageService/order");

            //  region SafePay PAYMENT request
            String input = "{" +
                    "\"SPCustomerPhone\": \"" + safePayRequest.getCustomerPhone() + "\"," +
                    "\"SPPassword\": \"" + safePayRequest.getPassword() + "\"," +
                    "\"SPReceivingAmount.Currency\": \"" + safePayRequest.getCurrency() + "\"," +
                    "\"SPReceivingAmount.Value\": " + safePayRequest.getValue() + "," +
                    "\"SPReceivingCard.AccountNumber\": " + safePayRequest.getAccountNumber() + "," +
                    "\"SPTransactionDate\": \"" + safePayRequest.getTransactionDate() + "\"," +
                    "\"SPTransactionType\": \"" + safePayRequest.getTransactionType() + "\"," +
                    "\"SPUserName\":\"" + safePayRequest.getUserName() + "\"" +
                    "}";
            //  endregion

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                safePayResponse.setResponseCode("Rejected");
                if(safePayRequest.getUserName().equals(safePayRequest.getPassword()))
                    safePayResponse.setResponseReason("Error! Username and password cannot be identical");
                else
                    safePayResponse.setResponseReason("illegal username and/or password.");
                conn.disconnect();
                return safePayResponse;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder sb = new StringBuilder();
            StringBuffer sbLog = new StringBuffer("Output from Server ....").append(System.lineSeparator());
            while ((output = br.readLine()) != null) {
                sb.append(output);
                sbLog.append("\t").append(output).append(System.lineSeparator());
            }
            logger.debug(sbLog.toString());

            Map<String, Object> jsonMap = JsonHelper.jsonStringToMap(sb.toString());

            safePayResponse.setReferenceNumber(Long.valueOf(String.valueOf(jsonMap.get("SPRefNumber"))));
            safePayResponse.setResponseCode((String) jsonMap.get("SPResponse.Code"));
            safePayResponse.setResponseReason((String) jsonMap.get("SPResponse.Reason"));
            safePayResponse.setTransactionType((String) jsonMap.get("SPTransactionType"));
            safePayResponse.setTransactionDate((String) jsonMap.get("TransactionDate"));

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return safePayResponse;
    }

    /**
     * Send SOAP request for ShipEx tracking number and receive it in response.
     * @param orderRequest Shipping Express request for tracking number.
     * @return Shipping Express Tracking Number in {@link PlaceShippingOrderResponse}.
     */
    public PlaceShippingOrderResponse placeShippingOrder(PlaceShippingOrderRequest orderRequest) {

        PlaceShippingOrderResponse orderResponse = null;

        if (orderRequest == null) {
            orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping order request is empty");
        }

        if (orderResponse == null) {
            URL urlWsdlLocation = Url_resources.getUrlSoapShipEx();

            //QName serviceName = new QName("https://www.AdvantageOnlineBanking.com/ShipEx/", "ShipExPortService");
            //ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation, serviceName);
            ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation);

            ShipExPort shipExPort = shipExPortService.getShipExPortSoap11();

            orderResponse = shipExPort.placeShippingOrder(orderRequest);

            if (!orderResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
                logger.info("Response returned \'" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping Express: placeShippingOrder() --> Response returned '" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
            } else if (orderResponse.getTransactionDate().isEmpty()) {
                logger.warn(ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
            } else if (orderResponse.getTransactionReference().isEmpty()) {
                logger.warn(ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
            } else if (orderResponse.getTransactionReference().length() != 10) {
                logger.warn(ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
            } else if (!orderResponse.getSETransactionType().value().equalsIgnoreCase(orderRequest.getSETransactionType().value())) {
                logger.warn(ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType().value(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
            }
        }
        return orderResponse;
    }

    /**
     * Update tracking number in {@code OrderHeder} entity.
     *
     * @param userId
     * @param orderNumber
     * @param shippingTrackingNumber
     */
    public void updateUserOrderTrackingNumber(long userId, long orderNumber, long shippingTrackingNumber) {
        orderManagementRepository.updateUserOrderTrackingNumber(userId, orderNumber, shippingTrackingNumber);
    }

    private PlaceShippingOrderResponse generatePlaceShippingOrderResponseError(String transactionType, String errorText) {

        PlaceShippingOrderResponse orderResponse = new PlaceShippingOrderResponse();

        orderResponse.setSETransactionType(PlaceOrderTransactionType.PLACE_SHIPPING_ORDER);
        orderResponse.setCode(ResponseEnum.ERROR.getStringCode());
        orderResponse.setReason(errorText);
        orderResponse.setTransactionDate("");
        orderResponse.setTransactionReference("");

        return orderResponse;
    }

//    public DemoAppConfigGetAllParametersResponse getAllDemoAppConfigParameters() {
//
//        URL urlWsdlLocation = Url_resources.getUrlSoapAccount();
//
//        AccountServicePortService accountServicePostService = new AccountServicePortService(urlWsdlLocation);
//
//        AccountServicePort accountServicePort = accountServicePostService.getAccountServicePortSoap11();
//
//        DemoAppConfigGetAllParametersRequest request = new DemoAppConfigGetAllParametersRequest();
//
//        DemoAppConfigGetAllParametersResponse response = accountServicePort.demoAppConfigGetAllParameters(request);
//
//        return response;
//    }
//
//    public DemoAppConfigGetParametersByToolResponse getDemoAppConfigParametersByTool(String toolName) {
//
//        URL urlWsdlLocation = Url_resources.getUrlSoapAccount();
//
//        AccountServicePortService accountServicePostService = new AccountServicePortService(urlWsdlLocation);
//
//        AccountServicePort accountServicePort = accountServicePostService.getAccountServicePortSoap11();
//
//        DemoAppConfigGetParametersByToolRequest request = new DemoAppConfigGetParametersByToolRequest();
//        request.setToolName(toolName);
//        DemoAppConfigGetParametersByToolResponse response = accountServicePort.demoAppConfigGetParametersByTool(request);
//
//        return response;
//    }

    //  region History Orders
    /*
    get orders history by userID or orderID
    not set any value(userID =0, orderID=0 or not set in REST) => get all
    currently order saved by unique orderID
    if change to unique per user the option get by userID and orderID available
     */

    public RestoredefaultDBsettingsResponse dbRestoreFactorySettings (){

        boolean result = orderManagementRepository.restoreToDefaultDb();

        if(!result){
            RestoredefaultDBsettingsResponse response = new RestoredefaultDBsettingsResponse(false, "internal error check server logs ");
            return response;
        }

        RestoredefaultDBsettingsResponse response = new RestoredefaultDBsettingsResponse(true, "default setting restored");

        return response;
    }
    public HistoryOrderResponseDto getOrdersHistory(Long userId, Long orderId) {

        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(orderId, "order id");

        HistoryOrderResponseDto historyOrderResponseDto = new HistoryOrderResponseDto();
        List<OrderHeader> orderHistoryHeaders = new ArrayList<OrderHeader>();

        if ((userId == 0) && (orderId == 0)) {
            orderHistoryHeaders = historyOrderHeaderRepository.getAll();
        } else if ((userId == 0) && (orderId > 0)) {
            orderHistoryHeaders = historyOrderHeaderRepository.getOrdersHeaderByOrderId(orderId);
        } else if ((orderId == 0) && (userId > 0)) {
            orderHistoryHeaders = historyOrderHeaderRepository.getOrdersHeaderByUserId(userId);
        } else if ((orderId > 0) && (userId > 0)) {
            orderHistoryHeaders = historyOrderHeaderRepository.getOrdersHeaderByOrderIdAndUserId(orderId, userId);
        }
        if (orderHistoryHeaders.size() > 0) {
            try {

                orderHistoryHeaders.forEach(order -> {
                    HistoryOrderHeaderDto historyOrderHeaderDto = new HistoryOrderHeaderDto();
                    //get products by orderID
                    List<OrderLines> orderLines = historyOrderLineRepository.getHistoryOrdersLinesByUserId(order.getOrderNumber());

                    //set order fields
                    historyOrderHeaderDto.setOrderNumber(order.getOrderNumber());
                    historyOrderHeaderDto.setOrderTimestamp(order.getOrderTimestamp());
                    historyOrderHeaderDto.setShippingTrackingNumber(order.getShippingTrackingNumber());
                    historyOrderHeaderDto.setPaymentMethod(order.getPaymentMethod());
                    historyOrderHeaderDto.setOrderTotalSum(order.getAmount());
                    historyOrderHeaderDto.setOrderShipingCost(order.getShippingCost());
                    historyOrderHeaderDto.setShippingAddress(order.getShippingAddress());

                    //set user
                    historyOrderHeaderDto.setCustomer(new HistoryOrderAccountDto(order.getUserId(), order.getCustomerName(), order.getCustomerPhone()));

                    //set products
                    orderLines.forEach(product -> {
                        historyOrderHeaderDto.addOrderHistoryProductDto(new HistoryOrderProductDto(product.getProductId(), product.getProductName(), ShoppingCart.convertIntColorToHex(product.getProductColor()),
                                product.getPricePerItem(), product.getQuantity(), product.getOrderNumber()));
                    });
                    historyOrderResponseDto.addOrderHistoryDto(historyOrderHeaderDto);
                });
            } catch (Exception e) {
                logger.error(historyOrderResponseDto, e);
                return null;
            }
        }
        return historyOrderResponseDto;
    }

    public HistoryOrderLinesDto getHistoryOrdersLines(Long userId) {

        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");

        List<OrderLines> lines = new ArrayList<>();

        if (userId == 0) {
            lines = historyOrderLineRepository.getAll();
        } else {
            lines = historyOrderLineRepository.getHistoryOrdersLinesByUserId(userId);
        }

        if (lines.size() == 0) {
            return new HistoryOrderLinesDto();
        }

        HistoryOrderLinesDto historyOrderLinesDto = new HistoryOrderLinesDto();
        List<HistoryOrderLineDto> orderLines = new ArrayList<HistoryOrderLineDto>();

        historyOrderLinesDto.setUserId(userId);
        historyOrderLinesDto.setOrderNumber(lines.get(0).getOrderNumber());
        historyOrderLinesDto.setOrderLines(new ArrayList<HistoryOrderLineDto>());

        long orderNumber = 0L;
        long orderTimestamp = -1L;

        for (OrderLines line: lines) {
            if (orderNumber != line.getOrderNumber()) {
                orderNumber = line.getOrderNumber();
                OrderHeader orderHeader = historyOrderHeaderRepository.find(userId, orderNumber);

                if (orderHeader != null) {
                    orderTimestamp = orderHeader.getOrderTimestamp();

                    //Date date = new Date(orderTimestamp);
                    //
                    //String shortDate = "DefaultFormatLocale (SHORT): " + DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
                    //String mediumDate = "DefaultFormatLocale (MEDIUM): " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date);
                    //String longDate = "DefaultFormatLocale (LONG): " + DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(date);
                    //String fullDate = "DefaultFormatLocale (FULL): " + DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(date);
                    //
                    //shortDate = "United Kingdom (SHORT): " + DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK).format(date);
                    //mediumDate = "United Kingdom (MEDIUM): " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK).format(date);
                    //longDate = "United Kingdom (LONG): " + DateFormat.getDateInstance(DateFormat.LONG, Locale.UK).format(date);
                    //fullDate = "United Kingdom (FULL): " + DateFormat.getDateInstance(DateFormat.FULL, Locale.UK).format(date);
                    //
                    //shortDate = "United States (SHORT): " + DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).format(date);
                    //mediumDate = "United States (MEDIUM): " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(date);
                    //longDate = "United States (LONG): " + DateFormat.getDateInstance(DateFormat.LONG, Locale.US).format(date);
                    //fullDate = "United States (FULL): " + DateFormat.getDateInstance(DateFormat.FULL, Locale.US).format(date);
                    //
                    //shortDate = "Japan (SHORT): " + DateFormat.getDateInstance(DateFormat.SHORT, Locale.JAPAN).format(date);
                    //mediumDate = "Japan (MEDIUM): " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).format(date);
                    //longDate = "Japan (LONG): " + DateFormat.getDateInstance(DateFormat.LONG, Locale.JAPAN).format(date);
                    //fullDate = "Japan (FULL): " + DateFormat.getDateInstance(DateFormat.FULL, Locale.JAPAN).format(date);
                }
            }
//            ProductDto dto = shoppingCartService.getProductDtoDetails(line.getProductId());
//            String productImageUrl = "";
//            if (dto != null) {
//                if ((dto.getImageUrl() != null) && (!dto.getImageUrl().isEmpty())) {
//                    productImageUrl = dto.getImageUrl();
//                }
//            }
            historyOrderLinesDto.addOrderLine(
                    line.getUserId(),
                    line.getOrderNumber(),
                    orderTimestamp,
                    line.getProductId(),
                    line.getProductImageUrl(),
                    line.getProductName(),
                    line.getProductColor(),
                    line.getPricePerItem(),
                    line.getQuantity());

        }

        //  region Generate_memory_leak
        String stringValue = RestApiHelper.getDemoAppConfigParameterValue("Generate_memory_leak");
        if ((stringValue != null) && (!stringValue.isEmpty()) && (!stringValue.equals("0"))) {
            logger.info("Generating Memory Leak!!!!");
            Feature1779OrdersHistoryLeak ordersHistory = new Feature1779OrdersHistoryLeak();
            try {
                ordersHistory.StartLeaking();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }

//            ordersHistory.generateMemoryLeak(Integer.valueOf(stringValue).intValue());
        }

        //  endregion

        return historyOrderLinesDto;
    }
    //  endregion

    @Override
    public String toString() {
        return "OrderManagementService{" +
                "totalAmount=" + totalAmount +
                ", orderManagementRepository=" + orderManagementRepository +
                ", historyOrderHeaderRepository=" + historyOrderHeaderRepository +
                ", historyOrderLineRepository=" + historyOrderLineRepository +
                ", shoppingCartRepository=" + shoppingCartRepository +
                ", shoppingCartService=" + shoppingCartService +
                '}';
    }

    private static void tmpLogAllObject(HttpURLConnection conn) {
        List<Field> allFieldsList = FieldUtils.getAllFieldsList(conn.getClass());
        List<Field> staticFields = allFieldsList.stream().filter(tempField -> (tempField.getModifiers() & Modifier.STATIC) == Modifier.STATIC).collect(Collectors.toList());
        List<Field> nonStaticFields = allFieldsList.stream().filter(tempField -> (tempField.getModifiers() & Modifier.STATIC) != Modifier.STATIC).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        //staticFields.stream().forEach(temp);
        //TODO-EVG continue
    }

    public HistoryOrderLinesDto removeOrder(Long userId, Long orderId){
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        long orderNumber = 0L;
        long orderTimestamp = -1L;

        List<OrderLines> lines;
        HistoryOrderLinesDto historyOrderLinesDto = new HistoryOrderLinesDto();

        if (userId == 0) {
            lines = historyOrderLineRepository.getAll();
        } else {
            boolean b = historyOrderHeaderRepository.removeOrder(userId, orderId);
            lines = historyOrderLineRepository.removeOrder(userId, orderId);
        }

        if (lines.size() == 0) {
            return historyOrderLinesDto;
        }

        historyOrderLinesDto.setUserId(userId);
        historyOrderLinesDto.setOrderNumber(lines.get(0).getOrderNumber());
        historyOrderLinesDto.setOrderLines(new ArrayList<HistoryOrderLineDto>());

        for (OrderLines line: lines) {
            if (orderNumber != line.getOrderNumber()) {
                orderNumber = line.getOrderNumber();
                OrderHeader orderHeader = historyOrderHeaderRepository.find(userId, orderNumber);

                if (orderHeader != null) {
                    orderTimestamp = orderHeader.getOrderTimestamp();

                    historyOrderLinesDto.addOrderLine(
                            line.getUserId(),
                            line.getOrderNumber(),
                            orderTimestamp,
                            line.getProductId(),
                            line.getProductImageUrl(),
                            line.getProductName(),
                            line.getProductColor(),
                            line.getPricePerItem(),
                            line.getQuantity());
                }
            }
        }
        return historyOrderLinesDto;
    }
    //Will return true if no orders appear for user, even if none were deleted.
    public OrderHistoryRemoveDto removeAllOrdersHistory(Long userId){
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");


        OrderHistoryRemoveDto orderHistoryRemoveDto = new OrderHistoryRemoveDto();
        boolean whereOrdersHeaderRemovedSuccessfully = false;
        boolean whereOrdersLinesRemovedSuccessfully = false;

            whereOrdersHeaderRemovedSuccessfully = historyOrderHeaderRepository.removeAllOrdersHeaders(userId);
            whereOrdersLinesRemovedSuccessfully = historyOrderLineRepository.removeAllOrdersLinesForUser(userId);

        if (whereOrdersHeaderRemovedSuccessfully == true && whereOrdersLinesRemovedSuccessfully == true) {

            orderHistoryRemoveDto.setSuccessfulOrderHeaderDelete(true);
            orderHistoryRemoveDto.setSuccessfulOrderLineDelete(true);
            orderHistoryRemoveDto.setSuccess(true);
            return orderHistoryRemoveDto;
        }
        else{

            orderHistoryRemoveDto.setSuccessfulOrderHeaderDelete(whereOrdersHeaderRemovedSuccessfully);
            orderHistoryRemoveDto.setSuccessfulOrderLineDelete(whereOrdersLinesRemovedSuccessfully);
            orderHistoryRemoveDto.setSuccess(false);
            return orderHistoryRemoveDto;
        }

    }
}
