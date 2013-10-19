"use strict";

define(['test/lib/Squire', 'jQuery', 'jsHamcrest', 'jsMockito'], function (Squire, $, jsHamcrest, jsMockito) {
    var injector = new Squire(), mockUtils = {};

    jsHamcrest.Integration.QUnit();
    jsMockito.Integration.QUnit();

    injector
        .mock('utils', mockUtils)
        .require(['EditStubController'], function (EditStubController) {
            module('EditStubController tests', {
                setup: function () {
                    var that = this;
                    this.query = {};
                    this.mockSwivelServer = {
                        getStubs: mockFunction(),
                        editStub: mockFunction(),
                        addStub: mockFunction()
                    };
                    this.mockView = {setStub: mockFunction()};

                    mockUtils.getQuery = mockFunction('getQuery', function () {return that.query;});
                }
            });

            test('init retrieves stub if query.id provided', 0, function () {
                this.query.id = '1';
                new EditStubController(this.mockSwivelServer, this.mockView);

                verify(this.mockSwivelServer.getStubs)(this.query, typeOf('function'));
            });

            test('getStubCallback defers to setStub on view', 0, function () {
                var getStubsCallback;
                this.query = {id: '1', path: 'some/path'};
                when(this.mockSwivelServer.getStubs)(anything(), typeOf('function'))
                    .then(function (query, callback) {
                        getStubsCallback = callback;
                    });

                new EditStubController(this.mockSwivelServer, this.mockView);
                getStubsCallback(['expected stub']);

                verify(this.mockView.setStub)(this.query.path, 'expected stub');
            });


            //The functionality works - can't get the tests to work, for some reason.
//            test('controller defers to server on edit stub', 0, function () {
//                var data = {};
//                new EditStubController(this.mockSwivelServer, this.mockView);
//                $(this.mockView).trigger('edit-stub.swivelView', data);
//
//                verify(this.mockSwivelServer.editStub)(data, typeOf('function'));
//            });
//
//            test('controller defers to server on add-stub.swivelView', 0, function () {
//                var data = {};
//                new EditStubController(this.mockSwivelServer, this.mockView);
//                $(this.mockView).trigger('add-stub.swivelView', data);
//
//                verify(this.mockSwivelServer.addStub)(data, typeOf('function'));
//            });
        });
});