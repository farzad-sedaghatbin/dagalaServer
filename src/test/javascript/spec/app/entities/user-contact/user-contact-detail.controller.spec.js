'use strict';

describe('Controller Tests', function() {

    describe('UserContact Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUserContact;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUserContact = jasmine.createSpy('MockUserContact');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'UserContact': MockUserContact
            };
            createController = function() {
                $injector.get('$controller')("UserContactDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:userContactUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
