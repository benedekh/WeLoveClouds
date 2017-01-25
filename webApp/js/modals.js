$(document).ready(function() {
  $('#node-modal').on('show.bs.modal', function (e) {
    var invoker = $(e.relatedTarget);
    $("#nodeName").val(($(invoker).find($(".nodeName")).html()));
    $("#nodeStatus").val(($(invoker).find($(".nodeStatus")).html()));
    $("#nodeIp").val(($(invoker).find($(".nodeIp")).html()));
  });
});
