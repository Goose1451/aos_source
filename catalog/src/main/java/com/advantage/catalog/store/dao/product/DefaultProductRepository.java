package com.advantage.catalog.store.dao.product;

import com.advantage.catalog.store.dao.AbstractRepository;
import com.advantage.catalog.store.image.ImageManagement;
import com.advantage.catalog.store.model.attribute.Attribute;
import com.advantage.catalog.store.model.category.Category;
import com.advantage.catalog.store.model.category.CategoryAttributeFilter;
import com.advantage.catalog.store.model.deal.Deal;
import com.advantage.catalog.store.model.product.*;
import com.advantage.catalog.store.services.AttributeService;
import com.advantage.catalog.store.services.CategoryService;
import com.advantage.catalog.store.services.ProductService;
import com.advantage.catalog.util.ArgumentValidationHelper;
import com.advantage.catalog.util.JPAQueryHelper;
import com.advantage.common.Constants;
import com.advantage.common.dto.*;
import com.advantage.common.enums.ProductStatusEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.mapping.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Qualifier("productRepository")
@Repository
public class DefaultProductRepository extends AbstractRepository implements ProductRepository {

    private static final int TOTAL_CATEGORIES_COUNT = 5;
    private static final int TOTAL_ATTRIBUTES_COUNT = 17;
    private static final int MAX_NUM_OF_PRODUCTS = 100;

    private static final Logger logger = Logger.getLogger(DefaultProductRepository.class);

    /**
     * Create Product entity
     *
     * @param name            {@link String} product name
     * @param description     {@link String} product description
     * @param price           {@link Integer} product price
     * @param imgUrl
     * @param category        {@link Category} category which be related with product
     * @param productStatus @return entity reference
     */

    @Autowired
    private CategoryService categoryService;
    @Autowired
    public ProductService productService;
    @Autowired
    public AttributeService attributeService;
    @Autowired
    private Environment environment;

    @Override
    public Product create(String name, String description, double price, String imgUrl, Category category, String productStatus) {
        //validate productStatus
        if(!ProductStatusEnum.contains(productStatus)) return null;
        Product product = new Product(name, description, price, category,productStatus);
        if(imgUrl.isEmpty())
            product.setManagedImageId(Constants.NO_IMAGE_AVAILABLE_ID);
        else
            product.setManagedImageId(imgUrl);
        entityManager.persist(product);

        return product;
    }

    private Set<ColorAttribute> getColorAttributes(Collection<ColorAttributeDto> colors, Product product) {
        Set<ColorAttribute> colorAttributes = new HashSet<>();

        for (ColorAttributeDto s : colors) {
            if (!(s.getInStock() > 0)) {
                String stringInt = environment.getProperty(Constants.ENV_PRODUCT_INSTOCK_DEFAULT_VALUE) == null ? "0" : environment.getProperty(Constants.ENV_PRODUCT_INSTOCK_DEFAULT_VALUE);
                s.setInStock(Integer.parseInt(stringInt));
            }
            Optional<ColorAttribute> attribute =
                    colorAttributes.stream().filter(x -> x.getName().equalsIgnoreCase(s.getName())).findFirst();
            if (attribute.isPresent() && attribute.get().getInStock() != s.getInStock()) {
                attribute.get().setInStock(s.getInStock());
            }
            s.setName(s.getName().toUpperCase());
            s.setCode(s.getCode().toUpperCase());
            ColorAttribute colorAttribute = new ColorAttribute(s.getName(), s.getCode(), s.getInStock());
            colorAttribute.setProduct(product);
            colorAttributes.add(colorAttribute);
        }

        return colorAttributes;
    }

