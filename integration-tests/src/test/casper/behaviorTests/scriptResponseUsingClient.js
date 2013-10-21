(function () {
    var swivelUtils = require('../lib/swivelUtils'), server = require('webserver').create();
    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    server.listen(8090, function (request, response) {
        response.headers['Content-Type'] = 'text/html';
        response.write('<html><body>Successfully scripted a proxy call</body></html>');
        response.close();
    });

    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Test: script response using client',
            when: {method: 'GET'},
            then: {script: '(function(client){\n' +
                '   var request = new Packages.org.apache.http.client.methods.HttpGet("http://localhost:8090/path");\n' +
                '\n' +
                '   return client.execute(request);\n' +
                '})(client);'}
        }, function () {
            casper.test.begin('Static stub response', function (test) {
                casper.start(PROXY_URL, function (response) {
                    test.assertHttpStatus(200, 'status code matches');
                    test.assertEquals(response.contentType, 'text/html');
                    test.assertSelectorHasText('body', 'Successfully scripted a proxy call');
                });

                casper.run(function () {
                    server.close();
                    test.done();
                });
            });
        });
    });
})();