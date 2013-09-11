define(function () {

    var data = {
        'some/path': {
            shunt: 'some shunt description',
            stubs: [
                {id: 1, description: 'some stub description'},
                {id: 2, description: 'some other stub description'}
            ]
        }
    };
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
            return ajaxResultBuilder(data);
        };

        this.deleteShunt = function (path) {
            if (data[path] && data[path].shunt) {
                delete data[path].shunt;
            }

            return ajaxResultBuilder(data);
        }
    }
});