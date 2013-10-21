(function () {

    var swivelUtils = require('../lib/swivelUtils');

    casper.test.begin('Reset Configuration test', function (test) {
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
            test.assertVisible('#resetDialog', 'Reset Dialog appears');
        });

        casper.then(function () {
            casper.click('#resetOK');
            test.assertNotVisible('#resetDialog', 'Reset Dialog dismissed');
        });

        casper.then(function () {
            casper.waitUntilVisible('#configRoot', function () {

                test.assertElementCount('#configRoot ul', 0, 'Configuration root has zero entries');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();