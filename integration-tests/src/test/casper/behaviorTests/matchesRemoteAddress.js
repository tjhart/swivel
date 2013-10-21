(function () {
    var swivelUtils = require('../lib/swivelUtils'), stubCount = 0;

    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    function checkConfigurationComplete() {
        stubCount = stubCount + 1;
        if (stubCount == 2) {
            runTests();
        }
    }
    swivelUtils.reset();
    swivelUtils.configureStub('some/path', {
        description: 'Testing: this stub matches 127.0.0.1',
        when: {remoteAddress: '0:0:0:0:0:0:0:1'},//localhost match
        then: {statusCode: 200, reason: 'OK'}
    }, checkConfigurationComplete);
    swivelUtils.configureStub('some/other/path', {
        description: 'Testing:this stub should not match during automated testing',
        when: {remoteAddress: '999.999.999.999'},
        then: {statusCode: 200, reason: 'OK'}
    }, checkConfigurationComplete);

    function runTests() {
        casper.test.begin('stub matches remoteAddress tests', function (test) {
            casper.start(PROXY_URL, function () {
                test.assertHttpStatus(200, 'stub matched 0:0:0:0:0:0:0:1');
            });

            casper.then(function () {
                casper.open([swivelUtils.PROXY_URL, 'some/other/path'].join('/')).then(function () {
                    test.assertHttpStatus(404, 'stub did not match 0:0:0:0:0:0:0:1');
                });
            });
            casper.run(function () {
                test.done();
            });
        });
    }
})();