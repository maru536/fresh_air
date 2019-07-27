var http = require('http')
var console = require('console')
var fail = require('fail');

module.exports.function = function signIn ($vivContext) {
  const response = http.oauthGetUrl('https://openidconnect.googleapis.com/v1/userinfo', {format: 'json'});
  
  const revokeUrl = 'https://accounts.google.com/o/oauth2/revoke?token='+$vivContext.accessToken;
  const revokeResponse = http.oauthGetUrl(revokeUrl, {format:'json', cacheTime: 0, returnHeaders:true});
  
  if (response != null && response.email != null) {
    return response.email;  
  }
  else if (response.code == 401 || response.code == 404) {
    throw fail.checkedError("SignIn fail", 'FailSignIn', {})
  }
  else {
    throw fail.checkedError('Api call fail', 'FailApiCall', {})
  }
}
