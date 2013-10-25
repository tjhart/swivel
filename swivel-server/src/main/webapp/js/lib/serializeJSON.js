"use strict";
define(['jQuery'], function ($) {
    $.fn.serializeJSON = function (options) {
        var json = {}, opts = $.extend({
            checkBoxesAsBooleans: false
        }, options || {}), checkBoxes = {};

        if (opts.checkBoxesAsBooleans) {
            this.find('[type="checkbox"]').each(function () {
                checkBoxes[this.name] = true;
            });
        }
        $.each(this.serializeArray(), function () {
            var entry = json[this.name], val = this.value;
            if (checkBoxes[this.name]) {
                val = val === 'on';
            }
            if (val && val.length) {
                if (entry) {
                    if (!entry.push) {
                        entry = json[this.name] = [entry];
                    }
                    entry.push(val || '');
                } else {
                    json[this.name] = val || '';
                }
            }
        });
        return json;
    };
});