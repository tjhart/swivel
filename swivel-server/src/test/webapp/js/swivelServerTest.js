RequireJSTestCase('swivel Server Tests', {
    Squire: 'Squire',
    $: 'jQuery',

    jsMockito: 'jsMockito',
    jsHamcrest: 'jsHamcrest'
}, {
    BASE_URL: 'http://server/path',
    setUp: function (queue) {
        this.r.jsHamcrest.Integration.JsTestDriver();
        this.r.jsMockito.Integration.JsTestDriver();

        var that = this, injector;

        function returnAjaxResult() {
            return that.ajaxResult;
        }

        this.ajaxResult = {
            done: mockFunction('done', returnAjaxResult),
            fail: mockFunction('fail', returnAjaxResult),
            always: mockFunction('always', returnAjaxResult)
        };

        this.mockJQuery = {ajax: mockFunction('ajax', returnAjaxResult), extend: this.r.$.extend};

        injector = new this.r.Squire();
        injector.mock('jQuery', this.mockJQuery);

        queue.call('Injecting dependencies', function (callbacks) {
            injector.require(['SwivelServer'], callbacks.add(function (SwivelServer) {
                that.swivelServer = new SwivelServer(that.BASE_URL);
            }));
        });
    },

    'test getConfig calls expected URL on server': function () {
        this.swivelServer.getConfig();

        verify(this.mockJQuery.ajax)(allOf(
            hasMember('url', equalTo(this.BASE_URL + '/rest/config')),
            hasMember('accepts', equalTo('application/json'))
        ));
    },

    'test getConfig returns promise': function () {
        assertThat(this.swivelServer.getConfig(), equalTo(this.ajaxResult));
    }
});