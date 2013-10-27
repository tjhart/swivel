"use strict";

define(['jQuery', 'utils', 'codemirror', 'json2', 'jQuery-ui', 'cm-javascript', 'cm-xml', 'cm-matchbrackets',
    'cm-closebrackets', 'lib/serializeJSON'], function ($, utils, CodeMirror, json2) {
    var CONTENT_TYPES = ['application/json', 'text/javascript', 'application/xml', 'text/html', 'text/plain',
            'text/css', 'application/x-www-form-urlencoded'],
        STATUS_CODES = ["100", "101",
            "200", "201", "202", "203", "204", "205", "206",
            "300", "301", "302", "303", "304", "305", "307",
            "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414",
            "415", "416", "417",
            "500", "501", "502", "503", "504", "505"],
        CODEMIRROR_OPTS = {
            theme: 'default neat',
            matchBrackets: true,
            autoCloseBrackets: true,
            lineNumbers: true};

    return function () {
        var view = this, $view = $(this), $path = $('#path');

        this.setStub = function (path, stub) {
            function loadStubPart(form, source) {
                $.each(source, function (key, val) {
                    $(form[key]).val(val).change();
                });
            }

            var when = stub.when, then = stub.then;
            $path.val(path)
                .prop('readonly', true)
                .addClass('ui-state-disabled');
            if (stub.then.script) {
                $('#scriptThen').click();
            }
            else if (stub.then.fileName) {
                $('#fileThen').click();
                $('#currentFileName').html(stub.then.fileName);
                $('.file ul').removeClass('ui-helper-hidden');
                $('.file').not('.static').find(' input').addClass('ui-helper-hidden');
            }
            loadStubPart(document.stubDescription, stub);
            loadStubPart(document.when, when);
            loadStubPart(document.then, then);
            view.content.setValue(when.content || '');
            view.whenScript.setValue(when.script || '');
            view.content2.setValue(then.content || '');
            view.thenScript.setValue(then.script || '');
        };

        this.configure = function () {

            var editorElement = {
                content: document.getElementById('content'),
                content2: document.getElementById('content2'),
                whenScript: document.getElementById('whenScript'),
                thenScript: document.getElementById('thenScript')
            };
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
                .find('[type="radio"]').click(function (event) {
                    var visibleClass = $(event.target).val();
                    $('.static,.script,.file').addClass('ui-helper-hidden');
                    $('#then').find([ '.', visibleClass].join('')).removeClass('ui-helper-hidden');
                    if (visibleClass === 'static') {
                        view.content2.refresh();
                    }
                    else if (visibleClass === 'script') {
                        view.thenScript.refresh();
                    }
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
            $('.file button')
                .button({icons: {primary: 'ui-icon-trash'}, text: false})
                .click(function () {
                    $('.file ul').addClass('ui-helper-hidden');
                    $('.file')
                        .not('.static')
                        .find('input')
                        .removeClass('ui-helper-hidden');
                });
            $('.content').removeClass('ui-helper-hidden');
            //The script editors need to be initialized *after* the content is made visible. Otherwise,
            //they don't render properly
            this.content = CodeMirror(editorElement.content, $.extend({smartIndent: false}, CODEMIRROR_OPTS));
            this.content2 = CodeMirror(editorElement.content2, $.extend({smartIndent: false}, CODEMIRROR_OPTS));
            this.whenScript = CodeMirror(editorElement.whenScript, $.extend({mode: 'javascript'}, CODEMIRROR_OPTS));
            this.thenScript = CodeMirror(editorElement.thenScript, $.extend({mode: 'javascript'}, CODEMIRROR_OPTS));

            //put the editors in the DOM so the casper test scripts can find them
            $.each(editorElement, function (key, val) {
                $(val).data('editor', view[key]);
            });
        };

        this.editStub = function () {
            function trimToUndefined(val) {
                return val && val.length ? val : undefined;
            }

            var stubData = $(document.stubDescription).serializeJSON(), event = 'add-stub.swivelView', when, then,
                formData;

            when = $(document.when).serializeJSON();
            when.content = trimToUndefined(view.content.getValue());
            when.script = trimToUndefined(view.whenScript.getValue());

            stubData.when = when;

            then = $(document.then).serializeJSON();
            if (then.thenType === 'script') {
                then = {script: view.thenScript.getValue()};
            } else {
                then.statusCode = parseInt(then.statusCode);
                if (then.thenType === 'file') {
                    formData = new FormData();
                    formData.append('contentFile', document.then.contentFile.files[0]);
                } else {
                    then.content = trimToUndefined(view.content2.getValue());
                }
                delete then.thenType;
            }

            stubData.then = then;
            if (stubData.id) {
                event = 'edit-stub.swivelView';
            }

            //cleanup
            $.each([when, then], function (i, item) {
                $.each(item, function (key, val) {
                    if (typeof val === 'undefined') {
                        delete item[key];
                    }
                });
            });

            if (formData) {
                formData.append('stubDescription', json2.stringify(stubData));
                stubData = {id: stubData.id, path: stubData.path, formData: formData};
            }
            $view.trigger(event, stubData);
        };

        this.configure();
    };
});