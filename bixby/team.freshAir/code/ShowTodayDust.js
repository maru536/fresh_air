var http = require('http');
var console = require('console');
var dates = require('dates');
var fail = require('fail');

module.exports.function = function showTodayDust (userId) {
  // RollResult
  console.log(userId);
  var responseAvg = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/public/todayDust', {
    format: 'json', 
    headers: {
      userId: userId
    }
  });
  
  var responseDustMap = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/public/todayDustMap', {
    format: 'json', 
    headers: {
      userId: userId
    }
  });
  
  console.log(responseDustMap.representDustWithLocationList);
  
  if (responseAvg != null && responseDustMap != null && responseDustMap.code == 200 && responseAvg.code == 200) {
    return {
      userId: responseAvg.dust.userId,
      avgDust: {
        pm100: responseAvg.dust.pm100,
        pm25: responseAvg.dust.pm25
      }, 
      positionDustList: responseDustMap.representDustWithLocationList
    }
  }
  else if (responseAvg.code == 404 && responseDustMap.code == 404) {
    throw fail.checkedError('There is no Dust data', 'NoDustData', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
