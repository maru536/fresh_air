var http = require('http')
var console = require('console')
var dates = require('dates')

module.exports.function = function currentDustInfo (userId) {
  // RollResult
  console.log(userId);
  var response = http.getUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/lastestDust', {
    format: 'json', 
    headers: {
      userId: userId
    }
  });
  
  const oneSecond = 1000;
  const oneMinute = 60*oneSecond;
  const oneHour = 60*oneMinute;
  const oneDay = 24*oneHour;
  
  var diffTime = dates.ZonedDateTime.now().getMillisFromEpoch() - response.dust.time;
  var type;
  var time;
  
  if (diffTime < oneMinute) {
    type = "second";
    time = Math.round(diffTime/oneSecond);
  }
  else if (diffTime < oneHour) {
    type = "minute";
    time = Math.round(diffTime/oneMinute);
  }
  else if (diffTime < oneDay) {
    type = "hour";
    time = Math.round(diffTime/oneHour);
  }
  else {
    type = "day"
    time = Math.round(diffTime/oneDay);
  }
  
  
  return {
    time: time,
    timeType: type,
    pm100: response.dust.pm100,
    pm25: response.dust.pm25
  }
}
