/**
 * Created by kubany on 10/29/2015.
 */
define(['./module'], function (directives) {
    'use strict';
    directives.directive('loginModal', ['$rootScope', 'userService', 'ipCookie',
        '$templateCache', '$location', '$timeout', 'productsCartService', '$filter', 'Analytics',
        function ($rootScope, userService, $cookie, $templateCache, $location,
                  $timeout, productsCartService, $filter, Analytics) {
            return {
                restrict: 'E',
                replace: false,
                template: $templateCache.get('app/user/partials/login.html'),
                controller: function ($scope) {


                    /* VARIABLES */
                    $scope.rememberMe = false;
                    //$scope.message = "";
                    $scope.message = {text: $filter('translate')('OR'), _class: ''};
                    var _____errorMessage;

                    $scope.setErrorMessage = function(reason) {
                        $timeout.cancel(_____errorMessage);
                        _____errorMessage = $timeout(function () {
                            $scope.message.text = $filter('translate')('OR');
                            $scope.message._class = "";
                            $rootScope.rsMessage.text = "";
                            $rootScope.rsMessage._class = "";
                        }, 10000);
                        $timeout(function () {
                            $scope.message = { text: reason, _class: "invalid" }
                            $rootScope.rsMessage = { text: reason, _class: "invalid" }
                        }, 0);
                    }

                    /*================================ END VARIABLES ======================================*/

                    /* Sign user in */
                    $scope.signIn = function (user, rememberMe) {

                        var userBlocked = $cookie(user.loginUser);
                        if (userBlocked) {
                            var rest = userBlocked.dateUntil - userBlocked.dateFrom;
                            var date = new Date();
                            var millisecondsLess = userBlocked.dateUntil - new Date(date.getTime() - new Date(rest)).getTime();

                            $scope.setErrorMessage(userBlocked.reason);
                            if (millisecondsLess < 0) {
                                $cookie.remove(user.loginUser);
                            }
                            else {
                                return user;
                            }
                        }

                        userService.login(user).then(function (response) {

                            if (response.userId != -1 && response.success) {

                                Analytics.trackEvent('Account', 'signIn', user);
                                if (response.userId === undefined) {
                                    $scope.setErrorMessage(response.reason);
                                    return user;
                                }

                                userCookie.fillParams(user.loginUser, user.loginPassword, user.email, response);
                                $rootScope.userCookie = userCookie;
                                var userKeyTrimmed = userCookie.getKey(userCookie).replace(/[^a-zA-Z ]/g, "");

                                if (rememberMe) {
                                    $cookie(userKeyTrimmed, userCookie);
                                    $cookie('lastLogin', userKeyTrimmed);
                                    var userInfoString = JSON.stringify(userCookie);
                                    localStorage.setItem('rememberMe', userInfoString);
                                    $scope.refreshTimeOut();
                                }
                                else {
                                    $cookie.remove("userCookie" + user.email);
                                    localStorage.removeItem('rememberMe');
                                }

                                productsCartService.joinCartProducts().then(function (cart) {
                                    $scope.cart = cart;
                                },function (error) {
                                    userService.singOut().then(function () {
                                        $cookie.remove('lastLogin');
                                        $rootScope.userCookie = undefined;
                                        $scope.loginUser = {email: '', loginPassword: '', loginUser: '',}

                                        // productsCartService.loadCartProducts().then(function (cart) {
                                        //     $scope.cart = cart;
                                        // });
                                        $(".mini-title").css("display", "none");
                                        $(".mini-title-help").css("display", "none");
                                    });
                                });

                                if ($location.path() == '/register') {
                                    $location.path('/')
                                }

                                wellcome();
                            }
                            else {
                                if (response.reason.toLowerCase().indexOf('blocked') != -1) {

                                    userService.getConfiguration().then(function(conf){
                                       var now = new Date();
                                        userBlocked = {
                                            dateFrom: now.getTime(),
                                            dateUntil: now.getTime() + (conf.loginBlockingIntervalInSeconds * 1000),
                                            reason : response.reason,
                                        }
                                        $cookie(user.loginUser, userBlocked, {
                                            expirationUnit: 'milliseconds', expires: userBlocked.dateUntil - userBlocked.dateFrom,
                                        });

                                    });
                                }
                                $scope.setErrorMessage(response.reason);
                            }
                            return user;
                        }, function(){
                            $scope.setErrorMessage($filter('translate')('login_faild'));
                        });
                    }
                    /*=============================== end Sign in ===============================*/


                    /* increment wrong user login  */
                    var incrementLogins = function () {
                        var test = $cookie("loginsCounter");
                        var loginsCounter = test === undefined ? -1 : test;
                        return function () {
                            if (loginsCounter == -1) {
                                var test = $cookie("loginsCounter");
                                if (test === undefined) {
                                    test = 0;
                                }

                                loginsCounter = test;
                            }
                            var count = ++loginsCounter;
                            $cookie("loginsCounter", count, {expires: 365});
                            return count;
                        }
                    }();
                    /*=============================== end increment logins ===============================*/


                    /* create to user new account in application */
                    $scope.createNewAccount = function (user) {
                        wellcome();
                        $location.path('register');
                    }
                    /*================ end create to user new account in application  ========================*/

                    /* go to about page */
                    $scope.openAboutPage = function (user) {
                        wellcome();
                        $location.path('about');
                    }
                    /*================ end go to about page  ========================*/

                    $scope.forgotPassword = function () {
                        console.log("forgot Password not done yet!");
                        //$location.path('404');
                    }


                    $scope.singWithFacebook = function (user) {
                        console.log("Sing With Facebook not done yet!");
                        userService.facebookLogin().then(function(success){

                        }, function (error){
                            $scope.setErrorMessage(error.data.reason);
                        } );
                    }

                }
            };
        }
    ]);
});


function wellcome() {
    $(".login").css("opacity", "0.2")
    $(".PopUp > div:nth-child(1)").animate({"top": "-150%"}, 600, function () {

        $("#mobile-section").css("left", "-" + $("#mobile-section").css("width"));
        $(".PopUp").fadeOut(100);
        $("body").css("overflow-y", "scroll")
        $(".login").css("opacity", "1");
    });
}
