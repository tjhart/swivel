"use strict";

define(['jQuery', 'utils', 'codemirror', 'jQuery-ui', 'cm-javascript', 'cm-xml', 'cm-matchbrackets', 'cm-closebrackets'],
    function ($, utils, CodeMirror) {
        var CONTENT_TYPES = ['application/json', 'text/javascript', 'application/xml', 'text/html', 'text/plain',
                'text/css', 'application/x-www-form-urlencoded'],
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
                script: 'thenScript',
                thenScript: 'script'},
            CODEMIRROR_OPTS = {
                theme: 'default neat',
                matchBrackets: true,
                autoCloseBrackets: true,
                lineNumbers: true};

        return function () {
            var view = this, $view = $(this);

            function loadStubPart(keyHash, source) {
                $.each(source, function (sourceKey, sourceVal) {
                    var viewKey = keyHash[sourceKey];
                    if (view[viewKey]) {
                        view[viewKey].setValue(sourceVal);
                    } else {
                        $(['#', viewKey].join(''))
                            .val(sourceVal)
                            .trigger('change');
                    }
                });
            }

            function getStubPart(keyHash, target) {
                return function (index, item) {
                    var itemId = item.id, val;
                    if (view[itemId]) {
                        val = view[itemId].getValue();
                    } else {
                        val = $(item).val();
                    }
                    if (val.length) {
                        target[keyHash[itemId]] = val;
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
                        utils.navigate('index.html');
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
                        view.content2.refresh();
                        view.thenScript.refresh();
                    });
                $('#method').menu();
                $('#contentType,#contentType2').
                    autocomplete({source: CONTENT_TYPES})
                    .on('change', function (e) {
                        var viewName = e.target.id.match('(.*)Type(.*)').slice(1).join(''),
                            mimeType = $(e.target).val();

                        view[viewName].setOption('mode', mimeType);
                    });
                $('#statusCode').autocomplete({source: STATUS_CODES});
                $('.content').removeClass('ui-helper-hidden');
                //The script editors need to be initialized *after* the content is made visible. Otherwise,
                //they don't render properly
                this.content = CodeMirror(document.getElementById('content'),
                    $.extend({smartIndent: false}, CODEMIRROR_OPTS));
                this.content2 = CodeMirror(document.getElementById('content2'),
                    $.extend({smartIndent: false}, CODEMIRROR_OPTS));
                this.whenScript = CodeMirror(document.getElementById('whenScript'),
                    $.extend({mode: 'javascript'}, CODEMIRROR_OPTS));
                this.thenScript = CodeMirror(document.getElementById('thenScript'),
                    $.extend({mode: 'javascript'}, CODEMIRROR_OPTS));
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
                    .find('input, select, div.editor')
                    .each(getStubPart(WHEN_HASH, stubData.when));

                $('#then')
                    .find(['.', $('[name="thenType"]:checked').val()].join(''))
                    .find('input, div.editor')
                    .each(getStubPart(THEN_HASH, stubData.then));

                if (stubData.then.statusCode) {
                    stubData.then.statusCode = parseInt(stubData.then.statusCode);
                }
                $view.trigger(event, stubData);
            };

            this.configure();
        };
    });