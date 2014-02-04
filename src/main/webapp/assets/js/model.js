define(['jquery','backbone'],function(){

    return Backbone.Model.extend({

        defaults : {
            'pokerId' : 0,
            'value' : 0,
            'fileName' : '0.jpg'
        },

        advisor : function() {
            var url;
            if ( localStorage.getItem("status") == "before" )
                url = "/advisor/bet";
            else if( localStorage.getItem("status") == "play" )
                url = "/advisor/action";
            else
                return;

            $.ajax({
                url : url,
                type : "POST",
                dataType : 'Json',
                async : false,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    $("#advisor").attr("data-content", data.data + ", Sir.");
                    $("#advisor").popover('show');
                }
            });
        },

        hit : function() {
            localStorage.setItem("status", "play");
            $("#player-status-tag").text("Judging...");
            $("#player-status-tag").text("Hitting...");
            $(".special").hide();
            var model = this;
            $.ajax({
                url : "/hit",
                type : "POST",
                dataType : 'Json',
                async : false,
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
                        localStorage.setItem("status", "bust");
                    }
                    model.updatePlayerCard(model, data.data);
                    model.checkRoutine(model, data.data);
                }
            });

            return localStorage.getItem("status");
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
            $("#player-total-score").text(model.getScore("player"));
        },

        checkRoutine : function(model, data) {
            var routine = data.routine;
            if( routine.name != "Normal" ) {
                $("#player-card-tag").text(routine.name);
            }
            if(routine.name == "Black Jack") {
                $("#black-jack").text(routine.name);
                $("#black-jack").val("blackJack");
                $("#black-jack").show();
            }

        },

        stand : function() {
            localStorage.setItem("status", "stand");
            $("#player-status-tag").text("Stand");
            var model = this;
            $.ajax({
                url : "/stand",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    setTimeout(function(){
                        if(data.data.routine.name != "Normal")
                            model.judge();
                        else
                            model.dealerHit();
                    }, 1000);
                }
            });

        },

        doubleCard : function() {
            localStorage.setItem("balance", localStorage.getItem("balance")-localStorage.getItem("bet"));
            localStorage.setItem("bet", parseInt(localStorage.getItem("bet"))*2 );
            var model = this;
            $.ajax({
                url : "/double",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(){
                    var res = model.hit();
                    if ( res != "bust" )
                        model.stand();
                }
            });

        },

        openCards : function() {
            localStorage.setItem("status", "play");
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
                    var routine = data.data.player.routine;
                    localPlayer.routine = routine;
                    localStorage.setItem("player", JSON.stringify(localPlayer));
                    $("#player-total-score").text(model.getScore("player"));
                    if( routine.name != "Normal" )
                        $("#player-card-tag").text(routine.name);

                    var localDealer = JSON.parse(localStorage.getItem("dealer"));
                    var dealer = data.data.dealer.cards;
                    for (var i=0; i<dealer.length; i++) {
                        localDealer.cards[i] = dealer[i];
                        model.set({'pokerId': dealer[i].pokerId,
                            'value' : dealer[i].value,
                            'fileName' : dealer[i].fileName,
                            'role' : 'dealer'});
                    }
                    model.checkRoutine(model, data.data.player);
                    localStorage.setItem("dealer", JSON.stringify(localDealer));
                    if( dealer[1].value == 11 ) {
                        $("#surrender").hide();
                        $("#insurance").show();
                    }
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

                    model.alertJudge(data.data);
                }
            });
        },

        alertJudge : function (data) {
            $("#win-info").hide();
            if(data.name != "Normal"){
                $("#win-info>strong").text(data.name);
                $("#win-info").show();
            }
            var money = parseInt(data.money);
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
        },

        blackJack : function() {
            var model = this;
             $.ajax({
                url : "/blackJack",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                data : {
                    'role' : "player"
                },
                success : function(data){
                    model.alertJudge(data.data);
                }
             });
        },

        surrender: function() {
             $.ajax({
                url : "/surrender",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    if(data.status == 1){
                        $("#balance-show").text(data.data);
                        $("#mask").show();
                        var balance = parseInt(localStorage.getItem("balance"));
                        var bet = parseInt(localStorage.getItem("bet"));
                        $(".bet-lose").text(balance+bet-data.data);
                        $("#alert-surrender").fadeIn();
                        localStorage.setItem("balance", data.data);
                    }
                }
             });
        },


        insurance: function() {
             $.ajax({
                url : "/insurance",
                type : "POST",
                dataType : 'Json',
                headers : {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    if(data.status == 1){
                        $("#balance-show").text(data.data);
                        $("#mask").show();
                        if( data.data > parseInt(localStorage.getItem("balance")) ) {
                            $(".insurance-money").text(data.data-localStorage.getItem("balance"));
                            $("#alert-insurance-win").fadeIn();
                        } else {
                            $(".insurance-money").text(localStorage.getItem("balance")-data.data);
                            $("#alert-insurance-lose").fadeIn();
                        }
                        localStorage.setItem("balance", data.data);
                    }
                }
             });
        }



    });

});
