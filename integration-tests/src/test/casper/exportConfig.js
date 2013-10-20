(function () {
    var utils = require('utils');

    var configFound = false, config;

    casper.options.onResourceReceived = function (casper, resource) {
        config = null;
        configFound = resource.url.match(/\/config$/) != null;
        if (configFound) {
            config = resource;
        }
    };

    casper.test.comment('Exporting config');

    casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
        casper.waitFor(function () {return configFound});
    });

    casper.then(function () {
        casper.click('#getConfig');
        casper.waitFor(function () {return configFound}, function () {
            var headers = {};
            casper.each(config.headers, function (casper, item) {
                headers[item.name] = item.value;
            });
            casper.test.assertTruthy(headers['Content-Disposition'].match(/^attachment;/), 'Config retrieved');
        });
    });

    casper.run(function () {
        casper.test.done();
    });
})();