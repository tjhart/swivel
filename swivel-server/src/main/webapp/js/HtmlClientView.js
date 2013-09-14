"use strict";

define(['jQuery', 'jsTree'], function ($) {
    return function () {
        var view = this, $view = $(this);

        this.loadConfigurationData = function (data) {
            this.configTree.find('.path').each(function (index, dom) {
                view.configTree.jstree('delete_node', dom);
            });
            $.each(data, function (index, item) {
                var pathNode = view.configTree.jstree('create_node', view.rootNode, 'last', {
                    data: item.path,
                    state: 'open',
                    attr: {class: 'path'}
                })
                    .data('path-data', item), i, stubNode, stub;
                if (item.shunt) {
                    view.configTree.jstree('create_node', pathNode, 'inside', {
                        data: ['shunt: ', item.shunt].join(''),
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
                            data: stub.description,
                            attr: {class: 'stub'}
                        })
                            .data('stub-data', stub);
                    }
                }
            });

            this.decorateTree();
            this.configTree.jstree('open_node', this.rootNode, null, true);
        };

        this.decorateTree = function () {
            function getPath(e) {
                return $(e.target)
                    .closest('.path')
                    .data('path-data');
            }

            var $removeShunt = $('<button class="delete" title="Delete"></button>'), $removeStub, $removePath,
                $add = $('<button class="add" title="Add..."></button>')
                    .click(function (e) {
                        console.log('adding to path');
                        console.log(getPath(e));
                    });
            $removeStub = $removeShunt.clone()
                .click(function (e) {
                    $view.trigger('delete-stub.swivelView', $(e.target)
                        .closest('.stub')
                        .data('stub-data'));
                });
            $removePath = $removeShunt.clone()
                .click(function (e) {
                    $view.trigger('delete-path.swivelView', getPath(e));
                });
            $removeShunt.click(function (e) {
                $view.trigger('delete-shunt.swivelView', $(e.target)
                    .closest('.shunt').
                    data('shunt-data'));
            });
            this.configTree.find('.shunt')
                .find('a')
                .append($removeShunt);
            this.configTree.find('.stub')
                .find('a')
                .append($removeStub);
            this.configTree.find('.path')
                .find('a:first')
                .append($add)
                .append($removePath);
        };

        this.configTree = $('.currentConfig')
            .one('loaded.jstree', function () {
                view.rootNode = $('#configRoot');
                $view.trigger('loaded.swivelView')
            })
            .jstree({
                core: {},
                plugins: ['themes', 'json_data'],
                json_data: {data: [
                    {data: 'Configuration', state: 'open', attr: {id: 'configRoot'}}
                ]}
            });
    };
});