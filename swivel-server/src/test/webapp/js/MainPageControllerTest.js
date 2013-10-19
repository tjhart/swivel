define(['MainPageController', 'SwivelServer', 'MainPageView', 'jQuery', 'jsHamcrest', 'jsMockito'],
    function (MainPageController, SwivelServer, MainPageView, $, jsHamcrest, jsMockito) {
        jsHamcrest.Integration.QUnit();
        jsMockito.Integration.QUnit();

        var SERVER_DATA = {
            'some/path': {shunt: 'shunt description',
                stubs: [
                    {id: 1, description: 'stub 1'},
                    {id: 2, description: 'stub2'}
                ]}};

        module('MainPageController tests', {
            setup: function () {
                this.mockSwivelServer = mock(SwivelServer);
                this.mockView = mock(MainPageView);
                this.controller = new MainPageController(this.mockSwivelServer, this.mockView);
                $('#qunit-fixture').append($('<div class="currentConfig"></div>'));
            }
        });

        asyncTest('listens for view to load', 0, function () {
            this.controller.loadConfiguration = mockFunction();

            $(this.mockView).trigger('loaded.swivelView');

            verify(this.controller.loadConfiguration)();
            start();
        });

        asyncTest('loadConfiguration defers to server', 0, function () {
            this.controller.loadConfiguration();

            verify(this.mockSwivelServer).getConfig();
            start();
        });

        asyncTest('loadConfiguration done defers to success', 0, function () {
            var doneHandler;
            when(this.mockSwivelServer)
                .getConfig(typeOf('function'))
                .then(function (handler) {
                    doneHandler = handler;
                    return this;
                });

            this.controller.loadConfigurationSuccess = mockFunction();

            this.controller.loadConfiguration();
            doneHandler('data');

            verify(this.controller.loadConfigurationSuccess)('data');
            start();
        });

        asyncTest('getConfig done defers to view to load the data', 0, function () {
            this.controller.loadConfigurationSuccess(SERVER_DATA);

            verify(this.mockView).loadConfigurationData(
                hasItem(
                    allOf(
                        hasMember('path', equalTo('some/path')),
                        hasMember('shunt', equalTo(SERVER_DATA['some/path'].shunt)),
                        hasMember('stubs', allOf(
                            hasItem(allOf(
                                hasMember('id', equalTo(SERVER_DATA['some/path'].stubs[0].id)),
                                hasMember('description', equalTo(SERVER_DATA['some/path'].stubs[0].description))
                            )),
                            hasItem(allOf(
                                hasMember('id', equalTo(SERVER_DATA['some/path'].stubs[1].id)),
                                hasMember('description', equalTo(SERVER_DATA['some/path'].stubs[1].description))
                            ))
                        ))
                    )
                ));

            start();
        });

        asyncTest('delete shunt listener defers to server', 0, function () {
            var shuntData = {path: 'some/path'};
            $(this.mockView).trigger('delete-shunt.swivelView', shuntData);

            verify(this.mockSwivelServer).deleteShunt('some/path');
            start();
        });

        asyncTest('delete shunt success loads configuration', 0, function () {
            var doneHandler;
            this.controller.loadConfigurationSuccess = mockFunction();
            when(this.mockSwivelServer).deleteShunt(anything(), typeOf('function')).then(function (path, handler) {
                doneHandler = handler;
                return this;
            });

            $(this.mockView).trigger('delete-shunt.swivelView', {path: 'some/path'});
            doneHandler('data');

            verify(this.controller.loadConfigurationSuccess)('data');
            start();
        });

        asyncTest('delete stub listener defers to server', 0, function () {
            var stubData = {path: 'some/path', id: 1};
            $(this.mockView).trigger('delete-stub.swivelView', stubData);

            verify(this.mockSwivelServer).deleteStub(stubData);
            start();
        });

        asyncTest('delete stub success loads configuration', 0, function () {
            var doneHandler;
            this.controller.loadConfigurationSuccess = mockFunction();
            when(this.mockSwivelServer).deleteStub(anything(), typeOf('function')).then(function (stubData, handler) {
                doneHandler = handler;
                return this;
            });

            $(this.mockView).trigger('delete-stub.swivelView', {path: 'some/path'});
            doneHandler('data');

            verify(this.controller.loadConfigurationSuccess)('data');
            start();
        });

        asyncTest('delete path listener deletes path', 0, function () {
            var doneHandler, pathData = {path: 'some/path', stubs: [
                {path: 'some/path', id: 1}
            ]};

            this.controller.loadConfigurationSuccess = mockFunction();
            when(this.mockSwivelServer).deletePath(anything(), typeOf('function')).then(function (path, handler) {
                doneHandler = handler;
            });

            $(this.mockView).trigger('delete-path.swivelView', pathData);

            verify(this.mockSwivelServer).deletePath(pathData.path);

            doneHandler('data');
            verify(this.controller.loadConfigurationSuccess)('data');
            start();
        });

    });

