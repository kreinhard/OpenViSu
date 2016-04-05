(function() {
    'use strict';

    angular.module('OpenVisu', ['ngSanitize',
                                'ui.bootstrap',
                                'ui.bootstrap.datetimepicker',
                                'ui.router',
                                "com.2fdevs.videogular",
                                "com.2fdevs.videogular.plugins.controls",
                                "com.2fdevs.videogular.plugins.overlayplay",
                                "com.2fdevs.videogular.plugins.poster"
                                ])
                                .run(run);

    run.$inject = ['stateHandler'];

    function run(stateHandler) {
        stateHandler.initialize();
    }
})();
