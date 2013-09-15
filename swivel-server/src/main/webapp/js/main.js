"use strict";

requirejs.config({
    baseUrl: 'js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min'
    },
    shim: {
        jQuery: {exports: 'jQuery'},
        jsTree: ['jQuery'],
        'jQuery-ui': ['jQuery']
    }
});

(function (href) {
    var loadedFromFile = href.match(/^file:/),
        fileDependencies = ['jQuery', 'HtmlClientController', 'HtmlClientView', 'TestSwivelServer'];

    function startApp($, HtmlClientController, HtmlClientView, SwivelServer) {
        new HtmlClientController(new SwivelServer(href.substr(0, href.lastIndexOf('/'))),
            new HtmlClientView($('#addElementDialog')));
    }

    if (loadedFromFile) {
        //HACKTAG:TJH - the minifier ignores dynamic calls to require. This call keeps
        //the test data from being included in the minified version
        require(fileDependencies, startApp);
    } else {
        require(['jQuery', 'HtmlClientController', 'HtmlClientView', 'SwivelServer'], startApp);
    }
})(window.location.href);
