(function () {
    'use strict';

    var application = angular.module('application');

    application.controller('NavigationController',

        function ($rootScope, $http, $location, $timeout) {

            var self = this;
            self.username = 'user';
            self.password = 'pw';
            // $timeout(function
            // (){angular.element('[ng-model="self.username"]').focus();});

// self.tab = function(route) {
// return $route.current && route === $route.current.controller;
// };

            var authenticate = function (callback) {

                $http.get('user').then(function (response) {
                    if (response.data.name) {
                        $rootScope.authenticated = true;
                    } else {
                        $rootScope.authenticated = false;
                    }
                    callback && callback();
                }, function () {
                    $rootScope.authenticated = false;
                    callback && callback();
                });

            }

            authenticate();

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

        });
})();
