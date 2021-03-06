(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'StubMatchingTest: This stub matches all gets',
            when: {method: 'POST'},
            then: {statusCode: 200}
        }, function () {
            casper.test.begin('Stub matching POST tests', function (test) {
                casper.start(PROXY_URL, function () {
                    test.assertHttpStatus(404, 'Properly did not matched GET');
                });

                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'DELETE'}).then(function () {
                        test.assertHttpStatus(404, 'Properly did not match DELETE');
                    })
                });
                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'PUT'}).then(function () {
                        test.assertHttpStatus(404, 'Properly did not match PUT');
                    })
                });
                casper.then(function () {
                    casper.open(PROXY_URL, {method: 'POST'}).then(function () {
                        test.assertHttpStatus(200, 'Properly matched POST');
                    })
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();