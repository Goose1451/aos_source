define(['./module'], function (directives) {
    'use strict';
    directives.directive('iconDeleteAccountSvg', ['$templateCache', function($templateCache){
        return{
            restrict: 'A',
            replace: true,
            template: $templateCache.get('app/partials/icon-delete-account-svg.html')
        }
    }]);
});