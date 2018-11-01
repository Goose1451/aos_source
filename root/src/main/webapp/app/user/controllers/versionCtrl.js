define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('versionCtrl', ['$scope', 'versionService', '$location',
        '$timeout', '$filter', '$state', 'Analytics',
        function ($scope, versionService, $location, $timeout, $filter, $state, Analytics) {

            /* record of the versions */
            $scope.records = [
                "VERSION 1.1.6",
                "VERSION 1.1.5",
                "VERSION 1.1.4",
                "VERSION 1.1.3",
                "VERSION 1.1.2"
            ]


            /* Set default value to release_version */
            var release_version;
            var release_version_only;
            $scope.release_version=$scope.records[0];
            $scope.release_version_only=$scope.records[0].split(' ')[1];

            /* Set value according to value chose from the list */
            $scope.getVersion = function (index) {
                $scope.release_version=$scope.records[index];
                $scope.release_version_only=$scope.records[index].split(' ')[1];
                $scope.contentUrl = 'app/user/views/version-page-' + $scope.release_version_only + '.html';
            };

            /* show and hidden list of versions*/
            $scope.login = function (miniTitleId) {
                $("#" + miniTitleId).fadeToggle(300);
            }

            var ____loginInterval;
            $scope.miniTitleOut = function (miniTitleId) {
                if ($("#" + miniTitleId).css('display') != 'none') {
                    ____loginInterval = $timeout(function () {
                        $("#" + miniTitleId).fadeOut(300);
                    }, 2000);
                }
            };

            var current_selected_link;

            $scope.gotoElement = function (sectionId, id) {
                $scope.selected_menu_side_link(id);
                var element = $("#" + sectionId);
                var navHeight = $("nav.pages").height();
                var scrollHight = $("body, html").scrollTop();
                if ( scrollHight == 0)
                    navHeight = 2*navHeight;
                if (element.length > 0){
                    $("body, html").animate({
                        scrollTop: (element.offset().top - (navHeight) - 65 )
                    }, 1000)
                }
            };

            $scope.selected_menu_side_link = function (id) {
                var element = $("#" + id);
                if (current_selected_link)
                    current_selected_link.removeClass('selected');
                element.addClass('selected');
                current_selected_link=element;
            };

    }]);

});