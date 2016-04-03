(function() {
    'use strict';
    angular
        .module('openViSuApp')
        .factory('Monitor', Monitor);

    Monitor.$inject = ['$resource'];

    function Monitor ($resource) {
        var resourceUrl =  'api/monitors/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
