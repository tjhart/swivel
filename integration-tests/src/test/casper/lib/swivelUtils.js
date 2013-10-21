(function () {
    const BASE_URL = 'http://localhost:8080/swivel_server_war_exploded/';
    exports.BASE_URL = BASE_URL;
    exports.HOME_URL = BASE_URL + 'index.html';
    exports.EDIT_STUB_URL = BASE_URL + 'editStub.html';
    const CONFIG_URL = BASE_URL + 'rest/config';

    var webpage = require('webpage');
    var fs = require('fs');


    function getConfigEntries() {
        return casper.evaluate(function () {return __utils__.findAll('.stub,.shunt').length;});
    }

    exports.getConfigEntries = getConfigEntries;

    function waitForConfigToLoad(callback) {
        casper.waitUntilVisible('#configRoot', callback);
    }

    exports.waitForConfigToLoad = waitForConfigToLoad;

    function loadTestConfig() {
        var config = fs.read('integration-tests/src/test/casper/testSwivelConfig.json'),
            page = webpage.create();

        page.customHeaders = {'Content-Type': 'application/json'};

        page.open(CONFIG_URL, 'PUT', config, function (status) {
            page.close();
            if (status === 'fail') {
                throw 'load config failed';
            }
        });
    }

    exports.loadTestConfig = loadTestConfig;

    function reset() {
        var page = webpage.create();
        page.open(CONFIG_URL, 'DELETE', function (status) {
            page.close();
            if (status === 'fail') {
                throw 'reset failed';
            }
        });
    }

    exports.reset = reset;

    function setEditorText(selector, content) {
        casper.evaluate(function (pSelector, pContent) {
            $(pSelector)
                .data('editor')
                .setValue(pContent);
        }, selector, content);
    }

    exports.setEditorText = setEditorText;

    function getEditorText(selector) {
        return casper.evaluate(function (pSelector) {
            return $(pSelector)
                .data('editor')
                .getValue();
        }, selector);
    }

    exports.getEditorText = getEditorText;
})();
