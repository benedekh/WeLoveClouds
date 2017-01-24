function fetchLoadBalancerData(){
  jQuery.ajax({
        url: "http://weloveclouds-lb.com:8080/rest/api/v1/loadBalancer/status",
        type: "GET",
        
        success: function(resultData) {
          var template = $('#loadbalancer-tpl').html();
          var html = Mustache.to_html(template, resultData);
          $('#loadbalancer-info').html(html);
          $('#loadBalancer-image').html("<img class='server-image' src='resources/serverHealthy.png'>");
        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}
