"use strict";

define(['jQuery', 'utils'], function ($, utils) {
    return function (swivelServer, editStubView) {
        var query = utils.getQuery();

        swivelServer.getStubs(query, function (data) {
            editStubView.setStub(data[0], query.path);
        });
    }
});