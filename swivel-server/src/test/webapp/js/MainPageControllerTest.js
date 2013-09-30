RequireJSTestCase('MainPageController tests', {
    MainPageController: 'MainPageController',
    SwivelServer: 'SwivelServer',
    MainPageView: 'MainPageView',

    $: 'jQuery',
    jsHamcrest: 'jsHamcrest',
    jsMockito: 'jsMockito'
}, {
    SERVER_DATA: {
        'some/path': {shunt: 'shunt description',
            stubs: [
                {id: 1, description: 'stub 1'},
                {id: 2, description: 'stub2'}
            ]}},
    setUp: function () {
        /*:DOC +=
         <div class="currentConfig"></div>
         */

        this.r.jsHamcrest.Integration.JsTestDriver();
        this.r.jsMockito.Integration.JsTestDriver();

        var that = this;

        function returnAjaxResult() { return that.ajaxResult; }

        this.ajaxResult = {
            done: mockFunction('done', returnAjaxResult),
            fail: mockFunction('fail', returnAjaxResult),
            always: mockFunction('always', returnAjaxResult)
        };

        this.mockSwivelServer = mock(this.r.SwivelServer);
        this.mockView = mock(this.r.MainPageView);

        when(this.mockSwivelServer).getConfig().thenReturn(this.ajaxResult);
        when(this.mockSwivelServer).deleteShunt().thenReturn(this.ajaxResult);
        when(this.mockSwivelServer).deleteStub().thenReturn(this.ajaxResult);
        when(this.mockSwivelServer).putShunt().thenReturn(this.ajaxResult);

        this.client = new this.r.MainPageController(this.mockSwivelServer, this.mockView);
    },

    'test listens for view load': function () {
        this.client.loadConfiguration = mockFunction();

        $(this.mockView).trigger('loaded.swivelView');

        verify(this.client.loadConfiguration)();
    },

    'test loadConfiguration defers to server': function () {
        this.client.loadConfiguration();

        verify(this.mockSwivelServer).getConfig();
    },

    'test loadConfiguration done defers to success': function () {
        var doneHandler;
        when(this.ajaxResult.done)(typeOf('function')).then(function (handler) {
            doneHandler = handler;
            return this;
        });

        this.client.loadConfigurationSuccess = mockFunction();

        this.client.loadConfiguration();
        doneHandler('data');

        verify(this.client.loadConfigurationSuccess)('data');
    },

    'test getConfig done defers to view to load the data': function () {
        this.client.loadConfigurationSuccess(this.SERVER_DATA);

        verify(this.mockView).loadConfigurationData(
            hasItem(
                allOf(
                    hasMember('path', equalTo('some/path')),
                    hasMember('shunt', equalTo(this.SERVER_DATA['some/path'].shunt)),
                    hasMember('stubs', allOf(
                        hasItem(allOf(
                            hasMember('id', equalTo(this.SERVER_DATA['some/path'].stubs[0].id)),
                            hasMember('description', equalTo(this.SERVER_DATA['some/path'].stubs[0].description))
                        )),
                        hasItem(allOf(
                            hasMember('id', equalTo(this.SERVER_DATA['some/path'].stubs[1].id)),
                            hasMember('description', equalTo(this.SERVER_DATA['some/path'].stubs[1].description))
                        ))
                    ))
                )
            ));
    },

    'test delete shunt listener defers to server': function () {
        var shuntData = {path: 'some/path'};
        this.r.$(this.mockView).trigger('delete-shunt.swivelView', shuntData);

        verify(this.mockSwivelServer).deleteShunt('some/path');
    },

    'test delete shunt success loads configuration': function () {
        var doneHandler;
        this.client.loadConfigurationSuccess = mockFunction();
        when(this.ajaxResult.done)(typeOf('function')).then(function (handler) {
            doneHandler = handler;
            return this;
        });

        this.r.$(this.mockView).trigger('delete-shunt.swivelView', {path: 'some/path'});
        doneHandler('data');

        verify(this.client.loadConfigurationSuccess)('data');
    },

    'test delete stub listener defers to server': function () {
        var stubData = {path: 'some/path', id: 1};
        this.r.$(this.mockView).trigger('delete-stub.swivelView', stubData);

        verify(this.mockSwivelServer).deleteStub(stubData);
    },

    'test delete stub success loads configuration': function () {
        var doneHandler;
        this.client.loadConfigurationSuccess = mockFunction();
        when(this.ajaxResult.done)(typeOf('function')).then(function (handler) {
            doneHandler = handler;
            return this;
        });

        this.r.$(this.mockView).trigger('delete-stub.swivelView', {path: 'some/path'});
        doneHandler('data');

        verify(this.client.loadConfigurationSuccess)('data');
    },

    'test delete path listener deletes paths and stubs': function () {
        var doneHandler, pathData = {path: 'some/path', stubs: [
            {path: 'some/path', id: 1}
        ]};

        this.client.loadConfigurationSuccess = mockFunction();
        when(this.ajaxResult.done)(typeOf('function')).then(function (handler) {
            doneHandler = handler;
        });

        this.r.$(this.mockView).trigger('delete-path.swivelView', pathData);

        verify(this.mockSwivelServer).deleteStub(pathData.stubs[0]);
        verify(this.mockSwivelServer).deleteShunt(pathData.path);

        doneHandler('data');
        verify(this.client.loadConfigurationSuccess)('data');
    }
});