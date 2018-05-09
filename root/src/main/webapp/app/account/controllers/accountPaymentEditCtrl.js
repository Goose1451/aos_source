/**
 * Created by correnti on 02/02/2016.
 */


define(['./module'], function (controllers) {
    'use strict';
    controllers.controller('accountPaymentEditCtrl', ['$scope', '$timeout',
        '$location', 'resolveParams', 'accountService',
        function (s, $timeout, $location, resolveParams, accountService) {

            checkLogin();
            function checkLogin() {
                s.checkLogin();
                if ($location.path().indexOf('/accountPaymentEdit') != -1) {
                    $timeout(checkLogin, 2000);
                }
            }

            s.years = [];
            var now = new Date();
            for (var i = 0; i < 10; i++) {
                s.years.push((now.getFullYear() + i) + "");
            }

            s.month = ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];
            s.calculateMonths = function (value) {
                var arr = Helper.getMonthInYearForMasterCredit(value, s.month);
                if (arr != null) {
                    s.month = arr;
                }
            }


            s.card = {number: '', cvv: '', expirationDate: {month: '', year: ''}, name: ''}
            s.savePay = {username: '', password: ''}

            var alreadyHaveMasterCreditCart = false;
            var alreadyHaveSafePayCart = false;
            var masterCredit = resolveParams.paymentPreferences && resolveParams.paymentPreferences.masterCredit ?
                resolveParams.paymentPreferences.masterCredit : null;

            if (masterCredit != null) {
                try {
                    var m = masterCredit.expirationDate.substring(0, 2)
                }
                catch(err) {
                    m = ( masterCredit.expirationDate += "").substring(0,2)
                }
                s.card = {
                    number: masterCredit.cardNumber.substring(4),
                    cvv: masterCredit.cvvNumber,
                    expirationDate: {
                        month: m,
                        year: masterCredit.expirationDate.substring(2)
                    },
                    name: masterCredit.customername,
                }
                alreadyHaveMasterCreditCart = true;

                var date = new Date();
                if (date.getFullYear() == s.card.expirationDate.year) {
                    var currentMonth = date.getMonth() + 1;
                    s.month = [];
                    for (var i = currentMonth; i <= 12; i++) {
                        s.month.push((i < 10 ? "0" + i : "" + i))
                    }
                }
            }

            var safePay = resolveParams.paymentPreferences && resolveParams.paymentPreferences.safePay ?
                resolveParams.paymentPreferences.safePay : null;
            if (safePay != null) {
                s.savePay.username = safePay.safepayUsername;
                //s.savePay.password = safePay.safepayPassword;
                alreadyHaveSafePayCart = true;
            }

            s.preferredPayment_MasterCredit = true;
            s.preferredPayment_SafePay = true;
            s.savePayBlocked = safePay == null || masterCredit == null;

            s.imgRadioButton = resolveParams.accountDetails.defaultPaymentMethodId + "" == "20" ? 2
                : safePay != null ? 1
                : masterCredit != null ? 2 : 1;

            s._saveMasterCredit = function () {

                var response;
                if (!alreadyHaveMasterCreditCart) {
                    response = accountService.addMasterCreditMethod(s.card)
                }
                else {
                    response = accountService.updateMasterCreditMethod(s.card)
                }
                response.then(function (response) {

                    if (s.preferredPayment_MasterCredit && response && response.reason && response.success) {
                        accountService.updatePrefferedPaymentMethod(20).then(function (res) {
                            setMessage(res);
                        });
                    }
                    else {
                        setMessage(response);
                    }
                });
            }

            s.saveSafePay = function () {
                var response;
                if (!alreadyHaveSafePayCart) {
                    response = accountService.addSafePayMethod(s.savePay)
                }
                else {
                    response = accountService.updateSafePayMethod(s.savePay)
                }
                response.then(function (response) {

                    if (s.preferredPayment_SafePay && response && response.reason && response.success) {
                        accountService.updatePrefferedPaymentMethod(10).then(function (res) {
                            setMessage(res);
                        });
                    }
                    else {
                        setMessage(response);
                    }
                });
            }

            function setMessage(response) {

                if (response && response.reason) {
                    s.accountDetailsAnswer = {
                        message: response.reason,
                        class: response.success ? 'valid' : 'invalid'
                    }
                    if (response.success) {
                        $location.path('myAccount');
                    }
                    if (!response.success) {
                        $("#safePayDetailErrorLabel").html(response.reason);
                    }
                    else {
                        $timeout(function () {
                            s.accountDetailsAnswer = {message: '', class: 'invalid'}
                        }, 4000)
                    }
                }
            }

            Helper.forAllPage();

        }]);
});






