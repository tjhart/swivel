requirejs.config({
    baseUrl: '/test/src/main/webapp/js',
    paths: {
        jQuery: 'lib/jquery-1.8.3.min',
        jsTree: 'lib/jstree-v.pre1.0/jquery.jstree',
        'jQuery-ui': 'lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.min',
        json2: 'lib/json2',

        jsHamcrest: '/test/src/test/webapp/js/lib/jshamcrest-0.7.0.min',
        jsMockito: '/test/src/test/webapp/js/lib/jsmockito-1.0.4-minified',
        Squire: '/test/src/test/webapp/js/lib/Squire'
    },
    shim: {
        jQuery: {deps: ['json2'], exports: 'jQuery'},
        jsTree: ['jQuery'],
        'jQuery-ui': ['jQuery'],
        json2: {exports: 'JSON'},

        jsHamcrest: {exports: 'JsHamcrest'},
        jsMockito: {deps: ['jsHamcrest'], exports: 'JsMockito'}
    }
});
