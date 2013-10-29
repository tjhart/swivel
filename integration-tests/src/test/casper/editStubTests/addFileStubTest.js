(function () {
    "use strict";

    var swivelUtils = require('../lib/swivelUtils'),
        EXPECTED_STUB_DESCRIPTION = {
            path: 'some/path',
            description: 'Test: file response'
        },
        EXPECTED_WHEN = {
            method: 'GET',
            remoteAddress: '',
            query: '',
            contentType: ''
        },
        EXPECTED_THEN = {
            thenType: 'file',
            statusCode: 200,
            contentFile: swivelUtils.TEST_CONFIG_FILE_PATH,
            contentType: ''
        };

    swivelUtils.reset(function () {
        casper.test.begin('Add File Stub Test', function (test) {
            casper.start(swivelUtils.EDIT_STUB_URL, function () {
                casper.waitUntilVisible('.content', function () {
                    casper.fill('form[name="stubDescription"]', EXPECTED_STUB_DESCRIPTION);
                    casper.fill('form[name="when"]', EXPECTED_WHEN);
                    casper.click('#fileThen');
                    casper.fill('form[name="then"]', EXPECTED_THEN);

                    casper.click('#submit');
                });
            });

            casper.then(function () {
                casper.waitForUrl(swivelUtils.HOME_URL, function () {
                    swivelUtils.waitForConfigToLoad(function () {
                        casper.click('.stub button.edit');
                    });
                });
            });

            casper.then(function () {
                casper.waitForUrl(swivelUtils.EDIT_STUB_URL, function () {
                    casper.waitUntilVisible('.content', function () {
                        var stubForm = casper.getFormValues('form[name="stubDescription"]'),
                            thenForm = casper.getFormValues('form[name="then"]'),
                            testFileName = swivelUtils.TEST_CONFIG_FILE_PATH
                                .substr(swivelUtils.TEST_CONFIG_FILE_PATH.lastIndexOf('/') + 1);

                        test.assertEquals(stubForm.path, EXPECTED_STUB_DESCRIPTION.path);
                        test.assertEquals(stubForm.description, EXPECTED_STUB_DESCRIPTION.description);

                        test.assertEquals(casper.getFormValues('form[name="when"]'), EXPECTED_WHEN);

                        test.assertEquals(thenForm.thenType, 'file');
                        test.assertEquals(thenForm.statusCode, '200');
                        test.assertEquals(thenForm.contentType, '');
                        test.assertSelectorHasText('#currentFileName', testFileName);
                    });
                });
            });

            casper.run(function () {
                test.done();
            });
        });
    });
})();