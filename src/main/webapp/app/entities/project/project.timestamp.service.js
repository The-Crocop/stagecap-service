(function() {
    'use strict';
    /* globals SockJS, Stomp */

    angular
        .module('stageCapServiceApp')
        .factory('ProjectTimestampService', ProjectTimestampService);

    ProjectTimestampService.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q', '$localStorage'];

    function ProjectTimestampService ($rootScope, $window, $cookies, $http, $q, $localStorage) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            sendUpdate: sendUpdate,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        function connect () {
            //building absolute path so that websocket doesnt fail when deploying with a context path
            var loc = $window.location;
            var url = '//' + loc.host + loc.pathname + 'websocket/project';
            /*jshint camelcase: false */
            var authToken = angular.fromJson($localStorage.authenticationToken).access_token;
            url += '?access_token=' + authToken;
            var socket = new SockJS(url);
            stompClient = Stomp.over(socket);
            var stateChangeStart;
            var headers = {};
            stompClient.connect(headers, function() {
                connected.resolve('success');
                //sendActivity();
                if (!alreadyConnectedOnce) {
                    stateChangeStart = $rootScope.$on('$stateChangeStart', function () {
                        //sendActivity();
                    });
                    alreadyConnectedOnce = true;
                }
            });
        }

        function disconnect () {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }

        function receive () {
            return listener.promise;
        }

        function sendUpdate(projectId,dto) {
            if (stompClient !== null && stompClient.connected) {
                stompClient
                    .send('/topic/update/'+projectId,
                        {},
                        angular.toJson(dto));
            }
        }

        function subscribe (projectId) {

            connected.promise.then(function() {
                subscriber = stompClient.subscribe('/topic/project/'+projectId, function(data) {
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe () {
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }
    }
})();
