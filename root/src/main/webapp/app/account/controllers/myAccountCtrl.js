/**
 * Created by correnti on 31/12/2015.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('myAccountCtrl', ['$scope', '$timeout',
        '$location', 'resolveParams', 'accountService',
        function (s, $timeout, $location, resolveParams, accountService) {

            checkLogin();
            function checkLogin(){
                s.checkLogin();
                if($location.path().indexOf('/myAccount') != -1){
                    $timeout(checkLogin, 2000);
                }
            }

            s.masterCredit4Digits = resolveParams.paymentPreferences &&
            resolveParams.paymentPreferences.masterCredit &&
            resolveParams.paymentPreferences.masterCredit.cardNumber ?
                resolveParams.paymentPreferences.masterCredit.cardNumber.substring(resolveParams.paymentPreferences.masterCredit.cardNumber.length - 4) : null;

            s.safePayName = resolveParams.paymentPreferences &&
            resolveParams.paymentPreferences.safePay &&
            resolveParams.paymentPreferences.safePay.safepayUsername ?
                resolveParams.paymentPreferences.safePay.safepayUsername : null;

            s.accountDetails = resolveParams.accountDetails;
            s.shippingDetails = resolveParams.shippingDetails;

            s.defaultPaymentMethodId = resolveParams.accountDetails.defaultPaymentMethodId == 0 ?
                s.masterCredit4Digits != null ? 20 : s.safePayName != null ? 10 : 0 : resolveParams.accountDetails.defaultPaymentMethodId;

            s.categoriesPromotions = [
                { categoryName : 'Tablets', categoryValue : true, },
                { categoryName : 'Laptops', categoryValue : true, },
                { categoryName : 'Headphones', categoryValue : true, },
                { categoryName : 'Speakers', categoryValue : true, },
                { categoryName : 'Mice', categoryValue : true, },
            ];

            s.allowOffersPromotionChanged = function(){
                accountService.accountUpdate(s.accountDetails);
            }
            s.deleteAccount = function(){

                $(".PopUp").fadeIn(100, function () {
                    $(".PopUp > div:nth-child(1)").animate({"top": top}, 600);
                    $("body").css({"left": "0px"})
                });
                $('.deleteAccountPopup').removeClass('close');
                $('.deleteAccountPopup').addClass('show');
                //
                // $("body").addClass('overlayDelete');

                // if (confirm("Press a button!")) {
                //    var responsePromise = accountService.deleteAccount();
                //    responsePromise.then(function(result) {
                //
                //        // this is only run after getData() resolves
                //        var any  = result;
                //        console.log("result "+any);
                //    });
                // }
            }

            Helper.forAllPage();

        }]);
});




