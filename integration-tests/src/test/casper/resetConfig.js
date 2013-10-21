(function () {

    var swivelUtils = require('lib/swivelUtils');

    casper.test.begin('Reset Configuration test', function () {
        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.waitUntilVisible('#loadConfig', function () {
                casper.click('#loadConfig');
                casper.fill('#loadConfigDialog form', {
                    'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
                });
                casper.click('#loadConfigOK');

                casper.waitUntilVisible('#configRoot', function () {
                    casper.echo('Configuration now has ' + swivelUtils.getConfigEntries() + ' entries');
                });
            });
        });

        casper.then(function () {
            casper.click('#reset');
            casper.test.assertVisible('#resetDialog', 'Reset Dialog appears');
        });

        casper.then(function () {
            casper.click('#resetOK');
            casper.test.assertNotVisible('#resetDialog', 'Reset Dialog dismissed');
        });

        casper.then(function () {
            casper.waitUntilVisible('#configRoot', function () {

                casper.test.assertEquals(swivelUtils.getConfigEntries(), 0, 'Configuration root has zero entries');
            });
        });

        casper.run(function () {
            casper.test.done();
        });
    });
})();