
package ShipExServiceClient;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ShipExPort", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ShipExPort {


    /**
     * 
     * @param trackNumberRequest
     * @return
     *     returns ShipExServiceClient.TrackNumberResponse
     */
    @WebMethod(operationName = "TrackNumber")
    @WebResult(name = "TrackNumberResponse", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "TrackNumberResponse")
    public TrackNumberResponse trackNumber(
        @WebParam(name = "TrackNumberRequest", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "TrackNumberRequest")
        TrackNumberRequest trackNumberRequest);

    /**
     * 
     * @param placeShippingOrderRequest
     * @return
     *     returns ShipExServiceClient.PlaceShippingOrderResponse
     */
    @WebMethod(operationName = "PlaceShippingOrder")
    @WebResult(name = "PlaceShippingOrderResponse", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "PlaceShippingOrderResponse")
    public PlaceShippingOrderResponse placeShippingOrder(
        @WebParam(name = "PlaceShippingOrderRequest", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "PlaceShippingOrderRequest")
        PlaceShippingOrderRequest placeShippingOrderRequest);

    /**
     * 
     * @param shippingCostRequest
     * @return
     *     returns ShipExServiceClient.ShippingCostResponse
     */
    @WebMethod(operationName = "ShippingCost")
    @WebResult(name = "ShippingCostResponse", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "ShippingCostResponse")
    public ShippingCostResponse shippingCost(
        @WebParam(name = "ShippingCostRequest", targetNamespace = "https://www.AdvantageOnlineBanking.com/ShipEx/", partName = "ShippingCostRequest")
        ShippingCostRequest shippingCostRequest);

}