/**
 * Created by crocop on 11.05.16.
 * create module to do the heavy lifting here
 */
(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .controller('StageCapController', StageCapController);

    StageCapController.$inject = ['$scope', '$rootScope','$state', '$stateParams', 'base64', 'DataUtils', 'Project', 'Subtitle', 'AudioDescription', 'ProjectTimestampService', 'SrtParser'];

    function StageCapController($scope, $rootScope, $state, $stateParams, base64, DataUtils, Project, Subtitle, AudioDescription, ProjectTimestampService, SrtParser) {
        var vm = this;
        vm.subtitles;
        vm.listOptions = {};
        //vm.listOptions =
        vm.projectId = $state.params.projectId;

        vm.subtitleEntities;

        var parseTime = function(time){
            var d =  moment(time,"hh:mm:ss,SSS");
            return  d.milliseconds()+d.seconds()*1000+ d.minutes()*60000+ d.hours()*60000*60;
        };

        var parseBase64Subtitles = function(subtitles){
            var decoded = base64.decode(subtitles);
            var srt = SrtParser.fromSrt(decoded);
            return srt;
        };

        var play = function(){
                ProjectTimestampService.sendUpdate(vm.projectId,
                    {
                        'playerInstruction':'PLAY',
                    });
        };

        var pause = function(){
            ProjectTimestampService.sendUpdate(vm.projectId,
                {
                    'playerInstruction':'PAUSE'
                });
        };


        ProjectTimestampService.receive().then(null, null, function(update) {
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

        Subtitle.query({projectId:vm.projectId},function(retVal){
            if(retVal.length > 0){
                vm.subtitleEntities = retVal;
                vm.subtitles = parseBase64Subtitles(retVal[0].file);
                vm.sliderOptions = {
                    floor:0,
                    ceil:parseTime(vm.subtitles[vm.subtitles.length-1].endTime),
                    translate: function(value) {
                        return moment.utc(value).format("HH:mm:ss");
                    },
                    onStart:function(sliderId,modelValue,highValue){
                        pause();
                    },
                    onEnd:function(sliderId,modelValue,highValue){
                      //alert(sliderId+";"+modelValue+";"+highValue);
                        ProjectTimestampService.sendUpdate(vm.projectId,
                            {
                                'playerInstruction':'JUMP',
                                'timestamp': modelValue
                            });
                        play();
                    }
                };
                ProjectTimestampService.subscribe(vm.projectId);
            }
        });

        $scope.$on('$destroy', function(){
            ProjectTimestampService.unsubscribe(vm.projectId);
        });

        vm.switchLanguage = function(index){
                vm.language = vm.subtitleEntities[index].language;
                vm.subtitles =  parseBase64Subtitles(vm.subtitleEntities[index].file);
        };

        vm.itemclick = function(index){
            // console.log(vm.listOptions.listView);
            // vm.listOptions.listView.setViewport(50,2);
            vm.listOptions.listView.updateRowOffsets(50);
            var sub = vm.subtitles[index];
            var t = parseTime(sub.startTime);
            // var z = pattern.exec(sub);
             ProjectTimestampService.sendUpdate(vm.projectId,
                 {
                     'playerInstruction':'JUMP',
                     'timestamp': t
                 });
        };
    }
})();
