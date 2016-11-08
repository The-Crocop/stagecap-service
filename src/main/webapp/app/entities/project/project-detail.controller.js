(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$sce', '$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Project', 'Subtitle', 'AudioDescription', 'ProjectTimestampService'];

    function ProjectDetailController($sce, $scope, $rootScope, $stateParams, DataUtils, entity, Project, Subtitle, AudioDescription, ProjectTimestampService) {
        var vm = this;
        vm.project = entity;
       AudioDescription.query({projectId:vm.project.id},function(retVal){
           var ad;
           if(retVal.length > 0){
               ad = retVal[0];
               vm.type = ad.fileContentType;
               vm.file = $sce.trustAsResourceUrl("data:"+vm.type+";base64,"+ad.file);

           }
       });

        var unsubscribe = $rootScope.$on('stageCapServiceApp:projectUpdate', function(event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);

        ProjectTimestampService.receive().then(null, null, function(update) {
            console.log(update);
            if(update.timestamp !== null){
                vm.project.seeker = update.timestamp;
            }
        });


        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;




        vm.change = function(sliderId,modelValue,highValue){
           // vm.updateTime(modelValue);
        }

        vm.sliderOptions = {
            floor:0,
            ceil:30000,
            onChange: vm.change
        }

    }
})();
