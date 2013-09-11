"use strict";

define(['jQuery'], function ($) {
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

        this.deleteStub = function (stubData) {
            var path = [baseUrl, this.CONFIG_PATH, 'stub', stubData.path].join('/'), url;
            url = [path, $.param({id: stubData.id})].join('?');
            return $.ajax({
                url: url,
                type: 'DELETE',
                accept: 'application/json'
            });
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});