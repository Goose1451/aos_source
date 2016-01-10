package com.advantage.account.store.api;

import com.advantage.account.store.user.dto.CountryResponseStatus;
import com.advantage.account.store.user.model.AppUser;
import com.advantage.account.store.user.model.Country;
import com.advantage.account.store.user.services.AppUserService;
import com.advantage.account.store.user.services.CountryService;
import com.advantage.common.SecurityTools;
import com.advantage.common.dto.AccountType;
import com.advantage.common.dto.AppUserDto;
import com.advantage.common.dto.AppUserResponseDto;
import com.advantage.common.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Binyamin Regev on 16/11/2015.
 */
@RestController
@RequestMapping(value = Constants.URI_API + "/v1")
public class AccountController {

    //private static final String REQUEST_PARAM_COUNTRY_ID = "country_id";

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CountryService countryService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ApiOperation(value = "Get all registered users")
    public ResponseEntity<List<AppUser>> getAllAppUsers(HttpServletRequest request, HttpServletResponse response) {

        SecurityTools.isAutorized(request.getHeader("Authorization"), AccountType.ADMIN);
        List<AppUser> appUsers = appUserService.getAllAppUsers();

        return new ResponseEntity<>(appUsers, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "IsExists registered user by user-id")
    public ResponseEntity<Boolean> get(@PathVariable("user_id") long userId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        Boolean isExists = appUserService.isExists(userId);

        if (isExists) {
            //  userId belongs to a registered user
            return new ResponseEntity<>(isExists, HttpStatus.OK);
        } else {
            //  userId is not a registered user
            return new ResponseEntity<>(isExists, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/login_OLD", method = RequestMethod.POST)
    @ApiOperation(value = "Login user")
    public ResponseEntity<AppUserResponseDto> doLogin(@RequestBody AppUserDto appUser,
                                                      HttpServletRequest request, HttpServletResponse response) {

        response.setHeader("sessionId", request.getSession().getId());

        final AppUserResponseDto appUserResponseDto = appUserService.doLogin(appUser.getLoginUser(),
                appUser.getLoginPassword(),
                appUser.getEmail());

        if (appUserResponseDto.isSuccess()) {
            HttpSession session = request.getSession();
            session.setAttribute(Constants.UserSession.TOKEN, appUserResponseDto.getToken());
            session.setAttribute(Constants.UserSession.USER_ID, appUserResponseDto.getUserId());
            session.setAttribute(Constants.UserSession.IS_SUCCESS, appUserResponseDto.isSuccess());

            //  Set SessionID to Response Entity
            //response.getHeader().
            appUserResponseDto.setSessionId(session.getId());


            return new ResponseEntity<>(appUserResponseDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(appUserResponseDto, HttpStatus.NOT_FOUND);
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "Login JWT")
    public ResponseEntity<AppUserResponseDto> doLoginJWT(@RequestBody AppUserDto appUserDto,
                                                         HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus;

        response.setHeader("sessionId", request.getSession().getId());

        AppUserResponseDto appUserResponseDto = appUserService.doLogin(appUserDto.getLoginUser(),
                appUserDto.getLoginPassword(),
                appUserDto.getEmail());

        if (appUserResponseDto.isSuccess()) {
            HttpSession session = request.getSession();

            //session.setAttribute(Constants.UserSession.TOKEN, appUserResponseDto.getToken());
            session.setAttribute(Constants.UserSession.USER_ID, appUserResponseDto.getUserId());
            session.setAttribute(Constants.UserSession.IS_SUCCESS, appUserResponseDto.isSuccess());

            //  Set SessionID to Response Entity
            //response.getHeader().
            appUserResponseDto.setSessionId(session.getId());

//            Cookie cookie = new Cookie("token", appUserResponseDto.getToken());
//            response.addCookie(cookie);

            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(appUserResponseDto, httpStatus);
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ApiOperation(value = "Register new application user")
    public ResponseEntity<AppUserResponseDto> createUser(@RequestBody AppUser appUser, HttpServletRequest request) {

        final AppUserResponseDto appUserResponseDto = appUserService.create(
                appUser.getAppUserType(),
                appUser.getLastName(),
                appUser.getFirstName(),
                appUser.getLoginName(),
                appUser.getPassword(),
                appUser.getCountry(),
                appUser.getPhoneNumber(),
                appUser.getStateProvince(),
                appUser.getCityName(),
                appUser.getAddress(),
                appUser.getZipcode(),
                appUser.getEmail(),
                appUser.getAllowOffersPromotion());

        if (appUserResponseDto.isSuccess())
            return new ResponseEntity<>(appUserResponseDto, HttpStatus.OK);
        else
            return new ResponseEntity<>(appUserResponseDto, HttpStatus.CONFLICT);

    }

    @RequestMapping(value = "/countries", method = RequestMethod.GET)
    @ApiOperation(value = "Get all countries")
    public ResponseEntity<List<Country>> getCountries(HttpServletRequest request) {
        List<Country> countries = countryService.getAllCountries();
        return new ResponseEntity<>(countries, HttpStatus.OK);

    }

    @RequestMapping(value = "/countries", method = RequestMethod.POST)
    @ApiOperation(value = "Create country")
    public ResponseEntity<CountryResponseStatus> createCountry(@RequestBody Country country, HttpServletRequest request) {

        final CountryResponseStatus countryResponseStatus = countryService.create(country.getName(),
                country.getIsoName(),
                country.getPhonePrefix());

        if (countryResponseStatus.isSuccess())
            return new ResponseEntity<>(countryResponseStatus, HttpStatus.OK);
        else
            return new ResponseEntity<>(countryResponseStatus, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/countries/search", method = RequestMethod.GET)
    @ApiOperation(value = "Search for countries")
    public ResponseEntity<List<Country>> searchInCountries(@RequestParam(value = "phonePrefix", required = false)
                                                           Integer internationalPhonePrefix,
                                                           @RequestParam(value = "nameStartFrom", required = false)
                                                           String startOfName, HttpServletRequest request) {
        List<Country> countries;

        if (internationalPhonePrefix != null && startOfName != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (internationalPhonePrefix != null) {
            countries = countryService.getCountriesByPhonePrefix(internationalPhonePrefix);
        } else {//nameStartFrom!=null
            countries = countryService.getCountriesByPartialName(startOfName);
        }
        if (countries == null || countries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(countries, HttpStatus.OK);
        }
    }

}
