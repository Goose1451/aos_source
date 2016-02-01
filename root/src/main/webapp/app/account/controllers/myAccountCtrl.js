/**
 * Created by correnti on 31/12/2015.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('myAccountCtrl', ['$scope', '$timeout',
        '$location', 'resolveParams',
        function (s, $timeout, $location, resolveParams) {

            l("resolveParams")
            l(resolveParams)

            checkLogin();
            function checkLogin(){
                s.checkLogin();
                if($location.path().indexOf('/myAccount') != -1){
                    $timeout(checkLogin, 2000);
                }
            }

            s.accountDetails = resolveParams.accountDetails;
            s.shippingDetails = resolveParams.shippingDetails;
            s.masterCredit = resolveParams.paymentPreferences ?
                resolveParams.paymentPreferences.masterCredit.substring(resolveParams.paymentPreferences.masterCredit.length - 4) : null;

            s.Notify_me_about_Promotions = true;
            s.categoriesPromotions = [
                { categoryName : 'Tablets', categoryValue : true, },
                { categoryName : 'Laptops', categoryValue : false, },
                { categoryName : 'Headphones', categoryValue : false, },
                { categoryName : 'Speakers', categoryValue : false, },
                { categoryName : 'Mice', categoryValue : false, },
            ];

            s.checkLogin();

            $("nav .navLinks").css("display" , "none");

            Helper.forAllPage();

            $(document).ready(function(){

                $("#myAccountContainer .cube").each(function(){



                })

            })

        }]);
});




