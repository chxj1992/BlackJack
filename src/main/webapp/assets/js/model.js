define(['jquery','backbone'],function(){

    return Backbone.Model.extend({

        defaults : {
            'pokerId' : 0,
            'value' : 0,
            'fileName' : '0.jpg'
        },

        hit : function() {
            $("#player-status-tag").text("Hitting...");
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
                    if ( data.status == 0 && data.info == "timeout" ) {
                        $("#mask").show();
                        $("#alert-timeout").fadeIn();
                        return;
                    }
                    var localPlayer = JSON.parse(localStorage.getItem("player"));
                    localPlayer[Object.keys(localPlayer).length] = data.data;
                    model.set({'pokerId':data.data.pokerId,
                        'value' : data.data.value,
                        'fileName' : data.data.fileName,
                        'role' : 'player'});
                    localStorage.setItem("player", JSON.stringify(localPlayer));
                    $("#total-score").text(model.getScore("player"));

                    if ( data.status == 0 && data.info == "bust" ) {
                        $("#player-status-tag").text("Bust");
                        $("#mask").show();
                        $(".bet-lose").text(localStorage.getItem("bet"));
                        $("#alert-bust").fadeIn();
                    }
                }
            });

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

        openCards : function() {
            $("#player-status-tag").text("Open Card");
            var model = this;
            localStorage.setItem('bet', $("#bet-value").text());
            localStorage.setItem('balance', localStorage.getItem('balance') - $("#bet-value").text());
            localStorage.setItem("player", JSON.stringify({}));
            localStorage.setItem("dealer", JSON.stringify({}));
            $.ajax({
                url : "/openCards",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    var localPlayer = JSON.parse(localStorage.getItem("player"));
                    var player = data.data.playerCards;
                    for (var i=0; i<player.length; i++) {
                        localPlayer[i] = player[i];
                        model.set({'pokerId': player[i].pokerId,
                            'value' : player[i].value,
                            'fileName' : player[i].fileName,
                            'role' : 'player'});
                    }
                    localStorage.setItem("player", JSON.stringify(localPlayer));
                    $("#total-score").text(model.getScore("player"));

                    var localDealer = JSON.parse(localStorage.getItem("dealer"));
                    var dealer = data.data.dealerCards;
                    for (var i=0; i<dealer.length; i++) {
                        localDealer[i] = dealer[i];
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
            var cards = JSON.parse(localStorage.getItem(role));
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
                        localDealer[Object.keys(localDealer).length] = data.data;
                        model.set({'pokerId':data.data.pokerId,
                            'value' : data.data.value,
                            'fileName' : data.data.fileName,
                            'role' : 'dealer'});
                        localStorage.setItem("dealer", JSON.stringify(localDealer));
                        if ( data.status == 1 ) {
                            setTimeout(function(){
                                    model.dealerHit();
                                } , 2000);
                        } else if ( data.status == 0 ) {
                            setTimeout(function(){
                                $("#dealer-status-tag").text("Stand");
                                model.judge();
                            }, 2000 );
                        }
                    }
            });
        },

        judge : function() {
            $("#player-status-tag").text("Judging...");
            $("#dealer-status-tag").text("Judging...");
            var model = this;
            localStorage.setItem("dealer", JSON.stringify({}));
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
                    var dealer = data.data.dealer;
                    for (var i=0; i<dealer.length; i++) {
                        localDealer[i] = dealer[i];
                        model.set({'pokerId': dealer[i].pokerId,
                            'value' : dealer[i].value,
                            'fileName' : dealer[i].fileName,
                            'role' : 'dealer'});
                    }
                    localStorage.setItem("dealer", JSON.stringify(localDealer));
                    if ( data.data.winner == "player" ) {
                        if(data.data.win != "normal"){
                            $("#win-info>strong").text(data.data.win);
                            $("#win-info").show();
                        }
                        var winBet = parseInt(parseInt(localStorage.getItem("bet"))*data.data.rate);
                        $(".bet-win").text(winBet);
                        localStorage.setItem("balance",parseInt(localStorage.getItem("balance"))+parseInt(localStorage.getItem("bet"))+winBet);
                        $("#mask").show();
                        $("#alert-win").fadeIn();
                    } else if ( data.winner == "dealer" ) {
                        var winLose = parseInt(parseInt(localStorage.getItem("bet"))*data.data.rate);
                        $(".bet-lose").text(winLose);
                        localStorage.setItem("balance",parseInt(localStorage.getItem("balance"))+parseInt(localStorage.getItem("bet"))-winBet);
                        $("#mask").show();
                        $("#alert-lose").fadeIn();
                    } else {
                        $("#mask").show();
                        $("#alert-draw").fadeIn();
                        localStorage.setItem("balance",parseInt(localStorage.getItem("balance"))+parseInt(localStorage.getItem("bet")));
                    }

                    $("#balance-show").text(localStorage.getItem('balance'));
                }
            });
        }


    });

});
