"use strict";

define(['jQuery', 'jsTree', 'jQuery-ui'], function ($) {
    var DELETE_BUTTON = '<button class="delete" title="Delete"></button>',
        INFO_BUTTON = '<button class="info" title="Edit"></button>';

    return function ($configTree, $resetDialog) {
        var view = this, $view = $(this);

        this.loadConfigurationData = function (data) {
            $configTree.find('.path').each(function (index, dom) {
                $configTree.jstree('delete_node', dom);
            });

            $.each(data, function (index, item) {
                var pathNode = $configTree.jstree('create_node', view.rootNode, 'last', {
                    data: [DELETE_BUTTON, item.path].join(''),
                    state: 'open',
                    attr: {class: 'path'}
                })
                    .data('path-data', item), i, stubNode, stub;
                if (item.shunt) {
                    $configTree.jstree('create_node', pathNode, 'inside', {
                        data: [DELETE_BUTTON, 'shunt: ', item.shunt.remoteURL].join(''),
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
                            data: [DELETE_BUTTON, INFO_BUTTON, 'Stub: ', stub.description].join(''),
                            attr: {class: 'stub'}
                        })
                            .data('stub-data', stub);
                    }
                }
            });

            $configTree.find('button.delete')
                .button({icons: {primary: 'ui-icon-circle-close'}, text: false});
            $configTree.find('button.info')
                .button({icons: {primary: 'ui-icon-info'}, text: false});
            this.addClickEvents();
            $configTree.jstree('open_node', this.rootNode, null, true);
        };

        this.addClickEvents = function () {

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
            $configTree.find('.path > a > button.delete')
                .click(function (e) {
                    $view.trigger('delete-path.swivelView', $(e.target)
                        .closest('.path')
                        .data('path-data'));
                });
            $configTree.find('.stub button.info')
                .click(function (e) {
                    $view.trigger('stub-info.swivelView', $(e.target)
                        .closest('.stub')
                        .data('stub-data'));
                });
        };

        //can be null in testing situations
        if ($configTree) {
            $configTree.one('loaded.jstree',function () {
                view.rootNode = $('#configRoot');
                $view.trigger('loaded.swivelView')
            }).jstree({
                    core: {html_titles: true},
                    plugins: ['json_data', 'themeroller'],
                    json_data: {data: [
                        {data: 'Configuration', state: 'open', attr: {id: 'configRoot'}}
                    ]} });
        }

        if ($resetDialog) {
            $resetDialog.dialog({
                autoOpen: false,
                closeOnEscape: false,
                modal: true,
                resizable: false,
                dialogClass: 'no-close',
                buttons: [
                    {text: 'OK', click: function () {
                        $view.trigger('reset.swivelView');
                        $(this).dialog('close');
                    }},
                    {text: 'Cancel', click: function () {$(this).dialog('close');}}

                ]
            });
        }

        $('#reset').button()
            .click(function () {
                console.log('about to open dialog');
                $resetDialog.dialog('open');
            });
    };
});