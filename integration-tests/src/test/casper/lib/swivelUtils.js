exports.getConfigEntries = function getConfigEntries() {
    return casper.evaluate(function () {return __utils__.findAll('#configRoot ul').length;});
};
