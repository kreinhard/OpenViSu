(function() {
    'use strict';

    angular
        .module('openViSuApp')
        .controller('MonitorDialogController', MonitorDialogController);

    MonitorDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Monitor'];

    function MonitorDialogController ($scope, $stateParams, $uibModalInstance, entity, Monitor) {
        var vm = this;
        vm.monitor = entity;
        vm.load = function(id) {
            Monitor.get({id : id}, function(result) {
                vm.monitor = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('openViSuApp:monitorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.monitor.id !== null) {
                Monitor.update(vm.monitor, onSaveSuccess, onSaveError);
            } else {
                Monitor.save(vm.monitor, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
