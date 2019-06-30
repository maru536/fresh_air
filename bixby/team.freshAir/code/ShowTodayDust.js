var http = require('http');
var console = require('console');
var dates = require('dates');
var fail = require('fail');

module.exports.function = function showTodayDust (userId) {
  // RollResult
  console.log(userId);
  var response = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/todayDust', {
    format: 'json', 
    headers: {
      userId: userId
    }
  });
  
  if (response != null && response.code != null && response.code == 200) {
    return {
      userId: response.dust.userId,
      dust: {
        pm100: response.dust.pm100,
        pm25: response.dust.pm25
      }
    }
  }
  else if (response != null && response.code != null && response.code == 404) {
    throw fail.checkedError('There is no Dust data', 'NoDustData', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
