var http = require('http');
var console = require('console');
var dates = require('dates');
var fail = require('fail');

module.exports.function = function showPublicDust (currentPosition) {
  if (currentPosition != null) {
    var options = {
      format: 'json', 
      headers: {
        'Content-Type': 'application/json',
      }
    };

    var body = {
      "latitude": currentPosition.latitude,
      "longitude": currentPosition.longitude
    };

    var response = http.postUrl('http://ec2-15-164-164-86.ap-northeast-2.compute.amazonaws.com:8080/1.0/publicDust', body, options);
    console.log(response);

    if (response != null && response.code != null) {
      if (response.code == 200) {
        if (response.publicDust != null && response.publicDust.pm100 != null && response.publicDust.pm25 != null) {
          return {
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
        else {
          throw fail.checkedError("잘못된 응답입니다.", 'malformResponse', {});
        }
      }
      else if (response.code == 404) {
        throw fail.checkedError("지원하지 않는 지역입니다.", 'notSupportedArea', {});
      }
      else {
        throw fail.checkedError("잘못된 응답입니다.", 'malformResponse', {});
      }
    }
    else {
      throw fail.checkedError("응답을 받지 못했습니다.", 'responseFail', {});
    }
  }
  else {
    throw fail.checkedError("지원하지 않는 지역입니다.", 'notSupportedArea', {});
  }
}
