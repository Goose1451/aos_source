package ShippingExpress.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShippingExpressService {
    public static final int TRACK_NUMBER_LENGTH = 10;
    public static final String DATE_FORMAT = "ddMMyyyy";
    private static AtomicLong trackNumber = new AtomicLong(getStarterTrackNumber());

    private ShippingExpressRepository shipexRepository ;
    private long starterTrackNumber;

    @Autowired
    public ShippingExpressService(DefaultShippingExpressRepository shipexRepository) {
        this.shipexRepository = shipexRepository;
    }



    /**
     * Determines shipping cost according to country and orders quantity
     * @param countryIsoName {@link String} ISO name of country
     * @param quantity {@link Integer} products quantity in the order
     * @return {@link Double} shipping cost
     */
    public String getShippingCost(String countryIsoName, int quantity){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if(quantity == shipexRepository.getFreeShippingOption()) return decimalFormat.format(0.0);

        return decimalFormat.format(shipexRepository.getShippingCostByCountry(countryIsoName));
    }

    /**
     * Generate track number
     * @return {@link String} 10 digits incremented number
     */
    public long getTrackNumber() {
        return trackNumber.incrementAndGet();
    }

    /**
     * Determines currency name
     * @return {@link String} 3 chars Currency
     */
    public String getCurrency() {
        String currency = shipexRepository.getCurrency();

        if(currency != null && !currency.isEmpty()) return currency;

        return "ERROR";
    }

    /**
     * Determines date in a specified format
     * @return {@link} formatted Date
     */
    public String getFormatTimeNow() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return date.format(timeFormatter);
    }

    private String getRandomNumber(int digitCount) {
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(digitCount);
        for(int i=0; i < digitCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));

        return sb.length() < digitCount ? sb.append("0").toString() : sb.toString();
    }

    private static long getStarterTrackNumber() {
        String digits = String.valueOf(new Date().getTime()).substring(0, 10);
        return Long.parseLong(digits);
    }
}
