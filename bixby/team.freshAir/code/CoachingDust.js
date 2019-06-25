var http = require('http')
var console = require('console')
var dates = require('dates')

module.exports.function = function coachingDust (address, userId) {
  
  console.log(userId);
  var options = {
    format: 'json', 
    headers: {
      'Content-Type': 'application/json',
      userId: userId
    }
  };
  
  var body = {
    "levelOne": address.levelOne,
    "levelTwo": address.levelTwo
  }
  
  var response = http.postUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/coachingDust', body, options);
  
  
  const oneSecond = 1000;
  const oneMinute = 60*oneSecond;
  const oneHour = 60*oneMinute;
  const oneDay = 24*oneHour;
  
  if (response != null) {
    if (response.coachingMessage != null) {
      if (response.latestDust != null) {
        var diffTime = dates.ZonedDateTime.now().getMillisFromEpoch() - response.latestDust.time;
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
        
        if (response.airData != null) {
          console.log("airData is not null");
          return {
            coachingMessage: response.coachingMessage,
            latestDust: {
              timeType: type,
              time: time, 
              dust: {
                pm100: response.latestDust.pm100,
                pm25: response.latestDust.pm25
              }
            },
            publicDust: {
              dust: {
                pm100: response.airData.pm100,
                pm25: response.airData.pm25
              },
              address: address
            }
          }
        }
        else {
          console.log("airData is null");
          return {
            coachingMessage: response.coachingMessage,
            latestDust: {
              timeType: type,
              time: time, 
              dust: {
                pm100: response.latestDust.pm100,
                pm25: response.latestDust.pm25
              }
            }
          }
        }
      }
      else {
        console.log("latestDust is null");
        if (response.airData != null) {
          console.log("airData is not null");
          return {
            coachingMessage: response.coachingMessage,
            publicDust: {
              dust: {
                pm100: response.airData.pm100,
                pm25: response.airData.pm25
              },
              address: address
            }
          }
        }
        else {
          console.log("airData is null");
          return {
            coachingMessage: response.coachingMessage
          }
        }
      }
    }
    else {
      console.log("coachingMessage is null");
      if (response.airData != null) {
        console.log("airData is not null");
        return {
          publicDust: {
              dust: {
                pm100: response.airData.pm100,
                pm25: response.airData.pm25
              },
            address: address
          }
        }
      }
      else {
        console.log("airData is null");
        return {
          code: 500
        }
      }
    }
  }
  else {
    console.log("response is null");
    return {
      code: 500
    }
  }
}
