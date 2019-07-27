var http = require('http')

module.exports.function = function revoke ($vivContext) {
  if ($vivContext.accessToken != null) {
    http.oauthGetUrl('https://accounts.google.com/o/oauth2/revoke?token='+$vivContext.accessToken, {format:'json', cacheTime: 0, returnHeaders:true});
  }
}
