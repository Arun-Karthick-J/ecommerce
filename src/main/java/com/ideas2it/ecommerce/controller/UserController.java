
package com.ideas2it.ecommerce.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ideas2it.ecommerce.common.Constants;
import com.ideas2it.ecommerce.common.enums.Role.USER_ROLES;
import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.logger.EcommerceLogger;
import com.ideas2it.ecommerce.model.Address;
import com.ideas2it.ecommerce.model.Customer;
import com.ideas2it.ecommerce.model.Seller;
import com.ideas2it.ecommerce.model.User;
import com.ideas2it.ecommerce.service.UserService;
import com.ideas2it.ecommerce.service.impl.UserServiceImpl;

/**
 * <p>
 * The {@code UserController} class provides functionality related to the
 * management of users. It provides option to create and validate login
 * credentials.
 * </p>
 * 
 * @author Arun Karthick.J
 */
@Controller
public class UserController {
    private static final Character INITIAL_PATH = '/';
    private static final String INDEX_PAGE = "CustomerHome";
    private static final String ADMIN_HOME = "displayCategories";
    private static final String CUSTOMER_HOME = "CustomerHome";
    private static final String SELLER_HOME = "SellerHome";
    private static final String SELLER_LOGIN = "SellerLogin";
    private static final String ADMIN_LOGIN = "adminLogin";
    private static final String ADDRESS_FORM = "AddressForm"; 

    private UserService userService = new UserServiceImpl();

