(function () {
    const THEN_SCRIPT_KEY = 'thenScript',
        EXPECTED_FORM = {
            path: 'add/stub/test/path',
            description: 'casper test for adding stub',
            method: 'GET',
            query: 'query',
            remoteAddress: '127.0.0.1',
            requestContentType: 'application/xml',
            thenType: 'static',
            statusCode: '200',
            responseContentType: 'text/xml'
        },
        EXPECTED_EDITORS = {
            content: '<node></node>',
            whenScript: '(function(){return true;})();',
            content2: '<html><body>response</body></html>'
        };

    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.reset();

    casper.test.begin('Add Stub', function (test) {
        casper.start(swivelUtils.EDIT_STUB_URL, function () {
            casper.waitUntilVisible('.content');
        });

        casper.then(function () {
            var key;

            casper.fill('form', EXPECTED_FORM);

            for (key in EXPECTED_EDITORS) {
                if (key !== THEN_SCRIPT_KEY && EXPECTED_EDITORS.hasOwnProperty(key)) {
                    swivelUtils.setEditorText(['#', key].join(''), EXPECTED_EDITORS[key]);
                }
            }

            casper.click('#submit');
        });

        casper.then(function () {
            casper.waitForUrl(swivelUtils.HOME_URL, function () {
                swivelUtils.waitForConfigToLoad(function () {
                    test.assertSelectorHasText('.path', EXPECTED_FORM.path, 'stub path listed');
                    test.assertSelectorHasText('.stub', EXPECTED_FORM.description, 'stub description listed');
                    casper.click('.stub button.edit');
                });
            });
        });

        casper.then(function () {
            casper.waitUntilVisible('.content', function () {
                var form = casper.getFormValues('form'), key;

                test.assertExists('#path.ui-state-disabled', 'Path has ui-state-disabled class');
                test.assertExists('#path[readonly]', 'path is read only');
                test.assertEquals(form, EXPECTED_FORM, 'form is populated as expected');

                for (key in EXPECTED_EDITORS) {
                    if (key !== THEN_SCRIPT_KEY && EXPECTED_EDITORS.hasOwnProperty(key)) {
                        test.assertEquals(swivelUtils.getEditorText(['#', key].join('')),
                            EXPECTED_EDITORS[key],
                            [key, 'editor is populated as expected'].join(' '));
                    }
                }
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();