"use strict";

define(['jQuery'], function ($) {

        var data = {
            'some/path': {
                shunt: {remoteURL: 'http://remoteHost/path'},
                stubs: [
                    { id: 1,
                        description: 'simple stub',
                        when: {method: 'GET'},
                        then: {statusCode: 200, reason: 'OK'} },
                    { id: 2,
                        description: 'complicated stub',
                        when: {
                            method: 'PUT',
                            remoteAddress: '127.0.0.1',
                            contentType: 'application/json',
                            content: 'some data' },
                        then: { statusCode: 200,
                            reason: 'OK',
                            contentType: 'application/json',
                            content: '{"key":"val"}'
                        } }
                ] },
            'some/other/path': {
                shunt: {remoteURL: 'http://localhost/path'},
                stubs: [
                    { id: 3,
                        description: 'simple stub',
                        when: {method: 'PUT', script: '(function(){return true;})();'},
                        then: {statusCode: 200, reason: 'OK'} },
                    { id: 4,
                        description: 'complicated stub',
                        when: {
                            method: 'PUT',
                            remoteAddress: '127.0.0.1',
                            contentType: 'application/json',
                            content: 'some data' },
                        then: { method: 'GET',
                            script: '(function(){\n' +
                                '    return responseFactory.createResponse({\n' +
                                '        statusCode:200,\n' +
                                '        reason:"OK"});\n' +
                                '})();'
                        } }
                ] } }, stubCounter = 0;

        $.each(data, function (path, config) {
            stubCounter += config.stubs.length;
        });

        function defaultCallback() {}

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

        return function () {
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
                var shunt = data[shuntData.path] = {shunt: shuntData};
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
            };

            this.getStubs = function (query, callback) {
                var stubs = data[query.path].stubs, ids = [].concat(query.id || []).concat(query.ids || []),
                    result = [], i, id;

                for (i = 0; i < ids.length; i++) {
                    ids[i] = parseInt(ids[i]);
                }
                $.each(stubs, function (index, value) {
                    if (ids.length == 0
                        || ids.indexOf(value.id) > -1) {
                        result.push(value);
                    }
                });

                return ajaxResultBuilder(result)
                    .done(callback || defaultCallback);
            };

            this.editStub = function (stubData, callback) {
                var stubs = data[stubData.path].stubs, i;
                console.log('TestSwivelServer#editStub');
                console.log('stubData:');
                console.log(stubData);
                for (i = 0; i < stubs.length; i++) {
                    if (parseInt(stubs[i].id) === parseInt(stubData.id)) {
                        stubs[i] = stubData;
                    }
                }

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.addStub = function (stubData, callback) {
                console.log('TestSwivelServer#addStub');
                console.log('stubData:');
                console.log(stubData);
                stubCounter = stubCounter + 1;
                stubData.id = stubCounter;
                var stubs = data[stubData.path] = data[stubData.path] || {stubs: []};
                data[stubData.path].stubs.push(stubData);

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            };

            this.loadConfiguration = function (formData, callback) {
                console.log('TestSwivelServer.loadConfiguration');
                console.log('can\'t fake this out in test');
                console.log('formData is');
                console.log(formData);

                return ajaxResultBuilder(data)
                    .done(callback || defaultCallback);
            }
        };
    }
);