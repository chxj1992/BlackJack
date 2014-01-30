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
                var user = new view.user;
                user.userInfo();
            }
        },

        homePage: function() {
            if ( localStorage.getItem('userInfo') == undefined ) {
                window.location.hash = 'user-info';
            } else {
                var app = new view.app;
                app.homePage();
            }
        }
    });

    new AppRouter;
    Backbone.history.start();


});


