(function () {

    casper.test.begin('Add a shunt', function(test){
        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.viewport(1280, 1024).then(function () {
                casper.waitUntilVisible('#configRoot', function () {
                    casper.click('#reset');
                    casper.click('#resetOK');
                });
            });
        });

        casper.then(function () {
            casper.click('#addShunt');
            casper.waitUntilVisible('#addOrEditShuntDialog', function () {
                casper.fill('#addOrEditShuntDialog form', {
                    path: 'some/path',
                    remoteURL: 'http://host/some/path'
                });
                casper.click('#addShuntOK');
                casper.waitWhileVisible('#addOrEditShuntDialog');
            });
        });

        casper.then(function () {
            casper.waitUntilVisible('#configRoot', function () {
                test.assertElementCount('#configRoot ul', 2, 'shunt is listed');
            });
        });

        casper.run(function () {
            test.done();
        });
    });
})();