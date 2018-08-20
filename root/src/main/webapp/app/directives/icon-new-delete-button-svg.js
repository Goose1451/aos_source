define(['./module'], function (directives) {
    'use strict';
    directives.directive('iconNewDeleteButtonSvg', ['$templateCache', function($templateCache){
        return{
            restrict: 'A',
            replace: true,
            template: $templateCache.get('app/partials/icon-new-delete-button-svg.html')
        }
    }]);
});