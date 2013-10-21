"use strict";

define(['test/lib/Squire', 'jsHamcrest', 'jsMockito'], function (Squire, jsHamcrest, jsMockito) {
    jsHamcrest.Integration.QUnit();
    jsMockito.Integration.QUnit();

    var mockUtils = {};

    new Squire()
        .mock('utils', mockUtils)
        .require(['EditStubView', 'jQuery'], function (EditStubView, $) {
            module('EditStubView tests', {
                setup: function () {
                    $('#qunit-fixture')
                        .append(
                            '<div class="content ui-helper-hidden">' +
                                '    <table class="stubHeader">' +
                                '        <tbody>' +
                                '            <tr>' +
                                '                <td><label for="path">Path:</label></td>' +
                                '                <td><input id="path" type="text" name="path" placeholder="(empty)"/></td>' +
                                '            </tr>' +
                                '            <tr>' +
                                '                <td><label for="description">Description:</label></td>' +
                                '                <td><input id="description" type="text" name="description" placeholder="(empty)"/></td>' +
                                '            </tr>' +
                                '        </tbody>' +
                                '    </table>' +
                                '    <form>' +
                                '        <div class="stubDefinition">' +
                                '            <div id="when">' +
                                '                <div class="name">When</div>' +
                                '                <div class="when">' +
                                '                    <div>' +
                                '                        <label for="method">Method:</label>' +
                                '                        <select id="method" name="method">' +
                                '                            <option value="">(not set)</option>' +
                                '                            <option>GET</option>' +
                                '                            <option>PUT</option>' +
                                '                            <option>POST</option>' +
                                '                            <option>DELETE</option>' +
                                '                        </select>' +
                                '' +
                                '                    </div>' +
                                '                    <div>' +
                                '                        <label for="query">Query:</label>' +
                                '                        <input id="query" type="text" name="query" placeholder="(empty)"/>' +
                                '' +
                                '                    </div>' +
                                '                    <div>' +
                                '                        <label for="contentType">Content Type:</label>' +
                                '                        <input id="contentType" type="text" name="requestContentType" placeholder="(empty)"/>' +
                                '' +
                                '                    </div>' +
                                '                    <div>' +
                                '                        <label for="remoteAddress">Remote Address:</label>' +
                                '                        <input id="remoteAddress" type="text" name="remoteAddress" placeholder="(empty)"/>' +
                                '                    </div>' +
                                '                    <div>' +
                                '                        <label for="content">Content:</label>' +
                                '' +
                                '                        <div id="content" class="editor verticalAlignTop"></div>' +
                                '                    </div>' +
                                '                    <div>' +
                                '                        <label for="whenScript">Script:</label>' +
                                '' +
                                '                        <div id="whenScript" class="editor verticalAlignTop"></div>' +
                                '                    </div>' +
                                '                </div>' +
                                '            </div>' +
                                '            <div id=then>' +
                                '                <div class="name">Then</div>' +
                                '                <div class="then">' +
                                '                    <div class="type">' +
                                '                        <label for="staticThen">Static</label>' +
                                '                        <input id="staticThen" type="radio" name="thenType" value="static" checked/>' +
                                '                        <label for="scriptThen">Script</label>' +
                                '                        <input id="scriptThen" type="radio" name="thenType" value="script"/>' +
                                '                    </div>' +
                                '                    <div class="static">' +
                                '                        <div>' +
                                '                            <label for="statusCode">Status Code:</label>' +
                                '                            <input id="statusCode" type="text" name="statusCode" placeholder="(empty)"/>' +
                                '                        </div>' +
                                '                        <div>' +
                                '                            <label for="contentType2">Content Type:</label>' +
                                '                            <input id="contentType2" type="text" name="responseContentType" placeholder="(empty)"/>' +
                                '                        </div>' +
                                '                        <div>' +
                                '                            <label for="content2">Content:</label>' +
                                '' +
                                '                            <div id="content2" class="editor verticalAlignTop"></div>' +
                                '' +
                                '                        </div>' +
                                '                    </div>' +
                                '                    <div class="script ui-helper-hidden">' +
                                '                        <div id="thenScript" class="editor"></div>' +
                                '                    </div>' +
                                '                </div>' +
                                '            </div>' +
                                '        </div>' +
                                '        <div class="action">' +
                                '            <button id="submit" type="button">OK</button>' +
                                '            <button id="cancel" type="button">Cancel</button>' +
                                '        </div>' +
                                '    </form>' +
                                '</div>'
                        );

                    mockUtils.navigate = mockFunction('navigate');
                    this.view = new EditStubView();
                }
            });

            test('cancel navigates to index', 0, function () {
                $('#cancel').click();

                verify(mockUtils.navigate)('index.html');
            });

            test('submit calls editStub', 0, function () {
                this.view.editStub = mockFunction();
                $('#submit').click();

                verify(this.view.editStub)();
            });

            test('clicking staticThen sets expected css', function () {
                var $then = $('#then');
                $('#staticThen').click();

                assertThat($then.find('.static').hasClass('ui-helper-hidden'), is(false));
                assertThat($then.find('.script').hasClass('ui-helper-hidden'), is(true));
            });

            test('clicking scriptThen sets expected css', function () {
                var $then = $('#then');
                $('#scriptThen').click();

                assertThat($then.find('.script').hasClass('ui-helper-hidden'), is(false));
                assertThat($then.find('.static').hasClass('ui-helper-hidden'), is(true));
            });

            test('clicking staticThen refreshes codemirror', 0, function () {
                this.view.content2.refresh = mockFunction();
                this.view.thenScript.refresh = mockFunction();
                $('#staticThen').click();

                verify(this.view.content2.refresh)();
                verify(this.view.thenScript.refresh)();
            });

            test('clicking scriptThen refreshes codemirror', 0, function () {
                this.view.content2.refresh = mockFunction();
                this.view.thenScript.refresh = mockFunction();
                $('#scriptThen').click();

                verify(this.view.content2.refresh)();
                verify(this.view.thenScript.refresh)();
            });

            test('contentType change updates codemirror mode', function () {
                $('#contentType').val('application/json').change();

                assertThat(this.view.content.getOption('mode'), equalTo('application/json'));
            });

            test('contentType2 change updates condemirror mode', function () {
                $('#contentType2').val('application/json').change();

                assertThat(this.view.content2.getOption('mode'), equalTo('application/json'));
            });

            test('editStub triggers with expected map', function () {
                var actualData;
                $(this.view).on('edit-stub.swivelView', function (event, data) {
                    actualData = data;
                });
                this.view.id = '1';
                $('#path').val('some/path');
                $('#description').val('description');
                $('#method').val('GET');
                $('#query').val('query');
                $('#remoteAddress').val('remoteAddress');
                $('#contentType').val('application/json');
                this.view.content.setValue('{"application":"json"}');
                this.view.whenScript.setValue('true;');
                $('#statusCode').val(200);
                $('#contentType2').val('application/xml');
                this.view.content2.setValue('<xml></xml>');

                this.view.editStub();
                deepEqual(actualData, {
                    path: 'some/path',
                    id: '1',
                    description: 'description',
                    when: {
                        method: 'GET',
                        query: 'query',
                        remoteAddress: 'remoteAddress',
                        contentType: 'application/json',
                        content: '{"application":"json"}',
                        script: 'true;'
                    }, then: {
                        statusCode: 200,
                        contentType: 'application/xml',
                        content: '<xml></xml>'
                    }});
            });

            test('editStub triggers with thenScript', function () {
                var actualData;
                $(this.view).on('add-stub.swivelView', function (event, data) {
                    actualData = data;
                });
                $('#path').val('some/path');
                $('#description').val('description');
                $('#method').val('GET');
                $('#scriptThen').click();
                this.view.thenScript.setValue('(function(){responseFactory.createResponse({statusCode:200})})();');

                this.view.editStub();
                deepEqual(actualData, {
                    path: 'some/path',
                    description: 'description',
                    when: {
                        method: 'GET'
                    }, then: {
                        script: '(function(){responseFactory.createResponse({statusCode:200})})();'
                    }});
            });

            test('setStub populates fields', function () {
                var stubData = {
                    id: 1,
                    description: 'description',
                    when: {
                        method: 'GET',
                        query: 'query',
                        remoteAddress: 'remoteAddress',
                        contentType: 'application/json',
                        content: '{"application":"json"}',
                        script: 'true;'
                    }, then: {
                        statusCode: 200,
                        contentType: 'application/xml',
                        content: '<xml></xml>'
                    }};

                this.view.setStub('some/path', stubData);

                assertThat($('#path').val(), equalTo('some/path'));
                assertThat(this.view.id, equalTo(stubData.id));
                assertThat($('#description').val(), equalTo(stubData.description));
                assertThat($('#method').val(), equalTo(stubData.when.method));
                assertThat($('#query').val(), equalTo(stubData.when.query));
                assertThat($('#remoteAddress').val(), equalTo(stubData.when.remoteAddress));
                assertThat($('#contentType').val(), equalTo(stubData.when.contentType));
                assertThat(this.view.content.getValue(), equalTo(stubData.when.content));

                assertThat($('#statusCode').val(), equalTo(stubData.then.statusCode));
                assertThat($('#contentType2').val(), equalTo(stubData.then.contentType));
                assertThat(this.view.content2.getValue(), equalTo(stubData.then.content));

                assertThat($('#staticThen').prop('checked'), is(true));

                assertThat(this.view.content.getOption('mode'), equalTo(stubData.when.contentType));
                assertThat(this.view.content2.getOption('mode'), equalTo(stubData.then.contentType));
            });

            test('setStub with then script populates fields', function () {
                var stubData = {
                    id: 1,
                    description: 'description',
                    when: {
                        method: 'GET'
                    }, then: {
                        script: 'responseFactory.createResponse({});'
                    }};

                this.view.setStub('some/path', stubData);

                assertThat($('#path').val(), equalTo('some/path'));
                assertThat(this.view.id, equalTo(stubData.id));
                assertThat($('#description').val(), equalTo(stubData.description));
                assertThat($('#method').val(), equalTo(stubData.when.method));

                assertThat(this.view.thenScript.getValue(), equalTo(stubData.then.script));

                assertThat($('#scriptThen').prop('checked'), is(true));
            });
        });
});
