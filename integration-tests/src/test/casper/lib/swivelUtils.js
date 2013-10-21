(function () {
    const BASE_URL = 'http://localhost:8080/swivel_server_war_exploded/';
    const HOME_URL = BASE_URL + 'index.html';
    const EDIT_STUB_URL = BASE_URL + 'editStub.html';
    const CONFIG_URL = BASE_URL + 'rest/config';

    var webpage = require('webpage');

    exports.BASE_URL = BASE_URL;
    exports.HOME_URL = HOME_URL;
    exports.EDIT_STUB_URL = EDIT_STUB_URL;

    function getConfigEntries() {
        return casper.evaluate(function () {return __utils__.findAll('.stub,.shunt').length;});
    }

    exports.getConfigEntries = getConfigEntries;

    function whenConfigLoaded(callback) {
        casper.waitUntilVisible('#configRoot', callback);
    }

    exports.whenConfigLoaded = whenConfigLoaded;

    function loadTestConfig(callback) {
        whenConfigLoaded(function () {
            casper.click('#loadConfig');
            casper.fill('#loadConfigDialog form', {
                'swivelConfig': 'integration-tests/src/test/casper/testSwivelConfig.json'
            });
            casper.click('#loadConfigOK');

            whenConfigLoaded(function () {
                casper.echo('Configuration now has ' + getConfigEntries() + ' entries');
                if (callback) callback();
            });
        });
    }

    exports.loadTestConfig = loadTestConfig;

    function reset() {
        var page = webpage.create();
        page.open(CONFIG_URL, 'DELETE', function (status) {
            if (status === 'fail') {
                throw "reset failed";
            }
            page.close();
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
