"use strict";

define(['jQuery'], function ($) {
    return function (swivelServer, view) {

        var client = this;

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
                    datum.stubs = [];
                    $.each(val.stubs, function (index, item) {
                        datum.stubs.push({path: key, id: item.id, description: item.description});
                    });
                }
                viewData.push(datum);
            });

            view.loadConfigurationData(viewData);
        };

        function clientLoadConfigurationSuccess(data) {
            client.loadConfigurationSuccess(data);
        }

        $(view).one('loaded.swivelView',function () {
            client.loadConfiguration();
        }).on('delete-shunt.swivelView',function (event, shuntData) {
                swivelServer.deleteShunt(shuntData.path)
                    .done(clientLoadConfigurationSuccess);
            }).on('delete-stub.swivelView',function (event, stubData) {
                swivelServer.deleteStub(stubData)
                    .done(clientLoadConfigurationSuccess);
            }).on('delete-path.swivelView', function (event, pathData) {
                swivelServer.deletePath(pathData.path)
                    .done(clientLoadConfigurationSuccess);
            });
    };
});