(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset();
    swivelUtils.configureStub('some/path', {
        description: 'StubMatchingTest: This stub matches all gets',
        when: {method: 'GET'},
        then: {statusCode: 200, reason: 'OK'}
    });

    casper.test.begin('Stub matching GET tests', function (test) {
        casper.start(PROXY_URL, function () {
            test.assertHttpStatus(200, 'Properly matched GET');
        });

        casper.then(function () {
            casper.open(PROXY_URL, {method:'DELETE'}).then(function(){
                test.assertHttpStatus(404, 'Properly did not match DELETE');
            })
        });
        casper.then(function () {
            casper.open(PROXY_URL, {method:'PUT'}).then(function(){
                test.assertHttpStatus(404, 'Properly did not match PUT');
            })
        });
        casper.then(function () {
            casper.open(PROXY_URL, {method:'POST'}).then(function(){
                test.assertHttpStatus(404, 'Properly did not match POST');
            })
        });

        casper.run(function () {
            test.done();
        });
    });
})();