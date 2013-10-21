(function () {
    const
        EXPECTED_FORM = {
            description: 'Edit Stub Test Description',
            method: 'PUT',
            query: '',
            remoteAddress: '',
            requestContentType: 'application/xml',
            thenType: 'script',
            statusCode: '',
            responseContentType: ''
        },
        WHEN_CONTENT =
            '<?xml version="1.0" encoding="UTF-8"?>\n' +
                '<WebRequest></WebRequest>',
        THEN_SCRIPT =
            '(function(){\n' +
                '    var content = \'<?xml version="1.0" encoding="UTF-8"?>\\n\' +\n' +
                '        \'<WebResponse></WebResponse>\';\n' +
                '    return responseFactory.createResponse({\n' +
                '        statusCode:200,\n' +
                '        contentType:\'application/xml\',\n' +
                '        content:content});\n' +
                '})();';

    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.loadTestConfig();
    casper.test.begin('Edit stub', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.waitForConfigToLoad(function () {
                casper.click('.stub button.edit');
            });
        });

        casper.then(function () {
            casper.waitUntilVisible('.content', function () {
                var origForm = casper.getFormValues('form');
                EXPECTED_FORM.path = origForm.path;//can't truly edit the path,
                //but capturing the existing path makes testing much more convenient later
                casper.click('#scriptThen');
                casper.fill('form', EXPECTED_FORM);

                swivelUtils.setEditorText('#content', WHEN_CONTENT);
                swivelUtils.setEditorText('#thenScript', THEN_SCRIPT);
                casper.click('#submit');
            });
        });

        casper.then(function () {
            swivelUtils.waitForConfigToLoad(function () {
                test.assertSelectorHasText('.stub', EXPECTED_FORM.description);
                casper.click('.stub button.edit');
            });
        });

        casper.then(function () {
            casper.waitUntilVisible('.content', function () {
                var form = casper.getFormValues('form');

                test.assertVisible('#thenScript', 'then script editor is visible');

                test.assertEquals(form, EXPECTED_FORM, 'form is populated as expected');

                test.assertEquals(swivelUtils.getEditorText('#content'), WHEN_CONTENT,
                    'when content editor is populated as expected');
                test.assertEquals(swivelUtils.getEditorText('#thenScript'), THEN_SCRIPT,
                    'then script editor is populated as expected');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();