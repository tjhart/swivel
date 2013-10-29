(function () {
    const
        EXPECTED_STUB_FORM = {
            description: 'Edit Stub Test Description'
        },
        EXPECTED_WHEN_FORM = {
            method: 'PUT',
            query: '',
            remoteAddress: '',
            contentType: 'application/xml'
        },
        EXPECTED_THEN_FORM = {
            thenType: 'script',
            contentFile: '',
            statusCode: '',
            contentType: ''
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
    swivelUtils.loadTestConfig(function () {
        casper.test.begin('Edit stub', function (test) {
            casper.start(swivelUtils.HOME_URL, function () {
                swivelUtils.waitForConfigToLoad(function () {
                    casper.click('.stub button.edit');
                });
            });

            casper.then(function () {
                casper.waitUntilVisible('.content', function () {
                    casper.click('#scriptThen');
                    casper.fill('form[name="stubDescription"]', EXPECTED_STUB_FORM);
                    casper.fill('form[name="when"]', EXPECTED_WHEN_FORM);
                    casper.fill('form[name="then"]', EXPECTED_THEN_FORM);

                    swivelUtils.setEditorText('#content', WHEN_CONTENT);
                    swivelUtils.setEditorText('#thenScript', THEN_SCRIPT);
                    casper.capture('screenshot.png');
                    casper.click('#submit');
                });
            });

            casper.then(function () {
                swivelUtils.waitForConfigToLoad(function () {
                    test.assertSelectorHasText('.stub', EXPECTED_STUB_FORM.description);
                    casper.click('.stub button.edit');
                });
            });

            casper.then(function () {
                casper.waitUntilVisible('.content', function () {
                    var stubForm = casper.getFormValues('form[name="stubDescription"]');
                    test.assertVisible('#thenScript', 'then script editor is visible');

                    test.assertEquals(stubForm.description, EXPECTED_STUB_FORM.description);
                    test.assertEquals(casper.getFormValues('form[name="when"]'),
                        EXPECTED_WHEN_FORM, 'form is populated as expected');
                    test.assertEquals(casper.getFormValues('form[name="then"]'),
                        EXPECTED_THEN_FORM, 'form is populated as expected');

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
    });
})();