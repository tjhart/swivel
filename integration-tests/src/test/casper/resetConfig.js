(function () {

    function getConfigEntries() {
        return $('#configRoot').find('ul').length;
    }

    casper.test.comment('Reset Configuration test');

    casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
        casper.waitUntilVisible('#loadConfig', function () {
            casper.click('#loadConfig');
            casper.fill('#loadConfigDialog form', {
                'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
            });
            casper.clickLabel('Load Configuration');

            casper.waitUntilVisible('#configRoot', function () {
                var entries = casper.evaluate(getConfigEntries);
                casper.echo('Configuration now has ' + entries + ' entries');
            });
        });
    });

    casper.then(function () {
        casper.click('#reset');
        casper.test.assertVisible('#resetDialog', 'Reset Dialog appears');
    });

    casper.then(function () {
        casper.clickLabel('OK');
        casper.test.assertNotVisible('#resetDialog', 'Reset Dialog dismissed');
    });

    casper.then(function () {
        casper.waitUntilVisible('#configRoot', function () {
            var entries = casper.evaluate(getConfigEntries);

            casper.test.assertEquals(entries, 0, 'Configuration root has zero entries');
        });
    });

    casper.run(function () {
        casper.test.done();
    });
})();