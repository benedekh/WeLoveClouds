function fetchEcsData(){
  jQuery.ajax({
        url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/status",
        type: "GET",


        success: function(resultData) {
          var template = $('#ecs-tpl').html();
          var html = Mustache.to_html(template, resultData);
          $('#ecs-info').html(html);
          $('#ecs-image').html("<img class='server-image' src='resources/serverHealthy.png'>");
        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}

function fetchRepository(){
  jQuery.ajax({
        url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/repository",
        type: "GET",

        
        success: function(resultData) {
          $('.no-server-found').hide();
          $.each(resultData["repositoryNodes"], function(index, it){
            if($("#" + it.name).length == 0) {
              var template = $('#node-tpl').html();
              var html = Mustache.to_html(template, it);
              $('#repository').append(html);
            }else{
              $("#" + it.name).remove();
              var template = $('#node-tpl').html();
              var html = Mustache.to_html(template, it);
              $('#repository').append(html);
            }
          })
        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}
