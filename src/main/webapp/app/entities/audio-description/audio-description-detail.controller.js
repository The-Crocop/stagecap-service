(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('AudioDescriptionDetailController', AudioDescriptionDetailController);

    AudioDescriptionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'AudioDescription', 'Project'];

    function AudioDescriptionDetailController($scope, $rootScope, $stateParams, DataUtils, entity, AudioDescription, Project) {
        var vm = this;
        vm.audioDescription = entity;
        console.log(vm.audioDescription.file);
        var unsubscribe = $rootScope.$on('stageCapServiceApp:audioDescriptionUpdate', function(event, result) {
            vm.audioDescription = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();
