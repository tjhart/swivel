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
                type: 'DELETE'
            });
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});