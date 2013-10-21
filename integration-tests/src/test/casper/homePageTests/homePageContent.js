(function () {
    casper.test.begin('Home page loaded content', function (test) {

        casper.start('http://localhost:8080/swivel_server_war_exploded/', function () {
            test.assertHttpStatus(200);
            test.assertTitle('Swivel');
        });

        casper.then(function () {
            this.waitUntilVisible('#configRoot', function () {

                test.assertTrue(casper.getHTML('#configRoot > a').match(/Configuration/) != null,
                    'configuration root label is: "Configuration"');
                test.assertVisible('#reset', 'Reset button is visible');
                test.assertVisible('#addShunt', 'Add shunt button is visible');
                test.assertVisible('#addStub', 'Add stub button is visible');
                test.assertVisible('#getConfig', 'Export button is visible');
                test.assertVisible('#loadConfig', 'Load Configuration button is visible');
            });
        });

        casper.run(function () { test.done(); });
    });
})();