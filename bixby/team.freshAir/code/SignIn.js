var http = require('http')
var console = require('console')

module.exports.function = function signIn (id, passwd, $vivContext) {
  var response = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/signIn', {
    format: 'json', 
    headers: {
      id: id,
      passwd: passwd
    }
  });
  
  if (response.code == 200)
    $vivContext.userid = id;
  
  console.log($vivContext);
  
  return {
    code: response.code,
    userId: id
  }
}
