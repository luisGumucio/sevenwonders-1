'use strict';

angular.
    module('sevenWonders.core.game').
    factory('Game', ['$cookies', 'Restangular', 'Auth', '$q', '$websocket',
        function ($cookies, Restangular, Auth, $q, $websocket) {
            var Game = Restangular.service('games');
            var dataStream = $websocket('ws://localhost:9294/lobbyChannel');
            dataStream.onOpen(function () {
                console.log('connected');
            })
            dataStream.onMessage(function (message) {
                 Game.getList();
            });
            var storeGame = function (data) {
                var gameModel = {
                    name: data.name,
                    maxPlayers: data.maxPlayers,
                    owner: {
                        userName: data.userName,
                        roomId: data.roomId
                    },
                    players: [
                        {
                            userName: data.userName,
                            roomId: data.roomId
                        }
                    ]
                };
                $cookies.putObject('game', gameModel);
            };

            return {
                getAvailableGames: function () {
                    return Game.getList();
                },
                create: function (gameSetting) {
                    return $q(function (resolve, reject) {
                        Restangular.all('games/create').post(
                            {
                                "maxPlayers": gameSetting.players,
                                "name": gameSetting.name,
                                "owner": {
                                    "userName": $cookies.getObject('user').userName,
                                    "roomId": 1
                                },
                                "players": [
                                    {
                                        "userName": $cookies.getObject('user').userName,
                                        "roomId": 1
                                    }
                                ]
                            }
                        )
                            .then(function (data) {
                                resolve(data);
                            })
                            .catch(function (data) {
                                reject(data);
                            });
                    });
                },
                send: function () {
                    dataStream.send("create new game");
                },
                join: function (game) {
                    var user = Auth.getLoggedUser();
                    var defer = $q.defer();

                    var gameRest = Game.one(game.id);
                    gameRest.user = user.id;
                    gameRest.put()
                        .then(function (data) {
                            storeGame(data);
                            defer.resolve();
                        }).catch(function () {
                            defer.reject();
                        });
                    return defer.promise;

                },
                getCurrentGame: function () {
                    return $cookies.getObject('game');
                }
            };
        }
    ]);