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
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    model.set({'pokerId':data.data.pokerId,
                        'value' : data.data.value,
                        'fileName' : data.data.fileName,
                        'role' : 'player'});
                }
            });

        },

        openCards : function() {
            var model = this;
            $.ajax({
                url : "/openCards",
                type : "POST",
                dataType : 'Json',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(data){
                    var player = data.data.playerCards;
                    for (var i=0; i<player.length; i++) {
                        model.set({'pokerId': player[i].pokerId,
                            'value' : player[i].value,
                            'fileName' : player[i].fileName,
                            'role' : 'player'});
                    }
                    var dealer = data.data.dealerCards;
                    for (var i=0; i<dealer.length; i++) {
                        model.set({'pokerId': dealer[i].pokerId,
                            'value' : dealer[i].value,
                            'fileName' : dealer[i].fileName,
                            'role' : 'dealer'});
                    }
                }
            });
        }



    });

});
