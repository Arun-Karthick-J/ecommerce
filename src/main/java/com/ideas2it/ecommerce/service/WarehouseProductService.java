package com.ideas2it.ecommerce.service;

import java.util.List;

import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.model.Order;
import com.ideas2it.ecommerce.model.OrderItem;
import com.ideas2it.ecommerce.model.WarehouseProduct;

/**
 * <p>
 * The {@code WarehouseProductService} interface provides the operations that
 * can be done by a seller on warehouse products. It provides the basic
 * functions related to the warehouse products.
 * </p>
 *
 * @author Arun Karthick.J
 */
public interface WarehouseProductService {

    /**
     * <p>
     * Adds a new product that is available for sale from a particular seller.
     * </p>
     * 
     * @param warehouseProduct WarehouseProduct which needs to be stored
     * @return message Returns true if the new warehouseproduct is added
     *         successfully else returns false
     */
    Boolean addWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException;

    /**
     * <p>
     * Removes a product that is no longer available from a seller.
     * </p>
     * 
     * @param warehouseProduct WarehouseProduct which needs to be removed
     * @return message Returns true if the new warehouseproduct is removed
     *         successfully else returns false
     */
    Boolean deleteWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException;

    /**
     * <p>
     * Updates a product that is available from a particular seller.
     * </p>
     * 
     * @param warehouseProduct WarehouseProduct which needs to be updated
     * @return message Returns true if the new warehouseproduct is updated
     *         successfully else returns false
     */
    Boolean updateWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException;

    /**
     * <p>
     * Searches through the list for the specific WarehouseProduct ID.
     * </p>
     * 
     * @param warehouseProductId ID of the WarehouseProduct which has to be
     *                           searched for in the list.
     * @return warehouseProduct Returns the WarehouseProduct with the desired ID
     *         if it exist Returns null if no such WarehouseProduct exist.
     */
    WarehouseProduct searchById(Integer warehouseId) throws EcommerceException;

    /**
     * <p>
     * Searches the warehouse products based on the Product ID.
     * </p>
     *
     * @param ProductId ID of the product whose corresponding warehouse products
     *                  needs to be searched
     * @return warehouseProducts Returns the list of warehouse products
     *         corresponding to the product Id.
     */
    List<WarehouseProduct> searchByProductId(Integer productId)
            throws EcommerceException;

    /**
     * <p>
     * Searches the warehouse products based on the Product ID.
     * </p>
     *
     * @param ProductId ID of the product whose corresponding warehouse products
     *                  needs to be searched
     * @param sellerId  ID of the Seller whose warehouse products needs to be
     *                  searched.
     * @return warehouseProduct Returns the warehouse product corresponding to
     *         the product Id and seller.
     */
    WarehouseProduct searchByProductId(Integer productId, Integer sellerId)
            throws EcommerceException;

    /**
     * <p>
     * Searches the warehouse products based on the seller.
     * </p>
     *
     * @param sellerId Seller whose warehouse products needs to be searched.
     * @return warehouseProducts Returns the list of warehouse products
     *         corresponding to the seller.
     */
    List<WarehouseProduct> searchBySeller(Integer sellerId)
            throws EcommerceException;

    /**
     * <p>
     * Fetches the list of warehouse product Ids based on the seller id.
     * </p>
     *
     * @param sellerId Seller whose warehouse products needs to be searched.
     * @return warehouseProducts Returns the list of warehouse products
     *         corresponding to the seller.
     */
    List<Integer> getIdsBySeller(Integer sellerId) throws EcommerceException;

    /**
     * <p>
     * Fetches the list of warehouse product Ids based on the product id.
     * </p>
     *
     * @param productId Product whose warehouse products needs to be searched.
     * @return warehouseProducts Returns the list of warehouse products
     *         corresponding to the product.
     */
    List<Integer> getIdsByProduct(Integer productId) throws EcommerceException;

    /**
     * <p>
     * It fetches the list of warehouse products based on the list of ids
     * </p>
     *
     * @param warehouseProductIds List of warehouseProducts to be fetched.
     * @return warehouseProducts Returns the list of warehouse products
     *         corresponding to the ids provided.
     */
    List<WarehouseProduct> getWarehouseProductsByIds(
            List<Integer> warehouseProductIds) throws EcommerceException;

    /**
     * <p>
     * Reduces the stock from the warehouse based on the order item quantity.
     * </p>
     * 
     * @param orders Order whose corresponding product stocks need to be reduced
     *               from the warehouse
     * @return orderItems Returns the list of orderItems which can't be placed
     *         due to stock unavailability.
     */
    List<OrderItem> reduceQuantity(Order order) throws EcommerceException;

    /**
     * <p>
     * Increases the stock from the warehouse based on the order quantity.
     * </p>
     * 
     * @param order Order whose corresponding product stocks need to be
     *              increased in the warehouse
     * @return message Returns true if stock quanitity is increased in the
     *         warehouse
     */
    Boolean increaseQuantity(Order order) throws EcommerceException;
}
