"use strict";

define(['MainPageView', 'jQuery', 'jsHamcrest', 'jsMockito'], function (MainPageView, $, jsHamcrest, jsMockito) {
    jsHamcrest.Integration.QUnit();
    jsMockito.Integration.QUnit();

    var NODE_DATA = [
        {
            path: 'some/path',
            shunt: {remoteURL: 'http://remoteHost/path'},
            stubs: [
                {id: 1, description: 'stub 1 description'},
                {id: 2, description: 'stub 2 description'}
            ]
        }
    ];

    module('MainPageView tests', {
        setup: function () {
            $('#qunit-fixture').append(
                '       <ul> ' +
                    '       <li class="path"> ' +
                    '       <ins>&nbsp;</ins> ' +
                    '       <a href="#"> ' +
                    '          <ins>&nbsp;</ins> ' +
                    '           <button class="delete" title="Delete" role="button"> ' +
                    '               <span></span> ' +
                    '              <span></span> ' +
                    '           </button>' +
                    '           some/path ' +
                    '       </a> ' +
                    '       <ul> ' +
                    '           <li class="shunt"> ' +
                    '               <ins>&nbsp;</ins> ' +
                    '               <a href="#"> ' +
                    '                   <ins>&nbsp;</ins> ' +
                    '                   <button class="delete" title="Delete" role="button"> ' +
                    '                       <span></span> ' +
                    '                       <span></span> ' +
                    '                   </button> ' +
                    '                   <button class="edit" title="Edit" role="button"> ' +
                    '                       <span></span> ' +
                    '                       <span></span> ' +
                    '                   </button> ' +
                    '                   shunt: http://localhost/path ' +
                    '               </a> ' +
                    '           </li> ' +
                    '           <li class="stubs"> ' +
                    '               <ins>&nbsp;</ins> ' +
                    '               <a href="#"> ' +
                    '                   <ins>&nbsp;</ins> ' +
                    '                   stubs ' +
                    '               </a> ' +
                    '               <ul> ' +
                    '                  <li class="stub"> ' +
                    '                       <ins>&nbsp;</ins> ' +
                    '                       <a href="#"> ' +
                    '                           <ins>&nbsp;</ins> ' +
                    '                           <button class="delete" title="Delete" role="button"> ' +
                    '                               <span></span> ' +
                    '                               <span></span> ' +
                    '                          </button> ' +
                    '                           <button class="edit" title="Edit" role="button"> ' +
                    '                               <span></span> ' +
                    '                               <span></span> ' +
                    '                           </button> ' +
                    '                           Stub: 1 ' +
                    '                      </a> ' +
                    '                   </li> ' +
                    '                   <li class="stub"> ' +
                    '                       <ins>&nbsp;</ins> ' +
                    '                       <a href="#"> ' +
                    '                           <ins>&nbsp;</ins> ' +
                    '                           <button class="delete" title="Delete" role="button"> ' +
                    '                               <span></span> ' +
                    '                              <span></span> ' +
                    '                           </button> ' +
                    '                           <button class="edit" title="Edit" role="button"> ' +
                    '                               <span></span> ' +
                    '                               <span></span> ' +
                    '                           </button> ' +
                    '                           Stub: 2 ' +
                    '                      </a> ' +
                    '                   </li> ' +
                    '               </ul> ' +
                    '          </li> ' +
                    '       </ul>' +
                    '   </li> ' +
                    '</ul>' +
                    '<button id="reset"></button>' +
                    '<button id="loadConfig"></button>' +
                    '<button id="addShunt"></button>' +
                    '<button id="addStub"></button>' +
                    '<button id="getConfig"></button>');

            this.shuntData = {path: 'some/path', shunt: {remoteURL: 'http://host/path'}};
            this.stubData = {};
            this.pathData = {path: 'some/path'};

            $('.shunt').data('shunt-data', this.shuntData);
            $('.stub').data('stub-data', this.stubData);
            $('.path').data('path-data', this.pathData);

            this.mockConfigTree = mock($);
            this.mockJQueryObject = mock($);
            this.mockResetDialog = mock($);
            this.mockAddShuntDialog = mock($);
            this.mockLoadConfigDialog = mock($);

            when(this.mockConfigTree)
                .one(anything(), anything())
                .thenReturn(this.mockConfigTree);
            when(this.mockConfigTree)
                .find(anything())
                .thenReturn(this.mockJQueryObject);
            when(this.mockConfigTree)
                .jstree()
                .thenReturn(this.mockConfigTree);
            when(this.mockAddShuntDialog)
                .find(anything())
                .thenReturn(this.mockJQueryObject);
            when(this.mockJQueryObject)
                .removeClass(anything())
                .thenReturn(this.mockJQueryObject);
            when(this.mockJQueryObject)
                .addClass(anything())
                .thenReturn(this.mockJQueryObject);
            when(this.mockJQueryObject)
                .prop(anything(), anything())
                .thenReturn(this.mockJQueryObject);

            this.view = new MainPageView(this.mockConfigTree, this.mockResetDialog, this.mockAddShuntDialog,
                this.mockLoadConfigDialog);
        }
    });


    test('construction configures tree', 0, function () {
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
    });

    test('construction listens for loaded.jstree', 0, function () {
        verify(this.mockConfigTree).one('loaded.jstree', typeOf('function'));
    });

    test('loadConfigurationData creates path node', 0, function () {
        var mockPathNode = mock($);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        this.view.loadConfigurationData(NODE_DATA);

        verify(this.mockConfigTree).jstree('create_node', this.view.rootNode, 'last', allOf(
            hasMember('data', containsString(NODE_DATA[0].path)),
            hasMember('state', equalTo('open'))
        ));
    });

    test('loadConfigurationData creates shunt leaf', 0, function () {
        var mockPathNode = mock($);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);
        this.view.loadConfigurationData(NODE_DATA);

        verify(this.mockConfigTree).jstree('create_node', mockPathNode, 'inside', allOf(
            hasMember('data', containsString('shunt: ' + NODE_DATA[0].shunt.remoteURL)),
            hasMember('attr', hasMember('class', equalTo('shunt')))
        ));
    });

    test('loadConfigurationData creates stub nodes', 0, function () {
        var mockPathNode = mock($);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', anything(), anything(), anything())
            .thenReturn(mockPathNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(NODE_DATA);
        verify(this.mockConfigTree).jstree('create_node', mockPathNode, 'last', allOf(
            hasMember('data', equalTo('stubs')),
            hasMember('state', equalTo('open')),
            hasMember('attr', hasMember('class', equalTo('stubs')))
        ));
    });

    test('loadConfigurationData creates stub leafs', 0, function () {
        var mockPathNode = mock($), mockStubsNode = mock($);
        this.view.addClickEvents = mockFunction();

        when(this.mockConfigTree).jstree('create_node', this.view.rootNode, anything(), anything())
            .thenReturn(mockPathNode);
        when(this.mockConfigTree).jstree('create_node', mockPathNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(this.mockConfigTree).jstree('create_node', mockStubsNode, anything(), anything())
            .thenReturn(mockStubsNode);
        when(mockPathNode).data(anything(), anything()).thenReturn(mockPathNode);

        this.view.loadConfigurationData(NODE_DATA);
        verify(this.mockConfigTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(NODE_DATA[0].stubs[0].id)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
        verify(this.mockConfigTree).jstree('create_node', mockStubsNode, 'last', allOf(
            hasMember('data', containsString(NODE_DATA[0].stubs[1].description)),
            hasMember('attr', hasMember('class', equalTo('stub')))
        ));
    });

    test('shunt delete button triggers as expected', 1, function () {
        var that = this, $deleteShuntButton = $('.shunt button.delete');

        when(this.mockConfigTree)
            .find('.shunt button.delete')
            .thenReturn($deleteShuntButton);
        this.view.addClickEvents();
        $(this.view).one('delete-shunt.swivelView', function (event, data) {
            assertThat(data, equalTo(that.shuntData));
        });

        $deleteShuntButton.click();
    });

    test('stub delete button triggers as expected', 1, function () {
        var that = this, $deleteStubButton = $('.stub button.delete');

        when(this.mockConfigTree)
            .find('.stub button.delete')
            .thenReturn($deleteStubButton);
        this.view.addClickEvents();
        $(this.view).one('delete-stub.swivelView', function (event, data) {
            assertThat(data, equalTo(that.stubData));
        });

        $deleteStubButton.click();
    });

    test('delete path button triggers as expected', 1, function () {
        var that = this, $deletePathButton = $('.path > a > button.delete');

        when(this.mockConfigTree)
            .find('.path > a > button.delete')
            .thenReturn($deletePathButton);
        this.view.addClickEvents();
        $(this.view).one('delete-path.swivelView', function (event, data) {
            assertThat(data, equalTo(that.pathData));
        });

        $deletePathButton.click();
    });

    test('edit stub triggers as expected', 1, function () {
        var $editStubButton = $('.stub button.edit'), that = this;

        when(this.mockConfigTree)
            .find('.stub button.edit')
            .thenReturn($editStubButton);
        this.view.addClickEvents();
        $(this.view).one('edit-stub.swivelView', function (event, data) {
            assertThat(data, equalTo(that.stubData));
        });

        $editStubButton.click();
    });

    test('edit shunt disables shuntPath', function () {
        var $fixture = $('#qunit-fixture').append(
            '   <form>' +
                '   <input id="shuntPath" type="text"/>' +
                '   <input id="remoteURL" type="text"/>' +
                '</form>'
        ), $editShuntButton = $('.shunt button.edit'), $shuntPath;
        $shuntPath = $('#shuntPath');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn($shuntPath);
        when(this.mockConfigTree)
            .find('.shunt button.edit')
            .thenReturn($editShuntButton);
        this.view.addClickEvents();
        $editShuntButton.click();

        assertThat($shuntPath.hasClass('ui-state-disabled'), is(true));
        assertThat($shuntPath.prop('readonly'), is(true));
    });

    test('edit shunt sets input values', function () {
        var $fixture = $('#qunit-fixture').append(
            '   <form>' +
                '   <input id="shuntPath" type="text"/>' +
                '   <input id="remoteURL" type="text"/>' +
                '</form>'
        ), $editShuntButton = $('.shunt button.edit'), $shuntPath, $remoteURL;
        $shuntPath = $('#shuntPath');
        $remoteURL = $('#remoteURL');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn($shuntPath);
        when(this.mockAddShuntDialog)
            .find('#remoteURL')
            .thenReturn($remoteURL);
        when(this.mockConfigTree)
            .find('.shunt button.edit')
            .thenReturn($editShuntButton);
        this.view.addClickEvents();
        $editShuntButton.click();

        assertThat($shuntPath.val(), equalTo(this.shuntData.path));
        assertThat($remoteURL.val(), equalTo(this.shuntData.shunt.remoteURL));
    });

    test('edit shunt sets dialog title', 0, function () {
        var $editShuntButton = $('.shunt button.edit');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn(this.mockJQueryObject);
        when(this.mockAddShuntDialog)
            .find('#remoteURL')
            .thenReturn(this.mockJQueryObject);
        when(this.mockConfigTree)
            .find('.shunt button.edit')
            .thenReturn($editShuntButton);
        this.view.addClickEvents();

        $editShuntButton.click();

        verify(this.mockAddShuntDialog).dialog('option', 'title', 'Edit Shunt');
    });

    test('edit shunt opens dialog', 0, function () {
        var $editShuntButton = $('.shunt button.edit');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn(this.mockJQueryObject);
        when(this.mockAddShuntDialog)
            .find('#remoteURL')
            .thenReturn(this.mockJQueryObject);
        when(this.mockConfigTree)
            .find('.shunt button.edit')
            .thenReturn($editShuntButton);
        this.view.addClickEvents();

        $editShuntButton.click();

        verify(this.mockAddShuntDialog).dialog('open');
    });

    test('init configures dialogs', 0, function () {
        $.each([this.mockResetDialog, this.mockAddShuntDialog, this.mockLoadConfigDialog], function (i, dialog) {
            verify(dialog).dialog(allOf(
                hasMember('autoOpen', is(false)),
                hasMember('closeOnEscape', is(false)),
                hasMember('modal', is(true)),
                hasMember('resizable', is(false))
            ));
        });
    });

    test('reset opens dialog', 0, function () {
        $('#reset').click();

        verify(this.mockResetDialog).dialog('open');
    });

    test('addShunt enablesShuntPath', function () {
        var $fixture = $('#qunit-fixture').append(
            '   <form>' +
                '   <input id="shuntPath" type="text"/>' +
                '   <input id="remoteURL" type="text"/>' +
                '</form>'
        ), $shuntPath;
        $shuntPath = $('#shuntPath');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn($shuntPath);
        $('#addShunt').click();

        assertThat($shuntPath.hasClass('ui-state-disabled'), is(false));
        assertThat($shuntPath.prop('readonly'), is(false));
    });

    test('addShunt clears values', function () {
        var $fixture = $('#qunit-fixture').append(
            '   <form>' +
                '   <input id="shuntPath" type="text"/>' +
                '   <input id="remoteURL" type="text"/>' +
                '</form>'
        ), $shuntPath, $remoteURL;
        $shuntPath = $('#shuntPath');
        $remoteURL = $('#remoteURL');

        when(this.mockAddShuntDialog)
            .find('#shuntPath')
            .thenReturn($shuntPath);
        when(this.mockAddShuntDialog)
            .find('#remoteURL')
            .thenReturn($remoteURL);
        $('#addShunt').click();

        assertThat($shuntPath.val(), equalTo(''));
        assertThat($remoteURL.val(), equalTo(''));
    });

    test('addShunt sets title', 0, function () {
        $('#addShunt').click();

        verify(this.mockAddShuntDialog).dialog('option', 'title', 'Add Shunt');
    });

    test('addShunt opens dialog', 0, function () {
        $('#addShunt').click();

        verify(this.mockAddShuntDialog).dialog('open');
    });

    test('addStub triggers event', 1, function () {
        var triggered = false;
        $(this.view).on('add-stub.swivelView', function () {
            triggered = true;
        });

        $('#addStub').click();
        assertThat(triggered, is(true));
    });

    test('getConfig triggers event', 1, function () {
        var triggered = false;
        $(this.view).on('get-config.swivelView', function () {
            triggered = true;
        });

        $('#getConfig').click();
        assertThat(triggered, is(true));
    });

    test('loadConfig opens dialog', 0, function () {
        $('#loadConfig').click();

        verify(this.mockLoadConfigDialog).dialog('open');
    });
});

