(function () {
    var swivelUtils = require('../lib/swivelUtils');
    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Test: static response',
            when: {method: 'GET'},
            then: {statusCode: 200,
                contentType: 'text/plain',
                content: 'swiveling sneeze'}
        }, function () {
            casper.test.begin('Static stub response', function (test) {
                casper.start(PROXY_URL, function (response) {
                    test.assertHttpStatus(200, 'status code matches');
                    test.assertEquals(response.contentType, 'text/plain');
                    test.assertEquals(casper.page.plainText, 'swiveling sneeze', 'content matches');
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();