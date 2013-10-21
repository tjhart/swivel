(function () {

    var swivelUtils = require('../lib/swivelUtils');

    casper.test.begin('Load configuration', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.waitForConfigToLoad(function () {
                casper.click('#loadConfig');
                casper.fill('#loadConfigDialog form', {
                    'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
                });
                casper.click('#loadConfigOK');
            });
        });

        casper.then(function () {
            test.assertElementCount('.stub,.shunt', 2, 'Successfully loaded entries');
        });

        casper.run(function () {
            test.done();
        });
    });
})();