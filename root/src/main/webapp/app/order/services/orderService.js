/**
 * Created by correnti on 10/01/2016.

 [‎25/‎01/‎2016 11:12] Regev, Binyamin:
 accountType:
 10 = admin
 20 = user
 30 = guest

 */

define(['./module'], function (services) {
    'use strict';
    services.service('orderService', ['$rootScope', '$q', '$http', '$filter', 'productsCartService',

        function ($rootScope, $q, $http, $filter, productsCartService) {

            return ({
                getAccountById: getAccountById,
                getShippingCost: getShippingCost,
                SafePay: SafePay,
                accountUpdate: accountUpdate,
                userIsLogin: userIsLogin,
                removeOrder: removeOrder
            });

            function userIsLogin() {
                var user = $rootScope.userCookie;
                return user && user.response && user.response.userId != -1;
            }

            function SafePay(user, savePay, card, shipping, cart, accountNumber, TransPaymentMethod, cartIncrement) {
                var defer = $q.defer();

                var purchasedProducts = [];
                angular.forEach(cart.productsInCart, function (product) {
                    purchasedProducts.push({
                        "hexColor": product.color.code,
                        "productId": product.productId,
                        "quantity": product.quantity
                    });
                })

                var paramsToPass = {
                    "orderPaymentInformation": {
                        "Transaction_AccountNumber": accountNumber,
                        "Transaction_Currency": shipping.currency,
                        "Transaction_CustomerPhone": user.phoneNumber,
                        "Transaction_MasterCredit_CVVNumber": card.cvv,
                        "Transaction_MasterCredit_CardNumber": '4886' + card.number,
                        "Transaction_MasterCredit_CustomerName": card.name,
                        "Transaction_MasterCredit_ExpirationDate": card.expirationDate.month + "" + card.expirationDate.year,
                        "Transaction_PaymentMethod": TransPaymentMethod,
                        "Transaction_ReferenceNumber": 0,
                        "Transaction_SafePay_Password": savePay.password,
                        "Transaction_SafePay_UserName": savePay.username,
                        "Transaction_TransactionDate": shipping.transactionDate,
                        "Transaction_Type": "PAYMENT"
                    },
                    "orderShippingInformation": {
                        "Shipping_Address_Address": user.address,
                        "Shipping_Address_City": user.cityName,
                        "Shipping_Address_CountryCode": user.countryId,
                        "Shipping_Address_CustomerName": user.firstName + " " + user.lastName,
                        "Shipping_Address_CustomerPhone": user.phoneNumber,
                        "Shipping_Address_PostalCode": user.zipcode,
                        "Shipping_Address_State": user.stateProvince,
                        "Shipping_Cost": $filter('productsCartSum')(cart),
                        "Shipping_NumberOfProducts": $filter('productsCartCount')(cart),
                        "Shipping_TrackingNumber": 0
                    },
                    "purchasedProducts": purchasedProducts,
                }
                Loger.Params(paramsToPass, server.order.safePay(user.id));
                Helper.enableLoader();
                $http({
                    method: "post",
                    url: server.order.safePay(user.id),
                    data: paramsToPass,
                    headers: {
                        "content-type": "application/json",
                        "Authorization": "Basic " + $rootScope.userCookie.response.t_authorization,
                    },
                }).
                then(function (res) {
                    Helper.disableLoader();
                    Loger.Received(res);
                    defer.resolve(res.data)
                }, function (err) {
                    Helper.disableLoader();
                    Loger.Received(err);
                    defer.reject(JSON.stringify(err))
                })
                return defer.promise;
            }

            function accountUpdate(user) {

                var paramsToPass = {
                    lastName: user.lastName,
                    firstName: user.firstName,
                    accountId: user.id,
                    countryId: user.countryId,
                    stateProvince: user.stateProvince,
                    cityName: user.cityName,
                    address: user.address,
                    zipcode: user.zipcode,
                    phoneNumber: user.phoneNumber,
                    email: user.email,
                    accountType: 20,
                    allowOffersPromotion: user.allowOffersPromotion
                }

                Loger.Params(paramsToPass, server.order.accountUpdate().method);

                var defer = $q.defer();
                var params = server.order.accountUpdate();

                Helper.enableLoader();

                $.soap({
                    url: params.path,
                    method: params.method,
                    namespaceURL: server.namespaceURL,
                    SOAPAction: server.namespaceURL + params.method,
                    data: paramsToPass,
                    success: function (soapResponse) {
                        var response = soapResponse.toJSON(params.response);
                        Helper.disableLoader();
                        Loger.Received(response);
                        defer.resolve(response.AccountResponse);
                    },
                    error: function (response) {
                        Loger.Received(response);
                        Helper.disableLoader();
                        defer.reject("Request failed! ");
                    },
                    enableLogging: true
                });

                return defer.promise;
            }

            function getAccountById() {

                var defer = $q.defer();
                var user = $rootScope.userCookie;
                if (user && user.response && user.response.userId != -1) {

                    var params = server.account.getAccountById();
                    Helper.enableLoader();

                    $.soap({
                        url: params.path,
                        method: params.method,
                        namespaceURL: server.namespaceURL,
                        SOAPAction: server.namespaceURL + params.method,
                        data:
                            {
                                accountId: user.response.userId,
                                base64Token: "Basic "+user.response.t_authorization
                            },
                        success: function (soapResponse) {
                            var response = soapResponse.toJSON(params.response);
                            if(response && response.AccountResponse){
                                response.AccountResponse.country = response.AccountResponse.countryIsoName;
                            }
                            Helper.disableLoader();
                            Loger.Received(response);
                            defer.resolve(response.AccountResponse);
                        },
                        error: function (response) {
                            Loger.Received(response);
                            Helper.disableLoader();
                            defer.reject("Request failed! ");
                        },
                        enableLogging: true
                    });
                }
                else {
                    defer.resolve(null);
                }
                return defer.promise;
            }

            function getShippingCost(user) {

                var defer = $q.defer();

                //if((user.firstName + user.lastName).replace(/\s/g, "").length < 1){
                //    defer.resolve(null);
                //}

                productsCartService.getCart().then(function (cart) {

                    /// plaster
                    if ((user.firstName + user.lastName).trim().length < 1) {
                        user.firstName = $rootScope.userCookie.name;
                    }
                    /// end plaster

                    var paramsToPass = {
                        "seaddress": {
                            "addressLine1": user.address,
                            "addressLine2": "",
                            "city": user.cityName,
                            "country": user.country,
                            "postalCode": user.zipcode,
                            "state": user.stateProvince
                        },
                        "secustomerName": user.firstName + " " + user.lastName,
                        "secustomerPhone": user.phoneNumber,
                        "senumberOfProducts": $filter('productsCartCount')(cart),
                        "setransactionType": "SHIPPING_COST",
                        "sessionId" : $rootScope.orderSessionId ? $rootScope.orderSessionId : null
                    };

                    Loger.Params(paramsToPass, server.order.getShippingCost());

                    Helper.enableLoader();
                    $http({
                        method: "post",
                        url: server.order.getShippingCost(),
                        data: paramsToPass,
                    }).
                    then(function (shippingCost) {

                        Loger.Received(shippingCost);
                        Helper.disableLoader();
                        defer.resolve(shippingCost.data)
                    }, function (err) {
                        Loger.Received(err);
                        Helper.disableLoader();
                        defer.reject(JSON.stringify(err))
                    })
                });

                return defer.promise;
            }

            function removeOrder(order){
                var defer = $q.defer();

                var user = $rootScope.userCookie;

                Helper.enableLoader();
                    $http({
                        method: "delete",
                        url: server.order.deleteOrder(order, user.response.userId),
                        headers: {
                            "content-type": "application/json",
                            "Authorization": "Basic " + $rootScope.userCookie.response.t_authorization,
                        },
                    }).
                    then(function (res) {

                        var data = res.data;
                        var ordersObj = [];
                        angular.forEach(data.OrderLines, function(obj){

                            if(ordersObj[obj.OrderNumber] == null){
                                ordersObj[obj.OrderNumber] = {
                                    "orderDate": obj.OrderDate,
                                    "products": []
                                };
                            }
                            ordersObj[obj.OrderNumber].products.push({
                                "ProductImageUrl": obj.ProductImageUrl,
                                "ProductName": obj.ProductName,
                                "ProductColorCode": obj.ProductColorCode,
                                "ProductColorName": obj.ProductColorName,
                                "PricePerUnit": obj.PricePerUnit,
                                "Quantity": obj.Quantity
                            });
                        });

                        var orders = [];
                        for(var index in Object.keys(ordersObj)){
                            var name = Object.keys(ordersObj)[index];
                            var order = {
                                orderNumber: name,
                                orderDate: ordersObj[name].orderDate,
                                products: ordersObj[name].products
                            }
                            orders.push(order);
                        }

                        Loger.Received(orders);
                        Helper.disableLoader();
                        defer.resolve(orders)
                    }, function (err) {
                        Loger.Received(err);
                        Helper.disableLoader();
                        defer.reject(JSON.stringify(err))
                    });

                return defer.promise;
            }
        }]);
});

