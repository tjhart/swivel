requirejs.config({
    baseUrl: '/test/src/main/webapp/js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min',

        jsHamcrest: '/test/src/test/webapp/js/lib/jshamcrest-0.7.0.min',
        jsMockito: '/test/src/test/webapp/js/lib/jsmockito-1.0.4-minified',
        Squire: '/test/src/test/webapp/js/lib/Squire'
    },
    shim: {
        jQuery: {exports: 'jQuery'},
        jsTree: ['jQuery'],
        'jQuery-ui': ['jQuery'],

        jsHamcrest: {exports: 'JsHamcrest'},
        jsMockito: {deps: ['jsHamcrest'], exports: 'JsMockito'}
    }
});
