(function () {

    var swivelUtils = require('../lib/swivelUtils');

    casper.test.begin('Load configuration', function(test){
        casper.start(swivelUtils.HOME_URL, function () {
            casper.waitUntilVisible('#configRoot', function () {
                casper.click('#reset');
                casper.click('#resetOK');
                casper.waitUntilVisible('#configRoot', function () {
                    casper.echo('Reset Swivel to ' + swivelUtils.getConfigEntries() + ' entries');
                });
            });
        });

        casper.then(function () {
            swivelUtils.loadTestConfig(function(){
                test.assertElementCount('.stub,.shunt', 2, 'Successfully loaded entries');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();