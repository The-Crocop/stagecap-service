'use strict';

describe('Controller Tests', function() {

    describe('Project Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProject, MockSubtitle, MockAudioDescription;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProject = jasmine.createSpy('MockProject');
            MockSubtitle = jasmine.createSpy('MockSubtitle');
            MockAudioDescription = jasmine.createSpy('MockAudioDescription');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Project': MockProject,
                'Subtitle': MockSubtitle,
                'AudioDescription': MockAudioDescription
            };
            createController = function() {
                $injector.get('$controller')("ProjectDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'stageCapServiceApp:projectUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
