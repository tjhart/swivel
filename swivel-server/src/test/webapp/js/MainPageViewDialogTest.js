"use strict";

define(['MainPageView', 'jQuery', 'jsHamcrest', 'jsMockito'], function (MainPageView, $, jsHamcrest, jsMockito) {
    jsHamcrest.Integration.QUnit();
    jsMockito.Integration.QUnit();

    function findButton(dialog, buttonText) {
        return dialog.closest('.ui-dialog')
            .find('.ui-dialog-buttonpane')
            .find(['span:contains("', buttonText, '")'].join(''))
            .closest('button')
    }

    module('MainPageView Dialog tests', {
        setup: function () {
            $('#qunit-fixture').append(
                '<div class="content ui-helper-hidden">' +
                    '    <div class="treeContainer">' +
                    '        <div id="currentConfig"></div>' +
                    '    </div>' +
                    '    <div class="action">' +
                    '        <button id="reset">Reset</button>' +
                    '        <button id="addShunt">Add Shunt</button>' +
                    '        <button id="addStub">Add Stub</button>' +
                    '        <button id="getConfig">Export</button>' +
                    '        <button id="loadConfig">Load Configuration...</button>' +
                    '    </div>' +
                    '</div>' +
                    '<div class="ui-helper-hidden">' +
                    '    <div id="resetDialog" title="Are you sure?" data-action="reset">' +
                    '    Are you sure you want to reset Swivel? All stubs and shunts will be removed.' +
                    '    </div>' +
                    '    <div id="addOrEditShuntDialog" title="Add Shunt" data-action="add-or-edit-shunt"' +
                    '        <form name="addShunt" action="#">' +
                    '            <div class="shuntElements">' +
                    '                <label for="shuntPath">Swivel Path</label>' +
                    '                <input id="shuntPath" type="text" name="path"/>' +
                    '                <label for="remoteURL">Remote URL:</label>' +
                    '                <input id="remoteURL" type="text" name="remoteURL"/>' +
                    '            </div>' +
                    '        </form>' +
                    '    </div>' +
                    '    <div id="loadConfigDialog" title="Load Swivel Configuration" data-action="load-configuration">' +
                    '        <form action="#">' +
                    '            <label for="swivelConfig">File</label>' +
                    '            <input id="swivelConfig" type="file" name="swivelConfig"/>' +
                    '        </form>' +
                    '    </div>' +
                    '</div>');

            this.configTree = $('#currentConfig');
            this.resetDialog = $('#resetDialog');
            this.addShuntDialog = $('#addOrEditShuntDialog');
            this.loadConfigDialog = $('#loadConfigDialog');

            this.view = new MainPageView(this.configTree, this.resetDialog, this.addShuntDialog, this.loadConfigDialog);
        }
    });

    test('reset triggers resetEvent', function () {

        this.resetDialog.dialog('open');
        $(this.view).on('reset.swivelView', function () {
            ok(true);
        });
        findButton(this.resetDialog, 'OK').click();
    });

    test('reset closes dialog', function () {

        this.resetDialog.dialog('open');
        $(this.resetDialog).on('dialogclose', function () {
            ok(true);
        });
        findButton(this.resetDialog, 'OK').click();
    });

    test('reset prevents default', function () {

        this.resetDialog.dialog('open');
        var $okButton = findButton(this.resetDialog, 'OK');
        $okButton.click(function (e) {
            assertThat(e.isDefaultPrevented(), is(true));
        }).click();
    });

    test('addShunt closes dialog', function () {
        this.addShuntDialog.dialog('open');
        $(this.addShuntDialog).on('dialogclose', function () {
            ok(true);
        });
        findButton(this.addShuntDialog, 'OK').click();
    });

    test('addShunt prevents default', function () {
        this.addShuntDialog.dialog('open');
        findButton(this.addShuntDialog, 'OK').click(function (e) {
            assertThat(e.isDefaultPrevented(), is(true));
        }).click();
    });

    test('addShunt triggers add-shunt with expected data', function () {
        var $shuntPath = this.addShuntDialog.find('#shuntPath'), $remoteURL = this.addShuntDialog.find('#remoteURL');
        $('#addShunt').click();
        $(this.view).on('add-shunt.swivelView', function (event, data) {
            assertThat(data, allOf(
                hasMember('path', equalTo($shuntPath.val())),
                hasMember('remoteURL', equalTo($remoteURL.val()))
            ));
        });

        $shuntPath.val('some/path').change();
        $remoteURL.val('http://host/path').change();
        findButton(this.addShuntDialog, 'OK').click();
    });

    test('addShunt triggers edit-shunt with expected data', function () {
        var $shuntPath = this.addShuntDialog.find('#shuntPath'), $remoteURL = this.addShuntDialog.find('#remoteURL');
        this.view._setMode('edit');
        this.addShuntDialog.dialog('open');
        $(this.view).on('edit-shunt.swivelView', function (event, data) {
            assertThat(data, allOf(
                hasMember('path', equalTo($shuntPath.val())),
                hasMember('remoteURL', equalTo($remoteURL.val()))
            ));
        });

        $shuntPath.val('some/path').change();
        $remoteURL.val('http://host/path').change();
        findButton(this.addShuntDialog, 'OK').click();
    });

    test('loadConfiguration closes dialog', function () {
        this.loadConfigDialog.dialog('open');
        $(this.loadConfigDialog).on('dialogclose', function () {
            ok(true);
        });
        findButton(this.loadConfigDialog, 'Load Configuration').click();
    });

    test('loadConfiguration triggers event', function () {
        this.loadConfigDialog.dialog('open');
        $(this.view).on('load-configuration.swivelView', function (event, data) {
            ok(true);
        });
        findButton(this.loadConfigDialog, 'Load Configuration').click();
    });

    test('loadConfiguration prevents default', function () {
        this.loadConfigDialog.dialog('open');
        findButton(this.loadConfigDialog, 'Load Configuration')
            .click(function (e) {
                assertThat(e.isDefaultPrevented(), is(true));
            })
            .click();
    });
});