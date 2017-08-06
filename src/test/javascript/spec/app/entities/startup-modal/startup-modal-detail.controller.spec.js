'use strict';

describe('Controller Tests', function() {

    describe('StartupModal Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockStartupModal;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockStartupModal = jasmine.createSpy('MockStartupModal');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'StartupModal': MockStartupModal
            };
            createController = function() {
                $injector.get('$controller')("StartupModalDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:startupModalUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
