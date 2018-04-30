package com.advantage.root.util;

import com.advantage.common.Constants;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Binyamin Regev on 19/11/2015.
 */
public class ValidationHelper {
    //  RegEx to match a number with optional minus ('-') sign and decimal dot (.)
    private static final String NUMERIC_PATTERN = "-?\\d+(\\.\\d+)?";

    //  RegEx to match a color RGB hexadecimal value
    private static final String COLOR_HEX_PATTERN = "^#([A-Fa-f0-9]{1,6})$";

    /**
     * RegEx to match a full-name with optional characters: ".'-". <br/>
     * e.g. James T. Kirk, Walter O'Brian, Bat-Sheva
     */
    private static final String FULL_NAME_PATTERN = "^[\\p{Blank}A-Za-z0-9.'_-]{2,30}$";        // old value "^[\\p{L} .'-]+$"

    private static final String MASTER_CREDIT_CVV_NUMBER_PATTERN = "([0-9]{3})";

    private static final String MASTER_CREDIT_CARD_NUMBER_PATTERN = "([0-9]{16})";
    private static final String MASTER_CREDIT_ACCOUNT_NUMBER_PATTERN = "([0-9]{12})";

    //private static final String PHONE_PATTERN = "^\\+([0-9]{1,3})?[-.\\s]\\(?([0-9]{1,3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
    //private static final String PHONE_PATTERN = "((\\+([1-9]{1}[0-9]{0,3})|00[1-9]{3})[-.\\s]?)?\\(?([0-9]{1,3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
    private static final String PHONE_PATTERN = "((\\+([1-9]{1}[0-9]{0,3}))?[-.\\s]?)\\(?([0-9]{1,3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //  Contain 5-15 characters of: Digits 0-9, UPPER case (A-Z), lower case (a-z), underscore (_), dot (.) and hyphen (-)
    private static final String LOGIN_USER_NAME_PATTERN = "^[A-Za-z0-9_.-]{5,15}$";

