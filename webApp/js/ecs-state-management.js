function updateEcsCommandFromEcsStatus(){
  switch($(".ecsStatus").html()){
    case "Waiting for load balancer initialization":
      $('#startLoadBalancerCommand').removeClass("ecsCommandDisabled ecsCommandInProgress").addClass("active threed");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      resetAllCommandImageAndProgessState();
      break;
    case "Waiting for service initialization":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').removeClass("ecsCommandDisabled").addClass("active threed");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      resetAllCommandImageAndProgessState();
      break;
    case "Initialized":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').removeClass("ecsCommandDisabled").addClass("active threed");
      $('#removeNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
      $('#startNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
      $('#stopNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
      $('#shutdownNodeCommand').removeClass("ecsCommandDisabled").addClass("threed active");
      resetAllCommandImageAndProgessState();
      break;
    case "Initializing service":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#initServiceCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Starting load balancer":
      $('#startLoadBalancerCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#startLoadBalancerCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Starting node":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#startNodeCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Stopping node":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#stopNodeCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Removing node":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#removeNodeCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Adding node":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#addNodeCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    case "Shutting down node":
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#shutdownNodeCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      break;
    case "Initializing load balancer":
      $('#startLoadBalancerCommand').addClass("ecsCommandInProgress").removeClass("threed active ecsCommandDisabled");
      $('#startLoadBalancerCommand').html("<img class='ecsCommandImage' src='resources/loading.gif'>");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      break;
    default:
      $('#startLoadBalancerCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#initServiceCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#addNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#removeNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#startNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#stopNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
      $('#shutdownNodeCommand').addClass("ecsCommandDisabled").removeClass("threed active");
  }
}

function resetAllCommandImageAndProgessState(){
    $("#startLoadBalancerCommand").html("<img class='ecsCommandImage' src='resources/startLoadBalancer.png'>");
    $("#initServiceCommand").html("<img class='ecsCommandImage' src='resources/initService.png'>");
    $("#startNodeCommand").html("<img class='ecsCommandImage' src='resources/start.png'>");
    $("#stopNodeCommand").html("<img class='ecsCommandImage' src='resources/stop.png'>");
    $("#shutdownNodeCommand").html("<img class='ecsCommandImage' src='resources/shutdown.png'>");
    $("#removeNodeCommand").html("<img class='ecsCommandImage' src='resources/remove.png'>");
    $("#addNodeCommand").html("<img class='ecsCommandImage' src='resources/add.png'>");
    $(".ecsCommand").removeClass("ecsCommandInProgress");
}
