(function () {
    'use strict';

    var openVisuApp = angular.module('open.visu', ['ui.router',
        'ngSanitize',
        "com.2fdevs.videogular",
        "com.2fdevs.videogular.plugins.controls",
        "com.2fdevs.videogular.plugins.overlayplay",
        "com.2fdevs.videogular.plugins.poster"
    ]);

// .run(run);
//
// run.$inject = ['stateHandler'];
//
// function run(stateHandler) {
// stateHandler.initialize();
// }


    openVisuApp.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('home', {
                url: '/home',
                templateUrl: 'home.html',
                controller: 'HomeController',
                data: {
                    requireLogin: false
                }
            })
            .state('login', {
                url: '/login',
                templateUrl: 'login.html',
                controller: 'NavigationController'
                data: {
                    requireLogin: false
                }
            })
            .state('app', {
                abstract: true,
                data: {
                    requireLogin: true // this property will apply to all children of 'app'
                }
            })
        // .state('app.playEvent', {
        // child state of `app`
        // requireLogin === true
        //});
    });

    openVisuApp.config(['$provide', '$stateProvider', '$urlRouterProvider', '$httpProvider',
        function ($provide, $stateProvider, $urlRouterProvider, $httpProvider) {
            /* Put this routeprovider for when'/' then go to dashboard page. */
            $urlRouterProvider
                .when('/', function ($state) {
                    $state.go('home');
                }).otherwise('/login');
        }
    ]);

    openVisuApp.run(function ($rootScope, $state, loginModal) {


        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            var requireLogin = toState.data.requireLogin;

            if (requireLogin && typeof $rootScope.currentUser === 'undefined') {
                event.preventDefault();

                loginModal()
                    .then(function () {
                        return $state.go(toState.name, toParams);
                    })
                    .catch(function () {
                        return $state.go('home');
                    });
            }
        });

    });

    openVisuApp.controller('NavigationController',

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

    openVisuApp.controller('HomeController', function ($http) {
        var self = this;
        $http.get('/resource/').then(function (response) {
            self.greeting = response.data;
        })
    });
})();