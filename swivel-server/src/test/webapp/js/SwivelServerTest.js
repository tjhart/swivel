RequireJSTestCase('swivel Server Tests', {
    Squire: 'Squire',
    $: 'jQuery',

    json2: 'json2',
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
            hasMember('accept', equalTo('application/json')),
            hasMember('type', equalTo('GET'))
        ));
    },

    'test getConfig returns promise': function () {
        assertThat(this.swivelServer.getConfig(), equalTo(this.ajaxResult));
    },

    'test deleteShunt calls expected URL on server': function () {
        this.swivelServer.deleteShunt('some/path');

        verify(this.mockJQuery.ajax)(
            allOf(
                hasMember('url', equalTo(this.BASE_URL + '/rest/config/shunt/some/path')),
                hasMember('type', equalTo('DELETE'))
            )
        );
    },

    'test putShunt calls expected URL on server': function () {
        var shuntData = {path: 'local/uri', remoteURI: 'some/remote/uri'};
        this.swivelServer.putShunt(shuntData);

        verify(this.mockJQuery.ajax)(
            allOf(
                hasMember('url', equalTo(this.BASE_URL + '/rest/config/shunt/local/uri')),
                hasMember('type', equalTo('PUT')),
                hasMember('accept', equalTo('application/json')),
                hasMember('contentType', equalTo('application/json')),
                hasMember('data', equalTo(this.r.json2.stringify({remoteURI: shuntData.remoteURI})))
            )
        );
    }
});