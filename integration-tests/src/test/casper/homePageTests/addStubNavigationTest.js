(function () {

    var swivelUtils = require('../lib/swivelUtils'), urlRegexp = /\/editStub.html$/;

    casper.test.begin('Add Stub Navigates as Expected', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.waitForConfigToLoad(function () {
                casper.click('#addStub');
            });
        });

        casper.then(function () {
            test.assertHttpStatus(200, 'Successful navigation');
            test.assertUrlMatch(urlRegexp, 'Navigation successful');
        });

        casper.run(function () {
            test.done();
        });
    });
})();