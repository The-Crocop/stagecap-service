(function() {
    'use strict';
    angular
        .module('stageCapServiceApp')
        .factory('Subtitle', Subtitle);

    Subtitle.$inject = ['$resource'];

    function Subtitle ($resource) {
        var resourceUrl =  'api/subtitles/:id';

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
