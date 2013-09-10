define(function () {
    return function () {
        function ajaxResultBuilder(data) {
            return {
                done: function (handler) {
                    handler(data);
                    return this;
                },

                fail: function (handler) {
                    return this;
                },

                always: function (handler) {
                    handler(data);
                    return this;
                }
            }
        }

        this.getConfig = function () {
            return ajaxResultBuilder({
                'some/path': {
                    shunt: 'some shunt description',
                    stubs: ['some stub description', 'some other stub description']
                }
            });
        }
    }
});