'use strict';

describe('Controller Tests', function() {

    describe('Comment Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockComment, MockProduct, MockUser, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockComment = jasmine.createSpy('MockComment');
            MockProduct = jasmine.createSpy('MockProduct');
            MockUser = jasmine.createSpy('MockUser');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Comment': MockComment,
                'Product': MockProduct,
                'User': MockUser,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("CommentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:commentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
