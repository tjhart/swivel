(function () {
    var swivelUtils = require('../lib/swivelUtils');

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');
    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Testing:Stub matching content',
            when: {content: 'fred'},
            then: {statusCode: 200, reason: 'OK'}
        }, function () {
            casper.test.begin('stub matching content tests', function (test) {
                casper.start().then(function () {
                    casper.open(PROXY_URL, {method: 'PUT', data: 'fred'}).then(function () {
                        test.assertHttpStatus(200, 'Stub matched content');
                    });
                });

                casper.then(function(){
                    casper.open(PROXY_URL, {method: 'PUT', data: 'Steve'}).then(function () {
                        test.assertHttpStatus(404, 'Stub did not match content');
                    });
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();