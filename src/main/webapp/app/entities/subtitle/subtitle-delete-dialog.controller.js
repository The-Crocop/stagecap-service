(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('SubtitleDeleteController',SubtitleDeleteController);

    SubtitleDeleteController.$inject = ['$uibModalInstance', 'entity', 'Subtitle'];

    function SubtitleDeleteController($uibModalInstance, entity, Subtitle) {
        var vm = this;
        vm.subtitle = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Subtitle.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
