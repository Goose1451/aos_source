package ShippingExpress;

import ShipExServiceClient.ShippingCostTransactionType;
import ShipExServiceClient.TransactionType;
import ShippingExpress.WsModel.*;
import ShippingExpress.model.ShippingExpressRepository;
import ShippingExpress.model.ShippingExpressService;
import com.advantage.common.Url_resources;
import com.advantage.common.enums.ResponseEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShipexApplication.class)
public class ShipexApplicationTests {

    @Autowired
    @Mock
    private ShippingExpressService service;

    private ShippingCostRequest costRequest;
    private PlaceShippingOrderRequest orderRequest;
    private SEAddress address;
    private ShipExEndpoint endpoint;
    private Url_resources url_resources;

    @Before
    public void init() {
        costRequest = new ShippingCostRequest();
        orderRequest = new PlaceShippingOrderRequest();
        address = new SEAddress();
//        service = mock((ShippingExpressService.class));
        endpoint = new ShipExEndpoint(service);

//        url_resources = new Url_resources();
//        MockitoAnnotations.initMocks(url_resources);
//        url_resources.initEnvForTests();
//        url_resources.setConfigForTest();

        address.setAddressLine1("address");
        address.setCity("city");
        address.setCountry("ua");
        address.setPostalCode("123123");
        address.setState("state");

        costRequest.setSETransactionType(ShippingCostTransactionType.SHIPPING_COST.value());
        costRequest.setSEAddress(address);
        costRequest.setSENumberOfProducts(5);
        costRequest.setSECustomerPhone("+1231234567");
        costRequest.setSECustomerName("name");

        orderRequest.setSEAddress(address);
        orderRequest.setSETransactionType(TransactionType.PLACE_SHIPPING_ORDER.value());
        orderRequest.setOrderNumber("1234567890");
        orderRequest.setSECustomerPhone("+1231234567");
        orderRequest.setSECustomerName("name");


//        when(service.getShippingCost("ua", 5))
//                .thenReturn("3");
//        when(service.getCurrency())
//                .thenReturn("3");
    }

//    @Test
//    public void getShippingCostTest() throws IOException {
//        ShippingCostResponse response = endpoint.getShippingCost(costRequest);
//
//        Assert.assertEquals(ResponseEnum.OK.getStringCode(), response.getCode());
//        Assert.assertEquals(true, !response.getAmount().isEmpty());
//        Assert.assertEquals(true, !response.getCurrency().isEmpty());
//        Assert.assertEquals(costRequest.getSETransactionType(), response.getSETransactionType());
//    }

