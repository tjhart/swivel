RequireJSTestCase('HtmlClientView tests', {
    HtmlClientView: 'HtmlClientView',

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
         <ins>&nbsp;</ins>
         <a href="#">
         <ins>&nbsp;</ins>
         <button class="treeButton add" title="Add"></button>
         <button class="treeButton delete" title="Delete"></button>
         some/path
         </a>
         <ul>
         <li class="shunt">
         <ins>&nbsp;</ins>
         <a href="#">
         <ins>&nbsp;</ins>
         <button class="treeButton delete" title="Delete"></button>
         shunt: some shunt description
         </a>
         </li>
         <li class="stubs">
         <ins>&nbsp;</ins>
         <a href="#">
         <ins>&nbsp;</ins>
         stubs
         </a>
         <ul>
         <li class="stub">
         <ins>&nbsp;</ins>
         <a href="#">
         <ins>&nbsp;</ins>
         <button class="treeButton delete" title="Delete"></button>
         some stub description</a>
         </li>
         </ul>
         </li>
         </ul>
         </li>
         </ul>
         */
        this.r.jsHamcrest.Integration.JsTestDriver();
        this.r.jsMockito.Integration.JsTestDriver();

        this.pathData = {path: 'some/path'};
        this.shuntData = {};
        this.stubData = {};

        this.r.$('.path').data('path-data', this.pathData);
        this.r.$('.stub').data('stub-data', this.stubData);
        this.r.$('.shunt').data('shunt-data', this.shuntData);

        this.mockAddElementDialog = mock(this.r.$);
        this.mockConfigTree = mock(this.r.$);
        this.mockJQueryObject = mock(this.r.$);

        when(this.mockJQueryObject)
            .find(anything())
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

        this.view = new this.r.HtmlClientView(this.mockConfigTree, this.mockAddElementDialog);
        this.view.$addElementForm = this.mockJQueryObject;
        this.view.targetPath = {path: 'some/path'};
    },

    'test construction configures tree': function () {
        verify(this.mockConfigTree).jstree(allOf(
            hasMember('core', hasMember('html_titles', is(true))),
            hasMember('plugins', allOf(hasItem('themes'), hasItem('json_data'))),
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
        this.r.$(this.view).one('delete-shunt.swivelView', function(event, data){
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
        this.r.$(this.view).one('delete-stub.swivelView', function(event, data){
            triggeredData = data;
        });

        $deleteStubButton.click();

        assertThat(triggeredData, equalTo(this.stubData));
    },

    'test delete path button triggers as expected':function(){
        var $deletePathButton = this.r.$('.path button.delete'), triggeredData;

        when(this.mockConfigTree)
            .find('.path button.delete')
            .thenReturn($deletePathButton);
        this.view.addClickEvents();
        this.r.$(this.view).one('delete-path.swivelView', function(event, data){
            triggeredData = data;
        });

        $deletePathButton.click();

        assertThat(triggeredData, equalTo(this.pathData));
    },

    'test construction configures addElement dialog': function () {
        verify(this.mockAddElementDialog).dialog(allOf(
            hasMember('autoOpen', is(false)),
            hasMember('modal', is(true)),
            hasMember('draggable', is(false)),
            hasMember('resizable', is(false)),
            hasMember('width', is(600))
        ));
    },

    'test addElementDialog cancel button closes dialog': function () {
        var dialogButtons;
        when(this.mockAddElementDialog).dialog(anything()).then(function (opts) {
            dialogButtons = opts.buttons;
            return this;
        });

        new this.r.HtmlClientView(this.mockConfigTree, this.mockAddElementDialog);
        dialogButtons[0].click();

        verify(this.mockAddElementDialog).dialog('close');
    },

    'test addElementDialog add defers to addElement': function () {
        var dialogButtons;
        when(this.mockAddElementDialog).dialog(anything()).then(function (opts) {
            dialogButtons = opts.buttons;
            return this;
        });

        var view = new this.r.HtmlClientView(this.mockConfigTree, this.mockAddElementDialog);
        view.addElement = mockFunction();
        dialogButtons[1].click();

        verify(view.addElement)();
    },

    'test addElement closes dialog': function () {
        this.view.addElement();

        verify(this.mockAddElementDialog).dialog('close');
    },

    'test addElement triggers add-shunt event': function () {
        var addShuntTriggered = false;
        this.r.$(this.view).one('add-shunt.swivelView', function () {
            addShuntTriggered = true;
        });

        this.view.addElement();

        assertThat(addShuntTriggered, is(true));
    },

    'test addElement trigger includes expected object': function () {
        var eventObject;
        this.r.$(this.view).one('add-shunt.swivelView', function (event, shuntDescription) {
            eventObject = shuntDescription;
        });

        when(this.mockJQueryObject).val()
            .thenReturn('some/remote/uri');
        this.view.addElement();

        assertThat(eventObject, allOf(
            hasMember('path', equalTo(this.view.targetPath.path)),
            hasMember('remoteURI', equalTo('some/remote/uri'))
        ));
    },

    'test addElement clears remoteURI field': function () {
        this.view.addElement();

        verify(this.mockJQueryObject).val('');
    },

    'test path add event handler sets target path': function () {
        var $pathAddButton = this.r.$('.path button.add');

        when(this.mockConfigTree).find('.path button.add')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        assertThat(this.view.targetPath, equalTo(this.pathData));
    },

    'test path add event handler sets dialog title':function(){
        var $pathAddButton = this.r.$('.path button.add');

        when(this.mockConfigTree).find('.path button.add')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        verify(this.mockAddElementDialog).dialog('option',
            hasMember('title', containsString(this.pathData.path)));
    },

    'test path add event handler opens dialog':function(){
        var $pathAddButton = this.r.$('.path button.add');

        when(this.mockConfigTree).find('.path button.add')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        verify(this.mockAddElementDialog).dialog('open');
    }
});