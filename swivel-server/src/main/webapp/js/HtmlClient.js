"use strict";

define(['jQuery', 'jsTree'], function ($) {
    var HtmlClient = function (swivelServer) {

        var client = this;

        this.updateConfig = function (data) {

        };

        this.configTree = $('.currentConfig').jstree({
            core: {},
            plugins: ['themes', 'json_data'],
            json_data: {data: [
                {data: 'Root Node'}
            ]}
        });
        swivelServer.getConfig()
            .done(function (data) {
                client.updateConfig(data);
            });
    };

    return HtmlClient;
});