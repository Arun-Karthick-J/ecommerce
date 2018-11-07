package com.ideas2it.ecommerce.service.impl;

import java.util.List;

import com.ideas2it.ecommerce.dao.WarehouseProductDao;
import com.ideas2it.ecommerce.dao.impl.WarehouseProductDaoImpl;
import com.ideas2it.ecommerce.exception.EcommerceException;
import com.ideas2it.ecommerce.model.Seller;
import com.ideas2it.ecommerce.model.WarehouseProduct;
import com.ideas2it.ecommerce.service.WarehouseProductService;



public class WarehouseProductServiceImpl implements WarehouseProductService{
    private WarehouseProductDao warehouseProductDao = new WarehouseProductDaoImpl();
    
    /**
     * @{inheritDoc}
     */
    @Override
    public Boolean addWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException {
        return warehouseProductDao.addWarehouseProduct(warehouseProduct);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public Boolean deleteWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException {
        return warehouseProductDao.removeWarehouseProduct(warehouseProduct);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public Boolean updateWarehouseProduct(WarehouseProduct warehouseProduct)
            throws EcommerceException {
        return warehouseProductDao.updateWarehouseProduct(warehouseProduct);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public WarehouseProduct searchById(Integer warehouseProductId)
            throws EcommerceException {
        return warehouseProductDao.getWarehouseProductById(warehouseProductId);
    }
    
    /**
     * @{inheritDoc}
     */
    @Override
    public List<WarehouseProduct> searchByProductId(Integer productId)
            throws EcommerceException {
        return warehouseProductDao.getWarehouseProductsByProductId(productId);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public List<WarehouseProduct> searchBySeller(Seller seller)
            throws EcommerceException {
        return warehouseProductDao.getWarehouseProductsBySeller(seller);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public List<WarehouseProduct> getWarehouseProductsByIds(List<Integer> warehouseProductIds)
            throws EcommerceException {
        return warehouseProductDao.getWarehouseProductsByIds(warehouseProductIds);
    }

}
