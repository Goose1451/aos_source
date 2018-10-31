/**
 * Created by kubany on 10/18/2015.
 */
define(['./module'], function (services) {
    'use strict';
    services.service('categoryService', ['$http', '$q', 'userService',
        function ($http, $q, userService) {

            var allData;
            var categories;
            var popularProducts;

            return {
                loadServer: loadServer,
                getAllData: getAllData,
                getCategories: getCategories,
                getCategoryById: getCategoryById,
                getPopularProducts: getPopularProducts,
                getExistingData: getExistingData,
                haveInternet: haveInternet,
                getMostPopularComments: getMostPopularComments,
                nvHandler: nvHandler,
                nv_loadUnuseScripts: nv_loadUnuseScripts,
            }

            function haveInternet() {

                var response = $q.defer();
                // $http({
                //     method: "get",
                //     url: server.getKey() + "catalog/api/v1/catalog/LastUpdate/timestamp"
                // }).success(function () {
                //     response.resolve(true);
                // }).error(function () {
                //     response.resolve(false);
                // });
                response.resolve(true);
                return response.promise;
            }

            function getExistingData() {
                if (!allData) {
                    this.getAllData();
                }
                return allData;
            }

            function getAllData() {
                var response = $q.defer();
                $http({
                    method: "get",
                    url: server.catalog.getAllData()
                }).success(function (res) {
                    Loger.Received(res)
                    var duplicateProductPrice = userService.getDuplicateProductPrice();
                    for (var index1 = 0; index1 < res.length; index1++) {
                        for (var index2 = 0; index2 < res[index1].products.length; index2++) {
                            res[index1].products[index2].price *= duplicateProductPrice;
                        }
                    }
                    res.forEach(function(cat){
                        cat.products.forEach(function(d){
                            d.colors = d.colors.filter(function(col){return col.inStock > 0});
                        });
                    });
                    allData = res;
                    response.resolve(allData);
                }).error(function (err) {
                    //alert('An error occurred, please try again')
                    Loger.Received(err);
                    response.reject('error in load cart (productCartService - loadCartProducts)');
                });
                return response.promise;
            }

            function getMostPopularComments(categoryId) {

                var response = $q.defer();
                if (categoryId != 2) {
                    response.resolve([]);
                }


                $http({
                    method: "get",
                    url: server.catalog.getMostPopularComments()
                }).success(function (res) {
                    var arr = res && res.UserComments ? res.UserComments : [];
                    Loger.Received(arr)

                    angular.forEach(arr, function (itm, index) {

                        if (itm.score > 9.5) {
                            itm.title = "Excellent";
                        }
                        else if (itm.score > 8.5) {
                            itm.title = "Very good";
                        }
                        else if (itm.score > 7.5) {
                            itm.title = "Good";
                        }
                        else if (itm.score > 5.5) {
                            itm.title = "Average";
                        }
                        else if (itm.score > 4.5) {
                            itm.title = "Poor";
                        }
                        else if (itm.score > 2.5) {
                            itm.title = "Very poor";
                        }
                        else {
                            itm.title = "Shameful";
                        }
                        itm.reviewsCount = (itm.comment.length + itm.title.length) * 2;
                    });

                    response.resolve(arr);

                }).error(function (err) {
                    Loger.Received(err);
                    response.reject('error in load cart (productCartService - loadCartProducts)');
                });

                return response.promise;
            }

            function getCategories() {

                var defer = $q.defer();

                if (userService.nv_slowPage()) {
                    $q.all([
                            getCategoryById(1),
                            getCategoryById(2),
                            getCategoryById(3),
                            getCategoryById(4),
                            getCategoryById(5),
                            getCategoryById(1),
                            getCategoryById(2),
                            getCategoryById(3),
                            getCategoryById(4),
                            getCategoryById(5),
                        ])
                        .then(function (res) {
                            categories = [];
                            angular.forEach(res, function(_category){
                                for(var i = 0; i < _category.products.length; i++){
                                    _category.products[i].id =_category.products[i].productId;
                                    _category.products[i].managedImageId =_category.products[i].imageUrl;
                                }
                                _category.managedImageId =_category.categoryImageId;
                                categories.push(_category);
                            })
                            categories = duplicateProductPrice(categories);
                            defer.resolve(categories);
                        });
                }
                else {
                    if (categories) {
                        defer.resolve(categories);
                    }
                    else {
                        Helper.enableLoader();
                        $http({
                            method: "get",
                            url: server.catalog.getCategories(),
                        }).success(function (res) {
                            Helper.disableLoader();
                            Loger.Received(res)
                            res = duplicateProductPrice(res);
                            categories = res;
                            defer.resolve(res)
                        }).error(function (err) {
                            Helper.disableLoader();
                            Loger.Received(err)
                            defer.reject(null)
                        });
                    }
                }
                return defer.promise;
            }

            function duplicateProductPrice(res){
                var duplicateProductPrice = userService.getDuplicateProductPrice();
                for (var index1 = 0; index1 < res.length; index1++) {
                    for (var index2 = 0; index2 < res[index1].products.length; index2++) {
                        res[index1].products[index2].price *= duplicateProductPrice;
                    }
                }
                return res;
            }

            function getCategoryById(id) {

                var defer = $q.defer();
                var found = false;
                if (allData) {
                    for (var i = 0; i < allData.length; i++) {
                        var category = allData[i];
                        if (category.categoryId == id) {
                            defer.resolve(category);
                            print("category return from app");
                            found = true;
                        }
                    }
                }
                if (!found) {
                    if (id == '') {
                        defer.resolve(null)
                    }
                    else {
                        Helper.enableLoader();
                        $http({
                            method: "get",
                            url: server.catalog.getCategoryById(id),
                        }).success(function (res) {
                            print("category return from server");
                            Helper.disableLoader();
                            Loger.Received(res)
                            var duplicateProductPrice = userService.getDuplicateProductPrice();
                            for (var index = 0; index < res.products.length; index++) {
                                res.products[index].price *= duplicateProductPrice;
                            }
                            res.products.forEach(function(d){
                                d.colors = d.colors.filter(function(col){return col.inStock > 0});
                            });

                            defer.resolve(res)
                        }).error(function (err) {
                            Helper.disableLoader();
                            Loger.Received(err)
                            defer.reject(err)
                        });
                    }
                }
                return defer.promise;
            };

            function getPopularProducts() {

                var defer = $q.defer();
                if (popularProducts && !userService.nv_slowPage()) {
                    print("popularProducts return from app");
                    defer.resolve(popularProducts);
                }
                else {

                    var times = userService.nv_slowPage() ? 3 : 1;
                    var counterHttptimesCalls = 0;
                    for(var i = 0; i < times; i++) {
                        setTimeout(function () {
                            $http({
                                method: "get",
                                url: server.catalog.getPopularProducts(),
                            }).success(function (res) {
                                Loger.Received(res)
                                print("popularProducts return from server");
                                var duplicateProductPrice = userService.getDuplicateProductPrice();
                                for (var index = 0; index < res.length; index++) {
                                    res[index].price *= duplicateProductPrice;
                                }
                                popularProducts = res;
                                if (counterHttptimesCalls + 1 >= times) {
                                    defer.resolve(res)
                                }
                                else {
                                    counterHttptimesCalls++;
                                }
                            }).error(function (err) {
                                Loger.Received(err)
                                defer.reject(null)
                            });
                        }, 600 * (i + 1));
                    }
                }
                return defer.promise;
            }

            function loadServer() {
                var response = $q.defer();

                var file = 'services.properties'
                console.log("Extracting file: " + file);

                var rawFile = new XMLHttpRequest();

                rawFile.open("GET", file, false);
                rawFile.onreadystatechange = function () {

                    if (rawFile.readyState === 4) {
                        if (rawFile.status === 200 || rawFile.status == 0) {
                            var fileText = rawFile.responseText;
                            var rawFile_responseText = fileText;
                            fileText = fileText.split('');
                            var _param = '';
                            var _value = '';
                            var attr = true;
                            var arrayApi = [];
                            var invalidChars = '#';
                            fileText.forEach(function (a) {
                                switch (a.charCodeAt(0)) {
                                    case 10:
                                    case 13:
                                        var validParam = true;
                                        for (var i = 0; i < invalidChars.length; i++) {
                                            if (_param.indexOf(invalidChars[i]) != -1) {
                                                validParam = false;
                                                break;
                                            }
                                        }
                                        if (validParam && _param != '' && _value != '') {
                                            arrayApi.push("{\"" + _param.split(".").join("_") + "\":\"" + _value + "\"}");
                                            _param = '';
                                            _value = '';
                                        }
                                        attr = true;
                                        break;
                                    case 61:
                                        attr = false;
                                        break;
                                    default:
                                        if (attr) {
                                            _param += a;
                                        } else {
                                            _value += a;
                                        }
                                        break;
                                }
                            });

                            arrayApi.forEach(function (a) {
                                var jsonObj = JSON.parse(a);
                                services_properties[Object.keys(jsonObj)] = jsonObj[Object.keys(jsonObj)];
                            });

                            var reverseProxy = services_properties['reverse_proxy'] === 'true';
                            var hostKey = window.location.hostname;
                            var protocol = window.location.protocol;
                            if(services_properties['single_machine_deployment'] === 'true'){
                                server.setKey(protocol + "//" + hostKey +
                                    (reverseProxy ? "" : ":" +
                                    services_properties['catalog_service_url_port'])+ "/");

                                server.setCatalogKey(protocol + "//" + hostKey +
                                    (reverseProxy ? "" : ":" +
                                    services_properties['catalog_service_url_port']) + "/" + services_properties['catalog_service_url_suffix'] + "/");

                                server.setOrderKey(protocol + "//" + hostKey +
                                    (reverseProxy ? "" : ":" +
                                    services_properties['order_service_url_port']) + "/" + services_properties['order_service_url_suffix'] + "/");

                                server.setWsdlPath(protocol + "//" +
                                    hostKey +
                                    (reverseProxy ? "" : ":" +
                                    services_properties['account_soapservice_url_port']) + "/" +
                                    services_properties['account_soapservice_url_suffix'] + "/");

                            } if(reverseProxy === 'true'){
                                server.setKey(protocol + "//" + hostKey + "/");
                                server.setCatalogKey(protocol + "//" + hostKey + "/");
                                server.setOrderKey(protocol + "//" + hostKey + "/");
                                server.setWsdlPath(protocol + "//" + hostKey + "/" +
                                    services_properties['account_soapservice_url_suffix'] + "/");
                            }
                            else{
                                server.setKey("http://" + services_properties['catalog_service_url_host'] + ":" +
                                    services_properties['catalog_service_url_port'] + "/");

                                server.setCatalogKey("http://" + services_properties['catalog_service_url_host'] + ":" +
                                    services_properties['catalog_service_url_port'] + "/" + services_properties['catalog_service_url_suffix'] + "/");

                                server.setOrderKey("http://" + services_properties['order_service_url_host'] + ":" +
                                    services_properties['order_service_url_port'] + "/" + services_properties['order_service_url_suffix'] + "/");

                                server.setWsdlPath("http://" +
                                    services_properties['account_soapservice_url_host'] + ":" +
                                    services_properties['account_soapservice_url_port'] + "/" +
                                    services_properties['account_soapservice_url_suffix'] + "/");
                            }
                            response.resolve("OK");

                        }
                    }
                }
                rawFile.send(null)


                //$http({
                //    method: "get",
                //    url: server.catalog.getAllData()
                //}).success(function (res) {
                //    Loger.Received(res)
                //    var duplicateProductPrice = userService.getDuplicateProductPrice();
                //    for(var index1= 0 ; index1 < res.length; index1++){
                //        for(var index2= 0 ; index2 < res[index1].products.length; index2++){
                //            res[index1].products[index2].price *= duplicateProductPrice;
                //        }
                //    }
                //    allData = res;
                //    response.resolve(allData);
                //}).error(function (err) {
                //    alert('An error occurred, please try again')
                //    Loger.Received(err);
                //    response.reject('error in load cart (productCartService - loadCartProducts)');
                //});


                return response.promise;
            }

            function nvHandler() {


                var defer = $q.defer();
                if (userService.nv_slowPage()) {
                    Helper.enableLoader();
                    var calls = 6;
                    for (var index = 0; index < calls; index++) {
                        var code;
                        switch (index){
                            case 0:
                                code = 50;
                                break;
                            case 1: case 2:
                                code = 44;
                                break;
                            case 3:
                                code = 53;
                                break;
                            case 4:
                                code = 49;
                                break;
                            case 5:
                                code = 40;
                                break;
                            default:
                                code = 40;
                                break;
                        }

                        $http({
                            method: "get",
                            url: server.catalog.nvHandler(code),
                        }).success(function (res) {
                            Loger.Received(res)
                            defer.resolve()
                        }).error(function (err) {
                            Loger.Received(err)
                            //defer.resolve()
                            if (index + 1 >= calls) {
                                defer.resolve();
                            }
                        });
                    }
                }
                else {
                    defer.resolve();
                }

                return defer.promise;
            }


            function nv_loadUnuseScripts(){
                var defer = $q.defer();

                if(userService.nv_slowPage()) {
                    $http({
                        method: "get",
                        url: "scripts/nv_files/angular-cookies-for-nv.js",
                    }).success(function () {
                        $http({
                            method: "get",
                            url: "scripts/nv_files/generate-sourcemap-for-nv.js",
                        }).success(function () {
                            $http({
                                method: "get",
                                url: "scripts/nv_files/route-for-nv.js",
                            }).success(function () {
                                $http({
                                    method: "get",
                                    url: "scripts/nv_files/SourceMapper-for-nv.js",
                                }).success(function () {
                                    defer.resolve(null)
                                });
                            });
                        });

                    }).error(function (err) {
                        Helper.disableLoader();
                        Loger.Received(err)
                        defer.reject(null)
                    });
                }

                return defer.promise;
            };

        }]);
});