    @Test
    public void shippingCostRequestValidatorTest() {
        //validate transaction type
        ShippingCostRequest request = getDefaultCostRequest();
        request.setSETransactionType("invalid type");
        ShippingCostResponse response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate addressLine1
        request = getDefaultCostRequest();
        request.getSEAddress().setAddressLine1(createStringWithLength(51));
        response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate city
        request = getDefaultCostRequest();
        request.getSEAddress().setCity(createStringWithLength(26));
        response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate state
        request = getDefaultCostRequest();
        request.getSEAddress().setState(createStringWithLength(11));
        response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate number of products
        request = getDefaultCostRequest();
        request.setSENumberOfProducts(123456);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        ////=================================================
        ////No Validation on Phone-number, always TRUE
        ////=================================================
        ////validate phone number
        ////phone !contains "+"
        //request = getDefaultCostRequest();
        //request.setSECustomerPhone("1234567");
        //response = endpoint.getShippingCost(request);
        //Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
        //
        ////phone=5
        //request.setSECustomerPhone("+1234");
        //response = endpoint.getShippingCost(request);
        //Assert.assertEquals(true, response.getCode().contains(ResponseEnum.OK.getStringCode()));
        //
        ////phone > 20
        //request.setSECustomerPhone("+23567890123456789201");
        //response = endpoint.getShippingCost(request);
        //Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
        //
        ////phone<=20
        //request.setSECustomerPhone("+2356789012345678920");
        //response = endpoint.getShippingCost(request);
        //Assert.assertEquals(true, response.getCode().contains(ResponseEnum.OK.getStringCode()));
        //
        ////phone is empty
        //request.setSECustomerPhone("");
        //response = endpoint.getShippingCost(request);
        //Assert.assertEquals(true, response.getCode().contains(ResponseEnum.OK.getStringCode()));

        //validate country alias
        request = getDefaultCostRequest();
        request.getSEAddress().setCountry(createStringWithLength(3));
        response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
        request.getSEAddress().setCountry(createStringWithLength(0));
        response = endpoint.getShippingCost(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
    }

    @Test
    public void PlaceShippingOrderTest() {
        PlaceShippingOrderResponse response = endpoint.placeShippingOrder(orderRequest);
        Assert.assertEquals(ResponseEnum.OK.getStringCode(), response.getCode());
        Assert.assertEquals(true, !response.getTransactionDate().isEmpty());
        Assert.assertEquals(true, !response.getTransactionReference().isEmpty());
        Assert.assertEquals(10, response.getTransactionReference().length());
        Assert.assertEquals(orderRequest.getSETransactionType(), response.getSETransactionType());
    }

    private ShippingCostRequest getDefaultCostRequest() {
        ShippingCostRequest request = new ShippingCostRequest();
        SEAddress seAddress = new SEAddress();
        seAddress.setAddressLine1(costRequest.getSEAddress().getAddressLine1());
        seAddress.setCountry(costRequest.getSEAddress().getCountry());
        seAddress.setState(costRequest.getSEAddress().getState());
        seAddress.setPostalCode(costRequest.getSEAddress().getPostalCode());
        seAddress.setCity(costRequest.getSEAddress().getCity());

        request.setSEAddress(seAddress);
        request.setSETransactionType(costRequest.getSETransactionType());
        request.setSENumberOfProducts(5);
        request.setSECustomerPhone(costRequest.getSECustomerPhone());
        request.setSECustomerName(costRequest.getSECustomerName());

        return request;
    }

    private PlaceShippingOrderRequest getDefaultOrderRequest() {
        PlaceShippingOrderRequest request = new PlaceShippingOrderRequest();
        request.setOrderNumber(orderRequest.getOrderNumber());
        SEAddress seAddress = new SEAddress();
        seAddress.setAddressLine1(orderRequest.getSEAddress().getAddressLine1());
        seAddress.setCountry(orderRequest.getSEAddress().getCountry());
        seAddress.setState(orderRequest.getSEAddress().getState());
        seAddress.setPostalCode(orderRequest.getSEAddress().getPostalCode());
        seAddress.setCity(orderRequest.getSEAddress().getCity());

        request.setSEAddress(seAddress);
        request.setSETransactionType(orderRequest.getSETransactionType());
        request.setSECustomerPhone(orderRequest.getSECustomerPhone());
        request.setSECustomerName(request.getSECustomerName());

        return request;
    }

    @Test
    public void orderRequestValidatorTest() {
        //validate transaction type
        PlaceShippingOrderRequest request = getDefaultOrderRequest();

        request.setSETransactionType("invalid type");
        PlaceShippingOrderResponse response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate addressLine1
        request = getDefaultOrderRequest();
        request.getSEAddress().setAddressLine1(createStringWithLength(51));
        response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate city
        request = getDefaultOrderRequest();
        request.getSEAddress().setCity(createStringWithLength(26));
        response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate state
        request = getDefaultOrderRequest();
        request.getSEAddress().setState(createStringWithLength(11));
        response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));

        //validate country alias
        request = getDefaultOrderRequest();
        request.getSEAddress().setCountry(createStringWithLength(11));
        response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
        request.getSEAddress().setCountry(createStringWithLength(0));
        response = endpoint.placeShippingOrder(request);
        Assert.assertEquals(true, response.getCode().contains(ResponseEnum.ERROR.getStringCode()));
    }

    private String createStringWithLength(int i) {
        StringBuilder builder = new StringBuilder(i);
        for (int j = 0; j < i; j++) {
            builder.append("s");
        }

        return builder.toString();
    }


}
