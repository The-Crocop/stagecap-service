/**
 * Created by crocop on 06.05.16.
 */
(function() {
    'use strict';

    var subController = {
        template: '<button ng-hide="$ctrl.state == \'PLAYING\'" class="btn btn-sm btn-default" ng-click="$ctrl.play()" title="play"><span class="glyphicon glyphicon-play"></span></button>' +
        '<button ng-show="$ctrl.state == \'PLAYING\'" class="btn btn-sm btn-default" ng-click="$ctrl.pause()" title="pause"><span class="glyphicon glyphicon-pause"></span></button>' +
        '<button class="btn btn-sm btn-default" ng-click="$ctrl.stop()" title="stop"><span class="glyphicon glyphicon-stop"></span></button>',
        controller: subControllerController,
        bindings: {
            projectId: '<'
        }
    };

    angular
        .module('stageCapServiceApp')
        .component('subController', subController);

    subControllerController.$inject = ['$scope', 'ProjectTimestampService'];

    function subControllerController($scope, ProjectTimestampService) {
        var vm = this;

        vm.currentSub = '';
        vm.state = 'STOPPED';
        vm.time = 0;

        ProjectTimestampService.subscribe(vm.projectId);

        vm.play = function(){
            ProjectTimestampService.sendUpdate(vm.projectId,
                {
                    'playerInstruction':'PLAY',
                });
        }

        vm.stop = function() {
            ProjectTimestampService.sendUpdate(vm.projectId,
                {
                    'playerInstruction':'STOP',
                    'timestamp': 0
                });
        }

        vm.updateTime = function(timestamp){
            ProjectTimestampService.sendUpdate(vm.projectId,
                {
                    'playerInstruction':'JUMP',
                    'timestamp': timestamp
                });
        }

        vm.pause = function() {
            ProjectTimestampService.sendUpdate(vm.projectId,
                {
                    'playerInstruction':'PAUSE'
                });
        }
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
