/**
 * Created by correnti on 21/11/2015.
 */

define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('productCtrl', ['$scope', 'productService', 'product',
        function ($scope, productService, product) {

            $scope.Quantity = 2;

            $scope.product = product;

            $(document).mousemove(function(e){
                $("#x").text(e.clientX);
                $("#y").text(e.clientY);
            });

            $scope.addToCart = function(){

                var img = $("#imgToBuy").clone();
                var imgPoss = $("#imgToBuy").offset();
                var mainCartPossition = $("#mainCart").offset();

                img.css({
                    "position" : "absolute",
                    "top": imgPoss.top + "px",
                    "left": imgPoss.left + "px",
                    "display" : "none",
                })
                img.appendTo("#product-image");
                img.fadeIn(1000, function(){
                    img.animate({
                        top : mainCartPossition.top + 5,
                        left : mainCartPossition.left + 10,
                        width: 10,
                        height: 10,
                        opacity: 0.3
                    }, 1000, function(){
                        img.remove();
                        //$rootScope.user.cart.laptops.push($scope.product) = get user
                    });
                })

            }

        }]);
    });

/**
 * Created by correnti on 21/11/2015.
 */
