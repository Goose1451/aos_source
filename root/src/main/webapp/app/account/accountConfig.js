/**
 * Created by correnti on 31/12/2015.
 */

define([], function () {

    function config($stateProvider) {

        $stateProvider.
        state('myAccount', {
            url: '/myAccount',
            templateUrl: 'app/account/views/myAccount-page.html',
            controller: 'myAccountCtrl',
            controllerAs: 'maCtrl',
            resolve: {
                resolveParams: function ($q, accountService) {
                    var defer = $q.defer();
                    accountService.getAccountDetails().then(
                        function (accountDetails) {
                            accountService.getShippingDetails(accountDetails).then(
                                function (shippingDetails) {
                                    accountService.getAccountPaymentPreferences().then(
                                        function (paymentPreferences) {
                                            defer.resolve({
                                                accountDetails: accountDetails,
                                                shippingDetails: shippingDetails,
                                                paymentPreferences: paymentPreferences,
                                            });
                                        });
                                });
                        });
                    return defer.promise;
                }
            }
        }).
        state('accountDetails', {
            url: '/accountDetails',
            templateUrl: 'app/account/views/accountDetails-page.html',
            controller: 'accountDetailsCtrl',
            controllerAs: 'adCtrl',
            resolve: {
                resolveParams: function ($q, accountService) {
                    var defer = $q.defer()
                    accountService.getAccountDetails().then(
                        function (accountDetails) {
                            defer.resolve({
                                accountDetails: accountDetails,
                            });
                        });
                    return defer.promise;
                }
            }
        }).
        state('accountPaymentEdit', {
            url: '/accountPaymentEdit',
            templateUrl: 'app/account/views/accountPaymentEdit-page.html',
            controller: 'accountPaymentEditCtrl',
            controllerAs: 'apeCtrl',
            resolve: {
                resolveParams: function ($q, accountService) {
                    var defer = $q.defer()
                    accountService.getAccountDetails().then(
                        function (accountDetails) {
                            accountService.getAccountPaymentPreferences().then(
                                function (paymentPreferences) {
                                    defer.resolve({
                                        paymentPreferences: paymentPreferences,
                                        accountDetails: accountDetails,
                                    });
                                });
                        });
                    return defer.promise;
                }
            }
        }).
        state('MyOrders', {
            url: '/MyOrders',
            templateUrl: 'app/account/views/MyOrders-page.html',
            controller: 'myOrdersCtrl',
            controllerAs: 'myOrdersCtrl',
            resolve: {
                resolveParams: function ($q, accountService) {
                    var defer = $q.defer()
                    accountService.getAccountOrder().then(
                        function (orders) {
                            defer.resolve({
                                orders: orders,
                            });
                        }, function (error) {
                            defer.reject(error);
                        });
                    return defer.promise;
                }
            }
        });
    }

    return ['$stateProvider', config];

});


//var defer = $q.defer();
//orderService.getAccountById().
//then(function (user) {
//    if(user)
//    {
//        orderService.getShippingCost(user).
//        then(function (shippingCost) {
//
//            defer.resolve({
//                shippingCost : shippingCost,
//                user : user,
//                noCards: true,
//                CardNumber: [],
//            });
//        });
//    }
//    else {
//        defer.resolve({
//            shippingCost : null,
//            user : null
//        });
//    }
//});
//return defer.promise;




