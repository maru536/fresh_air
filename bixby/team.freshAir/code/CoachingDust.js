var http = require('http');
var console = require('console');
var dates = require('dates');
var fail = require('fail');

module.exports.function = function coachingDust (currentPosition, userId) {
  if (currentPosition != null) {
    var options = {
      format: 'json', 
      headers: {
        'Content-Type': 'application/json',
        userId: userId
      }
    };

    var body = {
      "latitude": currentPosition.latitude,
      "longitude": currentPosition.longitude
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

          if (response.publicDust != null) {
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
                  pm100: response.publicDust.pm100,
                  pm25: response.publicDust.pm25
                },
                address: {
                  levelOne: response.publicDust.addressLevelOne,
                  levelTwo: response.publicDust.addressLevelTwo
                }
              }
            }
          }
          else {
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
          if (response.publicDust != null) {
            return {
              coachingMessage: response.coachingMessage,
              publicDust: {
                dust: {
                  pm100: response.publicDust.pm100,
                  pm25: response.publicDust.pm25
                },
                address: {
                  levelOne: response.publicDust.addressLevelOne,
                  levelTwo: response.publicDust.addressLevelTwo
                }
              }
            }
          }
          else {
            return {
              coachingMessage: response.coachingMessage
            }
          }
        }
      }
      else {
        if (response.publicDust != null) {
          return {
            publicDust: {
                dust: {
                  pm100: response.publicDust.pm100,
                  pm25: response.publicDust.pm25
                },
                address: {
                  levelOne: response.publicDust.addressLevelOne,
                  levelTwo: response.publicDust.addressLevelTwo
                }
            }
          }
        }
        else {
          throw fail.checkedError("응답이 없습니다.", 'noResponseFail', {});
        }
      }
    }
    else {
      throw fail.checkedError("응답이 없습니다.", 'noResponseFail', {});
    }
  }
  else {
    throw fail.checkedError("지원하지 않는 지역입니다.", 'getAddressFail', {});
  }
}
