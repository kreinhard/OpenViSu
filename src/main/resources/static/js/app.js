var openVisuApp = angular.module('OpenVisu', [ 'ngRoute',
                                   			"ngSanitize",
                                   			"com.2fdevs.videogular"
                                   		]).config(function($routeProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home',
		controllerAs: 'controller'
	}).when('/playEvent', {
		templateUrl : 'playEvent.html',
		controller : 'playEvent',
		controllerAs: 'controller'
	}).when('/login', {
		templateUrl : 'login.html',
		controller : 'navigation',
		controllerAs: 'controller'
	}).otherwise('/');

}).controller('navigation',

function($rootScope, $http, $location, $route) {
	
	var self = this;

	self.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};

	var authenticate = function(callback) {

		$http.get('user').then(function(response) {
			if (response.data.name) {
				$rootScope.authenticated = true;
			} else {
				$rootScope.authenticated = false;
			}
			callback && callback();
		}, function() {
			$rootScope.authenticated = false;
			callback && callback();
		});

	}

	authenticate();

	self.credentials = {};
	self.login = function() {
		$http.post('login', $.param(self.credentials), {
			headers : {
				"content-type" : "application/x-www-form-urlencoded"
			}
		}).then(function() {
			authenticate(function() {
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
		}, function() {
			console.log("Login failed")
			$location.path("/login");
			self.error = true;
			$rootScope.authenticated = false;
		})
	};

	self.logout = function() {
		$http.post('logout', {}).finally(function() {
			$rootScope.authenticated = false;
			$location.path("/");
		});
	}

}).controller('home', function($http) {
    var self = this;
    $http.get('/resource/').then(function(response) {
            self.greeting = response.data;
    })
}).controller('playEvent', [ '$sce', function($sce) {
	/*
	 * $scope.master = {};
	 * 
	 * $scope.update = function(user) { $scope.master = angular.copy(user); };
	 * 
	 * $scope.reset = function() { $scope.user = angular.copy($scope.master); };
	 * 
	 * $scope.reset();
	 */
	this.config = {
			sources: [
				{src: $sce.trustAsResourceUrl("http://static.videogular.com/assets/videos/videogular.mp4"), type: "video/mp4"},
				{src: $sce.trustAsResourceUrl("http://static.videogular.com/assets/videos/videogular.webm"), type: "video/webm"},
				{src: $sce.trustAsResourceUrl("http://static.videogular.com/assets/videos/videogular.ogg"), type: "video/ogg"}
			],
			tracks: [
				{
					src: "http://www.videogular.com/assets/subs/pale-blue-dot.vtt",
					kind: "subtitles",
					srclang: "en",
					label: "English",
					default: ""
				}
			],
			theme: "bower_components/videogular-themes-default/videogular.css",
			plugins: {
				poster: "http://www.videogular.com/assets/images/videogular.png"
			}
		};
	}]);
