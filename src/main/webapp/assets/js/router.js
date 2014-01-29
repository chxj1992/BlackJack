define(['view','jquery','bootstrap','backbone'],function(view){

    var AppRouter = Backbone.Router.extend({

        routes: {
            'user-info': 'userInfo',
            '': 'homePage'
        },

        userInfo: function() {
            if ( localStorage.getItem('userInfo') != undefined ) {
                window.location.hash = '';
            } else {
                view.userInfo();
            }
        },

        homePage: function() {
            if ( localStorage.getItem('userInfo') == undefined ) {
                window.location.hash = 'user-info';
            } else {
                view.homePage();
            }
        }
    });

    new AppRouter;
    Backbone.history.start();

    /*
    $(document).ready(function(){
        $('#advisor').click(function(){
            $(this).popover();
            setTimeout(function(){
                $("#advisor").popover('hide');
            },5000);
        });
    });
    */


});


