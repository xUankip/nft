<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NFT Gallery - Create New NFT</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<!-- Header -->
<%--<jsp:include page="../components/header.jsp" />--%>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h2 class="card-title mb-0">Create New NFT</h2>
                </div>
                <div class="card-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/nft/create" method="post">
                        <div class="mb-3">
                            <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" value="${param.name}" required minlength="6">
                            <div class="form-text">Name must be at least 6 characters long.</div>
                        </div>

                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4">${param.description}</textarea>
                        </div>

                        <div class="mb-3">
                            <label for="imageUrl" class="form-label">Image URL <span class="text-danger">*</span></label>
                            <input type="url" class="form-control" id="imageUrl" name="imageUrl" value="${param.imageUrl}" required>
                            <div class="form-text">Enter a valid URL for the NFT image.</div>
                        </div>

                        <div class="mb-3">
                            <label for="price" class="form-label">Price (ETH) <span class="text-danger">*</span></label>
                            <input type="number" class="form-control" id="price" name="price" value="${param.price}" step="0.001" min="0.001" required>
                        </div>

                        <div class="mb-3">
                            <label for="creator" class="form-label">Creator <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="creator" name="creator" value="${param.creator}" required>
                        </div>

                        <div class="mb-3">
                            <label for="categoryId" class="form-label">Category <span class="text-danger">*</span></label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">-- Select Category --</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="walletAddress" class="form-label">Wallet Address <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="walletAddress" name="walletAddress" value="${param.walletAddress}" required pattern="0x[a-fA-F0-9]{40}">
                            <div class="form-text">Enter a valid Ethereum wallet address (e.g., 0x71C7656EC7ab88b098defB751B7401B5f6d8976F).</div>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/nft" class="btn btn-secondary me-md-2">Cancel</a>
                            <button type="submit" class="btn btn-primary">Create NFT</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<%--<jsp:include page="../components/footer.jsp" />--%>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
<script>
    // Client-side validation
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');
        form.addEventListener('submit', function(event) {
            let valid = true;

            // Validate name
            const name = document.getElementById('name').value;
            if (name.length < 6) {
                valid = false;
                alert('Name must be at least 6 characters long');
            }

            // Validate price
            const price = parseFloat(document.getElementById('price').value);
            if (isNaN(price) || price <= 0) {
                valid = false;
                alert('Price must be greater than 0');
            }

            // Validate wallet address
            const walletAddress = document.getElementById('walletAddress').value;
            const ethAddressRegex = /^0x[a-fA-F0-9]{40}$/;
            if (!ethAddressRegex.test(walletAddress)) {
                valid = false;
                alert('Please enter a valid Ethereum wallet address');
            }

            if (!valid) {
                event.preventDefault();
            }
        });
    });
</script>
</body>
</html>