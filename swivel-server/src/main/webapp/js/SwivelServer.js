"use strict";

define(['jQuery', 'json2'], function ($, json2) {
    function defaultCallback() {}

    var CONFIG_PATH = 'rest/config';

    return function (baseUrl) {
        this.getConfig = function (callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH].join('/'),
                type: 'GET',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.deleteShunt = function (path, callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH, 'shunt', path].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.deleteStub = function (stubData, callback) {
            var path = [baseUrl, CONFIG_PATH, 'stub', stubData.path].join('/'), url;
            url = [path, $.param({id: stubData.id})].join('?');
            return $.ajax({
                url: url,
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.putShunt = function (shuntDescription, callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH, 'shunt', shuntDescription.path].join('/'),
                type: 'PUT',
                contentType: 'application/json',
                accept: 'application/json',
                data: json2.stringify({remoteURL: shuntDescription.remoteURL})
            }).done(callback || defaultCallback);
        };

        this.deletePath = function (path, callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH, 'path', path].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.reset = function (callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH].join('/'),
                type: 'DELETE',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        this.getStubs = function (query, callback) {
            var url = [baseUrl, CONFIG_PATH, 'stub', query.path].join('/');
            url = [url, $.param({ids: [].concat(query.id || []).concat(query.ids || [])})].join('?');
            return $.ajax({
                url: url,
                type: 'GET',
                accept: 'application/json'
            }).done(callback || defaultCallback);
        };

        function configureOpts(stubData, opts) {
            if (stubData instanceof FormData) {
                opts.processData = false;
                opts.contentType = false;
                opts.data = stubData;
            } else {
                opts.contentType = 'application/json';
                opts.data = json2.stringify(stubData);
            }
        }

        this.editStub = function (stubData, callback) {
            var opts = {
                url: [baseUrl, CONFIG_PATH, 'stub', stubData.path, stubData.id].join('/'),
                type: 'PUT'
            };
            configureOpts(stubData.formData || stubData, opts);
            return $.ajax(opts)
                .done(callback || defaultCallback);
        };

        this.addStub = function (stubData, callback) {
            var opts = {
                url: [baseUrl, CONFIG_PATH, 'stub', stubData.path].join('/'),
                type: 'POST'
            };
            configureOpts(stubData.formData || stubData, opts);
            return $.ajax(opts)
                .done(callback || defaultCallback);
        };

        this.loadConfiguration = function (config, callback) {
            return $.ajax({
                url: [baseUrl, CONFIG_PATH].join('/'),
                type: 'PUT',
                contentType: 'application/json',
                processData: false,
                data: config
            })
                .done(callback || defaultCallback);
        }
    };
});