package com.advantage.catalog_test.online.store.dao;

import java.io.IOException;
import java.util.List;

import com.advantage.catalog.store.model.product.Product;
import com.advantage.catalog_test.cfg.AdvantageTestContextConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import com.advantage.catalog.store.dao.category.CategoryRepository;
import com.advantage.catalog.store.dao.product.ProductRepository;
import com.advantage.catalog.store.model.category.Category;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author Binyamin Regev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AdvantageTestContextConfiguration.class})
public class ProductRepositoryTests extends GenericRepositoryTests {

    public static final String CATEGORY_NAME = "LAPTOPS";
    public static final String IMAGE_ID = "1234";
    public static final String PRODUCT_NAME = "LG G3";
    public static final String PRODUCT_STATUS_BAD = "Test";
    public static final String PRODUCT_STATUS_ACTIVE = "Active";
    public static final String PRODUCT_STATUS_BLOCK = "Block";
    public static final String PRODUCT_STATUS_OUT_OF_STOCK = "OutOfStock";
    public static final String PRODUCT_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
    public static final double PRODUCT_PRICE = 400;
    @Qualifier("categoryRepository")
    @Autowired
    private CategoryRepository categoryRepository;

    @Qualifier("productRepository")
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testGetCategoryProductsFetch() {

        final TransactionStatus transactionStatusForCreation = transactionManager.getTransaction(transactionDefinition);
        final Category category1 = categoryRepository.createCategory("LAPTOPS", "1234");
        final Category category2 = categoryRepository.createCategory("CELL PHONES", "1234");
        final Category category3 = categoryRepository.createCategory("KEYBOARDS", "1234");
        final int CATEGORY1_PRODUCTS_COUNT = 10;
        final int CATEGORY2_PRODUCTS_COUNT = 5;

        for (int i = 0; i < CATEGORY1_PRODUCTS_COUNT; i++) {
            final String productName = "product" + i;
            productRepository.create("LG G3", productName, 400, "1234", category1);
        }

        for (int i = 0; i < CATEGORY2_PRODUCTS_COUNT; i++) {
            final String nameAndDescriptionForTest = "product" + i;
            productRepository.create(nameAndDescriptionForTest,
                    nameAndDescriptionForTest, 400, "1234", category2);
        }

        transactionManager.commit(transactionStatusForCreation);

        final List<Product> category1Products = productRepository.getCategoryProducts(category1);
        Assert.assertEquals(CATEGORY1_PRODUCTS_COUNT, category1Products.size());

        final List<Product> category2Products = productRepository.getCategoryProducts(category2);
        Assert.assertEquals(CATEGORY2_PRODUCTS_COUNT, category2Products.size());

        final List<Product> category3Products = productRepository.getCategoryProducts(category3);
        Assert.assertTrue(category3Products.isEmpty());

        final TransactionStatus transactionStatusForDeletion = transactionManager.getTransaction(transactionDefinition);
        productRepository.delete(category1Products);
        productRepository.delete(category2Products);
        categoryRepository.delete(category1, category2, category3);
        transactionManager.commit(transactionStatusForDeletion);
    }

    @Test
    public void testProductCreate() throws IOException {

        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatusForCreation = transactionManager.getTransaction(transactionDefinition);
        final Category category = categoryRepository.createCategory(CATEGORY_NAME, IMAGE_ID);
        final Product product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID,
                category);

        transactionManager.commit(transactionStatusForCreation);
        long id = product.getId();
        Assert.assertNotNull(product);
        Assert.assertNotNull(id);
        Assert.assertEquals(PRODUCT_NAME, product.getProductName());
        Assert.assertEquals(PRODUCT_DESCRIPTION, product.getDescription());
        Assert.assertEquals(PRODUCT_PRICE, product.getPrice(), 0.5);
        Assert.assertEquals(IMAGE_ID, product.getManagedImageId());
        final TransactionStatus transactionStatusForDeletion = transactionManager.getTransaction(transactionDefinition);

        productRepository.delete(product);
        categoryRepository.delete(category);
        transactionManager.commit(transactionStatusForDeletion);
    }

    @Test
    public void testProductStatus() throws IOException {

        final TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        final TransactionStatus transactionStatusForCreation = transactionManager.getTransaction(transactionDefinition);
        final Category category = categoryRepository.createCategory(CATEGORY_NAME, IMAGE_ID);

        //test set productStatus BAD
       // System.out.println("create product with productStatus BAD");
        Product product = null;
        //System.out.println("product is null. [product] "+product);
        Assert.assertNull("Error! Expecting product null but got not null. [product] "+product, (product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID,category,PRODUCT_STATUS_BAD)));

        //test set productStatus empty string ""
        //System.out.println("create product with productStatus string empty");
        Assert.assertNull("Error! Expecting product null but got not null. [product] "+product, (product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID, category,"")));
        //product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID,

        //System.out.println("test, productStatus Active");
        //product have productStatus Active(true)
        Assert.assertNotNull("Error! Expecting true but got false. [productStatus] " + product, (product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID, category,PRODUCT_STATUS_ACTIVE)));


        //System.out.println("test, productStatus Block");
        //product have productStatus Block(true)
        Assert.assertNotNull("Error! Expecting true but got false. [productStatus] " + product, (product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID, category,PRODUCT_STATUS_BLOCK)));

       // System.out.println("test, productStatus OutOfStock");
        //product have productStatus OutOfStock(true)
        Assert.assertNotNull("Error! Expecting true but got false. [productStatus] " + product, (product = productRepository.create(PRODUCT_NAME, PRODUCT_DESCRIPTION, PRODUCT_PRICE, IMAGE_ID, category,PRODUCT_STATUS_OUT_OF_STOCK)));







        transactionManager.commit(transactionStatusForCreation);
        System.out.println("transactionManager.commit(transactionStatusForCreation);");

        final TransactionStatus transactionStatusForDeletion = transactionManager.getTransaction(transactionDefinition);

        productRepository.delete(product);
        System.out.println("productRepository.delete(product);");
        categoryRepository.delete(category);
        System.out.println("categoryRepository.delete(category);");
        transactionManager.commit(transactionStatusForDeletion);


    }


}