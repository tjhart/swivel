(function () {

    var swivelUtils = require('../lib/swivelUtils');
    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Test: Stub matches script',
            when: {script: '(function(request){' +
                '   var path = request.getURI().getPath();' +
                '   return path.matches("some/path/\\\\d+/more/path");' +
                '})(request);'},
            then: {statusCode: 200, reason: 'OK'}
        }, function () {
            casper.test.begin('Stub matches script', function (test) {
                casper.start([PROXY_URL, '12345/more/path'].join('/'), function () {
                    test.assertHttpStatus(200, 'script matched path');
                });

                casper.then(function(){
                    casper.open([PROXY_URL, 'notNumbers/more/path'].join('/')).then(function () {
                        test.assertHttpStatus(404, 'script did not match path');
                    });
                });

                casper.then(function(){
                    casper.open([PROXY_URL, '12345/less/path'].join('/')).then(function () {
                        test.assertHttpStatus(404, 'script did not match path');
                    });
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();