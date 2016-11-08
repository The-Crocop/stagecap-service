(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('subtitle', {
            parent: 'entity',
            url: '/subtitle?projectId',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'stageCapServiceApp.subtitle.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subtitle/subtitles.html',
                    controller: 'SubtitleController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('subtitle');
                    $translatePartialLoader.addPart('language');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                project: ['$stateParams', 'Project', function($stateParams, Project){
                 return Project.get({id:$stateParams.projectId});
                }]
            }
        })
        .state('subtitle-detail', {
            parent: 'entity',
            url: '/subtitle/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'stageCapServiceApp.subtitle.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/subtitle/subtitle-detail.html',
                    controller: 'SubtitleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('subtitle');
                    $translatePartialLoader.addPart('language');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Subtitle', function($stateParams, Subtitle) {
                    return Subtitle.get({id : $stateParams.id});
                }]
            }
        })
        .state('subtitle.new', {
            parent: 'subtitle',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subtitle/subtitle-dialog.html',
                    controller: 'SubtitleDialogController',
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
                    $state.go('subtitle', null, { reload: true });
                }, function() {
                    $state.go('subtitle');
                });
            }]
        })
        .state('subtitle.edit', {
            parent: 'subtitle',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subtitle/subtitle-dialog.html',
                    controller: 'SubtitleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Subtitle', function(Subtitle) {
                            return Subtitle.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('subtitle', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('subtitle.delete', {
            parent: 'subtitle',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/subtitle/subtitle-delete-dialog.html',
                    controller: 'SubtitleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Subtitle', function(Subtitle) {
                            return Subtitle.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('subtitle', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
