/**
 * Created by correnti on 31/12/2015.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('myAccountCtrl', ['$scope', '$timeout',
        '$location', 'resolveParams', 'accountService', 'ipCookie',
        function (s, $timeout, $location, resolveParams, accountService, $cookie) {

            checkLogin();
            function checkLogin(){
                s.checkLogin();
                if($location.path().indexOf('/myAccount') != -1){
                    $timeout(checkLogin, 2000);
                }
            }
            $(document).click(function(event) {
                if (!$(event.target).closest(".deleteAccountPopup,.deleteMainBtnContainer").length) {
                    $("body").find(".deleteAccountPopup").fadeOut(350);
                }
            });

            s.closeDeleteDialogBox = function(){


                $(".deleteAccountPopup").fadeOut(350);
                $(".PopUp > div:nth-child(1)").animate({
                    "top": "-150%"
                }, 300, function () {
                    $(".PopUp").fadeOut(100, function () {
                        $("body").css("overflow-y", "scroll");
                    });
                });
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
            s.deleteAccountUi = function(){

                $(".PopUp").fadeIn(100, function () {
                    $(".PopUp > div:nth-child(1)").animate({"top": top}, 600);
                    $("body").css({"left": "0px"})
                });

                $('.deleteAccountPopup').fadeIn(350);

            }

            s.deleteAccountConfirmed = function(){

                s.closeDeleteDialogBox();

                var responsePromise = accountService.deleteAccount();
                responsePromise.then(function(result) {

                    // this is only run after getData() resolves
                    var isSuccess  = result.IsSuccess;
                    console.log("result is success - "+isSuccess +"  Result outcome - "+ result.Reason );
                    if(isSuccess){

                        localStorage.removeItem('rememberMe');
                        $cookie.remove($cookie('lastLogin'));

                        $(".PopUp").fadeIn(100, function () {
                            $(".PopUp > div:nth-child(1)").animate({"top": top}, 600);
                            $("body").css({"left": "0px"})
                        });

                        $('.successfulDeleteMessage').fadeIn(350);

                        setTimeout(function(){

                            $(".successfulDeleteMessage").fadeOut(350);
                            $(".PopUp > div:nth-child(1)").animate({
                                "top": "-150%"
                            }, 300, function () {
                                $(".PopUp").fadeOut(100, function () {
                                    $("body").css("overflow-y", "scroll");
                                });
                            });
                            location.reload();

                        }, 2000);


                    }

                });
            }

            Helper.forAllPage();

        }]);
});




