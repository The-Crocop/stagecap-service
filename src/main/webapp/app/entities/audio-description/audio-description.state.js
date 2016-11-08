(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('audio-description', {
            parent: 'entity',
            url: '/audio-description?projectId',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'stageCapServiceApp.audioDescription.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audio-description/audio-descriptions.html',
                    controller: 'AudioDescriptionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('audioDescription');
                    $translatePartialLoader.addPart('language');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                project: ['$stateParams', 'Project', function($stateParams, Project){
                    return Project.get({id:$stateParams.projectId});
                }]
            }
        })
        .state('audio-description-detail', {
            parent: 'entity',
            url: '/audio-description/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'stageCapServiceApp.audioDescription.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/audio-description/audio-description-detail.html',
                    controller: 'AudioDescriptionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('audioDescription');
                    $translatePartialLoader.addPart('language');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'AudioDescription', function($stateParams, AudioDescription) {
                    return AudioDescription.get({id : $stateParams.id});
                }]
            }
        })
        .state('audio-description.new', {
            parent: 'audio-description',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-description/audio-description-dialog.html',
                    controller: 'AudioDescriptionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                language: null,
                                file: null,
                                fileContentType: null,
                                id: null,
                                project:{
                                    id:$stateParams.projectId
                                }
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('audio-description', null, { reload: true });
                }, function() {
                    $state.go('audio-description');
                });
            }]
        })
        .state('audio-description.edit', {
            parent: 'audio-description',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-description/audio-description-dialog.html',
                    controller: 'AudioDescriptionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['AudioDescription', function(AudioDescription) {
                            return AudioDescription.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('audio-description', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('audio-description.delete', {
            parent: 'audio-description',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/audio-description/audio-description-delete-dialog.html',
                    controller: 'AudioDescriptionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['AudioDescription', function(AudioDescription) {
                            return AudioDescription.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('audio-description', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
