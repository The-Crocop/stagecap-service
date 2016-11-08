(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('AudioDescriptionDeleteController',AudioDescriptionDeleteController);

    AudioDescriptionDeleteController.$inject = ['$uibModalInstance', 'entity', 'AudioDescription'];

    function AudioDescriptionDeleteController($uibModalInstance, entity, AudioDescription) {
        var vm = this;
        vm.audioDescription = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            AudioDescription.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
