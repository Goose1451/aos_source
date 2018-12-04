package ShippingExpress.model;

import com.advantage.root.util.RestApiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

/**
 * @author Binyamin Regev Created on 26/03/2016.
 */
@Qualifier("shipexRepository")
@Repository
public class DefaultShippingExpressRepository implements ShippingExpressRepository {
    public static final String SHIPEX_DEFAULT_COST = "shipex.country.defaultCost";
    public static final String SHIPEX_FREE_SHIPPING_OPTION = "shipex.free.numberOfProducts";
    public static final String SHIPEX_CURRENCY_DEFAULT = "shipex.currency.default";
    public static final String SHIPEX_QUANTITY_ENTITLING_FREE_SHIPPING = "ShipEx_Free_Number_Of_Products";
    private Environment env;

    @Autowired
    public DefaultShippingExpressRepository(Environment env) {
        this.env = env;
    }

    @Override
    public double getShippingCostByCountry(String countryIsoName) {
        String cost = env.getProperty(countryIsoName);
        if(cost == null || cost.isEmpty()) return getDefaultShippingCost();

        return Double.parseDouble(cost);
    }

    @Override
    public double getDefaultShippingCost() {
        return Double.parseDouble(env.getProperty(SHIPEX_DEFAULT_COST));
    }

    /**
     * Get quantity of products that entitle <i>Free Shipping</i> based on
     * &lt;ShipEx_Free_Number_Of_Products&gt; configuration parameter. <br/>
     * The parameter must contains a valid int, or the server will return 0. <br/>
     * Default value for the parameter is 1.
     * @return {@code int} value of the configuration parameter or 0 if the
     * value is invalid, meaning negative or not an int.
     */
    @Override
    public int getFreeShippingOption() {
//        return Integer.parseInt(env.getProperty(SHIPEX_FREE_SHIPPING_OPTION));
        return Integer.parseInt(RestApiHelper.getDemoAppConfigParameterValue(SHIPEX_QUANTITY_ENTITLING_FREE_SHIPPING));
    }

    @Override
    public String getCurrency() {
        return env.getProperty(SHIPEX_CURRENCY_DEFAULT);
    }
}
