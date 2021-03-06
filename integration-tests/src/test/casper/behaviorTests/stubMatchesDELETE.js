(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'StubMatchingTest: This stub matches all gets',
            when: {method: 'DELETE'},
            then: {statusCode: 200}
        }, function () {
            casper.test.begin('Stub matching DELETE tests', function (test) {
                casper.start(PROXY_URL, function () {
                    test.assertHttpStatus(404, 'Did not match GET');
                });

                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'DELETE'}).then(function () {
                        test.assertHttpStatus(200, 'Matched DELETE');
                    })
                });
                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'PUT'}).then(function () {
                        test.assertHttpStatus(404, 'Did not match PUT');
                    })
                });
                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'POST'}).then(function () {
                        test.assertHttpStatus(404, 'Did not match POST');
                    })
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();