(function () {
    function getConfigEntries() {
        return $('#configRoot').find('ul').length;
    }

    casper.test.comment('Load configuration');

    casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
        casper.waitUntilVisible('#configRoot', function () {
            casper.click('#reset');
            casper.click('#resetOK');
            casper.waitUntilVisible('#configRoot', function () {
                var entries = casper.evaluate(getConfigEntries);
                casper.echo('Reset Swivel to ' + entries + ' entries');
            });
        });
    });

    casper.then(function () {
        casper.click('#loadConfig');
        casper.fill('#loadConfigDialog form', {
            'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
        });
        casper.click('#loadConfigOK');
        casper.waitWhileVisible('#loadConfigDialog', function(){
            casper.waitUntilVisible('#configRoot', function () {
                var entries = casper.evaluate(getConfigEntries);
                casper.test.assertTruthy(entries, 'Successfully loaded entries');
            });
        });
    });

    casper.run(function () {
        casper.test.done();
    });
})();