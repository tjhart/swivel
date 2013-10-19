"use strict";

requirejs.config({
    baseUrl: '../../main/webapp/js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min',
        json2: 'lib/json2',
        codemirror: 'lib/codemirror-3.18/lib/codemirror',
        'cm-javascript': 'lib/codemirror-3.18/mode/javascript/javascript',
        'cm-xml': 'lib/codemirror-3.18/mode/xml/xml',
        'cm-matchbrackets': 'lib/codemirror-3.18/addon/edit/matchbrackets',
        'cm-closebrackets': 'lib/codemirror-3.18/addon/edit/closebrackets',

        test: '../../../test/webapp/js',
        jsHamcrest: '../../../test/webapp/js/lib/jshamcrest-0.7.0.min',
        jsMockito: '../../../test/webapp/js/lib/jsmockito-1.0.4-minified'
    },
    shim: {
        jQuery: {deps: ['json2'], exports: 'jQuery'},
        jsTree: ['jQuery'],
        'jQuery-ui': ['jQuery'],
        json2: {exports: 'JSON'},
        codemirror: {exports: 'CodeMirror'},
        'cm-javascript': ['codemirror'],
        'cm-xml': ['codemirror'],
        'cm-matchbrackets': ['codemirror'],
        'cm-closebrackets': ['codemirror'],

        jsHamcrest: {exports: 'JsHamcrest'},
        jsMockito: {deps: ['jsHamcrest'], exports: 'JsMockito'}
    }
});

(function () {
    require(['test/MainPageControllerTest', 'test/MainPageViewTest', 'test/MainPageViewDialogTest',
        'test/SwivelServerTest', 'test/EditStubControllerTest']);
})();