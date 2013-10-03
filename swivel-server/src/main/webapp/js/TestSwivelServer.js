define(['jQuery'], function ($) {

        var data = {
            'some/path': {
                shunt: {remoteURL: 'http://remoteHost/path'},
                stubs: [
                    {id: 1},
                    {id: 2}
                ]
            },
            'some/other/path': {
                shunt: {remoteURL: 'http://localhost/path'},
                stubs: [
                    {id: 1},
                    {id: 2}
                ]
            }
        };

        function defaultCallback() {}

        return function () {
            function ajaxResultBuilder(data) {
                return {
                    done: function (handler) {
                        setTimeout(function () {handler(data)}, 0);
                        return this;
                    },

                    fail: function (handler) {
                        return this;
                    },

                    always: function (handler) {
                        setTimeout(function () {handler(data)}, 0);
                        return this;
                    }
                }
            }

            this.getConfig = function (callback) {
                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.deleteShunt = function (path, callback) {
                if (data[path] && data[path].shunt) {
                    delete data[path].shunt;
                    if (!data[path].stubs) {
                        delete data[path];
                    }
                }

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.deleteStub = function (stubData, callback) {
                var path = stubData.path, id = stubData.id, stubs, i, stub;
                if (data[path] && data[path].stubs) {
                    stubs = data[path].stubs;
                    for (i = 0; i < stubs.length; i++) {
                        if (stubs[i].id === id) {
                            stubs.splice(i, 1);
                            break;
                        }
                    }
                    if (stubs.length === 0) {
                        delete data[path].stubs;
                        if (!data[path].shunt) {
                            delete data[path];
                        }
                    }
                }

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.putShunt = function (shuntData, callback) {
                data[shuntData.path].shunt = 'shunting to: ' + shuntData.remoteURL;
                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.deletePath = function (path, callback) {
                var pathRegex = new RegExp(['^', path].join(''));
                $.each(data, function (key, val) {
                    if (key.match(pathRegex)) { delete data[key]; }
                });

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.reset = function (callback) {
                data = {};

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            }
        }
    }
);