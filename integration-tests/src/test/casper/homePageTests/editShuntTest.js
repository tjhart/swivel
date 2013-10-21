(function () {
    var swivelUtils = require('../lib/swivelUtils');
    swivelUtils.loadTestConfig();
    casper.test.begin('Edit Shunt', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            casper.viewport(1280, 1024, function () {
                swivelUtils.waitForConfigToLoad(function () {
                    casper.click('.shunt button.edit');
                });
            });
        });

        casper.then(function () {
            test.assertVisible('#addOrEditShuntDialog', 'Shut dialog is visible');
            test.assertExists('#shuntPath.ui-state-disabled', 'Path element has disabled class');
            test.assertExists('#shuntPath[readonly]', 'path element is readonly');
            casper.fill('#addOrEditShuntDialog form', {
                'remoteURL': 'http://different.host/path'
            });
            casper.click('#addShuntOK');
        });

        casper.then(function () {
            test.assertSelectorHasText('.shunt', 'http://different.host/path', 'Shunt updated');
        });

        casper.run(function () {
            test.done();
        });
    });
})();