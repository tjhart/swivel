RequireJSTestCase('MainPageView tests', {
    MainPageView: 'MainPageView',

    $: 'jQuery',
    jsHamcrest: 'jsHamcrest',
    jsMockito: 'jsMockito'
}, {
    NODE_DATA: [
        {
            path: 'some/path',
            shunt: 'some shunt description',
            stubs: [
                {id: 1, description: 'stub 1'},
                {id: 2, description: 'stub 2'}
            ]
        }
    ],
    setUp: function () {
        /*:DOC +=
         <ul>
         <li class="path">
         <button class="addShunt" title="Add"></button>
         <button class="addStub" title="Add"></button>
         <button class="treeButton delete" title="Delete"></button>
         some/path
         <ul>
         <li class="shunt">
         <button class="treeButton delete" title="Delete"></button>
         shunt: some shunt description
         </li>
         <li class="stubs">
         stubs
         <ul>
         <li class="stub">
         <button class="treeButton delete" title="Delete"></button>
         some stub description
         </li>
         </ul>
         </li>
         </ul>
         </li>
         </ul>


         <form>
         <input id="staticWhen" type="radio" name="whenType"/>
         <input id="scriptWhen" type="radio" name="whenType"/>
         <div class="when">
         <div class="static">
         <label for="method">Method:</label>
         <select id="method" name="method">
         <option value="">(any)</option>
         <option>GET</option>
         <option>PUT</option>
         <option>POST</option>
         <option>DELETE</option>
         </select>
         <label for="contentType">Content Type:</label>
         <input id="contentType" type="text" name="requestContentType"/>
         <label for="remoteAddr">Remote Address:</label>
         <input id="remoteAddr" type="text" name="remoteAddr"/>
         <label for="content">Content:</label>
         <textarea id="content" name="content"></textarea>
         </div>
         <div class="script hidden">
         <textarea name="whenScript"></textarea>
         </div>
         </div>
         <div id="then" class="then">
         <input id="staticThen" type="radio" name="thenType" value="static" checked/>
         <input id="scriptThen" type="radio" name="thenType" value="script"/>
         <div class="static">
         <input id="statusCode" type="text" name="statusCode"/>
         <input id="reason" type="text" name="reason"/>
         <input id="contentType2" type="text" name="responseContentType"/>
         <textarea id="content2" name="content2"></textarea>
         </div>
         <div class="script hidden">
         <textarea name="thenScript"></textarea>
         </div>
         </div>
         </form>         */
        this.r.jsHamcrest.Integration.JsTestDriver();
        this.r.jsMockito.Integration.JsTestDriver();

        this.pathData = {path: 'some/path'};
        this.shuntData = {};
        this.stubData = {};

        this.r.$('.path').data('path-data', this.pathData);
        this.r.$('.stub').data('stub-data', this.stubData);
        this.r.$('.shunt').data('shunt-data', this.shuntData);

        this.mockConfigTree = mock(this.r.$);
        this.mockJQueryObject = mock(this.r.$);

        when(this.mockJQueryObject)
            .find(anything())
            .thenReturn(this.mockJQueryObject);
        when(this.mockJQueryObject)
            .not(anything())
            .thenReturn(this.mockJQueryObject);
        when(this.mockConfigTree)
            .one(anything(), anything())
            .thenReturn(this.mockConfigTree);
        when(this.mockConfigTree)
            .find(anything())
            .thenReturn(this.mockJQueryObject);
        when(this.mockConfigTree)
            .jstree()
            .thenReturn(this.mockConfigTree);

        this.view = new this.r.MainPageView(this.mockConfigTree);
        this.view.$remoteURI = this.mockJQueryObject;
        this.view.targetPath = {path: 'some/path'};
    },

    'test construction configures tree': function () {
        verify(this.mockConfigTree).jstree(allOf(
            hasMember('core', hasMember('html_titles', is(true))),
            hasMember('plugins', allOf(hasItem('json_data'), hasItem('themeroller'))),
            hasMember('json_data',
                hasMember('data', hasItem(allOf(
                    hasMember('data', equalTo('Configuration')),
                    hasMember('state', equalTo('open')),
                    hasMember('attr', hasMember('id', equalTo('configRoot')))
                ))))
        ));
    },

    'test construction listens for loaded.jstree': function () {
        verify(this.mockConfigTree).one('loaded.jstree', typeOf('function'));
    },

    'test loadConfigurationData creates path node': function () {
        var mockPathNode = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        this.view.loadConfigurationData(this.NODE_DATA);

        verify(this.mockConfigTree).jstree('create_node', this.view.rootNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].path)),
            hasMember('state', equalTo('open'))
        ));
    },

    'test loadConfigurationData creates shunt leaf': function () {
        var mockPathNode = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);
        this.view.loadConfigurationData(this.NODE_DATA);

        verify(this.mockConfigTree).jstree('create_node', mockPathNode, 'inside', allOf(
            hasMember('data', containsString('shunt: ' + this.NODE_DATA[0].shunt)),
            hasMember('attr', hasMember('class', equalTo('shunt')))
        ));
    },

    'test loadConfigurationData creates stub nodes': function () {
        var mockPathNode = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(this.NODE_DATA);
        verify(this.mockConfigTree).jstree('create_node', mockPathNode, 'last', allOf(
            hasMember('data', equalTo('stubs')),
            hasMember('state', equalTo('open')),
            hasMember('attr', hasMember('class', equalTo('stubs')))
        ));
    },

    'test loadConfigurationData creates stub leafs': function () {
        var mockPathNode = mock(this.r.$), mockStubsNode = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', this.view.rootNode, anything(), anything())
            .thenReturn(mockPathNode);
        when(this.mockConfigTree).jstree('create_node', mockPathNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(this.mockConfigTree).jstree('create_node', mockStubsNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(this.NODE_DATA);
        verify(this.mockConfigTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].stubs[0].description)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
        verify(this.mockConfigTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].stubs[1].description)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
    },

    'test shunt delete button triggers as expected': function () {
        var $deleteShuntButton = this.r.$('.shunt button.delete'), triggeredData;

        when(this.mockConfigTree)
            .find('.shunt button.delete')
            .thenReturn($deleteShuntButton);
        this.view.addClickEvents();
        this.r.$(this.view).one('delete-shunt.swivelView', function (event, data) {
            triggeredData = data;
        });

        $deleteShuntButton.click();

        assertThat(triggeredData, equalTo(this.shuntData));
    },

    'test stub delete button triggers as expected': function () {
        var $deleteStubButton = this.r.$('.stub button.delete'), triggeredData;

        when(this.mockConfigTree)
            .find('.stub button.delete')
            .thenReturn($deleteStubButton);
        this.view.addClickEvents();
        this.r.$(this.view).one('delete-stub.swivelView', function (event, data) {
            triggeredData = data;
        });

        $deleteStubButton.click();

        assertThat(triggeredData, equalTo(this.stubData));
    },

    'test delete path button triggers as expected': function () {
        var $deletePathButton = this.r.$('.path button.delete'), triggeredData;

        when(this.mockConfigTree)
            .find('.path button.delete')
            .thenReturn($deletePathButton);
        this.view.addClickEvents();
        this.r.$(this.view).one('delete-path.swivelView', function (event, data) {
            triggeredData = data;
        });

        $deletePathButton.click();

        assertThat(triggeredData, equalTo(this.pathData));
    }
});