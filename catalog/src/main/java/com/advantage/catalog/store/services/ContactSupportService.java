package com.advantage.catalog.store.services;

import com.advantage.catalog.store.dao.AbstractRepository;
import com.advantage.common.dto.ContactUsMailRequest;
import com.advantage.common.dto.ContactUsResponse;
import com.advantage.root.util.ArgumentValidationHelper;
import com.advantage.root.util.ValidationHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

/**
 * This is a MOCK service, it's always successful, always returns "OK".
 * @author Binyamin Regev on 02/02/2016.
 */
@Service
public class ContactSupportService extends AbstractRepository {
    private static int SUCCESS = 1;
    private static int FAILURE = -1;

    private static String MESSAGE_SUCCESS = "Thank you for contacting Advantage support.";
    private static String MESSAGE_INVALID_EMAIL_ADDRESS = "Invalid e-mail address.";
    private static String DB_LOCK_FLAG = "database";
    private static String MESSAGE_DB_LOCK_ACTIVATED = "DB lock flag activated successfully.";


    public ContactUsResponse sendMail(ContactUsMailRequest contactUs) {
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(contactUs.getEmail(), "user e-mail address");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(contactUs.getText(), "e-mail text");

        ContactUsResponse response;

        if(contactUs.getEmail().toLowerCase().equals(DB_LOCK_FLAG)){

            SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

            Session session = sessionFactory.openSession();

            Transaction transaction = session.beginTransaction();

            new Thread(() -> {
                entityManager.createNativeQuery("SELECT cpu_load()");
                transaction.commit();
                session.flush();
                session.close();
            }).start();


            response = new ContactUsResponse(true, MESSAGE_DB_LOCK_ACTIVATED, SUCCESS);
            return response;
        }

        if (ValidationHelper.isValidEmail(contactUs.getEmail())) {
            response = new ContactUsResponse(true, MESSAGE_SUCCESS, SUCCESS);
        }
        else {
            response = new ContactUsResponse(false, MESSAGE_INVALID_EMAIL_ADDRESS, FAILURE);
        }

        return response;
    }
}
