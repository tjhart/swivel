(function () {
    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('Delete stub', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.loadTestConfig();
        });

        casper.then(function () {
            casper.click('.stub button.delete');
            swivelUtils.whenConfigLoaded(function () {
                test.assertDoesntExist('.stub', 'Stub deleted');
                test.assertElementCount('.path', 1, 'stub path automatically removed');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();