/**
* Created by kubany on 10/11/2015.
*/
package com.advantage.online.store.init;

import com.advantage.online.store.model.Category;
import com.advantage.online.store.model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class DataSourceInit {
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    public void init() throws Exception {

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        Category category = new Category("LAPTOPS", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Laptop.jpg"));
        session.persist(new Category("HEADPHONES", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Headphones.png")));
        session.persist(new Category("TABLETS", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Tablet.jpg")));
        session.persist(new Category("SPEAKERS", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Speakers.png")));
        session.persist(category);



        session.persist(new Category("MICE", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Mice.png")));
        session.persist(new Category("BAGS & CASES", readBytesFromFile("C:/Users/kubany/dev/AOSRES/Bags.png")));

        session.persist(new Product("HP EliteBook Folio", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 550, category));
        session.persist(new Product("LG G3", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 400, category));
        session.persist(new Product("Samsung Galaxy S5", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 600, category));
        session.persist(new Product("Samsung Galaxy S4", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 300, category));
        session.persist(new Product("iPhone 6S", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 1000, category));

        transaction.commit();
    }

    private static byte[] readBytesFromFile(String filePath) throws IOException {
        File inputFile = new File(filePath);
        FileInputStream inputStream = new FileInputStream(inputFile);

        byte[] fileBytes = new byte[(int) inputFile.length()];
        inputStream.read(fileBytes);
        inputStream.close();

        return fileBytes;
    }
}


