function fetchEcsData(){
  jQuery.ajax({
        url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/status",
        type: "GET",


        success: function(resultData) {
          var template = $('#ecs-tpl').html();
          var html = Mustache.to_html(template, resultData);
          $('#ecs-info').html(html);
          $('#ecs-image').html("<img class='server-image' src='resources/serverHealthy.png'>");
          $("#ecsStatus").val($(".ecsStatus").html());
          updateEcsCommandFromEcsStatus();
        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      });
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
  $('#startLoadBalancerCommand').on('click', function () {
    if($('#startLoadBalancerCommand').hasClass("active")){
    $("#startLoadBalancerCommand").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
    $('#startLoadBalancerCommand').addClass("ecsCommandInProgress").removeClass("threed active");
    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/startLoadBalancer",
          type: "POST",


          success: function(resultData) {
            $(".ecsStatus").html("Initializing load balancer");
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
      }
  });

  $('#addNodeButton').on('click', function () {
    var cacheSize = $("#addNodeCacheSize").val();
    var displacementStrategy = $("#addNodeDisplacementStrategy").val();
    var autoStart = $("#addNodeAutoStart").val();
    if(!isNaN(cacheSize)){
      $("#addNodeCommand").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#addNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");
    jQuery.ajax({
          url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/addNode?cacheSize=" + cacheSize + "&displacementStrategy=" + displacementStrategy + "&autoStart="+autoStart,
          type: "POST",


          success: function(resultData) {
            $(".ecsStatus").html("Adding node");
          },
          error : function(errorThrown) {
            var errorMessage = jQuery.parseJSON(errorThrown.responseText).errorMessage;
            $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
              "<div class='alertWarningMessage'></div>" + errorMessage +"</div>");
            $("#addNodeCommand").html("<img class='ecsCommandImage' src='resources/add.png'>");
            $('#addNodeCommand').removeClass("ecsCommandInProgress").addClass("threed active");
          },

          timeout: 12000000,
        })
      }else{
        $("#errorMessageDanger").html("<div class='alert alert-danger alert-dismissible' role='alert'><button type='button' class='close' data-dismiss='alert' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
          "<div class='alertWarningMessage'></div>Invalid parameters ! The cache size should be an integer.</div>");
      }
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
            $(".ecsStatus").html("Initializing service");
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
    if($('#startNodeCommand').hasClass("active")){
      $("#startNodeCommand").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#startNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

      jQuery.ajax({
            url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/startNode",
            type: "POST",

            success: function(resultData) {

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
      }
  });

  $('#stopNodeCommand').on('click', function () {
    if($('#stopNodeCommand').hasClass("active")){
      $("#stopNodeCommand").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#stopNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

      jQuery.ajax({
            url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/stopNode",
            type: "POST",

            success: function(resultData) {
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
      }
  });

  $('#shutdownNodeCommand').on('click', function () {
    if(  $('#shutdownNodeCommand').hasClass("active")){
      $("#shutdownNodeCommand").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#shutdownNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

      jQuery.ajax({
            url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/shutdown",
            type: "POST",

            success: function(resultData) {
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
      }
  });

  $('#removeNodeCommand').on('click', function () {
    if($('#removeNodeCommand').hasClass("active")){
      $("#removeNodeCommand.active").html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#removeNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active");

      jQuery.ajax({
            url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/removeNode",
            type: "POST",

            success: function(resultData) {
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
      }
  });

});

$(document).ready(function() {
  $('#ecsAnalytics').on('click', function (e) {
    window.open("http://weloveclouds-stats.com:3000/dashboard/db/weloveclouds-ECS", 'Ecs Stats');
  });
});
