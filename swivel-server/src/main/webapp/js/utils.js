"use strict";

define(['jQuery'], function ($) {
    return {
        getQuery: function () {
            var query = {};
            $.each(decodeURIComponent(window.location.search.substr(1)).split('&'), function (index, item) {
                var keyVal = item.split('='), element, val;
                if (keyVal.length == 1) {
                    keyVal[1] = true;
                }
                val = keyVal[1];

                element = query[keyVal[0]];
                if (!element) {
                    element = val;
                } else {
                    if ($.isArray(element)) {
                        element.push(val)
                    } else {
                        element = [element, val];
                    }
                }

                query[keyVal[0]] = element;
            });

            return query;
        },

        //useful for testing
        navigate: function (location) { window.location = location; }
    }
});