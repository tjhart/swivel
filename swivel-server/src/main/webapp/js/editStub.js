"use strict";

requirejs.config({
    baseUrl: 'js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min',
        json2: 'lib/json2',
        codemirror: 'lib/codemirror-3.18/lib/codemirror',
        'cm-javascript': 'lib/codemirror-3.18/mode/javascript/javascript',
        'cm-xml': 'lib/codemirror-3.18/mode/xml/xml',
        'cm-matchbrackets': 'lib/codemirror-3.18/addon/edit/matchbrackets',
        'cm-closebrackets': 'lib/codemirror-3.18/addon/edit/closebrackets'

    },
    shim: {
        jQuery: {deps: ['json2'], exports: 'jQuery'},
        'jQuery-ui': ['jQuery'],
        json2: {exports: 'JSON'},
        codemirror: {exports: 'CodeMirror'},
        'cm-javascript': ['codemirror'],
        'cm-xml': ['codemirror'],
        'cm-matchbrackets': ['codemirror'],
        'cm-closebrackets': ['codemirror']
    }
});

(function (href) {
    var loadedFromFile = href.match(/^file:/),
        fileDependencies = ['jQuery', 'EditStubController', 'EditStubView', 'TestSwivelServer'];

    function startApp($, EditStubController, EditStubView, SwivelServer) {
        new EditStubController(new SwivelServer(href.substr(0, href.lastIndexOf('/'))),
            new EditStubView($('#content')))
    }

    if (loadedFromFile) {
        //HACKTAG:TJH - the minifier ignores dynamic calls to require. This call keeps
        //the test data from being included in the minified version
        require(fileDependencies, startApp);
    } else {
        require(['jQuery', 'EditStubController', 'EditStubView', 'SwivelServer'], startApp);
    }

})(window.location.href);