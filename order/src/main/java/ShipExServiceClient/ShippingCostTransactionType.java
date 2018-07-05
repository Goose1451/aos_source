
package ShipExServiceClient;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ShippingCostTransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ShippingCostTransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ShippingCost"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ShippingCostTransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
@XmlEnum
public enum ShippingCostTransactionType {

    @XmlEnumValue("ShippingCost")
    SHIPPING_COST("ShippingCost");
    private final String value;

    ShippingCostTransactionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ShippingCostTransactionType fromValue(String v) {
        for (ShippingCostTransactionType c: ShippingCostTransactionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
