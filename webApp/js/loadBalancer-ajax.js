function fetchLoadBalancerData(){
  jQuery.ajax({
        url: "http://weloveclouds-lb.com:8080/rest/api/v1/loadBalancer/status",
        type: "GET",

        success: function(resultData) {
          var template = $('#loadbalancer-tpl').html();
          var html = Mustache.to_html(template, resultData);
          $('#loadbalancer-info').html(html);
          $('#loadBalancer-image').html("<img class='server-image' src='resources/serverHealthy.png'>");
          $("#loadBalancerStatus").html($(".loadBalancerStatus").html());
        },
        error : function(jqXHR, textStatus, errorThrown) {
          $('#loadbalancer-info').html("<img class='loading-gif' src='resources/loading.gif'>");
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}

$(document).ready(function() {
  $('#loadBalancerAnalytics').on('click', function (e) {
    window.location.href = "http://weloveclouds-stats.com:3000/dashboard/db/weloveclouds-loadbalancer";
  });
});
