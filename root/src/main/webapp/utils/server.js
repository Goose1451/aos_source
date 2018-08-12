/**
 * Created by correnti on 13/12/2015.
 */


var Loger = Loger ? Loger : function () {
};


Loger.Params = function (params, url) {
    spaces(3);
    console.log('Param to pass to ' + url);
    if (params) {
        if (params.orderPaymentInformation && params.orderPaymentInformation.Transaction_SafePay_Password) {
            console.log({
                "Transaction_AccountNumber": params.Transaction_AccountNumber,
                "Transaction_Currency": params.Transaction_Currency,
                "Transaction_CustomerPhone": params.Transaction_CustomerPhone,
                "Transaction_MasterCredit_CVVNumber": params.Transaction_MasterCredit_CVVNumber,
                "Transaction_MasterCredit_CardNumber": params.Transaction_MasterCredit_CardNumber,
                "Transaction_MasterCredit_CustomerName": params.Transaction_MasterCredit_CustomerName,
                "Transaction_MasterCredit_ExpirationDate": params.Transaction_MasterCredit_ExpirationDate,
                "Transaction_PaymentMethod": params.Transaction_PaymentMethod,
                "Transaction_ReferenceNumber": params.Transaction_ReferenceNumber,
                "Transaction_SafePay_UserName": params.Transaction_SafePay_UserName,
                "Transaction_TransactionDate": params.Transaction_TransactionDate,
                "Transaction_Type": params.Transaction_Type
            });
        }
        else if (params.safePayPassword) {
            console.log({safePayUsername: params.safePayUsername, referenceId: params.referenceId});
        }
        else if (params.password) {
            console.log({
                "accountType": params.accountType, "address": params.address,
                "allowOffersPromotion": params.allowOffersPromotion, "cityName": params.cityName,
                "countryId": params.countryId, "email": params.email,
                "firstName": params.firstName, "lastName": params.lastName,
                "loginName": params.loginName, "phoneNumber": params.phoneNumber,
                "stateProvince": params.stateProvince, "zipcode": params.zipcode,
            });
        }
        else {
            console.log(params);
        }
    }
    spaces(1);
};
Loger.Extract = function (file, obj, paths) {
    spaces(3);
    lines(2);
    console.log('Extracted file: ' + file);
    spaces(1);
    console.log("Recieve: ");
    console.log(obj);
    spaces(1);
    for (var i = 0; i < paths.length; i++) {
        console.log(paths[i])
    }
    spaces(1);
    lines(2);
    spaces(3);
};

Loger.CallingWSDL = function (obj) {
    spaces(1);
    console.log("========================   Reading wsdl  =================================")
    console.log("url: " + obj.path);
    console.log("method: " + obj.method);
};

Loger.Calling = function (str) {
    spaces(1);
    console.log("===========================   Calling  ===================================")
    console.log("Path: " + str);
};

Loger.Received = function (str) {
    console.log("============================  Received  ==================================")
    console.log(str)
    spaces(1);
};

function spaces(num) {
    var v = ""
    for (var i = 0; i < num; i++) {
        v += " ";
        console.log("" + v)
    }
}

function lines(num) {
    var v = ""
    for (var i = 0; i < num; i++) {
        v += " ";
        console.log("==========================================================================" + v)
    }
}


var services_properties = []

//var catalogKey = "http://localhost:8080/catalog";
//var orderKey = "http://localhost:8080/order";
//var accountKey = "http://localhost:8080/account";
//var serviceKey = "http://localhost:8080/service";
//var wsdlPath = 'http://localhost:8080/accountservice';
var catalogKey = serverKey = orderKey = wsdlPath = "undefined";
/*accountKey =*//* serviceKey =*/

