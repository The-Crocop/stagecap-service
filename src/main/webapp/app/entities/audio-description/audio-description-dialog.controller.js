(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('AudioDescriptionDialogController', AudioDescriptionDialogController);

    AudioDescriptionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'AudioDescription', 'Project'];

    function AudioDescriptionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, AudioDescription, Project) {
        var vm = this;
        vm.audioDescription = entity;
        vm.projects = Project.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('stageCapServiceApp:audioDescriptionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.audioDescription.id !== null) {
                AudioDescription.update(vm.audioDescription, onSaveSuccess, onSaveError);
            } else {
                AudioDescription.save(vm.audioDescription, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setFile = function ($file, audioDescription) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        audioDescription.file = base64Data;
                        audioDescription.fileContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();
