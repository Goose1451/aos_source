/**
 * Created by correnti on 06/12/2015.
 */

define(['./module'], function(controllers){
    'use strict';
    controllers.controller('shoppingCartCtrl', ['$scope', 'productsCartService',
        function(s, cartService){

            console.log(s.cart)

            Helper.forAllPage();
            $("nav .navLinks").css("display" , "none");
    }]);
});