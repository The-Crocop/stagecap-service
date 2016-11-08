(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('SubtitleDialogController', SubtitleDialogController);

    SubtitleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Subtitle', 'Project'];

    function SubtitleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Subtitle, Project) {
        var vm = this;
        vm.subtitle = entity;
        vm.projects = Project.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('stageCapServiceApp:subtitleUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.subtitle.id !== null) {
                Subtitle.update(vm.subtitle, onSaveSuccess, onSaveError);
            } else {
                Subtitle.save(vm.subtitle, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setFile = function ($file, subtitle) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        subtitle.file = base64Data;
                        subtitle.fileContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();
