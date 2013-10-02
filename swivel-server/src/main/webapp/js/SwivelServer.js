"use strict";

define(['jQuery', 'json2'], function ($, json2) {
    var SwivelServer = function (baseUrl) {
        this.getConfig = function () {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH].join('/'),
                type: 'GET',
                accept: 'application/json'
            });
        };

        this.deleteShunt = function (path) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'shunt', path].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            });
        };

        this.deleteStub = function (stubData, syncronous) {
            var path = [baseUrl, this.CONFIG_PATH, 'stub', stubData.path].join('/'), url;
            url = [path, $.param({id: stubData.id})].join('?');
            return $.ajax({
                url: url,
                type: 'DELETE',
                accept: 'application/json',
                async: !(syncronous || false)
            });
        };

        this.putShunt = function (shuntDescription) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'shunt', shuntDescription.path].join('/'),
                type: 'PUT',
                contentType: 'application/json',
                accept: 'application/json',
                data: json2.stringify({remoteURL: shuntDescription.remoteURL})
            });
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});