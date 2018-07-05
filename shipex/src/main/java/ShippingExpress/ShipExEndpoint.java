package ShippingExpress;

import ShippingExpress.WsModel.*;
import ShippingExpress.config.WebServiceConfig;
import ShippingExpress.model.ShippingExpressService;
import ShippingExpress.util.ArgumentValidationHelper;
import com.advantage.common.Constants;
import com.advantage.common.enums.ResponseEnum;
import https.www_advantageonlinebanking_com.shipex.ShippingCostTransactionType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class ShipExEndpoint {
    final static Logger logger = Logger.getLogger(ShipExEndpoint.class);
    public static final String TRANSACTION_TYPE_SHIPPING_COST = "ShippingCost";
    public static final String TRANSACTION_TYPE_PLACE_SHIPPING_ORDER = "PlaceShippingOrder";
    private ShippingExpressService shippingService;

    public ShipExEndpoint() {
        logger.info(" ********************************** \n" +
                " ****** ShipEx service start ****** \n" +
                " ********************************** ");
    }

    @Autowired
    public ShipExEndpoint(ShippingExpressService shippingService) {
        this();
        this.shippingService = shippingService;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "ShippingCostRequest")
    @ResponsePayload
    public ShippingCostResponse getShippingCost(@RequestPayload ShippingCostRequest request) {
        ShippingCostResponse response = new ShippingCostResponse();
        String validation = ArgumentValidationHelper.shippingCostRequestValidation(request);
        logShippingCostRequest(request);
        response.setSETransactionType(ShippingCostTransactionType.SHIPPING_COST.value());
        if (!validation.equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
            response.setCode(ResponseEnum.ERROR.getStringCode());
            response.setReason(validation);
            logShippingCostResponse(response);
            return response;
        }

        response.setAmount(shippingService.getShippingCost(request.getSEAddress().getCountry(),
                request.getSENumberOfProducts()));
        response.setCurrency(shippingService.getCurrency());
        response.setTransactionDate(shippingService.getFormatTimeNow());
        response.setCode(ResponseEnum.OK.getStringCode());
        logShippingCostResponse(response);

        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "TrackNumberRequest")
    @ResponsePayload
    public TrackNumberResponse getShippingTrackNumber(@RequestPayload TrackNumberRequest request) {
        TrackNumberResponse response = new TrackNumberResponse();
        response.setTrack(shippingService.getTrackNumber());

        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "PlaceShippingOrderRequest")
    @ResponsePayload
    public PlaceShippingOrderResponse placeShippingOrder(@RequestPayload PlaceShippingOrderRequest request) {
        PlaceShippingOrderResponse response = new PlaceShippingOrderResponse();
        String validation = ArgumentValidationHelper.placeShippingOrderRequestValidation(request);
        logPlaceOrderRequest(request);
        response.setSETransactionType(Constants.TRANSACTION_TYPE_PLACE_SHIPPING_ORDER);
        if (!validation.equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
            response.setCode(ResponseEnum.ERROR.getStringCode());
            response.setReason(validation);
            logPlaceOrderResponse(response);
            return response;
        }
        response.setTransactionDate(shippingService.getFormatTimeNow());
        response.setTransactionReference(Long.toString(shippingService.getTrackNumber()));
        response.setCode(ResponseEnum.OK.getStringCode());
        logPlaceOrderResponse(response);

        return response;
    }

    private void logShippingCostRequest(ShippingCostRequest request) {
        String builder = "TransactionType: " + request.getSETransactionType() + " Request" +
                " SENumberOfProducts: " + request.getSENumberOfProducts();
        logger.info(builder);
    }

    private void logShippingCostResponse(ShippingCostResponse response) {
        String builder = "TransactionType: " + response.getSETransactionType() + " Response" +
                "; SEResponse.Code: " + response.getCode() +
                "; SEResponse.Reason: " + response.getReason() +
                "; SEResponse.Amount: " + response.getAmount() +
                "; TransactionDate: " + response.getTransactionDate();
        logger.info(builder);
    }

    private void logPlaceOrderRequest(PlaceShippingOrderRequest request) {
        String builder = "TransactionType: " + request.getSETransactionType() + " Request" +
                "; OrderNumber: " + request.getOrderNumber();

        logger.info(builder);
    }

    private void logPlaceOrderResponse(PlaceShippingOrderResponse response) {
        String builder = "TransactionType: " + response.getSETransactionType() + " Response" +
                "; SEResponse.Code: " + response.getCode() +
                "; SEResponse.Reason: " + response.getReason() +
                "; SETransactionReference: " + response.getTransactionReference() +
                "; TransactionDate: " + response.getTransactionDate();

        logger.info(builder);
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "GetHealthCheckRequest")
    @ResponsePayload
    public GetHealthCheckResponse getHealthCheckResponse(@RequestPayload GetHealthCheckRequest request) {
        GetHealthCheckResponse response = new GetHealthCheckResponse();
        response.setStatus("success");

        return response;
    }
}
