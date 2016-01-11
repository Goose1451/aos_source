/**
 * Created by correnti on 14/12/2015.
 */

define(['./module'], function (controllers) {
    'use strict';
    controllers.filter('filterFullArrayforAutoComplate', function(){
        return function(_a, autoCompleteResult, categoryFilter) {

            if(autoCompleteResult[0] == undefined){
                return _a;
            }
            var products = [];
            var max = 0;
            for (var i = 0; i < autoCompleteResult.length; i++){
                max = autoCompleteResult[i].products.length > max ? autoCompleteResult[i].products.length : max;
            }
            for (var i = 0; i < max; i++){
                for (var j = 0; j < autoCompleteResult.length; j++){

                    if(categoryFilter == autoCompleteResult[j].categoryId || categoryFilter == null)
                    {
                        var prd = autoCompleteResult[j].products[i]
                        if(prd){
                            products.push(prd);
                            if(products.length == 6){
                                return products;
                            }
                        }
                    }
                }
            }
            return products;
        };
    });
});

