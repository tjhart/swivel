(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Testing: Match content type',
            when: {contentType: 'application/xml'},
            then: {statusCode: 200, reason: 'OK'}
        }, function () {
            casper.test.begin('Stub matches content type', function (test) {
                casper.start().then(function () {
                    casper.open(PROXY_URL, {method: 'PUT', headers: {'Content-Type': 'application/xml'}})
                        .then(function () {
                            test.assertHttpStatus(200, 'matched content type');
                        });
                    casper.then(function () {
                        casper.open(PROXY_URL, {method: 'PUT', headers: {'Content-Type': 'application/json'}})
                            .then(function () {
                                test.assertHttpStatus(404, 'did not match different content type');
                            });
                    });
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();