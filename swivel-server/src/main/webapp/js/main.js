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
        fileDependencies = ['HtmlClient', 'TestSwivelServer'];

    function startApp(HtmlClient, SwivelServer) {
        new HtmlClient(new SwivelServer(href.substr(0, href.lastIndexOf('/'))));
    }

    if (loadedFromFile) {
        //HACKTAG:TJH - the minifier ignores dynamic calls to require
        require(fileDependencies, startApp);
    } else {
        require(['HtmlClient', 'SwivelServer'], startApp);
    }
})(window.location.href);
