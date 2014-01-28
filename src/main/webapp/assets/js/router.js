require(['jquery','bootstrap','backbone'],function(){

     var AppRouter = Backbone.Router.extend({
        routes: {
            'user-info': 'userInfo',
            '': 'homePage'
        },

        userInfo: function() {
            alert("userInfo");
            // render first view
        },
        homePage: function() {
            alert("homePage");
            // render second view
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


