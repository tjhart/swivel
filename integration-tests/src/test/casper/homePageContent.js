(function () {
    casper.test.begin('Home page loaded content', function () {

        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            casper.test.assertHttpStatus(200);
            casper.test.assertTitle('Swivel');
        });

        casper.then(function () {
            this.waitUntilVisible('#configRoot', function () {

                casper.test.assertTrue(casper.getHTML('#configRoot > a').match(/Configuration/) != null,
                    'configuration root label is: "Configuration"');
                casper.test.assertVisible('#reset', 'Reset button is visible');
                casper.test.assertVisible('#addShunt', 'Add shunt button is visible');
                casper.test.assertVisible('#addStub', 'Add stub button is visible');
                casper.test.assertVisible('#getConfig', 'Export button is visible');
                casper.test.assertVisible('#loadConfig', 'Load Configuration button is visible');
            });
        });

        casper.run(function () {
            casper.test.done();
        });
    });
})();