(function () {

    var swivelUtils = require('../lib/swivelUtils'), config;

    casper.options.onResourceReceived = function (casper, resource) {
        if (resource.url.match(/\/config$/)) {
            config = resource;
        }
    };

    casper.test.begin('Exporting config', function (test) {
        casper.start(swivelUtils.HOME_URL, function () {
            swivelUtils.loadTestConfig(function () {
                config = null;
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