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
            script: 'whenScript'},
        THEN_HASH = {statusCode: 'statusCode',
            reason: 'reason',
            contentType: 'contentType2',
            content: 'content2',
            script: 'thenScript'};

    return function () {
        var $view = $(this);

        function loadStubPart(keyHash, source) {
            $.each(keyHash, function (sourceKey, targetId) {
                $(['#', targetId].join('')).val(source[sourceKey]);
            });
        }

        this.setStub = function (stub, path) {
            $('#path').html(path);
            $('#description').html(stub.description);
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
            $('#submit').button();
            $('.type')
                .buttonset()
                .find('[type="radio"]').click(function (e) {
                    var $target = $(e.target), divId, visibleClass;
                    divId = '#then ';
                    visibleClass = $target.val();
                    $(['#then .', visibleClass].join('')).removeClass('ui-helper-hidden');
                    $(['#then .', ANTI_TYPE[visibleClass]].join('')).addClass('ui-helper-hidden');
                });
            $('#method').menu();
            $('#contentType,#contentType2').autocomplete({source: CONTENT_TYPES});
            $('#statusCode').autocomplete({source: STATUS_CODES});
        };

        this.configure();
    }
});