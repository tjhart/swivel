(function () {
    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('Home page loaded content', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            test.assertHttpStatus(200);
            test.assertTitle('Swivel');
        });

        casper.then(function () {
            swivelUtils.waitForConfigToLoad(function () {
                test.assertSelectorHasText('#configRoot > a', 'Configuration',
                    'configuration root label is: "Configuration"');
                test.assertVisible('#reset', 'Reset button is visible');
                test.assertVisible('#addShunt', 'Add shunt button is visible');
                test.assertVisible('#addStub', 'Add stub button is visible');
                test.assertVisible('#getConfig', 'Export button is visible');
                test.assertVisible('#loadConfig', 'Load Configuration button is visible');
            });
        });

        casper.run(function () { test.done(); });
    });
})();