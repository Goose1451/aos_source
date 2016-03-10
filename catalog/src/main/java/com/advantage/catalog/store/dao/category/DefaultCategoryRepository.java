package com.advantage.catalog.store.dao.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import com.advantage.catalog.store.model.category.Category;
import com.advantage.catalog.store.model.category.CategoryAttributeFilter;
import com.advantage.catalog.store.model.category.CategoryAttributeFilterPK;
import com.advantage.catalog.util.ArgumentValidationHelper;
import com.advantage.catalog.util.JPAQueryHelper;
import com.advantage.catalog.store.dao.AbstractRepository;
import com.advantage.root.util.JsonHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Qualifier("categoryRepository")
@Repository
public class DefaultCategoryRepository extends AbstractRepository implements CategoryRepository {

    private static final int MAX_NUM_OF_CATEGORIES = 6;

    @Override
    public Category createCategory(final String name, final String managedImageId) {
        Category category = new Category(name, managedImageId);
        entityManager.persist(category);

        return category;
    }

    @Override
    public List<CategoryAttributeFilter> getAllCategoryAttributeFilter() {
        List<CategoryAttributeFilter> caf = entityManager.createNamedQuery(CategoryAttributeFilter.QUERY_GET_ALL, CategoryAttributeFilter.class)
                .getResultList();

        return caf.isEmpty() ? null : caf;
    }

    @Override
    public void addCategoryAttributeFilter(CategoryAttributeFilter categoryAttributeFilterObj) {
        ArgumentValidationHelper.validateArgumentIsNotNull(categoryAttributeFilterObj,"CategoryAttributeFilter object");
        ArgumentValidationHelper.validateLongArgumentIsPositive(categoryAttributeFilterObj.getCategoryId(),"category id");
        ArgumentValidationHelper.validateLongArgumentIsPositive(categoryAttributeFilterObj.getAttributeId(),"attribute id");
        entityManager.persist(categoryAttributeFilterObj);
    }

    @Override
    public void updateCategoryAttributeFilter(Long categoryId, Long attributeId, boolean showInFilter) {
        CategoryAttributeFilter categoryAttributeFilter = findCategoryAttributeFilter(categoryId, attributeId);
        if (categoryAttributeFilter != null) {
            categoryAttributeFilter.setShowInFilter(showInFilter);
            entityManager.persist(categoryAttributeFilter);
        }
    }

    @Override
    public CategoryAttributeFilter findCategoryAttributeFilter(Long categoryId, Long attributeId) {
        CategoryAttributeFilterPK categoryAttributeFilterPk = new CategoryAttributeFilterPK(categoryId, attributeId);
        CategoryAttributeFilter categoryAttributeFilter = entityManager.find(CategoryAttributeFilter.class, categoryAttributeFilterPk);

        return categoryAttributeFilter;
    }

    //@Override
    //public int delete(Category... entities) {
    //    ArgumentValidationHelper.validateArrayArgumentIsNotNullAndNotZeroLength(entities, "categories");
    //    int categoriesCount = entities.length;
    //    Collection<Long> categoryIds = new ArrayList<>(categoriesCount);
    //
    //    for (Category category : entities) {
    //        Long categoryId = category.getCategoryId();
    //        categoryIds.add(categoryId);
    //    }
    //
    //    String hql = JPAQueryHelper.getDeleteByPkFieldsQuery(Category.class, Category.FIELD_CATEGORY_ID,
    //            Category.PARAM_CATEGORY_ID);
    //    Query query = entityManager.createQuery(hql);
    //    query.setParameter(Category.PARAM_CATEGORY_ID, categoryIds);
    //
    //    return query.executeUpdate();
    //}
    @Override
    public int delete(Category... entities) {
        int count = 0;
        for (Category entity : entities) {
            if (entityManager.contains(entity)) {
                entityManager.remove(entity);
                count++;
            }
        }

        return count;
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = entityManager.createNamedQuery(Category.QUERY_GET_ALL, Category.class)
                .setMaxResults(MAX_NUM_OF_CATEGORIES)
                .getResultList();

        return categories.isEmpty() ? null : categories;
    }

    @Override
    public Category get(Long entityId) {
        ArgumentValidationHelper.validateArgumentIsNotNull(entityId, "category id");
        String selectCategoryHql = JPAQueryHelper.getSelectByPkFieldQuery(Category.class, Category.FIELD_CATEGORY_ID,
                entityId);
        Query query = entityManager.createQuery(selectCategoryHql);

        Category category = (Category) query.getSingleResult();
        return (category != null ? category : null);
    }

    @Override
    public String getCategoryName(int categoryId) {

        //StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("getCategoryName")
        //        .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
        //        .setParameter(1, categoryId);

        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("getCategoryName");

        query = query.registerStoredProcedureParameter("category_id", Integer.class, ParameterMode.IN)
                     .registerStoredProcedureParameter("category_name", Object.class, ParameterMode.OUT)
                     .setParameter("category_id", categoryId);

        boolean result = query.execute();

        System.out.println("query.execute() returned " + result);

        String categoryName = (String) query.getSingleResult();

        return categoryName;
    }

    @Override
    public String getAllCategories02(int seconds_to_sleep) {
        String statement = "SELECT * FROM public.get_all_categories_02(" + seconds_to_sleep + ")";

        String jsonString = (String) entityManager.createNativeQuery(statement)
                .getSingleResult();

        return jsonString;
    }

}