(function() {
    'use strict';

    angular
        .module('openViSuApp')
        .controller('MonitorController', MonitorController);

    MonitorController.$inject = ['$scope', '$state', 'Monitor'];

    function MonitorController ($scope, $state, Monitor) {
        var vm = this;
        vm.monitors = [];
        vm.loadAll = function() {
            Monitor.query(function(result) {
                vm.monitors = result;
            });
        };

        vm.loadAll();
        
    }
})();
