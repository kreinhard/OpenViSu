(function() {
    'use strict';

    angular
        .module('openViSuApp')
        .controller('MonitorDeleteController',MonitorDeleteController);

    MonitorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Monitor'];

    function MonitorDeleteController($uibModalInstance, entity, Monitor) {
        var vm = this;
        vm.monitor = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Monitor.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
