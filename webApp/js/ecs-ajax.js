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
              $('#storageAnalytics').on('click', function (e) {
                window.location.href = "http://weloveclouds-stats.com:3000/dashboard/db/weloveclouds-storage";
              });
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


$(document).ready(function() {
  $('#startLoadBalancerCommand.active').on('click', function () {
    $("#startLoadBalancerCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#startLoadBalancerCommand').addClass("ecsCommandInProgress").removeClass("threed active");
    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/startLoadBalancer",
          type: "POST",


          success: function(resultData) {
            $('#startLoadBalancerCommand').removeClass("ecsCommandInProgress").addClass("ecsCommandDisabled");
            $("#startLoadBalancerCommand").html("<img class='ecsCommandImage' src='resources/startLoadBalancer.png'>");
            $('#initServiceCommand').removeClass("ecsCommandDisabled").addClass("threed active");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#startLoadBalancerCommand").html("<img class='ecsCommandImage' src='resources/startLoadBalancer.png'>");
            $('#startLoadBalancerCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
  });
});

$(document).ready(function() {
  $('#ecsAnalytics').on('click', function (e) {
    window.location.href = "http://weloveclouds-stats.com:3000/dashboard/db/weloveclouds-ECS";
  });
});
