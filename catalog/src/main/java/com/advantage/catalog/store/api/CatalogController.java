package com.advantage.catalog.store.api;

import com.advantage.catalog.store.model.category.Category;
import com.advantage.catalog.store.model.deal.Deal;
import com.advantage.catalog.store.model.product.LastUpdate;
import com.advantage.catalog.store.model.product.Product;
import com.advantage.catalog.store.services.*;
import com.advantage.catalog.util.ArgumentValidationHelper;
import com.advantage.common.Constants;
import com.advantage.common.cef.CefHttpModel;
import com.advantage.common.dto.*;
import com.advantage.common.enums.ColorPalletEnum;
import com.advantage.common.security.AuthorizeAsAdmin;
import com.advantage.common.security.AuthorizeAsUser;
import com.advantage.root.util.StringHelper;
import com.advantage.root.util.ValidationHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.advantage.common.Constants.NO_IMAGE_AVAILABLE_ID;
import static java.lang.System.in;

/**
 * @author Binyamin Regev on 23/05/2016
 */
@RestController
@RequestMapping(value = Constants.URI_API + "/v1")
public class CatalogController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttributeService attributeService;
    @Autowired
    private DealService dealService;
    @Autowired
    private DemoAppConfigService demoAppConfigService;
    @Autowired
    private ContactSupportService contactSupportService;
    @Autowired
    private Environment environment;

    private static final Logger logger = Logger.getLogger(CatalogController.class);
    private static final Logger cefLogger = Logger.getLogger("CEF");

    @ModelAttribute
    public void setResponseHeaderForAllRequests(HttpServletResponse response) {
//        response.setHeader(com.google.common.net.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.setHeader(HttpHeaders.EXPIRES, "0");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
    }

    //  region /products
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    @ApiOperation(value = "Get all products info")
    public ResponseEntity<ProductCollectionDto> getAllProducts(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products".hashCode()), "Get all products info", 5);
        } else {
            logger.warn("cefData is null");
        }

        ResponseEntity<ProductCollectionDto> productCollectionDtoResponseEntity = new ResponseEntity<>(productService.getProductCollectionDto(), HttpStatus.OK);
        return productCollectionDtoResponseEntity;

    }

    @RequestMapping(value = "/products/{product_id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get product info")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("product_id") Long id,
                                                     HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/{product_id}".hashCode()), "Get product info", 5);
        } else {
            logger.warn("cefData is null");
        }


        ResponseEntity result;
        Product product = productService.getProductById(id);
        if (product == null) {
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            ProductDto dto = productService.getDtoByEntity(product);
            result = new ResponseEntity<>(dto, HttpStatus.OK);
        }
        return result;
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Create new product")
    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductDto product,
                                                            HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products".hashCode()), "Create new product", 5);
        } else {
            logger.warn("cefData is null");
        }

        if (product == null) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, "Data not valid"), HttpStatus.NO_CONTENT);
        }

        ProductResponseDto responseStatus = productService.createProduct(product);

        return responseStatus.isSuccess() ? new ResponseEntity<>(responseStatus, HttpStatus.OK) :
                new ResponseEntity<>(responseStatus, HttpStatus.BAD_REQUEST);
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Create product with image")
    @RequestMapping(value = "/products/images", method = RequestMethod.POST)
    public ResponseEntity<ProductResponseDto> createProductWithImage(@RequestParam("product") String product,
                                                                     @RequestParam("file") MultipartFile file,
                                                                     HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/images".hashCode()), "Create product with image", 5);
        } else {
            logger.warn("cefData is null");
        }

        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        ProductDto dto = null;
        try {
            dto = objectMapper.readValue(product, ProductDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            new ResponseEntity<>(new ProductResponseDto(false, -1, "json not valid"), HttpStatus.BAD_REQUEST);
        }

        if (dto == null) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, "Data not valid"), HttpStatus.NO_CONTENT);
        }
        if (file.isEmpty()) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, "File was empty"), HttpStatus.NO_CONTENT);
        }

        ImageUrlResponseDto imageResponseStatus = productService.fileUpload(file, null);

        if (!imageResponseStatus.isSuccess()) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, imageResponseStatus.getReason()),
                    HttpStatus.BAD_REQUEST);
        }
        dto.setImageUrl(imageResponseStatus.getId());
        ProductResponseDto responseStatus = productService.createProduct(dto);
        responseStatus.setImageId(imageResponseStatus.getId());

        return responseStatus.isSuccess() ? new ResponseEntity<>(responseStatus, HttpStatus.OK) :
                new ResponseEntity<>(responseStatus, HttpStatus.BAD_REQUEST);
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Update product details")
    @RequestMapping(value = "/products/{product_id}", method = RequestMethod.PUT)
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody ProductDto product,
                                                            @PathVariable("product_id") Long id,
                                                            HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/{product_id}".hashCode()), "Update product details", 5);
        } else {
            logger.warn("cefData is null");
        }

        ProductResponseDto responseStatus = productService.updateProduct(product, id);
        return new ResponseEntity<>(responseStatus, HttpStatus.OK);
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Change Category of product id=13 to Speakers")
    @RequestMapping(value = "/products/to_speakers", method = RequestMethod.PUT)
    public ResponseEntity<ProductResponseDto> updateProductCategoryToSpeakers(HttpServletRequest request,
                                                                              HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/to_speakers".hashCode()), "Change Category of product id=13 to Speakers", 5);
        } else {
            logger.warn("cefData is null");
        }

        /*
        Change Category of product id=13 to Speakers:
        1. Verify Admin-User Base64Token.
        2. check DemoAppConfig.xml parameter "Add_wrong_product_to_speakers_category" value = "Yes".
        3. update category of the product to Speakers.
        4. return response.
         */
        //  TODO-Moti to change code, return success / failure, etc.
        HttpStatus httpStatus = HttpStatus.PRECONDITION_FAILED;
        ProductResponseDto responseStatus = null;
        //productID to move to speakers category = 13
        long productID = 13;
        //speakers category =4
        long categoryID = 4;
        DemoAppConfigParameter parameter = demoAppConfigService.getDemoAppConfigParametersByName("Add_wrong_product_to_speakers_category");
        if (parameter.getParameterValue().equalsIgnoreCase("yes")) {
            Product product = productService.getProductById(productID);
            ProductDto dto = productService.getDtoByEntity(product);
            dto.setCategoryId(categoryID);
            responseStatus = productService.updateProduct(dto, productID);

            httpStatus = responseStatus.isSuccess() ? httpStatus = HttpStatus.OK : HttpStatus.BAD_REQUEST;
        } else {
            httpStatus = HttpStatus.PRECONDITION_FAILED;
            String responceReason = "Configuration parameter is not ready";
            responseStatus = new ProductResponseDto(false, productID, responceReason);
        }
        return new ResponseEntity<>(responseStatus, httpStatus);
    }

    @RequestMapping(value = "/products/search", method = RequestMethod.GET)
    @ApiOperation(value = "Search product by Name")
    public ResponseEntity<List<CategoryDto>> searchProductByName(@RequestParam("name") String name,
                                                                 @RequestParam(value = "quantityPerEachCategory", defaultValue = "-1", required = false) Integer quantity,
                                                                 HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/search".hashCode()), "Search product by Name", 5);
        } else {
            logger.warn("cefData is null");
        }

        if (quantity == 0) return new ResponseEntity<>(HttpStatus.OK);
        List<Product> products = productService.filterByName(name, quantity);
        if (products == null) return new ResponseEntity<>(HttpStatus.OK);
        List<ProductDto> productDtos = productService.fillPureProducts(products);
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Product product : products) {
            if (categoryDtos.stream()
                    .anyMatch(x -> Long.compare(x.getCategoryId(), product.getCategory().getCategoryId()) == 0)) {
                continue;
            }
            Category category = product.getCategory();
            CategoryDto categoryDto = categoryService.getCategoryDto(category);
            List<ProductDto> productsDtoList = productDtos.stream()
                    .filter(x -> Long.compare(x.getCategoryId(), categoryDto.getCategoryId()) == 0)
                    .collect(Collectors.toList());
            if (categoryDto.getProducts() == null) {
                if (productsDtoList.size() > quantity) {
                    productsDtoList = quantity == -1 ? productsDtoList : productsDtoList.subList(0, quantity);
                }
                categoryDto.setProducts(productsDtoList);
            }
            categoryDtos.add(categoryDto);
        }
        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Delete product from catalog")
    @RequestMapping(value = "/products/{product_id}", method = RequestMethod.DELETE)
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable("product_id") Long productId,
                                                            HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/{product_id}".hashCode()), "Delete product from catalog", 5);
        } else {
            logger.warn("cefData is null");
        }


        ProductResponseDto responseStatus = productService.deleteProduct(productId);

        return new ResponseEntity<>(responseStatus, HttpStatus.OK);
    }

    @ApiOperation(value = "Get color-attribute by product-id and color-code")
    @RequestMapping(value = "/products/{product_id}/color/{color_code}", method = RequestMethod.GET)
    public ResponseEntity<ColorAttributeDto> getColorAttributeByProductIdAndColorCode(@PathVariable("product_id") Long productId,
                                                                                      @PathVariable("color_code") String hexColor,
                                                                                      HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/products/{product_id}/color/{color_code}".hashCode()), "Get color-attribute by product-id and color-code", 5);
        } else {
            logger.warn("cefData is null");
        }

        ColorAttributeDto colorAttributesDto = productService.getColorAttributeByProductIdAndColorCode(productId, hexColor);

        return new ResponseEntity<>(colorAttributesDto, HttpStatus.OK);
    }

    //  endregion

    //  region /LastUpdate

    @RequestMapping(value = "/catalog/LastUpdate/timestamp", method = RequestMethod.GET)
    @ApiOperation(value = "Convert Timestamp (0 = Get Current Timestamp)")
    public ResponseEntity<Long> getTimestamp(@RequestParam(value = "timestamp", defaultValue = "0", required = false) String timestamp,
                                             @RequestParam(value = "date_format", defaultValue = "0", required = false) String dateFormat,
                                             HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/catalog/LastUpdate/timestamp".hashCode()), "Convert or get current", 5);
        } else {
            logger.warn("cefData is null");
        }

        if ((timestamp == null) || (!timestamp.isEmpty()) || (Long.valueOf(timestamp).intValue() == 0)) {
            return new ResponseEntity<>(Calendar.getInstance().getTime().getTime(), HttpStatus.OK);
        }

        return new ResponseEntity<>(StringHelper.convertStringToDate(timestamp, dateFormat).getTime(), HttpStatus.OK);
    }

    /**
     * Valid values: case insensitive. "ALL" or valid Last-Update Name.
     *
     * @return {@link LastUpdate}.
     */
    @RequestMapping(value = "/catalog/LastUpdate/{what_to_get}", method = RequestMethod.GET)
    @ApiOperation(value = "Get Last-Update by ID, By Name or ALL")
    public ResponseEntity<List<LastUpdate>> getLastUpdates(@PathVariable("what_to_get") String whatToGet, HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/catalog/LastUpdate/{what_to_get}".hashCode()), "Get Last-Update by ID, By Name or ALL", 5);
        } else {
            logger.warn("cefData is null");
        }

        HttpStatus httpStatus = HttpStatus.OK;

        List<LastUpdate> listLastUpdates = new ArrayList<>();
        if (ValidationHelper.isNumeric(whatToGet)) {
            listLastUpdates.add(productService.getLastUpdate(Long.valueOf(whatToGet)));
        } else if (whatToGet.equalsIgnoreCase("ALL")) {
            listLastUpdates = productService.getAllLastUpdates();
        } else {
            listLastUpdates.add(productService.getLastUpdateByName(whatToGet));
        }

        return new ResponseEntity<>(listLastUpdates, httpStatus);
    }

    @RequestMapping(value = "/catalog/LastUpdate/create/{last_update_name}", method = RequestMethod.POST)
    @ApiOperation(value = "FOR DEV: Create new Last-Update and set its Timestamp (0 = Now")
    public ResponseEntity<LastUpdate> createLastUpdate(@PathVariable("last_update_name") String lastUpdateName,
                                                       @RequestParam(value = "timestamp", defaultValue = "0", required = false) long timestamp,
                                                       HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/catalog/LastUpdate/create/{last_update_name}".hashCode()), "FOR DEV: Create new Last-Update and set its Timestamp", 0);
        } else {
            logger.warn("cefData is null");
        }

        //  Already exists?
        if (productService.getLastUpdateByName(lastUpdateName) != null) {
            return new ResponseEntity<>(new LastUpdate(), HttpStatus.BAD_REQUEST);
        }

        if (timestamp <= 0) {
            Calendar calendar = Calendar.getInstance();
            timestamp = calendar.getTime().getTime();
            ;
        }

        LastUpdate lastUpdate = productService.createLastUpdate(lastUpdateName, timestamp);

        return new ResponseEntity<>(lastUpdate, HttpStatus.OK);
    }

    @RequestMapping(value = "/catalog/LastUpdate/update/last_update_id/{last_update_id}/last_update_name/{last_update_name}", method = RequestMethod.PUT)
    @ApiOperation(value = "FOR DEV: Update Timestamp of an existing Last-Update")
    public ResponseEntity<LastUpdate> updateLastUpdate(@PathVariable("last_update_id") Long lastUpdateId,
                                                       @PathVariable("last_update_name") String lastUpdateName,
                                                       @RequestParam(value = "timestamp", defaultValue = "0", required = false) String timestamp,
                                                       @RequestParam(value = "date_format", defaultValue = "0", required = false) String dateFormat,
                                                       HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/catalog/LastUpdate/update/last_update_id/{last_update_id}/last_update_name/{last_update_name}".hashCode()),
                    "FOR DEV: Update Timestamp of an existing Last-Update", 0);
        } else {
            logger.warn("cefData is null");
        }

        HttpStatus httpStatus = HttpStatus.OK;

        if ((dateFormat == null) || (!dateFormat.isEmpty())) {
            dateFormat = "yyyy.MM.dd.HH.mm.ss";
        }

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        //long timeStamp = calendar.getTime().getTime();

        if ((timestamp == null) || (!timestamp.isEmpty()) || (Long.valueOf(timestamp).intValue() == 0)) {
            timestamp = StringHelper.convertDateToString(calendar.getTime(), dateFormat);
        }

        LastUpdate lastUpdate = productService.updateLastUpdate(new LastUpdate(lastUpdateName, StringHelper.convertStringToDate(timestamp, dateFormat).getTime()), lastUpdateId.longValue());
        if (lastUpdate == null) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(lastUpdate, httpStatus);
    }
    //  endregion

    //  region /categories
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ApiOperation(value = "Get all categories with product's short details")
    public ResponseEntity<List<Category>> getAllCategories(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/categories".hashCode()),
                    "Get all categories with product's short details", 5);
        } else {
            logger.warn("cefData is null");
        }

        List<Category> category = categoryService.getAllCategories();

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{category_id}/products", method = RequestMethod.GET)
    @ApiOperation(value = "Get deal and products full details for this category")
    public ResponseEntity<CategoryDto> getCategoryData(@PathVariable("category_id") String id,
                                                       HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/categories/{category_id}/products".hashCode()),
                    "Get deal and products full details for this category", 5);
        } else {
            logger.warn("cefData is null");
        }

        Long categoryId = Long.valueOf(id);

        CategoryDto categoryDto = null;
        ResponseEntity result = null;
        try{
            categoryDto = categoryService.getCategoryDto(categoryId);
        } catch (Exception e){

            e.printStackTrace();
            logger.error(e);
//            categoryDto = new CategoryDto();
        }

        if (categoryDto == null) {
            result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            result = new ResponseEntity<>(categoryDto, HttpStatus.OK);
        }

        return result;
