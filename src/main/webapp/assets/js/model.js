define(['jquery','backbone'],function(){

    return PokerModel = Backbone.Model.extend({

        defaults : {
            'pokerId' : 0,
            'value' : 0,
            'fileName' : '0.jpg'
        },

        hit : function() {
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
                    var localPlayer = JSON.parse(localStorage.getItem("player"));
                    console.log(localPlayer);
                    localPlayer[Object.keys(localPlayer).length] = data.data;
                    model.set({'pokerId':data.data.pokerId,
                        'value' : data.data.value,
                        'fileName' : data.data.fileName,
                        'role' : 'player'});
                    localStorage.setItem("player", JSON.stringify(localPlayer));
                    $("#total-score").text(model.getScore("player"));

                    if ( data.status == 0 ) {
                        $("#bet-lose").text(localStorage.getItem("bet"));
                        $("#alert-bust").fadeIn();
                        $("#mask").show();
                    }
                }
            });

        },

        stand : function() {

        },

        openCards : function() {
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
        }



    });

});
