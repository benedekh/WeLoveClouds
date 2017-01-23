function fetchEcsData(){
  jQuery.ajax({
        url: "http://weloveclouds-ecs.com:8081/rest/api/v1/ecs/status",
        type: "GET",

        contentType: 'application/json; charset=utf-8',
        success: function(resultData) {
          var template = $('#ecs-tlp').html();
          var html = Mustache.to_html(template, resultData);
          $('#ecs-info').html(html);

        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}
