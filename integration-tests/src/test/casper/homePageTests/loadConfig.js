(function () {

    var swivelUtils = require('../lib/swivelUtils');

    casper.test.begin('Load configuration', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.loadTestConfig(function () {
                test.assertElementCount('.stub,.shunt', 2, 'Successfully loaded entries');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();