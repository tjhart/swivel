(function () {

    var urlRegexp = /\/editStub.html$/;

    casper.test.begin('Add Stub Navigates as Expected', function (test) {
        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.waitUntilVisible('#addStub', function(){
                casper.click('#addStub');
                casper.waitForUrl(urlRegexp, function (arg) {
                    test.assertHttpStatus(200, 'Successful navigation');
                    test.assertUrlMatch(urlRegexp, 'Navigation successful');
                });
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();