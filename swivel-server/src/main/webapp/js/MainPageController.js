"use strict";

define(['jQuery'], function ($) {
    return function (swivelServer, view) {

        function clientLoadConfigurationSuccess(data) {
            client.loadConfigurationSuccess(data);
        }

        var client = this;

        this.loadConfiguration = function () {
            swivelServer.getConfig(clientLoadConfigurationSuccess);
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

        $(view).one('loaded.swivelView',function () {
            client.loadConfiguration();
        }).on('delete-shunt.swivelView',function (event, shuntData) {
                swivelServer.deleteShunt(shuntData.path, clientLoadConfigurationSuccess);
            }).on('delete-stub.swivelView',function (event, stubData) {
                swivelServer.deleteStub(stubData, clientLoadConfigurationSuccess);
            }).on('delete-path.swivelView',function (event, pathData) {
                swivelServer.deletePath(pathData.path, clientLoadConfigurationSuccess);
            }).on('reset.swivelView',function () {
                swivelServer.reset(clientLoadConfigurationSuccess);
            }).on('stub-info.swivelView',function (event, stubData) {
                document.location = ['editStub.html', $.param({path: stubData.path, id: stubData.id})].join('?');
            }).on('add-shunt.swivelView',function (event, shuntData) {
                swivelServer.putShunt(shuntData, clientLoadConfigurationSuccess);
            }).on('edit-shunt.swivelView', function (event, shuntData) {
                swivelServer.deleteShunt(shuntData.path)
                    .always(function () {
                        swivelServer.putShunt(shuntData, clientLoadConfigurationSuccess);
                    });
            });
    };
});