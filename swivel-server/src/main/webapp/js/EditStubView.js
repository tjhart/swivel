"use strict";

define(['jQuery', 'jQuery-ui'], function ($) {
    var CONTENT_TYPES = ['application/json', 'application/xml', 'application/pdf', 'text/html', 'application/zip',
            'text/plain', 'text/css', 'application/x-www-form-urlencoded'],
        STATUS_CODES = ["100", "101",
            "200", "201", "202", "203", "204", "205", "206",
            "300", "301", "302", "303", "304", "305", "307",
            "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414",
            "415", "416", "417",
            "500", "501", "502", "503", "504", "505"],
        ANTI_TYPE = {static: 'script', script: 'static'},
        WHEN_HASH = {method: 'method',
            query: 'query',
            contentType: 'contentType',
            remoteAddress: 'remoteAddress',
            content: 'content',
            script: 'whenScript',
            whenScript: 'script'},
        THEN_HASH = {statusCode: 'statusCode',
            reason: 'reason',
            contentType: 'contentType2',
            contentType2: 'contentType',
            content: 'content2',
            content2: 'content',
            script: 'thenScript'};

    return function () {
        var view = this, $view = $(this);

        function loadStubPart(keyHash, source) {
            $.each(source, function (sourceKey, sourceVal) {
                $(['#', keyHash[sourceKey]].join('')).val(sourceVal);
            });
        }

        function getStubPart(keyHash, target) {
            return function (index, item) {
                var val = $(item).val();
                if (val.length) {
                    target[keyHash[item.id]] = val;
                }
            }
        }

        this.setStub = function (path, stub) {
            this.id = stub.id;
            $('#path')
                .val(path)
                .prop('readonly', true)
                .addClass('ui-state-disabled');
            $('#description').val(stub.description);
            if (stub.then.script) {
                $('#scriptThen').click();
            }
            loadStubPart(WHEN_HASH, stub.when);
            loadStubPart(THEN_HASH, stub.then);
        };

        this.configure = function () {
            $('#cancel')
                .button()
                .click(function () {
                    window.location = 'index.html';
                });
            $('#submit')
                .button()
                .click(function () {
                    view.editStub();
                });
            $('.type')
                .buttonset()
                .find('[type="radio"]').click(function (e) {
                    var $target = $(e.target), visibleClass;
                    visibleClass = $target.val();
                    $(['#then .', visibleClass].join('')).removeClass('ui-helper-hidden');
                    $(['#then .', ANTI_TYPE[visibleClass]].join('')).addClass('ui-helper-hidden');
                });
            $('#method').menu();
            $('#contentType,#contentType2').autocomplete({source: CONTENT_TYPES});
            $('#statusCode').autocomplete({source: STATUS_CODES});
            $('.content').removeClass('ui-helper-hidden');
        };

        this.editStub = function () {
            var stubData = {
                description: $('#description').val(),
                path: $('#path').val(),
                when: { },
                then: { } }, event = 'add-stub.swivelView';

            if (this.id) {
                stubData.id = this.id;
                event = 'edit-stub.swivelView';
            }
            $('#when')
                .find('input, select, textarea')
                .each(getStubPart(WHEN_HASH, stubData.when));

            $('#then')
                .find(['.', $('[name="thenType"]').val()].join(''))
                .find('input, textarea')
                .each(getStubPart(THEN_HASH, stubData.then));

            stubData.then.statusCode = parseInt(stubData.then.statusCode);
            $view.trigger(event, stubData);
        };

        this.configure();
    };
});