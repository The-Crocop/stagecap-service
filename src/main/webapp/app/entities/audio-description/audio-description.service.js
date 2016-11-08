(function() {
    'use strict';
    angular
        .module('stageCapServiceApp')
        .factory('AudioDescription', AudioDescription);

    AudioDescription.$inject = ['$resource'];

    function AudioDescription ($resource) {
        var resourceUrl =  'api/audio-descriptions/:id';

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
