/**
 * Created by kubany on 10/13/2015.
 */
define(['./module'], function (controllers) {


    'use strict';
    controllers.controller('mainCtrl', ['$scope', '$q', 'productService', 'smoothScroll', 'userService', 'orderService',
        '$location', 'ipCookie', '$rootScope', 'productsCartService', '$filter', '$state', '$timeout', 'categoryService',
        function ($scope, $q, productService, smoothScroll, userService, orderService, $location, $cookie, $rootScope,
                  productsCartService, $filter, $state, $timeout, categoryService) {

            var ctrl = this;
            ctrl.config = {};
            $scope.cart;
            $scope.autoCompleteValue = '';
            $scope.autoCompleteResult = {};


            var EnterInFocus = {
                login: 'login',
                search: 'search'
            };
            var enterInFocus = "";

            categoryService.loadServer().then(function () {


                $(document).on({
                    keydown: function (event) {
                        var code = event.keyCode || event.which;
                        if (code === 13) {
                            switch (enterInFocus) {
                                case EnterInFocus.login:
                                    if ($scope.loginUser.loginPassword != "" && $scope.loginUser.loginUser != "") {
                                        if ($scope.loginUser.email == "" && ctrl.config.emailAddressInLogin) {
                                        }
                                        else {
                                            $scope.signIn($scope.loginUser, ctrl.rememberMe);
                                        }
                                    }
                                    break;
                                case EnterInFocus.search:

                                    break;
                            }
                        }
                    }
                });

                /* Get all products data */
                categoryService.getAllData();
                /*===========================  end Get all products data ============================*/


                /* Get configuration */
                userService.getConfiguration().then(function (response) {
                    ctrl.config = response;
                    $scope.refreshTimeOut();
                });
                /*===========================  end Get configuration ============================*/

                /* loading Cart section  */

                productsCartService.loadCartProducts().then(function (cart) {
                    $scope.cart = cart;
                    $timeout(function () {
                        productsCartService.checkOutOfStockProductsInCart().then(function (_cart) {
                            $scope.cart = _cart;
                        });
                    })
                });
                /* end loading Cart section  */


                $scope.go_up = function () {
                    $('body, html').animate({scrollTop: 0}, 10, function () {
                        if ($(".autoCompleteCover").width() < 100) {
                            $("nav .navLinks").css("display", "block");
                        }
                    });
                };

                /* About section */

                $scope.help = function (miniTitleId) {
                    $("#loginMiniTitle").fadeOut(300);
                    $("#loginMobileMiniTitle").fadeOut(300);
                    $("#" + miniTitleId).fadeToggle(300);
                };


                /* User section */


                $scope.login = function (miniTitleId) {
                    $("#helpMiniTitle").fadeOut(300);

                    if ($rootScope.userCookie) {
                        $("#helpMobileMiniTitle").fadeOut(300);
                        $("#" + miniTitleId).fadeToggle(300);
                        return;
                    }

                    enterInFocus = EnterInFocus.login;
                    var userInfo = localStorage.getItem('rememberMe');
                    if(userInfo){
                        var parsedUserInfo = JSON.parse(userInfo);
                        $scope.loginUser.loginUser = parsedUserInfo.name;
                        $scope.loginUser.loginPassword = parsedUserInfo.userPassword;
                        $scope.mainCtrl.rememberMe = true;
                    }

                    Helper.mobileSectionClose();
                    $('#toolTipCart').css('display', 'none');
                    var windowsWidth = $(window).width();
                    var top = "5%";
                    if (windowsWidth < 480) {
                        top = "0";
                    }
                    else if (windowsWidth < 700) {
                        top = "18%";
                    }

                    $(".PopUp").fadeIn(100, function () {
                        $(".PopUp > div:nth-child(1)").animate({"top": top}, 600);
                        $("body").css({"left": "0px"})
                    });

                };

                $(".PopUp, .closeBtn.loginPopUpCloseBtn").click(function () {

                    setEnterInFocusHandler();

                    $(".PopUp > div:nth-child(1)").animate({
                        "top": "-150%"
                    }, 600, function () {
                        $(".PopUp").fadeOut(100, function () {
                            $("body").css("overflow-y", "scroll");
                        });
                    });
                });


                function setEnterInFocusHandler() {
                    if (document.location.hash.indexOf("#/search") != -1) {
                        enterInFocus = enterInFocus.search;
                    } else {
                        enterInFocus = "";
                    }
                }


                $(".PopUp > div").click(function (e) {
                    e.stopPropagation();
                });

                /* END User section */


                /* Application helper section */


                $scope.redirect = function (path) {
                    Helper.mobileSectionClose();
                    $location.path(path);
                };

                $scope.mobileRedirect = function (path) {
                    Helper.mobileSectionClose();
                    $state.go(path)
                };

                $scope.mobileRedirectHelp = function (path) {

                    Helper.mobileSectionClose();
                    $state.go(path)
                };

                //setTimeout(function(){
                //    $scope.gotoElement("contact_us")
                //}, 1500)

                $scope.gotoElement = function (id) {
                    var element = $("#" + id);
                    if (element.length > 0) {
                        $("body, html").animate({
                            scrollTop: (element.offset().top - 65) + "px"
                        }, 1000)
                    }
                };


                var _____autoLogOut;
                $scope.refreshTimeOut = function () {

                    if (ctrl.config == null) {
                        return;
                    }
                    if (JSON.stringify(ctrl.config) == JSON.stringify({})) {
                        return;
                    }

                    if (orderService.userIsLogin()) {

                        $timeout.cancel(_____autoLogOut);
                        _____autoLogOut = $timeout(function () {
                            $scope.signOut()
                        }, ctrl.config.userLoginTimeOut == 0 ? (60 * 60000)
                            : (ctrl.config.userLoginTimeOut * 60000));

                    }
                };

                /* END Application helper section */


                $rootScope.$on('clearCartEvent', function (event, args) {
                    productsCartService.clearCart().then(function (cart) {
                        $scope.cart = cart;
                    });
                });


                $rootScope.$on('$locationChangeStart', function (event, current, previous) {
                    //$("html, body").css({opacity: 0});
                    //$(".waitBackground").css({opacity: 1, display: "block",});
                    //$("div.loader").css({opacity: 1, display: "block",});
                });

                $rootScope.$on('$locationChangeSuccess', function (event, current, previous) {

                    $scope.welcome = $location.path().indexOf('/welcome') <= -1 && $location.path().indexOf('/404') <= -1;
                    $scope.showCategoryHeader = $location.path().indexOf('/search') <= -1;

                    //$("#searchSection #output").css("opacity", $location.path().indexOf('/search') == -1 ? 1 : 0);

                    Helper.UpdatePageFixed();

                    if ($location.path().indexOf('/search') == -1 && $scope.closeSearchForce + "" != "undefined") {
                        $scope.closeSearchForce();
                    }
                    Helper.mobileSectionClose();

                    $timeout(function () {
                        if ($location.path() == '/') {
                            if ($(".autoCompleteCover").width() < 100) {
                                $("nav .navLinks").css("display", "block");
                            }
                        }
                        else {
                            $("nav .navLinks").css("display", "none");
                        }
                    }, 1050);
                    $scope.refreshTimeOut();
                    $scope.miniTitleOut();
                });

                $("#mobile-section").css("left", "-" + $("#mobile-section").css("width"));
                Helper.mobile_section_moved = $("#mobile-section").width();

                $scope.openMobileSection = function () {
                    Helper.mobileSectionHandler();
                };

                $scope.$on('$viewContentLoaded', function (event) {
                    Helper.forAllPage();
                });

            });

            $scope.loginUser = {email: '', loginPassword: '', loginUser: ''};
            this.rememberMe = false;

            $scope.checkCart = function () {

                if ($scope.cart + "" == "undefined" || $scope.cart.productsInCart.length == 0) {
                    switch ($location.$$path) {
                        case '/login':
                        case '/MyOrders':
                        case '/orderPayment':
                            $state.go("default");
                            break;
                    }
                }
            };

            $scope.checkLogin = function () {
                var user = $rootScope.userCookie;
                if (!(user && user.response && user.response.userId != -1 && user.response.t_authorization)) {
                    $state.go("default");
                }
            };


            var _setUser = 0;
            $scope.setUser = function () {
                if (_setUser > 0) {
                    $scope.signIn({email: "", loginPassword: "Aa123", loginUser: "sergio"}, true);
                }
                _setUser++;
                setTimeout(function () {
                    _setUser = 0;
                }, 1000);
            };


            $scope.accountSection = function () {
                $state.go('myAccount');
            };

            $scope.signOut = function (even) {

                if (even) {
                    even.stopPropagation();
                }
                userService.singOut().then(function () {
                    $cookie.remove(userCookie.getKey($rootScope.userCookie).replace(/[^a-zA-Z ]/g, ""));
                    $cookie.remove('lastLogin');
                    $rootScope.userCookie = undefined;
                    $scope.loginUser = {email: '', loginPassword: '', loginUser: ''};

                    productsCartService.loadCartProducts().then(function (cart) {
                        $scope.cart = cart;
                        $scope.checkCart();
                    });
                    $(".mini-title").css("display", "none");
                    $(".mini-title-help").css("display", "none");
                });
            };

            $scope.miniTitleIn = function (miniTitleId) {
                if ($rootScope.userCookie) {
                    $("#" + miniTitleId).stop().delay(500).fadeIn(300);
                }
            };

            $scope.miniTitleOut = function (miniTitleId) {
                if ($rootScope.userCookie) {
                    $("#" + miniTitleId).stop().delay(500).fadeOut(300);
                }
            };

            var ____loginInterval;
            $scope.miniTitleIn = function () {
                if (____loginInterval) {
                    $timeout.cancel(____loginInterval);
                }
            };

            $scope.miniTitleOut = function (miniTitleId) {
                if ($("#" + miniTitleId).css('display') != 'none') {
                    ____loginInterval = $timeout(function () {
                        $("#" + miniTitleId).fadeOut(300);
                    }, 2000);
                }
            };

            /* Cart section  */

            $scope.removeProduct = function (index, event) {

                if (event) {
                    event.stopPropagation();
                }
                productsCartService.removeProduct(index).then(function (cart) {
                    $scope.cart = cart;
                    $scope.checkCart();
                    fixToolTipCartHeight();

                    if ($(window).width() < 480) {
                        $("#toast a").html($filter("translate")("Product_removed"));
                        $("#toast").stop().fadeIn(500);
                        setTimeout(function () {
                            $("#toast").stop().fadeOut(1500);
                        }, 1500)
                    }

                });
            };

            $scope.addProduct = function (product, quantity, toastMessage) {
                clearInterval(Helper.____closeTooTipCart);
                var defer = $q.defer();
                productsCartService.addProduct(product, quantity).then(function (cart) {
                    $scope.cart = cart;
                    animateToolTipCart(toastMessage);
                    fixToolTipCartHeight();
                    defer.resolve(cart);
                }, function(err){
                    defer.reject(err)
                });
                return defer.promise;
            };

            $scope.updateProduct = function (product, color, quantity, oldColor, toastMessage) {
                var defer = $q.defer();
                productsCartService.updateProduct(product, color, quantity, oldColor)
                    .then(function (cart) {
                        $scope.cart = cart;
                        animateToolTipCart(toastMessage);
                        defer.resolve(cart);
                    });
                return defer.promise;
            };

            function animateToolTipCart(toastMessage) {
                clearInterval(Helper.____closeTooTipCart);

                if ($(window).width() < 480) {
                    $("#toast a").html(toastMessage);
                    $("#toast").stop().fadeIn(200);
                    setTimeout(function () {
                        $("#toast").stop().fadeOut(600);
                    }, 1500)
                }

                $('#toolTipCart').delay(100).slideDown(function () {
                    $('#toolTipCart tbody').stop().animate({
                        scrollTop: 0 + 'px'
                    }, 500, function () {
                        Helper.____closeTooTipCart = setTimeout(function () {
                            $('#toolTipCart').stop().slideUp();
                        }, 4000)
                    });
                });
            }

            var _____enterCart;
            var enterInCart = false;
            $scope.enterCart = function () {
                clearInterval(Helper.____closeTooTipCart); // defined in categoryTypeProductsDrtv -> addProduct
                enterInCart = true;
                _____enterCart = setTimeout(function () {
                    $('#toolTipCart').stop().slideDown();
                    fixToolTipCartHeight();
                }, 500);
            };

            $scope.leaveCart = function () {
                enterInCart = false;
                clearInterval(_____enterCart);
                closeToolTipCart();
            };


            function closeToolTipCart() {

                var toolTipCart = $('#toolTipCart');
                if (toolTipCart.length > 0) {
                    setTimeout(function () {
                        if (!enterInCart) {
                            toolTipCart.stop().slideUp(function () {
                                $('#toolTipCart tbody').animate({scrollTop: 0}, 500);
                            });
                        }
                    }, 3000);
                }
            }

            function fixToolTipCartHeight() {
                var winHeight = $(window).height() - 200;
                var prodsHeight = ($scope.cart.productsInCart.length * 104);
                var heightToAsign = (Math.round(winHeight / 104) * 104);
                if (winHeight < prodsHeight) {
                    $("#toolTipCart tbody").css({
                        "height": heightToAsign + "px",
                        "overflow-y": "auto"
                    });
                }
                else {
                    $("#toolTipCart tbody").css({
                        "height": prodsHeight + "px",
                        "overflow-y": "hidden"
                    });
                }
            }

            /* END Cart section */


            this.getRequire = function (nameRequire) {
                return JSON.stringify({
                    error: $filter("translate")(nameRequire) + " " + $filter("translate")("field_is_required")
                });
            };


            this.getCompare = function (name, model) {
                var nameAfterTranslate = $filter("translate")(name);
                return JSON.stringify({
                    error: nameAfterTranslate + $filter("translate")('This_field_not_match_with'),
                    info: "- " + $filter("translate")("Same_as") + " " + nameAfterTranslate,
                    model: model
                });
            };

            this.getCardNumber = function (exactlyNum) {
                return JSON.stringify({
                    error: $filter("translate")('Invalid_Card_number'),
                    info: "- " + $filter("translate")("Use_exactly") + " " + exactlyNum + " " + $filter("translate")('numbers'),
                    exactly: exactlyNum,
                    regex: "^[0-9]*$"
                });
            };

            this.getPattern = function (data) {

                if (typeof data === 'string') {
                    switch (data) {
                        case 'Username':
                            data = [
                                ['letters_number_symbols_only', 'letters_number_symbols_only',
                                    '^[A-Za-z0-9_.-]{0,999}$']];
                            break;
                        case 'Email':
                            data = [['email_no_formatted_correctly', '',
                                '^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,100})$']];
                            break;
                        case 'Password':
                            data = [
                                ['one_lower_letter_required', 'Including_one_lower_letter', '(?=.*[a-z])'],
                                ['one_upper_letter_required', 'Including_one_upper_letter', '(?=.*[A-Z])'],
                                ['one_number_required', 'Including_one_number', '(?=.*[0-9])']
                            ];
                            break;
                        case 'Compare':
                            data = [
                                ['letters_number_symbols_only', 'letters_number_symbols_only',
                                    '^[A-Za-z0-9_.-]{0,999}$']];
                            break;
                        case 'CCV_Number':
                            data = [
                                ['Invalid_CCV_number', 'Valid_CCV_number_required', '^[0-9]{3,3}$']];
                            break;
                        default:
                            throw "type of pattern not match (this.getPattern('" + data + "');"
                    }
                }
                var arr = [];
                for (var i = 0; i < data.length; i++) {
                    var info = $filter("translate")(data[i][1]);
                    arr.push({
                        error: $filter("translate")(data[i][0]),
                        info: info != '' ? "- " + info : '',
                        regex: data[i][2]
                    });
                }
                return JSON.stringify({
                    regexes: arr
                });
            };

            this.getMin = function (min) {
                return JSON.stringify({
                    error: $filter("translate")("Use_up_of") + " " + min + " " + $filter("translate")("character_or_longer"),
                    info: "- " + $filter("translate")("Use_up_of") + " " + min + " " + $filter("translate")("character_or_longer"),
                    min: min
                });
            };


            this.getMax = function (max) {
                return JSON.stringify({
                    error: $filter("translate")("Use_maximum") + " " + max + " " + $filter("translate")("character"),
                    info: "- " + $filter("translate")("Use_maximum") + " " + max + " " + $filter("translate")("character"),
                    max: max
                });
            };

            this.getAgreeAgreementRequire = function () {
                return JSON.stringify({
                    error: $filter("translate")("AgreeAgreementRequire"),
                    info: $filter("translate")("AgreeAgreementRequire")
                });
            };

            this.getNoticeInfo = function () {
                return JSON.stringify([
                    $filter("translate")("This_is_a_demo"),
                    $filter("translate")("Please_enter_a_fake_data")
                ]);
            };

            this.closeImgTeam = function () {
                $("#teamIdToAnimate").addClass("hinge");
                setTimeout(function () {
                    $("#team").fadeOut(1000);
                    setTimeout(function () {
                        $("#teamIdToAnimate").removeClass("hinge");
                        $("#teamIdToAnimate").css({
                            display: "none"
                        });
                    }, 1000)
                }, 2500)
            }
        }
    ]);
});








