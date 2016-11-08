(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('SubtitleDetailController', SubtitleDetailController);

    SubtitleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Subtitle', 'Project'];

    function SubtitleDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Subtitle, Project) {
        var vm = this;
        vm.subtitle = entity;
        
        var unsubscribe = $rootScope.$on('stageCapServiceApp:subtitleUpdate', function(event, result) {
            vm.subtitle = result;
        });
        $scope.$on('$destroy', unsubscribe);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
    }
})();
