"use strict";

define(['jQuery'], function ($) {
    return function (swivelServer, view) {

        var client = this;

        $(view).one('loaded.swivelView', function () {
            client.loadConfiguration();
        });

        this.loadConfiguration = function () {
            swivelServer.getConfig()
                .done(function (data) {
                    client.loadConfigurationSuccess(data);
                });
        };

        this.loadConfigurationSuccess = function (data) {
            var keys = [], viewData = [];

            $.each(data, function (key) {
                keys.push(key);
            });

            keys.sort();
            $.each(keys, function (index, key) {
                var datum = {path: key}, val = data[key];
                if (val.shunt) {
                    datum.shunt = val.shunt;
                }
                if (val.stubs) {
                    datum.stubs = val.stubs;
                }
                viewData.push(datum);
            });

            view.loadConfigurationData(viewData);
        }
    };
});