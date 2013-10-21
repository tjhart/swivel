(function () {

    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.loadTestConfig(function () {
        casper.test.begin('Reset Configuration test', function (test) {
            casper.start(swivelUtils.HOME_URL, function () {
                swivelUtils.waitForConfigToLoad(function () {
                    casper.click('#reset');
                    test.assertVisible('#resetDialog', 'Reset Dialog appears');
                });
            });

            casper.then(function () {
                casper.click('#resetOK');
                test.assertNotVisible('#resetDialog', 'Reset Dialog dismissed');
            });

            casper.then(function () {
                swivelUtils.waitForConfigToLoad(function () {
                    test.assertElementCount('.path,.stub,.shunt', 0, 'Configuration root has zero entries');
                });
            });

            casper.run(function () {
                test.done();
            });
        });
    });
})();