(function () {
    'use strict';

    angular
        .module('stageCapServiceApp')
        .factory('Register', Register);

    Register.$inject = ['$resource'];

    function Register ($resource) {
        return $resource('api/register', {}, {});
    }
})();
