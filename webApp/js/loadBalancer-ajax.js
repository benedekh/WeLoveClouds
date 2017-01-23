function fetchLoadBalancerData(){
  jQuery.ajax({
        url: "http://weloveclouds-lb.com:8080/rest/api/v1/loadBalancer/status",
        type: "GET",

        contentType: 'application/json; charset=utf-8',
        success: function(resultData) {
          var template = $('#loadbalancer-tlp').html();
          var html = Mustache.to_html(template, resultData);
          $('#loadbalancer-info').html(html);

        },
        error : function(jqXHR, textStatus, errorThrown) {
           console.log(errorThrown);
        },

        timeout: 12000000,
      })
}