    public List<ColorAttribute> getColorAttributeByProductIdAndColorCode(Long productId, String hexColor) {
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        StringBuilder sqlQuery = new StringBuilder("SELECT ca.code, ca.name, ca.instock ")
                .append("from colorattribute ca ")
                .append("where ca.product_id")
                .append(" = ")
                .append(productId.longValue())
                .append(" and ca.code")
                .append(" = ")
                .append("'").append(hexColor).append("'");

        org.hibernate.Query query = session.createSQLQuery(sqlQuery.toString());
        List resultSet =  session.createSQLQuery(sqlQuery.toString()).list();

        List<ColorAttribute> colorAttributeList = new ArrayList<>();

        for(Object object : resultSet) {
            System.out.println(object);

            Object[] row = (Object[]) object;

            ColorAttribute colorAttribute = new ColorAttribute((String)row[1], (String)row[0], ((Integer)row[2]).intValue());

            colorAttributeList.add(colorAttribute);
        }

        transaction.commit();
        session.close();

        return colorAttributeList.size() != 0 ? colorAttributeList : null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Product update(ProductDto dto, long id) {
        Product product = get(id);
        Category category = categoryService.getCategory(dto.getCategoryId());

        if(!ProductStatusEnum.contains(product.getProductStatus()) || product==null) return null;

        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        String imageUrl = dto.getImageUrl();


        product.setManagedImageId(dto.getImageUrl());
        product.setCategory(category);
        product.setProductStatus(dto.getProductStatus());

        for (AttributeItem item : dto.getAttributes()) {
            String attrName = item.getAttributeName();
            String attrValue = item.getAttributeValue();

            ProductAttributes productAttributes = new ProductAttributes();
            boolean isAttributeExist = product.getProductAttributes().stream().
                    anyMatch(i -> i.getAttribute().getName().equalsIgnoreCase(attrName));

            if (isAttributeExist) {
                productAttributes = product.getProductAttributes().stream().
                        filter(x -> x.getAttribute().getName().equalsIgnoreCase(attrName)).
                        findFirst().get();

                productAttributes.setAttributeValue(attrValue);
            }

            Attribute attribute = productService.getAttributeByDto(item);
            if (attribute == null) {
                attribute = attributeService.createAttribute(attrName);
            }

            productAttributes.setAttributeValue(attrValue);
            productAttributes.setProduct(product);

            productAttributes.setAttribute(attribute);
            product.getProductAttributes().add(productAttributes);
        }

        //  Binyamin Regev 2016-03-23: Try to save new images without dragging the old ones too
        //Set<ImageAttribute> imageAttributes = new HashSet<>(product.getImages());
        boolean found = false;
        Set<ImageAttribute> imageAttributes = new HashSet<>();
        for (String s : dto.getImages()) {
            ImageAttribute imageAttribute = new ImageAttribute(s);
            imageAttribute.setProduct(product);
            imageAttributes.add(imageAttribute);
            if(s.contains(imageUrl))
                found = true;
        }
        if(!found){
            if(dto.getImages().isEmpty())
                product.setManagedImageId(Constants.NO_IMAGE_AVAILABLE_ID);
            else{
                String managedImage = dto.getImages().get(0).split("##")[1];
                managedImage = managedImage.contains("jpg") ? managedImage.split(".jpg")[0] : managedImage;
                product.setManagedImageId(managedImage);
            }

        }
        product.setImages(imageAttributes);

        //product.setColors(productService.getColorAttributes(dto.getColors(), product));
        product.setColors(this.getColorAttributes(dto.getColors(), product));



        entityManager.persist(product);

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return product;
    }

    @Override
    public Product create(String name, String description, double price, String imgUrl, Category category) {
        Product product = new Product(name, description, price, category);
        product.setManagedImageId(imgUrl);
        entityManager.persist(product);

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return product;
    }

    @Override
    public Long create(Product product) {
        entityManager.persist(product);

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return product.getId();
    }

    @Override
    public int deleteByIds(Collection<Long> ids) {
        ArgumentValidationHelper.validateCollectionArgumentIsNotNullAndNotEmpty(ids, "product ids");
        String hql = JPAQueryHelper.getDeleteByPkFieldsQuery(Product.class, Product.FIELD_ID, Product.PARAM_ID);
        Query query = entityManager.createQuery(hql);
        query.setParameter(Product.PARAM_ID, ids);

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> getCategoryProducts(Long categoryId) {
        ArgumentValidationHelper.validateArgumentIsNotNull(categoryId, "category id");
        String hql = getSelectProductsByCategoryIdHql();
        Query query = entityManager.createQuery(hql);
        query.setParameter(Product.PARAM_CATEGORY_ID, categoryId);
        List<Product> products=query.getResultList();
        return products;
    }

    @Override
    public List<Product> getCategoryProducts(Category category) {
        ArgumentValidationHelper.validateArgumentIsNotNull(category, "category");
        Long categoryId = category.getCategoryId();

        return getCategoryProducts(categoryId);
    }

    private String getSelectProductsByCategoryIdHql() {
        StringBuilder hql = new StringBuilder("FROM ")
                .append(Product.class.getName())
                .append(" P")
                .append(" WHERE ")
                .append("UPPER(P.active)")
                .append(" = ")
                .append("'Y'")
                .append(" AND ")
                .append("UPPER(P.productStatus)")
                .append(" <> ")
                .append("'BLOCK'")
                .append(" AND P.")
                .append(Product.FIELD_CATEGORY_ID)
                .append(" = :")
                .append(Product.PARAM_CATEGORY_ID)
                .append(" ORDER BY P.productName"); //add by Moti

        return hql.toString();
    }

    //@Override
    //public int delete(Product... entities) {
    //    ArgumentValidationHelper.validateArrayArgumentIsNotNullAndNotZeroLength(entities, "products");
    //    int productsCount = entities.length;
    //    Collection<Long> productIds = new ArrayList<>(productsCount);
    //
    //    for (final Product product : entities) {
    //        productIds.add(product.getId());
    //    }
    //
    //    return deleteByIds(productIds);
    //}
    @Override
    public int delete(Product... entities) {
        int count = 0;
        for (Product entity : entities) {
            if (entityManager.contains(entity)) {
                entity.setActive('N');              //  Logical DELETE
                entityManager.persist(entity);
                count++;
            }
        }

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return count;
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = entityManager.createNamedQuery(Product.QUERY_GET_ALL, Product.class)
                .getResultList();
        return products.isEmpty() ? null : products;
    }

    @Override
    public List<Product> filterByName(String pattern, int quantity) {
        List<Product> products = entityManager.createNamedQuery(Product.PRODUCT_FILTER_BY_NAME, Product.class)
                .setParameter("pname", "%" + pattern.toUpperCase() + "%")
                .setMaxResults(MAX_NUM_OF_PRODUCTS)
                .getResultList();

        return products.isEmpty() ? null : products;
    }

    @Override
    public List<Product> filterByName(String pattern) {
        List<Product> products = entityManager.createNamedQuery(Product.PRODUCT_FILTER_BY_NAME, Product.class)
                .setParameter("pname", "%" + pattern.toUpperCase() + "%")
                .getResultList();

        return products.isEmpty() ? null : products;
    }

    @Override
    public List<Product> filterByCategoryId(Long categoryId, int quantity) {
        List<Product> products = entityManager.createNamedQuery(Product.PRODUCT_FILTER_BY_CATEGORY_ID, Product.class)
                .setParameter(Product.PARAM_CATEGORY_ID, categoryId)
                .setMaxResults(MAX_NUM_OF_PRODUCTS)
                .getResultList();

        return products.isEmpty() ? null : products;
    }

    @Override
    public List<Product> filterByCategoryId(Long categoryId) {
        List<Product> products = entityManager.createNamedQuery(Product.PRODUCT_FILTER_BY_CATEGORY_ID, Product.class)
                .setParameter(Product.PARAM_CATEGORY_ID, categoryId)
                .getResultList();

        return products.isEmpty() ? null : products;
    }

    @Override
    public Product get(Long entityId) {
        ArgumentValidationHelper.validateArgumentIsNotNull(entityId, "product id");
        String hql = JPAQueryHelper.getSelectActiveByPkFieldQuery(Product.class, Product.FIELD_ID, entityId);
        Query query = entityManager.createQuery(hql);
        List<Product> productList = query.getResultList();
        return productList.size() != 0 ? productList.get(0) : null;
    }

    @Override
    public int delete(final Collection<Product> products) {
        ArgumentValidationHelper.validateCollectionArgumentIsNotNullAndNotEmpty(products,
                "products");
        final int productsCount = products.size();
        final Collection<Long> productIds = new ArrayList<>(productsCount);

        for (final Product product : products) {

            final long productId = product.getId();
            productIds.add(productId);
        }

        //  Update TIMESTAMP of Last-Update "Data"
        LastUpdate lastUpdate = this.getLastUpdate(1L);
        if (lastUpdate != null) {
            lastUpdate.setLastUpdate(new Date().getTime());
            entityManager.persist(lastUpdate);
        }

        return deleteByIds(productIds);
    }

    public CatalogResponse dbRestoreFactorySettings() {

        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);

        Session session = sessionFactory.openSession();

        Transaction transaction = session.beginTransaction();

        //  region TRUNCATE_CATALOG_TABLES()
        //entityManager.createNativeQuery(statement).executeUpdate();
        //int result = session.createSQLQuery(statement).executeUpdate();
        String resultTruncate = (String) entityManager.createNativeQuery("SELECT public.truncate_catalog_tables()")
                .getSingleResult();
        transaction.commit();
        session.flush();
        session.close();

        StringBuilder sb = new StringBuilder("Database Restore Factory Settings - CATALOG schema truncated successfully. ");
        logger.info("Database Restore Factory Settings - CATALOG schema truncated successfully.");
        System.out.println("Database Restore Factory Settings - CATALOG schema truncated successfully");
        //  endregion

        sb.append("Database Restore Factory Settings: ");

        //  region CATEGORY
        final Category category1 = new Category("LAPTOPS", "1235");
        final Category category2 = new Category("HEADPHONES", "1234");
        final Category category3 = new Category("TABLETS", "1236");
        final Category category4 = new Category("SPEAKERS", "1237");
        final Category category5 = new Category("MICE", "1238");

        entityManager.persist(category1);
        entityManager.persist(category2);
        entityManager.persist(category3);
        entityManager.persist(category4);
        entityManager.persist(category5);

        if (categoryService.getAllCategories().size() == TOTAL_CATEGORIES_COUNT) {
            sb.append("Category").append(Constants.COMMA).append(Constants.SPACE);
            System.out.println("Database Restore Factory Settings successful - table 'category'");
            logger.info("Database Restore Factory Settings successful - table 'category'");
        } else {
            sb.append("Table 'category' - FAILED").append(Constants.COMMA).append(Constants.SPACE);
            System.out.println("Database Restore Factory Settings - table 'category' - FAILED");
            logger.warn("Database Restore Factory Settings - table 'category' - FAILED");
            return new CatalogResponse(false, "Database Restore Factory Settings - table 'category'", -1);
        }
        //  endregion

        //  region ATTRIBUTE
        String[] attributes = new String[] {"Graphics", "Customization", "Operating System", "Processor", "Memory", "Display", "Connector", "Compatibility", "Weight", "Wireless technology", "Sensor resolution", "Type", "Manufacturer", "Scroller Type", "Display Size", "Display Resolution", "Touchscreen" };
        Map<String, Attribute> defAttributes = new HashMap<>();

        for (String attrib : attributes) {
            Attribute attribute = new Attribute(attrib);
            entityManager.persist(attribute);
            defAttributes.put(attrib.toUpperCase(), attribute);
        }

        if (attributeService.getAllAttributes().size() == TOTAL_ATTRIBUTES_COUNT) {
            sb.append("Attribute").append(Constants.COMMA).append(Constants.SPACE);
            System.out.println("Database Restore Factory Settings successful - table 'attribute'");
            logger.info("Database Restore Factory Settings successful - table 'attribute'");
        } else {
            sb.append("Table 'attribute' - FAILED").append(Constants.COMMA).append(Constants.SPACE);
            System.out.println("Database Restore Factory Settings - table 'attribute' - FAILED");
            logger.warn("Database Restore Factory Settings - table 'attribute' - FAILED");
            return new CatalogResponse(false, "Restore factory settings FAILED - table 'attribute'", -2);
        }
        //  endregion

        //  region CATEGORY_ATTRIBUTES_FILTER
        try {
            ClassPathResource filePath = new ClassPathResource("categoryAttributes_4.json");

            File json = filePath.getFile();

            ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            CategoryAttributeFilter[] categoryAttributeFilters = objectMapper.readValue(json, CategoryAttributeFilter[].class);

            for (CategoryAttributeFilter categoryAttributeFilter : categoryAttributeFilters) {
                entityManager.persist(categoryAttributeFilter);
            }

            if (categoryService.getAllCategoryAttributesFilter().getCategoriesAttributes().size() > 0) {
                sb.append("Category_Attribute_Filter").append(Constants.COMMA).append(Constants.SPACE);
                System.out.println("Database Restore Factory Settings successful - table 'category_attributes_filter'");
            } else {
                sb.append("Table 'category_attribute_filter' - FAILED").append(Constants.COMMA).append(Constants.SPACE);
                System.out.println("Database Restore Factory Settings - table 'category_attribute_filter' - FAILED");
                return new CatalogResponse(false, "Restore factory settings FAILED - table 'category_attribute_filter'", -3);
            }

            //transaction.commit();
            //session.flush();
            //session.close();

        } catch (IOException e) {
            e.printStackTrace();
            return new CatalogResponse(false, "Restore factory settings FAILED - table 'Category_Attribute_Filter'", -99999);
        }
        //  endregion

        //  region Product (colors, attributes, images, etc)
        try {
            //  Initializr Category Products
            ClassPathResource filePath = new ClassPathResource("categoryProducts_4.json");
            File json = filePath.getFile();

            ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

            CategoryDto[] categoryDtos = objectMapper.readValue(json, CategoryDto[].class);
            //Transaction transaction = session.beginTransaction();
            Map<Long, Product> productMap = new HashMap<>();
            for (CategoryDto categoryDto : categoryDtos) {
                Category category = categoryService.getCategory(categoryDto.getCategoryId());

                /*PRODUCT*/
                ProductService productService = new ProductService();

                for (ProductDto productDto : categoryDto.getProducts()) {
                    Product product = new Product(productDto.getProductName(), productDto.getDescription(), productDto.getPrice(), category, productDto.getProductStatus());
                    product.setManagedImageId(productDto.getImageUrl());
                    entityManager.persist(product);

                    //load attributes
                    for (AttributeItem attributeItem : productDto.getAttributes()) {
                        ProductAttributes productAttributes = new ProductAttributes();
                        productAttributes.setProduct(product);
                        productAttributes.setAttribute(defAttributes.get(attributeItem.getAttributeName().toUpperCase()));
                        productAttributes.setAttributeValue(attributeItem.getAttributeValue());

                        entityManager.persist(productAttributes);
                    }

                    if (productDto.getImages().size() == 0) {
                        productDto.getImages().add(product.getManagedImageId());
                    }

                    product.setColors(productService.getColorAttributes(productDto.getColors(), product));
                    product.setImages(productService.getImageAttribute(productDto.getImages(), product));

                    productMap.put(product.getId(), product);
                }

                //  Initialize Promoted products
                PromotedProductDto promotedProductDto = categoryDto.getPromotedProduct();
                Long prodId = promotedProductDto.getId();
                Product product = productMap.get(prodId);
                Assert.notNull(product, "\nPromotedProduct null, promoted product id=" + prodId + ", category number=" + categoryDto.getCategoryId());
                Deal deal = new Deal(10, product.getDescription(), promotedProductDto.getPromotionHeader(), promotedProductDto.getPromotionSubHeader(), promotedProductDto.getStaringPrice(),
                        promotedProductDto.getPromotionImageId(), 0, "", "", product);

                entityManager.persist(deal);

            }

        } catch (IOException e) {
            e.printStackTrace();
            return new CatalogResponse(false, "Restore factory settings FAILED - table 'Product'", -1);
        }
        //  endregion

        return new CatalogResponse(true, sb.toString(), 1);
    }

    //  region Last Update
    @Override
    public List<LastUpdate> getAllLastUpdates() {
        List<LastUpdate> lastUpdates = entityManager.createNamedQuery(LastUpdate.QUERY_GET_ALL, LastUpdate.class)
                .getResultList();

        return lastUpdates.isEmpty() ? null : lastUpdates;
    }

    /**
     * Get selected LAST_UPDATE by name, e.g. "DATA"
     * @param name
     * @return
     */
    @Override
    public LastUpdate getLastUpdateByName(final String name) {

        List<LastUpdate> lastUpdates = entityManager.createQuery("SELECT u FROM LastUpdate u WHERE UPPER(u.lastUpdateName) = '" + name.toUpperCase() + "'", LastUpdate.class)
                .getResultList();

        return lastUpdates.isEmpty() ? null : lastUpdates.get(0);
    }

    @Override
    public LastUpdate createLastUpdate(String lastUpdateName, long lastUpdateTimestamp) {
        if ((lastUpdateName == null) || (lastUpdateName.isEmpty())) return null;

        if (lastUpdateTimestamp <= 0) {
            lastUpdateTimestamp = new Date().getTime();
        }

        LastUpdate lastUpdate = new LastUpdate(lastUpdateName, lastUpdateTimestamp);

        entityManager.persist(lastUpdate);

        return lastUpdate;
    }

    @Override
    public LastUpdate updateLastUpdate(LastUpdate lastUpdateDto, long id) {

        long timestamp = lastUpdateDto.getLastUpdate();
        if (timestamp <= 0) {
            timestamp = new Date().getTime();
            lastUpdateDto.setLastUpdate(timestamp);
        }

        //LastUpdate lastUpdate = getLastUpdate(id);
        LastUpdate lastUpdate = entityManager.find(LastUpdate.class, id);
        if (lastUpdate != null) {
            //  Update LastUpdate Timestamp
            lastUpdate.setLastUpdate(lastUpdateDto.getLastUpdate());
        } else {
            //  New LastUpdate
            lastUpdate = lastUpdateDto;
        }

        entityManager.persist(lastUpdate);

        return lastUpdate;
    }

    @Override
    public LastUpdate getLastUpdate(Long entityId) {
        ArgumentValidationHelper.validateArgumentIsNotNull(entityId, "last update id");

        String hql = JPAQueryHelper.getSelectByPkFieldQuery(LastUpdate.class, LastUpdate.FIELD_ID, entityId);

        Query query = entityManager.createQuery(hql);

        List<LastUpdate> lastUpdateList = query.getResultList();

        return lastUpdateList.size() != 0 ? lastUpdateList.get(0) : null;
    }
    //  endregion
}