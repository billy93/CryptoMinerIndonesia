package com.cryptominer.indonesia.web.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.cryptominer.indonesia.config.Constants;
import com.cryptominer.indonesia.domain.User;
import com.cryptominer.indonesia.domain.UserReferral;
import com.cryptominer.indonesia.repository.UserReferralRepository;
import com.cryptominer.indonesia.repository.UserRepository;
import com.cryptominer.indonesia.security.AuthoritiesConstants;
import com.cryptominer.indonesia.security.SecurityUtils;
import com.cryptominer.indonesia.service.MailService;
import com.cryptominer.indonesia.service.UserService;
import com.cryptominer.indonesia.service.dto.UserDTO;
import com.cryptominer.indonesia.web.rest.errors.BadRequestAlertException;
import com.cryptominer.indonesia.web.rest.errors.EmailAlreadyUsedException;
import com.cryptominer.indonesia.web.rest.errors.LoginAlreadyUsedException;
import com.cryptominer.indonesia.web.rest.util.HeaderUtil;
import com.cryptominer.indonesia.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the User entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final UserReferralRepository userReferralRepository;
    
    public UserResource(UserRepository userRepository, UserService userService, MailService mailService, UserReferralRepository userReferralRepository) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.userReferralRepository = userReferralRepository;
    }

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws BadRequestAlertException 400 (Bad Request) if the login or email is already in use
     */
    @PostMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert( "A user is created with identifier " + newUser.getLogin(), newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * PUT /users : Updates an existing User.
     *
     * @param userDTO the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user
     * @throws EmailAlreadyUsedException 400 (Bad Request) if the email is already in use
     * @throws LoginAlreadyUsedException 400 (Bad Request) if the login is already in use
     */
    @PutMapping("/users")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("REST request to update User : {}", userDTO);
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser,
            HeaderUtil.createAlert("A user is updated with identifier " + userDTO.getLogin(), userDTO.getLogin()));
    }

    /**
     * GET /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users")
    @Timed
    public ResponseEntity<List<User>> getAllUsers(Pageable pageable) {
        final Page<User> page = userService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET /users/referral : get all users referral.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users/referral")
    @Timed
    public ResponseEntity<List<UserRef>> getAllUserReferrals() {
    		
    		List<UserRef> userRefs = new ArrayList<UserResource.UserRef>();
		UserRef userRef = new UserRef();
		userRef.name = SecurityUtils.getCurrentUserLogin().get();   
		
    		List<UserReferral> userReferrals = userReferralRepository.findAllByUsername(SecurityUtils.getCurrentUserLogin().get());
        for(UserReferral ur : userReferrals) {
        		UserRef userRef1 = new UserRef();
        		userRef1.name = ur.getReferral();
    			userRef.children.add(userRef1);
        }
        
    		/*
    		List<UserRef> userRefs = new ArrayList<UserResource.UserRef>();
    		UserRef userRef = new UserRef();
    		userRef.name = "test1";    		
	    		List<UserRef> userRefs1 = new ArrayList<>();
	    		UserRef userRef1 = new UserRef();
	    		userRef1.name = "test2";
	    		userRefs1.add(userRef1);
	    		userRef.children = userRefs1;    		
    		userRefs.add(userRef);
    		*/
    		
        userRefs.add(userRef);
    		return new ResponseEntity<>(userRefs, null, HttpStatus.OK);
    }

    /**
     * GET /users/referral : get all users referral.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users/referral/{username}")
    @Timed
    public ResponseEntity<List<UserRef>> getUserReferrals(@PathVariable String username) {
    		List<UserRef> userRefs = new ArrayList<>();
    		List<UserReferral> userReferrals = userReferralRepository.findAllByUsername(username);
        for(UserReferral ur : userReferrals) {
        		UserRef userRef1 = new UserRef();
        		userRef1.name = ur.getReferral();
        		userRefs.add(userRef1);
        }        
    		return new ResponseEntity<>(userRefs, null, HttpStatus.OK);
    }
    
    private class UserRef{
	    private String name;
	    private List<UserRef> children = new ArrayList<>();
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<UserRef> getChildren() {
			return children;
		}
		public void setChildren(List<UserRef> children) {
			this.children = children;
		}
    }
    
    /**
     * @return a string list of the all of the roles
     */
    @GetMapping("/users/authorities")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public List<String> getAuthorities() {
        return userService.getAuthorities();
    }

    /**
     * GET /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new));
    }

    /**
     * DELETE /users/:login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUser(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "A user is deleted with identifier " + login, login)).build();
    }
    
    /**
     * POST  /users/gauth  : Enable/Disable gauth user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntax is incorrect
     * @throws BadRequestAlertException 400 (Bad Request) if the login or email is already in use
     */
    @PostMapping("/users/gauth")
    @Timed
    public ResponseEntity<User> gauth(@RequestBody Gauth gauth) throws URISyntaxException {
        log.debug("REST request to enable/disable Gauth : {}", gauth);

        if (gauth.getType().contentEquals("disable")) {
            if(gauth.getCode() == null) {
            	throw new BadRequestAlertException("A gauth code cannot be blank", "userManagement", "idexists");
            }
            else {
            	User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
                final Totp totp = new Totp(user.getSecret());
                if (!isValidLong(gauth.getCode()) || !totp.verify(gauth.getCode())) {
                    throw new BadCredentialsException("Invalid verfication code");
                }
                else {
                	user.setEnabled(false);
                	userRepository.save(user);
                }
            }
            // Lowercase the user login before comparing with database
        } else if (gauth.getType().contentEquals("enable")) {
            if(gauth.getCode() == null) {
            	throw new BadRequestAlertException("A gauth code cannot be blank", "userManagement", "idexists");
            }
            else {
            	User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
                final Totp totp = new Totp(user.getSecret());
                if (!isValidLong(gauth.getCode()) || !totp.verify(gauth.getCode())) {
                    throw new BadCredentialsException("Invalid verfication code");
                }
                else {
                	user.setEnabled(true);
                	userRepository.save(user);
                }
            }
            // Lowercase the user login before comparing with database
        } 
        /*else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            User newUser = userService.createUser(userDTO);
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert( "A user is created with identifier " + newUser.getLogin(), newUser.getLogin()))
                .body(newUser);
        }*/
        
        return ResponseEntity.ok().build();
    }
    
    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "CryptoMinerIndonesia";
    
    public String generateQRUrl(User user) throws UnsupportedEncodingException {
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, user.getEmail(), user.getSecret(), APP_NAME), "UTF-8");
    }
    
    /**
     * GET /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     */
    @GetMapping("/users/getOtp")
    @Timed
    public ResponseEntity<Gauth> getOtp() {
    	User u = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
    	Gauth gauth = new Gauth();
    	
    	String qrUrl = null;
    	try {
    		qrUrl = generateQRUrl(u);
    		gauth.setCode(qrUrl);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ResponseEntity<>(gauth, HttpStatus.OK);
    }
    
    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static class Gauth{
    	private String code;
    	private String type;
    	
    	public Gauth() {
			// TODO Auto-generated constructor stub
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
    	
    	
    }
}
