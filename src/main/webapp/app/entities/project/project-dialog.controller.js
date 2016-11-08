(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('ProjectDialogController', ProjectDialogController);

    ProjectDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Project', 'Subtitle', 'AudioDescription'];

    function ProjectDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Project, Subtitle, AudioDescription) {
        var vm = this;
        vm.project = entity;
        vm.subtitles = Subtitle.query();
        vm.audiodescriptions = AudioDescription.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('stageCapServiceApp:projectUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.project.id !== null) {
                Project.update(vm.project, onSaveSuccess, onSaveError);
            } else {
                Project.save(vm.project, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.setImage = function ($file, project) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        project.image = base64Data;
                        project.imageContentType = $file.type;
                    });
                });
            }
        };

        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
    }
})();
