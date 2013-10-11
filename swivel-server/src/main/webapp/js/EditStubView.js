"use strict";

define(['jQuery', 'jQuery-ui'], function ($) {
    var CONTENT_TYPES = ['application/json', 'application/xml', 'application/pdf', 'text/html', 'application/zip',
            'text/plain', 'text/css'],
        STATUS_CODES = ["100", "101",
            "200", "201", "202", "203", "204", "205", "206",
            "300", "301", "302", "303", "304", "305", "307",
            "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414",
            "415", "416", "417",
            "500", "501", "502", "503", "504", "505"],
        ANTI_TYPE = {static: 'script', script: 'static'};

    return function () {
        var $view = $(this);

        this.setStub = function (stub) {

        };

        this.style = function () {
            $('button').button();
            $('.type').buttonset();
            $('#method').menu();
            $('#contentType,#contentType2').autocomplete({source: CONTENT_TYPES});
            $('#statusCode').autocomplete({source: STATUS_CODES});
        };

        this.style();
        $('.type [type="radio"]').click(function (e) {
            var $target = $(e.target), whenOrThen, divId, visibleClass;
            whenOrThen = $target.attr('name').substr(0, 4);
            divId = ['#', whenOrThen, ' '].join('');
            visibleClass = $target.val();
            $([divId, '.', visibleClass].join('')).removeClass('ui-helper-hidden');
            $([divId, '.', ANTI_TYPE[visibleClass]].join('')).addClass('ui-helper-hidden');
        });
    }
});