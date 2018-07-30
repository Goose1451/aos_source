/**
 * Created by correnti on 20/11/2015.
 */

var userCookie = userCookie || {};

userCookie.fillParams = function(name, password, email, response){
      this.name = name;
      this.userPassword = password;
      this.email = email;
      this.response = response;
//    this.sessionId = sessionId;
//    this.id = id;
//    this.token = "Bearer " + token;
};

userCookie.getKey = function(lastUser){

      if(lastUser)
      {
            if(lastUser.response)
            {
                  return "userCookie" +
                      lastUser.response.userId
                        + lastUser.response.t_authorization;
                  //+ lastUser.response.sessionId;
            }
      }
      return "guestCookie";
};

