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
                if (!data[path].stubs) {
                    delete data[path];
                }
            }

            return ajaxResultBuilder(data);
        };

        this.deleteStub = function (stubData) {
            var path = stubData.path, id = stubData.id, stubs, i, stub;
            if (data[path] && data[path].stubs) {
                stubs = data[path].stubs;
                for (i = 0; i < stubs.length; i++) {
                    if (stubs[i].id === id) {
                        stubs.splice(i, 1);
                        break;
                    }
                }
                if (stubs.length === 0) {
                    delete data[path].stubs;
                    if (!data[path].shunt) {
                        delete data[path];
                    }
                }
            }

            return ajaxResultBuilder(data);
        }
    }
});