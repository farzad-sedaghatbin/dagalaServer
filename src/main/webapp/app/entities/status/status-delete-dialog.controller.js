(function() {
    'use strict';

    angular
        .module('baseApp')
        .controller('StatusDeleteController',StatusDeleteController);

    StatusDeleteController.$inject = ['$uibModalInstance', 'entity', 'Status'];

    function StatusDeleteController($uibModalInstance, entity, Status) {
        var vm = this;

        vm.status = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Status.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
