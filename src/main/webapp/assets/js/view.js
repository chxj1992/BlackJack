define(['jquery','backbone'],function(){

    var view = Backbone.View.extend({
            el: $('body'),
            events: {
            },

            userInfo : function(){
                alert('userInfo');
            },

            homePage : function(){
                alert('homePage');
            }
        });

    return new view();
});
