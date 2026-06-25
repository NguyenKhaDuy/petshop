package org.example.petshop;

import org.example.petshop.DTO.AddWishListRequest;
import org.example.petshop.DTO.DataResponse;
import org.example.petshop.DTO.MessageDTO;
import org.example.petshop.DTO.OrderItemRequest;
import org.example.petshop.DTO.OrderDTO;
import org.example.petshop.DTO.OrderRequest;
import org.example.petshop.DTO.ProductRequest;
import org.example.petshop.DTO.RegisterDTO;
import org.example.petshop.DTO.VoucherDTO;
import org.example.petshop.Entity.PaymentMethodEntity;
import org.example.petshop.Entity.CategoryEntity;
import org.example.petshop.Entity.ProductsEntity;
import org.example.petshop.Entity.SizeProductEntity;
import org.example.petshop.Entity.UserEntity;
import org.example.petshop.Entity.VoucherEntity;
import org.example.petshop.Repository.CartRepository;
import org.example.petshop.Repository.CategoryRepository;
import org.example.petshop.Repository.PaymentMethodRepository;
import org.example.petshop.Repository.ProductRepository;
import org.example.petshop.Repository.UserRepository;
import org.example.petshop.Repository.VoucherRepository;
import org.example.petshop.Repository.WishListRepository;
import org.example.petshop.Service.OrderService;
import org.example.petshop.Service.ProductService;
import org.example.petshop.Service.UserService;
import org.example.petshop.Service.VoucherService;
import org.example.petshop.Service.WishListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@Transactional
class CommerceFlowIntegrationTests {
    @Autowired
    UserService userService;
    @Autowired
    WishListService wishListService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    WishListRepository wishListRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PaymentMethodRepository paymentMethodRepository;
    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    VoucherService voucherService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void registrationCreatesCartAndWishlistIsIdempotent() {
        UserEntity user = registerTestUser();

        assertEquals("USER", user.getRole());
        assertNotNull(cartRepository.findByUserEntity(user));

        ProductsEntity product = productRepository.findAll().stream().findFirst().orElse(null);
        assumeTrue(product != null, "Database needs at least one product");

        AddWishListRequest request = new AddWishListRequest(user.getIdUser(), product.getIdProduct());
        assertEquals(HttpStatus.OK, wishListService.addWishList(request).getStatus());
        assertEquals(HttpStatus.OK, wishListService.addWishList(request).getStatus());
        assertEquals(1, wishListRepository.findByUserEntityAndProductsEntity(user, product).size());
    }

