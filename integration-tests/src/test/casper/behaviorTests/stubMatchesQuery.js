(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'StubMatchingTest: This stub matches a query',
            when: {query: 'queryString'},
            then: {statusCode: 200}
        }, function () {
            casper.test.begin('Stub matches query tests', function (test) {
                casper.start([PROXY_URL, 'queryString'].join('?'), function () {
                    test.assertHttpStatus(200, 'stub matched query');
                });

                casper.then(function () {
                    casper.open([PROXY_URL, 'differentQuery'].join('?')).then(function () {
                        test.assertHttpStatus(404, 'Stub did not match different query');
                    });
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();