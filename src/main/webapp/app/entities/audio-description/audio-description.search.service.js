(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .factory('AudioDescriptionSearch', AudioDescriptionSearch);

    AudioDescriptionSearch.$inject = ['$resource'];

    function AudioDescriptionSearch($resource) {
        var resourceUrl =  'api/_search/audio-descriptions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
