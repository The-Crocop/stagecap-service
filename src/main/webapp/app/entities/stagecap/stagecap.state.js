/**
 * Created by crocop on 11.05.16.
 */
(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('stagecap', {
                parent: 'entity',
                url: '/stagecap?projectId',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'stageCapServiceApp.stagecap.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/stagecap/stagecap.html',
                        controller: 'StageCapController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stagecap');
                        return $translate.refresh();
                    }],
                },
            });
    }

})();
