package com.advantage.order.store.services;

import AccountServiceClient.*;
import ShipExServiceClient.*;
import com.advantage.common.Constants;
import com.advantage.common.Url_resources;
import com.advantage.common.enums.PaymentMethodEnum;
import com.advantage.common.enums.ResponseEnum;
import com.advantage.order.store.dao.OrderHistoryHeaderManagementRepository;
import com.advantage.order.store.dao.OrderHistoryLineManagementRepository;
import com.advantage.order.store.dao.OrderManagementRepository;
import com.advantage.order.store.dao.ShoppingCartRepository;
import com.advantage.order.store.dto.*;
import com.advantage.order.store.model.OrderHeader;
import com.advantage.order.store.model.OrderLines;
import com.advantage.order.store.model.ShoppingCart;
import com.advantage.root.util.JsonHelper;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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

    private static AtomicLong orderNumber;
    private double totalAmount = 0.0;

    private Logger logger = Logger.getLogger(OrderManagementService.class);

    @Autowired
    @Qualifier("orderManagementRepository")
    public OrderManagementRepository orderManagementRepository;

    @Autowired
    @Qualifier("orderHistoryHeaderManagementRepository")
    public OrderHistoryHeaderManagementRepository orderHistoryHeaderManagementRepository;

    @Autowired
    @Qualifier("orderHistoryLineManagementRepository")
    public OrderHistoryLineManagementRepository orderHistoryLineManagementRepository;

    @Autowired
    @Qualifier("shoppingCartRepository")
    public ShoppingCartRepository shoppingCartRepository;

    private ShoppingCartService shoppingCartService = new ShoppingCartService();

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
            return generateShippingCostResponseError(costRequest.getSETransactionType(), ERROR_SHIPEX_GET_SHIPPING_COST_REQUEST_IS_EMPTY);
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
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> Response returned \'" + costResponse.getCode() + "\', Reason: \'" + costResponse.getReason() + "\'");
        } else if (costResponse.getAmount().isEmpty()) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
        } else if (costResponse.getCurrency().isEmpty()) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
        } else if (!costResponse.getSETransactionType().equalsIgnoreCase(costRequest.getSETransactionType())) {
            logger.info(ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
        } else {
            costResponse.setReason(ResponseEnum.OK.getStringCode());
        }

        return costResponse;
    }


    private ShippingCostResponse generateShippingCostResponseError(String transactionType, String errorText) {

        ShippingCostResponse costResponse = new ShippingCostResponse();

        costResponse.setSETransactionType(transactionType);
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
        long paymentRefNumber = 0;

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
            orderRequest.setSETransactionType(Constants.TRANSACTION_TYPE_PLACE_SHIPPING_ORDER);

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
                    cartProduct.getHexColor()
                            .toUpperCase());

            if (!product.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {
                /*  Add a product to user shopping cart response class  */
                purchasedProducts.add(new OrderPurchasedProductInformation(cartProduct.getProductId(),
                        product.getProductName(),
                        product.getColor().getCode(),
                        product.getColor().getName(),
                        product.getPrice(),
                        cartProduct.getQuantity()));

                totalAmount += product.getPrice() * cartProduct.getQuantity();

            } else {
                //  Product from cart was not found in CATALOG
                purchasedProducts.add(new OrderPurchasedProductInformation(cartProduct.getProductId(),
                        Constants.NOT_FOUND,
                        "000000",
                        "BLACK",
                        -999999.99,                     //  Price-per-item
                        cartProduct.getQuantity()));
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
                    logger.warn("Geting \"connected\" field by reflection for HttpURLConnection failed", e);
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
                logger.warn("WORK AROUND: Geting \"connected\" field by reflection for HttpURLConnection failed", e);
            }
            //endregion


            conn.setDoOutput(true);
            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

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

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                RuntimeException e = new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + Constants.SPACE + "MasterCredit JSON string sent: '" + input + "'");
                logger.fatal(conn.getResponseCode(), e);
                throw e;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String output;
            logger.debug("Output from Server...");
            while ((output = br.readLine()) != null) {
                sb.append(output);
                logger.debug(output);
            }

            Map<String, Object> jsonMap = JsonHelper.jsonStringToMap(sb.toString());

            masterCreditResponse.setResponseCode((String) jsonMap.get("MCResponse.Code"));
            masterCreditResponse.setResponseReason((String) jsonMap.get("MCResponse.Reason"));
            masterCreditResponse.setReferenceNumber(Long.valueOf(String.valueOf(jsonMap.get("MCRefNumber"))));
            masterCreditResponse.setTransactionType((String) jsonMap.get("TransactionType"));
            masterCreditResponse.setTransactionDate((String) jsonMap.get("TransactionDate"));

            conn.disconnect();

        } catch (MalformedURLException e) {
            logger.fatal(masterCreditResponse, e);
        } catch (IOException e) {
            logger.fatal(masterCreditResponse, e);
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
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

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
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode() + Constants.SPACE + "SafePay JSON string sent: '" + input + "'");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            StringBuilder sb = new StringBuilder();

            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                sb.append(output);
                System.out.println(output);
            }

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
     *
     * @param orderRequest Shipping Express request for tracking number.
     * @return Shipping Express Tracking Number in {@link PlaceShippingOrderResponse}.
     */
    public PlaceShippingOrderResponse placeShippingOrder(PlaceShippingOrderRequest orderRequest) {

        PlaceShippingOrderResponse orderResponse = null;

        if (orderRequest == null) {
            orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping order request is empty");
        }

        if (orderResponse == null) {
            URL urlWsdlLocation = Url_resources.getUrlSoapShipEx();

            //QName serviceName = new QName("https://www.AdvantageOnlineBanking.com/ShipEx/", "ShipExPortService");
            //ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation, serviceName);
            ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation);

            ShipExPort shipExPort = shipExPortService.getShipExPortSoap11();

            orderResponse = shipExPort.placeShippingOrder(orderRequest);

            if (!orderResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
                System.out.println("Shipping Express: placeShippingOrder() --> Response returned \'" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> Response returned '" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
            } else if (orderResponse.getTransactionDate().isEmpty()) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
            } else if (orderResponse.getTransactionReference().isEmpty()) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
            } else if (orderResponse.getTransactionReference().length() != 10) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
            } else if (!orderResponse.getSETransactionType().equalsIgnoreCase(orderRequest.getSETransactionType())) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
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

        orderResponse.setSETransactionType(transactionType);
        orderResponse.setCode(ResponseEnum.ERROR.getStringCode());
        orderResponse.setReason(errorText);
        orderResponse.setTransactionDate("");
        orderResponse.setTransactionReference("");

        return orderResponse;
    }

    public DemoAppConfigGetAllParametersResponse getAllDemoAppConfigParameters() {

        URL urlWsdlLocation = Url_resources.getUrlSoapAccount();

        AccountServicePortService accountServicePostService = new AccountServicePortService(urlWsdlLocation);

        AccountServicePort accountServicePort = accountServicePostService.getAccountServicePortSoap11();

        DemoAppConfigGetAllParametersRequest request = new DemoAppConfigGetAllParametersRequest();

        DemoAppConfigGetAllParametersResponse response = accountServicePort.demoAppConfigGetAllParameters(request);

        return response;
    }

    public DemoAppConfigGetParametersByToolResponse getDemoAppConfigParametersByTool(String toolName) {

        URL urlWsdlLocation = Url_resources.getUrlSoapAccount();

        AccountServicePortService accountServicePostService = new AccountServicePortService(urlWsdlLocation);

        AccountServicePort accountServicePort = accountServicePostService.getAccountServicePortSoap11();

        DemoAppConfigGetParametersByToolRequest request = new DemoAppConfigGetParametersByToolRequest();
        request.setToolName(toolName);
        DemoAppConfigGetParametersByToolResponse response = accountServicePort.demoAppConfigGetParametersByTool(request);

        return response;
    }

    //region get orders
    /*
    get orders history by userID or orderID
    not set any value(userID =0, orderID=0 or not set in REST) => get all
    currently order saved by unique orderID
    if change to unique per user the option get by userID and orderID available
     */
    public OrderHistoryCollectionDto getOrdersHistory(Long userId, Long orderId) {
        OrderHistoryCollectionDto orderHistoryCollectionDto = new OrderHistoryCollectionDto();
        List<OrderHeader> orderHistoryHeaders = new ArrayList<OrderHeader>();
        if ((userId == null || userId == 0) && (orderId == null || orderId == 0)) {
            orderHistoryHeaders = orderHistoryHeaderManagementRepository.getAll();//getByUserId(accountId);
        } else if ((userId == null || userId == 0) && (orderId != null && orderId > 0)) {
            orderHistoryHeaders = orderHistoryHeaderManagementRepository.getOrdersHeaderByOrderId(orderId);
        } else if ((orderId == null || orderId == 0) && (userId != null && userId > 0)) {
            orderHistoryHeaders = orderHistoryHeaderManagementRepository.getOrdersHeaderByUserId(userId);
        } else if ((orderId != null || orderId > 0) && (userId != null && userId > 0)) {
            orderHistoryHeaders = orderHistoryHeaderManagementRepository.getOrdersHeaderByOrderIdAndUserId(orderId, userId);
        }
        if (orderHistoryHeaders.size() > 0) {
            try {

                orderHistoryHeaders.forEach(order -> {
                    OrderHistoryDto orderHistoryDto = new OrderHistoryDto();
                    //get products by orderID
                    List<OrderLines> orderLines = orderHistoryLineManagementRepository.getAllOrderLinesByOrderId(order.getOrderNumber());
//
                    //set order fields
                    orderHistoryDto.setOrderNumber(order.getOrderNumber());
                    orderHistoryDto.setOrderTimestamp(order.getOrderTimestamp());
                    orderHistoryDto.setShippingTrackingNumber(order.getShippingTrackingNumber());
                    orderHistoryDto.setPaymentMethod(order.getPaymentMethod());
                    orderHistoryDto.setOrderTotalSum(order.getAmount());
                    orderHistoryDto.setOrderShipingCost(order.getShippingCost());
                    orderHistoryDto.setShippingAddress(order.getShippingAddress());
                    //set user
                    orderHistoryDto.setCustomer(new OrderHistoryAccountDto(order.getUserId(), order.getCustomerName(), order.getCustomerPhone()));
                    //set products
                    orderLines.forEach(product -> {
                        orderHistoryDto.addOrderHistoryProductDto(new OrderHistoryProductDto(product.getProductId(), product.getProductName(), ShoppingCart.convertIntColorToHex(product.getProductColor()),
                                product.getPricePerItem(), product.getQuantity(), product.getOrderNumber()));
                    });
                    orderHistoryCollectionDto.addOrderHistoryDto(orderHistoryDto);
                });
            } catch (Exception e) {
                logger.error(orderHistoryCollectionDto, e);
                return null;
            }
        }
        return orderHistoryCollectionDto;
    }

    //endregion get orders

    @Override
    public String toString() {
        return "OrderManagementService{" +
                "totalAmount=" + totalAmount +
                ", orderManagementRepository=" + orderManagementRepository +
                ", orderHistoryHeaderManagementRepository=" + orderHistoryHeaderManagementRepository +
                ", orderHistoryLineManagementRepository=" + orderHistoryLineManagementRepository +
                ", shoppingCartRepository=" + shoppingCartRepository +
                ", shoppingCartService=" + shoppingCartService +
                '}';
    }
}
