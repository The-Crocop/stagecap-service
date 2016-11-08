'use strict';

describe('Controller Tests', function() {

    describe('AudioDescription Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAudioDescription, MockProject;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAudioDescription = jasmine.createSpy('MockAudioDescription');
            MockProject = jasmine.createSpy('MockProject');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'AudioDescription': MockAudioDescription,
                'Project': MockProject
            };
            createController = function() {
                $injector.get('$controller')("AudioDescriptionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stageCapServiceApp:audioDescriptionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
