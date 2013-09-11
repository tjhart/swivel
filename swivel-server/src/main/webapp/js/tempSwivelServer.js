"use strict";

define(['jQuery'], function ($) {
    var SwivelServer = function (baseUrl) {
        this.getConfig = function () {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH].join('/'),
                method: 'GET',
                accept: 'application/json'
            });
        };

        this.deleteShunt = function (path) {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH, 'shunt', path].join('/'),
                method: 'DELETE'
            });
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});