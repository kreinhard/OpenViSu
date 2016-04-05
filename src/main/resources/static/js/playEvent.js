(function() {
    'use strict';

    angular
        .module('OpenVisu')
        .controller('PlayEventController', PlayEventController);

    PlayEventController.$inject = ['$scope', '$sce'];

   // MonitorDialogController.$inject = ['$sce', '$stateParams',
	// '$uibModalInstance', 'entity', 'Monitor'];

    function PlayEventController ($scope, $sce) {
    	
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
    			theme: "css/videogular.css",
    			plugins: {
    				poster: "http://www.videogular.com/assets/images/videogular.png"
    			}
    		};

    	function playEvent () {
    		console.log('playEvent()!');
        }
       /*
		 * var vm = this; vm.monitor = entity; vm.load = function(id) {
		 * Monitor.get({id : id}, function(result) { vm.monitor = result; }); };
		 * 
		 * var onSaveSuccess = function (result) {
		 * $sce.$emit('jhipsterApp:monitorUpdate', result);
		 * $uibModalInstance.close(result); vm.isSaving = false; };
		 * 
		 * var onSaveError = function () { vm.isSaving = false; };
		 * 
		 * vm.save = function () { vm.isSaving = true; if (vm.monitor.id !==
		 * null) { Monitor.update(vm.monitor, onSaveSuccess, onSaveError); }
		 * else { Monitor.save(vm.monitor, onSaveSuccess, onSaveError); } };
		 * 
		 * vm.clear = function() { $uibModalInstance.dismiss('cancel'); };
		 */
    }
})();
