"use strict";

define(['jQuery', 'json2'], function ($, json2) {
    function defaultCallback() {}

    var SwivelServer = function (baseUrl) {
        this.getConfig = function (callback) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH].join('/'),
                type: 'GET',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.deleteShunt = function (path, callback) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'shunt', path].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.deleteStub = function (stubData, callback) {
            var path = [baseUrl, this.CONFIG_PATH, 'stub', stubData.path].join('/'), url;
            url = [path, $.param({id: stubData.id})].join('?');
            return $.ajax({
                url: url,
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.putShunt = function (shuntDescription, callback) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'shunt', shuntDescription.path].join('/'),
                type: 'PUT',
                contentType: 'application/json',
                accept: 'application/json',
                data: json2.stringify({remoteURL: shuntDescription.remoteURL})
            }).done(callback || defaultCallback);
        };

        this.deletePath = function (path, callback) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'path', path].join('/'),
                type: 'DELETE',
                contentType: 'application/json',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.reset = function (callback) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});