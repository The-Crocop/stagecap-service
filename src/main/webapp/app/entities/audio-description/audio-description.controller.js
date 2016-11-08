(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('AudioDescriptionController', AudioDescriptionController);

    AudioDescriptionController.$inject = ['$scope', '$state', 'DataUtils', 'AudioDescription', 'AudioDescriptionSearch', 'project'];

    function AudioDescriptionController ($scope, $state, DataUtils, AudioDescription, AudioDescriptionSearch, project) {
        var vm = this;
        vm.audioDescriptions = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.loadAll = function() {
            AudioDescription.query({projectId:vm.project.id},function(result) {
                vm.audioDescriptions = result;
            });
        };

        project.$promise.then(function(data){
            vm.project = data;
            vm.loadAll();
        });

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AudioDescriptionSearch.query({query: vm.searchQuery}, function(result) {
                vm.audioDescriptions = result;
            });
        };


    }
})();
