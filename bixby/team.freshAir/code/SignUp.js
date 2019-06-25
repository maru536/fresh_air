var http = require('http')
var console = require('console')
var fail = require('fail');

module.exports.function = function signUp (id, passwd) {
  
  var options = {
    format: 'json', 
    headers: {
      'Content-Type': 'application/json'
    }
  };
  
  var body = {
    "id": id,
    "passwd": passwd
  }
  
  var response = http.postUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/signUp', body, options);
  
  
  if (response != null && response.code != null && response.code == 200) {
    return id;
  }
  else if (response.message != null && response.code == 302) {
    throw fail.checkedError(response.message, 'AlreadyRegistedUser', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailSignUp', {})
  }
}
