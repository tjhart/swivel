"use strict";

define(['jQuery', 'jsTree', 'jQuery-ui'], function ($) {
    var ADD_SHUNT_BUTTON = '<button class="addShunt" title="Add Shunt">Add Shunt</button>',
        ADD_STUB_BUTTON = '<button class="addStub" title="Add Stub">Add Stub</button>',
        DELETE_BUTTON = '<button class="treeButton delete" title="Delete"></button>',
        DIALOG_OPTS = {
            autoOpen: false,
            modal: true,
            draggable: false,
            resizable: false,
            width: 600
        },
        DIALOG_CANCEL_BUTTON = {
            text: 'Cancel',
            click: function () {$(this).dialog('close');}
        };
    return function ($configTree, $addShuntDialog, $addStubDialog) {
        var view = this, $view = $(this);

        this.loadConfigurationData = function (data) {
            $configTree.find('.path').each(function (index, dom) {
                $configTree.jstree('delete_node', dom);
            });
            $.each(data, function (index, item) {
                var pathNode = $configTree.jstree('create_node', view.rootNode, 'last', {
                    data: [ADD_STUB_BUTTON, ADD_SHUNT_BUTTON,
                        DELETE_BUTTON,
                        item.path].join(''),
                    state: 'open',
                    attr: {class: 'path'}
                })
                    .data('path-data', item), i, stubNode, stub;
                if (item.shunt) {
                    $configTree.jstree('create_node', pathNode, 'inside', {
                        data: [DELETE_BUTTON, 'shunt: ', item.shunt].join(''),
                        attr: {class: 'shunt'}
                    })
                        .data('shunt-data', item);
                }
                if (item.stubs) {
                    stubNode = $configTree.jstree('create_node', pathNode, 'last', {
                        data: 'stubs',
                        state: 'open',
                        attr: {class: 'stubs'}
                    });
                    for (i = 0; i < item.stubs.length; i++) {
                        stub = item.stubs[i];
                        $configTree.jstree('create_node', stubNode, 'last', {
                            data: [DELETE_BUTTON, stub.description].join(''),
                            attr: {class: 'stub'}
                        })
                            .data('stub-data', stub);
                    }
                }
            });

            this.addClickEvents();
            $configTree.jstree('open_node', this.rootNode, null, true);
        };

        this.addClickEvents = function () {
            function getPath(e) {
                return $(e.target)
                    .closest('.path')
                    .data('path-data');
            }

            $configTree.find('.shunt button.delete')
                .click(function (e) {
                    $view.trigger('delete-shunt.swivelView', $(e.target)
                        .closest('.shunt').
                        data('shunt-data'));
                });
            $configTree.find('.stub button.delete')
                .click(function (e) {
                    $view.trigger('delete-stub.swivelView', $(e.target)
                        .closest('.stub')
                        .data('stub-data'));
                });
            //REDTAG:TJH - consider just sending an event to the controller.
            //If a shunt already exists, shouldn't we warn the user
            //that it will be replaced?
            $configTree.find('.path button.addShunt')
                .click(function (e) {
                    view.targetPath = getPath(e);
                    $addShuntDialog.dialog('option', {
                        title: ['Add/replace shunt at: ', view.targetPath.path].join('')
                    });
                    $addShuntDialog.dialog('open');
                });
            $configTree.find('.path button.delete')
                .click(function (e) {
                    $view.trigger('delete-path.swivelView', getPath(e));
                });
            $configTree.find('.path button.addStub')
                .click(function (e) {
                    view.targetPath = getPath(e);
                    $addStubDialog.dialog('open');
                });
        };

        this.addShunt = function () {
            $addShuntDialog.dialog('close');
            $view.trigger('put-shunt.swivelView', {
                path: view.targetPath.path,
                remoteURI: view.$remoteURI.val()
            });
            view.$remoteURI.val('');
        };

        this.addStub = function () {
            throw "Not yet implemented";
        };

        //can be null in testing situations
        if ($configTree) {
            $configTree.one('loaded.jstree', function () {
                view.rootNode = $('#configRoot');
                $view.trigger('loaded.swivelView')
            })
                .jstree({
                    core: {html_titles: true},
                    plugins: ['themes', 'json_data'],
                    json_data: {data: [
                        {data: 'Configuration', state: 'open', attr: {id: 'configRoot'}}
                    ]}
                });
        }

        if ($addShuntDialog) {
            this.$remoteURI = $addShuntDialog.find('[name="remoteURI"]');

            $addShuntDialog.dialog(DIALOG_OPTS);
            $addShuntDialog.dialog('option', {
                buttons: [ DIALOG_CANCEL_BUTTON, {
                    text: 'Add Shunt',
                    click: function () { view.addShunt(); }
                } ]
            });

            if ($addStubDialog) {
                $addStubDialog.dialog(DIALOG_OPTS);
                $addStubDialog.dialog('option', {
                    buttons: [DIALOG_CANCEL_BUTTON, {
                        text: 'Add Stub',
                        click: function () {view.addStub();}
                    }]
                });

                $addStubDialog.find('#staticButton').click(function () {
                    $('.staticInput').removeClass('hidden');
                    $('.scriptInput').addClass('hidden');
                });
                $addStubDialog.find('#scriptButton').click(function () {
                    $('.scriptInput').removeClass('hidden');
                    $('.staticInput').addClass('hidden');
                });
            }
        }
    };
});