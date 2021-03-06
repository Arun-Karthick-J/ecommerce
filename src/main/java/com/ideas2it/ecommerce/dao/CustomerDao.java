package com.ideas2it.ecommerce.dao;


import java.util.List;

import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.model.Customer;

/**
 * <p>
 * CustomerDao interface class is used to insert the new customer, 
 * soft delete on existing customer details from database,
 * update existing customer detail in the e-commerce website.
 * </p>
 * 
 * @author Anantharaj.S
 * 
 */
public interface CustomerDao {


    /** 
     * <p>
     * Adding new customer details to e-commerce website.
     * 
     * @param customer
     *        Needed for inserting customer details
     *
     * @return Boolean
     *        If customer details added to database returns true, otherwise false
     * </p> 
     */
    public Boolean insertCustomer(Customer customer) throws EcommerceException ;

    /**
     * <p>
     * Getting particular customer detail from database based on mobile number.
     *
     * @param mobile
     *        Needed for check the customer details
     *
     * @param status
     *        needed for check active or inactive
     *
     * @return Customer
     *        Returns particular customer details from database
     * </p>
     */
    public Customer getCustomerByMobile(String mobile, Boolean isActive)
        throws EcommerceException;

    /**
     * <p>
     * Getting particular customer detail from database based on customer id.
     *
     * @param id
     *        Needed for check customer detail.
     *
     * @param status
     *        needed for check active or inactive.
     *
     * @return Customer
     *        Returns particular customer details form database.
     * </p>
     */
    public Customer getCustomerById(Integer id, Boolean isActive) 
        throws EcommerceException ;

    /**
     * <p>
     * Getting particular customer detail from database based on userId.
     *
     * @param userId
     *        Needed for check customer detail.
     *
     * @param isActive
     *        needed for check customer active or inactive.
     *
     * @return Customer
     *        Returns particular customer details form database.
     * </p>
     */
    public Customer getCustomerByUserId(Integer userId, Boolean isActive)
        throws EcommerceException ;

    /**
     * <p>
     * Getting particular customer detail from database based on userId.
     *
     * @param name
     *        Needed for check customer detail.
     *
     * @param isActive
     *        needed for check customer active or inactive.
     *
     * @return Customer
     *        Returns particular customer details form database.
     * </p>
     */
    public List<Customer> getCustomerByName(String name, Boolean isActive)
        throws EcommerceException ;
    
    /** 
     * <p>
     * Delete existing customer detail in the store, if available
     * 
     * @param customer
     *        Needed for deleting customer.
     *
     * @return Boolean
     *        If customer deleted in database returns true, otherwise false
     * </p> 
     */
    public Boolean deleteCustomer(Customer customer) throws EcommerceException;

    /** 
     * <p>
     * Update existing customer detail to database, using updated values
     * 
     * @param customer
     *        Needed for updating customer
     *
     * @return Boolean
     *        If customer updated to store returns true, otherwise false
     * </p> 
     */
    public Boolean updateCustomer(Customer customer) throws EcommerceException;

    /**
     * <p>
     * Getting all active customer details in e-commerce
     *
     * @param status
     *        needed for check active or inactive customer
     *
     * @return List<Customer>
     *        Returns list of customer details form database
     * </p>
     */
    public List<Customer> getCustomers(Boolean isActive) throws EcommerceException ; 

}
