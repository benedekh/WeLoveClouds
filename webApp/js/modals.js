$(document).ready(function() {
  $('#node-modal').on('show.bs.modal', function (e) {
    var invoker = $(e.relatedTarget);
    $("#nodeName").val(($(invoker).find($(".nodeName")).html()));
    $("#nodeStatus").val(($(invoker).find($(".nodeStatus")).html()));
    $("#nodeIp").val(($(invoker).find($(".nodeIp")).html()));
    $("#nodeMetadataStatus").val(($(invoker).find($(".nodeMetadataStatus")).html()));
    $("#nodeHashKey").val(($(invoker).find($(".nodeHashKey")).html()));
    $("#nodeHashStartValue").val(($(invoker).find($(".nodeHashStartValue")).html()));
    $("#nodeHashEndValue").val(($(invoker).find($(".nodeHashEndValue")).html()));
    $("#nodeModalImage").html(($(invoker).find($(".nodeImage")).html()));
  });

  $('#ecsModal').on('show.bs.modal', function (e) {
    var invoker = $(e.relatedTarget);
    $("#ecsStatus").val(($(invoker).find($(".ecsStatus")).html()));
    $("#ecsIp").val(($(invoker).find($(".ecsIp")).html()));
    $("#ecsModalImage").html(($(invoker).find($("#ecs-image")).html()));
  });

  $('#loadBalancerModal').on('show.bs.modal', function (e) {
    var invoker = $(e.relatedTarget);
    $("#loadBalancerModalImage").html(($(invoker).find($("#loadBalancer-image")).html()));
    $("#loadBalancerStatus").html(($(invoker).find($(".loadBalancerStatus")).html()));
    $("#loadBalancerIp").html(($(invoker).find($(".loadBalancerIp")).html()));
  });  
});