//        return new ResponseEntity<>(categoryDto, httpStatus);
    }

    @RequestMapping(value = "/categories/all_data", method = RequestMethod.GET)
    @ApiOperation(value = "Get all categories with product's full details")
    public ResponseEntity<List<CategoriesDto>> getCategoryDtoData(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/categories/all_data".hashCode()),
                    "Get all categories with product's full details", 5);
        } else {
            logger.warn("cefData is null");
        }

        List<CategoriesDto> categories = categoryService.getCategoryDtoData();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @RequestMapping(value = "/attributes/colors_pallet", method = RequestMethod.GET)
    @ApiOperation(value = "Get all colors codes")
    public ResponseEntity<Map<String, String>> getColorPallet(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/attributes/colors_pallet".hashCode()),
                    "Get all colors codes", 5);
        } else {
            logger.warn("cefData is null");
        }

        HttpStatus httpStatus = HttpStatus.OK;
        Map<String, String> response = attributeService.getColorPallet();

        if ((response == null) || (response.size() == 0)) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Retrieve all categories and their attributes with the flag indicating
     * whether to show in category Filter.
     */
    @RequestMapping(value = "/categories/attributes", method = RequestMethod.GET)
    @ApiOperation(value = "Get all categories attributes")
    public ResponseEntity<CategoryAttributeFilterResponse> getAllCategoriesAttributes(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/categories/attributes".hashCode()),
                    "Get all categories attributes", 5);
        } else {
            logger.warn("cefData is null");
        }

        HttpStatus httpStatus = HttpStatus.OK;

        CategoryAttributeFilterResponse response = categoryService.getAllCategoryAttributesFilter();

        if ((response == null) || (response.getCategoriesAttributes() == null) || (response.getCategoriesAttributes().isEmpty())) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(response, httpStatus);
    }
    //  endregion

    //  region /Images
    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    @ApiOperation(value = "Upload image")
    public ResponseEntity<ImageUrlResponseDto> imageUpload(@RequestParam("file") MultipartFile file,
                                                           HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/images".hashCode()),
                    "Upload image", 5);
        } else {
            logger.warn("cefData is null");
        }

        if (!file.isEmpty()) {
            ImageUrlResponseDto responseStatus = productService.fileUpload(file, null);
            return responseStatus.isSuccess() ? new ResponseEntity<>(responseStatus, HttpStatus.OK) :
                    new ResponseEntity<>(responseStatus, HttpStatus.EXPECTATION_FAILED);
        } else {
            return new ResponseEntity<>(new ImageUrlResponseDto("", false,
                    "Failed to upload, file was empty."), HttpStatus.EXPECTATION_FAILED);
        }
    }
    //  endregion

    //  region /deals
    @RequestMapping(value = "/deals", method = RequestMethod.GET)
    @ApiOperation(value = "Get all deals")
    public ResponseEntity<List<Deal>> getAllDeals(final HttpServletRequest request,
                                                  final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/deals".hashCode()),
                    "Get all deals", 5);
        } else {
            logger.warn("cefData is null");
        }

        ArgumentValidationHelper.validateArgumentIsNotNull(request, "http servlet request");
        ArgumentValidationHelper.validateArgumentIsNotNull(response, "http servlet response");
        final List<Deal> deals = dealService.getAllDeals();

        return new ResponseEntity<>(deals, HttpStatus.OK);
    }

    //@RequestMapping(value = "/catalog/deals/0", method = RequestMethod.GET)
    @RequestMapping(value = "/deals/search", method = RequestMethod.GET)
    @ApiOperation(value = "Get deal of the day")
    public ResponseEntity<List<Deal>> getDealOfTheDay(@RequestParam(value = "dealOfTheDay", defaultValue = "false") boolean search,
                                                      final HttpServletRequest request,
                                                      final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/deals/search".hashCode()),
                    "Get deal of the day", 5);
        } else {
            logger.warn("cefData is null");
        }

        if (!search) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        ArgumentValidationHelper.validateArgumentIsNotNull(request, "http servlet request");
        ArgumentValidationHelper.validateArgumentIsNotNull(response, "http servlet response");
        List<Deal> deals = new ArrayList<>();
        Deal dealOfTheDay = dealService.getDealOfTheDay();
        deals.add(dealOfTheDay);

        return new ResponseEntity<>(deals, HttpStatus.OK);
    }
    //  endregion

    //  region DemoAppConfig.xml
    @RequestMapping(value = "/DemoAppConfig/parameters/by_tool/{tools_names}", method = RequestMethod.GET)
    @ApiOperation(value = "Get parameters by tools (separate tools by semi-colon (;))")
    public ResponseEntity<DemoAppConfigResponse> getDemoAppConfigParametersByTools(@PathVariable("tools_names") String toolsNames,
                                                                                   final HttpServletRequest request,
                                                                                   final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/DemoAppConfig/parameters/by_tool/{tools_names}".hashCode()),
                    "Get parameters by tools", 5);
        } else {
            logger.warn("cefData is null");
        }

        List<DemoAppConfigParameter> parameters = new ArrayList<>();
        if (toolsNames.equalsIgnoreCase("ALL")) {
            parameters = demoAppConfigService.getAllDemoAppConfigParameters();
        } else {
            parameters = demoAppConfigService.getDemoAppConfigParametersByTool(toolsNames);
        }

        return new ResponseEntity<>(new DemoAppConfigResponse(parameters), (parameters != null ? HttpStatus.OK : HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/DemoAppConfig/parameters/{parameter_name}", method = RequestMethod.GET)
    @ApiOperation(value = "Get configuration parameter by name")
    public ResponseEntity<DemoAppConfigParameter> getDemoAppConfigParameterByName(@PathVariable("parameter_name") String parameterName,
                                                                                  final HttpServletRequest request,
                                                                                  final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/DemoAppConfig/parameters/{parameter_name}".hashCode()),
                    "Get configuration parameter by name", 5);
        } else {
            logger.warn("cefData is null");
        }

        DemoAppConfigParameter parameter = demoAppConfigService.getDemoAppConfigParametersByName(parameterName);

        return new ResponseEntity<>(parameter, (parameter != null ? HttpStatus.OK : HttpStatus.NOT_FOUND));
    }

    @AuthorizeAsAdmin
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @RequestMapping(value = "/DemoAppConfig/Restore_Factory_Settings", method = RequestMethod.GET)
    @ApiOperation(value = "Restore parameters default values")
    public ResponseEntity<DemoAppConfigStatusResponse> restoreFactorySettings(final HttpServletRequest request,
                                                                              final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/DemoAppConfig/Restore_Factory_Settings".hashCode()),
                    "Restore parameters default values", 5);
        } else {
            logger.warn("cefData is null");
        }

        DemoAppConfigStatusResponse restoreFactorySettingsResponse = demoAppConfigService.restoreFactorySettingsDemoAppConfig();

        return new ResponseEntity<>(restoreFactorySettingsResponse, (restoreFactorySettingsResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }

    @RequestMapping(value = "/DemoAppConfig/update/parameter/{name}/value/{new_value}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update DemoAppConfig parameter value")
    public ResponseEntity<DemoAppConfigStatusResponse> updateDemoAppConfigParameter(@PathVariable("name") String parameterName,
                                                                                    @PathVariable("new_value") String parameterValue,
                                                                                    final HttpServletRequest request,
                                                                                    final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/DemoAppConfig/update/parameter/{name}/value/{new_value}".hashCode()),
                    "Update DemoAppConfig parameter value", 5);
        } else {
            logger.warn("cefData is null");
        }

        DemoAppConfigStatusResponse statusResponse = demoAppConfigService.updateParameterValue(parameterName, parameterValue);

        return new ResponseEntity<>(statusResponse, (statusResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }
    //  endregion

    @RequestMapping(value = "/DemoAppConfig/update/parameters", method = RequestMethod.PUT)
    @ApiOperation(value = "Update DemoAppConfig all parameters values")
    public ResponseEntity<DemoAppConfigStatusResponse> updateDemoAppConfigParameters(@RequestBody DemoAppConfigParametersDto parameters,
                                                                                     final HttpServletRequest request,
                                                                                     final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/DemoAppConfig/update/parameters".hashCode()),
                    "Update DemoAppConfig all parameters values", 5);
        } else {
            logger.warn("cefData is null");
        }

        DemoAppConfigStatusResponse statusResponse = demoAppConfigService.updateParametersValues(parameters);

        return new ResponseEntity<>(statusResponse, (statusResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }
    //  endregion

    //  region /Contact Us
    @RequestMapping(value = "/support/contact_us/email", method = RequestMethod.POST)
    @ApiOperation(value = "Contact support by email")
    public ResponseEntity<ContactUsResponse> supportSendMail(@RequestBody ContactUsMailRequest contactUsRequest,
                                                             final HttpServletRequest request,
                                                             final HttpServletResponse response) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/support/contact_us/email".hashCode()),
                    "Contact support by email", 5);
        } else {
            logger.warn("cefData is null");
        }

        ArgumentValidationHelper.validateArgumentIsNotNull(contactUsRequest, "Contact Us Mail Request");
        ArgumentValidationHelper.validateArgumentIsNotNull(request, "http servlet request");
        ArgumentValidationHelper.validateArgumentIsNotNull(response, "http servlet response");

        ContactUsResponse contactUsResponse = contactSupportService.sendMail(contactUsRequest);

        return new ResponseEntity<>(contactUsResponse, HttpStatus.OK);
    }
    //  endregion


    //  region /Restore Database Factory Settings
    @RequestMapping(value = "/catalog/Restore_db_factory_settings", method = RequestMethod.GET)
    @ApiOperation(value = "Restore Database factory settings")
    public ResponseEntity<CatalogResponse> dbRestoreFactorySettings(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/catalog/Restore_db_factory_settings".hashCode()),
                    "Restore Database factory settings", 5);
        } else {
            logger.warn("cefData is null");
        }
        HttpStatus httpStatus = HttpStatus.OK;

        CatalogResponse response = productService.dbRestoreFactorySettings();
        if (!response.isSuccess()) {
            httpStatus = HttpStatus.BAD_REQUEST;

            return new ResponseEntity<>(response, httpStatus);
        }

        return new ResponseEntity<>(response, httpStatus);
    }
    //  endregion

    //  region Most Popular Comments
    @RequestMapping(value = "/MostPopularComments", method = RequestMethod.GET)
    public ResponseEntity<MostPopularCommentsResponse> getAllOrders(HttpServletRequest request,
                                                                    HttpServletResponse response) {

        MostPopularCommentsResponse mostPopularCommentsResponse = productService.getTop10MostPopularComments(request, response);

        HttpStatus httpStatus = mostPopularCommentsResponse.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(mostPopularCommentsResponse, httpStatus);
    }
    //endregion

    //  region Network Virtualization - Return 4xx and 5xx HttpStatus Codes

    /**
     * Method returns an HTTP status code number sent in the request.
     * @param httpStatusCode
     * @param request {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return
     */
    @RequestMapping(value = "/products/id/{product_id}", method = RequestMethod.GET)
    @ApiOperation(value = "Return requested HTTP status code for NV")
    public ResponseEntity<String> returnHttpStatusForCodeNetworkVirtualization(@PathVariable("product_id") int httpStatusCode,
                                                                            HttpServletRequest request,
                                                                            HttpServletResponse response) {

        HttpStatus httpStatus = null;

        if ((httpStatusCode >= 40) && (httpStatusCode < 50)) {
            httpStatus = categoryService.returnHttpStatusCode4xxForNetworkVirtualization(httpStatusCode);
        } else if ((httpStatusCode >= 50) && (httpStatusCode < 60)) {
            httpStatus = categoryService.returnHttpStatusCode5xxForNetworkVirtualization(httpStatusCode);
        } else {
            httpStatus = HttpStatus.OK;
        }

        return new ResponseEntity<>(httpStatusCode + " " + httpStatus.getReasonPhrase(), httpStatus);
    }

    @ApiOperation(value = "AppPulse API that mimic a failed facebook login")
    @RequestMapping(value = "/facebookLogin", method = RequestMethod.POST)
    public ResponseEntity<ErrorResponseDto> facebookLogin(HttpServletRequest request) {
        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
        } else {
            logger.warn("cefData is null");
        }
        HTTPException e = new HTTPException(403);
        logger.error(e.getMessage(), e);
        ErrorResponseDto erd = new ErrorResponseDto(false, "Sorry, connecting to Facebook is currently unavailable. Please try again later.");
        return new ResponseEntity<>(erd, HttpStatus.FORBIDDEN);
    }

    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Upload a new image to a product")
    @RequestMapping(value = "/product/image/{userId}/{source}", method = RequestMethod.POST)
    public ResponseEntity<ProductResponseDto> addImageToProduct(@PathVariable("userId") Long userId, @RequestParam("product_id") Long productId,
                                                                     @RequestParam("file") MultipartFile file, @PathVariable("source") String source,
                                                                     HttpServletRequest request) {
        return addImageToProduct(request, productId, file, source, null, null);
    }


    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Upload a new image to a product")
    @RequestMapping(value = "/product/image/{userId}/{source}/{color}", method = RequestMethod.POST)
    public ResponseEntity<ProductResponseDto> addImageToProduct(@PathVariable("userId") Long userId, @RequestParam("product_id") Long productId,
                                                                @RequestParam("file") MultipartFile file, @PathVariable("source") String source,
                                                                @PathVariable("color") String color, HttpServletRequest request) {
        return addImageToProduct(request, productId, file, source, color, null);
    }

    @AuthorizeAsUser
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", required = false, dataType = "string", paramType = "header", value = "JSON Web Token", defaultValue = "Bearer ")})
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Authorization token required", response = com.advantage.common.dto.ErrorResponseDto.class),
            @ApiResponse(code = 403, message = "Wrong authorization token", response = com.advantage.common.dto.ErrorResponseDto.class)})
    @ApiOperation(value = "Upload a new image to a product")
    @RequestMapping(value = "/product/images/{userId}/{source}", method = RequestMethod.POST)
    public ResponseEntity<ProductUploadImagesResponseDto> addMultipleImagesToProduct(@PathVariable("userId") Long userId, @RequestParam("product_id") Long productId,
                                                                                     @RequestParam("new_colors_quantity") String newColorsQuantity,
                                                                @RequestParam("file") List<MultipartFile> files, @PathVariable("source") String source,
                                                                         HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Set set = multipartRequest.getFileMap().entrySet();
        Iterator i = set.iterator();
        ResponseEntity<ProductResponseDto> response = null;
        List<ProductResponseDto> productResponseDtos = new LinkedList<>();
        ResponseEntity<ProductUploadImagesResponseDto> productUploadImagesResponseDtoRes = null;
        Product product = productService.getProductById(productId);
        ProductDto dto = productService.getDtoByEntity(product);
        if(!newColorsQuantity.isEmpty()){

            List<String> colors = Arrays.asList(newColorsQuantity.split("##"));
            List<ColorAttributeDto> newColors = new LinkedList<>();
            ColorAttributeDto color = null;
            String colorCode = "";
            int colorQuantity = 0;
            for(String colorAndQuantity : colors){
                colorCode = colorAndQuantity.split("_")[0];
                colorQuantity = Integer.parseInt(colorAndQuantity.split("_")[1]);
                String finalColorCode = colorCode;
                color = dto.getColors().stream()
                        .filter(d -> d.getCode().equals(finalColorCode))
                        .findAny()
                        .orElse(null);
                if(color != null){
                    //newColors.add(color);
                }
                else{
                    newColors.add(new ColorAttributeDto(colorCode, ColorPalletEnum.getColorByCode(colorCode).toString(), colorQuantity));
                }
            }
            dto.getColors().addAll(newColors);
            //productService.updateProduct(dto, productId);
        }
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            String color = (String)me.getKey();
            MultipartFile multipartFile = (MultipartFile)me.getValue();
            logger.info("Original fileName - " + multipartFile.getOriginalFilename());
            logger.info("selected color - " + color);
            response = addImageToProduct(request, productId, multipartFile, source, color, dto);
            productResponseDtos.add(response.getBody());
            if(!response.getStatusCode().equals(HttpStatus.OK)) {
                productUploadImagesResponseDtoRes = new ResponseEntity<ProductUploadImagesResponseDto>(response.getStatusCode());
                productUploadImagesResponseDtoRes.getBody().setImageId(productResponseDtos);
                return productUploadImagesResponseDtoRes;
            }
        }

        return new ResponseEntity<>(new ProductUploadImagesResponseDto(productResponseDtos), HttpStatus.OK);
    }


    private ResponseEntity<ProductResponseDto> addImageToProduct(HttpServletRequest request, Long productId, MultipartFile file,
                                                                String source, String color, ProductDto pDto){

        CefHttpModel cefData = (CefHttpModel) request.getAttribute("cefData");
        if (cefData != null) {
            logger.trace("cefDataId=" + cefData.toString());
            cefData.setEventRequiredParameters(String.valueOf("/product/image".hashCode()), "Create product with image", 5);
        } else {
            logger.warn("cefData is null");
        }

        ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        ProductDto dto = null;
//        try {
//            dto = objectMapper.readValue(product, ProductDto.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            new ResponseEntity<>(new ProductResponseDto(false, -1, "json not valid"), HttpStatus.BAD_REQUEST);
//        }
        Product product = productService.getProductById(productId);
        dto = pDto != null ? pDto :productService.getDtoByEntity(product);


        if (dto == null) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, "Data not valid"), HttpStatus.NO_CONTENT);
        }
        if (file.isEmpty()) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, "File was empty"), HttpStatus.NO_CONTENT);
        }

        ImageUrlResponseDto imageResponseStatus = productService.fileUpload(file, source);

        if (!imageResponseStatus.isSuccess()) {
            return new ResponseEntity<>(new ProductResponseDto(false, -1, imageResponseStatus.getReason()),
                    HttpStatus.BAD_REQUEST);
        }
        List<String> images = dto.getImages();
        color = color != null ? color : "ABCDEF";
        images.add(String.format("%s##%s", color, imageResponseStatus.getId()));
        dto.setImages(images);
        ColorPalletEnum.getColorByCode(color);
        //productService.getColorAttributeByProductIdAndColorCode(productId, color);
        //dto.setImageUrl(imageResponseStatus.getId());
        if(dto.getImageUrl().equals(NO_IMAGE_AVAILABLE_ID)){
            dto.setImageUrl(imageResponseStatus.getId());
            dto.setImages(dto.getImages().stream().filter(d -> !NO_IMAGE_AVAILABLE_ID.equals(d)).collect(Collectors.toList()));
        }
        ProductResponseDto responseStatus = productService.updateProduct(dto, productId);
        responseStatus.setImageId(imageResponseStatus.getId());
        responseStatus.setImageColor(color);

        return responseStatus.isSuccess() ? new ResponseEntity<>(responseStatus, HttpStatus.OK) :
                new ResponseEntity<>(responseStatus, HttpStatus.BAD_REQUEST);
    }
    //  endregion

}
