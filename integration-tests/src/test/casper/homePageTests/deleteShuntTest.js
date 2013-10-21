(function () {
    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.loadTestConfig();
    casper.test.begin('delete shunt', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.waitForConfigToLoad(function () {
                casper.click('.shunt button.delete');
            });
        });

        casper.then(function () {
            test.assertDoesntExist('.shunt', 'Shunt deleted');
            test.assertElementCount('.path', 1, 'shunt path automatically removed (because it\'s empty');
        });

        casper.run(function () {
            test.done();
        });
    });
})();