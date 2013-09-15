"use strict";

define(['jQuery', 'jsTree', 'jQuery-ui'], function ($) {
    var ADD_BUTTON = '<button class="treeButton add" title="Add"></button>',
        DELETE_BUTTON = '<button class="treeButton delete" title="Delete"></button>';
    return function ($addElementDialog) {
        var view = this, $view = $(this);

        this.loadConfigurationData = function (data) {
            this.configTree.find('.path').each(function (index, dom) {
                view.configTree.jstree('delete_node', dom);
            });
            $.each(data, function (index, item) {
                var pathNode = view.configTree.jstree('create_node', view.rootNode, 'last', {
                    data: [ADD_BUTTON,
                        DELETE_BUTTON,
                        item.path].join(''),
                    state: 'open',
                    attr: {class: 'path'}
                })
                    .data('path-data', item), i, stubNode, stub;
                if (item.shunt) {
                    view.configTree.jstree('create_node', pathNode, 'inside', {
                        data: [DELETE_BUTTON, 'shunt: ', item.shunt].join(''),
                        attr: {class: 'shunt'}
                    })
                        .data('shunt-data', item);
                }
                if (item.stubs) {
                    stubNode = view.configTree.jstree('create_node', pathNode, 'last', {
                        data: 'stubs',
                        state: 'open',
                        attr: {class: 'stubs'}
                    });
                    for (i = 0; i < item.stubs.length; i++) {
                        stub = item.stubs[i];
                        view.configTree.jstree('create_node', stubNode, 'last', {
                            data: [DELETE_BUTTON, stub.description].join(''),
                            attr: {class: 'stub'}
                        })
                            .data('stub-data', stub);
                    }
                }
            });

            this.addClickEvents();
            this.configTree.jstree('open_node', this.rootNode, null, true);
        };

        this.addClickEvents = function () {
            function getPath(e) {
                return $(e.target)
                    .closest('.path')
                    .data('path-data');
            }

            this.configTree.find('.shunt button.delete')
                .click(function (e) {
                    $view.trigger('delete-shunt.swivelView', $(e.target)
                        .closest('.shunt').
                        data('shunt-data'));
                });
            this.configTree.find('.stub button.delete')
                .click(function (e) {
                    $view.trigger('delete-stub.swivelView', $(e.target)
                        .closest('.stub')
                        .data('stub-data'));
                });
            this.configTree.find('.path button.add')
                .click(function (e) {
                    view.targetPath = getPath(e);
                    $addElementDialog.dialog('option', {
                        title: ['Add configuration element to: ', targetPath.path].join('')
                    });
                    $addElementDialog.dialog('open');
                });
            this.configTree.find('.path button.delete')
                .click(function (e) {
                    $view.trigger('delete-path.swivelView', getPath(e));
                });
        };

        this.addElement = function () {
            var remoteURIField = this.$addElementForm.find('[name="remoteURI"]');
            $addElementDialog.dialog('close');
            $view.trigger('add-shunt.swivelView', {
                path: view.targetPath.path,
                remoteURI: remoteURIField.val()
            });
            remoteURIField.val('');
        };

        this.configTree = $('.currentConfig')
            .one('loaded.jstree', function () {
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

        //can be null in testing situations
        if($addElementDialog){
            this.$addElementForm = $addElementDialog.find('form')

            $addElementDialog.dialog({
                autoOpen: false,
                modal: true,
                draggable: false,
                resizable: false,
                width: 600,
                buttons: [
                    {
                        text: 'Cancel',
                        click: function () {$addElementDialog.dialog('close');}
                    },
                    {
                        text: 'Add',
                        click: function () { view.addElement(); }
                    }
                ]
            });
        }
    };
});