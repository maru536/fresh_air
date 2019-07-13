var http = require('http')
var console = require('console')
var fail = require('fail');

module.exports.function = function signIn (userId, passwd) {
  var response = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/signIn', {
    format: 'json', 
    headers: {
      userId: userId,
      passwd: passwd
    }
  });
  
  
  if (response != null && response.code != null && response.code == 200) {
    return userId;
  }
  else if (response.code == 401 || response.code == 404) {
    throw fail.checkedError("SignIn fail", 'FailSignIn', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
