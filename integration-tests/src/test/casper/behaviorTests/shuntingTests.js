(function () {

    var swivelUtils = require('../lib/swivelUtils'),
        server = require('webserver').create();

    const PROXY_URL = [swivelUtils.PROXY_URL, 'testServer/some/path'].join('/');
    server.listen('127.0.0.1:8090', function (request, response) {
        var contentType = request.headers['Content-Type'];
        response.write('<html><body>' + 'url:' + request.url + ', method:' + request.method);
        if (contentType) {
            response.write(', contentType:' + contentType);
        }
        if (request.postRaw) {
            response.write(', postRaw:' + request.postRaw);
        }
        else if (request.post) {
            response.write(', post:' + request.post);
        }
        response.write('</body></html>');
        response.close();
    });

    swivelUtils.reset();
    swivelUtils.configureShunt('testServer', {remoteURL: 'http://127.0.0.1:8090'});

    casper.test.begin('Shunt behavior tests', function (test) {
        casper.start(PROXY_URL, function () {
            test.assertSelectorHasText('body', 'url:/some/path, method:GET');
        });

        casper.then(function () {
            casper.open(PROXY_URL, {method: 'DELETE'})
                .then(function () {
                    test.assertSelectorHasText('body', 'url:/some/path, method:DELETE');
                });
        });

        casper.then(function () {
            casper.open(PROXY_URL, {method: 'PUT', data: {foo: 'foo', bar: 1}, headers: {'Content-Type': 'application/json'}})
                .then(function () {
                    test.assertSelectorHasText('body', 'url:/some/path, method:PUT, contentType:application/json, post:foo=foo&bar=1');
                });
        });

        casper.then(function () {
            casper.open(PROXY_URL, {method: 'POST', data: 'plainData', headers: {'Content-Type': 'text/plain'}})
                .then(function () {
                    test.assertSelectorHasText('body', 'url:/some/path, method:POST, contentType:text/plain, post:plainData');
                });
        });

        casper.run(function () {
            server.close();
            test.done();
        });
    });
})();