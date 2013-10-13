"use strict";

define(['jQuery', 'utils'], function ($, utils) {
    return function (swivelServer, editStubView) {
        var query = utils.getQuery();

        swivelServer.getStubs(query, function (data) {
            editStubView.setStub(data[0], query.path);
        });

        $(editStubView).on('edit-stub.swivelView', function (controller, stubData) {
            swivelServer.editStub(stubData, function () {
                window.location = 'index.html';
            });
        });
    }
});