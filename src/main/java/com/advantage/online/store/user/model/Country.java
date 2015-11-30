package com.advantage.online.store.user.model;

import com.advantage.online.store.Constants;
import com.advantage.util.ArgumentValidationHelper;
import com.advantage.util.fs.FileSystemHelper;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Binyamin Regev on 15/11/2015.
 */
@Entity
@Table(name = "COUNTRY")
@NamedQueries({
        @NamedQuery(
                name = Country.QUERY_GET_ALL,
                query = "select c from Country c order by NAME"
        )
        ,@NamedQuery(
                name = Country.QUERY_GET_BY_COUNTRY_NAME,
                query = "select c from Country c where NAME = :" + Country.PARAM_COUNTRY_NAME
        )
        ,@NamedQuery(
                name = Country.QUERY_GET_COUNTRIES_BY_ISO_NAME,
                query = "select c from Country c where ISO_NAME = :" + Country.PARAM_ISO_NAME + " order by NAME"
        )
        ,@NamedQuery(
                name = Country.QUERY_GET_COUNTRIES_BY_PHONE_PREFIX,
                query = "select c from Country c where PHONE_PREFIX = :" + Country.PARAM_PHONE_PREFIX + " order by NAME"
        )
        ,@NamedQuery(
                name = Country.QUERY_GET_COUNTRIES_BY_PARTIAL_NAME,
                query = "select c from Country c where NAME like :" +
                        Country.PARAM_COUNTRY_NAME + " order by NAME"
        )
})
public class Country {

    public static final int MAX_NUM_OF_COUNTRIES = 50;

    public static final String QUERY_GET_ALL = "country.getAll";
    public static final String QUERY_GET_BY_COUNTRY_NAME = "country.getCountryIdByCountryName";
    public static final String QUERY_GET_COUNTRIES_BY_ISO_NAME = "country.getCountriesByIsoName";
    public static final String QUERY_GET_COUNTRIES_BY_PARTIAL_NAME = "country.getCountriesByPartialName";
    public static final String QUERY_GET_COUNTRIES_BY_PHONE_PREFIX = "country.getCountriesByPhonePrefix";


    public static final String FIELD_ID = "ID"; //  COUNTRY_ID

    public static final String FIELD_NAME = "NAME";
    public static final String PARAM_COUNTRY_NAME = "PARAM_COUNTRY_COUNTRY_NAME";

    public static final String FIELD_ISO_NAME = "ISO_NAME";
    public static final String PARAM_ISO_NAME = "PARAM_COUNTRY_ISO_NAME";

    public static final String PARAM_PHONE_PREFIX = "PARAM_COUNTRY_PHONE_PREFIX";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = FIELD_ID)
    private Integer id;

    @Column(name = FIELD_NAME)
    private String name;

    @Column(name = FIELD_ISO_NAME)
    private String isoName;

    @Column(name="PHONE_PREFIX")
    private int phonePrefix;

    public Country() { }

    public Country(String name, int phonePrefix) {
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(name, "country name");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(phonePrefix, "phone prefix");

        this.setName(name);
        this.setIsoName("##");
        this.setPhonePrefix(phonePrefix);
    }
    public Country(String name, String isoName) {
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(name, "country name");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(isoName, "ISO name");

        this.setName(name);
        this.setIsoName(isoName);
        this.phonePrefix = 0;
    }
    public Country(String name, String isoName, int phonePrefix) {
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(name, "country name");
        ArgumentValidationHelper.validateStringArgumentIsNotNullAndNotBlank(isoName, "ISO name");
        ArgumentValidationHelper.validateNumberArgumentIsPositive(phonePrefix, "phone prefix");

        this.setName(name);
        this.setIsoName(isoName);
        this.setPhonePrefix(phonePrefix);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoName() {
        return isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public int getPhonePrefix() {
        return phonePrefix;
    }

    public void setPhonePrefix(int phonePrefix) {
        this.phonePrefix = phonePrefix;
    }

    @Override
    public boolean equals(Object obj) {
        Country compareTo = (Country) obj;
        return (
                (this.getName() == compareTo.getName()) &&
                (this.getIsoName() == compareTo.getIsoName()) &&
                (this.getPhonePrefix() == compareTo.getPhonePrefix())
                );
    }

    @Override
    public String toString() {
        return "Country Information: " +
                "name=\"" + this.getName() + "\" " +
                "ISO name=\"" + this.getIsoName() + "\" " +
                "international code=" + this.getPhonePrefix();
    }

    public static void main(String[] args) {
        //List<String> lines = Country.readFileCsv("/Users/regevb/Downloads/countries_20150630.csv");
        List<String> lines = FileSystemHelper.readFileCsv("/Users/regevb/Downloads/countries_20150630.csv");
        System.out.println("total of " + lines.size() + " countries.");
    }
}
