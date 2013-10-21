(function () {
    var swivelUtils = require('../lib/swivelUtils');
    const PROXY_URL = [swivelUtils.PROXY_URL, 'some/path'].join('/');

    swivelUtils.reset(function () {
        swivelUtils.configureStub('some/path', {
            description: 'Test: script response',
            when: {method: 'GET'},
            then: {script: '(function(request, matchedURI, responseFactory, client){\n' +
                '   var content = \'requestedURI:\' + request.getURI() +\n' +
                '       \', matchedURI:\' + matchedURI;\n' +
                '\n' +
                '   return responseFactory.createResponse({\n' +
                '           statusCode:200,\n' +
                '           contentType:\'text/plain\',\n' +
                '           content:content});\n' +
                '})(request, matchedURI, responseFactory, client);'}
        }, function () {
            casper.test.begin('Static stub response', function (test) {
                casper.start(PROXY_URL, function (response) {
                    test.assertHttpStatus(200, 'status code matches');
                    test.assertEquals(response.contentType, 'text/plain');
                    test.assertEquals(casper.page.plainText, 'requestedURI:some/path, matchedURI:some/path',
                        'content matches');
                });

                casper.run(function () {
                    test.done();
                });
            });
        });
    });
})();