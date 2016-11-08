/**
 * Created by crocop on 06.05.16.
 */
(function() {
    'use strict';

    var subViewer = {
        template: '<pre>{{$ctrl.currentSub}}&nbsp;</pre>' +
                    '<div ng-show="$ctrl.showState">state:{{$ctrl.state}}</div>' +
                    '<div ng-show="$ctrl.showTime">time:{{$ctrl.time | date: "HH:mm:ss"}}</div>',
        controller: subViewerController,
        bindings: {
            projectId: '<',
            language: '<',
            showState: '<',
            showTime: '<'
        }
    };

    angular
        .module('stageCapServiceApp')
        .component('subViewer', subViewer);

    subViewerController.$inject = ['$scope', 'ProjectTimestampService'];

    function subViewerController($scope, ProjectTimestampService) {
        var vm = this;

        vm.currentSub = '';
        vm.state = 'STOPPED';
        vm.time = 0;


        ProjectTimestampService.receive().then(null, null, function(update) {
            console.log(update);
            switch (update.playerInstruction){
                case 'PLAY':
                    vm.state = update.state;
                    break;
                case 'STOP':
                    vm.currentSub = '';
                    vm.state = update.state;
                    break;
                case 'PAUSE':
                    vm.state = update.state;
                    break;
                case 'JUMP':
                    if(update.timestamp !== null){
                        vm.time = update.timestamp;
                    }
                    break;
                case 'UPDATE_TEXT':
                    vm.time = update.timestamp;
                    vm.currentSub = update.subtitles[vm.language];
                    vm.state = update.state;
                    break;
                case 'UPDATE_TIME':
                    vm.time = update.timestamp;
                    vm.state = update.state;
                case 'INIT':
                    vm.time = update.timestamp;
                    if(update.subtitles !== null)vm.currentSub = update.subtitles[vm.language];
                    vm.state = update.state;
                default:
                    //do nothing

            }

        });

        ProjectTimestampService.subscribe(vm.projectId);

        $scope.$on('$destroy', function () {
            ProjectTimestampService.unsubscribe(vm.projectId);
        });

    }


})();
