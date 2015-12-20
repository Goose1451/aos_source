package com.advantage.online.store.init;

import com.advantage.online.store.Constants;
import com.advantage.online.store.dao.category.CategoryRepository;
import com.advantage.online.store.user.model.AppUser;
import com.advantage.online.store.user.model.AppUserType;
import com.advantage.online.store.user.model.Country;
import com.advantage.online.store.user.model.YesNoReply;
import com.advantage.util.fs.FileSystemHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

@Component
public class DataSourceInit4Json {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    CategoryRepository categoryRepository;

    public void init() throws Exception {

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();
        Transaction transaction;

        //  Get countries list in CSV (Comma Separated Values) file
        ClassPathResource filePathCSV = new ClassPathResource("countries_20150630.csv");
        File coutriesCSV = filePathCSV.getFile();

        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        transaction = session.beginTransaction();

        /* Countries */
        List<String> countries = FileSystemHelper.readFileCsv(coutriesCSV.getAbsolutePath());

        if (countries != null) {
            for (String str : countries) {
                String[] substrings = str.split(",");

            /*
             *  substrings[0] = country name in Hebrew
             *  substrings[1] = country name in English
             *  substrings[2] = country ISO name in English
             *  substrings[3] = international phone country code prefix
             */
                System.out.println("Country: " + substrings[1] +
                        Constants.SPACE + substrings[2] +
                        Constants.SPACE + substrings[3]);

                session.persist(new Country(substrings[1], substrings[2], Integer.valueOf(substrings[3])));
            }

            System.out.println("Total of " + countries.size() + " countries loaded");
        }
        else {
            System.out.println("Countries CSV file might be empty.");
        }


        ////  Binyamin Regev 2015-11-18
        //session.persist(new Country("Austria", "at", 43));
        //session.persist(new Country("Australia", "au", 61));
        //session.persist(new Country("Cayman Islands", "ky", 1345));
        //session.persist(new Country("Bahamas", "bs", 1242));
        //session.persist(new Country("Uruguay", "uy", 598));
        //session.persist(new Country("Solomon Islands", "sb", 677));
        //session.persist(new Country("Falkland Islands", "fk", 500));
        //session.persist(new Country("Ukraine", "ua", 380));
        //session.persist(new Country("Cook Islands", "ck", 682));
        //session.persist(new Country("Israel", "il", 972));
        //session.persist(new Country("Canada", "ca", 1));
        //session.persist(new Country("Russia", "ru", 7));
        //session.persist(new Country("United Kingdom", "uk", 44));
        //session.persist(new Country("United States", "us", 1));
        //session.persist(new Country("Iceland", "is", 354));
        //session.persist(new Country("Uzbekistan", "uz", 998));
        ////  Binyamin Regev 2015-11-18 - End

        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "Avinu", "Avraham", "avinu.avraham", "Avraham1", 12, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "Avinu", "itshak", "avinu.itshak", "Itshak1", 12, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com", YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "Avinu", "jakob", "avinu.jakob", "Israel7", 12, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));

        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "imenu", "Sara", "sara.imenu", "Saramom2", 18, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "imenu", "Rivka", "rivka.imenu", "Rivka2", 18, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "imenu", "Lea", "lea.imenu", "Motherlea2", 18, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "imenu", "Rachel", "rachel.imenu", "Rachel21", 18, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));

        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "King", "David", "king.david", "DavidK1", 10, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com",YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "King", "solomon", "king.solomon", "SolomonK2", 10, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com", YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "Queen", "Sheeba", "queen.sheeba", "SheebaQ1", 10, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com", YesNoReply.YES.getReplyTypeChar()));
        session.persist(new AppUser(AppUserType.USER.getAppUserTypeCode(), "Queen", "Bat Sheva", "queen.bat-sheva", "BatShevaQ2", 10, "077-7654321", "Jerusalem Region", "Jerusalem", "address", "9876543", "a@b.com", YesNoReply.YES.getReplyTypeChar()));

        transaction.commit();
    }

    private String getPath() throws IOException {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String fullPath = URLDecoder.decode(path, "UTF-8");
        String pathArr[] = fullPath.split("/target/");
        fullPath = pathArr[0];

        return fullPath.substring(1);

        /*File catalinaBase = new File(System.getProperty("catalina.base")).getAbsoluteFile();
        File propertyFile = new File(catalinaBase, "webapps/root/app");

        return propertyFile.getAbsolutePath();*/

       // return new java.io.File( ".").getCanonicalPath().split("bin")[0];
    }

    /*private Set<ImageAttribute> getImageAttribute(Collection<String> images, Product product) {
        Set<ImageAttribute> imageAttributes = new HashSet<>();
        for (String s : images) {
            ImageAttribute image = new ImageAttribute(s);
            image.setProduct(product);
            imageAttributes.add(image);
        }

        return imageAttributes;
    }*/

   /* private  Set<ColorAttribute> getColorAttributes(Collection<ColorAttribute> colors, Product product) {
        Set<ColorAttribute> colorAttributes = new HashSet<>();
        for (ColorAttribute s : colors) {
            ColorAttribute color = new ColorAttribute(s.getColor());
            color.setProduct(product);
            color.setInStock(s.getInStock());
            colorAttributes.add(color);
        }
        return colorAttributes;
    }*/
}