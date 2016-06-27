package com.advantage.order.store.services;

//import com.advantage.order.store.order.dto.OrderPurchaseRequest;

import com.advantage.common.Constants;
import com.advantage.common.Url_resources;
import com.advantage.common.dto.ColorAttributeDto;
import com.advantage.common.dto.DemoAppConfigParameter;
import com.advantage.common.dto.ProductDto;
import com.advantage.order.store.dao.ShoppingCartRepository;
import com.advantage.order.store.dto.ShoppingCartDto;
import com.advantage.order.store.dto.ShoppingCartResponse;
import com.advantage.order.store.dto.ShoppingCartResponseDto;
import com.advantage.order.store.model.ShoppingCart;
import com.advantage.root.util.ArgumentValidationHelper;
import com.advantage.root.util.RestApiHelper;
import com.advantage.root.util.ValidationHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Binyamin Regev on 03/12/2015.
 */
@Service
public class ShoppingCartService {

    private static final String CATALOG_PRODUCT = "products/";
    private static final String CATALOG_PRODUCT_COLOR_ATTRIBUTE = "%s/color/%s";
    private static final String DEMO_APP_CONFIG_BY_PARAMETER_NAME = "DemoAppConfig/parameters/";    //  Show_error_500_in_update_cart
    private static final Logger logger = Logger.getLogger(ShoppingCartService.class);

    @Autowired
    @Qualifier("shoppingCartRepository")
    public ShoppingCartRepository shoppingCartRepository;

    /**
     * @param userId
     * @return
     */
    public ShoppingCartResponseDto getShoppingCartsByUserId(long userId) {
        return getUserShoppingCart(userId);
    }

    /**
     * @param userId
     * @param productId
     * @param stringColor
     * @param quantity
     * @return
     */
    @Transactional
    public ShoppingCartResponse addProductToCart(long userId, Long productId, String stringColor, int quantity) {

        int color = ShoppingCart.convertHexColorToInt(stringColor);

        //  Validate Arguments
        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "user id");
        ArgumentValidationHelper.validateArgumentIsNotNull(productId, "product id");
        ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(color, "color decimal RGB value");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(quantity, "quantity");

        /*
        //  Use API to verify userId belongs to a registered user by calling "Account Service"
        if (!isRegisteredUserExists(userId)) {
            shoppingCartResponse = new ShoppingCartResponse(false, ShoppingCart.MESSAGE_INVALID_USER_ID, -1);
            return null;
        }
         */
        ShoppingCartResponse shoppingCartResponse = new ShoppingCartResponse(false, "Initial values", 0);

