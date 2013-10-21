(function () {
    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('delete shunt', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.loadTestConfig();
        });

        casper.then(function () {
            casper.click('.shunt button.delete');
            swivelUtils.whenConfigLoaded(function () {
                test.assertDoesntExist('.shunt', 'Shunt deleted');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();