    @GetMapping("/")
    public ModelAndView showInitialPage() {
        ModelAndView modelAndView = new ModelAndView(INDEX_PAGE);
        try {
            modelAndView.addObject(Constants.LABEL_PRODUCTS,
                    userService.getAllProducts());
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    userService.getAllCategories());
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Requests the view handler to show the registration form page.
     * </p>
     */
    @GetMapping("registrationForm")
    public String registrationForm(ModelMap model) {
        model.addAttribute(Constants.LABEL_REGISTER, Boolean.TRUE);
        return INDEX_PAGE;
    }

    /**
     * <p>
     * Creates an entry for the user if no such user exists else returns a
     * message accordingly.
     * </p>
     */
    @PostMapping("registerCustomer")
    public ModelAndView registerCustomer(
            @ModelAttribute("customer") Customer customer) {
        ModelAndView modelAndView = new ModelAndView(INDEX_PAGE);
        customer.setMobileNumber(customer.getUser().getUserName());
        try {
            if (userService.registerCustomer(customer)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_REGISTER_SUCCESS
                                + customer.getMobileNumber());
            }
        } catch (EcommerceException e) {
            modelAndView.setViewName(INDEX_PAGE);
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Creates an entry for the user if no such user exists else returns a
     * message accordingly.
     * </p>
     */
    @PostMapping("registerSeller")
    public ModelAndView registerSeller(
            @ModelAttribute("seller") Seller seller) {
        ModelAndView modelAndView = new ModelAndView(SELLER_LOGIN);
        seller.setMobileNumber(seller.getUser().getUserName());
        try {
            if (userService.registerSeller(seller)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_REGISTER_SUCCESS
                                + seller.getMobileNumber());
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Checks the username and password and logins the user if the credentials
     * are correct. Redirects to the corresponding page based on their role.
     * </p>
     */
    @PostMapping("login")
    public ModelAndView login(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.TRUE);
        session.setMaxInactiveInterval(Constants.SESSION_MAX_INACTIVE_INTERVAL);
        User user = new User();
        user.setUserName(request.getParameter(Constants.LABEL_USERNAME));
        user.setPassword(request.getParameter(Constants.LABEL_PASSWORD));
        user.setRole(
                USER_ROLES.valueOf(request.getParameter(Constants.LABEL_ROLE)));
        ModelAndView modelAndView = new ModelAndView();
        if (USER_ROLES.CUSTOMER == user.getRole()) {
            modelAndView.setViewName(Constants.REDIRECT + INITIAL_PATH);
        } else if (USER_ROLES.SELLER == user.getRole()) {
            modelAndView.setViewName(SELLER_LOGIN);
        } else if (USER_ROLES.ADMIN == user.getRole()) {
            modelAndView.setViewName(ADMIN_LOGIN);
        }
        try {
            Boolean isAuthenticated = userService.validateUser(user);
            if (null == isAuthenticated) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_NO_SUCH_USER_EXISTS);
            } else if (!isAuthenticated) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_INVALID_CREDENTIALS);
            } else {
                session.setAttribute(Constants.LABEL_USER_ID, user.getId());
                session.setAttribute(Constants.LABEL_ROLE, user.getRole());
                if (USER_ROLES.CUSTOMER == user.getRole()) {
                    Customer customer = userService
                            .searchCustomer(user.getId());
                    session.setAttribute(Constants.LABEL_CUSTOMER, customer);
                } else if (USER_ROLES.SELLER == user.getRole()) {
                    Seller seller = userService.searchSeller(user.getId());
                    session.setAttribute(Constants.LABEL_SELLER_ID,
                            seller.getId());
                    modelAndView.addObject(Constants.LABEL_CATEGORIES,
                            userService.getAllCategories());
                    modelAndView.setViewName(SELLER_HOME);
                } else {
                    modelAndView.addObject(Constants.LABEL_CATEGORIES,
                            userService.getAllCategories());
                    modelAndView.setViewName(ADMIN_HOME);
                }
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Invalidates the session and logs the customer out of the page.
     * </p>
     */
    @GetMapping("logout")
    public String logout(HttpSession session, ModelMap model) {
        String viewName = Constants.REDIRECT + INITIAL_PATH;
        USER_ROLES role = USER_ROLES.CUSTOMER;
        if (null != session) {
            role = (USER_ROLES) session.getAttribute(Constants.LABEL_ROLE);
            session.invalidate();
        }

        if (USER_ROLES.SELLER == role) {
            viewName = SELLER_LOGIN;
        } else if (USER_ROLES.ADMIN == role) {
            viewName = ADMIN_LOGIN;
        }
        model.addAttribute(Constants.LABEL_MESSAGE, Constants.MSG_LOGGED_OUT);
        return viewName;
    }
    
    /**
     * <p>
     * Shows the new address form that can be used to provide additional
     * addresses.
     * </p>
     */
    @GetMapping("newAddress")
    public ModelAndView createAddress() {
        ModelAndView modelAndView = new ModelAndView(ADDRESS_FORM);
        modelAndView.addObject(Constants.LABEL_ADDRESS, new Address());
        return modelAndView;
    }

    /**
     * <p>
     * Adds the new address to the user.
     * </p>
     */
    @PostMapping("addAddress")
    public ModelAndView addAddress(@ModelAttribute("address") Address address,
            HttpSession session) throws EcommerceException {
        ModelAndView modelAndView = new ModelAndView();
        USER_ROLES role = (USER_ROLES) session.getAttribute(Constants.LABEL_ROLE);
        if (USER_ROLES.CUSTOMER == role) {
            modelAndView.setViewName(CUSTOMER_HOME);
        } else if (USER_ROLES.SELLER == role) {
            modelAndView.setViewName(SELLER_HOME);
        }
        User user = userService.searchUser(
                (Integer) session.getAttribute(Constants.LABEL_USER_ID));
        try {
            List<Address> addresses = user.getAddresses();
            addresses.add(address);
            user.setAddresses(addresses);
            if (userService.updateUser(user)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_ADDRESS_SUCCESS);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Shows the seller edit address form that can be used to provide updated
     * address details for the warehouse address given.
     * </p>
     */
    @PostMapping("editAddress")
    public ModelAndView editAddress(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView(ADDRESS_FORM);
        modelAndView.addObject("addressForm", Boolean.TRUE);
        Address address = new Address();
        Integer.parseInt(request.getParameter(Constants.LABEL_ADDRESS));
        modelAndView.addObject(Constants.LABEL_ADDRESS, address);
        return modelAndView;
    }

    /**
     * <p>
     * Updates the warehouse address based on the input from edit warehouse
     * address form.
     * </p>
     */
    @PostMapping("updateAddress")
    public ModelAndView updateAddress(
            @ModelAttribute("address") Address address, HttpSession session)
            throws EcommerceException {
        ModelAndView modelAndView = new ModelAndView(SELLER_HOME);
        User user = userService.searchUser(
                (Integer) session.getAttribute(Constants.LABEL_USER_ID));
        try {
            List<Address> addresses = user.getAddresses();
            Address existingaddress = new Address();
            existingaddress.setId(address.getId());
            addresses.remove(existingaddress);
            addresses.add(address);
            user.setAddresses(addresses);
            if (userService.updateUser(user)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_UPDATE_ADDRESS_SUCCESS);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * Removes the corresponding warehouse address from the seller based on the
     * seller input.
     * </p>
     */
    @PostMapping("removeAddress")
    public ModelAndView removeAddress(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView(SELLER_HOME);
        try {
            User user = userService.searchUser(
                    (Integer) session.getAttribute(Constants.LABEL_USER_ID));
            Address address = new Address();
            address.setId(Integer.parseInt(
                    request.getParameter(Constants.LABEL_ADDRESS_ID)));
            List<Address> addresses = user.getAddresses();
            addresses.remove(address);
            user.setAddresses(addresses);
            if (userService.updateUser(user)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_DELETE_ADDRESS_SUCCESS);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }


}
