package com.ideas2it.ecommerce.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ideas2it.ecommerce.common.Constants;
import com.ideas2it.ecommerce.common.enums.ORDER_STATUS;
import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.logger.EcommerceLogger;
import com.ideas2it.ecommerce.model.Address;
import com.ideas2it.ecommerce.model.CartItem;
import com.ideas2it.ecommerce.model.Customer;
import com.ideas2it.ecommerce.model.Order;
import com.ideas2it.ecommerce.model.OrderItem;
import com.ideas2it.ecommerce.model.Product;
import com.ideas2it.ecommerce.model.User;
import com.ideas2it.ecommerce.model.WarehouseProduct;
import com.ideas2it.ecommerce.service.CustomerService;
import com.ideas2it.ecommerce.service.impl.CustomerServiceImpl;

/**
 * <p>
 * CustomerController class to perform operations such as add new customer,
 * update customer details, purchase products, cancel placed orders, add
 * products to cart, remove from cart, place an order using cart products.
 * </p>
 *
 * @author Anantharaj.S
 */
@Controller
public class CustomerController {

    private CustomerService customerService = new CustomerServiceImpl();

    /**
     * <p>
     * This method is used to show the add new customer to e-commerce web-site.
     * 
     * @param request A request message from a client to a server includes,
     *                within the first line of that message, the method to be
     *                applied to the resource, the identifier of the resource in
     *                use.
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "login" is the view name. After creating
     *         customer return login page.
     */
    @PostMapping("AddCustomer")
    public ModelAndView addCustomer(
            @ModelAttribute(Constants.LABEL_CUSTOMER) Customer customer,
            HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            if (customerService.addCustomer(customer)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        customer.getName()
                                + Constants.MSG_ADD_CUSTOMER_SUCCESS);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        customer.getName() + Constants.MSG_ADD_CUSTOMER_FAIL);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject("message", e.getMessage());
        }
        modelAndView.setViewName("Login");
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to update customer details to e-commerce web-site.
     * 
     * @param request A request message from a client to a server includes,
     *                within the first line of that message, the method to be
     *                applied to the resource, the identifier of the resource in
     *                use.
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "myAccount" is the view name. After updating
     *         customer return profile page.
     */
    @PostMapping("updateCustomer")
    public ModelAndView updateCustomer( HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session
                .getAttribute(Constants.LABEL_CUSTOMER);
        try {
            customer.setName(request.getParameter("name"));
            customer.setEmailId(request.getParameter("emailId"));
            customer.setMobileNumber(request.getParameter("mobileNumber"));            
            if (customerService.updateCustomer(customer)) {
                session.setAttribute("customer", customer);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject("message", e.getMessage());
        }
        return modifyAccount(request);
    }
    
    /**
     * <p>
     * This method is used to show particular customer details..
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "CustomerUpdate" is view name and
     *         customer is model.
     */
    @GetMapping("myaccount")
    public ModelAndView modifyAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session
                .getAttribute(Constants.LABEL_CUSTOMER);
        try {
        modelAndView.addObject(Constants.LABEL_CATEGORIES,
                customerService.getAllCategories());
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        modelAndView.addObject(Constants.LABEL_CUSTOMER, customer);
        modelAndView.setViewName("myAccount");
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to display particular customer order details
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @GetMapping("myOrders")
    public ModelAndView myOrders(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session
                .getAttribute(Constants.LABEL_CUSTOMER);
        try {
            List<Order> orders = new ArrayList<Order>(customer.getOrders());
            Collections.reverse(orders);
            Map<Integer,LocalDate> returnDates = new HashMap<Integer,LocalDate>();
            LocalDate returnDate;
            for (Order order : orders) {
                returnDate = order.getOrderDate().toLocalDate().plusDays(10);
                returnDates.put(order.getId(), returnDate);
            }
            modelAndView.addObject("today", LocalDate.now());
            modelAndView.addObject("returnDates", returnDates);
            modelAndView.addObject("orders", orders);
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        modelAndView.setViewName("MyOrders");
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to cancel particular order item details
     * </p>
     *
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "myOrders" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("cancelOrder")
    public ModelAndView cancelOrder(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Integer orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
            List<Integer> orderItemIds = new ArrayList<Integer>();
            orderItemIds.add(orderItemId);
            List<OrderItem> orderItems = customerService.getOrderItemsByIds(orderItemIds);
            for (OrderItem orderItem : orderItems) {
                if (orderItemId == orderItem.getId()) {
                    orderItem.setStatus(ORDER_STATUS.CANCELLED);
                    break;
                }
            }          
            if (customerService.cancelOrder(orderItems)) {
                customer = customerService.getCustomerById(customer.getId(),
                        Boolean.TRUE);
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_CANCEL_ORDER_SUCCESS);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_CANCEL_ORDER_FAIL);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return myOrders(request);
    }


    /**
     * <p>
     * This method is used to return particular product order details
     * </p>
     *
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("returnOrder")
    public ModelAndView returnOrder(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Integer orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
            List<Integer> orderItemIds = new ArrayList<Integer>();
            orderItemIds.add(orderItemId);
            List<OrderItem> orderItems = customerService.getOrderItemsByIds(orderItemIds);
            for (OrderItem orderItem : orderItems) {
                if (orderItemId == orderItem.getId()) {
                    orderItem.setStatus(ORDER_STATUS.RETURNED);
                    break;
                }
            }          
            if (customerService.cancelOrder(orderItems)) {
                customer = customerService.getCustomerById(customer.getId(),
                        Boolean.TRUE);
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_CANCEL_ORDER_SUCCESS);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_CANCEL_ORDER_FAIL);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return myOrders(request);
    }
    
    /**
     * <p>
     * This method is used to display particular customer order details
     * </p>
     *
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "Cart" is the view name and set
     *         of cartItems is the model.
     */
    @GetMapping("Cart")
    public ModelAndView myCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        modelAndView.setViewName("Cart");
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to display particular customer order details
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("addCart")
    public ModelAndView addToCart(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            WarehouseProduct warehouseProduct = customerService
                    .getWarehouseProduct(id);
            List<CartItem> cartItems = customer.getCartItems();
            Boolean result = Boolean.TRUE;
            for (Integer i = 0; i < cartItems.size(); i++) {
                if (id == cartItems.get(i).getWarehouseProduct().getId()) {
                    cartItems.get(i)
                            .setQuantity(cartItems.get(i).getQuantity() + 1);
                    cartItems.get(i).setPrice(cartItems.get(i).getQuantity()
                            * warehouseProduct.getPrice());
                    result = Boolean.FALSE;
                    break;
                }
            }
            if (result) {
                CartItem cartItem = new CartItem();
                cartItem.setWarehouseProduct(warehouseProduct);
                cartItem.setCustomer(customer);
                cartItem.setQuantity(1);
                cartItem.setPrice(warehouseProduct.getPrice());
                cartItems.add(cartItem);
            }
            customer.setCartItems(cartItems);
            if (customerService.updateCustomer(customer)) {
                customer = customerService.getCustomerById(customer.getId(),
                        Boolean.TRUE);
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_CART_SUCCESS);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_CART_FAIL);
            }
            Product product = customerService
                    .searchProduct(warehouseProduct.getProduct().getId());
            modelAndView.addObject("product", product);
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
            modelAndView.setViewName("ProductPage");
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
            modelAndView.setViewName("CustomerHome");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to remove particular product from cart
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("removeFromCart")
    public ModelAndView removeFromCart(@RequestParam("id") String id,
            HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            List<CartItem> cartItems = customer.getCartItems();
            for (Integer i = 0; i < cartItems.size(); i++) {
                if (Integer.parseInt(id) == cartItems.get(i).getId()) {
                    cartItems.remove(cartItems.get(i));
                    break;
                }
            }
            customer.setCartItems(cartItems);
            if (customerService.updateCustomer(customer)) {
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_CART_FAIL);
            }
            modelAndView.setViewName("Cart");
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
            modelAndView.setViewName("Cart");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to remove particular product from cart
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("updateCart")
    public ModelAndView updateCart(@RequestParam("id") String id,
            HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            List<CartItem> cartItems = customer.getCartItems();
            Integer quantity = Integer
                    .parseInt(request.getParameter("quantity"));
            Float price = Float.parseFloat(request.getParameter("price"));
            for (Integer i = 0; i < cartItems.size(); i++) {
                if (Integer.parseInt(id) == cartItems.get(i).getId()) {
                    cartItems.get(i).setQuantity(quantity);
                    cartItems.get(i).setPrice(price);
                    break;
                }
            }
            customer.setCartItems(cartItems);
            if (customerService.updateCustomer(customer)) {
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
            }
            modelAndView.setViewName("Cart");
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
            modelAndView.setViewName("homepage");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to add the delivery address for place an order.
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("orderProduct")
    public ModelAndView OrderDetails(@RequestParam("id") Integer id,
            HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("buyProduct", Boolean.TRUE);
        try {
            WarehouseProduct warehouseProduct = customerService
                    .getWarehouseProduct(id);
            List<WarehouseProduct> warehouseProducts = new ArrayList<WarehouseProduct>();
            warehouseProducts.add(warehouseProduct);
            Map<Integer, Integer> quantities = new HashMap<Integer, Integer>();
            quantities.put(id, 1);
            modelAndView.addObject("warehouseProducts", warehouseProducts);
            modelAndView.addObject("quantities", quantities);
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
            modelAndView.addObject("totalPrice", warehouseProduct.getPrice());
            modelAndView.setViewName("OrderPage");
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_ADD_ORDER_FAIL);
            modelAndView.setViewName("homepage");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to purchase particular product or multiple products
     * with different quantities.
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("buyProducts")
    public ModelAndView OrderDetails(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("buyProduct", Boolean.FALSE);
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            List<Integer> warehouseProductIds = new ArrayList<Integer>();
            String[] warehouseProductIdss = request.getParameterValues("warehouseProductId");  
            if ((null == warehouseProductIdss) || (0 > warehouseProductIdss.length)) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_SELECT_ATLEAST_ONE_PRODUCT);
                return myCart(request);
            }
            for (String id : warehouseProductIdss) {
                warehouseProductIds.add(Integer.parseInt(id));
            }
            List<WarehouseProduct> warehouseProducts = customerService
                    .getWarehouseProductsByIds(warehouseProductIds);
            List<CartItem> cartItems = customer.getCartItems();
            Map<Integer, Integer> quantities = new HashMap<Integer, Integer>();
            Float totalPrice = (float) 0;
            for (Integer i = 0; i < cartItems.size(); i++) {
                for (Integer j = 0; j < warehouseProducts.size(); j++) {
                    if (warehouseProducts.get(j).getId() == cartItems.get(i)
                            .getWarehouseProduct().getId()) {
                        quantities.put(warehouseProducts.get(j).getId(),
                                cartItems.get(i).getQuantity());
                        totalPrice = totalPrice + (cartItems.get(i)
                                .getQuantity()
                                * (warehouseProducts.get(j)).getPrice());
                    }
                }
            }
            modelAndView.addObject("warehouseProducts", warehouseProducts);
            modelAndView.addObject("quantities", quantities);
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
            modelAndView.addObject("totalPrice", totalPrice);
            modelAndView.setViewName("OrderPage");

        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_ADD_ORDER_FAIL);
            modelAndView.setViewName("homepage");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to purchase particular product or multiple products
     * with different quantities.
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    @PostMapping("placeOrder")
    public ModelAndView placeOrder(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Boolean buyProduct = Boolean
                .parseBoolean(request.getParameter("buyProduct"));
        if (buyProduct) {
            modelAndView = PurchaseProductDirect(request);
        } else {
            modelAndView = PurchaseProductFromCart(request);
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to purchase a particular product.
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    private ModelAndView PurchaseProductDirect(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Integer warehouseProductId = Integer
                    .parseInt(request.getParameter("id"));
            Integer addressId = Integer
                    .parseInt(request.getParameter("addressId"));
            WarehouseProduct warehouseProduct = customerService
                    .getWarehouseProduct(warehouseProductId);
            Order order = new Order();
            order.setCustomer(customer);
            order.setPrice(warehouseProduct.getPrice());
            LocalDate todayDate = LocalDate.now();
            order.setOrderDate(Date.valueOf(todayDate));
            Address address = new Address();
            address.setId(addressId);
            order.setAddress(address);
            order.setModeOfPayment(request.getParameter("modeOfPayment"));
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(1);
            orderItem.setPrice(warehouseProduct.getPrice());
            orderItem.setStatus(ORDER_STATUS.ORDERED);
            orderItem.setWarehouseProduct(warehouseProduct);
            List<OrderItem> orderItems = new ArrayList<OrderItem>();
            orderItems.add(orderItem);
            order.setOrderItems(orderItems);
            List<OrderItem> unavailableOrderItems = customerService
                    .addOrder(order);
            modelAndView = myOrders(request);
            if (unavailableOrderItems.isEmpty()) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_ORDER_SUCCESS);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_DONT_HAVE_ENOUGH_QUANTITY);
            }
            customer = customerService.getCustomerById(customer.getId(),
                    Boolean.TRUE);
            session.setAttribute(Constants.LABEL_CUSTOMER, customer);
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_ADD_ORDER_FAIL);
            modelAndView.setViewName("CustomerOperations");
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to purchase list of products for a particular
     * customer.
     * </p>
     * 
     * @return ModelAndView ModelAndView is an object that holds both the model
     *         and view. In this method "OrdersDisplay" is the view name and set
     *         of orders and customer is the model.
     */
    public ModelAndView PurchaseProductFromCart(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        Order order;
        OrderItem orderItem;
        Customer customer = (Customer) session.getAttribute("customer");
        try {
            Address address = new Address();
            address.setId(Integer.parseInt(request.getParameter("addressId")));
            List<Integer> warehouseProductIds = new ArrayList<Integer>();
            for (String id : request.getParameterValues("id")) {
                warehouseProductIds.add(Integer.parseInt(id));
            }
            List<WarehouseProduct> warehouseProducts = customerService
                    .getWarehouseProductsByIds(warehouseProductIds);
            List<CartItem> cartItems = customer.getCartItems();
            List<Order> orders = customer.getOrders();
            List<OrderItem> orderItems = new ArrayList<OrderItem>();
            Float totalPrice = (float) 0;
            for (Integer i = 0; i < cartItems.size(); i++) {
                for (Integer j = 0; j < warehouseProducts.size(); j++) {
                    if (warehouseProducts.get(j).getId() == cartItems.get(i)
                            .getWarehouseProduct().getId()) {
                        orderItem = new OrderItem();
                        orderItem.setQuantity(cartItems.get(i).getQuantity());
                        orderItem.setPrice(cartItems.get(i).getQuantity()
                                * (warehouseProducts.get(j)).getPrice());
                        orderItem.setWarehouseProduct(warehouseProducts.get(j));
                        orderItem.setStatus(ORDER_STATUS.ORDERED);
                        orderItems.add(orderItem);
                        totalPrice = totalPrice + orderItem.getPrice();
                    }
                }
            }
            order = new Order();
            order.setAddress(address);
            order.setOrderItems(orderItems);
            LocalDate todayDate = LocalDate.now();
            order.setOrderDate(Date.valueOf(todayDate));
            order.setPrice(totalPrice);
            order.setCustomer(customer);
            order.setModeOfPayment(request.getParameter(Constants.LABEL_MODE_OF_PAYMENT));
            List<OrderItem> unplacedOrders = customerService.addOrder(order);
            modelAndView = myOrders(request);
            if (unplacedOrders.isEmpty()) {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_ORDER_SUCCESS);
                deleteCartProducts(request, customer, warehouseProducts);
            } else {
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_DONT_HAVE_ENOUGH_QUANTITY);
            }
            customer = customerService.getCustomerById(customer.getId(),
                    Boolean.TRUE);
            session.setAttribute(Constants.LABEL_CUSTOMER, customer);
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to delete cart product details after placing an order
     * </p>
     *
     * @param customer          needed for remove cart products from customer
     *                          object
     * @param warehouseProducts needed for which cart products want to remove
     *                          from customer object
     */
    private void deleteCartProducts(HttpServletRequest request,
            Customer customer, List<WarehouseProduct> warehouseProducts) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        try {
            List<CartItem> cartItems = customer.getCartItems();
            for (Integer i = 0; i < cartItems.size(); i++) {
                if (warehouseProducts.get(i).getId() == cartItems.get(i)
                        .getWarehouseProduct().getId()) {
                    cartItems.remove(cartItems.get(i));
                }
            }
            customer.setCartItems(cartItems);
            if (customerService.updateCustomer(customer)) {
                session.setAttribute(Constants.LABEL_CUSTOMER, customer);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                        Constants.MSG_ADD_ORDER_SUCCESS);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
    }

    /**
     * <p>
     * This method is used to search products from e-commerce web-site based on
     * category id and product name.
     * </p>
     */
    @PostMapping("searchProduct")
    public ModelAndView searchProduct(HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        try {
            Integer categoryId = Integer
                    .parseInt(request.getParameter("categoryId"));
            String productName = request.getParameter("name");
            List<Product> products = new ArrayList<Product>();
            if (0 == categoryId) {
                products = customerService
                        .searchProduct("%" + productName + "%");
            } else {
                products = customerService.searchProduct(categoryId,
                         "%" + productName + "%");
            }
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
            modelAndView.addObject("products", products);
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_UPDATE_ADDRESS_FAIL);
        }
        modelAndView.setViewName("CustomerHome");
        return modelAndView;
    }

    /**
     * <p>
     * This method is used to display particular product details such as
     * available sellers for this product, rating of this product, price, image,
     * description about that product etc.
     * </p>
     * 
     * @param id Needed for which product details will display.
     */
    @PostMapping("productPage")
    public ModelAndView getProduct(@RequestParam("id") Integer id,
            HttpServletRequest request) {
        HttpSession session = request.getSession(Boolean.FALSE);
        ModelAndView modelAndView = new ModelAndView();
        try {
            Product product = customerService.searchProduct(id);
            List<WarehouseProduct> warehouseProducts = product
                    .getWarehouseProducts();
            modelAndView.addObject("product", product);
            modelAndView.addObject(Constants.LABEL_CATEGORIES,
                    customerService.getAllCategories());
            modelAndView.setViewName("ProductPage");
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_PRODUCT_PAGE_OPEN_FAIL);
            modelAndView.setViewName("homepage");
        }
        return modelAndView;
    }

}
