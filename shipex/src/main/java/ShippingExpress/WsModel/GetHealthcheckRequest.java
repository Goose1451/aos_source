package ShippingExpress.WsModel;

import ShippingExpress.config.WebServiceConfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by ederymi on 9/18/2017.
 */
public class GetHealthcheckRequest {
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    @XmlRootElement(name = "GetHealthcheckRequest", namespace = WebServiceConfig.NAMESPACE_URI)
    public class GetCountriesRequest {
    }
}