var server = {
    namespaceURL: 'com.advantage.online.store.accountservice',

    haveInternet: function () {
        return serverKey.indexOf("localhost");
    },

    getKey: function () {
        return serverKey;
    },
    setKey: function (_serverKey) {
        serverKey = _serverKey;
    },
    setCatalogKey: function (_catalogKey) {
        catalogKey = _catalogKey;
    },
    setOrderKey: function (_orderKey) {
        orderKey = _orderKey;
    },
    setWsdlPath: function (_wsdlPath) {
        wsdlPath = _wsdlPath;

        console.log("File extracted: ");
        Loger.Extract("", "", [
            'Catalog path: ' + catalogKey,
            'Order path: ' + orderKey,
            //'Account path: ' + accountKey,
            //'Service path: ' + serviceKey,
            'Account WSDL (Web Services Description Language) path: ' + wsdlPath,
        ]);
    },

    catalog: {

        getKey: function () {
            return catalogKey;
        }
        ,

        nvHandler: function (code) {
            var paramToReturn = catalogKey + "products/id/" + code;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getPopularProducts: function () {
            var paramToReturn = "app/tempFiles/popularProducts.json";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getAllData: function () {
            var paramToReturn = catalogKey + "categories/all_data";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getMostPopularComments: function () {
            var paramToReturn = catalogKey + "MostPopularComments";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getCategories: function () {
            var paramToReturn = catalogKey + "categories";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getCategoryById: function (id) {
            var paramToReturn = catalogKey + "categories/" + id + "/products";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getDeals: function () {
            var paramToReturn = catalogKey + "deals";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getDealOfTheDay: function () {
            var paramToReturn = catalogKey + "deals/search?dealOfTheDay=true";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getProducts: function () {
            var paramToReturn = catalogKey + "products.json";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getProductById: function (id) {
            var paramToReturn = catalogKey + 'products/' + id;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getProductsBySearch: function (word, quantity) {
            var paramToReturn = catalogKey + "products/search?name=" + word;
            if (quantity > 0) {
                paramToReturn += "&quantityPerEachCategory=" + quantity;
            }
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getAllCategoriesAttributes: function () {
            var paramToReturn = catalogKey + "categories/attributes";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        sendSupportEmail: function () {
            var paramToReturn = catalogKey + "support/contact_us/email";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getConfigurations: function () {

            var paramToReturn = catalogKey + "DemoAppConfig/parameters/by_tool/ALL";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getAccountConfiguration: function () {

            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetAccountConfigurationRequest',
                response: "GetAccountConfigurationResponse",
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,


    }
    ,
    order: {

        updateUserCart: function (userId) {
            var paramToReturn = orderKey + "carts/" + userId;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        removeProductToUser: function (userId, productId, color) {
            var paramToReturn = orderKey + "carts/" + userId +
                "/product/" + productId +
                "/color/" + color;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        updateProductToUser: function (userId, productId, color, quantity, oldColor) {
            var path = server.order.addProductToUser(userId, productId, oldColor, quantity);
            var paramToReturn = color != oldColor ? path += "&new_color=" + color : path;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        addProductToUser: function (userId, productId, color, quantity) {
            var paramToReturn = orderKey + "carts/" + userId + "/product/" + productId +
                "/color/" + color + "?quantity=" + quantity;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        loadCartProducts: function (userId) {
            var paramToReturn = orderKey + "carts/" + userId;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        clearCart: function (userId) {
            var paramToReturn = orderKey + "carts/" + userId;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getShippingCost: function () {
            var paramToReturn = orderKey + "shippingcost/";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        safePay: function (userId) {

            var paramToReturn = orderKey + "orders/users/" + userId;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        accountUpdate: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountUpdateRequest',
                response: 'AccountUpdateResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        },

        deleteOrder: function(order, userId){
            var paramToReturn = orderKey +  "orders/history/users/" + userId + "/" + order.orderNumber;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        },

    }
    ,
    account: {

        getOrders: function (userId) {
            var paramToReturn = orderKey + "orders/history/lines/users/" + userId;
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
        ,

        getAllCountries: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetCountriesRequest',
                response: 'GetCountriesResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        register: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountCreateRequest',
                response: 'AccountCreateResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        login: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountLoginRequest',
                response: 'AccountLoginResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        getAccountById: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetAccountByIdRequest',
                response: 'GetAccountByIdResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        getAccountById_new: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetAccountByIdNewRequest',
                response: 'GetAccountByIdNewResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        getAccountPaymentPreferences: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetAccountPaymentPreferencesRequest',
                response: 'PaymentPreferences'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        getAddressesByAccountId: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'GetAddressesByAccountIdRequest',
                response: 'GetAddressesByAccountIdResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,
        deleteAccount: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountPermanentDeleteRequest',
                response: 'accountPermanentDeleteResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        accountUpdate: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountUpdateRequest',
                response: 'AccountUpdateResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        updateMasterCreditMethod: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'UpdateMasterCreditMethodRequest',
                response: 'UpdateMasterCreditMethodResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        addMasterCreditMethod: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AddMasterCreditMethodRequest',
                response: 'PaymentPreferencesStatusResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        updateSafePayMethod: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'UpdateSafePayMethodRequest',
                response: 'UpdateSafePayMethodResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        addSafePayMethod: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AddSafePayMethodRequest',
                response: 'AddSafePayMethodResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        changePassword: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'ChangePasswordRequest',
                response: 'ChangePasswordResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        accountLogout: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'AccountLogoutRequest',
                response: 'AccountLogoutResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        }
        ,

        paymentMethodUpdate: function () {
            var wsdlToReturn = {
                path: wsdlPath,
                method: 'PaymentMethodUpdateRequest',
                response: 'PaymentMethodUpdateResponse'
            }
            Loger.CallingWSDL(wsdlToReturn);
            return wsdlToReturn;
        },

        facebookLogin : function(){
            var paramToReturn = catalogKey + "facebookLogin";
            Loger.Calling(paramToReturn);
            return paramToReturn;
        }
    }
    ,
}
