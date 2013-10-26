//TODO:TJH - refactor into 2 modules - one specific for casper tests, one generic for Node environment

(function () {
    "use strict";

    //YELLOWTAG:TJH - this should probably be part of the config.
    var BASE_URL = 'http://localhost:8080/swivel_server_war_exploded/',
        TEST_CONFIG_FILE_PATH = 'integration-tests/src/test/casper/testSwivelConfig.json',
        CONFIG_URL = BASE_URL + 'rest/config',
        APPLICATION_JSON_HEADER = {'Content-Type': 'application/json'},
        webpage = require('webpage'), fs = require('fs');

    exports.BASE_URL = BASE_URL;
    exports.HOME_URL = BASE_URL + 'index.html';
    exports.EDIT_STUB_URL = BASE_URL + 'editStub.html';
    exports.PROXY_URL = BASE_URL + 'rest/proxy';
    exports.TEST_CONFIG_FILE_PATH = TEST_CONFIG_FILE_PATH;

    function getConfigEntries() {
        return casper.evaluate(function () {return __utils__.findAll('.stub,.shunt').length;});
    }

    exports.getConfigEntries = getConfigEntries;

    function waitForConfigToLoad(callback) {
        casper.waitUntilVisible('#configRoot', callback);
    }

    exports.waitForConfigToLoad = waitForConfigToLoad;

    function loadTestConfig(callback) {
        var config = fs.read(TEST_CONFIG_FILE_PATH),
            page = webpage.create();

        page.customHeaders = APPLICATION_JSON_HEADER;

        page.open(CONFIG_URL, 'PUT', config, function (status) {
            page.close();
            if (status === 'fail') {
                throw 'load config failed';
            }
            if (callback) callback();
        });
    }

    exports.loadTestConfig = loadTestConfig;

    function reset(callback) {
        var page = webpage.create();
        page.open(CONFIG_URL, 'DELETE', function (status) {
            page.close();
            if (status === 'fail') {
                throw 'reset failed';
            }
            if (callback) callback();
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

    function configureShunt(path, shuntConfig, callback) {
        var page = webpage.create();
        page.customHeaders = APPLICATION_JSON_HEADER;
        page.open([CONFIG_URL, 'shunt', path].join('/'), 'PUT', JSON.stringify(shuntConfig), function (status) {
            page.close();
            if (status === 'fail') {
                throw 'configureShunt failed';
            }
            if (callback) callback();
        });
    }

    exports.configureShunt = configureShunt;

    function configureStub(path, stubConfig, callback) {
        var page = webpage.create();
        page.customHeaders = APPLICATION_JSON_HEADER;
        page.open([CONFIG_URL, 'stub', path].join('/'), 'POST', JSON.stringify(stubConfig), function (status) {
            page.close();
            if (status === 'fail') {
                throw 'configureStub failed';
            }
            if (callback) callback();
        });
    }

    exports.configureStub = configureStub;
})();