        if (isProductExists(productId, ShoppingCart.convertIntColorToHex(color))) {

            ShoppingCart shoppingCart = shoppingCartRepository.find(userId, productId, color);

            //  Check if there is this ShoppingCart already exists
            ColorAttributeDto dto = getColorAttributeByProductIdAndColorCode(productId, color);
            if (shoppingCart != null) {

                int totalQuantity = shoppingCart.getQuantity() + quantity;
                if (totalQuantity > dto.getInStock()) {
                    totalQuantity = dto.getInStock();

                    shoppingCartResponse.setReason(ShoppingCart.MESSAGE_OOPS_WE_ONLY_HAVE_X_IN_STOCK);
                    //shoppingCartResponse.setReason(ShoppingCart.MESSAGE_WE_UPDATED_YOUR_ORDER_ACCORDINGLY);
                } else {
                    shoppingCartResponse.setReason(ShoppingCart.MESSAGE_QUANTITY_OF_PRODUCT_IN_SHOPPING_CART_WAS_UPDATED_SUCCESSFULLY);
                    //shoppingCartResponse.setReason(ShoppingCart.MESSAGE_WE_UPDATED_YOUR_ORDER_ACCORDINGLY);
                }

                shoppingCartRepository.update(userId, productId, color, totalQuantity);

                shoppingCartResponse.setSuccess(true);
                shoppingCartResponse.setId(shoppingCart.getProductId());

            } else {
                //  New ShoppingCart
                if (quantity > dto.getInStock()) {
                    quantity = dto.getInStock();
                    shoppingCartResponse.setReason(ShoppingCart.MESSAGE_OOPS_WE_ONLY_HAVE_X_IN_STOCK);
                    //shoppingCartResponse.setReason(ShoppingCart.MESSAGE_WE_UPDATED_YOUR_ORDER_ACCORDINGLY);
                } else {
                    shoppingCartResponse.setReason(ShoppingCart.MESSAGE_NEW_PRODUCT_UPDATED_SUCCESSFULLY);
                    //shoppingCartResponse.setReason(ShoppingCart.MESSAGE_WE_UPDATED_YOUR_ORDER_ACCORDINGLY);
                }
                shoppingCart = new ShoppingCart(userId, Calendar.getInstance().getTime().getTime(), productId, color, quantity);
                shoppingCartRepository.add(shoppingCart);

                shoppingCartResponse.setSuccess(true);
                shoppingCartResponse.setId(shoppingCart.getProductId());

                shoppingCartResponse = new ShoppingCartResponse(true,
                        ShoppingCart.MESSAGE_NEW_PRODUCT_UPDATED_SUCCESSFULLY,
                        shoppingCart.getProductId());
            }
        } else {
            shoppingCartResponse = new ShoppingCartResponse(false,
                    ShoppingCart.MESSAGE_PRODUCT_NOT_FOUND_IN_CATALOG,
                    productId);
        }

