(function() {
    'use strict';

    angular
        .module('openViSuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('monitor', {
            parent: 'entity',
            url: '/monitor',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'openViSuApp.monitor.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/monitor/monitors.html',
                    controller: 'MonitorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('monitor');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('monitor-detail', {
            parent: 'entity',
            url: '/monitor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'openViSuApp.monitor.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/monitor/monitor-detail.html',
                    controller: 'MonitorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('monitor');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Monitor', function($stateParams, Monitor) {
                    return Monitor.get({id : $stateParams.id});
                }]
            }
        })
        .state('monitor.new', {
            parent: 'monitor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/monitor/monitor-dialog.html',
                    controller: 'MonitorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('monitor', null, { reload: true });
                }, function() {
                    $state.go('monitor');
                });
            }]
        })
        .state('monitor.edit', {
            parent: 'monitor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/monitor/monitor-dialog.html',
                    controller: 'MonitorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Monitor', function(Monitor) {
                            return Monitor.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('monitor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('monitor.delete', {
            parent: 'monitor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/monitor/monitor-delete-dialog.html',
                    controller: 'MonitorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Monitor', function(Monitor) {
                            return Monitor.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('monitor', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
