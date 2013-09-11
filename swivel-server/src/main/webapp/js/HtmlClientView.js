"use strict";

define(['jQuery', 'jsTree'], function ($) {
    return function () {
        var view = this;
        this.configTree = $('.currentConfig')
            .one('loaded.jstree', function () {
                view.rootNode = $('#configRoot');
                $(view).trigger('loaded.swivelView')
            })
            .jstree({
                core: {},
                plugins: ['themes', 'json_data'],
                json_data: {data: [
                    {data: 'Configuration', state: 'open', attr: {id: 'configRoot'}}
                ]}
            });

        this.loadConfigurationData = function (data) {
            $.each(data, function (index, item) {
                var pathNode = view.configTree.jstree('create_node', view.rootNode, 'last', {
                    data: item.path,
                    state: 'open'
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
        };
    };
});