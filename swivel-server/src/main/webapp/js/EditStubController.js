"use strict";

define(['jQuery', 'utils'], function ($, utils) {
    return function (swivelServer, editStubView) {


        swivelServer.getStubs(utils.getQuery(), function (data) {
            editStubView.setStub(data[0]);
        });
    }
});