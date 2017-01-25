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
});
