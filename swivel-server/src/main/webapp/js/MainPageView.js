"use strict";

define(['jQuery', 'jsTree', 'jQuery-ui'], function ($) {
    var DELETE_BUTTON = '<button class="delete" title="Delete"></button>',
        INFO_BUTTON = '<button class="info" title="Edit"></button>',
        DEFAULT_DIALOG_OPTS = {
            autoOpen: false,
            closeOnEscape: false,
            modal: true,
            resizable: false,
            dialogClass: 'no-close',
            width: 450
        },
        CANCEL_BUTTON = {text: 'Cancel', click: function () {$(this).dialog('close');}};

    return function ($configTree, $resetDialog, $addShuntDialog) {
        var view = this, $view = $(this), mode = 'add',
            DIALOG_BUTTONS = {
                reset: {
                    text: 'OK',
                    click: function () {
                        $view.trigger('reset.swivelView');
                        $(this).dialog('close');
                    }
                },
                'add-or-edit-shunt': {
                    text: 'OK',
                    click: function () {
                        var dialog = $(this), remoteURL, path;
                        remoteURL = dialog.find('#remoteURL');
                        path = dialog.find('#path');
                        $view.trigger([mode, '-shunt.swivelView'].join(''), {
                            remoteURL: remoteURL.val(),
                            path: path.val()
                        });
                        dialog.dialog('close');
                    }
                }
            };

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
                        data: [DELETE_BUTTON, INFO_BUTTON, 'shunt: ', item.shunt.remoteURL].join(''),
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
            $('.content').removeClass('ui-helper-hidden');
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
            $configTree.find('.shunt button.info')
                .click(function (e) {
                    var data = $(e.target)
                        .closest('.shunt')
                        .data('shunt-data');
                    mode = 'edit';
                    $addShuntDialog.find('#path')
                        .addClass('ui-state-disabled')
                        .prop('readonly', true)
                        .val(data.path);
                    $addShuntDialog.find('#remoteURL')
                        .val(data.shunt.remoteURL);

                    $addShuntDialog.dialog('option', 'title', 'Edit Shunt');
                    $addShuntDialog.dialog('open');
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

        $.each([$resetDialog, $addShuntDialog], function (idx, dialog) {
            if (dialog) {
                dialog.dialog($.extend({
                    buttons: [
                        DIALOG_BUTTONS[dialog.attr('data-action')],
                        CANCEL_BUTTON]
                }, DEFAULT_DIALOG_OPTS));
            }
        });

        $('#reset').button()
            .click(function () {
                $resetDialog.dialog('open');
            });
        $('#addShunt').button()
            .click(function () {
                mode = 'add';
                $addShuntDialog.find('#path')
                    .removeClass('ui-state-disabled')
                    .prop('readonly', false)
                    .val('');
                $addShuntDialog.find('#remoteURL')
                    .val('');
                $addShuntDialog.dialog('option', 'title', 'Add Shunt');
                $addShuntDialog.dialog('open');
            })
    };
});