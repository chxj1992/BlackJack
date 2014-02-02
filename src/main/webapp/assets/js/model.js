define(['jquery','backbone'],function(){

    return Backbone.Model.extend({

        defaults : {
            'pokerId' : 0,
            'value' : 0,
            'fileName' : '0.jpg'
        },

        hit : function() {
            $("#player-status-tag").text("Hitting...");
            $(".special").hide();
            var model = this;
            $.ajax({
                url : "/hit",
                type : "POST",
                dataType : 'Json',
                data : {
                   'role' : 'player'
                },
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    if ( data.status == 0 && data.data.info == "timeout" ) {
                        $("#mask").show();
                        $("#alert-timeout").fadeIn();
                        return;
                    }

                    if ( data.status == 0 && data.data.info == "Bust" ) {
                        $("#player-status-tag").text("Bust");
                        $("#mask").show();
                        $(".bet-lose").text(localStorage.getItem("bet"));
                        $("#alert-bust").fadeIn();
                    }

                    model.updatePlayerCard(model, data.data);
                    model.checkRoutine(model, data.data);
                }
            });

        },

        updatePlayerCard : function(model, data) {
            var localPlayer = JSON.parse(localStorage.getItem("player"));
            localPlayer.cards[Object.keys(localPlayer.cards).length] = data.poker;
            localPlayer.routine = data.routine;
            var poker = data.poker;
            model.set({'pokerId': poker.pokerId,
                'value' : poker.value,
                'fileName' : poker.fileName,
                'role' : 'player'});
            localStorage.setItem("player", JSON.stringify(localPlayer));
            $("#total-score").text(model.getScore("player"));
            var routine =data.routine;
            if( routine.name != "Normal" ) {
                $("#player-card-tag").text(routine.name);
                $("#routine").text(routine.name);
                $("#routine").show();
            }
        },

        checkRoutine : function(model, data) {
            if(data.routine.name == "Black Jack") {
                $("#black-jack").show();
            }

            if(data.routine.name == "Five Card") {
                model.fiveCard();
            }

            if(data.routine.name == "Special Win") {
                model.specialWin();
            }

        },

        stand : function() {
            $("#player-status-tag").text("Stand");
            var model = this;
            $.ajax({
                url : "/stand",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(){
                    setTimeout(function(){
                        model.dealerHit();
                    }, 1000);
                }
            });

        },

        doubleCard : function() {
            localStorage.setItem("bet", parseInt(localStorage.getItem("bet"))*2 );
            var model = this;
            $.ajax({
                url : "/double",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(){

                }
            });

        },

        openCards : function() {
            $("#player-status-tag").text("Open card");
            var model = this;
            localStorage.setItem('bet', $("#bet-value").text());
            localStorage.setItem('balance', parseInt(localStorage.getItem('balance')) - parseInt($("#bet-value").text()));
            localStorage.setItem("player", JSON.stringify({cards:{}, routine: {}}));
            localStorage.setItem("dealer", JSON.stringify({cards:{}, routine: {}}));
            $("#balance-show").text(localStorage.getItem('balance'));
            $.ajax({
                url : "/openCards",
                type : "POST",
                dataType : 'Json',
                data : {
                    "bet" : parseInt(localStorage.getItem("bet"))
                },
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    if ( data.status == 0 )  {
                        $("#mask").show();
                        $("#alert-timeout").fadeIn();
                        return;
                    }
                    var localPlayer = JSON.parse(localStorage.getItem("player"));
                    var player = data.data.player.cards;
                    for (var i=0; i<player.length; i++) {
                        localPlayer.cards[i] = player[i];
                        model.set({'pokerId': player[i].pokerId,
                            'value' : player[i].value,
                            'fileName' : player[i].fileName,
                            'role' : 'player'});
                    }
                    localStorage.setItem("player", JSON.stringify(localPlayer));
                    $("#total-score").text(model.getScore("player"));
                    var routine = data.data.player.routine;
                    if( routine.name != "Normal" ) {
                        $("#player-card-tag").text(routine.name);
                        $("#routine").text(routine.name);
                        $("#routine").show();
                    }

                    var localDealer = JSON.parse(localStorage.getItem("dealer"));
                    var dealer = data.data.dealer.cards;
                    for (var i=0; i<dealer.length; i++) {
                        localDealer.cards[i] = dealer[i];
                        model.set({'pokerId': dealer[i].pokerId,
                            'value' : dealer[i].value,
                            'fileName' : dealer[i].fileName,
                            'role' : 'dealer'});
                    }
                    localStorage.setItem("dealer", JSON.stringify(localDealer));
                }
            });
        },

        getScore : function(role) {
            var cards = JSON.parse(localStorage.getItem(role)).cards;
            var total = 0;
            var aNum = 0;
            for (var i=0; i<Object.keys(cards).length; i++ ) {
                total += cards[i].value;
                if(cards[i].value == 11)
                    aNum++;
            }

            while(total>21 && aNum>0 ){
                total -= 10;
                aNum--;
            }
            return total;
        },

        dealerHit : function() {
            $("#dealer-status-tag").text("Hitting...");
            var model = this;
            $.ajax({
                url : "/hit",
                    type : "POST",
                    dataType : 'Json',
                    headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                    data : {
                        'role' : 'dealer'
                    },
                    success : function(data){
                        var localDealer = JSON.parse(localStorage.getItem("dealer"));
                        localDealer.cards[Object.keys(localDealer.cards).length] = data.data.poker;
                        localDealer.routine = data.data.routine;
                        var poker = data.data.poker;
                        model.set({'pokerId':poker.pokerId,
                            'value' : poker.value,
                            'fileName' : poker.fileName,
                            'role' : 'dealer'});
                        localStorage.setItem("dealer", JSON.stringify(localDealer));
                        var routine = data.data.routine;

                        if ( data.status == 1 ) {
                            if( routine.name != "Normal" ) {
                                $("#dealer-card-tag").text(routine.name);
                                model.judge();
                            } else {
                                setTimeout(function(){
                                        model.dealerHit();
                                    }, 2000);
                            }
                        } else if ( data.status == 0 ) {
                            setTimeout(function(){
                                $("#dealer-status-tag").text(data.data.info);
                                model.judge();
                            }, 2000 );
                        }
                    }
            });
        },

        judge : function() {
            $("#player-status-tag").text("Judging...");
            var model = this;
            localStorage.setItem("dealer", JSON.stringify({cards:{}, routine:{}}));
            $.ajax({
                url : "/judge",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                data : {
                    'role' : 'dealer'
                },
                success : function(data){
                    $("#dealer-card").html('');
                    var localDealer = JSON.parse(localStorage.getItem("dealer"));
                    localDealer.routine = data.data.routine;
                    var dealer = data.data.dealer;
                    for (var i=0; i<dealer.length; i++) {
                        localDealer.cards[i] = dealer[i];
                        model.set({'pokerId': dealer[i].pokerId,
                            'value' : dealer[i].value,
                            'fileName' : dealer[i].fileName,
                            'role' : 'dealer'});
                    }
                    localStorage.setItem("dealer", JSON.stringify(localDealer));

                    if(data.data.name != "Normal"){
                        $("#win-info>strong").text(data.data.name);
                        $("#win-info").show();
                    }

                    var money = parseInt(data.data.money);
                    var balance = parseInt(localStorage.getItem("balance"));
                    var bet = parseInt(localStorage.getItem("bet"));
                    $("#mask").show();
                    if( money > (balance+bet) ) {
                        $(".bet-win").text(money-balance-bet);
                        localStorage.setItem("balance", money);
                        $("#alert-win").fadeIn();
                    } else if ( money < (balance+bet) ) {
                        $(".bet-lose").text(balance+bet-money);
                        localStorage.setItem("balance", money);
                        $("#alert-lose").fadeIn();
                    } else {
                       $("#alert-draw").fadeIn();
                    }
                    localStorage.setItem("balance", money);
                    $("#balance-show").text(money);
                }
            });
        },

        blackJack : function(role) {
             $.ajax({
                url : "/blackJack",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                data : {
                    'role' : role
                },
                success : function(data){
                }
             });

        },

        fiveCard : function(role) {
             $.ajax({
                url : "/fiveCard",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                data : {
                    'role' : role
                },
                success : function(data){
                }
             });

        },

        specialWin : function(role) {
            $.ajax({
                url : "/special",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                data : {
                    'role' : role
                },
                success : function(data){
                }
            });
        }


    });

});
