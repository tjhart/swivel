(function () {
    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.loadTestConfig();
    casper.test.begin('Delete stub', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.waitForConfigToLoad(function () {
                casper.click('.stub button.delete');
            });
        });

        casper.then(function () {
            test.assertDoesntExist('.stub', 'Stub deleted');
            test.assertElementCount('.path', 1, 'stub path automatically removed');
        });

        casper.run(function () {
            test.done();
        });
    });
})();