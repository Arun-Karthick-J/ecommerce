<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="AdminHeader.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title>Customers</title>
<link rel="stylesheet"
	href="<c:url value='/resources/css/AdminDisplayCustomers.css' />">
</head>
<body>
	<div class="title">
		<h2> Sellers </h2>
	</div>
	<form>
		<div class="input-group">
			<input type="text" class="form-control" id="search" name="name"
				placeholder="Enter Seller Name to be searched">
		 	<button class="btn btn-default" type="submit"
				formaction="/ecommerce/admin/searchBySellerName" formmethod="post">
				<i class="fa fa-search"></i>
			</button>
		</div>
	</form>
	<table class="table table-striped">
		<thead>
			<tr>
				<th class="row-header"> ID </th>
				<th class="row-header"> Name </th>
				<th class="row-header"> Mobile Number </th>
				<th class="row-header"> Email ID </th>
				<th class="row-header"> Rating </th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${sellers}" var="seller">
				<form action="admin" method="Post">
					<input type="hidden" name="id" value="${seller.id}" />
					<tr>
						<td>${seller.id}</td>
						<td><button class="btn btn-default" formmethod="post"
								formaction="/ecommerce/admin/displaySellers">
								${seller.name}</button></td>
						<td>${seller.mobileNumber}</td>
						<td>${seller.emailId}</td>
						<td>${seller.rating}</td>
						<td>
							<button type="submit" class="btn btn-danger"
								formaction="/ecommerce/admin/deleteSeller" formmethod="post">
								<i class="fa fa-trash"></i></button>
						</td>
					</tr>
				</form>
			</c:forEach>
		</tbody>
	</table>
</body>
<c:if test="${null != message}">
	<script type="text/javascript">
		alert("${message}");
	</script>
</c:if>
</html>