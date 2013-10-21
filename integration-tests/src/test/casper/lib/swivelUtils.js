exports.getConfigEntries = function getConfigEntries() {
    return casper.evaluate(function () {return $('#configRoot').find('ul').length;});
};
