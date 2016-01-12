package com.advantage.order.store.order.services;

import ShipExServiceClient.*;
import com.advantage.common.Constants;
import com.advantage.common.Url_resources;
import com.advantage.common.enums.PaymentMethodEnum;
import com.advantage.common.enums.ResponseEnum;
import com.advantage.common.enums.TransactionTypeEnum;
import com.advantage.order.store.order.dao.ShoppingCartRepository;
import com.advantage.order.store.order.dto.*;
import com.advantage.order.store.order.dao.OrderManagementRepository;
import com.advantage.root.util.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Binyamin Regev on 07/01/2016.
 */
@Service
public class OrderManagementService {

    public static final String MESSAGE_ORDER_COMPLETED_SUCCESSFULLY = "order completed successfully";
    public static final String ERROR_SHIPEX_GET_SHIPPING_COST_REQUEST_IS_EMPTY = "Get shipping cost request is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY = "Get shipping cost response failure, currency is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT = "Get shipping cost response failure, shipping cost amount invalid empty ";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH = "Get shipping cost response failure, transaction type mismatch";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY = "Get shipping cost response failure, transaction date is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY = "Get shipping cost response failure, transaction reference is empty";
    public static final String ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH = "Get shipping cost response failure, invalid transaction reference length";

    private static AtomicLong orderNumber;
    private double totalAmount = 0.0;

    @Autowired
    @Qualifier("orderManagementRepository")
    public OrderManagementRepository orderManagementRepository;

    @Autowired
    @Qualifier("shoppingCartRepository")
    public ShoppingCartRepository shoppingCartRepository;

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

