"use strict";

define(['test/lib/Squire', 'jQuery', 'json2', 'jsHamcrest', 'jsMockito'],
    function (Squire, $, json2, jsHamcrest, jsMockito) {
        var BASE_URL = 'http://server/path', injector = new Squire(), mockJQuery;

        jsHamcrest.Integration.QUnit();
        jsMockito.Integration.QUnit();

        mockJQuery = {param: $.param};

        injector.mock('jQuery', mockJQuery);
        injector.require(['SwivelServer'], function (SwivelServer) {
            var ajaxResult;

            function returnAjaxResult() { return ajaxResult; }

            module('SwivelServer tests', {
                setup: function () {
                    this.swivelServer = new SwivelServer(BASE_URL);

                    //reset ajax for each test
                    mockJQuery.ajax = mockFunction('ajax', returnAjaxResult);

                    ajaxResult = {
                        done: mockFunction('done', returnAjaxResult),
                        fail: mockFunction('fail', returnAjaxResult),
                        always: mockFunction('always', returnAjaxResult)
                    };
                }
            });

            test('getConfig calls expected URL on server', 0, function () {
                this.swivelServer.getConfig();

                verify(mockJQuery.ajax)(allOf(
                    hasMember('url', equalTo(BASE_URL + '/rest/config')),
                    hasMember('accept', equalTo('application/json')),
                    hasMember('type', equalTo('GET'))
                ));
            });

            test('deleteShunt calls expected URL on server', 0, function () {
                this.swivelServer.deleteShunt('some/path');

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/shunt/some/path')),
                        hasMember('type', equalTo('DELETE'))
                    )
                );
            });

            test('deleteStub calls expected URL', 0, function () {
                this.swivelServer.deleteStub({id: 1, path: 'some/path'});

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/stub/some/path?id=1')),
                        hasMember('type', equalTo('DELETE')),
                        hasMember('accept', equalTo('application/json'))
                    )
                );
            });

            test('putShunt calls expected URL on server', 0, function () {
                var shuntData = {path: 'local/uri', remoteURL: 'http://some/remote/url'};
                this.swivelServer.putShunt(shuntData);

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/shunt/local/uri')),
                        hasMember('type', equalTo('PUT')),
                        hasMember('accept', equalTo('application/json')),
                        hasMember('contentType', equalTo('application/json')),
                        hasMember('data', equalTo(json2.stringify({remoteURL: shuntData.remoteURL})))
                    )
                );
            });

            test('deletePath calls expected URL', 0, function () {
                this.swivelServer.deletePath('some/path');

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/path/some/path')),
                        hasMember('type', equalTo('DELETE')),
                        hasMember('accept', equalTo('application/json'))
                    )
                )
            });

            test('reset calls expected URL', 0, function () {
                this.swivelServer.reset();

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config')),
                        hasMember('type', equalTo('DELETE')),
                        hasMember('accept', equalTo('application/json'))
                    )
                );
            });

            test('getStubs calls expected URL', 0, function () {
                this.swivelServer.getStubs({path: 'some/path', ids: [1, 2, 3]});

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/stub/some/path?' + $.param({ids: [1, 2, 3]}))),
                        hasMember('type', equalTo('GET')),
                        hasMember('accept', equalTo('application/json'))
                    )
                );
            });

            test('editStub calls expected URL', 0, function () {
                var stubData = {path: 'some/path', id: 1};
                this.swivelServer.editStub(stubData);

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/stub/some/path/1')),
                        hasMember('type', equalTo('PUT')),
                        hasMember('contentType', equalTo('application/json')),
                        hasMember('data', equalTo(json2.stringify(stubData)))
                    )
                );
            });

            test('addStub calls expected URL', 0, function () {
                var stubData = {path: 'some/path'};
                this.swivelServer.addStub(stubData);

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config/stub/some/path')),
                        hasMember('type', equalTo('POST')),
                        hasMember('contentType', equalTo('application/json')),
                        hasMember('data', equalTo(json2.stringify(stubData)))
                    )
                );
            });

            test('loadConfiguration calls expected URL', 0, function () {
                var data = {};
                this.swivelServer.loadConfiguration(data);

                verify(mockJQuery.ajax)(
                    allOf(
                        hasMember('url', equalTo(BASE_URL + '/rest/config')),
                        hasMember('type', equalTo('PUT')),
                        hasMember('contentType', equalTo('application/json')),
                        hasMember('processData', is(false)),
                        hasMember('data', equalTo(data))
                    )
                );
            });

            test('all server methods pass callback to done', 0, function () {
                var SINGLE_ARG_METHODS = ['getConfig', 'reset'], that = this;

                function myCallback() {}

                $.each(this.swivelServer, function (key) {
                    var args = [
                        {},
                        myCallback
                    ];
                    if (that.swivelServer.hasOwnProperty(key)) {
                        ajaxResult.done = mockFunction('done', returnAjaxResult);
                        if (SINGLE_ARG_METHODS.indexOf(key) > -1) {
                            args = [myCallback];
                        }
                        that.swivelServer[key].apply(that.swivelServer, args);

                        verify(ajaxResult.done)(myCallback);
                    }
                });
            });

            test('all server methods return promise', function () {
                var that = this;
                $.each(this.swivelServer, function (key) {
                    if (that.swivelServer.hasOwnProperty(key)) {
                        ajaxResult.done = mockFunction('done', returnAjaxResult);
                        assertThat(that.swivelServer[key]({}), equalTo(ajaxResult));
                    }
                });
            });
        });
    });
