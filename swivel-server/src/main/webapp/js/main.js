"use strict";

requirejs.config({
    baseUrl: 'js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree'
    },
    shim: {
        jQuery: {exports: 'jQuery'},
        jsTree: ['jQuery']
    }
});

(function (href) {
    var loadedFromFile = href.match(/^file:/),
        fileDependencies = ['HtmlClientController', 'HtmlClientView', 'TestSwivelServer'];

    function startApp(HtmlClientController, HtmlClientView, SwivelServer) {
        new HtmlClientController(new SwivelServer(href.substr(0, href.lastIndexOf('/'))), new HtmlClientView());
    }

    if (loadedFromFile) {
        //HACKTAG:TJH - the minifier ignores dynamic calls to require
        require(fileDependencies, startApp);
    } else {
        require(['HtmlClientController', 'HtmlClientView', 'SwivelServer'], startApp);
    }
})(window.location.href);
