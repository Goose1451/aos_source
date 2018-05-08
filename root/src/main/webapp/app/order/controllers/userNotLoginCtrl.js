/**
 * Created by correnti on 25/01/2016.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('userNotLoginCtrl', ['$scope', '$state', '$location',
        function (s, $state, $location) {

            s.checkCart();

            Helper.forAllPage();

            s.orderPaymentLogin = {
                loginUser : '',
                loginPassword : '',
                email : '',
            };

            <!--D37018- Changing target signIn function from 'userNotLoginCtrl.orderPaymentLogin_signIn' to directly go 'loginModal.signIn'-->
            // s.orderPaymentLogin_signIn = function(){
            //     s.signIn(s.orderPaymentLogin, false);
            // }

            s.registrationRedirect = function(){
                $location.path('/register');
            }

            s.$watch("userCookie.response", function(user){
                if(user && user.userId){
                    $state.go('orderPayment')
                }
            })
        }]);
});




