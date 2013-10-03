"use strict";

requirejs.config({
    baseUrl: 'js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min',
        json2: 'lib/json2'

    },
    shim: {
        jQuery: {deps: ['json2'], exports: 'jQuery'},
        jsTree: ['jQuery'],
        'jQuery-ui': ['jQuery'],
        json2: {exports: 'JSON'}
    }
});

(function (href) {
    var loadedFromFile = href.match(/^file:/),
        fileDependencies = ['jQuery', 'MainPageController', 'MainPageView', 'TestSwivelServer'];

    function startApp($, MainPageController, MainPageView, SwivelServer) {
        new MainPageController(new SwivelServer(href.substr(0, href.lastIndexOf('/'))),
            new MainPageView($('#currentConfig'), $('#reset')));
    }

    if (loadedFromFile) {
        //HACKTAG:TJH - the minifier ignores dynamic calls to require. This call keeps
        //the test data from being included in the minified version
        require(fileDependencies, startApp);
    } else {
        require(['jQuery', 'MainPageController', 'MainPageView', 'SwivelServer'], startApp);
    }
})(window.location.href);
