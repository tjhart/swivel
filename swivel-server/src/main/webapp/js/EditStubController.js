"use strict";

define(['jQuery', 'utils'], function ($, utils) {
    return function (swivelServer, editStubView) {
        var query = utils.getQuery();

        function goHome() {
            window.location = 'index.html';
        }

        if (query.id) {
            swivelServer.getStubs(query, function (data) {
                editStubView.setStub(query.path, data[0]);
            });
        }

        $(editStubView).on('edit-stub.swivelView',function (event, stubData) {
            swivelServer.editStub(stubData, goHome);
        }).on('add-stub.swivelView', function (event, stubData) {
                swivelServer.addStub(stubData, goHome);
            });
    }
});