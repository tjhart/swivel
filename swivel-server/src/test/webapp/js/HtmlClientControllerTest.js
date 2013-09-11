RequireJSTestCase('HtmlClientController tests', {
    HtmlClientController: 'HtmlClientController',
    SwivelServer: 'SwivelServer',
    HtmlClientView: 'HtmlClientView',

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
        this.mockView = mock(this.r.HtmlClientView);

        when(this.mockSwivelServer).getConfig().thenReturn(this.ajaxResult);

        this.client = new this.r.HtmlClientController(this.mockSwivelServer, this.mockView);
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

    'test loadConfiguration done defers to success':function(){
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
    }
});