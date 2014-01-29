require.config({
    baseUrl:'/js',
    paths : {
        'jquery' : '/bower/jquery/jquery.min',
        'bootstrap' : '/bower/bootstrap/dist/js/bootstrap.min',
        'underscore' : '/bower/underscore/underscore-min',
        'backbone' : '/bower/backbone/backbone-min'
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

require(['router']);
