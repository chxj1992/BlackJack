define(['jquery','backbone'],function(){

    var view = Backbone.View.extend({
            el: $('body'),
            events: {
                'click #start-game' : 'setUserInfo',
                'click #reset' : 'reset',
                'click #advisor' : 'advisor',
                'click #surrender' : 'surrender',
                'click #open-cards' : 'openCards',
                'click #stand' : 'stand',
                'click #double' : 'double',
                'click #hit' : 'hit'
            },

            userInfo : function(){
                $('#content').hide();
                $('#user-info').show();
                $('#reset').hide();
            },

            homePage : function(){
                $('#user-info').hide();
                $('#content').show();
                $('#reset').show();
                this.initUserInfo();
                this.toBeforeStart();
            },

            openCards : function(){
                this.toFirstRound();
            },

            setUserInfo : function(){
                var userInfo = {
                    'player' : $("#player-name").val() != "" ? $("#player-name").val() : $("#player-name").attr("placeholder"),
                    'level' : $(".level:checked").val(),
                    'gender' : $(".gender:checked").val()
                };
                localStorage.setItem('userInfo', JSON.stringify(userInfo) );
                localStorage.setItem('balance', 1000);
                window.location.hash = '';
            },

            reset : function(){
                localStorage.removeItem('userInfo');
                window.location.hash = '#user-info';
            },

            advisor : function(){
                $("#advisor").popover('show');
                setTimeout(function(){
                    $("#advisor").popover('hide');
                },3000);
            },

            surrender : function(){
                this.toBeforeStart();
            },

            hit : function() {
                this.toNonFirstRound();
            },

            double : function() {
                this.toNonFirstRound();
            },

        /*********************************************
         * Private Functions
         *********************************************/
            toBeforeStart : function(){
                $("#board-playing > button").hide();
                $("#board-before-start").show();
            },

            toFirstRound : function(){
                $("#board-before-start").hide();
                $("#board-playing > button").show();
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

    return new view();

});
