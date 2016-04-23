(function () {
    'use strict';

    var application = angular.module('application');

    /* http://brewhouse.io/blog/2014/12/09/authentication-made-simple-in-single-page-angularjs-applications.html */

    application.controller('LoginModalController', function ($scope) {

            this.cancel = $scope.$dismiss;

            this.submit = function (username, password) {
                /*
                 UsersApi.login(username, password).then(function (user) {
                 $scope.$close(user);
                 });
                 */


                self.credentials = {};
                self.login = function () {
                    $http.post('login', $.param(self.credentials), {
                        headers: {
                            "content-type": "application/x-www-form-urlencoded"
                        }
                    }).then(function () {
                        authenticate(function () {
                            if ($rootScope.authenticated) {
                                console.log("Login succeeded")
                                $location.path("/");
                                self.error = false;
                                $rootScope.authenticated = true;
                            } else {
                                console.log("Login failed with redirect")
                                $location.path("/login");
                                self.error = true;
                                $rootScope.authenticated = false;
                            }
                        });
                    }, function () {
                        console.log("Login failed")
                        $location.path("/login");
                        self.error = true;
                        $rootScope.authenticated = false;
                    })
                };

                self.logout = function () {
                    $http.post('logout', {}).finally(function () {
                        $rootScope.authenticated = false;
                        $location.path("/");
                    });
                }


            }
        }
        );
})();
