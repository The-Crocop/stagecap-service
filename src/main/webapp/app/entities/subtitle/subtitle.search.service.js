(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .factory('SubtitleSearch', SubtitleSearch);

    SubtitleSearch.$inject = ['$resource'];

    function SubtitleSearch($resource) {
        var resourceUrl =  'api/_search/subtitles/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
