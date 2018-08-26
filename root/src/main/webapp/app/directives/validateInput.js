/**
 * Created by correnti on 19/05/2016.
 */

define(['./module'], function (directives) {
    'use strict';
    directives.directive('secForm', ["$timeout", function ($timeout) {

        var invalid = "invalid";
        return {
            restrict: 'E',
            require: 'secForm',
            scope: {
                secGetFormValidationWhenReady: "&",
                secIsValidForm: "&"
            },
            controllerAs: "secFormCtrl",
            controller: [function () {

                var ctrl;
                var toLateToCheck = false;
                this.getFormValidationWhenReady;
                this.models = [];
                this.senders = [];
                this.setButtonCtrl = function (ctrl) {
                    this.senders.push(ctrl);
                };

                this.updateState = function (id, valid, owner) {
                    var validForm = true;
                    for (var i = 0; i < this.models.length; i++) {
                        var model = this.models[i];
                        if (model.id == id) {
                            model.valid = valid;
                            model.owner = owner;
                        }
                        validForm = !validForm ? false : model.valid;
                    }
                    for (var i = 0; i < this.senders.length; i++) {
                        this.senders[i].formIsValid(validForm);
                    }
                    if (this.isValidForm) {
                        this.isValidForm({valid: validForm});
                    }
                    if (!toLateToCheck && this.getFormValidationWhenReady) {
                        this.getFormValidationWhenReady({valid: validForm})
                    }
                };

                this.formIsValid = function () {
                    for (var i = 0; i < this.models.length; i++) {
                        var model = this.models[i];
                        if (model.valid == false) {
                            return false;
                        }
                    }
                    return true;
                };

                this.notifyWatcher = function (id, valid) {
                    this.models.push({
                        id: id,
                        valid: valid
                    });
                };

                this.setFormValidationWhenReady = function (_getFormValidationWhenReady) {
                    if (_getFormValidationWhenReady) {
                        this.getFormValidationWhenReady = _getFormValidationWhenReady
                    }
                };

                this.setCtrl = function (_ctrl) {
                    ctrl = _ctrl;
                };

                this.isValidForm;
                this.setIsValidForm = function (_isValidForm) {
                    this.isValidForm = _isValidForm;
                };

                this.setToLateToCheck = function () {
                    toLateToCheck = true;
                };

                this.setDisableUntilSomeChange = function () {
                    for (var i = 0; i < this.senders.length; i++) {
                        this.senders[i].setDisableUntilSomeChange();
                    }
                };

                this.changeAllowToChangeInvalidClass = function () {
                    for (var i = 0; i < this.senders.length; i++) {
                        this.senders[i].changeAllowToChangeInvalidClass();
                    }
                };


            }],
            link: {
                pre: function (s, e, a, ctrl) {
                    e.addClass("secForm");
                    ctrl.setCtrl(ctrl);
                    if (s.secIsValidForm) {
                        ctrl.setIsValidForm(s.secIsValidForm);
                    }
                },
                post: function (s, e, a, ctrl) {
                    ctrl.updateState(-1, null, null);
                    if (s.secGetFormValidationWhenReady) {
                        ctrl.setFormValidationWhenReady(s.secGetFormValidationWhenReady);
                    }
                    if (a.secDisableUntilSomeChange == 'true') {
                        ctrl.setDisableUntilSomeChange();
                    }
                }
            }
        }

    }]).
    directive('secSender', ["$compile", function ($compile) {

        var invalid = "invalid";

        return {
            restrict: 'E',
            require: ['secSender', '^secForm'],
            scope: {
                secSend: '&'
            },
            controllerAs: "senderCtrl",
            controller: ["$scope", function (s) {

                var button;
                var allowToChangeInvalidClass = true;
                this.setButton = function (_button) {
                    button = _button;
                };

                this.send = function () {
                    if (button.hasClass(invalid)) {
                    }
                    else {
                        s.secSend();
                    }
                };

                this.formIsValid = function (valid) {
                    if (allowToChangeInvalidClass) {
                        if (valid) {
                            button.removeClass(invalid);
                            button.removeAttr('disabled');
                        }
                        else {
                            button.addClass(invalid);
                            button.prop("disabled", true);
                        }
                    }
                };

                this.changeAllowToChangeInvalidClass = function () {
                    allowToChangeInvalidClass = true;
                };

                this.setDisableUntilSomeChange = function () {
                    allowToChangeInvalidClass = false;
                    button.addClass(invalid);
                    button.prop("disabled", true);
                };

            }],
            link: {
                pre: function (s, e, a, ctrls) {
                    e.addClass("sec-sender");
                    var description = a.aHint;
                    var button = $("<button id='" + a.aValue.toLowerCase().replace(/ /g,"_") + "_btn" + description + "' type='button' class='sec-sender-a invalid' role='button' data-ng-click='senderCtrl.send()'>" + a.aValue + "</button>");
                    $compile(button)(s);
                    e.append(button);

                    ctrls[0].setButton(button);
                    ctrls[1].setButtonCtrl(ctrls[0]);
                },
                post: function (s, e, a, ctrl) {
                }
            }
        }

    }]).
    directive('secView', ["$compile", "$timeout", function ($compile, $timeout) {

        function throwInvalidObjectFormat(obj, e) {

            console.log(e);
            console.log(JSON.stringify(e));
            throw  "-----------  Invalid object format ! \n\n" +
            "The element must be like this:\n\n" +
            "var obj = {\n" +
            "   error: 'the upper text message', \n" +
            "   info: 'the button information about this requirement', \n" +
            "   min: 'add this param if you use secMinLength directive', \n" +
            "   max: 'add this param if you use secMaxLength directive', \n" +
            "   regex: 'add this param if you use secPattern directive', \n" +
            "   model: 'add this param if you use secCompareTo directive, model have to pass secModelCompareTo model to compare', \n" +
            "}\n" +
            "\nYou object is: " + JSON.stringify(obj) + "\n\n\n\n";

            //get-form-validation-when-ready
            //a-hint: pass this text has a text ,

            //"   a-type: 'add this param if you wont to display a different control:', \n" +
            //"         select: 'to show a select design (required directives to this type: sec-select-options, a-show )', \n" +

            //sec-select-options
            //a-show
            //a-star

            //secModel: '=',
            //secRequire: '=',
            //secMinLength: '=',
            //secMaxLength: '=',
            //secPattern: '=',
            //
            //secCompareTo: '=',
            //secModelCompareTo: '=',
            //
            //sec-send: '&',
        }

        var Keys = {
            secRequired: 0,
            secMinLength: 1,
            secMaxLength: 2,
            secPattern: 3,
            secCompareTo: 4
        };

        var Types = {
            text: "text",
            textarea: "textarea",
            select: "select",
            password: "password",
            creditCard: "creditCard",
            checkbox: "checkbox"
        };

        var invalid = "invalid";
        var animated = "animated";
        var in_focus = "in-focus";
        var select_value = "select-value";

        return {
            restrict: 'E',
            require: ['secView', '^secForm'],
            scope: {
                secModel: '=',
                secRequire: '=',
                secMinLength: '=',
                secMaxLength: '=',
                secPattern: '=',
                secSelectOptions: '=',
                secDisableValidation: '=',
                secCardNumber: "=",

                secCompareTo: '=',
                secModelCompareTo: '=',

                secIsValid: '&',
                secSelectChange: '&'
            },
            controller: ["$scope", function (s) {

                var id;
                var input;
                var label;
                var isFieldValid;
                var hint;
                var ul;
                var form;
                var doNotShowInfo = false;
                var disableValidation = false;
                var isCardNumber = false;
                var ctrl = this;

                ctrl.compareTo;
                s.validations = [];
                $('#creditCard').on('keyup', function(e){
                    var val = $(this).val();
                    var newval = '';
                    val = val.replace(/\s/g, '');
                    for(var i=0; i < val.length; i++) {
                        if(i%4 == 0 && i > 0) newval = newval.concat(' ');
                        newval = newval.concat(val[i]);
                    }
                    $(this).val(newval);
                });

                ctrl.getListValidations = function () {
                    var li = "";
                    for (var i = 0; i < s.validations.length; i++) {
                        var validation = s.validations[i];
                        if (validation.info && validation.info != "") {
                            li += "<li><a>" + validation.info + " </a></li>"
                        }
                        else {
                            li += "<li style='display: block; position: absolute; height: 0'><a></a></li>"
                        }
                    }
                    return li;
                };


                ctrl.setCompareTo = function (_compareTo) {
                    ctrl.compareTo = _compareTo;
                };

                var secSelectChange;
                ctrl.setSecSelectChange = function (_secSelectChange) {
                    secSelectChange = _secSelectChange;
                };

                var secretField;
                ctrl.setSecretField = function () {
                    secretField = true;
                    if(s.secModel.length == 12) {
                        input.parent().addClass('card-number-four-star');
                        input.val("********" + s.secModel.substring(8));
                    }
                    if(s.secModel.length == 3) {
                        input.val("***");
                    }
                    //input.attr("type", "password");
                };

                ctrl.removeSecretField = function () {
                    if(secretField){
                        input.parent().removeClass('card-number-four-star');
                        secretField = false;
                        //input.attr("type", "text");
                        s.secModel = "";
                    }
                };


                ctrl.setDoNotShowInfo = function (_doNotShowInfo) {
                    doNotShowInfo = _doNotShowInfo;
                };

                ctrl.setCardNumberFourDigits = function (_CardNumberFourDigits) {
                    isCardNumber = _CardNumberFourDigits;
                };


                ctrl.setDisableValidation = function (_disableValidation) {
                    disableValidation = _disableValidation == undefined ? false : _disableValidation;
                    ctrl.change(input.val());
                };


                ctrl.pushValidation = function (validation, key) {

                    if (validation.error) {
                        validation.key = key;
                        s.validations.push(validation);
                    }
                    else if (validation.regexes) {
                        for (var regexIndex = 0; regexIndex < validation.regexes.length; regexIndex++) {
                            var regexformat = validation.regexes[regexIndex];
                            regexformat.key = key;
                            s.validations.push(regexformat);
                        }
                    }
                };

                var firstLoader = true;
                ctrl.modelCompareToChange = function (val) {
                    if (!firstLoader) {
                        if ((val+"").trim() == "") {
                            return;
                        }
                        ctrl.compareTo.val(val);
                        ctrl.change(input.val());
                        ctrl.blur(input.val());
                    }
                    firstLoader = false;
                };

                var selectedList;
                ctrl.fillSelect = function (arr, name) {
                    if (arr) {
                        var found = false;
                        selectedList = arr;
                        var selectList = ctrl.getSelectlist();
                        selectList.empty();
                        for (var i = 0; i < arr.length; i++) {
                            var item = selectedList[i];
                            var span;
                            if (name) {
                                span = $("<span data-ng-click='selectItemChangeModel(" + i + ")'" +
                                    " data-ng-mouseenter='selectItemMouseIn()' data-ng-mouseleave='selectItemMouseOut()'>"
                                    + item[name] + "</span>");
                            }
                            else {
                                span = $("<span data-ng-click='selectItemChangeModel(" + i + ")'" +
                                    " data-ng-mouseenter='selectItemMouseIn()' data-ng-mouseout='selectItemMouseOut()'>"
                                    + item + "</span>");
                            }
                            if (s.secModel) {
                                if (s.secModel + "" == item + "") {
                                    found = true;
                                }
                            }
                            $compile(span)(s);
                            selectList.append(span);
                        }
                        if (!found && s.secModel) {
                            s.secModel = arr[0];
                        }
                    }
                };

                s.selectItemChangeModel = function (index) {

                    var validation = selectedList[index];
                    if (!label.hasClass(animated)) {
                        label.addClass(animated);
                        $timeout(function () {
                            s.secModel = validation;
                        }, 200)
                    }
                    else {
                        if (s.secModel == validation) {
                            ctrl.getSelectlist().fadeOut();
                        }
                        else {
                            s.secModel = validation;
                        }
                    }
                    if (secSelectChange) {
                        secSelectChange({value: validation});
                    }
                };

                s.selectItemMouseIn = function () {
                    if (_____selectItemMouseOut) {
                        $timeout.cancel(_____selectItemMouseOut);
                    }
                };

                var _____selectItemMouseOut;
                s.selectItemMouseOut = function () {
                    var selectList = ctrl.getSelectlist();
                    if (selectList.css("display") != "none") {
                        _____selectItemMouseOut = $timeout(function () {
                            ctrl.getSelectlist().fadeOut();
                        }, 1200);
                    }
                };

                ctrl.change = function (val) {

                    if (val == undefined || s.secModel == undefined) {
                        return;
                    }
                    if (typeof val == 'string' && typeof s.secModel == 'object') {
                        return;
                    }
                    if (s.secModel != undefined) {
                        val = s.secModel;
                    }

                    var valid;
                    try {
                        if (disableValidation) {
                            valid = true;
                            return;
                        }

                        if (isCheckboxDesign()) {
                            valid = checkCheckboxValidations();
                            updateTextCheckboxValidations(valid);
                            return;
                        }
                        else if (isSelectedDesign()) {
                            valid = checkSelectValidations(val);
                            if (valid) {
                                if (!$(label).hasClass(animated)) {
                                    $(label).addClass(animated);
                                }
                            }
                            else {
                                valid = s.validations.length == 0;
                            }
                            if (secSelectChange) {
                                secSelectChange({value : val})
                            }

                            return;
                        }
                        else if ((val+"").trim() == "") {
                            if (s.validations.length > 0) {
                                if ((s.secModel+"").trim() == '' && input.hasClass(in_focus) && !doNotShowInfo) {
                                    ul.find('li').slideDown()
                                }
                            }
                            if (!input.hasClass(in_focus) && label.hasClass(animated) && s.secModel == "") {
                                label.removeClass(animated);
                                if (isCardNumber) {
                                    removeCardNumberFourDigits()
                                }
                            }
                            valid = getValidation(false);
                        }
                        else {
                            if (!$(label).hasClass(animated)) {
                                $(label).addClass(animated);
                                if (isCardNumber) {
                                    addCardNumberFourDigits()
                                }
                            }
                            valid = checkViewValidations();
                        }

                    } finally {

                        form.updateState(id, valid, hint);
                    }
                };

                function checkSelectValidations(val) {

                    if (typeof val == "object") {
                        for (var _property in val) {
                            if (val.hasOwnProperty(_property)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return val != "" && val != null && val != undefined;
                }

                ctrl.blur = function (val) {
                    if (val == undefined || (val+"").trim() == "undefined") {
                        return;
                    }
                    if ((val+"").trim() == "") {
                        label.removeClass(animated);
                        if (isCardNumber) {
                            removeCardNumberFourDigits()
                        }
                    }
                    var valid = getValidation(true);
                    if (isFieldValid) {
                        isFieldValid({valid: valid});
                    }
                    ul.find('li').slideUp();
                    input.removeClass(in_focus);
                };


                ctrl.focus = function () {
                    label.addClass(animated);
                    input.addClass(in_focus);
                    if (isCardNumber) {
                        addCardNumberFourDigits()
                    }
                    ctrl.change(s.secModel);
                    setNormalHint();
                };

                function addCardNumberFourDigits() {
                    input.parent().addClass('card-number-four-digits');
                    input.addClass('card-number-four-digits');
                }

                function removeCardNumberFourDigits() {
                    input.parent().removeClass('card-number-four-digits');
                    input.removeClass('card-number-four-digits');
                }

                ctrl.getSelectlist = function () {
                    return input.find(".selectList");
                };


                s.labelClicked = function () {
                    form.setToLateToCheck();

                    if (isSelectedDesign()) {
                        var selectedList = ctrl.getSelectlist();
                        if (selectedList.children().length > 0) {
                            selectedList.fadeToggle();
                        }
                        return;
                    }
                    if (isCheckboxDesign()) {
                        s.secModel = !s.secModel;
                        return;
                    }
                    if (!label.hasClass(animated)) {
                        input.focus();
                        ctrl.focus();
                    }
                };


                s.labelOut = function () {
                    _____selectItemMouseOut = $timeout(function () {
                        var list = ctrl.getSelectlist();
                        if (list.css("display") != "none") {
                            list.fadeOut();
                        }
                    }, 3000);
                };


                ctrl.setItems = function (_input, _label, _ul, _form, _id) {

                    id = _id;
                    input = _input;
                    label = _label;
                    ul = _ul;
                    hint = label.text();
                    form = _form;

                    var valid;
                    if (disableValidation) {
                        valid = true;
                    }
                    else {
                        if (isCheckboxDesign()) {
                            valid = checkCheckboxValidations();
                        }
                        else if (isSelectedDesign()) {
                            valid = s.validations.length == 0 ? true : checkSelectValidations();
                        }
                        else {
                            valid = checkViewValidations();
                        }
                    }
                    form.notifyWatcher(id, valid);


                    input.on({
                        blur: function () {
                            if (!isSelectedDesign()) {
                                ctrl.blur(s.secModel);
                            }
                        },
                        focus: function () {
                            if (isSelectedDesign()) {
                                form.changeAllowToChangeInvalidClass();
                            }
                            form.setToLateToCheck();
                            ctrl.focus();
                        },
                        keydown: function () {
                            form.changeAllowToChangeInvalidClass();
                        }
                    });
                };


                function updateTextCheckboxValidations(valid) {
                    if (s.validations[0].dontChange) {
                        return;
                    }
                    if (valid) {
                        label.text(s.validations[0].info);
                        label.removeClass(invalid);
                    }
                    else {
                        label.text(s.validations[0].error);
                        label.addClass(invalid);
                    }
                }

                function checkCheckboxValidations() {
                    return s.secModel == true;
                }

                function getValidation(changeHint) {
                    var validation = null;
                    try {
                        for (var i = 0; i < s.validations.length; i++) {
                            validation = s.validations[i];
                            switch (validation.key) {
                                case Keys.secRequired:
                                    if ((s.secModel+"").trim() == '') {
                                        if (changeHint) {
                                            return setInvalidTextToShow(validation.error);
                                        }
                                        return false;
                                    }
                                    break;
                                case Keys.secMinLength:
                                    if (s.secModel.length < validation.min && (s.secModel + "").length != 0) {
                                        if (changeHint) {
                                            return setInvalidTextToShow(validation.error);
                                        }
                                        return false;
                                    }
                                    break;
                                case Keys.secMaxLength:
                                    if (s.secModel.length > validation.max) {
                                        if (changeHint) {
                                            return setInvalidTextToShow(validation.error);
                                        }
                                        return false;
                                    }
                                    break;
                                case Keys.secPattern:
                                    if (!(new RegExp(validation.regex).test(s.secModel)) && (input.val()).length != 0) {
                                        if (changeHint) {
                                            return setInvalidTextToShow(validation.error);
                                        }
                                        return false;
                                    }
                                    break;
                                case Keys.secCompareTo:
                                    if (ctrl.compareTo.val() != s.secModel && ctrl.compareTo.val() != "") { // && input.val() != ""
                                        return setInvalidTextToShow(validation.error);
                                    }
                                    break;
                                case Keys.secCardNumber:
                                    //if (!(s.secModel.length == validation.exactly - 4 && (input.val() - 0) == input.val() &&
                                    //input.val().indexOf('-') == -1 &&  input.val().indexOf('+') == -1)) {
                                    var trimmedSecModel = s.secModel.replace(/\s/g,'');
                                    if (!(new RegExp(validation.regex).test(trimmedSecModel) && trimmedSecModel.length == validation.exactly - 4)) {
                                        if (changeHint) {
                                            return setInvalidTextToShow(validation.error);
                                        }
                                        return false;
                                    }
                                    break;
                            }
                        }
                        setNormalHint();
                        return true;
                    } catch (e) {
                        throwInvalidObjectFormat(validation);
                    }
                }

                function checkViewValidations() {
                    var validation = null;
                    var validInput = true;
                    if (s.secModel) {

                        try {
                            for (var i = 0; i < s.validations.length; i++) {
                                validation = s.validations[i];
                                switch (validation.key) {
                                    case Keys.secRequired:
                                        if ((s.secModel+"").trim() == '') {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i)
                                        }
                                        break;
                                    case Keys.secMinLength:
                                        if (s.secModel.length < validation.min && (s.secModel + "").length != 0) {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i);
                                        }
                                        break;
                                    case Keys.secMaxLength:
                                        if (s.secModel.length > validation.max) {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i)
                                        }
                                        break;
                                    case Keys.secPattern:
                                        if (!(new RegExp(validation.regex).test(s.secModel)) && (input.val()).length != 0) {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i);
                                        }
                                        break;
                                    case Keys.secCompareTo:
                                        if (ctrl.compareTo.val() != s.secModel && ctrl.compareTo.val() != "") {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i);
                                        }
                                        break;
                                    case Keys.secCardNumber:
                                        var trimmedSecModel = s.secModel.replace(/\s/g,'');
                                        if (!(new RegExp(validation.regex).test(trimmedSecModel) && trimmedSecModel.length == validation.exactly - 4)) {
                                            showValidation(i);
                                            validInput = false;
                                        }
                                        else {
                                            hideValidation(i);
                                        }
                                        break;
                                }
                            }


                        } catch (e) {
                            throwInvalidObjectFormat(validation, e);
                        }
                    }
                    return validInput;

                }

                function hideValidation(index) {
                    ul.find("li:nth-child(" + (index + 1) + ")").slideUp();
                }

                function showValidation(index) {
                    if (input.hasClass(in_focus) && !doNotShowInfo) {
                        ul.find("li:nth-child(" + (index + 1) + ")").slideDown();
                    }
                }

                function setNormalHint() {
                    input.removeClass(invalid);
                    label.removeClass(invalid);
                    label.text(hint)
                }

                function setInvalidTextToShow(error) {
                    label.text(error);
                    label.addClass(invalid);
                    input.addClass(invalid);
                    return false;
                }

                function isSelectedDesign() {
                    return input.prop("tagName").toLowerCase() == "select"
                }

                function isCheckboxDesign() {
                    return input.attr("type") == Types.checkbox;
                }

            }],

            link: {

                pre: function (s, e, a, ctrls) {

                    var ctrl = ctrls[0];
                    var inputTypeName = "";
                    e.addClass("sec-view");

                    s.$watch('secModel', function (n, o) {
                        ctrl.removeSecretField();
                        ctrl.change(n);
                    }, true);

                    if (s.secRequire) {
                        if (a.aDontChange == "true") {
                            var temp = JSON.parse(s.secRequire);
                            temp.dontChange = true;
                            ctrl.pushValidation(temp, Keys.secRequired);
                        }
                        else {
                            ctrl.pushValidation(JSON.parse(s.secRequire), Keys.secRequired);
                        }
                    }
                    if (s.secMinLength) {
                        ctrl.pushValidation(JSON.parse(s.secMinLength), Keys.secMinLength);
                    }
                    if (s.secMaxLength) {
                        ctrl.pushValidation(JSON.parse(s.secMaxLength), Keys.secMaxLength);
                    }
                    if (s.secPattern) {
                        ctrl.pushValidation(JSON.parse(s.secPattern), Keys.secPattern);
                    }
                    if (a.aDoNotShowInfo == 'true') {
                        ctrl.setDoNotShowInfo(true);
                    }
                    if (s.secCardNumber) {
                        ctrl.setCardNumberFourDigits(true);
                        ctrl.pushValidation(JSON.parse(s.secCardNumber), Keys.secCardNumber);
                    }
                    if (s.secSelectChange) {
                        ctrl.setSecSelectChange(s.secSelectChange)
                    }
                    if(a.secName){
                        inputTypeName = a.secName;
                    }


                    s.$watch('secDisableValidation ', function (n, o) {
                        ctrl.setDisableValidation(n);
                    }, true);

                    var div = $("<div class='inputContainer'></div>");
                    var type = a.aType || "text";
                    var input;
                    var hideLabel = "";
                    if (!a.aHint) {
                        hideLabel = "style='display:none'";
                    }
                    var partOfName = "";
                    if (a.aHintDescription != null) {
                        partOfName = a.aHintDescription;
                    }
                    var label = $("<label " + hideLabel + " data-ng-click='labelClicked()' data-ng-mouseout='labelOut()'>" + a.aHint + "</label>");
                    switch (type) {
                        case Types.select:

                            label.css("z-index", "-10");
                            label.addClass("animated");
                            var dot = (a.aShow ? "." + a.aShow : "");
                            var format = "<select name='" + a.aHint.toLowerCase() + 'Listbox' + partOfName + "' role='listbox' ng-model='secModel' ng-options='c as c" + dot
                                + " for c in secSelectOptions' ><option value='' selected='selected'></select>";

                            //var dot = (a.aShow ? a.aShow : null);
                            //var format = "<select ng-model='secModel'>" +
                            //"<option ng-repeat='c in secSelectOptions' >{{ (dot ? c[dot] : c ) }}</option>" +
                            //    "</select>";

                            input = $(format);
                            input.css("background", "transparent");

                            break;
                        case Types.textarea:
                            input = $("<textarea name='" + a.aHint + partOfName + "' data-ng-model='secModel' ></textarea>");
                            // On main Page in Contact US section under subject visible hint subjectTextArea
                            // label.text(JSON.parse(s.secRequire).info);
                            label.text("");
                            label.addClass("checkboxText roboto-light" + animated);
                            div.css({
                                "padding-top": "10px",
                                "height": "auto",
                                "margin": "0px 0"
                            });
                            break;
                        case Types.creditCard:
                            input = $("<input id='creditCard' name='" + a.aHint.replace(/\s/g, "_").toLowerCase() + partOfName + "' data-ng-model='secModel' />");
                            break;
                        default:
                            if (a.aHint == null) {
                                a.aHint = "i_agree";
                            }
                            input = $("<input name='" + a.aHint.replace(/\s/g, "_").toLowerCase() + partOfName + "' type='" + type + "' data-ng-model='secModel' />");
                            if (type == Types.checkbox) {
                                label.text(JSON.parse(s.secRequire).info);
                                label.css({"display": "block"});
                                label.addClass("checkboxText roboto-light " + animated);
                                div.css({
                                    "padding-top": "1px",
                                    "height": "auto",
                                    "margin": "8px 0"
                                });
                            }
                            break;
                    }

                    if (s.secRequire) {
                        if (type != Types.checkbox && a.aStar != "false") {
                            var star = $("<span class='star'>*</span>");
                            div.append(star);
                        }
                    }
                    $compile(input)(s);

                    div.append(input);
                    div.append(label);

                    $compile(div)(s);
                    e.append(div);

                    if (s.secCompareTo) {
                        s.$watch('secModelCompareTo', function (n, o) {
                            if (input.val() != "") {
                                ctrl.modelCompareToChange(n);
                            }
                        }, true);
                        ctrl.pushValidation(JSON.parse(s.secCompareTo), Keys.secCompareTo);
                        var compareTo = $("<input style='display: none' type='" + type + "' data-ng-model='secModelCompareTo' />");
                        $compile(compareTo)(s);
                        ctrl.setCompareTo(compareTo);
                        e.append(compareTo);
                    }

                    var ul = $("<ul>" + ctrl.getListValidations() + "</ul>");
                    e.append(ul);

                    var _form = ctrls[1];
                    ctrl.setItems(input, label, ul, _form, s.$id);

                },

                post: function (s, e, a, ctrls) {

                    if (a.aSecretField == "true") {
                        $timeout(function () {
                            ctrls[0].setSecretField()
                        }, 1000);
                    }
                }
            }
        }
    }
    ]);

});
