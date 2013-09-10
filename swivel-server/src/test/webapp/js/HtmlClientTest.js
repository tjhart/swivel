RequireJSTestCase('HtmlClient tests', {
    HtmlClient: 'HtmlClient',
    SwivelServer: 'SwivelServer',

    $: 'jQuery',
    jsHamcrest: 'jsHamcrest',
    jsMockito: 'jsMockito'
}, {
    setUp: function (queue) {
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

        when(this.mockSwivelServer).getConfig().thenReturn(this.ajaxResult);

        this.client = new this.r.HtmlClient(this.mockSwivelServer);
    },

    'test construction retrieves config from server': function () {
        verify(this.mockSwivelServer).getConfig();
    }

});