package com.advantage.accountsoap.dao.impl;

import com.advantage.accountsoap.dao.AbstractRepository;
import com.advantage.accountsoap.dao.PaymentPreferencesRepository;
import com.advantage.accountsoap.model.PaymentPreferences;
import com.advantage.accountsoap.model.PaymentPreferencesPK;
import com.advantage.accountsoap.services.AccountService;
import com.advantage.accountsoap.util.ArgumentValidationHelper;
import com.advantage.common.enums.PaymentMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
@Qualifier("paymentPreferencesRepository")
@Repository
public class DefaultPaymentPreferencesRepository extends AbstractRepository implements PaymentPreferencesRepository {
    @Autowired
    AccountService accountService;

    @Override
    public int delete(PaymentPreferences... entities) {
        int count = 0;
        for (PaymentPreferences entity : entities) {
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
                count++;
            }
        }

        return count;
    }

    @Override
    public PaymentPreferences delete(long userId, int paymentMethod) {

        PaymentPreferences entity = find(userId, paymentMethod);
        if (entity != null) delete(entity);

        return entity;
    }

    @Override
    public List<PaymentPreferences> getPaymentPreferencesByUserId(long userId) {
        List<PaymentPreferences> accounts = entityManager.createNamedQuery(PaymentPreferences.QUERY_GET_PAYMENT_PREFERENCES_BY_USER_ID, PaymentPreferences.class)
                                                            .setParameter(PaymentPreferences.PARAM_USER_ID, userId)
                                                            .getResultList();

        return accounts.isEmpty() ? null : accounts;
    }

    @Override
    public PaymentPreferences find(long userId, int paymentMethod) {

        ArgumentValidationHelper.validateLongArgumentIsPositive(userId, "payment preferences user-id");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(paymentMethod, "payment preferences payment-method");

        PaymentPreferencesPK paymentPreferencesPk = new PaymentPreferencesPK(userId, paymentMethod);
        PaymentPreferences paymentPreferences = entityManager.find(PaymentPreferences.class, paymentPreferencesPk);

        return paymentPreferences;

    }

    @Override
    public void create(PaymentPreferences entity) {
        entityManager.persist(entity);
    }

    /**
     * Create SafePay Prefered payment method line
     * @param cardNumber MasterCredit card number.
     * @param expirationDate MasterCredit expiration date (MMYYYY).
     * @param cvvNumber MasterCredit CVV number.
     * @param customerName MasterCredit customer name 2-30 characters.
     * @param accountId User id who used this payment method.
     * @return {@link PaymentPreferences}
     */
    @Override
    public PaymentPreferences createMasterCredit(String cardNumber, String expirationDate, String cvvNumber, String customerName, long accountId) {

        //Account account = accountService.getById(accountId);
        //if (account == null) return null;

        PaymentPreferences paymentPreferences = new PaymentPreferences(accountId, cardNumber, expirationDate, cvvNumber, customerName);
        //paymentPreferences.setAccount(account);

        entityManager.persist(paymentPreferences);

        return paymentPreferences;
    }

    /**
     * Create SafePay Prefered payment method line
     * @param safePayUsername   SafePay user name
     * @param accountId         user id who used this payment method
     * @return {@link PaymentPreferences}
     */
    @Override
    public PaymentPreferences createSafePay(String safePayUsername, long accountId) {
        //Account account = accountService.getById(accountId);
        //if (account == null) return null;

        PaymentPreferences paymentPreferences = new PaymentPreferences(accountId, safePayUsername);
        //paymentPreferences.setAccount(account);

        entityManager.persist(paymentPreferences);

        return paymentPreferences;
    }

    @Override
    public PaymentPreferences updateMasterCredit(String cardNumber, String expirationDate,
                                                 String cvvNumber, String customerName, long userId) {

        PaymentPreferences preferences = find(userId, PaymentMethodEnum.MASTER_CREDIT.getCode());
        if (preferences == null) return null;

        preferences.setCardNumber(cardNumber);
        preferences.setExpirationDate(expirationDate);
        preferences.setCvvNumber(cvvNumber);
        preferences.setCustomerName(customerName);

        entityManager.persist(preferences);

        return preferences;
    }

    @Override
    public PaymentPreferences updateSafePay(String safePayUsername, long userId) {

        PaymentPreferences paymentPreferences = find(userId, PaymentMethodEnum.SAFE_PAY.getCode());
        if (paymentPreferences == null) return null;

        paymentPreferences.setSafePayUsername(safePayUsername);
        entityManager.persist(paymentPreferences);

        return paymentPreferences;
    }
}
