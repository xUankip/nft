<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NFT Gallery - Available NFTs</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<!-- Header -->
<%--<jsp:include page="../components/header.jsp" />--%>

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Available NFTs</h1>
        <a href="${pageContext.request.contextPath}/nft/create" class="btn btn-primary">Create New NFT</a>
    </div>

    <!-- Search Form -->
    <div class="row mb-4">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/nft/search" method="get" class="d-flex">
                <input type="text" name="q" class="form-control me-2" placeholder="Search by name or creator" value="${param.q}">
                <button type="submit" class="btn btn-outline-primary">Search</button>
            </form>
        </div>
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/nft/filter" method="get" class="d-flex">
                <select name="categoryId" class="form-select me-2">
                    <option value="">All Categories</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.categoryName}</option>
                    </c:forEach>
                </select>
                <button type="submit" class="btn btn-outline-primary">Filter</button>
            </form>
        </div>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- NFT Cards -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        <c:forEach var="nft" items="${nfts}">
            <div class="col">
                <div class="card h-100">
                    <img src="${nft.imageUrl}" class="card-img-top" alt="${nft.name}" onerror="this.src='${pageContext.request.contextPath}/assets/images/placeholder.png'">
                    <div class="card-body">
                        <h5 class="card-title">${nft.name}</h5>
                        <p class="card-text">
                            <strong>Creator:</strong> ${nft.creator}<br>
                            <strong>Price:</strong> <fmt:formatNumber value="${nft.price}" type="currency" currencySymbol="ETH " />
                        </p>
                        <div class="badge bg-secondary mb-2">${nft.categoryName}</div>
                    </div>
                    <div class="card-footer d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/nft/view/${nft.id}" class="btn btn-sm btn-outline-primary">View Details</a>
                        <div>
                            <a href="${pageContext.request.contextPath}/nft/edit/${nft.id}" class="btn btn-sm btn-outline-secondary">Edit</a>
                            <a href="#" onclick="confirmDelete('${nft.id}', '${nft.name}')" class="btn btn-sm btn-outline-danger">Delete</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty nfts}">
            <div class="col-12 text-center py-5">
                <p class="text-muted">No NFTs available for sale.</p>
            </div>
        </c:if>
    </div>

    <!-- Pagination -->
    <c:if test="${totalPages > 1}">
        <nav class="mt-4">
            <ul class="pagination justify-content-center">
                <c:if test="${currentPage > 1}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${currentPage - 1}&limit=${param.limit}">Previous</a>
                    </li>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                        <a class="page-link" href="?page=${i}&limit=${param.limit}">${i}</a>
                    </li>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${currentPage + 1}&limit=${param.limit}">Next</a>
                    </li>
                </c:if>
            </ul>
        </nav>
    </c:if>
</div>

<!-- Footer -->
<%--<jsp:include page="../components/footer.jsp" />--%>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
<script>
    function confirmDelete(id, name) {
        if (confirm(`Are you sure you want to delete "${name}"?`)) {
            window.location.href = "${pageContext.request.contextPath}/nft/delete/" + id;
        }
    }
</script>
</body>
</html>