        return shoppingCartResponse;
    }

    private DemoAppConfigParameter getConfigParameterValueFromJsonObjectString(String jsonObjectString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        DemoAppConfigParameter demoAppConfigParameter = objectMapper.readValue(jsonObjectString, DemoAppConfigParameter.class);

        return demoAppConfigParameter;
    }

    private ColorAttributeDto getColorAttributeByProductIdAndColorCode(Long productId, int colorCode) {
        ArgumentValidationHelper.validateLongArgumentIsPositive(productId.longValue(), "product id");
        ArgumentValidationHelper.validateNumberArgumentIsPositiveOrZero(colorCode, "color RGB hexadecimal value");

        String hexColor = ShoppingCart.convertIntColorToHex(colorCode).toUpperCase();

        //  Create a URL for Catalog service -> products
        URL productsPrefixUrl = null;
        try {
            productsPrefixUrl = new URL(Url_resources.getUrlCatalog(), CATALOG_PRODUCT);
        } catch (MalformedURLException e) {
            logger.error(productsPrefixUrl, e);
        }

        //  Create a URL with product-id and color RGB Hexadecimal value
        URL getColorAttributeByProdctIdAndColorCode = null;
        try {
            getColorAttributeByProdctIdAndColorCode = new URL(productsPrefixUrl, String.format(CATALOG_PRODUCT_COLOR_ATTRIBUTE, String.valueOf(productId), hexColor));
        } catch (MalformedURLException e) {
            logger.error(getColorAttributeByProdctIdAndColorCode, e);
        }

        if (logger.isInfoEnabled()) {
            logger.info("stringURL=\"" + getColorAttributeByProdctIdAndColorCode.toString() + "\"");
        }

        ColorAttributeDto dto = null;
        try {
            String stringResponse = RestApiHelper.httpGet(getColorAttributeByProdctIdAndColorCode);
            if (!stringResponse.equalsIgnoreCase(Constants.NOT_FOUND)) {
                dto = getColorAttributeDtofromJsonObjectString(stringResponse);
            } else {
                //  Product not found (409)
                dto = new ColorAttributeDto(hexColor, Constants.NOT_FOUND, -1);
            }
        } catch (IOException e) {
            logger.error("Calling httpGet(" + getColorAttributeByProdctIdAndColorCode.toString() + ") throws IOException: ", e);
        }

        return dto;
    }

    private String getDemoAppConfigParameterValue(String parameterName) {
        URL productsPrefixUrl = null;
        try {
            productsPrefixUrl = new URL(Url_resources.getUrlCatalog(), DEMO_APP_CONFIG_BY_PARAMETER_NAME);
        } catch (MalformedURLException e) {
            logger.error(productsPrefixUrl, e);
        }

        URL getDemoAppConfigByParameterName = null;
        try {
            getDemoAppConfigByParameterName = new URL(productsPrefixUrl, parameterName);
        } catch (MalformedURLException e) {
            logger.error(getDemoAppConfigByParameterName, e);
        }

        if (logger.isInfoEnabled()) {
            logger.info("stringURL=\"" + getDemoAppConfigByParameterName.toString() + "\"");
        }

        DemoAppConfigParameter demoAppConfigParameter = null;
        String parameterValue = null;

        try {
            String stringResponse = RestApiHelper.httpGet(getDemoAppConfigByParameterName);
            if (!stringResponse.equalsIgnoreCase(Constants.NOT_FOUND)) {
                demoAppConfigParameter = getConfigParameterValueFromJsonObjectString(stringResponse);
                if (demoAppConfigParameter != null) {
                    parameterValue = demoAppConfigParameter.getParameterValue();
                }
            }
        } catch (IOException e) {
            logger.error("Calling httpGet(\"" + getDemoAppConfigByParameterName.toString() + "\") throws IOException: ", e);
        }

        return parameterValue;
    }

    /*
        productId   color       Quantity
        1           YELLOW      5
        1           BLUE        4

        Action: { productId=1, color=BLUE, newColor=YELLOW, Quantity=1 }

     */
    public ShoppingCartResponse updateProductInCart(long userId, Long productId, String hexColor, String hexColorNew, int quantity) {

        if (((!ValidationHelper.isValidColorHexNumber(hexColor)) ||
                (!ValidationHelper.isValidColorHexNumber(hexColorNew)) ||
                (hexColor.equalsIgnoreCase(hexColorNew))) && (quantity < 0)) {
            return new ShoppingCartResponse(false,
                    "Error: Bad request, Nothing to do",
                    productId);
        }

        ShoppingCartResponse shoppingCartResponse = null;

        //  Get parameter "Error_500_in_update_cart" value from DemoAppConfig.xml

        String parameterValue = this.getDemoAppConfigParameterValue("Error_500_in_update_cart");
//        LOGGER.info("Updating product " + productId + " in cart.");
//        LOGGER.info("Updating product details with color: " + ((hexColorNew.equals("-1"))? ColorPalletEnum.getColorByCode(hexColor).toString().toLowerCase() : ColorPalletEnum.getColorByCode(hexColorNew).toString().toLowerCase()) + " and quantity: " + quantity + ".");
//        LOGGER.info("Verifying that updated product is available at vendor shop.");
//        if (parameterValue.equalsIgnoreCase("No")) {
        if ((parameterValue == null) || (parameterValue.equalsIgnoreCase("No"))) {

            int color = ShoppingCart.convertHexColorToInt(hexColor);

            /*  Update QUANTITY of product in user cart */
            if (quantity > 0) {
                /* Update product quantity in cart  */
                System.out.println("ShoppingCartService.update(" + userId + ", " + productId + ", " + color + ", " + quantity + ")");
                shoppingCartResponse = shoppingCartRepository.update(userId, productId, color, quantity);
            }

            /*  Update COLOR CHANGE of product in user cart */
            if ((ValidationHelper.isValidColorHexNumber(hexColor)) &&
                    (ValidationHelper.isValidColorHexNumber(hexColorNew)) &&
                    (!hexColor.equalsIgnoreCase(hexColorNew))) {

                /*  Try to find a product with the same productId and new color in the user's cart  */
                int newColor = ShoppingCart.convertHexColorToInt(hexColorNew);
                ShoppingCart shoppingCart = shoppingCartRepository.find(userId, productId, newColor);

                if (shoppingCart != null) {
                    /*
                        There's already a product with the new color:
                        1. Delete product with previous color, but save its quantity.
                        2. Add quantity of product with previous color to product with new color.
                     */
                    int result = shoppingCartRepository.removeProductFromUserCart(userId, productId, color);
                    shoppingCartResponse = shoppingCartRepository.getShoppingCartResponse();
                    if (shoppingCartResponse.isSuccess()) {
                        int totalQuantity = shoppingCart.getQuantity() + quantity;
                        shoppingCartRepository.update(userId, productId, newColor, totalQuantity);
                    } else {
                        shoppingCartResponse.setReason("Error: failed to change product's color");
                    }
                } else {
                    /*
                        Unlikely to occur, but need to cover it:
                        Add a new product with the new color to user cart
                     */
                    shoppingCartRepository.removeProductFromUserCart(userId, productId, color);
                    shoppingCartRepository.add(new ShoppingCart(userId, productId, newColor, quantity));
                }
            } else {
                //  Nothing to update - Bad Request
                shoppingCartResponse.setSuccess(false);
                shoppingCartResponse.setReason("Error: Bad request. Product's color was not changed");
                shoppingCartResponse.setId(productId);
            }
        } else if (parameterValue.equalsIgnoreCase("Yes")) {
//			//simulate error 500
            logger.error("A problem occurred while updating cart.");
            throw new RuntimeException("A problem occurred while updating cart.");
        }

        return shoppingCartResponse;
    }

    public ShoppingCartResponse replaceUserCart(long userId, List<ShoppingCartDto> shoppingCarts) {
        ShoppingCartResponse shoppingCartResponse = new ShoppingCartResponse(true, "", 0);

        for (ShoppingCartDto shoppingCartDto : shoppingCarts) {
            int color = ShoppingCart.convertHexColorToInt(shoppingCartDto.getHexColor());
            ColorAttributeDto dto = getColorAttributeByProductIdAndColorCode(shoppingCartDto.getProductId().longValue(), color);

            if (shoppingCartDto.getQuantity() > dto.getInStock()) {
                shoppingCartDto.setQuantity(dto.getInStock());
                if (shoppingCartResponse.getReason().isEmpty()) {
                    shoppingCartResponse.setReason(ShoppingCart.MESSAGE_WE_UPDATED_YOUR_CART_BASED_ON_THE_ITEMS_IN_STOCK);
                }
            }
        }
        ShoppingCartResponse response = shoppingCartRepository.replace(userId, shoppingCarts);

        //  Database update successful?
        if (!response.isSuccess()) { shoppingCartResponse = response; }

        return shoppingCartResponse;
    }

    /**
     * @param userId
     * @param productId
     * @param stringColor
     * @return
     */
    @Transactional
    public ShoppingCartResponse removeProductFromUserCart(long userId, Long productId, String stringColor) {
        int color = ShoppingCart.convertHexColorToInt(stringColor);
        int result = shoppingCartRepository.removeProductFromUserCart(userId, productId, color);
        return shoppingCartRepository.getShoppingCartResponse();
    }

    /**
     * @param userId
     * @return
     */
    @Transactional
    public ShoppingCartResponse clearUserCart(long userId) {
        return shoppingCartRepository.clearUserCart(userId);
    }

    /**
     * Verify the quantity of each product in user cart exists in stock. If quantity
     * in user cart is greater than the quantity in stock than add the product with
     * the quantity in stock to {@link ShoppingCartResponseDto} {@code Response} JSON. <br/>
     *
     * @param userId               Unique user identity.
     * @param shoppingCartProducts {@link List} of {@link ShoppingCartDto} products in user cart to verify quantities.
     * @return {@code null} when all quantities of the products in the user cart <b>are equal or Less than</b> the quantities in
     * stock. If the quantity of any cart product <b>is greater than</b> the quantity in stock then the product will be added to
     * the list of products in the cart with the <ul>quantity in stock</ul>.
     */
    @Transactional
    public ShoppingCartResponseDto verifyProductsQuantitiesInUserCart(long userId, List<ShoppingCartDto> shoppingCartProducts) {
        logger.info("ShoppingCartService -> verifyProductsQuantitiesInUserCart(): userId=" + userId);

        ShoppingCartResponseDto responseDto = new ShoppingCartResponseDto(userId);

        for (ShoppingCartDto cartProduct : shoppingCartProducts) {

            ShoppingCartResponseDto.CartProduct cartProductDto = getCartProductDetails(cartProduct.getProductId(), cartProduct.getHexColor());

            if (cartProduct.getQuantity() > cartProductDto.getColor().getInStock()) {

                ShoppingCart shoppingCart = shoppingCartRepository.find(userId,
                        cartProduct.getProductId(),
                        ShoppingCart.convertHexColorToInt(cartProductDto.getColor().getCode()));

                if (shoppingCart != null) {
                    //  Update quantity of cart product to product's quantity in stock
                    shoppingCartRepository.update(userId,
                            cartProduct.getProductId(),
                            ShoppingCart.convertHexColorToInt(cartProductDto.getColor().getCode()),
                            cartProductDto.getColor().getInStock());
                } else {
                    //  Unlikely to occur, since we already got the product details and compare its
                    //  quantity in stock to the same product in cart. But, still need to be covered.
                    logger.warn("Product \"" + cartProductDto.getProductName() + "\" exists in table and in user " + userId + " cart, but cannot be found using primary-key.");
                }

                responseDto.addCartProduct(cartProductDto.getProductId(),
                        cartProductDto.getProductName(),
                        cartProductDto.getPrice(),
                        cartProductDto.getColor().getInStock(),
                        cartProductDto.getImageUrl(),
                        cartProductDto.getColor().getCode(),
                        cartProductDto.getColor().getName(),
                        cartProductDto.getColor().getInStock());
            }
        }

        //return shoppingCartRepository.verifyProductsQuantitiesInUserCart(userId, shoppingCartProducts);
        return responseDto;

    }

    public ProductDto getProductDtoDetails(Long productId) {
        URL productsPrefixUrl = null;
        URL productByIdUrl = null;
        try {
            productsPrefixUrl = new URL(Url_resources.getUrlCatalog(), CATALOG_PRODUCT);
            logger.debug("productsPrefixUrl=" + productsPrefixUrl.toString());
        } catch (MalformedURLException e) {
            logger.error(productsPrefixUrl, e);
        }
        try {
            productByIdUrl = new URL(productsPrefixUrl, String.valueOf(productId));
            logger.debug("productByIdUrl=" + productByIdUrl.toString());
        } catch (MalformedURLException e) {
            logger.error(productByIdUrl, e);
        }

        ProductDto dto = null;
        try {
            String stringResponse = RestApiHelper.httpGet(productByIdUrl);
            if (stringResponse.equalsIgnoreCase(Constants.NOT_FOUND)) {
                //  Product not found (409)
                dto = new ProductDto(productId, -1L, Constants.NOT_FOUND, -999999.99, Constants.NOT_FOUND, Constants.NOT_FOUND, null, null, null);
            } else {
                dto = getProductDtofromJsonObjectString(stringResponse);
            }
        } catch (IOException e) {
            logger.error("Calling httpGet(" + productByIdUrl.toString() + ") throws IOException: ", e);
        }

        return dto;
    }

    public ShoppingCartResponseDto getUserShoppingCart(long userId) {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.getShoppingCartProductsByUserId(userId);

        ShoppingCartResponseDto shoppingCartResponse = new ShoppingCartResponseDto(userId);
        if ((shoppingCarts != null) && (shoppingCarts.size() > 0)) {
            shoppingCartResponse = getCartProductsDetails(userId, shoppingCarts);
        }

        return shoppingCartResponse;
    }

    public ShoppingCartResponseDto getCartProductsDetails(long userId, List<ShoppingCart> shoppingCarts) {
        /*
        //  Verify userId belongs to a registered user by calling "Account Service"
        //  REST API GET REQUEST using URI
        if (!isRegisteredUserExists(userId)) {
            shoppingCartResponse = new ShoppingCartResponse(false, ShoppingCart.MESSAGE_INVALID_USER_ID, -1);
            return null; //  userId is not a registered user
        }
         */
        ShoppingCartResponseDto userCart = new ShoppingCartResponseDto(userId);

        if (shoppingCarts != null) {
            if ((shoppingCarts.size() > 0) || (!shoppingCarts.isEmpty())) {

                /* Scan user shopping cart and add all product to userCart response object  */
                //for (ShoppingCart cart : shoppingCarts) {
                for (ShoppingCart cart : shoppingCarts) {

                    //ProductDto dto = getCartProductDetails(cart.getProductId(),
                    //                                        ShoppingCart.convertIntColorToHex(cart.getColor()));
                    ShoppingCartResponseDto.CartProduct cartProduct = getCartProductDetails(cart.getProductId(),
                            ShoppingCart.convertIntColorToHex(cart.getColor()).toUpperCase());

                    if (cartProduct.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {
                        userCart.addCartProduct(cartProduct.getProductId(),
                                cartProduct.getProductName(),   //  "NOT FOUND"
                                cartProduct.getPrice(),         //  -999999.99
                                cartProduct.getQuantity(),      //  0
                                cartProduct.getImageUrl(),      //  "NOT FOUND"
                                "000000",
                                "BLACK",
                                0,
                                false); //  isExists = false
                    } else {
                        /*  Add a product to user shopping cart response class  */
                        userCart.addCartProduct(cartProduct.getProductId(),
                                cartProduct.getProductName(),
                                cartProduct.getPrice(),
                                cart.getQuantity(),
                                cartProduct.getImageUrl(),
                                cartProduct.getColor().getCode(),
                                cartProduct.getColor().getName(),
                                cartProduct.getColor().getInStock());
                    }
                }
            }
            //else {
            //    //ShoppingCart.MESSAGE_SHOPPING_CART_IS_EMPTY
            //    userCart.setProductsInCart(new ArrayList<>());
            //}
        }
        //else {
        //    //ShoppingCart.MESSAGE_SHOPPING_CART_IS_EMPTY
        //    userCart.setProductsInCart(new ArrayList<>());
        //}

        //return ((userCart == null) || (userCart.isEmpty())) ? null : userCart;
        return userCart;
    }


    /**
     * Get a single {@code Product} details using <b>REST API</b> {@code GET} request.
     *
     * @param productId Idetity of the product to get details.
     * @return {@link ProductDto} containing the JSON with requsted product details.
     */
    public ShoppingCartResponseDto.CartProduct getCartProductDetails(Long productId, String hexColor) {

        ProductDto dto = this.getProductDtoDetails(productId);

        ShoppingCartResponseDto.CartProduct cartProduct;
        if (dto.getProductName() != null) {
            if (!dto.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {

                ColorAttributeDto colorAttrib = getProductColorAttribute(hexColor.toUpperCase(), dto.getColors());

                if (colorAttrib != null) {
                    cartProduct = new ShoppingCartResponseDto()
                            .createCartProduct(dto.getProductId(),
                                    dto.getProductName(),
                                    dto.getPrice(),
                                    0,
                                    dto.getImageUrl(),
                                    true);

                    cartProduct.setColor(colorAttrib.getCode().toUpperCase(),
                            colorAttrib.getName().toUpperCase(),
                            colorAttrib.getInStock());

                    if (logger.isDebugEnabled()) {
                        StringBuilder sb = new StringBuilder("Received Product information: ");
                        sb.append("\n   product id = ").append(dto.getProductId());
                        sb.append("\n   product name = ").append(dto.getProductName());
                        sb.append("\n   price per item = ").append(dto.getPrice());
                        sb.append("\n   managedImageId = \"").append(dto.getImageUrl()).append("\"");
                        sb.append("\n   ColorAttrubute.Code (hex) = \'").append(colorAttrib.getCode().toUpperCase()).append("\'");
                        sb.append("\n   ColorAttrubute.Color (name) = \"").append(colorAttrib.getName().toUpperCase()).append("\"");
                        sb.append("\n   ColorAttrubute.inStock = ").append(colorAttrib.getInStock());
                        logger.debug(sb);
                    }
                } else {
                    //  Product with specific color NOT FOUND in Product table in CATALOG schema
                    cartProduct = setNotFoundCartProduct(productId);
                }
            } else {
                //  Product with this productId not found in Product table in CATALOG schema (409)
                cartProduct = setNotFoundCartProduct(productId);
            }
        } else {
            //  dto.getProductName() == null
            cartProduct = null;
        }

        return cartProduct;
    }

    public ColorAttributeDto getProductColorAttribute(String hexColor, List<ColorAttributeDto> colors) {
        ColorAttributeDto returnColor = null;

        if ((colors != null) && (colors.size() > 0)) {
            for (ColorAttributeDto color : colors) {
                //  Better to compare integers than Strings - no problem with leading zeros
                /*if (color.getCode().equalsIgnoreCase(hexColor)) {*/
                if (ShoppingCart.convertHexColorToInt(color.getCode()) == ShoppingCart.convertHexColorToInt(hexColor)) {
                    returnColor = color;
                }
            }
        } else {
            logger.warn("Colors list is empty");
        }

        return returnColor;
    }

//    public static String httpGet(URL url) throws IOException {
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        int responseCode = conn.getResponseCode();
//
//        String returnValue;
//        switch (responseCode) {
//            case HttpStatus.SC_OK: {
//                // Buffer the result into a string
//                InputStreamReader inputStream = new InputStreamReader(conn.getInputStream());
//                BufferedReader bufferedReader = new BufferedReader(inputStream);
//                StringBuilder sb = new StringBuilder();
//                String line;
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    sb.append(line);
//                }
//
//                bufferedReader.close();
//                returnValue = sb.toString();
//                logger.debug(returnValue);
//                break;
//            }
//            case HttpStatus.SC_CONFLICT:
//                //  Product not found
//                returnValue = "Not found";
//                logger.warn("Product not found");
//                break;
//            default:
//                IOException e = new IOException(conn.getResponseMessage());
//                logger.error("httpGet -> responseCode=" + responseCode, e);
//                throw e;
//        }
//        conn.disconnect();
//        return returnValue;
//    }

    private static ProductDto getProductDtofromJsonObjectString(String jsonObjectString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ProductDto dto = objectMapper.readValue(jsonObjectString, ProductDto.class);
        return dto;
    }

    private static ColorAttributeDto getColorAttributeDtofromJsonObjectString(String jsonObjectString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ColorAttributeDto dto = objectMapper.readValue(jsonObjectString, ColorAttributeDto.class);
        return dto;
    }

    public ShoppingCartResponseDto.CartProduct setNotFoundCartProduct(Long productId) {
        return new ShoppingCartResponseDto().createCartProduct(productId, Constants.NOT_FOUND, -999999.99, 0, Constants.NOT_FOUND, false);
    }

    public boolean isProductExists(Long productId, String hexColor) {
        boolean result = false;

        ProductDto productDetails = getProductDtoDetails(productId);
        if (!productDetails.getProductName().equalsIgnoreCase(Constants.NOT_FOUND)) {
            List<ColorAttributeDto> colors = productDetails.getColors();
            for (ColorAttributeDto color : colors) {
                //  Better to compare integers than Strings - no problem with leading zeros
                if (ShoppingCart.convertHexColorToInt(color.getCode()) == ShoppingCart.convertHexColorToInt(hexColor)) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ShoppingCartService{" +
                "shoppingCartRepository=" + shoppingCartRepository +
                '}';
    }
}

