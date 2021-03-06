package com.ideas2it.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ideas2it.ecommerce.common.Constants;
import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.model.Product;
import com.ideas2it.ecommerce.service.ProductService;
import com.ideas2it.ecommerce.service.impl.ProductServiceImpl;

/**
 * <p>
 * Used to perform basic functionalities such as displaying all Products
 * and fetching the details of the Products based on ID or name.
 * </p>
 * 
 * @author Pavithra.S
 *
 */
@Controller
@RequestMapping("product")
public class ProductController {
    private static final String ADMIN_CATEGORY_PAGE = "AdminCategoryPage";
    private static final String ADMIN_PRODUCT_PAGE = "AdminProductPage";
    
    private ProductService productService = new ProductServiceImpl();
    
    /**
     * <p>
     * Used to display the details of all the Products available
     * </p>
     * 
     * @return  Returns the list of Products. Otherwise, returns a failure 
     *          message indicating that no Products are available. 
     */
    @GetMapping("display")
    private ModelAndView displayProducts() {
        List<Product> products = new ArrayList<Product>();
        ModelAndView modelAndView = new ModelAndView();
        try {
            products = getProducts(modelAndView);
            if (!products.isEmpty()) {
                return new ModelAndView(ADMIN_PRODUCT_PAGE,
                    Constants.LABEL_PRODUCTS,products);  
            } else {
                return new ModelAndView(ADMIN_PRODUCT_PAGE,
                    Constants.LABEL_MESSAGE,Constants.MSG_PRODUCTS_UNAVAILABLE);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
            modelAndView.setViewName(ADMIN_CATEGORY_PAGE);
        }
        return modelAndView;
    }

    /**
     * <p>
     * Used to retrieve the details of the Product for the ID specified.
     * </p>
     * 
     * @param   id  ID of the product to be fetched.
     * @return      Returns the Product for the ID specified. Otherwise,
     *              returns a failure message indicating that the Product 
     *              of the specified ID isn't available.
     */
    @GetMapping("searchById") 
    private ModelAndView searchById(@RequestParam
            (Constants.LABEL_ID)Integer id) {
        ModelAndView modelAndView = new ModelAndView(ADMIN_PRODUCT_PAGE);
        List<Product> products = new ArrayList<Product>();
        try {
            Product product = productService.searchById(id);
            if (null != product) {
                products.add(product);
                modelAndView.addObject(Constants.LABEL_PRODUCTS, products);
            } else {
                products = getProducts(modelAndView);
                modelAndView.addObject(Constants.LABEL_PRODUCTS, products);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_PRODUCT_NOT_AVAILABLE);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }
    
    /**
     * <p>
     * Used to retrieve the details of the Product based on the name specified. 
     * </p>
     * 
     * @param   name  Name of the Product to be fetched.
     * @return        Returns the list of Products for the name specified. 
     *                Otherwise, returns a failure message indicating that no 
     *                Product is available for the name specified. 
     */
    @GetMapping("searchByName") 
    private ModelAndView searchByName(@RequestParam
            (Constants.LABEL_NAME)String name) {
        ModelAndView modelAndView = new ModelAndView(ADMIN_PRODUCT_PAGE);
        List<Product> products = new ArrayList<Product>();
        try {
            products = productService.searchByName("%"+name+"%");
            if (!products.isEmpty()) {
                modelAndView.addObject(Constants.LABEL_PRODUCTS, products);
            } else {
                products = getProducts(modelAndView);
                modelAndView.addObject(Constants.LABEL_PRODUCTS, products);
                modelAndView.addObject(Constants.LABEL_MESSAGE,
                    Constants.MSG_PRODUCT_NOT_AVAILABLE);
            }
        } catch (EcommerceException e) {
            modelAndView.addObject(Constants.LABEL_MESSAGE, e.getMessage());
        }
        return modelAndView;
    }
    
    /**
     * <p>
     * Used to fetch the details of all the Products available.
     * </p>
     * 
     * @return  Returns the list of Products available. Otherwise, returns 
     *          an empty object.
     */
    private List<Product> getProducts(ModelAndView modelAndView) 
            throws EcommerceException {
        List<Product> products = new ArrayList<Product>();
        products = productService.getProducts();
        return products;
    }
}