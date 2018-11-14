<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="AdminHeader.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Products</title>

<link rel="stylesheet"
	href="<c:url value='/resources/css/AdminDisplayProducts.css' />">

</head>
<body>
	<form>
		<div class="input-group">
			<input type="text" class="form-control" id="search" name="name"
				placeholder="Enter Product Name to be searched">
		 	<button class="btn btn-default" type="submit"
				formaction="/ecommerce/product/searchByName" formmethod="post">
				<i class="fa fa-search"></i>
			</button>
		</div>
	</form>
	<div class="card-deck">
		<c:forEach var="product" items="${products}">
			<form>
  				<div class="card">
  					<button type="button" class="btn btn-info"
							data-toggle="modal" data-target="#${product.id}">
	    				<img class="card-img-top" src="data:image/jpg;base64,
	    					${product.base64Image}">
    		    	</button>
    		    	<div class="card-body">
    		    		<button type="button" class="btn btn-info"
							data-toggle="modal" data-target="#${product.id}">
							<h5 class="card-title">${product.name}</h5>
						</button>
      					<p class="card-text">&#8377;${product.warehouseProducts[0].price}</p>
      					<p class="card-text">
	      					<input type="hidden" name="id" value="${product.category.id}"/>
    	  					<button class="btn btn-default" formmethod="post"
								formaction="/ecommerce/category/displayProducts">
								${product.category.name}
							</button>
      					</p>
    				</div>
	 	    	</div>
	 	    	<div id="${product.id}" class="modal fade" role="dialog">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<c:if test="${not empty product.warehouseProducts}">
									<h3 class="modal-title">Sellers</h3>
								</c:if>
							</div>
							<div class="modal-body">
								<c:if test="${not empty product.warehouseProducts}">
								<table class="table table-striped">
									<thead>
										<tr>
											<th> ID </th>
											<th> Name </th>
											<th> Mobile Number </th>
											<th> Email ID </th>
											<th> Rating </th>
											<th class="row-header"> Product Price </th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${product.warehouseProducts}" 
											var="warehouseProduct">
											<tr>
												<td>${warehouseProduct.seller.id}</td>
												<td>${warehouseProduct.seller.name}</td>
												<td>${warehouseProduct.seller.mobileNumber}</td>
												<td>${warehouseProduct.seller.emailId}</td>
												<c:if test="${warehouseProduct.seller.rating ne null}">
													<td>${warehouseProduct.seller.rating}</td>
												</c:if>
												<c:if test="${warehouseProduct.seller.rating eq null}">
													<td> Not yet Rated </td>
												</c:if>
												<td>${warehouseProduct.price}</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
								</c:if>
								<c:if test="${empty product.warehouseProducts}">
									<h3>  Sorry, No Seller is selling this Product......</h3>
								</c:if>
							</div>
						</div>
					</div>
				</div>
 	    	</form>
 	    </c:forEach>
	</div>
</body>
<c:if test="${null != message}">
	<script type="text/javascript">
		alert("${message}");
		
	</script>
</c:if>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</html>