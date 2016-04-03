(function() {
    'use strict';

    angular
        .module('openViSuApp')
        .controller('MonitorDetailController', MonitorDetailController);

    MonitorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Monitor'];

    function MonitorDetailController($scope, $rootScope, $stateParams, entity, Monitor) {
        var vm = this;
        vm.monitor = entity;
        vm.load = function (id) {
            Monitor.get({id: id}, function(result) {
                vm.monitor = result;
            });
        };
        var unsubscribe = $rootScope.$on('openViSuApp:monitorUpdate', function(event, result) {
            vm.monitor = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