        if (! costResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
            System.out.println("Shipping Express: getShippingCost() --> Response returned \'" + costResponse.getCode() + "\', Reason: \'" + costResponse.getReason() + "\'");
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> Response returned \'" + costResponse.getCode() + "\', Reason: \'" + costResponse.getReason() + "\'");
        }
        else if (costResponse.getAmount().isEmpty()) {
            System.out.println("Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT);
        }
        else if (costResponse.getCurrency().isEmpty()) {
            System.out.println("Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY);
        }
        else if (costResponse.getSETransactionType().equalsIgnoreCase(costRequest.getSETransactionType())) {
            System.out.println("Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
            costResponse = generateShippingCostResponseError(costRequest.getSETransactionType(), "Shipping Express: getShippingCost() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
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
     *
     * (V)  Step #1: Get products info
     * (V)  Step #2: Generate OrderNumber.
     * (V)  Step #3: Do payment (MasterCredit or SafePay) - send REST POST request, receive Payment confirmation number.
     * (V)  Step #4: INSERT: Save order header and lines (NO TRACKING NUMBER YET!!!)
     * (V)  Step #5: Send request to get Tracking Number from ShipEx.
     * (V)  Step #6: UPDATE: Save shipping express tracking number to order header.
     */
    @Transactional
    public OrderPurchaseResponse doPurchase(long userId, OrderPurchaseRequest purchaseRequest) {

        long orderNumber = 0;
        long paymentRefNumber = 0;

        OrderPurchaseResponse purchaseResponse = new OrderPurchaseResponse();

        OrderShippingInformation shippingInfo = purchaseRequest.getOrderShippingInformation();
        OrderPaymentInformation paymentInfo = purchaseRequest.getOrderPaymentInformation();

        //  Step #1: Get products info
        List<OrderPurchasedProductInformation> purchasedProducts = getPurchasedProductsInformation(userId, purchaseRequest.getPurchasedProducts());

        //  Step #2: Generate order number
        orderNumber = generateOrderNumberNextValue();

        //  Step #3: Do payment MasterCredit / SafePay
        boolean paymentSuccessful = true;
        if (purchaseRequest.getOrderPaymentInformation().getPaymentMethod().equals(PaymentMethodEnum.MASTER_CREDIT.getStringCode())) {
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
            if (masterCreditResponse.getResponseCode().equalsIgnoreCase("Approved")) {
                paymentInfo.setReferenceNumber(masterCreditResponse.getReferenceNumber());
            } else {
                paymentSuccessful = false;
                purchaseResponse.setSuccess(paymentSuccessful);
                purchaseResponse.setCode(masterCreditResponse.getResponseCode());
                purchaseResponse.setReason(masterCreditResponse.getResponseReason());
                purchaseResponse.setOrderNumber(orderNumber);
            }
        }
        else if (purchaseRequest.getOrderPaymentInformation().getPaymentMethod().equals(PaymentMethodEnum.SAFE_PAY.getStringCode())) {
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

            if (safePayResponse.getResponseCode().equalsIgnoreCase("Approved")) {
                paymentInfo.setReferenceNumber(safePayResponse.getReferenceNumber());
            } else {
                paymentSuccessful = false;
                purchaseResponse.setSuccess(paymentSuccessful);
                purchaseResponse.setCode(safePayResponse.getResponseCode());
                purchaseResponse.setReason(safePayResponse.getResponseReason());
                purchaseResponse.setOrderNumber(orderNumber);
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

            //  Assemble product name and quantity for purchased products
            SEProducts products = new SEProducts();
            for (OrderPurchasedProductInformation purchasedProduct : purchasedProducts) {
                Product product = new Product();
                product.setProductName(purchasedProduct.getProductName());
                product.setProductQuantity(purchasedProduct.getQuantity());
                products.getProduct().add(product);
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

            } else {
                purchaseResponse.setSuccess(false);
                purchaseResponse.setCode(orderResponse.getCode());
                purchaseResponse.setReason(orderResponse.getReason());
                purchaseResponse.setOrderNumber(orderNumber);
            }
        }

        return purchaseResponse;
    }

    /**
     *  For each purchased product:
     *      Retrieve product-name, product-color-name and price-per-item.
     */
    private List<OrderPurchasedProductInformation> getPurchasedProductsInformation(long userId,
                                                                                   List<ShoppingCartDto> cartProducts) {
        List<OrderPurchasedProductInformation> purchasedProducts = new ArrayList<>();

        for (ShoppingCartDto cartProduct : cartProducts) {
            ShoppingCartResponseDto.CartProduct product =
                    shoppingCartRepository.getCartProductDetails(cartProduct.getProductId(),
                                                                cartProduct.getHexColor().toUpperCase());

            if (! product.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {
                /*  Add a product to user shopping cart response class  */
                purchasedProducts.add(new OrderPurchasedProductInformation(cartProduct.getProductId(),
                        product.getProductName(),
                        product.getColor().getCode(),
                        product.getColor().getName(),
                        product.getPrice(),
                        cartProduct.getQuantity()));
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
     *  {
     *  "MCCVVNumber": 0,
     *  "MCCardNumber": 0,
     *  "MCCustomerName": "string",
     *  "MCCustomerPhone": "string",
     *  "MCExpirationDate": "string",
     *  "MCRecevingAmount.Value": 0,
     *  "MCRecevingCard.AccountNumber": 0,
     *  "MCRecevingCard.Currency": "string",
     *  "MCTransactionDate": "string",
     *  "MCTransactionType": "string"
     *  }
     * @param masterCreditRequest
     * @return
     */
    public MasterCreditResponse payWithMasterCredit(MasterCreditRequest masterCreditRequest) {
        URL urlPayment = null;

        MasterCreditResponse masterCreditResponse = new MasterCreditResponse();

        try {
            urlPayment = new URL(Url_resources.getUrlMasterCredit(), "payments/payment");

            HttpURLConnection conn = (HttpURLConnection) urlPayment.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

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

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            Map<String, Object> jsonMap = JsonHelper.jsonStringToMap(output);

            masterCreditResponse.setResponseCode((String) jsonMap.get("MCResponse.Code"));
            masterCreditResponse.setResponseReason((String) jsonMap.get("MCResponse.Reason"));
            masterCreditResponse.setReferenceNumber(Long.valueOf(String.valueOf(jsonMap.get("MCRefNumber"))));
            masterCreditResponse.setTransactionType((String) jsonMap.get("TransactionType"));
            masterCreditResponse.setTransactionDate((String) jsonMap.get("TransactionDate"));

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return masterCreditResponse;
    }

    /**
     * Send REST POST request to pay for order using <b>SafePay</b> service.
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

            //  SafePay PAYMENT request
            String input = "{" +
                    "\"SPCustomerPhone\": \"" + safePayRequest.getCustomerPhone() + "\"," +
                    "\"SPPassword\": \"" + safePayRequest.getPassword() + "\"," +
                    "\"SPRecevingAmount.Currency\": \"" + safePayRequest.getCurrency() + "\"," +
                    "\"SPRecevingAmount.Value\": " + safePayRequest.getValue() + "," +
                    "\"SPRecevingCard.AccountNumber\": " + safePayRequest.getAccountNumber() + "," +
                    "\"SPTransactionDate\": \"" + safePayRequest.getTransactionDate() + "\"," +
                    "\"SPTransactionType\": \"" + safePayRequest.getTransactionType() + "\"" +
                    "\"SPUserName\":\"" + safePayRequest.getUserName() + "\"," +
                    "}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            Map<String, Object> jsonMap = JsonHelper.jsonStringToMap(output);

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
     * Add user order header and line in ORDER schema.
     * @param orderPurchaseRequest
     */
    public void addUserOrder(OrderPurchaseRequest orderPurchaseRequest){

    }

    /**
     * Send SOAP request for ShipEx tracking number and receive it in response.
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

            if (! orderResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
                System.out.println("Shipping Express: placeShippingOrder() --> Response returned \'" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> Response returned '" + orderResponse.getCode() + "\', Reason: \'" + orderResponse.getReason() + "\'");
            }
            else if (orderResponse.getTransactionDate().isEmpty()) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY);
            }
            else if (orderResponse.getTransactionReference().isEmpty()) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY);
            }
            else if (orderResponse.getTransactionReference().length() == 10) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH);
            }
            else if (orderResponse.getSETransactionType().equalsIgnoreCase(orderRequest.getSETransactionType())) {
                System.out.println("Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
                orderResponse = generatePlaceShippingOrderResponseError(orderRequest.getSETransactionType(), "Shipping Express: placeShippingOrder() --> " + ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH);
            }
        }
        return orderResponse;
    }

    /**
     * Update tracking number in {@code OrderHeder} entity.
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
}