'use strict';

describe('Controller Tests', function() {

    describe('Merchant Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockMerchant, MockUserContact;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockMerchant = jasmine.createSpy('MockMerchant');
            MockUserContact = jasmine.createSpy('MockUserContact');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Merchant': MockMerchant,
                'UserContact': MockUserContact
            };
            createController = function() {
                $injector.get('$controller')("MerchantDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:merchantUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
