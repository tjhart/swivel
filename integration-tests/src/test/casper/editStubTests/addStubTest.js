(function () {
    const THEN_SCRIPT_KEY = 'thenScript',
        EXPECTED_FORM = {
            path: 'add/stub/test/path',
            description: 'casper test for adding stub',
            method: 'GET',
            query: 'query',
            remoteAddress: '127.0.0.1',
            requestContentType: 'application/xml',
            statusCode: '200',
            reason: 'OK',
            responseContentType: 'text/xml'

        },
        EXPECTED_EDITORS = {
            content: '<node></node>',
            whenScript: '(function(){return true;})();',
            content2: '<html><body>response</body></html>'
        };

    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('Add Stub', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.reset();
        });

        casper.then(function () {
            casper.open(swivelUtils.EDIT_STUB_URL);
            casper.waitUntilVisible('.content');
        });

        casper.then(function () {
            var key;

            casper.click('#staticThen');
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
                swivelUtils.whenConfigLoaded(function () {
                    test.assertSelectorHasText('.path', EXPECTED_FORM.path, 'stub path listed');
                    test.assertSelectorHasText('.stub', EXPECTED_FORM.description, 'stub description listed');
                });
            });
        });

        casper.then(function () {
            casper.click('.stub button.edit');
        });

        casper.then(function () {
            casper.waitUntilVisible('.content', function () {
                var form = casper.getFormValues('form'), key;

                test.assertExists('#path.ui-state-disabled', 'Path has ui-state-disabled class');
                test.assertExists('#path[readonly]', 'path is read only');

                for (key in EXPECTED_FORM) {
                    if (EXPECTED_FORM.hasOwnProperty(key)) {
                        test.assertEquals(form[key], EXPECTED_FORM[key], [key, 'is populated as expected'].join(' '));
                    }
                }

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