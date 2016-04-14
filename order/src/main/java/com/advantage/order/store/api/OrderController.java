package com.advantage.order.store.api;

//import com.advantage.order.store.order.dto.OrderPurchaseRequest;

import ShipExServiceClient.ShippingCostRequest;
import ShipExServiceClient.ShippingCostResponse;
import com.advantage.common.Constants;
import com.advantage.common.Url_resources;
import com.advantage.common.dto.DemoAppConfigParameter;
import com.advantage.common.security.AuthorizeAsUser;
import com.advantage.order.store.dto.*;
import com.advantage.order.store.model.ShoppingCart;
import com.advantage.order.store.services.OrderManagementService;
import com.advantage.order.store.services.ShoppingCartService;
import com.advantage.root.util.ValidationHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Binyamin Regev on 09/12/2015.
 * @see HttpStatus#BAD_REQUEST (400) = The request cannot be fulfilled due to bad syntax.
 * General error when fulfilling the request would cause an invalid state. <br/>
 * e.g. Domain validation errors, missing data, etc.
 * @see HttpStatus#NOT_IMPLEMENTED (501) = The server either does not recognise the
 * request method, or it lacks the ability to fulfill the request.
 */
@RestController
@RequestMapping(value = Constants.URI_API + "/v1")
public class OrderController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderManagementService orderManagementService;

    private ShoppingCartResponse shoppingCartResponse;

    private static final String DemoAppConfig = "DemoAppConfig/parameters/";
    private static final String ParameterName ="Repeat_ShipEx_call";
    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get user shopping cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponseDto> getUserCart(@PathVariable("userId") Long userId,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) {

        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));

        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.OK);
        }
    }


    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}/product/{productId}/color/{color}", method = RequestMethod.POST)
    @ApiOperation(value = "Add product to shopping cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponseDto> addProductToCart(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId,
            @PathVariable("color") String hexColor,
            @RequestParam(value = "quantity", defaultValue = "1", required = false) int quantity,
            HttpServletRequest request) {

        shoppingCartResponse = shoppingCartService.addProductToCart(userId, productId, hexColor, quantity);

        /*return new ResponseEntity<>(shoppingCartResponse, HttpStatus.OK);*/
        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));
        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            //return new ResponseEntity<>(userCartResponseDto, HttpStatus.OK);
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.CREATED);
        }

    }

    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}/product/{productId}/color/{color}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update Cart-Product quantity and/or color")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})

    public ResponseEntity<ShoppingCartResponseDto> updateProductInCart(@PathVariable("userId") Long userId,
                                                                       @PathVariable("productId") Long productId,
                                                                       @PathVariable("color") String hexColor,
																	   @RequestParam(value = "quantity", defaultValue = "-1", required = false) int quantity,
                                                                       @RequestParam(value = "new_color", defaultValue = "-1", required = false) String hexColorNew,
                                                                       HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;

        if (((ValidationHelper.isValidColorHexNumber(hexColor)) &&
                (ValidationHelper.isValidColorHexNumber(hexColorNew)) &&
                (! hexColor.equalsIgnoreCase(hexColorNew))) || (quantity > 0))
        {
            shoppingCartResponse = shoppingCartService.updateProductInCart(Long.valueOf(userId), productId, hexColor, hexColorNew, quantity);
        }
        else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));
        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            return new ResponseEntity<>(userCartResponseDto, httpStatus);
        }
    }

    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}", method = RequestMethod.PUT)
    @ApiOperation(value = "Replace user shopping cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponse> replaceUserCart(@PathVariable("userId") Long userId,
                                                                @RequestBody List<ShoppingCartDto> shoopingCartProducts,
                                                                HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;

        if (userId != null) {
            shoppingCartResponse = shoppingCartService.replaceUserCart(Long.valueOf(userId), shoopingCartProducts);

            if (shoppingCartResponse.isSuccess()) {
                ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));

                if (userCartResponseDto == null) {
                    //  Unlikely scenario - update of user cart successful and get user cart failed
                    httpStatus = HttpStatus.NOT_FOUND;
                    shoppingCartResponse = new ShoppingCartResponse(false, ShoppingCart.MESSAGE_SHOPPING_CART_IS_EMPTY, -1);
                } else {
                    httpStatus = HttpStatus.OK;
                }
            } else {
                //  Replace user cart failed
                httpStatus = HttpStatus.NOT_IMPLEMENTED;

                shoppingCartResponse.setSuccess(false);
                shoppingCartResponse.setReason(ShoppingCart.MESSAGE_REPLACE_USER_CART_FAILED);
                shoppingCartResponse.setId(-1);
            }
        } else {
            httpStatus = HttpStatus.NOT_FOUND;  //  Resource (registered user_id) not found

            shoppingCartResponse.setSuccess(false);
            shoppingCartResponse.setReason(ShoppingCart.MESSAGE_INVALID_USER_ID);
            shoppingCartResponse.setId(-1);
        }

        return new ResponseEntity<>(shoppingCartResponse, httpStatus);
    }

    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}/product/{productId}/color/{color}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove a product from user shopping cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponseDto> removeProductFromUserCart(@PathVariable("userId") long userId,
                                                                             @PathVariable("productId") Long productId,
                                                                             @PathVariable("color") String hexColor,
                                                                             HttpServletRequest request) {

        shoppingCartResponse = shoppingCartService.removeProductFromUserCart(userId, productId, hexColor);

        /*return new ResponseEntity<>(shoppingCartResponse, HttpStatus.OK);*/
        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));
        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.OK);
        }
    }

    /*  =========================================================================================================   */
    @RequestMapping(value = "/carts/{userId}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Clear user shopping cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponseDto> clearUserCart(@PathVariable("userId") Long userId) {

        if (userId != null) {
            shoppingCartResponse = shoppingCartService.clearUserCart(Long.valueOf(userId));
        } else {
            shoppingCartResponse.setSuccess(false);
            shoppingCartResponse.setReason(ShoppingCart.MESSAGE_INVALID_USER_ID);
            shoppingCartResponse.setId(-1);
        }

        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));
        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.OK);
        }
    }

    /*  =========================================================================================================   */

    /**
     * <b>REST API</b> {@code PUT} request to verify quantities of all products in user cart.
     *
     * @param shoopingCartProducts {@link List} of {@link ShoppingCartDto} products in user cart to verify quantities.
     * @param userId               Unique user identity.
     * @return {@link ShoppingCartResponseDto} products that had higher quantity in cart than in stock. {@code null}
     * when all products quantities less or equal to their quantities in stock.
     */
    @RequestMapping(value = "/carts/{userId}/quantity", method = RequestMethod.PUT)
    @ApiOperation(value = "Verify and update products quantities in user cart")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    public ResponseEntity<ShoppingCartResponseDto> verifyProductsQuantitiesInUserCart(@PathVariable("userId") long userId,
                                                                                      @RequestBody List<ShoppingCartDto> shoopingCartProducts,
                                                                                      HttpServletRequest request) {
        System.out.println("OrderController -> verifyProductsQuantitiesInUserCart(): userId=" + userId);
        ShoppingCartResponseDto responseDto = shoppingCartService.verifyProductsQuantitiesInUserCart(userId, shoopingCartProducts);
        /*return new ResponseEntity<>(responseDto, HttpStatus.OK);*/

        ShoppingCartResponseDto userCartResponseDto = shoppingCartService.getUserShoppingCart(Long.valueOf(userId));
        if (userCartResponseDto == null) {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.NOT_FOUND);    //  404 = Resource not found
        } else {
            return new ResponseEntity<>(userCartResponseDto, HttpStatus.OK);
        }
    }

    /**
     * At fisrt develop it as {@code POST} request, because it needs a <i>body</i>. <br/>
     * In the future, it will be changed to {@code GET} request, after sending a
     * request for <b><i>Account Service</i></b> to get parameters values.
     *
     * @param request
     * @param response
     * @return {@link ShippingCostResponse}
     */
    @RequestMapping(value = "/shippingcost", method = RequestMethod.POST)
    @ApiOperation(value = "Order shipping cost")
    public ResponseEntity<ShippingCostResponse> getShippingCostFromShipEx(@RequestBody ShippingCostRequest costRequest,
                                                                          HttpServletRequest request,
                                                                          HttpServletResponse response) {

        HttpStatus httpStatus = HttpStatus.OK;

        /*
        SEAddress address = new SEAddress();
        address.setAddressLine1("address");
        address.setCity("Jerusalem");
        address.setCountry("IL");
        address.setPostalCode("123123");
        address.setState("Israel");

        ShippingCostRequest costRequest = new ShippingCostRequest();
        costRequest.setSEAddress(address);
        costRequest.setSECustomerName("Customer Full Name");
        costRequest.setSECustomerPhone("+972 77 7654321");
        costRequest.setSENumberOfProducts(1);
        costRequest.setSETransactionType(Constants.TRANSACTION_TYPE_SHIPPING_COST);
        */

        //TODO Moti: get configuration parameter
        int repeat= checkRepeatShipExCall();
        repeat= repeat>0 ? repeat : 1;
        ShippingCostResponse costResponse =null; //orderManagementService.getShippingCostFromShipEx(costRequest);

        do {
            costResponse =orderManagementService.getShippingCostFromShipEx(costRequest);
            switch (costResponse.getReason()) {
                case OrderManagementService.ERROR_SHIPEX_GET_SHIPPING_COST_REQUEST_IS_EMPTY:
                    httpStatus = HttpStatus.BAD_REQUEST;
                    break;
                /* Response failure */
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_CURRENCY_IS_EMPTY:
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_EMPTY_AMOUNT:
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_TYPE_MISMATCH:
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_DATE_IS_EMPTY:
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_TRANSACTION_REFERENCE_IS_EMPTY:
                case OrderManagementService.ERROR_SHIPEX_RESPONSE_FAILURE_INVALID_TRANSACTION_REFERENCE_LENGTH:
                    httpStatus = HttpStatus.NOT_IMPLEMENTED;
                    break;
                default:
                    httpStatus = HttpStatus.OK;
            }

        }
        while (--repeat>0);
        return new ResponseEntity<>(costResponse, httpStatus);
    }

    private int checkRepeatShipExCall()
    {
        int repeat=0;
        URL DemoAppConfigPrefixUrl;
        URL parameterByNameUrl = null;
        try {
            DemoAppConfigPrefixUrl = new URL(Url_resources.getUrlCatalog(), DemoAppConfig);
            parameterByNameUrl = new URL(DemoAppConfigPrefixUrl, String.valueOf(ParameterName));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        DemoAppConfigParameter parameter = null;

        try {
            String stringResponse = httpGet(parameterByNameUrl);
            System.out.println("stringResponse = \"" + stringResponse + "\"");

            if (stringResponse.equalsIgnoreCase(Constants.NOT_FOUND)) {
                //  tool not found (409)
                return repeat=0;
            } else {
                parameter = getDemoAppConfigParameterfromJsonObjectString(stringResponse);
                if(parameter!=null)
                    return repeat= Integer.parseInt(parameter.getParameterValue());
            }
        } catch (IOException e) {
            System.out.println("Calling httpGet(\"" + parameterByNameUrl.toString() + "\") throws IOException: ");
            e.printStackTrace();
            return repeat;
        }
        catch (NullPointerException e) {
            System.out.println("convert Repeat_ShipEx_call value to int throws IOException: ");
            e.printStackTrace();
            return repeat;
        }
        return repeat;
    }

    private  DemoAppConfigParameter getDemoAppConfigParameterfromJsonObjectString(String jsonObjectString) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DemoAppConfigParameter parameter = objectMapper.readValue(jsonObjectString, DemoAppConfigParameter.class);

        return parameter;
    }

    private  String httpGet(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        int responseCode = conn.getResponseCode();

        String returnValue;
        switch (responseCode) {
            case org.apache.http.HttpStatus.SC_OK: {
                // Buffer the result into a string
                InputStreamReader inputStream = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStream);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                returnValue = sb.toString();
                break;
            }
            case org.apache.http.HttpStatus.SC_CONFLICT:
                //  Product not found
                returnValue = "Not found";
                break;

            default:
                System.out.println("httpGet -> responseCode=" + responseCode);
                throw new IOException(conn.getResponseMessage());
        }

        conn.disconnect();

        return returnValue;
    }


    /*  =========================================================================================================   */
    @RequestMapping(value = "/orders/users/{userId}", method = RequestMethod.POST)
    @ApiOperation(value = "Purchase new order")
    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 409, message = "Conflict", response = OrderPurchaseResponse.class)})
    public ResponseEntity<OrderPurchaseResponse> doPurchase(@PathVariable("userId") long userId,
                                                            @RequestBody OrderPurchaseRequest purchaseRequest,
                                                            HttpServletRequest request) {

        System.out.println("OrderController -> doPurchase(): userId=" + userId);

        OrderPurchaseResponse purchaseResponse = orderManagementService.doPurchase(userId, purchaseRequest);

        if (purchaseResponse.isSuccess()) {
            return new ResponseEntity<>(purchaseResponse, HttpStatus.OK);
        } else {
            // TODO-Benny return error code suitable to the error
            return new ResponseEntity<>(purchaseResponse, HttpStatus.CONFLICT);
        }
    }

}
