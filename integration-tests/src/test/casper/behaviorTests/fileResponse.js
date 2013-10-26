(function () {
    "use strict";
    var swivelUtils = require('../lib/swivelUtils'),
        PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    swivelUtils.reset(function () {
        casper.test.begin('File stub works', function (test) {
            casper.start(swivelUtils.EDIT_STUB_URL, function () {
                casper.waitUntilVisible('.content', function () {
                    //can't send multi-part through phantomjs API (as of 1.9.2
                    casper.fill('form[name="stubDescription"]', {
                        path: 'some/path',
                        description: 'Test: file response'
                    });
                    casper.fill('form[name="when"]', {
                        method: 'GET'
                    });
                    casper.click('#fileContent');
                    casper.fill('form[name="then"]', {
                        statusCode: 200, contentFile: swivelUtils.TEST_CONFIG_FILE_PATH
                    });

                    casper.click('#submit');
                    casper.waitForUrl(swivelUtils.HOME_URL);
                });
            });

            casper.then(function () {
                casper.open(PROXY_URL).then(function (response) {
                    var contentDisposition = response.headers.get('Content-Disposition'),
                        testFileName = swivelUtils.TEST_CONFIG_FILE_PATH
                            .substr(swivelUtils.TEST_CONFIG_FILE_PATH.lastIndexOf('/') + 1),
                        filenamePart = contentDisposition.match(/filename=\"(.*)\"/)[1];

                    //NOTE:TJH - need to come up with a better test
                    test.assertEquals(filenamePart, testFileName);
                });
            });

            casper.run(function () {
                test.done();
            });
        });
    });
})();