    @Test
    void orderCanBeCreatedWithoutVoucher() {
        UserEntity user = registerTestUser();
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findAll().stream().findFirst().orElse(null);
        ProductsEntity product = productRepository.findAll().stream()
                .filter(item -> !item.getSizeProductEntities().isEmpty())
                .findFirst()
                .orElse(null);

        assumeTrue(paymentMethod != null, "Database needs at least one payment method");
        assumeTrue(product != null, "Database needs at least one product size");

        SizeProductEntity productSize = product.getSizeProductEntities().get(0);
        OrderItemRequest item = new OrderItemRequest(
                product.getIdProduct(),
                productSize.getSizeEntity().getIdSize(),
                1
        );
        OrderRequest request = new OrderRequest(
                user.getIdUser(),
                "Integration test",
                paymentMethod.getIdmethod(),
                user.getInformationOrderEntities().get(0).getIdInformationOrder(),
                List.of(item),
                null
        );

        MessageDTO result = orderService.order(request);
        assertEquals(HttpStatus.OK, result.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    void ordersCanBeReadWhenAProductHasNoImage() {
        UserEntity user = registerTestUser();
        PaymentMethodEntity paymentMethod = paymentMethodRepository.findAll().stream().findFirst().orElse(null);
        ProductsEntity product = productRepository.findAll().stream()
                .filter(item -> !item.getSizeProductEntities().isEmpty())
                .filter(item -> item.getProductImageEntities() == null
                        || item.getProductImageEntities().isEmpty())
                .findFirst()
                .orElse(null);

        assumeTrue(paymentMethod != null, "Database needs at least one payment method");
        assumeTrue(product != null, "Database needs at least one product size without an image");

        SizeProductEntity productSize = product.getSizeProductEntities().get(0);
        OrderRequest request = new OrderRequest(
                user.getIdUser(),
                "Order without product image",
                paymentMethod.getIdmethod(),
                user.getInformationOrderEntities().get(0).getIdInformationOrder(),
                List.of(new OrderItemRequest(
                        product.getIdProduct(),
                        productSize.getSizeEntity().getIdSize(),
                        1
                )),
                null
        );

        assertEquals(HttpStatus.OK, orderService.order(request).getStatus());

        Object result = orderService.getOrderByUser(user.getIdUser());
        assertTrue(result instanceof DataResponse<?>);
        List<OrderDTO> orders = (List<OrderDTO>) ((DataResponse<?>) result).getData();
        assertEquals(1, orders.size());
        assertEquals(1, orders.get(0).getOrderDetailDTOS().size());
        assertEquals(null, orders.get(0).getOrderDetailDTOS().get(0).getImageProduct());
    }

    @Test
    @SuppressWarnings("unchecked")
    void publicVoucherListOnlyContainsAvailableVouchers() {
        VoucherEntity available = createVoucher("AVAILABLE", LocalDate.now(), 1L);
        VoucherEntity expired = createVoucher("EXPIRED", LocalDate.now().minusDays(1), 1L);
        VoucherEntity outOfStock = createVoucher("EMPTY", LocalDate.now().plusDays(1), 0L);

        DataResponse<List<VoucherDTO>> response = voucherService.getAvailableVouchers();
        List<Long> voucherIds = response.getData().stream().map(VoucherDTO::getIdVoucher).toList();

        assertTrue(voucherIds.contains(available.getIdVoucher()));
        assertTrue(!voucherIds.contains(expired.getIdVoucher()));
        assertTrue(!voucherIds.contains(outOfStock.getIdVoucher()));
    }

    @Test
    void productUpdateCanRetainRemoveAndAppendImages() {
        CategoryEntity category = categoryRepository.findAll().stream().findFirst().orElse(null);
        assumeTrue(category != null, "Database needs at least one category");

        String productName = "Image test " + UUID.randomUUID();
        ProductRequest createRequest = new ProductRequest();
        createRequest.setNameProduct(productName);
        createRequest.setDescription("Image selection integration test");
        createRequest.setIdCategory(category.getIdCategory());
        createRequest.setImages(List.of(
                imageFile("first.png", 1),
                imageFile("second.png", 2)
        ));

        assertEquals(HttpStatus.OK, productService.addProduct(createRequest).getStatus());
        ProductsEntity product = productRepository.findAll().stream()
                .filter(item -> productName.equals(item.getNameProduct()))
                .findFirst()
                .orElseThrow();
        assertEquals(2, product.getProductImageEntities().size());

        Long retainedImageId = product.getProductImageEntities().get(0).getIdImage();
        Long removedImageId = product.getProductImageEntities().get(1).getIdImage();
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setIdProduct(product.getIdProduct());
        updateRequest.setNameProduct(product.getNameProduct());
        updateRequest.setDescription(product.getDescription());
        updateRequest.setIdCategory(category.getIdCategory());
        updateRequest.setRetainedImageIds(List.of(retainedImageId));
        updateRequest.setImageSelectionProvided(true);
        updateRequest.setImages(List.of(imageFile("third.png", 3)));

        assertEquals(HttpStatus.OK, productService.updateProduct(updateRequest).getStatus());
        productRepository.flush();

        ProductsEntity updated = productRepository.findById(product.getIdProduct()).orElseThrow();
        List<Long> updatedImageIds = updated.getProductImageEntities().stream()
                .map(image -> image.getIdImage())
                .toList();
        assertEquals(2, updatedImageIds.size());
        assertTrue(updatedImageIds.contains(retainedImageId));
        assertTrue(!updatedImageIds.contains(removedImageId));

        updateRequest.setRetainedImageIds(List.of());
        updateRequest.setImages(null);
        assertEquals(HttpStatus.OK, productService.updateProduct(updateRequest).getStatus());
        productRepository.flush();
        assertTrue(productRepository.findById(product.getIdProduct()).orElseThrow()
                .getProductImageEntities().isEmpty());
    }

    private MockMultipartFile imageFile(String filename, int marker) {
        return new MockMultipartFile(
                "images",
                filename,
                "image/png",
                new byte[]{(byte) marker, 2, 3, 4}
        );
    }

    private VoucherEntity createVoucher(String prefix, LocalDate expiredDate, Long quantity) {
        VoucherEntity voucher = new VoucherEntity();
        voucher.setCode(prefix + "-" + UUID.randomUUID());
        voucher.setDiscount(10L);
        voucher.setExpiredDate(expiredDate);
        voucher.setQuantity(quantity);
        voucher.setCreatedAt(LocalDateTime.now());
        return voucherRepository.save(voucher);
    }

    private UserEntity registerTestUser() {
        String token = UUID.randomUUID().toString();
        RegisterDTO request = new RegisterDTO(
                "Test User",
                "test-" + token + "@example.com",
                "test-password",
                "Test address",
                "0900000000"
        );

        assertEquals(HttpStatus.OK, userService.register(request).getStatus());
        UserEntity user = userRepository.findByEmail(request.getEmail());
        assertNotNull(user);
        assertTrue(user.getIdUser() > 0);
        return user;
    }
}
