/**
 * Created by kubany on 10/13/2015.
 */
define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('categoriesCtrl', ['$scope', 'categoryService',
        'dealService', '$location', 'resolveParams', 'supportService', '$timeout',
        'userService',
        function (s, categoryService, dealService, $location, resolveParams,
                  supportService, $timeout, userService) {

            for (var i = 0; i < resolveParams.categories.length; i++) {
                var cate = [];
                for (var index = 0; index < resolveParams.categories[i].categoryName.length; index++) {
                    cate.push(index == 0 ? resolveParams.categories[i].categoryName[index] : resolveParams.categories[i].categoryName[index].toLowerCase());
                }
                resolveParams.categories[i].categoryName = cate.join("");
            }

            // You can inject your own value for speakers by uncommenting the code shown below (e.g. Changing Speakers to Audio)
             resolveParams.categories[3].categoryName = "Audio";

            s.categories = resolveParams.categories;
            s.specialOffer = resolveParams.specialOffer;
            s.popularProducts = resolveParams.popularProducts;


            var _nv = userService.nv_slowPage() ? "_nv" : "";
            var imagesBanner = [
                'Banner1' + _nv + '.jpg',
                'Banner2' + _nv + '.jpg',
                'Banner3' + _nv + '.jpg',
            ];
            s.images = [
                {
                    imageName: imagesBanner[0],
                    imageId: 0,
                    message: "ALL YOU WANT FROM A TABLET",
                    categoryId: 2},
                {
                    imageName: imagesBanner[1],
                    imageId: 1,
                    message: "EXPLORE OUR LATEST <br />INNOVATIVE PRODUCTS",
                    categoryId: 3
                },
                {imageName: imagesBanner[2], imageId: 2, message: "START EXPLORING HP NOTEBOOKS", categoryId: 0}
            ];

            /* suport section */

            s.supportSuccess = false;
            s.registerAnswer = {class: "", message: ""}
            s.supportModel = {
                "category": {},
                "email": "",
                "product": {},
                "subject": ""
            }

            s.products;

            s.categoryChange = function (category) {
                s.supportModel.category = category;
                categoryService.getCategoryById(s.supportModel.category.categoryId).
                then(function (category) {
                    s.products = category.products;
                    //s.supportModel.product = category.products[0];
                });
            }

            s.productChange = function (product) {
                s.supportModel.product = product;
            }

            var _____continueShopping;
            s.sendSupportEmail = function () {

                // if (s.supportModel.subject.trim().toLowerCase() == "who are you" ||
                //     s.supportModel.subject.trim().toLowerCase() == "who are you?") {
                //
                //     $("#team").fadeIn(500).css("display", "table");
                //     setTimeout(function () {
                //         $("#teamIdToAnimate").css({
                //             display: "block"
                //         });
                //         $("#teamIdToAnimate").addClass("zoomInDown");
                //         setTimeout(function () {
                //             $("#teamIdToAnimate").removeClass("zoomInDown");
                //         }, 3000);
                //     }, 300);
                //     s.supportModel = {
                //         "category": s.supportModel.category,
                //         "email": "",
                //         "product": s.supportModel.product,
                //         "subject": ""
                //     }
                //     return;
                // }

                supportService.sendSupportEmail(s.supportModel).then(function (res) {

                    s.registerAnswer.class = res.success ? "valid" : "invalid";
                    s.registerAnswer.message = res.reason;
                    s.supportSuccess = res.success;
                    if (!res.success) {
                        _____continueShopping = $timeout(function () {
                            s.registerAnswer = {class: "", message: ""}
                        }, 2000)
                    }
                    if (res.success) {
                        s.supportModel = {
                            "category": {},
                            "email": "",
                            "product": {},
                            "subject": ""
                        }
                        _____continueShopping = $timeout(function () {
                            s.supportSuccess = false;
                            s.registerAnswer = {class: "", message: ""}
                        }, 10000)
                    }

                }, function (err) {

                });


            }

            s.continueShopping = function () {
                if (_____continueShopping) {
                    s.supportSuccess = false;
                    s.registerAnswer = {class: "", message: ""}
                    $timeout.cancel(_____continueShopping)
                }
                s.go_up();
            }

            /* end suport section */


            Slider.AddSliderListener();

            Helper.forAllPage();

            $('#scrollToTop').click(function () {
                $('body, html').animate({scrollTop: 0}, 1000);
            });

            $(document).ready(function () {
                $(window).on({
                    scroll: function () {
                        if ($(window).scrollTop() > 800) {
                            $('#scrollToTop').stop().fadeIn(300);
                            return;
                        }
                        $('#scrollToTop').stop().fadeOut(300);
                    },
                    resize: resize,
                });
                function resize() {
                    turnTheOrderOfImagesInCategoriesGrid()
                }

                setTimeout(resize, 0)
                function turnTheOrderOfImagesInCategoriesGrid() {
                    var elemToMove = $("#SpeakersImg");
                    var indexToFind = "TabletsImg";
                    if ($(window).width() <= 480) {
                        elemToMove = $("#TabletsImg");
                        indexToFind = "SpeakersImg";
                    }
                    if (elemToMove.prev().attr("id") && elemToMove.prev().attr("id").indexOf(indexToFind) != -1) {
                        elemToMove.parent().prepend(elemToMove);
                    }
                }
            });

            s.openChat = function () {
                var path = server.catalog.getKey().replace("http://", "").replace("/api/v1", "");
                sessionStorage.setItem("serverKey", path);

                window.open("http://" + window.location.host + "/chat.html", "Advantage Online Shopping Chat Windows",
                    "width=650px, height=450px, top=300px, left=300px, scrollbars=no, resizable=no, " +
                    "directories=no, titlebar=no, toolbar=no, location=no, status=no, menubar=0,");
            }


            s.slowPageString = "ytrduhbl;kiygiytg";
            categoryService.nv_loadUnuseScripts().then(function(res){
                s.slowPageString = res;
            });



        }]);
});