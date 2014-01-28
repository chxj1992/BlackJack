require.config({
    baseUrl:'/bower',
    paths : {
        'jquery' : 'jquery/jquery.min',
        'bootstrap' : 'bootstrap/dist/js/bootstrap.min',
        'underscore' : 'underscore/underscore-min',
        'backbone' : 'backbone/backbone-min'
    },
    shim : {
        'bootstrap' : {
            deps : ['jquery']
        },
        'backbone' : {
           deps : ['underscore']
        }
    }
});


require(['jquery','bootstrap','backbone'],function(){

    var MyRouter = Backbone.Router.extend({
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
        // ...
    });

    new MyRouter;
    Backbone.history.start();

    $(document).ready(function(){
        $('#advisor').click(function(){
            $(this).popover();
            setTimeout(function(){
                $("#advisor").popover('hide');
            },5000);
        });
    });


});


