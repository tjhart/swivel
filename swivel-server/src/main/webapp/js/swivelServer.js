"use strict";

define(['jQuery'], function ($) {
    var SwivelServer = function (baseUrl) {
        this.getConfig = function () {
            return $.ajax({
                url: [baseUrl, this.CONFIG_PATH].join('/'),
                accepts: 'application/json'
            });
        }
    };

    $.extend(SwivelServer.prototype, {
        CONFIG_PATH: 'rest/config'
    });

    return SwivelServer;
});