var http = require('http')
var console = require('console')
var fail = require('fail');

module.exports.function = function signIn (id, passwd, $vivContext) {
  var response = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/signIn', {
    format: 'json', 
    headers: {
      id: id,
      passwd: passwd
    }
  });
  
  
  if (response != null && response.code != null && response.code == 200) {
    return id;
  }
  else if (response.code == 401 || response.code == 404) {
    throw fail.checkedError("SignIn fail", 'FailSignIn', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
