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
                }), i, stubNode, stub;
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
            var $removeShunt = $('<button class="delete"></button>')
                    .click(function (e) {
                        $view.trigger('delete-shunt.swivelView', $(e.target).closest('.shunt').data('shunt-data'));
                    }),
                $removeStub = $('<button class="delete"></button>')
                    .click(function (e) {
                        $view.trigger('delete-stub.swivelView', $(e.target).closest('.stub').data('stub-data'));
                    });
            this.configTree.find('.shunt')
                .find('a')
                .append($removeShunt);
            this.configTree.find('.stub')
                .find('a')
                .append($removeStub);
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