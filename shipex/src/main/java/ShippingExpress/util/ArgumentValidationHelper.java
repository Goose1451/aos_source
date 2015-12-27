package ShippingExpress.util;

import ShippingExpress.ShipExEndpoint;
import ShippingExpress.WsModel.PlaceShippingOrderRequest;
import ShippingExpress.WsModel.ShippingCostRequest;
import ShippingExpress.WsModel.ShippingCostResponse;

import java.util.regex.Pattern;

public class ArgumentValidationHelper {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR_COUNTRY_CODE = "ERROR. Country code is empty or not valid";
    public static final String STATUS_ERROR_CITY_VALUE = "ERROR. City value is empty or not valid";
    public static final String STATUS_ERROR_STATE_VALUE = "ERROR. State value is empty or not valid";
    public static final String STATUS_ERROR_ADDRESS_LINE1 = "ERROR. Address Line1 is empty or not valid";
    public static final String STATUS_ERROR_ADDRESS_LINE_2 = "ERROR. Address Line 2 is too long";
    public static final String STATUS_ERROR_AMOUNT_VALUE = "ERROR. Amount value is not valid";
    public static final String STATUS_ERROR_ORDER_NUMBER = "ERROR. OrderNumber value is not valid";
    public static final String ERROR_TRANSACTION_TYPE = "ERROR. Transaction type is not valid";
    public static final int COUNTRY_ORDER_PATTERN = 10;
    public static final int ORDER_NUMBER_PATTERN = 10;
    public static final int ADDRESS_LINE_PATTERN = 50;
    public static final int STATE_PATTERN = 10;
    public static final int CITY_PATTERN = 25;
    public static final int COUNTRY_COST_PATTERN = 2;
    private static final String PHONE_PATTERN = "(^\\+(?:[0-9]){4,19})$";
    public static final String ERROR_PHONE_NUMBER = "ERROR. Invalid phone number format";

    public static String shippingCostRequestValidation(ShippingCostRequest request) {
        if(!countryValidation(request.getSEAddress().getCountry())) {
            return STATUS_ERROR_COUNTRY_CODE;
        }
        if (!cityValidation(request.getSEAddress().getCity())) {
            return STATUS_ERROR_CITY_VALUE;
        }
        if (!stateValidation(request.getSEAddress().getState())) {
            return STATUS_ERROR_STATE_VALUE;
        }
        if (!postalCodeValidation(request.getSEAddress().getPostalCode())) {
            return STATUS_ERROR_STATE_VALUE;
        }
        if (!addressLineValidation(request.getSEAddress().getAddressLine1())) {
            return STATUS_ERROR_ADDRESS_LINE1;
        }
        if (!addressLine2Validation(request.getSEAddress().getAddressLine2())) {
            return STATUS_ERROR_ADDRESS_LINE_2;
        }
        if(!request.getSETransactionType().equalsIgnoreCase(ShipExEndpoint.TRANSACTION_TYPE_SHIPPING_COST)) {
            return ERROR_TRANSACTION_TYPE;
        }
        if(!numberOfProductValidation(request.getSENumberOfProducts())) {
            return "ERROR. Wrong format";
        }
        if(!phoneNumberValidation(request.getSEAddress().getPhone())){
            return ERROR_PHONE_NUMBER;
        }

        return STATUS_OK;
    }

    private static boolean phoneNumberValidation(String phone) {
        return Pattern.compile(PHONE_PATTERN).matcher(phone).matches();
    }

    private static boolean postalCodeValidation(String postalCode) {
        return postalCode != null && postalCode.length() <= 10;
    }

    private static boolean numberOfProductValidation(int value) {
        return Integer.toString(value).length() <= 5;
    }

    public static String shippingCostResponseValidation(ShippingCostResponse response) {
        if(!doubleTryParse(response.getAmount())) return STATUS_ERROR_AMOUNT_VALUE;

        return STATUS_OK;
    }

    public static String placeShippingOrderRequestValidation(PlaceShippingOrderRequest request) {
        if(!orderNumberValidation(request.getOrderNumber())) {
            return STATUS_ERROR_ORDER_NUMBER;
        }
        if (!cityValidation(request.getSEAddress().getCity())) {
            return STATUS_ERROR_CITY_VALUE;
        }
        if(!countryOrderValidation(request.getSEAddress().getCountry())) {
            return STATUS_ERROR_COUNTRY_CODE;
        }
        if (!stateValidation(request.getSEAddress().getState())) {
            return STATUS_ERROR_STATE_VALUE;
        }
        if (!postalCodeValidation(request.getSEAddress().getPostalCode())) {
            return STATUS_ERROR_STATE_VALUE;
        }
        if (!addressLineValidation(request.getSEAddress().getAddressLine1())) {
            return STATUS_ERROR_ADDRESS_LINE1;
        }
        if (!addressLine2Validation(request.getSEAddress().getAddressLine2())) {
            return STATUS_ERROR_ADDRESS_LINE_2;
        }
        if(!phoneNumberValidation(request.getSEAddress().getPhone())){
            return ERROR_PHONE_NUMBER;
        }
        if(!request.getSETransactionType().equalsIgnoreCase(ShipExEndpoint.TRANSACTION_TYPE_PLACE_SHIPPING_ORDER)) {
            return ERROR_TRANSACTION_TYPE;
        }

        return STATUS_OK;
    }

    private static boolean countryOrderValidation(String country) {
        return country != null && !country.isEmpty() && country.length() <= COUNTRY_ORDER_PATTERN;
    }

    private static boolean orderNumberValidation(String value) {
        return value != null && value.length() == ORDER_NUMBER_PATTERN;
    }

    public static boolean doubleTryParse(String value) {
        try {
            Double d = Double.parseDouble(value);
        }
        catch (IllegalArgumentException e)  {
            return false;
        }

        return true;
    }

    private static boolean addressLine2Validation(String addressLine) {
        return addressLine == null || addressLine.length() <= ADDRESS_LINE_PATTERN;
    }

    private static boolean addressLineValidation(String addressLine) {
        return addressLine != null && addressLine.length() <= ADDRESS_LINE_PATTERN;
    }

    private static boolean stateValidation(String state) {
        return state != null && state.length() <= STATE_PATTERN;
    }

    private static boolean cityValidation(String city) {
        return city != null && city.length() <= CITY_PATTERN;
    }

    private static boolean countryValidation(String country) {
        return country != null && country.length() == COUNTRY_COST_PATTERN;
    }
}
