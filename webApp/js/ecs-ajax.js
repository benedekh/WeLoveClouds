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

  $('#initializeServiceButton').on('click', function () {
    var numberOfNodes = $("#numberOfNodesToInitialize").val();
    var cacheSize = $("#cacheSize").val();
    var displacementStrategy = $("#displacementStrategy").val();
    if(!isNaN(numberOfNodes) && !isNaN(cacheSize)){
      $("#initServiceCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#initServiceCommand').addClass("ecsCommandInProgress").removeClass("threed active");
    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/initService?numberOfNodes="+ numberOfNodes +"&cacheSize=" + cacheSize + "&displacementStrategy=" + displacementStrategy,
          type: "POST",


          success: function(resultData) {
            $('#initServiceCommand').removeClass("ecsCommandInProgress").addClass("ecsCommandDisabled");
            $("#initServiceCommand").html("<img class='ecsCommandImage' src='resources/initService.png'>");
            $('#addNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
            $('#removeNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
            $('#startNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
            $('#shutdownNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#initServiceCommand").html("<img class='ecsCommandImage' src='resources/initService.png'>");
            $('#initServiceCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
      }else{
        $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
          "<div class='alertWarningMessage'></div>Invalid parameters ! The number of nodes and the cache size should be integers.</div>");
      }
  });

  $('#startNodeCommand').on('click', function () {
    $("#startNodeCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#startNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/startNode",
          type: "POST",

          success: function(resultData) {
            $('#startNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
            $("#startNodeCommand").html("<img class='ecsCommandImage' src='resources/start.png'>");
            $('#stopNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#startNodeCommand").html("<img class='ecsCommandImage' src='resources/start.png'>");
            $('#startNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
  });

  $('#stopNodeCommand').on('click', function () {
    $("#stopNodeCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#stopNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/stopNode",
          type: "POST",

          success: function(resultData) {
            $('#stopNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
            $("#stopNodeCommand").html("<img class='ecsCommandImage' src='resources/stop.png'>");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#stopNodeCommand").html("<img class='ecsCommandImage' src='resources/stop.png'>");
            $('#stopNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
  });

  $('#shutdownNodeCommand').on('click', function () {
    $("#shutdownNodeCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#shutdownNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/shutdown",
          type: "POST",

          success: function(resultData) {
            $('#shutdownNodeCommand').removeClass("ecsCommandInProgress").addClass("ecsCommandDisabled");
            $("#shutdownNodeCommand").html("<img class='ecsCommandImage' src='resources/shutdown.png'>");
            $("#initServiceCommand").removeClass("ecsCommandDisabled").addClass("threed active");
            $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
            $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
            $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
            $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#shutdownNodeCommand").html("<img class='ecsCommandImage' src='resources/shutdown.png'>");
            $('#shutdownNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
  });

  $('#removeNodeCommand').on('click', function () {
    $("#removeNodeCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#removeNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/removeNode",
          type: "POST",

          success: function(resultData) {
            $('#removeNodeCommand').removeClass("ecsCommandInProgress").addClass("ecsCommandDisabled");
            $("#removeNodeCommand").html("<img class='ecsCommandImage' src='resources/remove.png'>");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#removeNodeCommand").html("<img class='ecsCommandImage' src='resources/remove.png'>");
            $('#removeNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
  });

});

$(document).ready(function() {
  $('#ecsAnalytics').on('click', function (e) {
    window.open("http://weloveclouds-stats.com:3000/dashboard/db/weloveclouds-ECS", 'Ecs Stats');
  });
});
