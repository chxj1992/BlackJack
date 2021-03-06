define(['model','jquery','backbone'],function(PokerModel){

    var UserView = Backbone.View.extend({
        el: $('body'),
        events: {
            'click #start-game' : 'setUserInfo'
        },

        userInfo : function(){
            $("#start-game").removeAttr("disabled");
            $('#content').hide();
            $('#user-info').show();
            $('.quit-btn').hide();
        },

        setUserInfo : function(){
            $("#start-game").attr("disabled", "disabled");
            var userInfo = {
                'player' : $("#player-name").val() != "" ? $("#player-name").val() : $("#player-name").attr("placeholder"),
                'level' : $(".level:checked").val(),
                'gender' : $(".gender:checked").val()
            };
            localStorage.setItem('userInfo', JSON.stringify(userInfo) );
            localStorage.setItem('balance', 1000);

            this.setLevel(userInfo.level);
        },

    /*********************************************
     * Private Functions
     *********************************************/
        setLevel : function(level) {
            $.ajax({
                url : "/setLevel",
                type : "POST",
                data : {
                   'level' : level
                },
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                success : function(){
                    window.location.hash = '';
                }
            });
        }

    });


    var AppView = Backbone.View.extend({

        el:$('body'),
        model : new PokerModel,
        events: {
            'click #shuffle' : 'shuffle',
            'click .quit-btn' : 'quit',
            'click #adviser' : 'adviser',
            'click #surrender' : 'surrender',
            'click #open-cards' : 'openCards',
            'click #stand' : 'stand',
            'click #double' : 'double',
            'click #hit' : 'hit',
            'click .bet' : 'setBet',
            'click .retry-btn' : 'retry',
            'click #black-jack' : 'blackJack',
            'click #insurance' : 'insurance',
            'click .close-alert' : 'closeAlert'
        },

        initialize : function() {
            this.model.bind('change' , this.render, this);
        },

        render : function() {
            if ( this.model.get('role') == 'player' ){
                $("#player-card").append('<img src="/img/poker/'+ this.model.get('fileName')+'" value="'+
                    this.model.get('value')+'" class="img-thumbnail" width="75px" />' );
            } else {
                $("#dealer-card").append('<img src="/img/poker/'+ this.model.get('fileName')+'" value="'+
                    this.model.get('value')+'" class="img-thumbnail" width="75px" />' );
            }
        },

        homePage : function(){
            $('#user-info').hide();
            $('#content').show();
            $('.quit-btn').show();
            this.initUserInfo();
            this.toBeforeStart();
        },

        openCards : function(){
            $("#open-cards").attr("disabled", "disabled");
            $("#balance-show").text(localStorage.getItem('balance'));
            var view = this;
            setTimeout(function(){
                view.toFirstRound();
                view.model.openCards();
            }, 500);
        },

        shuffle : function() {
            $("#shuffle").attr("disabled", "disabled");
            this.model.shuffle();
        },

        retry : function(){
            this.toBeforeStart();
        },

        quit : function(){
            localStorage.removeItem('userInfo');
            window.location.reload();
        },

        setBet : function(e){
            var bet = $(e.currentTarget.children[0]).text();
            $("#bet-value").text(bet);
        },

        adviser : function(){
            this.model.adviser();
            setTimeout(function(){
                $("#adviser").popover('hide');
            }, 3000);
        },

        surrender : function(){
            $("#surrender").attr("disabled", "disabled");
            this.model.surrender();
        },

        stand : function(){
            $("#board-playing > button").attr("disabled","disabled");
            this.model.stand();
        },

        hit : function() {
            this.model.hit();
            this.toNonFirstRound();
        },

        double : function() {
            $("#double").attr("disabled", "disabled");
            var view = this;
            setTimeout(function(){
                    view.toNonFirstRound();
            }, 500);
            this.model.doubleCard();
        },

        blackJack : function() {
            $("#black-jack").attr("disabled", "disabled");
            this.model.blackJack();
        },

        insurance: function() {
            $("#insurance").attr("disabled", "disabled");
            this.model.insurance();
        },

        closeAlert : function() {
            $(".alert").fadeOut();
            $("#mask").hide();
        },

    /*********************************************
     * Private Functions
     *********************************************/
        toBeforeStart : function(){
            var userInfo = JSON.parse(localStorage.getItem("userInfo"));
            localStorage.setItem("status", "before");
            $("#board-playing > button").removeAttr("disabled");
            $("#open-cards").removeAttr("disabled");
            $("#shuffle").removeAttr("disabled");
            if( userInfo.level == "expert" )
                $("#shuffle").hide();
            $(".tag").text("");
            $(".alert").fadeOut();
            $("#mask").hide();
            $("#player-card").html('');
            $("#dealer-card").html('');
            $("#player-total-score").text(0);
            $("#board-playing > button").hide();
            $("#board-before-start").show();
        },

        toFirstRound : function(){
            $("#board-before-start").hide();
            $("#board-playing > button").show();
            $("#board-playing > .special").hide();
        },

        toNonFirstRound : function() {
            $("#board-playing > button").hide();
            $("#board-before-start").hide();
            $(".non-first-round").show();
        },

        initUserInfo : function() {
            var userInfo = JSON.parse(localStorage.getItem("userInfo"));

            $("#player-avatar-show").attr("src","/img/"+userInfo.gender+".jpeg");
            $("#player-name-show").text(userInfo.player);
            $("#level-show").text(userInfo.level);
            if (userInfo.level != "beginner" )
                $("#level-show").attr("class","label label-danger");
            else
                $("#level-show").attr("class","label label-success");
            $('#balance-show').text(localStorage.getItem("balance"));

        }

    });


    return {'user': UserView , 'app' : AppView};

});
