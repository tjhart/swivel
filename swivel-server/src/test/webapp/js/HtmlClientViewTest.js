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

         <button id="staticButton"/>
         <button id="scriptButton"/>

         <form>
         <div class="staticInput">
         <label for="method">Method:</label>
         <select id="method" name="method">
         <option value="">(any)</option>
         <option>GET</option>
         <option>PUT</option>
         <option>POST</option>
         <option>DELETE</option>
         </select>
         <label for="contentType">Content Type:</label>
         <input id="contentType" type="text" name="contentType"/>
         <label for="remoteAddr">Remote Address:</label>
         <input id="remoteAddr" type="text" name="remoteAddr"/>
         <label for="content">Content:</label>
         <textarea id="content" name="content"></textarea>
         </div>
         <div class="scriptInput hidden">
         <label>Script:<textarea name="script"></textarea></label>
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

        this.mockAddShuntDialog = mock(this.r.$);
        this.mockAddStubDialog = mock(this.r.$);
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
        when(this.mockAddShuntDialog).find(anything())
            .thenReturn(this.mockJQueryObject);
        when(this.mockAddStubDialog).find(anything())
            .thenReturn(this.mockJQueryObject);

        this.view = new this.r.HtmlClientView(this.mockConfigTree, this.mockAddShuntDialog, this.mockAddStubDialog);
        this.view.$remoteURI = this.mockJQueryObject;
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
    },

    'test construction configures addElement dialog': function () {
        verify(this.mockAddShuntDialog).dialog(allOf(
            hasMember('autoOpen', is(false)),
            hasMember('modal', is(true)),
            hasMember('draggable', is(false)),
            hasMember('resizable', is(false)),
            hasMember('width', is(600))
        ));
    },

    'test addShuntDialog add defers to addElement': function () {
        var dialogButtons;
        when(this.mockAddShuntDialog).dialog(anything()).then(function (opts) {
            dialogButtons = opts.buttons;
            return this;
        });

        var view = new this.r.HtmlClientView(this.mockConfigTree, this.mockAddShuntDialog, this.mockAddStubDialog);
        view.addShunt = mockFunction();
        dialogButtons[1].click();

        verify(view.addShunt)();
    },

    'test addShunt closes dialog': function () {
        this.view.addShunt();

        verify(this.mockAddShuntDialog).dialog('close');
    },

    'test addShunt triggers put-shunt event': function () {
        var addShuntTriggered = false;
        this.r.$(this.view).one('put-shunt.swivelView', function () {
            addShuntTriggered = true;
        });

        this.view.addShunt();

        assertThat(addShuntTriggered, is(true));
    },

    'test addShunt trigger includes expected object': function () {
        var eventObject;
        this.r.$(this.view).one('put-shunt.swivelView', function (event, shuntDescription) {
            eventObject = shuntDescription;
        });

        when(this.mockJQueryObject).val()
            .thenReturn('some/remote/uri');
        this.view.addShunt();

        assertThat(eventObject, allOf(
            hasMember('path', equalTo(this.view.targetPath.path)),
            hasMember('remoteURI', equalTo('some/remote/uri'))
        ));
    },

    'test addShunt clears remoteURI field': function () {
        this.view.addShunt();

        verify(this.mockJQueryObject).val('');
    },

    'test path addShunt event handler sets target path': function () {
        var $pathAddButton = this.r.$('.path button.addShunt');

        when(this.mockConfigTree).find('.path button.addShunt')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        assertThat(this.view.targetPath, equalTo(this.pathData));
    },

    'test path addShunt event handler sets dialog title': function () {
        var $pathAddButton = this.r.$('.path button.addShunt');

        when(this.mockConfigTree).find('.path button.addShunt')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        verify(this.mockAddShuntDialog).dialog('option',
            hasMember('title', containsString(this.pathData.path)));
    },

    'test path addShunt event handler opens dialog': function () {
        var $pathAddButton = this.r.$('.path button.addShunt');

        when(this.mockConfigTree).find('.path button.addShunt')
            .thenReturn($pathAddButton);
        this.view.addClickEvents();

        $pathAddButton.click();

        verify(this.mockAddShuntDialog).dialog('open');
    },

    'test path addStub event handler sets target path': function () {
        var $pathAddStubButton = this.r.$('.path button.addStub');

        when(this.mockConfigTree).find('.path button.addStub')
            .thenReturn($pathAddStubButton);
        this.view.addClickEvents();

        $pathAddStubButton.click();

        assertThat(this.view.targetPath, equalTo(this.pathData));
    },

    'test path addStub event handler sets dialog title': function () {
        var $pathAddStubButton = this.r.$('.path button.addStub');

        when(this.mockConfigTree).find('.path button.addStub')
            .thenReturn($pathAddStubButton);
        this.view.addClickEvents();

        $pathAddStubButton.click();

        verify(this.mockAddStubDialog).dialog('option',
            hasMember('title', containsString(this.pathData.path)));
    },

    'test path addStub event handler opens dialog': function () {
        var $pathAddStubButton = this.r.$('.path button.addStub');

        when(this.mockConfigTree).find('.path button.addStub')
            .thenReturn($pathAddStubButton);
        this.view.addClickEvents();

        $pathAddStubButton.click();

        verify(this.mockAddStubDialog).dialog('open');
    },

    'test addStub dialog add button defers to addStub': function () {
        var dialogButtons;
        when(this.mockAddStubDialog).dialog(anything()).then(function (opts) {
            dialogButtons = opts.buttons;
            return this;
        });

        var view = new this.r.HtmlClientView(this.mockConfigTree, this.mockAddShuntDialog, this.mockAddStubDialog);
        view.addStub = mockFunction();
        dialogButtons[1].click();

        verify(view.addStub)();
    },

    'test staticButton click shows staticInput and hides scriptInput': function () {
        this.r.$('#staticButton').click();

        assertThat(this.r.$('.staticInput').hasClass('hidden'), is(false));
        assertThat(this.r.$('.scriptInput').hasClass('hidden'), is(true));
    },

    'test scriptButton click shows scriptInput ahd hides staticInput': function () {
        this.r.$('#scriptButton').click();

        assertThat(this.r.$('.scriptInput').hasClass('hidden'), is(false));
        assertThat(this.r.$('.staticInput').hasClass('hidden'), is(true));
    },

    'test addStub closes dialog': function () {
        this.view.addStub();

        verify(this.mockAddStubDialog).dialog('close');
    },

    'test addStub triggers add-stub with expected data': function () {
        var addShuntData, $form = this.r.$('form');
        this.r.$(this.view).one('add-stub.swivelView', function (event, data) {
            addShuntData = data;
        });

        this.view.$addStubForm = $form;
        $form.find('[name="method"]').val('PUT');
        $form.find('[name="contentType"]').val('application/xml');
        $form.find('[name="remoteAddr"]').val('127.0.0.1');
        $form.find('[name="content"]').val('<doc><tag></tag></doc>');

        this.view.addStub();

        assertThat(addShuntData, allOf(
            hasMember('method', equalTo('PUT')),
            hasMember('contentType', equalTo('application/xml')),
            hasMember('remoteAddr', equalTo('127.0.0.1')),
            hasMember('content', equalTo('<doc><tag></tag></doc>'))
        ));
    },

    'test addStub clears form': function () {
        var that = this, $form = this.r.$('form');

        this.view.$addStubForm = $form;
        $form.find('[name="method"]').val('PUT');
        $form.find('[name="contentType"]').val('application/xml');
        $form.find('[name="remoteAddr"]').val('127.0.0.1');
        $form.find('[name="content"]').val('<doc><tag></tag></doc>');

        this.view.addStub();

        $form.find('[name]').each(function (index, item) {
            assertThat(that.r.$(item).val(), equalTo(''));
        });
    }
});