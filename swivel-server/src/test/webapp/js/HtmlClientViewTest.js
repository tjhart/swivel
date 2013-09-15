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
         <div class="currentConfig"></div>
         */

        this.r.jsHamcrest.Integration.JsTestDriver();
        this.r.jsMockito.Integration.JsTestDriver();

        var that = this;

        this.mockAddElementDialog = mock(this.r.$);
        this.mockJQueryObject = mock(this.r.$);

        when(this.mockJQueryObject)
            .find(anything())
            .thenReturn(this.mockJQueryObject);
        this.view = new this.r.HtmlClientView(this.mockAddElementDialog);
        this.view.$addElementForm = this.mockJQueryObject;
        this.view.targetPath = {path: 'some/path'};
        $(this.view).on('loaded.swivelView', function () {
            that.loadTriggered = true;
        });
    },

    'test construction configures tree': function () {
        assertThat(this.view.configTree.hasClass('jstree'), is(true));
    },

    'test construction triggers loadedEvent': function () {
        assertThat(this.loadTriggered, is(true));
    },

    'test loadConfigurationData creates path node': function () {
        var mockPathNode = mock(this.r.$);
        this.view.configTree = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.view.configTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(this.view.configTree).find(anything()).thenReturn(this.mockJQueryObject);
        this.view.loadConfigurationData(this.NODE_DATA);

        verify(this.view.configTree).jstree('create_node', this.view.rootNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].path)),
            hasMember('state', equalTo('open'))
        ));
    },

    'test loadConfigurationData creates shunt leaf': function () {
        var mockPathNode = mock(this.r.$);
        this.view.configTree = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.view.configTree).find(anything()).thenReturn(this.mockJQueryObject);
        when(this.view.configTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);
        this.view.loadConfigurationData(this.NODE_DATA);

        verify(this.view.configTree).jstree('create_node', mockPathNode, 'inside', allOf(
            hasMember('data', containsString('shunt: ' + this.NODE_DATA[0].shunt)),
            hasMember('attr', hasMember('class', equalTo('shunt')))
        ));
    },

    'test loadConfigurationData creates stub nodes': function () {
        var mockPathNode = mock(this.r.$);
        this.view.configTree = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.view.configTree).find(anything()).thenReturn(this.mockJQueryObject);
        when(this.view.configTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(this.NODE_DATA);
        verify(this.view.configTree).jstree('create_node', mockPathNode, 'last', allOf(
            hasMember('data', equalTo('stubs')),
            hasMember('state', equalTo('open')),
            hasMember('attr', hasMember('class', equalTo('stubs')))
        ));
    },

    'test loadConfigurationData creates stub leafs': function () {
        var mockPathNode = mock(this.r.$), mockStubsNode = mock(this.r.$);
        this.view.configTree = mock(this.r.$);
        this.view.addClickEvents = mockFunction();

        when(this.view.configTree).find(anything()).thenReturn(this.mockJQueryObject);
        when(this.view.configTree).jstree('create_node', this.view.rootNode, anything(), anything())
            .thenReturn(mockPathNode);
        when(this.view.configTree).jstree('create_node', mockPathNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(this.view.configTree).jstree('create_node', mockStubsNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(this.NODE_DATA);
        verify(this.view.configTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].stubs[0].description)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
        verify(this.view.configTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(this.NODE_DATA[0].stubs[1].description)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
    },

    'test deleteShunt click triggers delete': function () {
        var deleteTriggered;
        this.view.loadConfigurationData(this.NODE_DATA);

        $(this.view).one('delete-shunt.swivelView', function (event, shunt) {
            deleteTriggered = true;
        });

        this.view.configTree.find('.shunt:first').find('button').click();
        assertThat(deleteTriggered, is(true));
    },

    'test deleteShunt sends related shunt data': function () {
        var shuntData;
        this.view.loadConfigurationData(this.NODE_DATA);

        $(this.view).one('delete-shunt.swivelView', function (event, shunt) {
            shuntData = shunt;
        });

        this.view.configTree.find('.shunt:first').find('button').click();
        assertThat(shuntData, equalTo(this.NODE_DATA[0]));
    },

    'test deleteStub click triggers delete': function () {
        var deleteTriggered;
        this.view.loadConfigurationData(this.NODE_DATA);

        $(this.view).one('delete-stub.swivelView', function (event, stub) {
            deleteTriggered = true;
        });

        this.view.configTree.find('.stub:first').find('button').click();
        assertThat(deleteTriggered, is(true));
    },

    'test deleteStub sends related shunt data': function () {
        var stubData;
        this.view.loadConfigurationData(this.NODE_DATA);

        $(this.view).one('delete-stub.swivelView', function (event, stub) {
            stubData = stub;
        });

        this.view.configTree.find('.stub:first').find('button').click();
        assertThat(stubData, equalTo(this.NODE_DATA[0].stubs[0]));
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

        new this.r.HtmlClientView(this.mockAddElementDialog);
        dialogButtons[0].click();

        verify(this.mockAddElementDialog).dialog('close');
    },

    'test addElementDialog add defers to addElement': function () {
        var dialogButtons;
        when(this.mockAddElementDialog).dialog(anything()).then(function (opts) {
            dialogButtons = opts.buttons;
            return this;
        });

        var view = new this.r.HtmlClientView(this.mockAddElementDialog);
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
        $(this.view).one('add-shunt.swivelView', function () {
            addShuntTriggered = true;
        });

        this.view.addElement();

        assertThat(addShuntTriggered, is(true));
    },

    'test add-shunt trigger includes expected object': function () {
        var eventObject;
        $(this.view).one('add-shunt.swivelView', function (event, shuntDescription) {
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

    'test add-shunt clears remoteURI field':function(){
        this.view.addElement();

        verify(this.mockJQueryObject).val('');
    }
});