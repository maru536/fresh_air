var http = require('http');
var console = require('console');
var dates = require('dates');
var fail = require('fail');

module.exports.function = function showYesterdayDust (userId) {
  // RollResult
  console.log(userId);
  
  var apiResponse = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/public/yesterdayDustMap', {
    format: 'json',
    headers: {
      userId: userId
    }
  });
  
  if (apiResponse != null && apiResponse.code == 200) {
    return {
      userId: apiResponse.userId,
      avgDust: apiResponse.avgDust, 
      representativeDustPositionList: apiResponse.representDustWithLocationList
    }
  }
  else if (apiResponse.code == 404) {
    throw fail.checkedError('There is no Dust data', 'NoDustData', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
