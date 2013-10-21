(function () {

    var swivelUtils = require('../lib/swivelUtils');

    casper.test.begin('Load configuration', function(test){
        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.waitUntilVisible('#configRoot', function () {
                casper.click('#reset');
                casper.click('#resetOK');
                casper.waitUntilVisible('#configRoot', function () {
                    casper.echo('Reset Swivel to ' + swivelUtils.getConfigEntries() + ' entries');
                });
            });
        });

        casper.then(function () {
            casper.click('#loadConfig');
            casper.fill('#loadConfigDialog form', {
                'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
            });
            casper.click('#loadConfigOK');
            casper.waitWhileVisible('#loadConfigDialog', function () {
                casper.waitUntilVisible('#configRoot', function () {
                    test.assertElementCount('#configRoot ul', 4, 'Successfully loaded entries');
                });
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();