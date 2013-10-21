exports.HOME_URL = 'http://localhost:8080/swivel_server_war_exploded/';

function getConfigEntries() {
    return casper.evaluate(function () {return __utils__.findAll('.stub,.shunt').length;});
}

exports.getConfigEntries = getConfigEntries;

function whenConfigLoaded(callback) {
    casper.waitUntilVisible('#configRoot', callback);
}

exports.whenConfigLoaded = whenConfigLoaded;

function loadTestConfig(callback) {
    casper.waitUntilVisible('#loadConfig', function () {
        casper.click('#loadConfig');
        casper.fill('#loadConfigDialog form', {
            'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
        });
        casper.click('#loadConfigOK');

        whenConfigLoaded(function () {
            casper.echo('Configuration now has ' + getConfigEntries() + ' entries');
            if (callback) callback();
        });
    });
}

exports.loadTestConfig = loadTestConfig;
