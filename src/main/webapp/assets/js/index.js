define('index',['jquery','bootstrap','utils/datetime'],function(require, exports, module) {

    var $ = require('jquery');
    var datetime = require('datetime');

    $(document).ready(function(){

        $('#advisor').click(function(){
            $(this).popover();
            setTimeout(function(){
                $("#advisor").popover('hide');
            },5000);
        });
    });


});

