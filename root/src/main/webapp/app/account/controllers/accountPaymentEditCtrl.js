/**
 * Created by correnti on 02/02/2016.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('accountPaymentEditCtrl', ['$scope', '$timeout',
        '$location', 'resolveParams', 'accountService',
        function (s, $timeout, $location, resolveParams, accountService) {

            l("resolveParams.paymentPreferences");
            l(resolveParams.paymentPreferences);
            l("resolveParams.paymentPreferences");

            var _i = 0;
            checkLogin();
            function checkLogin() {
                console.log(++_i);
                s.checkLogin();
                if ($location.path().indexOf('/accountPaymentEdit') != -1) {
                    $timeout(checkLogin, 2000);
                }
            }

            s.imgRadioButton = 1;

            s.years = [];
            var now = new Date();
            for (var i = 0; i < 10; i++) {
                s.years.push((now.getFullYear() + i) + "");
            }

            s.card = {
                number: '',
                cvv: '',
                expirationDate: {
                    month: '',
                    year: ''
                },
                name: '',
            }

            s.saveMasterCredit = function () {
                var response;
                if (true) {
                    response = accountService.addMasterCreditMethod(s.card)
                }
                else {
                    response = accountService.updateMasterCreditMethod(s.card)
                }
                response.then(function (response) {
                    if (response && response.REASON) {
                        s.accountDetailsAnswer = {
                            message: response.REASON,
                            class: response.SUCCESS == 'true' ? 'valid' : 'invalid'
                        }
                        if (response.SUCCESS == 'true') {
                            $location.path('myAccount');
                        }
                        else {
                            $timeout(function () {
                                s.accountDetailsAnswer = {message: '', class: 'invalid'}
                            }, 4000)
                        }
                    }
                });
            }

            s.savePay = { username : '', password : '' }
            s.saveSafePay = function () {
                var response;
                if (true) {
                    response = accountService.addSafePayMethod(s.savePay)
                }
                else {
                    response = accountService.updateSafePayMethod(s.savePay)
                }
                response.then(function (response) {
                    if (response && response.REASON) {
                        s.accountDetailsAnswer = {
                            message: response.REASON,
                            class: response.SUCCESS == 'true' ? 'valid' : 'invalid'
                        }
                        if (response.SUCCESS == 'true') {
                            $location.path('myAccount');
                        }
                        else {
                            $timeout(function () {
                                s.accountDetailsAnswer = {message: '', class: 'invalid'}
                            }, 4000)
                        }
                    }
                });
            }

            Helper.forAllPage();

        }]);
});






