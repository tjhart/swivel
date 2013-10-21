function getConfigEntries() {
    return casper.evaluate(function () {return __utils__.findAll('.stub,.shunt').length;});
}

exports.getConfigEntries = getConfigEntries;

exports.HOME_URL = 'http://localhost:8080/swivel_server_war_exploded/';

function loadTestConfig(callback) {
    casper.waitUntilVisible('#loadConfig', function () {
        casper.click('#loadConfig');
        casper.fill('#loadConfigDialog form', {
            'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
        });
        casper.click('#loadConfigOK');

        casper.waitUntilVisible('#configRoot', function () {
            casper.echo('Configuration now has ' + getConfigEntries() + ' entries');
            if (callback) callback();
        });
    })
}

exports.loadTestConfig = loadTestConfig;
