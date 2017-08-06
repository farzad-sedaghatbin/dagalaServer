'use strict';

describe('Controller Tests', function() {

    describe('BaseParameter Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockBaseParameter;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockBaseParameter = jasmine.createSpy('MockBaseParameter');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'BaseParameter': MockBaseParameter
            };
            createController = function() {
                $injector.get('$controller')("BaseParameterDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:baseParameterUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