    /**
     * <ul><code>Password</code> must be compliant with <b>AOS policy</b></ul>:
     * Password length must be 4 to 12 characters which include <ul>at least 1
     * UPPER-case letter</ul> (A-Z), <ul>at least 1 lower-case letter</ul> (a-z)
     * and <ul>at least 1 digit</ul> (0-9).
     */
    //private static final String LOGIN_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%\\.\\-\\+\\*]).{6,20})";
    private static final String LOGIN_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,12})";

    private static final String TIME_24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";

    //  EUROPEAN_DATE_FORMAT = "dd.MM.yyyy" ; AMERICAN_DATE_FORMAT = "MM/dd/yyyy" ; SCANDINAVIAN_DATE_FORMAT = "yyyy-MM-dd"
    private static final String AMERICAN_DATE_PATTERN = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)";
    private static final String EUROPEAN_DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)";
    private static final String SCANDINAVIAN_DATE_PATTERN = "((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])"; //YYYY-MM-DD

    //private static final String SAFE_PAY_ACCOUNT_NUMBER = "843200971";
    private static final String SAFE_PAY_ACCOUNT_NUMBER_PATTERN = "([0-9]{9})";
    private static final String CURRENCY_PATTERN = "[A-Z]{3}";

    private static Pattern pattern;

    private static final Logger logger = Logger.getLogger(ValidationHelper.class);

    public ValidationHelper() {
    }

    /**
     * Checks that {@code userName} is a valid user-name and in compliance with AOS policy.
     */
    public static boolean isValidLogin(final String userName) {
        return isValidByRegExpPattern(LOGIN_USER_NAME_PATTERN, userName);
    }

    /**
     * Checks that {@code password} is a valid password and in compliance AOS policy.
     */
    public static boolean isValidPassword(final String password) {
        return isValidByRegExpPattern(LOGIN_PASSWORD_PATTERN, password);
    }

    public static boolean isValidPhoneNumber(final String phoneNumber) {
        return isValidByRegExpPattern(PHONE_PATTERN, phoneNumber);
    }

    /**
     * Check that {@code e-mail} is a valid e-mail address.
     */
    public static boolean isValidEmail(final String email) {
        return isValidByRegExpPattern(EMAIL_PATTERN, email);
    }

    /**
     * Check that {@code time24h} is a valid 24-hours time format.
     */
    public static boolean isValidTime24h(final String time24h) {
        return isValidByRegExpPattern(TIME_24HOURS_PATTERN, time24h);
    }

    private static boolean isValidByRegExpPattern(String regExp, String string) {
        Pattern pattern = Pattern.compile(regExp);
        final boolean isValid = pattern.matcher(string).matches();

        if (logger.isDebugEnabled()) {
            Priority level;
            String m;
            if (isValid) {
                level = Level.DEBUG;
                m = "match";
            } else {
                level = Level.WARN;
                m = "not match";
            }
            logger.log(level, string + " " + m + " to pattern " + regExp);
        }
        return isValid;
    }

    /**
     * By stringDate returns string date with last day in this month
     */
    public static String getLastDayOfMonthDate(final String stringDate) {
        SimpleDateFormat dateFormat = null;
        if (Pattern.compile(AMERICAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        } else if (Pattern.compile(EUROPEAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        } else if (Pattern.compile(SCANDINAVIAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(convertedDate);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        String newDate = dateFormat.format(c.getTime());

        return newDate;
    }

    /**
     * validate MasterCredit date isnt expired
     */
    public static boolean isMCDateExpired(final String stringDate){
        StringBuilder sb = new StringBuilder();


        switch (stringDate.substring(0, 2)) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                sb = new StringBuilder("31.");
                break;
            case "04":
            case "06":
            case "09":
            case "11":
                sb = new StringBuilder("30.");
                break;
            default:
                //  "02" is left - "28." or "29."
                if (Integer.valueOf(stringDate.substring(2, 6))%4 == 0) {
                    sb = new StringBuilder("29.");
                } else {
                    sb = new StringBuilder("28.");
                }
        }

        sb.append(stringDate.substring(0, 2))
                .append('.')
                .append(stringDate.substring(2, 6));

        Date mcExpirationDate = StringHelper.convertStringToDate(sb.toString(), "dd.MM.yyyy");

        return mcExpirationDate.before(new Date());
    }

    /**
     * Check that {@code stringDate} is a valid date format, either EUROPEAN, AMERICAN or SCANDINAVIAN.
     */
    public static boolean isValidDate(final String stringDate) {
        SimpleDateFormat dateFormat;

        logger.debug("date to check: \'" + stringDate + "\'");

        if (Pattern.compile(AMERICAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        } else if (Pattern.compile(EUROPEAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        } else if (Pattern.compile(SCANDINAVIAN_DATE_PATTERN).matcher(stringDate).matches()) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            //  invalid date format
            return false;
        }

        dateFormat.setLenient(false);
        try {
            dateFormat.parse(stringDate.trim());
        } catch (ParseException pe) {
            logger.error(stringDate + " : false");
            return false;
        }

        logger.debug(stringDate + " : true");
        return true;
    }

    /**
     * Validates <b>MasterCredit</b> card number.
     */
    public static boolean isValidMasterCreditCardNumber(final String cardNumber) {
        return isValidByRegExpPattern(MASTER_CREDIT_CARD_NUMBER_PATTERN, cardNumber);
    }

    /**
     * Validates <b>MasterCredit</b> CVV number.
     */
    public static boolean isValidMasterCreditCVVNumber(final String cvvNumber) {
        return isValidByRegExpPattern(MASTER_CREDIT_CVV_NUMBER_PATTERN, cvvNumber);
    }

    /**
     * Validates <b>MasterCredit</b> card holder full name.
     */
    public static boolean isValidFullName(final String fullName) {
        return isValidByRegExpPattern(FULL_NAME_PATTERN, fullName);
    }

    /**
     * Validates <b>MasterCredit</b> account number.
     */
    public static boolean isValidMasterCreditAccountNumber(final String accountNumber) {
        return isValidByRegExpPattern(MASTER_CREDIT_ACCOUNT_NUMBER_PATTERN, accountNumber);
    }

    /**
     * Validates <b>MasterCredit</b> account number.
     */
    public static boolean isValidSafePayAccountNumber(final String accountNumber) {
        return isValidByRegExpPattern(SAFE_PAY_ACCOUNT_NUMBER_PATTERN, accountNumber);
    }

    /**
     * Validate currency code. For now only <i>USD</i> is a valid currency code.
     */
    public static boolean isValidCurrency(final String currency) {
        return isValidByRegExpPattern(CURRENCY_PATTERN, currency);
    }

    public static boolean isValidColorHexNumber(final String hexColor) {
        boolean isValid = false;

        if (hexColor.startsWith("#")) {
            isValid = isValidByRegExpPattern(COLOR_HEX_PATTERN, hexColor);
        } else {
            isValid = isValidByRegExpPattern(COLOR_HEX_PATTERN, "#" + hexColor);
        }

        return isValid;
    }

    public static boolean isNumeric(final String str) {
        return str.matches(NUMERIC_PATTERN);
    }

    /**
     * Check the users authorization
     *
     * @param session {@link HttpSession} value  from HTTP request
     * @param token   {@link String} token key
     * @return <b>true</b> when {@code token} is a valid
     */
    @Deprecated
    public static boolean isAuthorized(HttpSession session, String token) {
        return !(session.getAttribute(Constants.UserSession.IS_SUCCESS) == null ||
                session.getAttribute(Constants.UserSession.TOKEN) == null ||
                !(Boolean) session.getAttribute(Constants.UserSession.IS_SUCCESS) ||
                session.getAttribute(Constants.UserSession.TOKEN).toString().compareTo(token) != 0);
    }

    public static void main(String[] args) {

        //  Date
        ValidationHelper.isValidDate("29.02.2012");         //  valid EUROPEAN date-format
        ValidationHelper.isValidDate("29.02.2011");         //  invalid EUROPEAN date-format: no Feb 29th in 2011

        ValidationHelper.isValidDate("02/29/2012");         //  valid AMERICAN date-format
        ValidationHelper.isValidDate("02/29/2011");         //  invalid AMERICAN date-format: no Feb 29th in 2011

        ValidationHelper.isValidDate("2012-02-29");         //  valid SCANDINAVIAN date-format
        ValidationHelper.isValidDate("2011-02-29");         //  invalid SCANDINAVIAN date-format: no Feb 29th in 2011

        //  24-hours time
        ValidationHelper.isValidTime24h("11:11:11");        //  true
        ValidationHelper.isValidTime24h("12:34:56");        //  true
        ValidationHelper.isValidTime24h("23:59:59");        //  true
        ValidationHelper.isValidTime24h("23:59:60");        //  false
        ValidationHelper.isValidTime24h("24:00:00");        //  false

        //  login username
        ValidationHelper.isValidLogin("king_david");        //  true
        ValidationHelper.isValidLogin("king.david");        //  true
        ValidationHelper.isValidLogin("king7david");        //  true
        ValidationHelper.isValidLogin("king david");        //  false. SPACE is not allowed

        //  login user password
        ValidationHelper.isValidPassword("King1david");     //  true
        ValidationHelper.isValidPassword("king2David");     //  true
        ValidationHelper.isValidPassword("kingDavid12");    //  false. Password too long

        //  e-mail address
        ValidationHelper.isValidEmail("a#b.com");           //  false, "#" is invalid character
        ValidationHelper.isValidEmail("a@b.com");           //  true
        ValidationHelper.isValidEmail("king.david@gov.il"); //  true

        //  International phone number
        ValidationHelper.isValidPhoneNumber("+1 123 456 7890");
        ValidationHelper.isValidPhoneNumber("+972 54 123 4567");
        ValidationHelper.isValidPhoneNumber("+972 54 1234567");
        ValidationHelper.isValidPhoneNumber("+44 123 4567890");
        ValidationHelper.isValidPhoneNumber("+44 1234567890");
        ValidationHelper.isValidPhoneNumber("+441234567890");

        //  All are public figures
        ValidationHelper.isValidFullName("King David");
        ValidationHelper.isValidFullName("Solomon Ben-David");
        ValidationHelper.isValidFullName("Ben E. King");
        ValidationHelper.isValidFullName("James T. Kirk");
        ValidationHelper.isValidFullName("G'kar");

        ValidationHelper.isValidMasterCreditCVVNumber("404");   //  "0" (zero)
        ValidationHelper.isValidMasterCreditCVVNumber("4O4");   //  "O" (UPPER case) is not numeric
        ValidationHelper.isValidMasterCreditCVVNumber("777");

        ValidationHelper.isValidColorHexNumber("FFFF00");   //  true (Yellow)
        ValidationHelper.isValidColorHexNumber("0000FF");   //  true (Blue)
        ValidationHelper.isValidColorHexNumber("FF");       //  true (Blue)
        ValidationHelper.isValidColorHexNumber("C0C0C0");   //  true (Silver)
        ValidationHelper.isValidColorHexNumber("888888");   //  true
        ValidationHelper.isValidColorHexNumber("FFFFFH");   //  false ("H" is not in Hex
        ValidationHelper.isValidColorHexNumber("FFFFFFH");  //  false ("H" is not in Hex and string is longer than 6 characters)
        ValidationHelper.isValidColorHexNumber("C0C0C0C");  //  false (string is longer than 6 characters)
    }
}
