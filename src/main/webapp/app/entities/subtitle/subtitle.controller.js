(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('SubtitleController', SubtitleController);

    SubtitleController.$inject = ['$scope', '$state', 'DataUtils', 'Subtitle', 'SubtitleSearch', 'project'];

    function SubtitleController ($scope, $state, DataUtils, Subtitle, SubtitleSearch, project) {
        var vm = this;
        vm.subtitles = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;
        vm.loadAll = function() {
            Subtitle.query({projectId:vm.project.id},function(result) {
                vm.subtitles = result;
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
            SubtitleSearch.query({query: vm.searchQuery}, function(result) {
                vm.subtitles = result;
            });
        };


    }
})();
