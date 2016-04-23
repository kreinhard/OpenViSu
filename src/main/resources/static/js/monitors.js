(function () {
    'use strict';

    var openVisuApp = angular.module('application').controller(
        'MonitorController', MonitorController);

    MonitorController.$inject = ['$scope', '$state', 'Monitor'];

    function MonitorController($scope, $state, Monitor) {
        var vm = this;
        vm.monitors = [];
        vm.loadAll = function () {
            Monitor.query(function (result) {
                vm.monitors = result;
            });
        };

        vm.loadAll();

    }

    openVisuApp.factory('Monitor', Monitor);

    Monitor.$inject = ['$resource'];

    function Monitor($resource) {
        var resourceUrl = 'api/monitors/:id';

        return $resource(resourceUrl, {}, {
            'query': {
                method: 'GET',
                isArray: true
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': {
                method: 'PUT'
            }
        });
    }

    openVisuApp.config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('monitor', {
                parent: 'entity',
                url: '/monitor',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Monitors'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/monitor/monitors.html',
                        controller: 'MonitorController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {}
            })
            .state('monitor-detail', {
                parent: 'entity',
                url: '/monitor/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Monitor'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/monitor/monitor-detail.html',
                        controller: 'MonitorDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Monitor', function ($stateParams, Monitor) {
                        return Monitor.get({id: $stateParams.id});
                    }]
                }
            })
            .state('monitor.new', {
                parent: 'monitor',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
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
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('monitor', null, {reload: true});
                    }, function () {
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
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/monitor/monitor-dialog.html',
                        controller: 'MonitorDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Monitor', function (Monitor) {
                                return Monitor.get({id: $stateParams.id});
                            }]
                        }
                    }).result.then(function () {
                        $state.go('monitor', null, {reload: true});
                    }, function () {
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
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/monitor/monitor-delete-dialog.html',
                        controller: 'MonitorDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Monitor', function (Monitor) {
                                return Monitor.get({id: $stateParams.id});
                            }]
                        }
                    }).result.then(function () {
                        $state.go('monitor', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }
})();
