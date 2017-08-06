'use strict';

describe('Controller Tests', function() {

    describe('CommentLike Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCommentLike, MockUser, MockComment;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCommentLike = jasmine.createSpy('MockCommentLike');
            MockUser = jasmine.createSpy('MockUser');
            MockComment = jasmine.createSpy('MockComment');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CommentLike': MockCommentLike,
                'User': MockUser,
                'Comment': MockComment
            };
            createController = function() {
                $injector.get('$controller')("CommentLikeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:commentLikeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
