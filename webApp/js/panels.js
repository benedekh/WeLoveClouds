$(document).ready(function() {
  $('#storagePanelIcon').on('click', function (e) {
    if($('#storagePanelIcon .glyphicon.glyphicon-chevron-down').length){
        $('#storagePanelIcon .glyphicon').removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-up");
    }else{
      $('#storagePanelIcon .glyphicon').removeClass("glyphicon-chevron-up").addClass("glyphicon-chevron-down");
    }
  });
});
