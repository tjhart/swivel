(function () {
    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('Delete stub', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.whenConfigLoaded(function () {
                swivelUtils.loadTestConfig();
            });
        });

        casper.then(function () {
            casper.click('.stub button.delete');
            swivelUtils.whenConfigLoaded(function () {
                test.assertDoesntExist('.stub', 'Stub deleted');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();