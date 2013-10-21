exports.HOME_URL = 'http://localhost:8080/swivel_server_war_exploded/index.html';
exports.EDIT_STUB_URL = 'http://localhost:8080/swivel_server_war_exploded/editStub.html';

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
    whenConfigLoaded(function () {
        casper.click('#reset');
        casper.click('#resetOK');
        whenConfigLoaded();
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