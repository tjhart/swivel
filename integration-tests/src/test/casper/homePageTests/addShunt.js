(function () {

    var swivelUtils = require('../lib/swivelUtils');
    casper.test.begin('Add a shunt', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            casper.viewport(1280, 1024).then(function () {
                swivelUtils.reset();
            });
        });

        casper.then(function () {
            casper.click('#addShunt');
            test.assertVisible('#addOrEditShuntDialog', 'Add shunt dialog appears');
            casper.fill('#addOrEditShuntDialog form', {
                path: 'some/path',
                remoteURL: 'http://host/some/path'
            });
            casper.click('#addShuntOK');
            casper.waitWhileVisible('#addOrEditShuntDialog');
        });

        casper.then(function () {
            swivelUtils.whenConfigLoaded(function () {
                test.assertSelectorHasText('.path', 'some/path', 'path is listed');
                test.assertElementCount('.shunt', 1, 'shunt is listed');
                test.assertSelectorHasText('.shunt', 'http://host/some/path', 'remoteURL is displayed');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();