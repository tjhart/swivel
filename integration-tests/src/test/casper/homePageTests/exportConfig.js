(function () {

    var swivelUtils = require('../lib/swivelUtils'), config;

    casper.options.onResourceReceived = function (casper, resource) {
        if (resource.url.match(/\/config$/)) {
            config = resource;
        }
    };

    casper.test.begin('Exporting config', function (test) {
        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.waitUntilVisible('#loadConfig', function () {
                casper.click('#loadConfig');
                casper.fill('#loadConfigDialog form', {
                    'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
                });
                casper.click('#loadConfigOK');

                casper.waitUntilVisible('#configRoot', function () {
                    casper.echo('Configuration now has ' + swivelUtils.getConfigEntries() + ' entries');
                    config = null;
                });
            });
        });

        casper.then(function () {
            casper.click('#getConfig');
            casper.waitFor(function () {return config != null}, function () {
                var headers = {};
                casper.each(config.headers, function (casper, item) {
                    headers[item.name] = item.value;
                });
                test.assertTruthy(headers['Content-Disposition'].match(/^attachment;/), 'Config retrieved');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();