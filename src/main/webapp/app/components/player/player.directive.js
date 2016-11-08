/**
 * Created by crocop on 27.04.16.
 */
(function() {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .directive('adPlayer', adPlayer);

    // adPlayer.$inject('$sce', 'ProjectTimestampService', 'AudioDescription');

    function adPlayer($sce, ProjectTimestampService, AudioDescription){

        var directive = {
            template: '<audio controls ng-src="{{file}}" currentTime="15"></audio>',
            // require:['project-id','language'],
            restrict: 'E',
            scope:{
              audioId: '=',
              projectId: '=',
              language: '='
            },
            link: linkFunc
        };

        return directive;

        function linkFunc(scope, element, attrs) {
            var vm = scope;
            vm.seeker = 7*60+20;
            vm.seeked = 0;

            element.on('$destroy', function() {
                ProjectTimestampService.unsubscribe(vm.projectId);
            });

            var audioPlayer = element.children()[0];

            audioPlayer.onloadeddata = function(){
                ProjectTimestampService.subscribe(vm.projectId);
            }
            audioPlayer.onseeked = function(){
               // if(audioPlayer.)
               //  ProjectTimestampService.sendUpdate(vm.projectId,
               //      {
               //          'playerInstruction':'JUMP',
               //          'timestamp': audioPlayer.currentTime*1000
               //      });
            };


            ProjectTimestampService.receive().then(null, null, function(update) {
                console.log(update);
                switch (update.playerInstruction){
                    case 'PLAY':
                        audioPlayer.play();
                        break;
                    case 'STOP':
                        audioPlayer.pause();
                        audioPlayer.currentTime = 0;
                        break;
                    case 'PAUSE':
                        audioPlayer.pause();
                        break;
                    case 'JUMP':
                        if(update.timestamp !== null){
                            audioPlayer.currentTime = update.timestamp/1000;
                            //vm.seeker = update.timestamp;
                        }
                        break;
                    case 'INIT':
                        switch(update.state){
                            case 'PLAYING':
                                audioPlayer.currentTime = update.timestamp/1000;
                                audioPlayer.play();
                                break;
                            case 'STOPPED':
                                break;
                            case 'PAUSED':
                                audioPlayer.currentTime = update.timestamp/1000;
                                break;
                            case 'ERROR':
                                break;
                            default:
                                //do nothing
                        }
                    default:
                        //do ntohign
                      // switch (update.state){
                      //     case null:
                      //         break;
                      //     case 'PLAYING':
                      //         audioPlayer.currentTime = update.timestamp/1000;
                      //         audioPlayer.play();
                      //         break;
                      //     case 'STOPPED':
                      //         break;
                      //         //do nothing
                      //     case 'PAUSED':
                      //         audioPlayer.currentTime = update.timestamp/1000;
                      //         break;
                      //     case 'ERROR':
                      //         break;
                      //     default:
                      //         //do nothing
                      // }

                }

            });

            vm.play = function(){
                ProjectTimestampService.sendUpdate(vm.projectId,
                    {
                        'playerInstruction':'PLAY',
                    });
            };
            vm.pause = function(){
                alert("pause");
                ProjectTimestampService.sendUpdate(vm.projectId,
                    {
                        'playerInstruction':'PAUSE'
                    });
            };
            vm.stop = function(){
                ProjectTimestampService.sendUpdate(vm.projectId,
                    {
                        'playerInstruction':'STOP',
                        'timestamp': 0
                    });
            };
               // ProjectTimestampService.subscribe(vm.projectId);
                AudioDescription.query({projectId:vm.projectId,language:vm.language},function(retVal){
                    var ad;
                    if(retVal.length > 0){
                        vm.exists = true;
                        ad = retVal[0];
                        vm.type = ad.fileContentType;
                        vm.file = $sce.trustAsResourceUrl("data:"+vm.type+";base64,"+ad.file);
                    }else vm.exists = false;
                });

        }
    };



})();
